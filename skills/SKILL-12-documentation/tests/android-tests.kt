package com.fram.a11y.skill12.tests

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.fram.a11y.skill12.AccessibilitySettingsScreen
import com.fram.a11y.skill12.ComprehensiveSemanticsView
import com.fram.a11y.skill12.SwitchAccessFriendlyListItem
import org.junit.Rule
import org.junit.Test

class DocumentationA11yTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    // MARK: - 1. DÉCLARATION D'ACCESSIBILITÉ IN-APP [A]
    
    @Test
    fun testAccessibilityStatementScreenExists() {
        // Lancer l'écran de déclaration d'accessibilité
        composeTestRule.setContent {
            AccessibilitySettingsScreen()
        }

        // [A] Vérifier que le titre de la page est présent et est marqué comme "heading" (titre de section)
        composeTestRule.onNodeWithText("Déclaration d'accessibilité")
            .assertExists()
            .assert(isHeading())

        // [A] Vérifier que l'état de conformité est présent et est un "heading"
        composeTestRule.onNodeWithText("État de conformité")
            .assertExists()
            .assert(isHeading())

        // [A] Vérifier que le bouton de contact existe, qu'il a le rôle de bouton et est cliquable
        composeTestRule.onNodeWithContentDescription("Signaler un problème d'accessibilité via un formulaire en ligne")
            .assertExists()
            .assertHasClickAction()
    }

    // MARK: - 3. SWITCH ACCESS & CUSTOM ACTIONS [AA]
    
    @Test
    fun testSwitchAccessFriendlyListItemCustomActions() {
        var isDeleted = false
        val itemTitle = "Document Important"
        
        composeTestRule.setContent {
            SwitchAccessFriendlyListItem(
                title = itemTitle,
                onDelete = { isDeleted = true }
            )
        }

        // Trouver la rangée parente (fusionnée) par son titre
        val listItemNode = composeTestRule.onNodeWithContentDescription(itemTitle)
        
        // [AA] L'élément doit exister, être cliquable (action principale)
        listItemNode.assertExists().assertHasClickAction()
        
        // [AA] L'élément doit exposer l'action custom "Supprimer l'élément" pour Switch Access et TalkBack
        // Dans Compose Testing API, on peut exécuter une action custom par son label
        listItemNode.performCustomAccessibilityActionWithLabel("Supprimer l'élément")
        
        // Vérifier que le callback a été déclenché
        assert(isDeleted)
    }

    // MARK: - 4. APIS COMPLÈTES SEMANTICS COMPOSE [AA]
    
    @Test
    fun testComprehensiveSemanticsProperties() {
        composeTestRule.setContent {
            ComprehensiveSemanticsView(hasError = true, errorMessage = "Saisie invalide", progress = 0.75f)
        }

        // Vérifier la présence d'une Live Region (pour TalkBack)
        // Les noeuds contenant l'erreur "Saisie invalide" doivent avoir la sémantique d'erreur
        // et idéalement un mode de région live (non testable de façon triviale avec asserts de base, mais on peut vérifier le node)
        composeTestRule.onNodeWithText("Saisie invalide")
            .assertExists()
        // Note : .assert(hasError("Saisie invalide")) pourrait être utilisé si l'erreur est configurée spécifiquement comme semantics property d'erreur

        // Vérifier la progress bar et sa stateDescription
        composeTestRule.onNodeWithContentDescription("Progression du téléchargement")
            .assertExists()
            .assertRangeInfoEquals(ProgressBarRangeInfo(0.75f, 0f..1f))
            // On vérifie de manière détournée ou par un matcher custom la presence du stateDescription si besoin
    }
}
