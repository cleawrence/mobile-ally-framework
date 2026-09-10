// FRAM — HardcodedColorDetector
// SKILL-02 [A]/[AA] — Critères 2.1, 2.2
// Détecte : Color(0xFF...) hardcodé hors MaterialTheme, Color.Red/Green pour statut

package com.fram.lint.checks

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*

@Suppress("UnstableApiUsage")
class HardcodedColorDetector : Detector(), Detector.UastScanner {

    companion object {

        private val SKILL_URL = "https://cleawrence.github.io/mobile-ally-framework/skills/skill-02/"

        /**
         * SKILL-02 [AA] 2.2 — Couleur hardcodée hors MaterialTheme
         *
         * Une couleur hardcodée `Color(0xFF...)` est statique et peut ne pas
         * respecter le ratio de contraste en dark mode.
         */
        val ISSUE_HARDCODED_COLOR = Issue.create(
            id = "HardcodedAccessibilityColor",
            briefDescription = "[FRAM SKILL-02] Couleur hardcodée hors MaterialTheme.colorScheme",
            explanation = """
                `Color(0xFFxxxxxx)` est une couleur statique qui peut provoquer des problèmes
                de contraste en dark mode ou en mode "Augmenter le contraste".

                **Préférer :**
                - `MaterialTheme.colorScheme.primary` / `.onSurface` / `.error`
                - Couleurs définies dans le thème `MaterialTheme`
                - `Color(0xFF...)` uniquement dans la définition du thème (`ColorScheme`)

                **Acceptable :**
                - Dans le fichier de thème (`Theme.kt`, `Color.kt`)
                - Pour des éléments décoratifs non-informatifs (fond de carte, etc.)

                Voir SKILL-02 [AA] critère 2.2 — $SKILL_URL
            """,
            category = Category.A11Y,
            priority = 6,
            severity = Severity.WARNING,
            implementation = Implementation(
                HardcodedColorDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        ).setAndroidSpecific(true)

        /**
         * SKILL-02 [A] 2.1 — Couleur de statut seule (Color.Red / Color.Green)
         *
         * Utiliser Color.Red seul pour indiquer une erreur est une violation du
         * critère 2.1 [A] : l'information ne doit pas être transmise par la couleur seule.
         */
        val ISSUE_COLOR_ONLY_STATUS = Issue.create(
            id = "ColorOnlyStatusCommunication",
            briefDescription = "[FRAM SKILL-02] Statut communiqué uniquement par Color.Red / Color.Green",
            explanation = """
                Utiliser `Color.Red` ou `Color.Green` seul pour indiquer un état (erreur/succès)
                est une violation du critère WCAG 1.4.1 / RAAM 2.1 [A].

                Les utilisateurs daltoniens (8% des hommes) ne distinguent pas le rouge du vert.

                **Mauvais :**
                ```kotlin
                Text("Erreur !", color = Color.Red) // ❌ Couleur seule
                ```

                **Bon :**
                ```kotlin
                Row {
                    Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Text("Erreur : email invalide", color = MaterialTheme.colorScheme.error)
                }
                // ✅ Icône + couleur + texte explicite
                ```

                Voir SKILL-02 [A] critère 2.1 — $SKILL_URL
            """,
            category = Category.A11Y,
            priority = 8,
            severity = Severity.WARNING,
            implementation = Implementation(
                HardcodedColorDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        ).setAndroidSpecific(true)

        // Références qualifiées de statut couramment mal utilisées (Color.Xxx, sans appel)
        private val STATUS_COLOR_REFS = setOf("Color.Red", "Color.Green", "Color.Yellow")

        // Constructeurs Color(0xFFxxxxxx) représentant une couleur de statut courante
        // (comparaison insensible à la casse sur le texte source réel du littéral hexadécimal)
        private val STATUS_COLOR_HEX = setOf(
            "0xFFFF0000", "0xFF00FF00", "0xFFFF4444", "0xFF4CAF50", "0xFFF44336"
        )

        // Fichiers de thème exclus du warning (normal d'y définir des couleurs)
        private val THEME_FILE_PATTERNS = listOf("Theme", "Color", "Colors", "Palette", "Design")
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UCallExpression::class.java, UQualifiedReferenceExpression::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {

            override fun visitCallExpression(node: UCallExpression) {
                val methodName = node.methodName ?: return

                // Exclure les fichiers de thème
                val fileName = context.file.name
                if (THEME_FILE_PATTERNS.any { fileName.contains(it, ignoreCase = true) }) return

                // `Color(...)` est un appel au constructeur d'une classe Kotlin : UAST rapporte
                // methodName = "<init>" pour ces appels, jamais le nom "Color" -- ce detector n'a
                // donc jamais pu se déclencher via `when (methodName) { "Color" -> ... }` sur du
                // code réel. Vérifier isConstructorCall() + le nom de la classe résolue à la place.
                val isColorConstructor = methodName == "Color" ||
                        (node.kind == UastCallKind.CONSTRUCTOR_CALL && node.resolve()?.containingClass?.name == "Color")
                if (isColorConstructor) {
                    checkHardcodedColor(context, node)
                }
            }

            override fun visitQualifiedReferenceExpression(node: UQualifiedReferenceExpression) {
                // Détecter l'utilisation de Color.Red / Color.Green / Color.Yellow comme couleur
                // de statut. Contrairement aux arguments nommés (voir les autres detectors),
                // node.asSourceString() rend correctement une référence qualifiée comme "Color.Red"
                // -- vérifié empiriquement, pas de piège de rendu ici.
                val fileName = context.file.name
                if (THEME_FILE_PATTERNS.any { fileName.contains(it, ignoreCase = true) }) return

                // stripWhitespace() : le TestMode "Extra whitespace added" de lint-tests insère
                // des espaces autour de chaque token ("Color . Red" au lieu de "Color.Red") —
                // comparer sur le texte débarrassé des espaces reste robuste à la mise en forme
                // exacte. Voir SourceTextUtils.kt.
                val src = stripWhitespace(node.asSourceString())
                val statusColor = STATUS_COLOR_REFS.firstOrNull { it == src } ?: return
                checkColorOnlyStatus(context, node, statusColor)
            }

            private fun checkHardcodedColor(context: JavaContext, node: UCallExpression) {
                val args = node.valueArguments
                if (args.isEmpty()) return

                // args[0].asSourceString() renvoie la valeur décimale du littéral ("4281545523"),
                // pas la notation hexadécimale écrite au site d'appel ("0xFF333333") -- UAST
                // recanonicalise les littéraux numériques. sourcePsi.text donne le texte source
                // réel tel qu'écrit.
                val firstArg = (args[0].sourcePsi?.text ?: args[0].asSourceString()).trim()

                // Color(0xFFxxxxxx) — Long hex
                if (firstArg.startsWith("0x") || firstArg.startsWith("0X")) {
                    // Exclure si dans un composable de fond décoratif (heuristique)
                    val parentContext = node.uastParent?.asSourceString()?.take(100) ?: ""
                    val isLikelyDecorative = parentContext.contains("background") &&
                            !parentContext.contains("error") &&
                            !parentContext.contains("status") &&
                            !parentContext.contains("state")

                    if (!isLikelyDecorative) {
                        context.report(
                            ISSUE_HARDCODED_COLOR,
                            node,
                            context.getLocation(node),
                            "`Color($firstArg)` hardcodé — utiliser `MaterialTheme.colorScheme.xxx` pour garantir le contraste en dark mode. [SKILL-02 AA]"
                        )
                    }

                    // Reconnaître aussi les teintes de statut courantes exprimées en hex brut
                    val normalizedHex = firstArg.uppercase().removeSuffix("L")
                    val matchedHex = STATUS_COLOR_HEX.firstOrNull { it.uppercase() == normalizedHex }
                    if (matchedHex != null) {
                        checkColorOnlyStatus(context, node, "Color($firstArg)")
                    }
                }
            }

            private fun checkColorOnlyStatus(
                context: JavaContext,
                node: UElement,
                statusColor: String
            ) {
                // Vérifier si une icône ou un texte d'erreur accompagne la couleur
                val surroundingCode = getSurroundingCode(node, 500)
                val hasIcon = surroundingCode.contains("Icon(") || surroundingCode.contains("Image(")
                val hasErrorText = surroundingCode.contains("Erreur") ||
                        surroundingCode.contains("erreur") ||
                        surroundingCode.contains("Error") ||
                        surroundingCode.contains("error") ||
                        surroundingCode.contains("Warning") ||
                        surroundingCode.contains("warning")

                if (!hasIcon && !hasErrorText) {
                    context.report(
                        ISSUE_COLOR_ONLY_STATUS,
                        node,
                        context.getLocation(node),
                        "`$statusColor` utilisé sans icône ni texte d'erreur explicite — " +
                        "doubler avec une icône (Icon(Icons.Default.Error, ...)) pour les daltoniens. [SKILL-02 A]"
                    )
                }
            }

            private fun getSurroundingCode(node: UElement, chars: Int): String {
                var current: UElement? = node
                repeat(8) { current = current?.uastParent }
                // Voir SourceTextUtils.kt : ne pas retirer les commentaires ici ferait qu'un
                // commentaire mentionnant "Erreur"/"Icon(" ferait passer à tort ce check.
                return stripComments(current?.asSourceString()?.take(chars) ?: "")
            }
        }
}
