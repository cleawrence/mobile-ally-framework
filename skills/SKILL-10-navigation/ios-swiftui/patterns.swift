import SwiftUI

// MARK: - 1. ORDRE DE FOCUS [A] — Critère 10.2

/// Pattern: Forcer l'ordre de focus pour que les informations importantes soient lues en premier
struct FocusOrderCardPattern: View {
    var body: some View {
        VStack(alignment: .leading) {
            // Le titre sera lu en premier grâce à accessibilitySortPriority(1)
            Text("Titre de la carte")
                .font(.headline)
                .accessibilitySortPriority(1)
            
            Text("Description détaillée du contenu de la carte...")
                .font(.subheadline)
            
            Button("Action principale") {
                // Action
            }
        }
        .padding()
        // Grouper les éléments n'est pas toujours nécessaire avec accessibilitySortPriority,
        // mais pour une carte entière, cela peut parfois être pertinent selon l'UX souhaitée.
    }
}

/// Pattern: Formulaire avec message d'erreur lu avant le champ
struct FocusOrderFormPattern: View {
    @State private var email: String = ""
    @State private var hasError: Bool = true
    
    var body: some View {
        VStack(alignment: .leading) {
            Text("Email")
            if hasError {
                Text("Format d'email invalide")
                    .foregroundColor(.red)
                    .accessibilitySortPriority(2) // L'erreur est lue en priorité
            }
            TextField("Entrez votre email", text: $email)
                .accessibilitySortPriority(1)
        }
    }
}

// MARK: - 2. POSITION DANS L'APP [A] — Critère 10.1

/// Pattern: Informer de la position via le titre de navigation
struct AppPositionPattern: View {
    var body: some View {
        NavigationStack {
            List {
                Text("Élément 1")
            }
            // Définit le titre de la page pour VoiceOver
            .navigationTitle("Accueil")
        }
        .onAppear {
            // Annonce explicite d'un changement d'écran si non géré nativement
            UIAccessibility.post(notification: .screenChanged, argument: "Écran d'accueil")
        }
    }
}

// MARK: - 3. FOCUS VISIBLE [AA] — Critère 10.5

/// Pattern: Gérer le focus visible au clavier
struct VisibleFocusPattern: View {
    @FocusState private var isFocused: Bool
    
    var body: some View {
        Button(action: {
            // Action
        }) {
            Text("Bouton interactif")
                .padding()
                .background(isFocused ? Color.blue : Color.gray)
                .foregroundColor(.white)
        }
        .focusable() // Rend l'élément focusable au clavier matériel
        .focused($isFocused) // Lie l'état de focus
        // Le système ajoute automatiquement un anneau de focus (Focus Ring) sur macOS/iPadOS
    }
}

// MARK: - 4. FOCUS NON PIÉGÉ [AA] — Critère 10.6

/// Pattern: Modal accessible qui ne piège pas le focus et masque l'arrière-plan
struct NonTrappedFocusPattern: View {
    @State private var showModal = false
    
    var body: some View {
        ZStack {
            Button("Ouvrir la modale") {
                showModal = true
            }
            .accessibilityHidden(showModal) // Masque l'arrière-plan pour VoiceOver
            
            if showModal {
                Color.black.opacity(0.5)
                    .ignoresSafeArea()
                
                VStack {
                    Text("Contenu de la modale")
                        .accessibilityAddTraits(.isHeader)
                    
                    Button("Fermer") {
                        showModal = false
                    }
                }
                .padding()
                .background(Color.white)
                .cornerRadius(10)
                .accessibilityAddTraits(.isModal) // Indique que c'est une modale
                // Permet de fermer la modale avec le geste "Z" de VoiceOver (Escape)
                .accessibilityAction(.escape) {
                    showModal = false
                }
            }
        }
    }
}

// MARK: - 5. NAVIGATION COHÉRENTE [AA] — Critère 10.4

/// Pattern: TabView avec labels accessibles pour chaque onglet
struct ConsistentNavigationPattern: View {
    var body: some View {
        TabView {
            Text("Contenu Accueil")
                .tabItem {
                    Label("Accueil", systemImage: "house")
                }
                // Si l'image n'est pas claire, ajouter un label spécifique
                .accessibilityLabel("Onglet Accueil")
            
            Text("Contenu Paramètres")
                .tabItem {
                    Label("Paramètres", systemImage: "gear")
                }
                .accessibilityLabel("Onglet Paramètres")
        }
    }
}

// MARK: - 6. SKIP NAVIGATION

/// Pattern: Bouton invisible visuellement mais lisible par VoiceOver pour sauter le menu
struct SkipNavigationPattern: View {
    @FocusState private var mainContentFocused: Bool
    
    var body: some View {
        VStack {
            Button("Sauter au contenu principal") {
                mainContentFocused = true
            }
            .opacity(0)
            .frame(width: 0, height: 0)
            .accessibilityHidden(false) // Forcé à false pour s'assurer qu'il soit lu
            
            // Menu de navigation complexe...
            
            Text("Contenu Principal")
                .focused($mainContentFocused)
        }
    }
}

// MARK: - 7. RETOUR FOCUS APRÈS ACTION

/// Pattern: Retour du focus sur l'élément déclencheur
struct FocusReturnPattern: View {
    @State private var showDetails = false
    @FocusState private var triggerButtonFocused: Bool
    
    var body: some View {
        VStack {
            Button("Voir détails") {
                showDetails = true
            }
            .focused($triggerButtonFocused)
            
            if showDetails {
                VStack {
                    Text("Détails...")
                    Button("Fermer") {
                        showDetails = false
                        // Retourne le focus au bouton déclencheur
                        triggerButtonFocused = true
                    }
                }
                .accessibilityAddTraits(.isModal)
            }
        }
    }
}
