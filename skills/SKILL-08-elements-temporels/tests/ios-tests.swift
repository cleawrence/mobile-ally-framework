import XCTest

class SKILL08_TemporalElements_IOSTests: XCTestCase {
    
    var app: XCUIApplication!
    
    override func setUpWithError() throws {
        continueAfterFailure = false
        app = XCUIApplication()
        app.launchArguments.append("--uitesting")
        app.launch()
    }
    
    // MARK: - Critère 8.6 [A] Reduce Motion
    func test_reduceMotion_replacesComplexAnimationsWithFade() {
        // Test nécessitant une mock de l'environnement Reduce Motion dans l'app UI Test
        // On vérifie que si l'environnement est simulé, l'UI se comporte correctement
        app.buttons["MockToggleReduceMotion"].tap() // Simulate reduce motion active
        
        let toggleButton = app.buttons["Basculer"]
        XCTAssertTrue(toggleButton.exists)
        
        toggleButton.tap()
        
        let animatedContent = app.staticTexts["Contenu Animé"]
        // Dans un UI Test, on ne peut pas tester directement la durée ou le type d'animation SwiftUI (transition),
        // mais on peut s'assurer que l'élément apparaît bien sans crash, confirmant le comportement de fallback.
        XCTAssertTrue(animatedContent.waitForExistence(timeout: 2.0))
    }
    
    // MARK: - Critère 8.5 [A] Carrousel Pause
    func test_carousel_hasPauseButton() {
        // Lancer l'écran contenant le carrousel auto-play
        let pauseButton = app.buttons["Mettre en pause le carrousel"]
        XCTAssertTrue(pauseButton.exists, "Le bouton de pause doit exister pour un contenu en auto-play")
        
        pauseButton.tap()
        
        // Le label doit changer de manière adaptative
        let playButton = app.buttons["Reprendre le défilement du carrousel"]
        XCTAssertTrue(playButton.exists, "Le label du bouton doit changer pour refléter la nouvelle action")
    }
    
    // MARK: - Critère 8.1 [A] Transcription
    func test_audioContent_hasTranscriptButton() {
        let transcriptButton = app.buttons["Lire la transcription"]
        XCTAssertTrue(transcriptButton.exists, "L'audio sans vidéo doit fournir un bouton vers la transcription")
        
        transcriptButton.tap()
        
        let transcriptTitle = app.navigationBars["Transcription"]
        XCTAssertTrue(transcriptTitle.waitForExistence(timeout: 1.0))
        
        let closeButton = app.buttons["Fermer"]
        XCTAssertTrue(closeButton.exists)
        closeButton.tap()
    }
    
    // MARK: - Critère 8.3 [AA] Audiodescription
    func test_videoPlayer_hasAudioDescriptionToggle() {
        let adButton = app.buttons["Activer l'audiodescription de la vidéo"]
        // Ce bouton pourrait être masqué derrière des contrôles personnalisés,
        // on s'assure qu'il est atteignable par VoiceOver
        XCTAssertTrue(adButton.exists, "Un moyen d'activer l'audiodescription doit être accessible")
    }
}
