import XCTest

class ConsultationAccessibilityTests: XCTestCase {
    
    var app: XCUIApplication!

    override func setUpWithError() throws {
        continueAfterFailure = false
        app = XCUIApplication()
        app.launch()
    }

    // MARK: - 1. ZONE DE TOUCHE [AA] — Critère 11.5
    func testTouchTargetSizeIsSufficient() throws {
        // Vérifie qu'un bouton a bien une taille minimum de 44x44pt
        let favoriteButton = app.buttons["Mettre en favori"]
        XCTAssertTrue(favoriteButton.waitForExistence(timeout: 2))
        
        let frame = favoriteButton.frame
        XCTAssertTrue(frame.width >= 44.0, "La largeur de la zone de touche doit être >= 44pt (actuelle: \(frame.width))")
        XCTAssertTrue(frame.height >= 44.0, "La hauteur de la zone de touche doit être >= 44pt (actuelle: \(frame.height))")
    }

    // MARK: - 2. TIMEOUT [A] — Critère 11.1
    func testSessionTimeoutDialogAppearsAndCanBeExtended() throws {
        // Déclencher le timeout
        let simulateButton = app.buttons["Simuler expiration de session"]
        if simulateButton.exists {
            simulateButton.tap()
            
            // Vérifier l'apparition de l'alerte
            let alert = app.alerts["Expiration de session imminente"]
            XCTAssertTrue(alert.waitForExistence(timeout: 2), "L'alerte de timeout de session n'est pas apparue")
            
            // Vérifier la présence d'un bouton pour prolonger
            let extendButton = alert.buttons["Prolonger la session"]
            XCTAssertTrue(extendButton.exists, "Il manque le bouton permettant de prolonger la session")
            extendButton.tap()
        }
    }

    // MARK: - 3. ERREURS RÉCUPÉRABLES [AA] — Critère 11.6
    func testDestructiveActionRequiresConfirmation() throws {
        // Appuyer sur un bouton de suppression de compte
        let deleteButton = app.buttons["Supprimer le compte"]
        if deleteButton.exists {
            deleteButton.tap()
            
            // Vérifier qu'une modale/feuille d'action de confirmation s'affiche
            let confirmationSheet = app.sheets.firstMatch
            XCTAssertTrue(confirmationSheet.waitForExistence(timeout: 2), "Aucune confirmation n'a été demandée avant la suppression")
            
            // Vérifier que l'annulation est possible
            let cancelButton = confirmationSheet.buttons["Annuler"]
            XCTAssertTrue(cancelButton.exists, "Impossible d'annuler l'action destructive")
            cancelButton.tap()
        }
    }
}
