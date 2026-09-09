import XCTest

class TableListAccessibilityTests: XCTestCase {
    
    let app = XCUIApplication()
    
    override func setUp() {
        super.setUp()
        app.launch()
    }
    
    func testListItemsHaveCombinedLabels() {
        // [A] - Vérifier que les éléments de la liste combinent bien leurs contenus
        // En supposant que le texte est de la forme "Alice, Developpeuse chez TechCorp"
        let listItem = app.staticTexts["Alice, Developpeuse chez TechCorp"]
        XCTAssertTrue(listItem.exists, "L'élément de liste avec label combiné n'existe pas.")
    }
    
    func testTableHeadersAreIdentified() {
        // [A] - Critère 4.2: En-têtes définis
        let nameHeader = app.staticTexts["Nom"]
        XCTAssertTrue(nameHeader.exists)
        
        // XCUITest ne permet pas facilement de vérifier directement le trait .isHeader via l'API publique standard sans appels bas-niveau,
        // mais on peut s'assurer de l'existence des éléments sémantiques ajoutés (labels précis).
    }
    
    func testTableCellsHaveContextualLabels() {
        // [A] - Critère 4.3: Chaque cellule est associée à ses en-têtes
        // En fonction du label injecté: "Salaire annuel pour Alice : 35 000 €"
        let aliceSalaryCell = app.staticTexts["Salaire annuel pour Alice : 35 000 €"]
        XCTAssertTrue(aliceSalaryCell.exists, "La cellule ne lit pas son en-tête contextuel.")
    }
    
    func testTableHasGlobalDescription() {
        // [AA] - Critère 4.4: Le tableau a une description
        let tableContainer = app.otherElements["Salaires des employés, contenant 2 colonnes et 2 lignes de données."]
        XCTAssertTrue(tableContainer.exists, "La description globale du tableau n'est pas exposée.")
    }
}
