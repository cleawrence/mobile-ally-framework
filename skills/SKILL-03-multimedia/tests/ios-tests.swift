import XCTest

class SKILL03_AdaptationTests: XCTestCase {
    
    var app: XCUIApplication!
    
    override func setUpWithError() throws {
        continueAfterFailure = false
        app = XCUIApplication()
        // Lancer avec la taille par défaut
        app.launchArguments = ["-UIContentSizeCategory", "UICTContentSizeCategoryL"]
        app.launch()
    }
    
    // MARK: - Test d'Orientation [AA]
    func testAppSupportsLandscapeOrientation() throws {
        // Simuler la rotation de l'appareil
        XCUIDevice.shared.orientation = .landscapeLeft
        
        // Laisser le temps à l'UI de s'adapter
        sleep(2)
        
        // Vérifier que l'élément principal est toujours accessible et à l'écran
        let mainContent = app.staticTexts["Description détaillée"]
        XCTAssertTrue(mainContent.exists)
        XCTAssertTrue(mainContent.isHittable, "Le contenu doit être accessible en mode paysage sans être masqué.")
        
        // Remettre en portrait
        XCUIDevice.shared.orientation = .portrait
    }
    
    // MARK: - Test Dynamic Type (Tailles d'accessibilité) [AA]
    func testContentAdaptsToLargeDynamicType() throws {
        // Relancer l'app avec une très grande taille de police (Accessibility XXXL)
        let largeApp = XCUIApplication()
        largeApp.launchArguments = ["-UIContentSizeCategory", "UICTContentSizeCategoryAccessibilityXXXL"]
        largeApp.launch()
        
        let titleLabel = largeApp.staticTexts["Titre adaptatif"]
        
        // Vérifier que le texte est présent
        XCTAssertTrue(titleLabel.exists)
        
        // Vérifier qu'il n'est pas tronqué par un conteneur trop petit
        // En UI tests on ne peut pas vérifier le frame exact par rapport au texte coupé, 
        // mais on peut vérifier que l'élément est fully hittable et a une grande hauteur.
        XCTAssertTrue(titleLabel.frame.height > 60, "La hauteur du label doit augmenter pour accueillir le grand texte.")
    }
    
    // MARK: - Test Reflow / Pas de défilement horizontal [AA]
    func testNoHorizontalScrollAtLargeSizes() throws {
        let largeApp = XCUIApplication()
        largeApp.launchArguments = ["-UIContentSizeCategory", "UICTContentSizeCategoryAccessibilityXXXL"]
        largeApp.launch()
        
        // XCUIElement n'expose pas `contentSize` (propriété UIScrollView, pas accessible
        // via XCUITest) : impossible de comparer directement la largeur du contenu à
        // celle du cadre visible. Heuristique de repli : un scrollView plus large que
        // l'écran lui-même indique un défilement horizontal ajouté pour accommoder le
        // texte, contraire au Reflow attendu.
        let screenWidth = largeApp.frame.width
        let horizontalScrollViews = largeApp.scrollViews.allElementsBoundByIndex.filter {
            $0.frame.width > screenWidth
        }
        
        XCTAssertTrue(horizontalScrollViews.isEmpty, "Il ne devrait pas y avoir de défilement horizontal à grande taille de texte. Utilisez le Reflow vertical.")
    }
}
