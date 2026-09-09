package com.fram.a11y.skill08.tests

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.fram.a11y.skill08.AccessibleAutoPlayCarousel
import com.fram.a11y.skill08.AudioWithTranscriptView
import com.fram.a11y.skill08.ReduceMotionExample
import org.junit.Rule
import org.junit.Test

class SKILL08_TemporalElements_AndroidTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    // MARK: - Critère 8.5 [A] Carrousel Pause
    @Test
    fun autoPlayContent_canBePaused() {
        composeTestRule.setContent {
            AccessibleAutoPlayCarousel()
        }

        // Vérifier l'existence du bouton pause avec la description appropriée
        val pauseButton = composeTestRule.onNodeWithContentDescription("Mettre en pause le carrousel")
        pauseButton.assertExists()
        pauseButton.assertIsDisplayed()
        pauseButton.assertHasClickAction()

        // Cliquer pour mettre en pause
        pauseButton.performClick()

        // Le bouton doit maintenant s'appeler "Reprendre"
        composeTestRule.onNodeWithContentDescription("Reprendre le carrousel")
            .assertExists()
            .assertIsDisplayed()
    }

    // MARK: - Critère 8.1 [A] Transcription
    @Test
    fun transcriptButton_existsNextToAudioPlayer() {
        composeTestRule.setContent {
            AudioWithTranscriptView()
        }

        // Vérifier l'existence du bouton de transcription
        val transcriptButton = composeTestRule.onNodeWithText("Lire la transcription")
        transcriptButton.assertExists()
        
        // Vérifier l'action et le lecteur d'écran
        val transcriptNode = composeTestRule.onNodeWithContentDescription("Lire la transcription texte du podcast")
        transcriptNode.assertExists()
        transcriptNode.performClick()

        // Vérifier que la modale s'ouvre bien
        composeTestRule.onNodeWithText("Transcription").assertExists()
    }

    // MARK: - Critère 8.6 [A] Reduce Motion UI Validation
    @Test
    fun reduceMotion_uiElementsRenderCorrectly() {
        // En Compose UI Test, tester directement l'échelle des animations système (Settings.Global)
        // nécessiterait un custom CompositionLocalProvider. Ici on s'assure que le contenu s'affiche correctement.
        composeTestRule.setContent {
            ReduceMotionExample()
        }

        composeTestRule.onNodeWithText("Basculer").performClick()
        
        // L'élément apparaît correctement
        composeTestRule.onNodeWithText("Contenu Animé").assertExists()
    }
}
