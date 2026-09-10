// FRAM — TouchTargetDetector
// SKILL-11 [AA] — Critère 11.5 (Zone de touche minimum 48×48dp)
// Détecte : IconButton/Icon avec taille < 48dp, Modifier.size < 48dp sur éléments interactifs

package com.fram.lint.checks

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*

@Suppress("UnstableApiUsage")
class TouchTargetDetector : Detector(), Detector.UastScanner {

    companion object {

        private val SKILL_URL = "https://cleawrence.github.io/mobile-ally-framework/skills/skill-11/"

        // Material Design : zone de touche minimum 48×48dp
        // WCAG 2.5.5 (AAA) : 44×44 CSS px — sur mobile Android, 48dp est le standard
        private const val MIN_TOUCH_TARGET_DP = 48

        /**
         * SKILL-11 [AA] 11.5 — Zone de touche inférieure à 48dp
         *
         * Les utilisateurs avec des difficultés motrices (Parkinson, arthrite, etc.)
         * ont besoin d'une zone de touche suffisamment grande.
         * Material Design et Android recommandent un minimum de 48×48dp.
         */
        val ISSUE_SMALL_TOUCH_TARGET = Issue.create(
            id = "SmallTouchTarget",
            briefDescription = "[FRAM SKILL-11] Zone de touche potentiellement < 48dp",
            explanation = """
                Les éléments interactifs (boutons, cases à cocher, icônes cliquables) doivent
                avoir une zone de touche minimum de **48×48dp** (Material Design) pour être
                utilisables par les personnes avec des difficultés motrices.

                Cela concerne :
                - Les personnes atteintes de Parkinson, d'arthrite ou de tremblements
                - Les utilisateurs avec une motricité réduite des doigts
                - Environ 15% des adultes de plus de 50 ans

                **Mauvais :**
                ```kotlin
                IconButton(
                    onClick = { /* ... */ },
                    modifier = Modifier.size(24.dp) // ❌ Trop petit
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer")
                }
                ```

                **Bon :**
                ```kotlin
                IconButton(
                    onClick = { /* ... */ }
                    // ✅ IconButton Material3 a déjà 48dp de zone de touche par défaut
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer")
                }

                // Ou pour un composable custom :
                Box(
                    modifier = Modifier
                        .minimumInteractiveComponentSize() // ✅ Garantit 48dp minimum
                        .clickable { /* ... */ }
                )
                ```

                Voir SKILL-11 [AA] critère 11.5 — $SKILL_URL
            """,
            category = Category.A11Y,
            priority = 7,
            severity = Severity.WARNING,
            implementation = Implementation(
                TouchTargetDetector::class.java,
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
                val source = node.asSourceString()

                when (methodName) {
                    // Pattern 1 : IconButton / Button avec Modifier.size trop petit
                    "IconButton", "Button", "TextButton", "OutlinedButton",
                    "FloatingActionButton", "SmallFloatingActionButton" -> {
                        checkExplicitSizeOnInteractiveComponent(context, node, source, methodName)
                    }

                    // Pattern 2 : Modifier.size sur un élément clickable
                    "size" -> {
                        checkSizeOnClickable(context, node, source)
                    }
                }
            }

            private fun checkExplicitSizeOnInteractiveComponent(
                context: JavaContext,
                node: UCallExpression,
                source: String,
                composableName: String
            ) {
                // Rechercher Modifier.size(N.dp) dans les arguments
                val sizeMatch = Regex("""\.size\(\s*(\d+)\.dp\s*\)""").find(source)
                if (sizeMatch != null) {
                    val sizeValue = sizeMatch.groupValues[1].toIntOrNull() ?: return
                    if (sizeValue < MIN_TOUCH_TARGET_DP) {
                        context.report(
                            ISSUE_SMALL_TOUCH_TARGET,
                            node,
                            context.getLocation(node),
                            "`$composableName` avec `.size(${sizeValue}.dp)` — zone de touche " +
                            "inférieure au minimum de ${MIN_TOUCH_TARGET_DP}dp. " +
                            "Utiliser `.minimumInteractiveComponentSize()` ou " +
                            "`.size(${MIN_TOUCH_TARGET_DP}.dp)`. [SKILL-11 AA]"
                        )
                    }
                }

                // Rechercher Modifier.height(N.dp) ou .width(N.dp) trop petit
                val heightMatch = Regex("""\.height\(\s*(\d+)\.dp\s*\)""").find(source)
                if (heightMatch != null) {
                    val heightValue = heightMatch.groupValues[1].toIntOrNull() ?: return
                    if (heightValue < MIN_TOUCH_TARGET_DP) {
                        context.report(
                            ISSUE_SMALL_TOUCH_TARGET,
                            node,
                            context.getLocation(node),
                            "`$composableName` avec `.height(${heightValue}.dp)` — hauteur " +
                            "inférieure au minimum de ${MIN_TOUCH_TARGET_DP}dp. [SKILL-11 AA]"
                        )
                    }
                }
            }

            private fun checkSizeOnClickable(
                context: JavaContext,
                node: UCallExpression,
                source: String
            ) {
                // Vérifier si .size() est dans un contexte clickable
                val parentChain = getModifierChainText(node)
                val isClickable = parentChain.contains("clickable") ||
                        parentChain.contains("selectable") ||
                        parentChain.contains("toggleable") ||
                        parentChain.contains("combinedClickable")

                if (!isClickable) return

                // Extraire la valeur
                val args = node.valueArguments
                if (args.isEmpty()) return
                val sizeArg = args[0].asSourceString().trim()
                val sizeMatch = Regex("""(\d+)\.dp""").find(sizeArg)
                val sizeValue = sizeMatch?.groupValues?.get(1)?.toIntOrNull() ?: return

                if (sizeValue < MIN_TOUCH_TARGET_DP) {
                    // Vérifier si minimumInteractiveComponentSize est déjà utilisé
                    if (parentChain.contains("minimumInteractiveComponentSize")) return

                    context.report(
                        ISSUE_SMALL_TOUCH_TARGET,
                        node,
                        context.getLocation(node),
                        "Élément interactif avec `.size(${sizeValue}.dp)` — zone de touche " +
                        "inférieure à ${MIN_TOUCH_TARGET_DP}dp. Ajouter " +
                        "`.minimumInteractiveComponentSize()` dans la chaîne Modifier. [SKILL-11 AA]"
                    )
                }
            }

            private fun getModifierChainText(node: UElement): String {
                var current: UElement? = node
                val sb = StringBuilder()
                repeat(12) {
                    current = current?.uastParent
                    sb.append(current?.asSourceString()?.take(300) ?: "")
                }
                return sb.toString()
            }
        }
}
