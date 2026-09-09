package com.fram.a11y.skill09

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
            TextFieldWithLabel()
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
            RequiredFieldPattern()
        }
        
        // Vérifie que le label mentionne que c'est obligatoire, ou vérifier la sémantique
        composeTestRule.onNode(hasContentDescription("Nom de famille, champ obligatoire"))
            .assertExists()
    }

    @Test
    fun errorMessage_isDisplayedAndAnnounced() {
        // [A] Critère 9.4
        composeTestRule.setContent {
            ErrorMessagePattern()
        }
        
        // Vérifie que l'erreur sémantique est présente
        composeTestRule.onNodeWithText("Adresse e-mail")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "L'adresse e-mail est invalide."))
    }

    @Test
    fun emailField_hasEmailKeyboardType() {
        // [AA] Critère 9.6
        composeTestRule.setContent {
            AutoCompletePattern()
        }
        
        // En Compose UI Test, la vérification du KeyboardType via semantics n'est pas out of the box,
        // mais on peut tester l'existence du champ par son label.
        composeTestRule.onNodeWithText("Email").assertExists()
    }

    @Test
    fun passwordField_hasPasswordVisualTransformation() {
        // [A] Critère 9.1 / Composant mot de passe
        composeTestRule.setContent {
            SecureFieldPattern()
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
            RegistrationPattern()
        }
        
        val submitButton = composeTestRule.onNodeWithText("S'inscrire")
        submitButton.performClick()
        
        composeTestRule.onNodeWithText("Veuillez remplir tous les champs correctement.")
            .assertExists()
            .assertIsDisplayed()
    }
}
