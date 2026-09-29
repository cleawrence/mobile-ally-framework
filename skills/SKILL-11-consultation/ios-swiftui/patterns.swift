import SwiftUI

// MARK: - 1. ZONE DE TOUCHE [AA] — Critère 11.5
// Minimum 44×44 points (recommandation Apple HIG)
// ❌ Mauvais: icône 20×20pt sans zone de touche élargie
// ✅ Bon: .frame(minWidth: 44, minHeight: 44) avec .contentShape(Rectangle())

struct TouchTargetPattern: View {
    var body: some View {
        HStack(spacing: 20) {
            // Inaccessible : Zone de touche trop petite
            Button(action: { print("Action") }) {
                Image(systemName: "star")
                    .resizable()
                    .frame(width: 20, height: 20)
            }
            .accessibilityLabel("Mettre en favori (Trop petit)")
            
            // Accessible : Zone de touche étendue sans altérer le visuel
            Button(action: { print("Action") }) {
                Image(systemName: "star")
                    .resizable()
                    .frame(width: 20, height: 20)
            }
            .frame(minWidth: 44, minHeight: 44)
            .contentShape(Rectangle()) // Rend la zone de padding cliquable
            .accessibilityLabel("Mettre en favori")
        }
    }
}

// MARK: - 2. TIMEOUT [A] — Critère 11.1
// Timer de session: avertir avant expiration avec option de prolonger

struct SessionTimeoutPattern: View {
    @State private var isTimeoutWarningVisible = false
    @State private var timeRemaining = 120
    
    var body: some View {
        VStack {
            Text("Espace sécurisé")
            Button("Simuler expiration de session") {
                isTimeoutWarningVisible = true
                UIAccessibility.post(notification: .announcement, argument: "Votre session expire dans 2 minutes. Voulez-vous la prolonger ?")
            }
        }
        .alert(isPresented: $isTimeoutWarningVisible) {
            Alert(
                title: Text("Expiration de session imminente"),
                message: Text("Pour des raisons de sécurité, votre session expirera dans \(timeRemaining) secondes."),
                primaryButton: .default(Text("Prolonger la session")) {
                    resetSession()
                },
                secondaryButton: .cancel(Text("Me déconnecter")) {
                    logout()
                }
            )
        }
    }
    
    private func resetSession() {
        timeRemaining = 300 // Reset timer
    }
    
    private func logout() {}
}

// MARK: - 3. ERREURS RÉCUPÉRABLES [AA] — Critère 11.6
// Confirmation avant suppression irréversible via .confirmationDialog

struct RecoverableErrorPattern: View {
    @State private var isDeleteConfirmationPresented = false
    
    var body: some View {
        Button(role: .destructive) {
            isDeleteConfirmationPresented = true
        } label: {
            Text("Supprimer le compte")
                .frame(minWidth: 44, minHeight: 44) // Zone de touche
        }
        .confirmationDialog(
            "Êtes-vous sûr de vouloir supprimer définitivement votre compte ? Cette action est irréversible.",
            isPresented: $isDeleteConfirmationPresented,
            titleVisibility: .visible
        ) {
            Button("Supprimer", role: .destructive) {
                deleteAccount()
            }
            Button("Annuler", role: .cancel) {
                // Annulation, pas d'action
            }
        }
    }
    
    private func deleteAccount() {
        // Logique de suppression
    }
}

// MARK: - 4. ERREURS SAISIE RÉCUPÉRABLES [AA]
// Ne pas vider les champs valides après une erreur

struct InputRetentionPattern: View {
    @State private var username: String = ""
    @State private var email: String = ""
    @State private var errorMessage: String? = nil
    
    var body: some View {
        Form {
            Section(header: Text("Informations de profil")) {
                TextField("Nom d'utilisateur", text: $username)
                    .textContentType(.username)
                    .autocapitalization(.none)
                
                TextField("Email", text: $email)
                    .keyboardType(.emailAddress)
                    .textContentType(.emailAddress)
                    .autocapitalization(.none)
                
                if let errorMessage = errorMessage {
                    Text(errorMessage)
                        .foregroundColor(.red)
                        .accessibilityLabel("Erreur : \(errorMessage)")
                }
                
                Button("Sauvegarder") {
                    validateForm()
                }
                .frame(minHeight: 44) // Accessibilité zone de touche
            }
        }
    }
    
    private func validateForm() {
        if !email.contains("@") {
            // L'erreur est affichée, mais `username` et le début d'`email` ne sont PAS effacés
            errorMessage = "Veuillez entrer une adresse email valide."
            UIAccessibility.post(notification: .announcement, argument: errorMessage)
        } else {
            errorMessage = nil
        }
    }
}

// MARK: - 5. MOUVEMENTS DE GLISSEMENT [2.2 · AA] — WCAG 2.5.7 (extension, hors RAAM 1.1)

/// Toute action qui se fait en faisant glisser (réordonner, déplacer) doit pouvoir se faire
/// avec un simple appui, sans glisser : Switch Control, Voice Control et de nombreux utilisateurs
/// à mobilité réduite ne peuvent pas maintenir un glissement.
struct DraggingAlternativePattern: View {
    @State private var meals = ["Petit-déjeuner", "Déjeuner", "Dîner"]

    var body: some View {
        NavigationStack {
            List {
                ForEach(meals, id: \.self) { meal in
                    Text(meal)
                        // ✅ Bon — alternatives sans glissement pour VoiceOver / Switch Control / Voice Control
                        .accessibilityAction(named: "Monter") { move(meal, by: -1) }
                        .accessibilityAction(named: "Descendre") { move(meal, by: 1) }
                        .accessibilityHint("Menu d'actions : monter ou descendre")
                        // ✅ Bon — alternative visible à un seul appui (appui long, sans glisser)
                        .contextMenu {
                            Button("Monter") { move(meal, by: -1) }
                                .accessibilityLabel("Monter \(meal)")
                            Button("Descendre") { move(meal, by: 1) }
                                .accessibilityLabel("Descendre \(meal)")
                        }
                }
                // Glisser reste disponible, mais n'est plus le seul moyen
                .onMove { meals.move(fromOffsets: $0, toOffset: $1) }
            }
            .navigationTitle("Menu de la journée")
        }
    }

    private func move(_ meal: String, by offset: Int) {
        guard let index = meals.firstIndex(of: meal) else { return }
        let target = index + offset
        guard meals.indices.contains(target) else { return }
        meals.swapAt(index, target)
    }
}

/// ❌ Mauvais — réordonnement possible uniquement par glissement : VIOLATION [2.2 · AA] 2.5.7
struct DragOnlyReorderPattern: View {
    @State private var meals = ["Petit-déjeuner", "Déjeuner", "Dîner"]

    var body: some View {
        NavigationStack {
            List {
                ForEach(meals, id: \.self) { meal in Text(meal) }
                    .onMove { meals.move(fromOffsets: $0, toOffset: $1) }
            }
            .navigationTitle("Menu de la journée")
        }
    }
}
