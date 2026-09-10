// FRAM — Utilitaires partagés pour l'analyse heuristique de texte source
//
// Plusieurs detectors matchent des mots-clés (ex: "semantics", "heading()") sur du texte
// reconstruit via UElement.asSourceString(). Avec la version courante de lint-api/UAST, ce
// texte reconstruit inclut les commentaires de fin de ligne du code source — un commentaire
// pédagogique du type "// ❌ Pas de topBar, pas de heading()" contient alors littéralement
// le mot-clé recherché et fait échouer silencieusement la détection (faux négatif). Retirer
// les commentaires avant tout matching heuristique évite cette classe de bug.

package com.fram.lint.checks

private val LINE_COMMENT = Regex("//[^\n]*")
private val BLOCK_COMMENT = Regex("/\\*.*?\\*/", RegexOption.DOT_MATCHES_ALL)

internal fun stripComments(text: String): String =
    text.replace(BLOCK_COMMENT, "").replace(LINE_COMMENT, "")

/**
 * Retire tous les espaces/retours à la ligne. Utile pour comparer un fragment de code à un
 * motif attendu (ex: ".dp") sans dépendre de la mise en forme exacte du texte source
 * reconstruit — le harness de test lint-tests rejoue chaque test avec des espaces
 * additionnels insérés entre les tokens ("Extra whitespace added" TestMode) pour vérifier que
 * les detectors restent robustes à la mise en forme.
 */
internal fun stripWhitespace(text: String): String =
    text.filterNot { it.isWhitespace() }
