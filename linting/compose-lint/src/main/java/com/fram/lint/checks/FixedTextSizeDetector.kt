// FRAM — FixedTextSizeDetector
// SKILL-03 [AA] — Critère 3.3 (Dynamic Type / Font Scale)
// Détecte : tailles de texte en dp au lieu de sp, et fontSize figée ignorant le fontScale

package com.fram.lint.checks

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*

@Suppress("UnstableApiUsage")
class FixedTextSizeDetector : Detector(), Detector.UastScanner {

    companion object {

        private val SKILL_URL = "https://fram.internal/skills/skill-03"

        /**
         * SKILL-03 [AA] 3.3 — Taille de texte en dp au lieu de sp
         *
         * En Android, les tailles de texte doivent utiliser l'unité `sp` (scale-independent pixels),
         * qui s'adapte au paramètre "Taille de la police" de l'utilisateur.
         * Utiliser `dp` fige la taille et empêche les utilisateurs malvoyants
         * d'agrandir le texte.
         *
         * WCAG 1.4.4 : le texte doit pouvoir être agrandi à 200% sans perte de contenu.
         */
        val ISSUE_FIXED_TEXT_SIZE_DP = Issue.create(
            id = "FixedTextSizeDp",
            briefDescription = "[FRAM SKILL-03] Taille de texte en dp au lieu de sp",
            explanation = """
                La taille de texte est définie en `dp` au lieu de `sp`.

                - **`sp` (scale-independent pixels)** : s'adapte au paramètre système
                  "Taille de la police" — **obligatoire** pour le texte ✅
                - **`dp` (density-independent pixels)** : ne s'adapte PAS à la taille
                  de police système — le texte reste figé ❌

                Environ **30% des utilisateurs** modifient la taille de police système.
                Le texte en `dp` est invisible pour les utilisateurs malvoyants
                qui agrandissent leur police à 200%.

                **Mauvais :**
                ```kotlin
                Text("Titre", fontSize = 14.dp.value.sp)  // ❌ Converti de dp — taille fixe
                Text("Titre", style = TextStyle(fontSize = 14.dp.value.sp))  // ❌
                ```

                **Bon :**
                ```kotlin
                Text("Titre", fontSize = 14.sp)  // ✅ S'adapte au fontScale
                Text("Titre", style = MaterialTheme.typography.bodyMedium)  // ✅ Utilise sp via Material
                ```

                Voir SKILL-03 [AA] critère 3.3 — $SKILL_URL
            """,
            category = Category.A11Y,
            priority = 7,
            severity = Severity.WARNING,
            implementation = Implementation(
                FixedTextSizeDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        ).setAndroidSpecific(true)
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UCallExpression::class.java, UQualifiedReferenceExpression::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {

            override fun visitCallExpression(node: UCallExpression) {
                val methodName = node.methodName ?: return
                val source = node.asSourceString()

                // Pattern 1 : Text(fontSize = N.dp)
                if (methodName == "Text" || methodName == "BasicText") {
                    if (source.contains("fontSize") && source.contains(".dp")) {
                        context.report(
                            ISSUE_FIXED_TEXT_SIZE_DP,
                            node,
                            context.getLocation(node),
                            "`fontSize` défini en `dp` — utiliser `sp` pour respecter le fontScale système. [SKILL-03 AA]"
                        )
                    }
                }

                // Pattern 2 : TextStyle(fontSize = N.dp)
                if (methodName == "TextStyle" || methodName == "SpanStyle") {
                    if (source.contains("fontSize") && source.contains(".dp")) {
                        context.report(
                            ISSUE_FIXED_TEXT_SIZE_DP,
                            node,
                            context.getLocation(node),
                            "`TextStyle.fontSize` en `dp` — remplacer par `sp` : `fontSize = 14.sp`. [SKILL-03 AA]"
                        )
                    }
                }
            }

            override fun visitQualifiedReferenceExpression(node: UQualifiedReferenceExpression) {
                // Pattern 3 : val textSize = 14.dp  (ensuite utilisé dans un Text)
                val source = node.asSourceString()
                if (source.endsWith(".dp") && isInTextSizeContext(node)) {
                    context.report(
                        ISSUE_FIXED_TEXT_SIZE_DP,
                        node,
                        context.getLocation(node),
                        "Valeur `dp` dans un contexte de taille de texte — utiliser `sp`. [SKILL-03 AA]"
                    )
                }
            }

            private fun isInTextSizeContext(node: UElement): Boolean {
                var current: UElement? = node.uastParent
                repeat(5) {
                    val text = current?.asSourceString()?.take(200) ?: ""
                    if (text.contains("fontSize") || text.contains("textSize") || text.contains("lineHeight")) {
                        return true
                    }
                    current = current?.uastParent
                }
                return false
            }
        }
}
