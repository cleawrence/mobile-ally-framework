import SwiftUI

struct AccessibleListPattern: View {
    let users = [
        ("Alice", "Developpeuse", "TechCorp"),
        ("Bob", "Designer", "CreativStudio")
    ]
    
    var body: some View {
        // MARK: - 1. LISTE ACCESSIBLE [A]
        // Utilisation de sémantique de liste native avec List
        List(users, id: \.0) { user in
            VStack(alignment: .leading) {
                Text(user.0).font(.headline)
                HStack {
                    Text(user.1)
                    Text("-")
                    Text(user.2)
                }
                .font(.subheadline)
            }
            // Regroupement sémantique complet pour éviter les pauses du lecteur d'écran
            .accessibilityElement(children: .combine)
            // Optionnel mais recommandé: surcharger le label si combine n'est pas optimal
            .accessibilityLabel("\(user.0), \(user.1) chez \(user.2)")
        }
    }
}

struct AccessibleTablePattern: View {
    struct Employee: Identifiable {
        let id = UUID()
        let name: String
        let salary: String
    }
    
    let employees = [
        Employee(name: "Alice", salary: "35 000 €"),
        Employee(name: "Bob", salary: "42 000 €")
    ]
    
    var body: some View {
        VStack {
            // MARK: - 3. TABLEAU AVEC DESCRIPTION [AA]
            Text("Tableau des salaires")
                .font(.title)
                .accessibilityAddTraits(.isHeader) // Marquer comme titre global
            
            // MARK: - 2. TABLEAU DE DONNÉES [A]
            // Pattern mobile : simulé car pas de Table native iOS pour de petites largeurs
            Grid {
                GridRow {
                    Text("Nom")
                        .bold()
                        .accessibilityAddTraits(.isHeader)
                    
                    Text("Salaire annuel")
                        .bold()
                        .accessibilityAddTraits(.isHeader)
                }
                
                ForEach(employees) { emp in
                    GridRow {
                        Text(emp.name)
                            // La cellule rappelle son en-tête de colonne et de ligne (elle-même)
                            .accessibilityLabel("Nom: \(emp.name)")
                        
                        Text(emp.salary)
                            // On injecte le contexte de la ligne (Nom) et de la colonne
                            .accessibilityLabel("Salaire annuel pour \(emp.name) : \(emp.salary)")
                    }
                }
            }
            .accessibilityElement(children: .contain)
            .accessibilityLabel("Salaires des employés, contenant 2 colonnes et 2 lignes de données.")
        }
    }
}

struct GroupedListPattern: View {
    var body: some View {
        // MARK: - 4. LISTE GROUPEE [A]
        List {
            Section(header: Text("Ressources Humaines")
                        .accessibilityAddTraits(.isHeader)) {
                Text("Alice")
                Text("Bob")
            }
            
            Section(header: Text("Technique")
                        .accessibilityAddTraits(.isHeader)) {
                Text("Charlie")
            }
        }
    }
}

struct ComplexTableAlternative: View {
    var body: some View {
        // MARK: - 5. ALTERNATIVE ACCESSIBLE POUR TABLEAU COMPLEXE [A]
        VStack {
            // Affichage du tableau complexe visuellement (potentiellement difficile au lecteur d'écran)
            ScrollView(.horizontal) {
                // Table implementation...
                Text("Tableau très large...")
            }
            // Ce tableau doit être masqué au lecteur d'écran si l'alternative est meilleure
            .accessibilityHidden(true)
            
            // Alternative propre
            NavigationLink(destination: AccessibleListPattern()) {
                Text("Voir les données en liste (Accessible)")
            }
            .accessibilityHint("Ouvre une vue présentant les données du tableau sous forme de liste séquentielle.")
        }
    }
}
