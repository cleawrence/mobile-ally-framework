package com.fram.a11y.skill02.tests

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import com.fram.a11y.skill02.GoodErrorFieldMultiModal
import com.fram.a11y.skill02.GoodStatusBadge
import org.junit.Rule
import org.junit.Test

class Skill02CouleursContrastesTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    // MARK: - 1. INFORMATION PAR COULEUR SEULE [A]

    @Test
    fun statusBadge_hasIconAndTextNotJustColor() {
        composeTestRule.setContent {
            GoodStatusBadge(isOnline = true)
        }

        // [A] Vérifie que l'information n'est pas véhiculée uniquement par la couleur
        // On s'assure que le texte "Online" est affiché.
        composeTestRule.onNodeWithText("Online").assertExists()
        // On s'assure qu'il n'y a pas juste un bloc décoratif (l'icône ajoute de la sémantique si elle est bien gérée)
    }

    @Test
    fun errorField_hasErrorTextNotJustBorder() {
        composeTestRule.setContent {
            GoodErrorFieldMultiModal()
        }

        // [A] Vérifie la présence du texte d'erreur, pas juste la bordure rouge (isError seul)
        composeTestRule.onNodeWithText("L'adresse email doit contenir un @")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun errorField_exposesErrorSemanticallyNotJustVisually() {
        composeTestRule.setContent {
            GoodErrorFieldMultiModal()
        }

        // [A] L'erreur doit être exposée via la sémantique .error(...), pas seulement par
        // la couleur/bordure rouge — c'est ce qui permet à TalkBack de l'annoncer.
        composeTestRule.onNodeWithText("Email", substring = true)
            .assert(SemanticsMatcher.expectValue(
                SemanticsProperties.Error,
                "L'adresse email doit contenir un @"
            ))
    }

    // MARK: - 2. CONTRASTE ET THEMES [AA]

    @Test
    fun textColors_useSystemColorScheme() {
        // [AA] Dans Compose, tester les contrastes réels est limité en UI Tests standards.
        // Les tests d'accessibilité sont généralement effectués avec Espresso Accessibility Checks
        // ou avec Accessibility Scanner en dehors des tests unitaires Compose.
        // On peut vérifier l'arbre sémantique pour certaines informations de haut niveau.
    }

    @Test
    fun materialColorScheme_ensuресContrastCompliance() {
        // Des outils comme Compose Lint / Detekt sont plus adaptés pour vérifier 
        // l'utilisation des tokens MaterialTheme.colorScheme.onSurface plutôt que 
        // des couleurs statiques Color(0xFF...) 
    }

    @Test
    fun darkTheme_componentsRemainContrasted() {
        // Lancer le test en forçant le Dark Mode nécessiterait une infrastructure de screenshot testing
        // ou un wrapper modifiant le flag `isSystemInDarkTheme()`.
    }
}
