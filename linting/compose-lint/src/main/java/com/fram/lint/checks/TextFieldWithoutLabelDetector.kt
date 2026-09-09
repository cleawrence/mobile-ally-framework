// FRAM — TextFieldWithoutLabelDetector
// SKILL-09 [A] — Critères 9.1, 9.2
// Détecte : TextField/OutlinedTextField sans paramètre label

package com.fram.lint.checks

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*

@Suppress("UnstableApiUsage")
class TextFieldWithoutLabelDetector : Detector(), Detector.UastScanner {

    companion object {

        private val SKILL_URL = "https://fram.internal/skills/skill-09"

        /**
         * SKILL-09 [A] 9.1 — TextField/OutlinedTextField sans label
         *
         * Le paramètre `label` de OutlinedTextField est le seul indicateur persistant
         * du rôle du champ. Sans lui, TalkBack ne peut pas annoncer ce que l'utilisateur
         * doit saisir.
         */
        val ISSUE_TEXTFIELD_WITHOUT_LABEL = Issue.create(
            id = "TextFieldMissingLabel",
            briefDescription = "[FRAM SKILL-09] TextField / OutlinedTextField sans paramètre label",
            explanation = """
                `OutlinedTextField` et `TextField` doivent avoir un paramètre `label` visible et persistant.
                Le `placeholder` disparaît dès que l'utilisateur commence à saisir —
                c'est une violation du critère 9.1 [A].

                **Mauvais :**
                ```kotlin
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("votre@email.fr") } // ❌ Disparaît à la saisie
                )
                ```

                **Bon :**
                ```kotlin
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") }, // ✅ Flotte au-dessus — toujours visible
                    placeholder = { Text("votre@email.fr") } // Optionnel en plus du label
                )
                ```

                Voir SKILL-09 [A] critère 9.1 — $SKILL_URL
            """,
            category = Category.A11Y,
            priority = 9,
            severity = Severity.ERROR,
            implementation = Implementation(
                TextFieldWithoutLabelDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        ).setAndroidSpecific(true)

        /**
         * SKILL-09 [A] 9.2 — label vide ou non pertinent
         */
        val ISSUE_EMPTY_LABEL = Issue.create(
            id = "TextFieldEmptyLabel",
            briefDescription = "[FRAM SKILL-09] TextField avec label vide ou non pertinent",
            explanation = """
                Un `label` vide dans OutlinedTextField est équivalent à l'absence de label.
                TalkBack ne peut pas annoncer le rôle du champ.

                Fournir un label descriptif correspondant à la donnée attendue :
                "Email", "Mot de passe", "Prénom", "Code postal", etc.

                Voir SKILL-09 [A] critère 9.2 — $SKILL_URL
            """,
            category = Category.A11Y,
            priority = 8,
            severity = Severity.ERROR,
            implementation = Implementation(
                TextFieldWithoutLabelDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        ).setAndroidSpecific(true)

        private val TEXTFIELD_COMPOSABLES = setOf(
            "TextField", "OutlinedTextField", "BasicTextField"
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UCallExpression::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {

            override fun visitCallExpression(node: UCallExpression) {
                val methodName = node.methodName ?: return
                if (methodName !in TEXTFIELD_COMPOSABLES) return

                val sourceText = node.asSourceString()

                // Vérifier la présence du paramètre label
                val hasLabel = sourceText.contains("label") &&
                        !sourceText.contains("label = null")

                // Vérifier la présence d'un accessibilityLabel via semantics
                val hasSemanticsLabel = sourceText.contains("semantics") &&
                        (sourceText.contains("contentDescription") || sourceText.contains("stateDescription"))

                if (!hasLabel && !hasSemanticsLabel) {
                    // BasicTextField n'a pas de label natif — vérifier le contexte
                    if (methodName == "BasicTextField") {
                        // BasicTextField nécessite un label externe — warning moins sévère
                        context.report(
                            ISSUE_TEXTFIELD_WITHOUT_LABEL,
                            node,
                            context.getLocation(node),
                            "`BasicTextField` sans label visible ni `semantics { contentDescription }` — " +
                            "ajouter un Text label au-dessus du champ et le lier via semantics. [SKILL-09 A]"
                        )
                    } else {
                        context.report(
                            ISSUE_TEXTFIELD_WITHOUT_LABEL,
                            node,
                            context.getLocation(node),
                            "`$methodName` sans `label = { Text(\"...\") }` — " +
                            "le placeholder seul est insuffisant (disparaît à la saisie). [SKILL-09 A]"
                        )
                    }
                    return
                }

                // Vérifier si le label est vide
                val emptyLabelPattern = Regex("""label\s*=\s*\{\s*Text\s*\(\s*""\s*\)\s*\}""")
                if (emptyLabelPattern.containsMatchIn(sourceText)) {
                    context.report(
                        ISSUE_EMPTY_LABEL,
                        node,
                        context.getLocation(node),
                        "`$methodName` avec `label = { Text(\"\") }` vide — fournir un label descriptif. [SKILL-09 A]"
                    )
                }
            }
        }
}
