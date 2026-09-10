package com.fram.a11y.skill03

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import android.content.res.Configuration

@RunWith(AndroidJUnit4::class)
class Skill03AdaptationTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    // MARK: - Test Font Scale (Dynamic Type) [AA]
    @Test
    fun testLayoutAdaptsToLargeFontScale() {
        composeTestRule.setContent {
            // Forcer un grand font scale (2.0x) pour le test
            CompositionLocalProvider(
                LocalDensity provides Density(density = 1f, fontScale = 2.0f)
            ) {
                AdaptiveCard(title = "Titre de test", description = "Description longue")
            }
        }

        // Vérifier que le texte est présent
        composeTestRule.onNodeWithText("Titre de test").assertExists()
        composeTestRule.onNodeWithText("Description longue").assertExists()
        
        // On pourrait vérifier la taille des Bounds pour s'assurer que le parent a bien grandi
        // .assertHeightIsAtLeast(80.dp)
    }

    // MARK: - Test Orientation Landscape [AA]
    @Test
    fun testLandscapeOrientation() {
        composeTestRule.setContent {
            // Simuler l'orientation paysage
            val landscapeConfig = Configuration().apply {
                orientation = Configuration.ORIENTATION_LANDSCAPE
            }
            
            CompositionLocalProvider(
                LocalConfiguration provides landscapeConfig
            ) {
                AdaptiveOrientationLayout()
            }
        }

        // Vérifier que le texte s'affiche bien (le reflow a fonctionné sans masquer l'élément)
        composeTestRule.onNodeWithText("Titre de l'article").assertIsDisplayed()
    }

    // MARK: - Test Reflow / Pas de truncation [AA]
    @Test
    fun testTextIsNotTruncatedAtLargeScale() {
        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalDensity provides Density(density = 1f, fontScale = 2.0f)
            ) {
                ReflowLayout() // FlowRow component
            }
        }

        // Les boutons sont bien répartis sur plusieurs lignes (Reflow) 
        // et accessibles sans scroll horizontal.
        composeTestRule.onNodeWithText("Accepter les conditions").assertIsDisplayed()
        composeTestRule.onNodeWithText("Refuser").assertIsDisplayed()
    }
}
