package com.fram.a11y.skill06

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test

class MandatoryElementsTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    // MARK: - TITRE D'ÉCRAN [A] — Critères 6.3, 6.4
    @Test
    fun screenHasRelevantAndUniqueTitle() {
        composeTestRule.setContent {
            ScreenTitlePattern()
        }

        // Vérifie qu'un élément texte agissant comme titre d'écran est présent
        composeTestRule
            .onNodeWithText("Paramètres du compte")
            .assertExists()
            .assertIsDisplayed()
    }

    // MARK: - CHANGEMENT DE LANGUE [A] — Critère 6.2
    @Test
    fun foreignLanguageElementExists() {
        composeTestRule.setContent {
            LanguageChangePattern()
        }

        // Vérifie la présence du texte avec un changement de locale.
        // Remarque: Compose UI Test ne fournit pas d'API directe pour affirmer `localeList` sur le style d'un Text.
        // L'audit manuel est nécessaire pour TalkBack.
        composeTestRule
            .onNodeWithText("To be or not to be, that is the question.")
            .assertExists()
    }
}
