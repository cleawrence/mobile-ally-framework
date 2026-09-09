import XCTest

class FormsAccessibilityTests: XCTestCase {

    var app: XCUIApplication!

    override func setUpWithError() throws {
        continueAfterFailure = false
        app = XCUIApplication()
        app.launch()
    }

    func test_allFormFields_haveAccessibilityLabel() throws {
        // [A] Critère 9.1
        let textFields = app.textFields
        for i in 0..<textFields.count {
            let field = textFields.element(boundBy: i)
            XCTAssertTrue(field.exists)
            let label = field.label
            XCTAssertFalse(label.isEmpty, "Le champ texte n'a pas de label d'accessibilité.")
        }
        
        let secureFields = app.secureTextFields
        for i in 0..<secureFields.count {
            let field = secureFields.element(boundBy: i)
            XCTAssertTrue(field.exists)
            let label = field.label
            XCTAssertFalse(label.isEmpty, "Le champ de mot de passe n'a pas de label d'accessibilité.")
        }
    }

    func test_requiredFields_areIdentified() throws {
        // [A] Critère 9.3
        let requiredEmailField = app.textFields["Email, obligatoire"]
        XCTAssertTrue(requiredEmailField.exists, "Le champ requis n'annonce pas explicitement qu'il est obligatoire.")
    }

    func test_errorMessages_areAnnounced() throws {
        // [A] Critère 9.4
        let loginButton = app.buttons["Se connecter"]
        XCTAssertTrue(loginButton.exists)
        
        // Clic sans rien remplir pour déclencher l'erreur
        loginButton.tap()
        
        // Vérification de l'existence d'un élément statique qui sert de message d'erreur
        let errorMessage = app.staticTexts["Email ou mot de passe incorrect."]
        XCTAssertTrue(errorMessage.waitForExistence(timeout: 2.0), "Le message d'erreur textuel doit être présent après validation.")
    }

    func test_emailField_hasCorrectKeyboardType() throws {
        // [AA] Critère 9.6
        // XCUI ne permet pas d'interroger keyboardType directement.
        // On peut vérifier l'affichage du clavier "@" ou vérifier la valeur du champ qui a un type spécifique.
        let emailField = app.textFields["Adresse email, obligatoire"]
        XCTAssertTrue(emailField.exists)
        emailField.tap()
        
        // Vérifie que la touche '@' du clavier est présente, indiquant probablement un clavier Email
        XCTAssertTrue(app.keys["@"].waitForExistence(timeout: 2.0))
    }

    func test_passwordField_hasCorrectContentType() throws {
        // [AA] Critère 9.6
        let passwordField = app.secureTextFields["Mot de passe, obligatoire"]
        XCTAssertTrue(passwordField.exists, "Le champ de mot de passe n'est pas identifié correctement.")
        // Note: textContentType n'est pas testable via XCUITest, on teste l'existence du SecureTextField.
    }

    func test_formSubmission_withMissingFields_showsErrors() throws {
        // [A] Critère 9.4
        let registerButton = app.buttons["S'inscrire"]
        XCTAssertTrue(registerButton.exists)
        registerButton.tap()
        
        let errorMessage = app.staticTexts["Veuillez remplir tous les champs correctement."]
        XCTAssertTrue(errorMessage.waitForExistence(timeout: 2.0))
    }
}
