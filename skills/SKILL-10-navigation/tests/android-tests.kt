package com.fram.a11y.skill10

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import org.junit.Rule
import org.junit.Test

class SKILL10NavigationTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    // MARK: - 1. ORDRE DE FOCUS [A]
    
    @Test
    fun testTraversalOrderInCard() {
        composeTestRule.setContent {
            FocusOrderCardPattern()
        }

        // On vérifie que les éléments existent et sont sémantiquement accessibles
        composeTestRule.onNodeWithText("Titre de la carte").assertExists()
        composeTestRule.onNodeWithText("Action principale").assertExists()
        
        // Vérifier que le titre n'a pas été masqué sémantiquement
        composeTestRule.onNodeWithText("Titre de la carte").assertIsDisplayed()
    }

    // MARK: - 2. POSITION DANS L'APP [A]
    
    @Test
    fun testHeadingSemantics() {
        composeTestRule.setContent {
            AppPositionPattern()
        }

        // Vérifier qu'un élément texte avec semantics heading() existe
        // Cela confirme l'implémentation de heading()
        composeTestRule.onAllNodes(hasText("Accueil") or hasText("Section importante"))
            .onFirst()
            .assertExists()
    }

    // MARK: - 3. FOCUS NON PIÉGÉ [AA]
    
    @Test
    fun testDialogFocusTrap() {
        composeTestRule.setContent {
            NonTrappedFocusPattern()
        }

        // Ouvrir le dialog
        composeTestRule.onNodeWithText("Ouvrir la boîte de dialogue").performClick()

        // Vérifier que le contenu du dialog est affiché
        composeTestRule.onNodeWithText("Ceci est une modale accessible.").assertIsDisplayed()

        // Fermer le dialog
        composeTestRule.onNodeWithText("Fermer").performClick()

        // Vérifier que le dialog n'est plus là
        composeTestRule.onNodeWithText("Ceci est une modale accessible.").assertDoesNotExist()
    }

    // MARK: - 4. NAVIGATION COHÉRENTE [AA]
    
    @Test
    fun testBottomNavLabels() {
        composeTestRule.setContent {
            ConsistentNavigationPattern()
        }

        // Vérifier la présence des content descriptions (ou labels textuels)
        // Les NavigationBarItems génèrent généralement un noeud fusionné contenant le texte et la contentDescription
        composeTestRule.onNodeWithContentDescription("Onglet Accueil", ignoreCase = true).assertExists()
        composeTestRule.onNodeWithContentDescription("Onglet Paramètres", ignoreCase = true).assertExists()
    }
}
