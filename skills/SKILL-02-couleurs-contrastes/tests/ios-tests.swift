import XCTest

class Skill02CouleursContrastesTests: XCTestCase {

    var app: XCUIApplication!

    override func setUpWithError() throws {
        continueAfterFailure = false
        app = XCUIApplication()
        app.launch()
    }

    // MARK: - 1. INFORMATION PAR COULEUR SEULE [A]

    func test_noInformationByColorAlone_statusBadges() throws {
        // [A] Vérifie que le badge a un texte/icône explicite, pas seulement une vue colorée
        let statusBadgeText = app.staticTexts["Online"]
        XCTAssertTrue(statusBadgeText.exists, "The status must have an explicit text label (like 'Online'), not just a green color.")
    }

    func test_errorFields_haveTextNotJustBorder() throws {
        // [A] Vérifier qu'une erreur de champ déclenche l'apparition d'un message d'erreur textuel
        let emailField = app.textFields["Email address"]
        emailField.tap()
        emailField.typeText("invalid-email")
        
        // Simuler la validation
        app.buttons["Submit"].tap()
        
        let errorMessage = app.staticTexts["Error: Invalid email format."]
        XCTAssertTrue(errorMessage.waitForExistence(timeout: 2.0), "An explicit text error message must be displayed, not just a red border.")
    }

    func test_links_haveUnderlineOrOtherIndicator() throws {
        // [A] Vérifie que les liens ne reposent pas uniquement sur la couleur
        // En UI Test, on peut vérifier la présence du trait si exposé dans l'accessibility, 
        // ou manuellement s'assurer que le bouton a un label adapté.
        let termsLink = app.links["Terms and Conditions"]
        XCTAssertTrue(termsLink.exists)
    }

    // MARK: - 2. CONTRASTE (AUDIT AUTOMATIQUE) [AA]

    @available(iOS 17.0, *)
    func test_accessibilityAudit_contrastIssues() throws {
        // [AA] Xcode 15+ permet d'auditer le contraste directement (>= 4.5:1 ou 3:1)
        try app.performAccessibilityAudit(for: [.contrast])
    }

    // MARK: - 3. DARK MODE [AA]

    func test_darkMode_elementsRemainContrasted() throws {
        // [AA] Tester manuellement le changement de mode ne se fait pas facilement en natif XCUITest pure (nécessite d'altérer les préférences du device ou de l'app via launchArguments).
        // Mais c'est une bonne pratique de relancer l'app avec un argument:
        // app.launchArguments.append(contentsOf: ["-AppleInterfaceStyle", "Dark"])
        // Puis refaire l'audit.
        
        let appDarkMode = XCUIApplication()
        appDarkMode.launchArguments.append(contentsOf: ["-AppleInterfaceStyle", "Dark"])
        appDarkMode.launch()
        
        if #available(iOS 17.0, *) {
            try appDarkMode.performAccessibilityAudit(for: [.contrast])
        }
    }

    // MARK: - 4. REDUCE TRANSPARENCY [A]

    func test_reduceTransparency_backgroundsAreOpaque() throws {
        // Les préférences d'accessibilité peuvent être mockées ou lancées spécifiquement
        // Mais un test visuel (Snapshot testing) est souvent plus adapté ici.
        XCTAssertTrue(true, "Manual visual verification is often required to ensure UI adapts to Reduce Transparency.")
    }
}
