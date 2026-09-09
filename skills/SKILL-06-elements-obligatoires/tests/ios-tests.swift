import XCTest

class MandatoryElementsTests: XCTestCase {

    let app = XCUIApplication()

    override func setUpWithError() throws {
        continueAfterFailure = false
        app.launch()
    }

    // MARK: - TITRE D'ÉCRAN [A] — Critères 6.3, 6.4
    func testScreenHasRelevantAndUniqueTitle() throws {
        // Le titre de la navigation barre agit comme titre d'écran
        let navBar = app.navigationBars.firstMatch
        XCTAssertTrue(navBar.exists, "Une barre de navigation doit exister pour fournir un titre à l'écran")
        
        let titleText = navBar.staticTexts.firstMatch
        XCTAssertTrue(titleText.exists, "La barre de navigation doit avoir un titre textuel")
        XCTAssertFalse(titleText.label.isEmpty, "Le titre de l'écran ne doit pas être vide")
        
        // Vérifier que ce n'est pas un titre générique (facultatif mais recommandé pour l'unicité/pertinence)
        XCTAssertNotEqual(titleText.label, "Écran", "Le titre doit être pertinent et descriptif")
        XCTAssertNotEqual(titleText.label, "Détails", "Le titre doit être spécifique au contenu")
    }

    // MARK: - CHANGEMENT DE LANGUE [A] — Critère 6.2
    // XCTest ne permet pas toujours de vérifier l'attribut `accessibilityLanguage` d'un élément directement.
    // L'inspection manuelle via l'Inspecteur d'Accessibilité est souvent nécessaire.
    func testForeignLanguageElementExists() throws {
        // Vérifie que l'élément contenant du texte étranger est accessible
        // Ce test s'assure juste de son existence, l'audit de la langue se fait manuellement ou par des tests unitaires sur les vues.
        let foreignText = app.staticTexts["To be, or not to be, that is the question."]
        XCTAssertTrue(foreignText.exists, "Le texte en langue étrangère doit être présent")
    }
}
