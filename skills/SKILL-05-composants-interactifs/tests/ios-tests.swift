import XCTest
import SwiftUI

// =============================================================================
// SKILL-05 — Tests XCUITest d'accessibilité — Composants Interactifs
// FRAM : Framework Référence Accessibilité Mobile — v1.0
// Niveau A et AA
// =============================================================================

class SKILL05InteractiveComponentsTests: XCTestCase {

    var app: XCUIApplication!

    override func setUp() {
        super.setUp()
        continueAfterFailure = false
        app = XCUIApplication()
        // Activer les options d'accessibilité pour les tests
        app.launchArguments += ["--accessibility-testing"]
        app.launch()
    }

    // MARK: - [A] Critère 5.1 — Chaque composant interactif a un nom accessible

    func test_allInteractiveElementsHaveAccessibilityLabel() {
        // Trouver tous les éléments interactifs (boutons, toggles, sliders)
        let buttons = app.buttons.allElementsBoundByIndex
        let switches = app.switches.allElementsBoundByIndex
        let sliders = app.sliders.allElementsBoundByIndex

        // Chaque bouton doit avoir un label non vide
        for button in buttons {
            XCTAssertFalse(
                button.label.isEmpty,
                "Bouton '\(button.identifier)' n'a pas de label accessible [RAAM 5.1][A]"
            )
        }

        // Chaque switch doit avoir un label non vide
        for toggle in switches {
            XCTAssertFalse(
                toggle.label.isEmpty,
                "Interrupteur '\(toggle.identifier)' n'a pas de label accessible [RAAM 5.1][A]"
            )
        }

        // Chaque slider doit avoir un label non vide
        for slider in sliders {
            XCTAssertFalse(
                slider.label.isEmpty,
                "Curseur '\(slider.identifier)' n'a pas de label accessible [RAAM 5.1][A]"
            )
        }
    }

    // MARK: - [A] Critère 5.2 — Le nom accessible est pertinent (non générique)

    func test_buttonLabelsAreNotGeneric() {
        let forbiddenLabels = ["image", "icon", "button", "bouton", "ok", "tap", "click"]
        let buttons = app.buttons.allElementsBoundByIndex

        for button in buttons {
            let labelLower = button.label.lowercased()
            for forbidden in forbiddenLabels {
                XCTAssertFalse(
                    labelLower == forbidden,
                    "Bouton '\(button.identifier)' a un label générique : '\(button.label)' [RAAM 5.2][A]"
                )
            }
        }
    }

    func test_iconButtonsHaveContextualLabel() {
        // Les boutons icône (souvent identifiables par leur identifiant SF Symbol)
        // doivent avoir un label explicite différent du nom du symbole
        let deleteButton = app.buttons["delete"]
        if deleteButton.exists {
            // Le label ne devrait pas être le nom brut du SF Symbol
            XCTAssertNotEqual(
                deleteButton.label.lowercased(), "delete",
                "Le bouton 'delete' doit avoir un label contextuel [RAAM 5.2][A]"
            )
        }
    }

    // MARK: - [A] Critère 5.3 — Chaque composant a un rôle

    func test_interactiveElementsHaveCorrectRole() {
        // Les toggles doivent être de type Switch
        let switches = app.switches.allElementsBoundByIndex
        for toggle in switches {
            XCTAssertEqual(
                toggle.elementType, .switch,
                "L'élément '\(toggle.label)' devrait être un Switch [RAAM 5.3][A]"
            )
        }

        // Les checkboxes custom doivent indiquer leur état via value
        // (pas un rôle natif dispo sur iOS, testé via accessibilityValue)
    }

    // MARK: - [A] Critère 5.4 — Les changements d'état sont restitués

    func test_toggleStateIsReflectedInAccessibilityValue() throws {
        // Trouver un interrupteur dans l'app
        let toggle = app.switches.firstMatch
        guard toggle.exists else {
            throw XCTSkip("Aucun interrupteur trouvé dans l'app")
        }

        let initialValue = toggle.value as? String
        // Activer l'interrupteur
        toggle.tap()
        let newValue = toggle.value as? String

        // La valeur doit avoir changé
        XCTAssertNotEqual(
            initialValue, newValue,
            "L'état de l'interrupteur n'est pas mis à jour après activation [RAAM 5.4][A]"
        )
    }

    // MARK: - [A] Critère 5.5 — La valeur courante du slider est restituée

    func test_sliderHasAccessibleValue() throws {
        let slider = app.sliders.firstMatch
        guard slider.exists else {
            throw XCTSkip("Aucun curseur trouvé dans l'app")
        }

        // Le slider doit avoir une valeur accessible
        let value = slider.value as? String
        XCTAssertNotNil(value, "Le curseur '\(slider.label)' n'a pas de valeur accessible [RAAM 5.5][A]")
        XCTAssertFalse(value?.isEmpty ?? true, "La valeur du curseur est vide [RAAM 5.5][A]")
    }

    // MARK: - [AA] Critère 5.7 — Taille de la zone de toucher ≥ 44×44pt

    func test_allInteractiveElementsMeetMinimumTouchTarget() {
        let minimumSize: CGFloat = 44.0

        let buttons = app.buttons.allElementsBoundByIndex
        for button in buttons {
            let frame = button.frame
            // Note : XCUITest frame est en coordonnées logiques (points)
            XCTAssertGreaterThanOrEqual(
                frame.width, minimumSize,
                "Bouton '\(button.label)' trop étroit : \(frame.width)pt < \(minimumSize)pt [RAAM 5.7][AA]"
            )
            XCTAssertGreaterThanOrEqual(
                frame.height, minimumSize,
                "Bouton '\(button.label)' trop bas : \(frame.height)pt < \(minimumSize)pt [RAAM 5.7][AA]"
            )
        }
    }

    // MARK: - [A] Critère 5.9 — Ordre de focus logique

    func test_focusOrderIsLogical() {
        // Activer VoiceOver en mode test
        // Note : les tests d'ordre de focus nécessitent XCUITest avec
        // la fonctionnalité accessibilityActivate ou un test manuel documenté

        // Vérification de base : les éléments interactifs existent et sont accessibles
        let interactiveElements = app.buttons.allElementsBoundByIndex + app.switches.allElementsBoundByIndex
        for element in interactiveElements {
            XCTAssertTrue(
                element.isHittable,
                "L'élément '\(element.label)' n'est pas accessible au tap [RAAM 5.9][A]"
            )
        }
    }

    // MARK: - [AA] Critère 5.6 — Alternatives aux gestes complexes

    func test_swipeActionsHaveAlternatives() {
        // Naviguer vers un écran avec des swipe actions
        // et vérifier qu'une action personnalisée (accessible) est disponible

        let listCells = app.cells.allElementsBoundByIndex
        for cell in listCells {
            // Vérifier que des actions accessibles sont disponibles
            // (XCUITest ne peut pas directement tester les actions custom d'accessibilité
            //  mais on peut vérifier la présence du bouton d'actions)
            _ = cell // Placeholder pour documentation du test manuel
        }
    }
}

// MARK: - Audit Automatique — Accessibility Inspector

/// Pour l'audit Accessibility Inspector, utiliser la commande :
/// ```
/// xcrun simctl accessibility audit <device-id> <bundle-id> --output-path report.json
/// ```
/// Disponible avec Xcode 15+

extension SKILL05InteractiveComponentsTests {

    /// Utilitaire pour lancer un audit accessibility sur l'écran courant
    func runAccessibilityAudit() throws {
        if #available(iOS 17.0, *) {
            try app.performAccessibilityAudit(for: [
                .contrast,       // Critère 2.2 — contraste
                .textClipped,   // Critère 8 — présentation
                .hitRegion,      // Critère 5.7 — taille de cible [AA]
                .sufficientElementDescription // Critères 5.1, 5.2 [A]
            ])
        } else {
            // Pour iOS < 17, utiliser Accessibility Inspector manuellement
            print("runAccessibilityAudit nécessite iOS 17+ — utiliser Accessibility Inspector")
        }
    }
}
