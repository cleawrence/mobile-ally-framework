import SwiftUI

// =============================================================================
// SKILL-05 — Composants Interactifs — Patterns SwiftUI
// FRAM : Framework Référence Accessibilité Mobile — v1.0
// Critères RAAM 1.1 : 5.1 à 5.12
// =============================================================================

// MARK: - 1. BUTTON / ICONBUTTON [A] — Critères 5.1, 5.2, 5.3

struct ButtonPatterns: View {

    // ✅ Bon — label descriptif en contexte (critère 5.2)
    var goodContextualButton: some View {
        Button(action: { deleteItem() }) {
            Image(systemName: "trash")
        }
        .accessibilityLabel("Supprimer l'article Chaussures")
        // Rôle "bouton" est automatiquement fourni par Button {}
        // VoiceOver annonce : "Supprimer l'article Chaussures, bouton"
    }

    // ❌ Mauvais — label sans contexte (violation critère 5.2)
    var badGenericButton: some View {
        Button(action: { deleteItem() }) {
            Image(systemName: "trash")
        }
        .accessibilityLabel("Supprimer")
        // VoiceOver annonce : "Supprimer, bouton" — quel article ?
    }

    // ❌ Mauvais — label vide = critère 5.1 non conforme
    var badEmptyLabelButton: some View {
        Button(action: { deleteItem() }) {
            Image(systemName: "trash")
        }
        // Aucun .accessibilityLabel → VoiceOver dit "trash, bouton" (nom SF Symbol)
        // VIOLATION [A] : nom non accessible
    }

    // ❌ Mauvais — accessibilityLabel explicitement vide — VIOLATION [A] 5.2
    var badExplicitEmptyLabelButton: some View {
        Button(action: { deleteItem() }) {
            Image(systemName: "trash")
        }
        .accessibilityLabel("")
        // "" n'est pas un label valide : VoiceOver ignore l'élément ou lit un état aléatoire
        // Utiliser .accessibilityHidden(true) pour masquer volontairement, sinon fournir un vrai label
    }

    // ✅ Bon — bouton désactivé exposé correctement
    var disabledButton: some View {
        Button(action: { submitForm() }) {
            Text("Valider")
        }
        .disabled(!isFormValid)
        // SwiftUI expose automatiquement l'état "estompé" à VoiceOver
        // VoiceOver annonce : "Valider, estompé, bouton"
    }

    // ✅ Bon — éviter de préfixer le rôle dans le label (critère 5.2)
    var noRoleRedundancy: some View {
        Button("Valider") { submitForm() }
        // ✅ "Valider" → VoiceOver dit "Valider, bouton"
        // ❌ .accessibilityLabel("Bouton Valider") → VoiceOver dirait "Bouton Valider, bouton"
    }

    // ✅ Bon — élément cliquable custom avec rôle déclaré (critère 5.3)
    var customTappableWithRole: some View {
        HStack {
            Text("Voir le profil")
            Spacer()
            Image(systemName: "chevron.right")
        }
        .contentShape(Rectangle())
        .onTapGesture { navigateToProfile() }
        .accessibilityAddTraits(.isButton)        // ← déclare le rôle [A]
        .accessibilityLabel("Voir le profil de Marie Dupont")
    }

    var body: some View {
        VStack(spacing: 16) {
            goodContextualButton
            badGenericButton
            badEmptyLabelButton
            badExplicitEmptyLabelButton
            disabledButton
            noRoleRedundancy
            customTappableWithRole
        }
    }

    // Helpers
    func deleteItem() {}
    func submitForm() {}
    func navigateToProfile() {}
    var isFormValid: Bool { true }
}

// MARK: - 2. TOGGLE / SWITCH [A] — Critères 5.3, 5.4

struct TogglePatterns: View {
    @State private var isEnabled = false
    @State private var isDarkMode = false

    // ✅ Bon — état ON/OFF restitué automatiquement par Toggle
    var standardToggle: some View {
        Toggle("Notifications push", isOn: $isEnabled)
        // VoiceOver annonce : "Notifications push, activé/désactivé, interrupteur"
        // Le rôle "interrupteur" et l'état sont automatiques via Toggle
    }

    // ✅ Bon — Toggle avec icône + texte, label consolidé
    var toggleWithIcon: some View {
        Toggle(isOn: $isDarkMode) {
            Label("Mode sombre", systemImage: "moon.fill")
        }
        .accessibilityLabel("Mode sombre")
        // L'icône est décorative dans ce contexte — le label consolidé suffit
    }

    // ✅ Bon — Toggle personnalisé (style custom) avec sémantique préservée
    var customStyledToggle: some View {
        Toggle(isOn: $isEnabled) {
            Text("Recevoir les alertes")
        }
        .toggleStyle(SwitchToggleStyle(tint: .blue))
        // Le style ne casse pas l'accessibilité — Toggle conserve son rôle et son état
    }

    // ❌ Mauvais — simuler un toggle avec Button (perd le rôle interrupteur)
    var badFakeToggle: some View {
        Button(action: { isEnabled.toggle() }) {
            Image(systemName: isEnabled ? "toggle.on" : "toggle.off")
        }
        // VIOLATION [A] : rôle "bouton" alors que c'est un interrupteur
        // Fix : utiliser Toggle ou ajouter .accessibilityValue et traits
    }

    // ✅ Fix du faux toggle — si usage de Button est inévitable
    var fixedFakeToggle: some View {
        Button(action: { isEnabled.toggle() }) {
            Image(systemName: isEnabled ? "toggle.on" : "toggle.off")
        }
        .accessibilityLabel("Recevoir les alertes")
        .accessibilityValue(isEnabled ? "activé" : "désactivé")
        .accessibilityAddTraits(isEnabled ? [.isButton, .isSelected] : [.isButton])
        // Acceptable si Toggle natif n'est vraiment pas possible
    }

    var body: some View {
        VStack(spacing: 16) {
            standardToggle
            toggleWithIcon
            customStyledToggle
            badFakeToggle
            fixedFakeToggle
        }
    }
}

// MARK: - 3. CHECKBOX (custom) [A] — Critères 5.3, 5.4

// SwiftUI n'a pas de Checkbox natif sur iOS — implémentation custom accessible
struct AccessibleCheckbox: View {
    @Binding var isChecked: Bool
    let label: String

    var body: some View {
        Button(action: { isChecked.toggle() }) {
            HStack(spacing: 12) {
                Image(systemName: isChecked ? "checkmark.square.fill" : "square")
                    .foregroundStyle(isChecked ? .blue : .secondary)
                    .accessibilityHidden(true) // Icône décorative — info portée par le parent
                Text(label)
            }
        }
        .buttonStyle(.plain)
        .accessibilityLabel(label)
        .accessibilityAddTraits(isChecked ? [.isButton, .isSelected] : [.isButton])
        .accessibilityValue(isChecked ? "cochée" : "non cochée")
        // VoiceOver annonce : "Accepter les CGU, non cochée, bouton"
        // Après activation : "cochée"
    }
}

// MARK: - 4. RADIOBUTTON (custom) [A]

struct AccessibleRadioGroup: View {
    @State private var selectedOption = "Option A"
    let options = ["Option A", "Option B", "Option C"]

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("Choisir un mode de livraison")
                .accessibilityAddTraits(.isHeader)

            ForEach(options, id: \.self) { option in
                Button(action: { selectedOption = option }) {
                    HStack {
                        Image(systemName: selectedOption == option
                              ? "largecircle.fill.circle"
                              : "circle")
                            .accessibilityHidden(true)
                        Text(option)
                    }
                }
                .buttonStyle(.plain)
                .accessibilityLabel(option)
                // ✅ Indique l'état sélectionné avec .isSelected trait
                .accessibilityAddTraits(selectedOption == option ? [.isSelected] : [])
                // VoiceOver annonce : "Option A, sélectionné, bouton" ou "Option B, bouton"
            }
        }
    }
}

// MARK: - 5. SLIDER [A] — Critères 5.5

struct SliderPatterns: View {
    @State private var volume: Double = 50
    @State private var rating: Double = 3
    @State private var uploadProgress: Double = 0.6

    // ✅ Bon — valeur courante annoncée
    var volumeSlider: some View {
        VStack {
            Text("Volume : \(Int(volume))%")
            Slider(value: $volume, in: 0...100, step: 1)
                .accessibilityLabel("Volume")
                .accessibilityValue("\(Int(volume))%")
                // VoiceOver annonce : "Volume, 50%, curseur"
                // Navigation : balayer haut/bas pour +/- 1 step
        }
    }

    // ✅ Bon — notation étoiles accessible (Slider custom)
    var starRatingSlider: some View {
        Slider(value: $rating, in: 1...5, step: 1)
            .accessibilityLabel("Note")
            .accessibilityValue("\(Int(rating)) étoile\(rating > 1 ? "s" : "") sur 5")
            // VoiceOver : "Note, 3 étoiles sur 5, curseur"
    }

    // ✅ Bon — ProgressBar (non interactif)
    var uploadProgressBar: some View {
        ProgressView(value: uploadProgress, total: 1.0)
            .accessibilityLabel("Progression de l'envoi")
            .accessibilityValue("\(Int(uploadProgress * 100))%")
            .accessibilityAddTraits(.updatesFrequently) // [AA] — contenu dynamique
            // VoiceOver annonce la valeur au focus, puis les mises à jour
    }

    var body: some View {
        VStack(spacing: 16) {
            volumeSlider
            starRatingSlider
            uploadProgressBar
        }
    }
}

// MARK: - 6. TABS [A] — Critères 5.4

enum AppTab { case home, search, profile }

struct TabPatterns: View {
    @State private var selectedTab = AppTab.home

    // ✅ TabView natif — sélection restituée automatiquement
    var nativeTabView: some View {
        TabView(selection: $selectedTab) {
            Text("Accueil")
                .tabItem { Label("Accueil", systemImage: "house.fill") }
                .tag(AppTab.home)

            Text("Recherche")
                .tabItem { Label("Recherche", systemImage: "magnifyingglass") }
                .tag(AppTab.search)

            Text("Profil")
                .tabItem { Label("Profil", systemImage: "person.fill") }
                .tag(AppTab.profile)
        }
        // VoiceOver : "Accueil, onglet 1 sur 3, sélectionné"
        // Shift-tab dans VoiceOver pour naviguer entre onglets
    }

    // ✅ Picker segmenté — sélection restituée automatiquement
    @State private var sortOrder = "Date"
    var segmentedPicker: some View {
        Picker("Trier par", selection: $sortOrder) {
            Text("Date").tag("Date")
            Text("Nom").tag("Nom")
            Text("Taille").tag("Taille")
        }
        .pickerStyle(.segmented)
        .accessibilityLabel("Trier par")
        // VoiceOver : "Trier par, Date, sélectionné, 1 sur 3"
    }

    var body: some View {
        VStack(spacing: 16) {
            nativeTabView
            segmentedPicker
        }
    }
}

// MARK: - 7. GESTES COMPLEXES — ALTERNATIVES [AA] — Critère 5.6

struct GestureAlternativePatterns: View {

    struct Item: Identifiable {
        let id = UUID()
        var name: String
    }

    @State private var items = [Item(name: "Article 1"), Item(name: "Article 2")]

    // ✅ Alternative au swipe-to-delete dans une liste
    var listWithSwipeAndAlternative: some View {
        List {
            ForEach(items) { item in
                Text(item.name)
                    .swipeActions(edge: .trailing) {
                        Button(role: .destructive) {
                            delete(item)
                        } label: {
                            Label("Supprimer", systemImage: "trash")
                        }
                    }
                    // [AA] ✅ Alternative accessible au swipe
                    .accessibilityAction(named: "Supprimer \(item.name)") {
                        delete(item)
                    }
                    // VoiceOver : balayer vers le haut → "Actions disponibles"
                    // → "Supprimer Article 1"
            }
        }
    }

    // ✅ Alternative au drag & drop pour réordonner
    var reorderableList: some View {
        List {
            ForEach(Array(items.enumerated()), id: \.element.id) { index, item in
                Text(item.name)
                    .accessibilityActions {
                        // [AA] ✅ Alternatives aux gestes drag
                        if index > 0 {
                            Button("Déplacer \(item.name) vers le haut") {
                                moveUp(at: index)
                            }
                        }
                        if index < items.count - 1 {
                            Button("Déplacer \(item.name) vers le bas") {
                                moveDown(at: index)
                            }
                        }
                    }
            }
            .onMove { moveItems(from:$0, to:$1) }
        }
        .toolbar { EditButton() }
    }

    // ✅ Alternative au pinch-to-zoom
    var zoomableImage: some View {
        VStack {
            // Image avec geste pinch
            AsyncImage(url: URL(string: "https://example.com/image.jpg")) { image in
                image.resizable().scaledToFit()
            } placeholder: { ProgressView() }
            // [AA] ✅ Boutons alternatifs
            HStack {
                Button("Zoom -") { zoomOut() }
                    .accessibilityLabel("Réduire l'image")
                Spacer()
                Button("Zoom +") { zoomIn() }
                    .accessibilityLabel("Agrandir l'image")
            }
        }
    }

    var body: some View {
        VStack(spacing: 16) {
            listWithSwipeAndAlternative
            reorderableList
            zoomableImage
        }
    }

    func delete(_ item: Item) { items.removeAll { $0.id == item.id } }
    func moveUp(at index: Int) { guard index > 0 else { return }; items.swapAt(index, index - 1) }
    func moveDown(at index: Int) { guard index < items.count - 1 else { return }; items.swapAt(index, index + 1) }
    func moveItems(from source: IndexSet, to destination: Int) { items.move(fromOffsets: source, toOffset: destination) }
    func zoomIn() {}
    func zoomOut() {}
}

// MARK: - 8. TAILLE DE CIBLE [AA] — Critère 5.7

struct TouchTargetPatterns: View {

    // ❌ Mauvais — zone de toucher trop petite
    var tooSmallButton: some View {
        Button(action: { dismiss() }) {
            Image(systemName: "xmark")
                .font(.system(size: 12))
        }
        // Zone de tap = ~20×20pt — VIOLATION [AA]
    }

    // ✅ Bon — agrandir la zone sans changer l'apparence visuelle
    var correctSizeButton: some View {
        Button(action: { dismiss() }) {
            Image(systemName: "xmark")
                .font(.system(size: 14))
                .frame(minWidth: 44, minHeight: 44) // [AA] ✅ Zone ≥ 44×44pt
        }
        .contentShape(Rectangle())
    }

    // ✅ Bon — padding pour agrandir la zone
    var paddedButton: some View {
        Image(systemName: "xmark")
            .padding(15)                  // 14pt icône + 15pt padding = 44pt total ≅
            .contentShape(Rectangle())    // Toute la zone est cliquable
            .onTapGesture { dismiss() }
            .accessibilityAddTraits(.isButton)
            .accessibilityLabel("Fermer")
    }

    // ✅ Bon — utiliser .buttonStyle qui respecte la taille
    var systemStyleButton: some View {
        Button("Annuler", action: { dismiss() })
            .buttonStyle(.borderless)
            .frame(minWidth: 44, minHeight: 44)
    }

    var body: some View {
        VStack(spacing: 16) {
            tooSmallButton
            correctSizeButton
            paddedButton
            systemStyleButton
        }
    }

    func dismiss() {}
}

// MARK: - 9. GESTION DU FOCUS — DIALOG / BOTTOMSHEET [A] — Critère 5.12

struct FocusManagementPatterns: View {
    @State private var showModal = false
    @FocusState private var isModalFocused: Bool

    var body: some View {
        Button("Ouvrir la boîte de dialogue") {
            showModal = true
        }
        .sheet(isPresented: $showModal) {
            ModalContent(isPresented: $showModal)
            // ✅ SwiftUI gère automatiquement :
            // - le focus entre dans la sheet à l'ouverture
            // - le focus retourne au déclencheur à la fermeture
            // - le contenu derrière est inaccessible (critère 5.12)
        }
    }
}

struct ModalContent: View {
    @Binding var isPresented: Bool
    @FocusState private var focusedField: Bool

    var body: some View {
        NavigationStack {
            Form {
                TextField("Nom", text: .constant(""))
                    .focused($focusedField)
            }
            .navigationTitle("Modifier le profil")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Annuler") { isPresented = false }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Enregistrer") { isPresented = false }
                }
            }
        }
        .onAppear {
            // ✅ Placer le focus sur le premier champ à l'ouverture
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                focusedField = true
            }
        }
    }
}

// MARK: - 10. ANNONCES D'ÉTAT [A] — Critère 5.4

struct StateAnnouncementPatterns: View {
    @State private var uploadState: UploadState = .idle

    enum UploadState { case idle, uploading, success, failure }

    var body: some View {
        VStack {
            Button("Envoyer") { startUpload() }

            // ✅ Annonce dynamique d'un changement d'état asynchrone
            Text(statusMessage)
                .accessibilityLabel(accessibleStatus)
                .accessibilityAddTraits(uploadState == .uploading ? .updatesFrequently : [])
        }
        .onChange(of: uploadState) { newState in
            // ✅ Annonce vocale sans déplacer le focus
            let message: String
            switch newState {
            case .success: message = "Envoi réussi"
            case .failure: message = "Échec de l'envoi. Veuillez réessayer."
            default: return
            }
            UIAccessibility.post(notification: .announcement, argument: message)
        }
    }

    var statusMessage: String {
        switch uploadState {
        case .idle: return ""
        case .uploading: return "Envoi en cours…"
        case .success: return "✓ Envoyé"
        case .failure: return "✗ Échec"
        }
    }

    var accessibleStatus: String {
        switch uploadState {
        case .idle: return ""
        case .uploading: return "Envoi en cours"
        case .success: return "Envoi réussi"
        case .failure: return "Échec de l'envoi"
        }
    }

    func startUpload() { uploadState = .uploading }
}
