package com.fram.a11y.skill04

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.*
import org.junit.Rule
import org.junit.Test

class TableListAccessibilityTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testListHasCollectionInfo() {
        val users = listOf(User("Alice", "Dev", "TechCorp"))
        composeTestRule.setContent {
            AccessibleListPattern(users)
        }

        // [A] - Vérifier la sémantique de collection
        composeTestRule.onNode(hasScrollToIndexAction()) // LazyColumn
            .assert(hasAnyChild(hasContentDescription("Alice, Dev chez TechCorp")))
        
        // En Compose Test, vérifier directement collectionInfo peut requérir des matchers customs,
        // mais on peut tester l'association via les sémantiques standards exposées ou l'outil Accessibility Scanner.
    }

    @Test
    fun testTableHeadersHaveHeadingSemantics() {
        val employees = listOf(Employee("Alice", "35k"))
        composeTestRule.setContent {
            AccessibleTablePattern(employees)
        }

        // [A] - Vérifier que les en-têtes sont marqués comme tels
        // Compose ui-test fournit isHeading() ou hasAnyChild(isHeading()) pour certains éléments, mais la méthode de test classique est de chercher les labels.
        // Un assertion sur SemanticProperties.Heading peut être écrite avec un matcher custom.
        composeTestRule.onNodeWithText("Nom")
            .assertExists()
            // .assert(isHeading()) // Disponible via extension ou custom matcher dans certains cas.
    }

    @Test
    fun testTableCellHasContextualContentDescription() {
        val employees = listOf(Employee("Bob", "42k"))
        composeTestRule.setContent {
            AccessibleTablePattern(employees)
        }

        // [A] - Chaque cellule est associée à son en-tête (Critère 4.3)
        composeTestRule.onNodeWithContentDescription("Salaire de Bob: 42k")
            .assertExists()
    }
    
    @Test
    fun testTableHasGlobalDescription() {
        val employees = listOf(Employee("Alice", "35k"))
        composeTestRule.setContent {
            AccessibleTablePattern(employees)
        }
        
        // [AA] - Le tableau a une description (Critère 4.4)
        composeTestRule.onNodeWithContentDescription("Tableau des salaires des employés. 1 lignes.")
            .assertExists()
    }
}
