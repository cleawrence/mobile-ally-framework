// FRAM — NavigationSemanticsDetector
// SKILL-10 [A] — Critère 10.1 (Position dans l'application)
// Détecte : Scaffold sans TopAppBar title, écrans sans titre accessible

package com.fram.lint.checks

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*

@Suppress("UnstableApiUsage")
class NavigationSemanticsDetector : Detector(), Detector.UastScanner {

    companion object {

        private val SKILL_URL = "https://cleawrence.github.io/mobile-ally-framework/skills/skill-10/"

        /**
         * SKILL-10 [A] 10.1 — Scaffold/écran sans titre accessible
         *
         * Chaque écran doit avoir un titre pertinent et unique pour que TalkBack
         * puisse informer l'utilisateur de sa position dans l'application.
         * Le titre est typiquement fourni via le paramètre `title` du `TopAppBar`
         * dans le `Scaffold`.
         */
        val ISSUE_MISSING_SCREEN_TITLE = Issue.create(
            id = "MissingScreenTitle",
            briefDescription = "[FRAM SKILL-10] Scaffold sans titre d'écran accessible",
            explanation = """
                Un `Scaffold` sans `topBar` (ou avec un `topBar` sans `title`) ne fournit
                pas de titre d'écran à TalkBack.

                L'utilisateur aveugle ne sait pas où il se trouve dans l'application.
                C'est une violation du critère 10.1 [A] — WCAG 2.4.2.

                **Bon :**
                ```kotlin
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text("Mon profil") }) // ✅ Titre annoncé
                    }
                ) { /* contenu */ }
                ```

                **Mauvais :**
                ```kotlin
                Scaffold { /* contenu */ } // ❌ Pas de topBar → pas de titre TalkBack
                ```

                **Alternative sans TopAppBar :**
                ```kotlin
                Scaffold { padding ->
                    Column(Modifier.padding(padding)) {
                        Text(
                            "Mon profil",
                            modifier = Modifier.semantics { heading() },
                            style = MaterialTheme.typography.headlineMedium
                        )
                        // ✅ Le heading() permet la navigation TalkBack par en-têtes
                    }
                }
                ```

                Voir SKILL-10 [A] critère 10.1 — $SKILL_URL
            """,
            category = Category.A11Y,
            priority = 7,
            severity = Severity.WARNING,
            implementation = Implementation(
                NavigationSemanticsDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        ).setAndroidSpecific(true)
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UCallExpression::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {

            override fun visitCallExpression(node: UCallExpression) {
                val methodName = node.methodName ?: return

                if (methodName != "Scaffold") return

                // node.asSourceString() ne préserve pas le nom d'un argument nommé pour un appel
                // Kotlin-vers-Kotlin (ex: le "topBar =" du site d'appel n'apparaît pas dans le
                // texte reconstruit) : chercher "topBar" dans la source du Scaffold ne trouve
                // donc jamais rien. Résoudre l'appelé pour retrouver l'argument par paramètre.
                val topBarArg = findArgument(node, "topBar")
                val contentArg = findArgument(node, "content")
                // stripComments() : le texte reconstruit inclut les commentaires de fin de
                // ligne — un commentaire pédagogique mentionnant "heading()" ou "topBar"
                // produirait sinon un faux négatif. Voir SourceTextUtils.kt.
                val topBarSource = stripComments(topBarArg?.asSourceString() ?: "")
                val contentSource = stripComments(contentArg?.asSourceString() ?: node.asSourceString())

                val hasTopBar = topBarArg != null
                val hasTopAppBar = topBarSource.contains("TopAppBar") ||
                        topBarSource.contains("CenterAlignedTopAppBar") ||
                        topBarSource.contains("MediumTopAppBar") ||
                        topBarSource.contains("LargeTopAppBar")

                if (!hasTopBar) {
                    // Vérifier si le contenu du Scaffold contient un heading() semantics
                    val hasHeading = contentSource.contains("heading()") ||
                            contentSource.contains(".isHeader")

                    if (!hasHeading) {
                        context.report(
                            ISSUE_MISSING_SCREEN_TITLE,
                            node,
                            context.getLocation(node),
                            "`Scaffold` sans `topBar` ni `heading()` dans le contenu — " +
                            "TalkBack ne peut pas annoncer le titre de cet écran. " +
                            "Ajouter un `TopAppBar(title = { Text(\"...\") })` " +
                            "ou un `Text(..., modifier = Modifier.semantics { heading() })`. [SKILL-10 A]"
                        )
                    }
                } else if (!hasTopAppBar) {
                    // topBar est présent mais ne contient pas de TopAppBar reconnu
                    // Cela pourrait être un custom topBar sans titre — warning plus léger
                    if (!topBarSource.contains("title") && !topBarSource.contains("Text(")) {
                        context.report(
                            ISSUE_MISSING_SCREEN_TITLE,
                            node,
                            context.getLocation(node),
                            "`Scaffold` avec un `topBar` custom sans titre visible — " +
                            "vérifier qu'un titre accessible est annoncé par TalkBack. [SKILL-10 A]"
                        )
                    }
                }
            }

            private fun findArgument(call: UCallExpression, name: String): UExpression? {
                val resolved = call.resolve()
                val paramIndex = resolved?.parameterList?.parameters?.indexOfFirst { it.name == name }
                if (resolved != null && paramIndex != null && paramIndex >= 0) {
                    return call.getArgumentForParameter(paramIndex)
                }
                return call.valueArguments.firstOrNull { (it as? UNamedExpression)?.name == name }
            }
        }
}
