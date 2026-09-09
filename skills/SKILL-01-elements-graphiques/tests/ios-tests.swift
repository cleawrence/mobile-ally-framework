import XCTest

class SKILL01_GraphicElementsTests: XCTestCase {

    var app: XCUIApplication!

    override func setUpWithError() throws {
        continueAfterFailure = false
        app = XCUIApplication()
        app.launch()
    }

    // [A] 1.1: Decorative elements ignored
    func test_decorativeImages_areHiddenFromAccessibility() throws {
        // Appeler la vue contenant les éléments décoratifs
        // Vérifier que l'icône étoile sans sémantique n'est pas exposée
        let starIcon = app.images["star"] // Should not exist if accessibilityHidden(true)
        XCTAssertFalse(starIcon.exists, "Les images décoratives doivent avoir accessibilityHidden(true)")
    }

    // [A] 1.2: Informative images have alternative
    func test_informativeImages_haveNonEmptyLabel() throws {
        let warningIcon = app.images["Avertissement : connexion réseau instable"]
        XCTAssertTrue(warningIcon.exists, "L'image informative doit avoir un label descriptif.")
    }

    // [A] 1.3: Labels do not start with redundant prefixes
    func test_imageLabels_doNotStartWithRedundantPrefix() throws {
        let allImages = app.images.allElementsBoundByIndex
        for image in allImages {
            if let label = image.label as String? {
                let lowercased = label.lowercased()
                XCTAssertFalse(lowercased.hasPrefix("image de"), "Le label ne doit pas commencer par 'image de'")
                XCTAssertFalse(lowercased.hasPrefix("icône de"), "Le label ne doit pas commencer par 'icône de'")
                XCTAssertFalse(lowercased.hasPrefix("photo de"), "Le label ne doit pas commencer par 'photo de'")
            }
        }
    }

    // [AA] 1.9: Captioned images are merged
    func test_captionedImages_readLabelAndCaption() throws {
        let profileCaption = app.staticTexts["Jean Dupont, Directeur Technique"]
        // The caption should exist as a combined element, not separate from image visually
        XCTAssertTrue(profileCaption.exists, "La légende de l'image doit être lue (groupée avec l'image).")
    }

    // [AA] 1.8: No image text
    func test_noImageTextExists() throws {
        // En XCUITest on s'assure que le composant de texte natif est présent
        let specialOfferText = app.staticTexts["Offre Spéciale"]
        XCTAssertTrue(specialOfferText.exists, "Le texte stylisé doit être un Text natif et non une Image.")
    }

    // [A] 1.6: Complex charts have detailed description
    func test_complexCharts_haveDetailedDescription() throws {
        let chart = app.images["Évolution des ventes en 2026"]
        XCTAssertTrue(chart.exists, "Le graphique doit avoir un label récapitulatif")
        
        let hint = chart.value(forKey: "help") as? String // AccessibilityHint
        XCTAssertNotNil(hint, "Le graphique doit avoir un hint ou une description détaillée attachée")
        
        let detailsButton = app.buttons["Voir les données détaillées du graphique"]
        XCTAssertTrue(detailsButton.exists, "Un bouton d'alternative détaillé doit être présent pour les graphiques complexes.")
    }
}
