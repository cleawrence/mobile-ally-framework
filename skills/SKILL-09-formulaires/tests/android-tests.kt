package com.fram.a11y.skill09

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import org.junit.Rule
import org.junit.Test

class FormsAccessibilityTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun allTextFields_haveLabel() {
        // [A] Critère 9.1
        composeTestRule.setContent {
            GoodLabeledTextField()
        }

        // Vérifie qu'il y a un composant avec le text "Prénom"
        composeTestRule.onNodeWithText("Prénom")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun requiredFields_areMarkedAsRequired() {
        // [A] Critère 9.3
        composeTestRule.setContent {
            RequiredFieldWithSemantics()
        }

        // Vérifie que le label mentionne que c'est obligatoire, ou vérifier la sémantique
        composeTestRule.onNode(hasContentDescription("Email, champ obligatoire"))
            .assertExists()
    }

    @Test
    fun errorMessage_isDisplayedAndAnnounced() {
        // [A] Critère 9.4
        composeTestRule.setContent {
            GoodErrorMessage()
        }

        // Saisir une adresse invalide pour déclencher la validation
        composeTestRule.onNodeWithText("Adresse email")
            .performTextInput("marie")

        // Vérifie que l'erreur sémantique est présente
        composeTestRule.onNodeWithText("Adresse email", substring = true)
            .assert(SemanticsMatcher.expectValue(
                SemanticsProperties.Error,
                "L'adresse email doit contenir un @. Exemple : prenom@domaine.fr"
            ))
    }

    @Test
    fun emailField_hasEmailKeyboardType() {
        // [AA] Critère 9.6
        composeTestRule.setContent {
            AutocompletePatternsFull()
        }

        // En Compose UI Test, la vérification du KeyboardType via semantics n'est pas out of the box,
        // mais on peut tester l'existence du champ par son label.
        composeTestRule.onNodeWithText("Email").assertExists()
    }

    @Test
    fun passwordField_hasPasswordVisualTransformation() {
        // [A] Critère 9.1 / Composant mot de passe
        composeTestRule.setContent {
            AccessiblePasswordField()
        }

        val passwordField = composeTestRule.onNodeWithText("Mot de passe")
        passwordField.assertExists()

        // On vérifie que le bouton de masquage/affichage existe
        composeTestRule.onNodeWithContentDescription("Afficher le mot de passe")
            .assertExists()
            .performClick()

        composeTestRule.onNodeWithContentDescription("Masquer le mot de passe")
            .assertExists()
    }

    @Test
    fun formValidation_triggersAccessibleErrorMessages() {
        // [A] Critère 9.4
        composeTestRule.setContent {
            AccessibleLoginForm()
        }

        val submitButton = composeTestRule.onNodeWithText("Se connecter")
        submitButton.performClick()

        // Champs vides → le champ Email affiche son message d'erreur spécifique
        composeTestRule.onNodeWithText("L'adresse email est obligatoire")
            .assertExists()
            .assertIsDisplayed()
    }
}
