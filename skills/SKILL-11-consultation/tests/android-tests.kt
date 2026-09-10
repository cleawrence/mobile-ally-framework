package com.fram.a11y.skill11.tests

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test
import com.fram.a11y.skill11.*

class ConsultationAccessibilityTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    // MARK: - 1. ZONE DE TOUCHE [AA] — Critère 11.5
    @Test
    fun testTouchTargetIsAtLeast48dp() {
        composeTestRule.setContent {
            TouchTargetPattern()
        }

        // On vérifie le composant accessible
        composeTestRule.onNodeWithContentDescription("Éditer")
            .assertExists()
            .assertWidthIsAtLeast(48.dp)
            .assertHeightIsAtLeast(48.dp)
    }

    // MARK: - 2. TIMEOUT [A] — Critère 11.1
    @Test
    fun testTimeoutDialogProvidesExtensionOption() {
        composeTestRule.setContent {
            SessionTimeoutPattern()
        }

        // Déclencher la modale
        composeTestRule.onNodeWithText("Simuler l'expiration").performClick()

        // Vérifier l'apparition de l'alerte
        composeTestRule.onNodeWithText("Expiration de session imminente")
            .assertIsDisplayed()

        // Vérifier la présence des options
        composeTestRule.onNodeWithText("Prolonger")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    // MARK: - 3. ERREURS RÉCUPÉRABLES [AA] — Critère 11.6
    @Test
    fun testDestructiveActionHasConfirmation() {
        composeTestRule.setContent {
            RecoverableErrorConfirmationPattern()
        }

        // Clic sur l'action irréversible
        composeTestRule.onNodeWithText("Supprimer le compte").performClick()

        // La modale de confirmation doit s'afficher
        composeTestRule.onNodeWithText("Supprimer définitivement ?")
            .assertIsDisplayed()

        // L'action annuler doit être proposée et fermer la modale
        composeTestRule.onNodeWithText("Annuler").performClick()
        composeTestRule.onNodeWithText("Supprimer définitivement ?").assertDoesNotExist()
    }

    // MARK: - 4. DONNÉES CONSERVÉES APRÈS ERREUR [AA]
    @Test
    fun testInputsAreRetainedAfterValidationError() {
        composeTestRule.setContent {
            InputRetentionPattern()
        }

        val inputNode = composeTestRule.onNodeWithText("Adresse e-mail")
        val invalidEmail = "mauvais-format-email"
        
        // Entrer une saisie invalide
        inputNode.performTextInput(invalidEmail)

        // Soumettre
        composeTestRule.onNodeWithText("Valider").performClick()

        // Vérifier que le message d'erreur est affiché
        composeTestRule.onNodeWithText("Veuillez entrer une adresse e-mail valide.")
            .assertIsDisplayed()

        // Vérifier que la saisie utilisateur n'a PAS été effacée
        composeTestRule.onNodeWithText(invalidEmail)
            .assertIsDisplayed()
    }
}
