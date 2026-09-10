// FRAM — ClickableWithoutSemanticsDetector
// SKILL-05 [A] — Critères 5.1, 5.2
// Détecte : Modifier.clickable sans semantics { role; contentDescription }

package com.fram.lint.checks

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*

@Suppress("UnstableApiUsage")
class ClickableWithoutSemanticsDetector : Detector(), Detector.UastScanner {

    companion object {

        private val SKILL_URL = "https://cleawrence.github.io/mobile-ally-framework/skills/skill-05/"

        /**
         * SKILL-05 [A] 5.1 — Modifier.clickable sans Modifier.semantics
         *
         * `Modifier.clickable` rend un Composable interactif mais ne lui donne pas
         * de rôle sémantique ni de description pour TalkBack.
         * TalkBack annoncera l'élément comme "sans label, bouton" de façon générique.
         */
        val ISSUE_CLICKABLE_WITHOUT_SEMANTICS = Issue.create(
            id = "ClickableWithoutSemantics",
            briefDescription = "[FRAM SKILL-05] Modifier.clickable sans Modifier.semantics",
            explanation = """
                `Modifier.clickable {}` rend un Composable interactif mais TalkBack ne peut pas
                en connaître le rôle ni la description sans `Modifier.semantics`.

                **Bon :**
                ```kotlin
                Box(
                    modifier = Modifier
                        .clickable { /* action */ }
                        .semantics {
                            role = Role.Button
                            contentDescription = "Voir les détails du produit"
                        }
                )
                ```

                **Mauvais :**
                ```kotlin
                Box(modifier = Modifier.clickable { /* action */ }) // ❌ TalkBack: "sans label"
                ```

                Alternativement, utiliser `Button {}` ou `IconButton {}` qui ont la sémantique intégrée.

                Voir SKILL-05 [A] critère 5.1 — $SKILL_URL
            """,
            category = Category.A11Y,
            priority = 9,
            severity = Severity.ERROR,
            implementation = Implementation(
                ClickableWithoutSemanticsDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        ).setAndroidSpecific(true)

        /**
         * SKILL-05 [A] 5.1 — Modifier.clickable avec semantics mais sans role
         *
         * Avoir `semantics { contentDescription }` sans `role` laisse TalkBack
         * sans information sur la nature de l'élément (bouton, lien, case à cocher...).
         */
        val ISSUE_MISSING_ROLE = Issue.create(
            id = "ClickableMissingRole",
            briefDescription = "[FRAM SKILL-05] Modifier.semantics sans role",
            explanation = """
                Un élément cliquable avec `semantics { contentDescription }` mais sans `role`
                ne permet pas à TalkBack d'annoncer la nature de l'interaction.

                Ajouter `role = Role.Button` (ou `.Checkbox`, `.RadioButton`, `.Tab`, `.Image`, `.Switch`).

                **Bon :**
                ```kotlin
                Modifier.semantics {
                    role = Role.Button
                    contentDescription = "Supprimer l'élément"
                }
                ```

                Voir SKILL-05 [A] critère 5.1 — $SKILL_URL
            """,
            category = Category.A11Y,
            priority = 7,
            severity = Severity.WARNING,
            implementation = Implementation(
                ClickableWithoutSemanticsDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        ).setAndroidSpecific(true)

        /**
         * SKILL-05 [A] 5.2 — contentDescription = "" dans semantics
         */
        val ISSUE_EMPTY_SEMANTICS_LABEL = Issue.create(
            id = "EmptySemanticsContentDescription",
            briefDescription = "[FRAM SKILL-05] semantics { contentDescription = \"\" } vide",
            explanation = """
                `contentDescription = ""` dans un bloc semantics est invalide.
                TalkBack lira l'élément sans contexte ou lira le nom de la fonction Composable.

                Fournir une description signifiante ou omettre le contentDescription
                si un élément parent en gère la description.

                Voir SKILL-05 [A] critère 5.2 — $SKILL_URL
            """,
            category = Category.A11Y,
            priority = 8,
            severity = Severity.ERROR,
            implementation = Implementation(
                ClickableWithoutSemanticsDetector::class.java,
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

                when (methodName) {
                    "clickable", "combinedClickable", "selectable", "toggleable" -> {
                        checkClickableHasSemantics(context, node)
                    }
                    "semantics" -> {
                        checkSemanticsContent(context, node)
                    }
                }
            }

            private fun checkClickableHasSemantics(context: JavaContext, clickableNode: UCallExpression) {
                // Remonter dans l'arbre pour trouver les autres Modifier dans la chaîne
                val sourceText = clickableNode.sourcePsi?.text ?: return
                val parentChain = getModifierChainText(clickableNode)

                val hasSemantics = parentChain.contains("semantics") ||
                        parentChain.contains("clearAndSetSemantics")

                if (!hasSemantics) {
                    // Vérifier si le composable parent est déjà un Button/IconButton
                    val parentComposable = findParentComposable(clickableNode)
                    if (parentComposable in listOf("Button", "IconButton", "TextButton",
                            "OutlinedButton", "FilledTonalButton", "ElevatedButton")) {
                        return // Ces composables ont la sémantique intégrée
                    }

                    context.report(
                        ISSUE_CLICKABLE_WITHOUT_SEMANTICS,
                        clickableNode,
                        context.getLocation(clickableNode),
                        "`.${clickableNode.methodName}()` sans `.semantics { role; contentDescription }` — " +
                        "TalkBack ne peut pas identifier cet élément. [SKILL-05 A]"
                    )
                }
            }

            private fun checkSemanticsContent(context: JavaContext, semanticsNode: UCallExpression) {
                val body = semanticsNode.valueArguments.lastOrNull()?.asSourceString() ?: return

                // Vérifier contentDescription = ""
                val emptyDescriptionPattern = Regex("""contentDescription\s*=\s*""")
                if (emptyDescriptionPattern.containsMatchIn(body) && body.contains("= \"\"")) {
                    context.report(
                        ISSUE_EMPTY_SEMANTICS_LABEL,
                        semanticsNode,
                        context.getLocation(semanticsNode),
                        "`contentDescription = \"\"` invalide dans semantics — utiliser une description significative. [SKILL-05 A]"
                    )
                }

                // Vérifier role absent quand contentDescription est présent
                val hasContentDescription = body.contains("contentDescription")
                val hasRole = body.contains("role") || body.contains("Role.")
                val parentChain = getModifierChainText(semanticsNode)
                val hasClickable = parentChain.contains("clickable") ||
                        parentChain.contains("selectable") ||
                        parentChain.contains("toggleable")

                if (hasClickable && hasContentDescription && !hasRole) {
                    context.report(
                        ISSUE_MISSING_ROLE,
                        semanticsNode,
                        context.getLocation(semanticsNode),
                        "`semantics` avec `contentDescription` mais sans `role` — ajouter `role = Role.Button` (ou .Checkbox, .RadioButton, .Switch, .Tab). [SKILL-05 A]"
                    )
                }
            }

            private fun getModifierChainText(node: UCallExpression): String {
                var current: UElement? = node
                val sb = StringBuilder()
                repeat(10) {
                    current = current?.uastParent
                    sb.append(current?.asSourceString()?.take(200) ?: "")
                }
                return sb.toString()
            }

            private fun findParentComposable(node: UCallExpression): String? {
                var current: UElement? = node.uastParent
                repeat(5) {
                    val callName = (current as? UCallExpression)?.methodName
                    if (callName != null) return callName
                    current = current?.uastParent
                }
                return null
            }
        }
}
