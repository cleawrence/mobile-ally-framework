// FRAM — IconContentDescriptionDetector
// SKILL-01 [A] — Critères 1.1, 1.2, 1.3
// Détecte : Icon() sans contentDescription, contentDescription vide, préfixe redondant

package com.fram.lint.checks

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*

@Suppress("UnstableApiUsage")
class IconContentDescriptionDetector : Detector(), Detector.UastScanner {

    companion object {

        private val SKILL_URL = "https://fram.internal/skills/skill-01"

        /**
         * SKILL-01 [A] 1.2 — Icon() sans contentDescription
         *
         * TalkBack lit le nom de la fonction Composable ("Icon") si contentDescription est absent.
         * Chaque Icon doit avoir soit contentDescription = null (décoratif) soit une description.
         */
        val ISSUE_ICON_MISSING_CONTENT_DESCRIPTION = Issue.create(
            id = "IconMissingContentDescription",
            briefDescription = "[FRAM SKILL-01] Icon() sans contentDescription",
            explanation = """
                Chaque `Icon()` doit déclarer explicitement son `contentDescription` :
                - `contentDescription = null` si l'icône est **décorative** (à côté d'un texte qui dit la même chose)
                - `contentDescription = "Description précise"` si l'icône est **porteuse d'information**

                Sans contentDescription, TalkBack ne peut pas annoncer l'icône et les utilisateurs aveugles
                perdent cette information.

                **Bon :**
                ```kotlin
                Icon(Icons.Default.Favorite, contentDescription = "Ajouter aux favoris")
                Icon(Icons.Default.Star, contentDescription = null) // Décoratif
                ```

                **Mauvais :**
                ```kotlin
                Icon(Icons.Default.Warning) // ❌ Pas de contentDescription
                ```

                Voir SKILL-01 [A] critère 1.2 — $SKILL_URL
            """,
            category = Category.A11Y,
            priority = 9,
            severity = Severity.ERROR,
            implementation = Implementation(
                IconContentDescriptionDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        ).setAndroidSpecific(true)

        /**
         * SKILL-01 [A] 1.2 — contentDescription = "" (vide)
         *
         * Un contentDescription vide est différent de null :
         * - null = ignoré par TalkBack (décoratif) ✅
         * - "" = TalkBack peut lire le nom de la Vue ou agir de façon imprévisible ❌
         */
        val ISSUE_EMPTY_CONTENT_DESCRIPTION = Issue.create(
            id = "EmptyContentDescription",
            briefDescription = "[FRAM SKILL-01] contentDescription = \"\" (vide)",
            explanation = """
                `contentDescription = ""` (chaîne vide) n'est PAS équivalent à `contentDescription = null`.

                - `null` → TalkBack **ignore** l'élément (comportement décoration correct) ✅
                - `""` → TalkBack peut lire le nom de classe ou un texte vide ❌

                Pour masquer un élément décoratif : `contentDescription = null`
                Pour les éléments porteurs d'information : fournir une vraie description.

                Voir SKILL-01 [A] critère 1.1 — $SKILL_URL
            """,
            category = Category.A11Y,
            priority = 9,
            severity = Severity.ERROR,
            implementation = Implementation(
                IconContentDescriptionDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        ).setAndroidSpecific(true)

        /**
         * SKILL-01 [A] 1.3 — Préfixe redondant dans le contentDescription
         *
         * TalkBack annonce déjà "Image" ou "Bouton" selon le rôle de l'élément.
         * Commencer par "image de" double cette information.
         */
        val ISSUE_REDUNDANT_PREFIX_CONTENT_DESCRIPTION = Issue.create(
            id = "RedundantContentDescriptionPrefix",
            briefDescription = "[FRAM SKILL-01] contentDescription commençant par 'image de' ou 'icône'",
            explanation = """
                TalkBack annonce déjà le type d'élément (Image, Bouton, etc.) avant le contentDescription.
                Commencer par "image de", "icône de", "photo de" provoque une redondance :
                TalkBack lirait "Image, image de chat" au lieu de "Image, chat".

                **Bon :** `contentDescription = "Chat qui dort"`
                **Mauvais :** `contentDescription = "image d'un chat qui dort"` ❌

                Voir SKILL-01 [A] critère 1.3 — $SKILL_URL
            """,
            category = Category.A11Y,
            priority = 5,
            severity = Severity.WARNING,
            implementation = Implementation(
                IconContentDescriptionDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        ).setAndroidSpecific(true)

        private val REDUNDANT_PREFIXES = listOf(
            "image de", "image d'", "icône de", "icone de",
            "photo de", "illustration de", "image of", "icon of"
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UCallExpression::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {

            override fun visitCallExpression(node: UCallExpression) {
                val methodName = node.methodName ?: return

                // Détecter les appels Icon() et AsyncImage()
                if (methodName !in listOf("Icon", "AsyncImage", "Image")) return

                val contentDescArg = findNamedArgument(node, "contentDescription")

                when {
                    // Cas 1 : contentDescription absent complètement
                    contentDescArg == null -> {
                        context.report(
                            ISSUE_ICON_MISSING_CONTENT_DESCRIPTION,
                            node,
                            context.getLocation(node),
                            "${methodName}() sans `contentDescription` — ajouter `contentDescription = null` (décoratif) ou une description (informatif). [SKILL-01 A]"
                        )
                    }

                    // Cas 2 : contentDescription = "" (vide)
                    else -> {
                        val value = contentDescArg.asSourceString().trim()
                        if (value == "\"\"") {
                            context.report(
                                ISSUE_EMPTY_CONTENT_DESCRIPTION,
                                contentDescArg,
                                context.getLocation(contentDescArg),
                                "`contentDescription = \"\"` invalide — utiliser `null` pour un élément décoratif. [SKILL-01 A]"
                            )
                        }

                        // Cas 3 : préfixe redondant
                        val stringValue = extractStringValue(value)
                        if (stringValue != null) {
                            val lower = stringValue.lowercase()
                            val prefix = REDUNDANT_PREFIXES.firstOrNull { lower.startsWith(it) }
                            if (prefix != null) {
                                context.report(
                                    ISSUE_REDUNDANT_PREFIX_CONTENT_DESCRIPTION,
                                    contentDescArg,
                                    context.getLocation(contentDescArg),
                                    "contentDescription commence par \"$prefix\" — TalkBack annonce déjà le type. " +
                                    "Préférer \"${stringValue.replaceFirst(prefix, "", ignoreCase = true).trimStart()}\" [SKILL-01 A]"
                                )
                            }
                        }
                    }
                }
            }

            private fun findNamedArgument(call: UCallExpression, name: String): UExpression? {
                // node.asSourceString() / UNamedExpression ne préservent pas de façon fiable le nom
                // d'un argument nommé pour un appel Kotlin-vers-Kotlin (constaté empiriquement avec
                // lint-api 31.5.0 : valueArguments perd l'association nom<->argument). La résolution
                // du appelé + getArgumentForParameter() est la seule façon fiable de retrouver
                // l'argument "contentDescription" quel que soit l'ordre d'écriture au site d'appel.
                val resolved = call.resolve()
                val paramIndex = resolved?.parameterList?.parameters?.indexOfFirst { it.name == name }
                if (resolved != null && paramIndex != null && paramIndex >= 0) {
                    return call.getArgumentForParameter(paramIndex)
                }

                // Repli si la résolution échoue (ex: bibliothèque absente du classpath d'analyse) :
                // position habituelle de contentDescription dans Icon/Image/AsyncImage.
                return call.valueArguments.firstOrNull {
                    (it as? UNamedExpression)?.name == name
                } ?: call.valueArguments.getOrNull(
                    when (call.methodName) {
                        "Icon", "Image", "AsyncImage" -> 1
                        else -> -1
                    }
                )
            }

            private fun extractStringValue(source: String): String? {
                return if (source.startsWith("\"") && source.endsWith("\"")) {
                    source.removeSurrounding("\"")
                } else null
            }
        }
}
