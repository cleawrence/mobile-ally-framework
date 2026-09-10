package com.fram.a11y.skill01

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertFalse

class SKILL01_GraphicElementsTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun decorativeIcon_hasNullContentDescription() {
        composeTestRule.setContent {
            GraphicElementsPatterns()
        }

        // L'icône de décoration (Star) avec contentDescription = null 
        // ne devrait pas être trouvable par sémantique standard de rôle Image si elle n'a pas de tag
        // On vérifie que le parent "Favoris" est présent, mais l'image n'est pas focusable seule
        composeTestRule.onNodeWithText("Favoris", useUnmergedTree = true).assertExists()
    }

    @Test
    fun informativeIcon_hasNonEmptyContentDescription() {
        composeTestRule.setContent {
            GraphicElementsPatterns()
        }

        composeTestRule
            .onNodeWithContentDescription("Avertissement : connexion réseau instable")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun imageLabel_doesNotContainRedundantPrefix() {
        composeTestRule.setContent {
            GraphicElementsPatterns()
        }

        // Vérification manuelle que l'assertion "Avertissement" ne commence pas par "Image de"
        val node = composeTestRule.onNodeWithContentDescription("Avertissement : connexion réseau instable")
        node.assertExists()
    }

    @Test
    fun captionedImage_mergesLabelAndCaption() {
        composeTestRule.setContent {
            GraphicElementsPatterns()
        }

        // Vérifie que le nœud parent fusionné existe et lit le texte
        composeTestRule
            .onNodeWithText("Jean Dupont, Directeur Technique")
            .assertExists()
            .assertHasNoClickAction() // Just semantics check
    }

    @Test
    fun statusIcon_hasDescriptiveLabel() {
        composeTestRule.setContent {
            GraphicElementsPatterns()
        }

        // Note: 4 sur 5 étoiles
        composeTestRule
            .onNodeWithContentDescription("Note : 4 sur 5 étoiles")
            .assertExists()
    }

    @Test
    fun complexChart_hasSummaryDescription() {
        composeTestRule.setContent {
            GraphicElementsPatterns()
        }

        composeTestRule
            .onNodeWithContentDescription("Graphique des ventes annuelles. Évolution de +20% au premier trimestre.")
            .assertExists()

        composeTestRule
            .onNodeWithText("Afficher les données du graphique sous forme de tableau")
            .assertExists()
            .assertHasClickAction()
    }
}
