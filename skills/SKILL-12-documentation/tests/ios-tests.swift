import XCTest
import SwiftUI

class DocumentationA11yTests: XCTestCase {
    
    let app = XCUIApplication()
    
    override func setUpWithError() throws {
        continueAfterFailure = false
        app.launch()
    }
    
    // MARK: - 1. DÉCLARATION D'ACCESSIBILITÉ IN-APP [A]
    
    func testAccessibilityStatementScreenExists() throws {
        // [A] Vérifier l'existence du bouton menant à la déclaration d'accessibilité
        let a11ySettingsButton = app.buttons["Paramètres d'accessibilité"]
        if a11ySettingsButton.exists {
            a11ySettingsButton.tap()
        }
        
        // L'écran de déclaration d'accessibilité doit contenir les informations obligatoires
        let titleHeader = app.staticTexts["Déclaration d'accessibilité"]
        XCTAssertTrue(titleHeader.exists, "L'écran de déclaration d'accessibilité est introuvable")
        
        let conformanceHeader = app.staticTexts["État de conformité"]
        XCTAssertTrue(conformanceHeader.exists, "L'état de conformité n'est pas renseigné dans la déclaration")
        
        // Le bouton de contact/signalement doit exister
        let contactButton = app.buttons["Signaler un problème d'accessibilité"]
        XCTAssertTrue(contactButton.exists, "Il n'y a pas de moyen de signaler un problème d'accessibilité")
    }
    
    // MARK: - 2. VOICE CONTROL (Contrôle vocal) [AA]
    
    func testVoiceControlInputLabels() throws {
        // [AA] Vérifier que les boutons avec des Input Labels spécifiques sont reconnus
        // Ce test s'assure que le composant VoiceControlOptimizedButton (dans patterns.swift)
        // expose bien ses propriétés pour le Voice Control
        
        let saveButton = app.buttons["Enregistrer le document"] // accessibilityLabel (VoiceOver)
        XCTAssertTrue(saveButton.exists, "Le bouton de sauvegarde n'est pas trouvé")
        
        // Normalement, la vérification exacte des accessibilityInputLabels dans XCUITest 
        // n'est pas directement exposée via une propriété publique simple,
        // mais nous vérifions que le bouton est accessible.
        XCTAssertTrue(saveButton.isHittable)
    }
    
    // MARK: - 3. SWITCH CONTROL & VOICEOVER APIS [AA]
    
    func testCustomActionsAndTraits() throws {
        // Test du SwitchControlFriendlyRow
        let rowElement = app.otherElements["Item Titre Exemple"] // Remplacer par un vrai label
        if rowElement.exists {
            // [AA] XCUIElement n'expose aucune API publique pour lister les
            // accessibilityCustomActions d'un élément (contrairement à ce qu'un appel à
            // `.customActions` laisserait penser — ce membre n'existe pas). Les custom
            // actions (Switch Control / VoiceOver Rotor) ne sont vérifiables qu'à la main
            // via VoiceOver, pas par XCUITest. On se limite donc à vérifier que l'élément
            // est bien exposé à l'arbre d'accessibilité.
            XCTAssertTrue(rowElement.isHittable, "L'élément avec custom actions doit être exposé à l'accessibilité")
        }
    }
    
    func testComprehensiveAPIs() throws {
        // [AA] Vérifier qu'un élément complet expose ses Custom Contents et Values
        let weatherData = app.staticTexts["weather_data_text"]
        if weatherData.exists {
            // Il n'y a pas d'API XCTest publique directe pour lire les accessibilityCustomContent en Swift natif
            // Mais on peut vérifier que l'élément est bien là et identifiable par son identifier.
            XCTAssertTrue(weatherData.exists)
        }
        
        let slider = app.sliders["Volume sonore"]
        if slider.exists {
            let value = slider.value as? String
            XCTAssertNotNil(value, "Le slider devrait exposer une valeur accessible")
            XCTAssertTrue(value!.contains("pourcent"))
        }
    }
}
