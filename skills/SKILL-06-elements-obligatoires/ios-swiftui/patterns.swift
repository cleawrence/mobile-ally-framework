import SwiftUI

// MARK: - 1. LANGUE PRINCIPALE [A] — Critère 6.1
// La langue principale est gérée dans Info.plist (CFBundleDevelopmentRegion = fr)
// Mais on peut forcer une locale pour un environnement spécifique
struct MainLanguagePattern: View {
    var body: some View {
        Text("Bonjour tout le monde")
            // Définit la locale pour cet arbre de vues, bien que généralement géré par le système
            .environment(\.locale, Locale(identifier: "fr-FR"))
    }
}

// MARK: - 2. CHANGEMENT DE LANGUE [A] — Critère 6.2
// Indiquer les changements de langue pour que VoiceOver prononce correctement
struct LanguageChangePattern: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            // ✅ Bon: Langue par défaut (Français)
            Text("Voici une citation célèbre :")
            
            // ✅ Bon: Indication de la langue anglaise
            // Sans cela, VoiceOver lira "To be, or not to be" avec l'accent français (incompréhensible)
            Text("To be, or not to be, that is the question.")
                .accessibilityLanguage("en-US")
            
            // ✅ Bon: Terme technique anglais dans un texte français
            Text("Pour valider, cliquez sur le bouton ") + 
            Text("Submit").accessibilityLanguage("en-US") + 
            Text(" en bas de page.")
            
            // ❌ Mauvais: L'anglais sera lu par la voix française
            Text("Hello World, how are you?")
        }
    }
}

// MARK: - 3. TITRE D'ÉCRAN PERTINENT ET UNIQUE [A] — Critères 6.3, 6.4
// Utiliser .navigationTitle pour donner un contexte à l'écran
struct ScreenTitlePattern: View {
    var body: some View {
        NavigationStack {
            List {
                NavigationLink("Voir les détails", destination: DetailView())
            }
            // ✅ Bon: Titre descriptif et unique pour cet écran
            .navigationTitle("Liste des utilisateurs")
        }
    }
}

struct DetailView: View {
    var body: some View {
        Text("Détails de l'utilisateur")
            // ✅ Bon: Titre unique et pertinent, différent de l'écran précédent
            .navigationTitle("Profil de Jean Dupont")
            // ❌ Mauvais: .navigationTitle("Détails") - trop générique si utilisé partout
    }
}

// MARK: - 4. ANNONCE DE CHANGEMENT D'ÉCRAN [A]
// Utile lors de changements de contexte qui ne sont pas des navigations standard
struct ScreenChangeAnnouncementPattern: View {
    @State private var step = 1
    
    var body: some View {
        VStack {
            if step == 1 {
                Text("Étape 1 : Informations personnelles")
            } else {
                Text("Étape 2 : Paiement")
            }
            
            Button("Suivant") {
                step = 2
                // ✅ Bon: Annoncer le changement de contexte manuellement si la structure de navigation est custom
                UIAccessibility.post(notification: .screenChanged, argument: "Étape 2 : Paiement")
            }
        }
    }
}

// MARK: - 5. EXEMPLE COMPLET — App bilingue FR/EN
struct BilingualAppExample: View {
    var body: some View {
        NavigationStack {
            VStack(alignment: .leading, spacing: 20) {
                Text("Bienvenue dans notre application de réservation.")
                
                VStack(alignment: .leading, spacing: 8) {
                    Text("Tarifs internationaux :")
                        .font(.headline)
                    
                    HStack {
                        Text("Flight to London:")
                            .accessibilityLanguage("en-GB")
                        Spacer()
                        Text("£250")
                            .accessibilityLanguage("en-GB")
                    }
                    
                    HStack {
                        Text("Vol pour Paris :")
                        Spacer()
                        Text("150€")
                    }
                }
                .padding()
                .background(Color.gray.opacity(0.1))
                .cornerRadius(8)
            }
            .padding()
            .navigationTitle("Réservations")
        }
    }
}
