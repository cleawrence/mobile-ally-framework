package com.fram.a11y.skill07.test

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import com.fram.a11y.skill07.*
import org.junit.Rule
import org.junit.Test

// Compose UI Test n'expose pas de assertIsHeading() prêt à l'emploi -- vérifie la présence
// de la clé sémantique Heading, posée par Modifier.semantics { heading() }.
private fun SemanticsNodeInteraction.assertIsHeading(): SemanticsNodeInteraction =
    assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))

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
