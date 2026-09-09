import SwiftUI

// MARK: - 1. TITRES / HEADINGS [AA] — Critère 7.3, 7.4
// Utiliser .accessibilityAddTraits(.isHeader) pour les éléments agissant comme titres.

struct HeadingsPatternView: View {
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                // ✅ Bon: Titre principal avec isHeader
                Text("Page de profil")
                    .font(.largeTitle)
                    .fontWeight(.bold)
                    .accessibilityAddTraits(.isHeader)
                
                // ❌ Mauvais: Titre visuel sans sémantique (VoiceOver ne le verra pas comme titre)
                Text("Informations personnelles")
                    .font(.title2)
                    .fontWeight(.semibold)
                
                // ✅ Bon: Titre de section avec isHeader
                Text("Paramètres du compte")
                    .font(.title2)
                    .fontWeight(.semibold)
                    .accessibilityAddTraits(.isHeader)
            }
            .padding()
        }
    }
}

// MARK: - 2. LISTES [A] — Critère 7.1
// Utilisation de `List` pour bénéficier de la sémantique de liste native.

struct ListPatternView: View {
    let contacts = ["Alice", "Bob", "Charlie"]
    
    var body: some View {
        // ✅ Bon: L'utilisation de List offre nativement la sémantique
        List {
            ForEach(contacts, id: \.self) { contact in
                Text(contact)
                    // Optionnel mais recommandé si la cellule est complexe
                    .accessibilityElement(children: .combine)
            }
        }
        
        // ❌ Mauvais: VStack n'apporte pas de sémantique de liste,
        // l'utilisateur ne saura pas combien il y a d'éléments.
        /*
        VStack {
            ForEach(contacts, id: \.self) { contact in
                Text(contact)
            }
        }
        */
    }
}

// MARK: - 3. GROUPES ET SECTIONS
// Utilisation de sections pour grouper logiquement l'information.

struct SectionGroupPatternView: View {
    var body: some View {
        Form {
            // ✅ Bon: Section dans un Form donne une structure annoncée
            Section(header: Text("Général").accessibilityAddTraits(.isHeader)) {
                Toggle("Notifications", isOn: .constant(true))
                Toggle("Sons", isOn: .constant(false))
            }
            
            // ✅ Bon: DisclosureGroup est sémantiquement un groupe extensible
            DisclosureGroup("Options avancées") {
                Text("Option 1")
                Text("Option 2")
            }
        }
    }
}

// MARK: - 4. & 5. EXEMPLE COMPLET — Article avec hiérarchie
// Utilisation du Rotor VoiceOver pour naviguer entre les isHeader

struct ArticleStructureView: View {
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                // H1: Titre de l'article
                Text("Comprendre l'Accessibilité")
                    .font(.largeTitle)
                    .accessibilityAddTraits(.isHeader)
                
                // H2: Introduction
                Text("Introduction")
                    .font(.title)
                    .accessibilityAddTraits(.isHeader)
                
                Text("L'accessibilité numérique consiste à rendre les services en ligne accessibles aux personnes en situation de handicap.")
                
                // H2: Développement
                Text("Principes fondamentaux")
                    .font(.title)
                    .accessibilityAddTraits(.isHeader)
                
                // H3: Sous-section (Perceptible)
                Text("1. Perceptible")
                    .font(.headline)
                    .accessibilityAddTraits(.isHeader)
                
                Text("L'information doit être présentée de manière à pouvoir être perçue.")
                
                // Liste sémantique d'exemples
                VStack(alignment: .leading, spacing: 8) {
                    Text("Exemples :").font(.subheadline)
                    ForEach(["Contraste suffisant", "Textes alternatifs"], id: \.self) { item in
                        HStack {
                            Image(systemName: "circle.fill").font(.system(size: 6))
                            Text(item)
                        }
                        // Grouper la puce et le texte
                        .accessibilityElement(children: .combine)
                    }
                }
                // Optionnel: On peut déclarer explicitement un conteneur si on n'utilise pas List
                .accessibilityElement(children: .contain)
                .accessibilityLabel("Liste de 2 exemples")
                
            }
            .padding()
        }
    }
}
