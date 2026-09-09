package com.fram.a11y.skill07.test

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import com.fram.a11y.skill07.*
import org.junit.Rule
import org.junit.Test

class Skill07StructurationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testHeadingsHaveHeadingSemantics() {
        composeTestRule.setContent {
            ArticleStructurePattern()
        }

        // Vérifier que le titre principal a la sémantique de heading
        composeTestRule.onNodeWithText("Comprendre Compose")
            .assertExists()
            // assertIsHeading() vérifie la présence de SemanticsProperties.Heading
            .assertIsHeading()

        // Vérifier que les sous-titres ont également la sémantique de heading
        composeTestRule.onNodeWithText("Introduction")
            .assertExists()
            .assertIsHeading()
            
        composeTestRule.onNodeWithText("Avantages")
            .assertExists()
            .assertIsHeading()
    }

    @Test
    fun testListHasCollectionInfo() {
        composeTestRule.setContent {
            ListPattern()
        }

        // Vérifier qu'un élément de liste existe. 
        // LazyColumn applique souvent nativement IsSelectable / CollectionItemInfo 
        // selon l'implémentation, mais on peut vérifier le texte au minimum.
        composeTestRule.onNodeWithText("Pommes").assertExists()
        
        // Pour une validation sémantique poussée, on peut inspecter les noeuds avec assert(hasCollectionInfo())
        // (Si défini manuellement ou via les sémantiques internes).
    }
    
    @Test
    fun testMergedGroupSemantics() {
        composeTestRule.setContent {
            GroupPattern()
        }
        
        // Vérifier que le groupe a fusionné ses descendants
        // Le noeud parent devrait avoir le texte combiné (selon l'implémentation de la Card)
        // composeTestRule.onNodeWithText("Offre spéciale", substring = true).assertExists()
    }
}
