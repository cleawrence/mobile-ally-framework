import SwiftUI
import Charts

// =============================================================================
// SKILL-01 — Éléments Graphiques — Patterns SwiftUI
// FRAM : Framework Référence Accessibilité Mobile — v1.0
// Critères RAAM 1.1 : 1.1 à 1.9
// =============================================================================

// MARK: - 1. ÉLÉMENTS DÉCORATIFS [A] — Critère 1.1

struct DecorativeElementPatterns: View {
    var body: some View {
        VStack(spacing: 24) {

            // ✅ Bon — icône à côté d'un texte : icône masquée
            HStack {
                Image(systemName: "star.fill")
                    .foregroundStyle(.yellow)
                    .accessibilityHidden(true) // [A] ✅ Décoratif — le texte "Favoris" dit tout
                Text("Favoris")
            }
            // VoiceOver : "Favoris" (l'icône est silencieuse)

            // ❌ Mauvais — icône décorative non masquée
            HStack {
                Image(systemName: "star.fill")
                    .foregroundStyle(.yellow)
                    // Pas de .accessibilityHidden(true) !
                Text("Favoris")
            }
            // VoiceOver : "star, rempli" puis "Favoris" — doublon confus

            // ✅ Bon — séparateur décoratif masqué
            Divider()
                .accessibilityHidden(true) // [A] ✅ Élément purement structurel/visuel

            // ✅ Bon — fond décoratif masqué
            ZStack {
                RoundedRectangle(cornerRadius: 12)
                    .fill(Color.blue.opacity(0.1))
                    .accessibilityHidden(true) // [A] ✅ Forme de fond décorative
                Text("Contenu de la carte")
            }

            // ✅ Bon — plusieurs icônes décoratives dans un groupe
            HStack(spacing: 4) {
                Image(systemName: "location.fill").accessibilityHidden(true)
                Text("Paris, France")
                Image(systemName: "calendar").accessibilityHidden(true)
                Text("24 juillet 2026")
            }
            // VoiceOver : "Paris, France" puis "24 juillet 2026"
        }
    }
}

// MARK: - 2. ÉLÉMENTS PORTEURS D'INFORMATION [A] — Critères 1.2, 1.3

struct InformativeElementPatterns: View {
    var connectionStatus: ConnectionStatus = .unstable

    enum ConnectionStatus { case connected, unstable, disconnected }

    var body: some View {
        VStack(spacing: 24) {

            // ✅ Bon — icône d'état avec label contextuel
            Image(systemName: "exclamationmark.triangle.fill")
                .foregroundStyle(.yellow)
                .accessibilityLabel("Avertissement : connexion réseau instable")
                // VoiceOver : "Avertissement : connexion réseau instable, image"
                // [A] ✅ Critère 1.2 : alternative textuelle
                // [A] ✅ Critère 1.3 : label contextuel (pas "icône jaune" ni "exclamationmark")

            // ❌ Mauvais — nom SF Symbol brut — VIOLATION [A] 1.3
            Image(systemName: "exclamationmark.triangle.fill")
                .accessibilityLabel("exclamationmark.triangle.fill")
                // VoiceOver : "exclamationmark.triangle.fill, image" — incompréhensible

            // ❌ Mauvais — préfixe redondant — VIOLATION [A] 1.3
            Image(systemName: "checkmark.circle.fill")
                .accessibilityLabel("Icône de coche verte")
                // "Icône de" est redondant — VoiceOver dit déjà "image"
                // De plus, la couleur est une info visuelle qui ne devrait pas être le label principal

            // ✅ Bon — icône d'état dynamique avec label adaptatif
            Image(systemName: statusIcon)
                .foregroundStyle(statusColor)
                .accessibilityLabel(statusLabel)
                // VoiceOver annonce l'état réel ("Connecté", "Connexion instable", "Déconnecté")

            // ✅ Bon — notation par étoiles (informative)
            starRating(rating: 4, outOf: 5)

            // ✅ Bon — image réseau informative (photo de profil)
            AsyncImage(url: URL(string: "https://example.com/avatar.jpg")) { image in
                image
                    .resizable()
                    .scaledToFill()
                    .clipShape(Circle())
                    .accessibilityLabel("Photo de profil de Marie Dupont")
                    // [A] ✅ Label contextuel avec le nom de la personne
            } placeholder: {
                Circle()
                    .fill(Color.gray.opacity(0.3))
                    .accessibilityLabel("Chargement de la photo de profil")
            }
            .frame(width: 60, height: 60)

            // ✅ Bon — image décorative dans une carte (le titre de la carte dit tout)
            AsyncImage(url: URL(string: "https://example.com/article-hero.jpg")) { image in
                image.resizable().scaledToFill()
                    .accessibilityHidden(true) // [A] Décoratif — le titre de l'article décrit l'article
            } placeholder: { Color.gray.opacity(0.2) }
            .frame(height: 200)
        }
    }

    // Computed properties for status icon
    var statusIcon: String {
        switch connectionStatus {
        case .connected: "wifi"
        case .unstable: "wifi.exclamationmark"
        case .disconnected: "wifi.slash"
        }
    }

    var statusColor: Color {
        switch connectionStatus {
        case .connected: .green
        case .unstable: .orange
        case .disconnected: .red
        }
    }

    var statusLabel: String {
        switch connectionStatus {
        case .connected: "Connecté au réseau Wi-Fi"
        case .unstable: "Connexion Wi-Fi instable"
        case .disconnected: "Déconnecté — aucun réseau"
        }
    }

    // ✅ Bon — notation étoiles accessible [A]
    @ViewBuilder
    func starRating(rating: Int, outOf total: Int) -> some View {
        HStack(spacing: 2) {
            ForEach(0..<total, id: \.self) { index in
                Image(systemName: index < rating ? "star.fill" : "star")
                    .foregroundStyle(index < rating ? .yellow : .gray)
                    .accessibilityHidden(true) // Chaque étoile individuelle est masquée
            }
        }
        .accessibilityElement(children: .ignore) // Combine tout le groupe
        .accessibilityLabel("Note : \(rating) étoile\(rating > 1 ? "s" : "") sur \(total)")
        // VoiceOver : "Note : 4 étoiles sur 5, image"
        // ❌ Sans cela : VoiceOver lirait "star, rempli" 4 fois + "star" 1 fois
    }
}

// MARK: - 3. DESCRIPTION DÉTAILLÉE [A] — Critères 1.6, 1.7

struct DetailedDescriptionPatterns: View {
    @State private var showDataTable = false

    struct SalesData: Identifiable {
        let id = UUID()
        let month: String
        let sales: Double
    }

    let salesData = [
        SalesData(month: "Jan", sales: 45000),
        SalesData(month: "Fév", sales: 62000),
        SalesData(month: "Mar", sales: 78000),
        SalesData(month: "Avr", sales: 55000),
        SalesData(month: "Mai", sales: 91000),
    ]

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {

            // ✅ Bon — graphique Swift Charts avec accessibilité native
            Chart(salesData) { item in
                BarMark(
                    x: .value("Mois", item.month),
                    y: .value("Ventes", item.sales)
                )
                // Swift Charts génère automatiquement les descriptions par point
                // VoiceOver : "Jan, 45 000 €" "Fév, 62 000 €" etc.
            }
            .frame(height: 200)
            .chartAccessibilityLabel("Évolution des ventes — Janvier à Mai 2026")
            // Label global pour le graphique entier [A] ✅

            // ✅ Bon — bouton alternatif pour les données brutes
            Button("Voir les données du graphique en tableau") {
                showDataTable = true
            }
            .accessibilityLabel("Voir les données de ventes en tableau")
            // [A] ✅ Critère 1.6 : alternative pour les non-voyants

            // ✅ Bon — image complexe (carte, schéma) avec description détaillée
            VStack(alignment: .leading, spacing: 8) {
                Image("metro_map_paris")
                    .resizable()
                    .scaledToFit()
                    .accessibilityLabel("Carte du métro parisien")
                    .accessibilityHint("Carte schématique montrant les 16 lignes de métro. Utilisez le bouton ci-dessous pour une description détaillée.")
                    // [A] ✅ Label bref + hint orientant vers la description

                Button("Lire la description de la carte du métro") {
                    // Ouvre une vue avec la description complète
                }
            }
        }
        .sheet(isPresented: $showDataTable) {
            DataTableView(data: salesData)
        }
    }
}

// Table alternative pour graphique
struct DataTableView: View {
    struct SalesData: Identifiable {
        let id = UUID()
        let month: String
        let sales: Double
    }
    let data: [SalesData]

    var body: some View {
        NavigationStack {
            List(data) { item in
                HStack {
                    Text(item.month)
                    Spacer()
                    Text("\(Int(item.sales)) €")
                }
            }
            .navigationTitle("Données de ventes")
            .navigationBarTitleDisplayMode(.inline)
        }
    }

    init(data: [InformativeElementPatterns.SalesDataItem] = []) {
        self.data = []
    }
}

// MARK: - 4. CAPTCHA [A] — Critères 1.4, 1.5

struct CaptchaPatterns: View {
    @State private var captchaInput = ""
    @State private var useAlternative = false

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {

            // ✅ Bon — CAPTCHA avec label décrivant nature et fonction [A] 1.4
            Image("captcha_image")
                .resizable()
                .scaledToFit()
                .accessibilityLabel("Test de sécurité anti-robot")
                .accessibilityHint("Entrez les 6 caractères affichés dans le champ ci-dessous")
                // [A] ✅ Critère 1.4 : nature (sécurité anti-robot) et fonction (saisir les caractères)

            TextField("Entrez les caractères", text: $captchaInput)
                .accessibilityLabel("Code de sécurité CAPTCHA, 6 caractères")

            // ✅ Bon — Alternative non graphique [A] 1.5
            VStack(alignment: .leading, spacing: 8) {
                Text("Vous n'arrivez pas à voir le code ?")
                    .font(.caption)

                // Alternative 1 : CAPTCHA audio
                Button {
                    playCaptchaAudio()
                } label: {
                    Label("Écouter le code audio", systemImage: "speaker.wave.3")
                }
                .accessibilityLabel("Écouter le code de sécurité audio")
                // [A] ✅ Critère 1.5 : alternative non graphique

                // Alternative 2 : validation par SMS
                Button {
                    sendSMSCode()
                } label: {
                    Label("Recevoir un code par SMS", systemImage: "message")
                }
                // [A] ✅ Critère 1.5 : alternative complète sans CAPTCHA
            }
        }
    }

    func playCaptchaAudio() {}
    func sendSMSCode() {}
}

// MARK: - 5. IMAGES TEXTE [AA] — Critère 1.8

struct ImageTextPatterns: View {

    // ❌ Mauvais — image bitmap de texte — VIOLATION [AA] 1.8
    // Ne pas faire :
    // Image("promo_banner_soldes_50_pourcent")  // Image PNG avec le texte "SOLDES -50%"
    // → Non redimensionnable, non personnalisable, non lisible avec Dynamic Type

    // ✅ Bon — texte natif stylisé avec effets visuels équivalents
    var gradientTitle: some View {
        Text("SOLDES -50%")
            .font(.largeTitle.bold())
            .foregroundStyle(
                LinearGradient(
                    colors: [.orange, .red],
                    startPoint: .leading,
                    endPoint: .trailing
                )
            )
        // [AA] ✅ Texte natif avec dégradé — s'adapte au Dynamic Type
        // VoiceOver : "SOLDES -50%, texte"
    }

    // ✅ Bon — police custom avec texte natif
    var customFontTitle: some View {
        Text("Offre Exclusive")
            .font(.custom("Georgia-Bold", size: 28))
            .shadow(color: .black.opacity(0.3), radius: 4, x: 0, y: 2)
        // [AA] ✅ Rendu premium avec texte natif — accessible et zoomable
    }

    // ✅ Bon — badge/label stylisé en texte natif
    var badgeView: some View {
        Text("NOUVEAU")
            .font(.caption.bold())
            .foregroundStyle(.white)
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .background(Color.red)
            .clipShape(Capsule())
        // [AA] ✅ Texte natif — s'adapte aux préférences de taille de police
    }

    // ⚠️ Exception — logotype (acceptable en image [AA])
    var logoException: some View {
        Image("company_logo")
            .resizable()
            .scaledToFit()
            .frame(height: 40)
            .accessibilityLabel("Logo Mon Entreprise")
            // [AA] ✅ Exception WCAG 1.4.5 : les logotypes peuvent être des images
    }

    var body: some View {
        VStack(spacing: 20) {
            gradientTitle
            customFontTitle
            badgeView
            logoException
        }
    }
}

// MARK: - 6. IMAGES LÉGENDÉES [AA] — Critère 1.9

struct CaptionedImagePatterns: View {

    // ✅ Bon — image + légende lues ensemble
    var goodCaptionedImage: some View {
        VStack(spacing: 8) {
            AsyncImage(url: URL(string: "https://example.com/team-photo.jpg")) { image in
                image.resizable().scaledToFill()
                    .accessibilityHidden(true)
                    // [AA] L'image seule est masquée — le tout sera lu via accessibilityElement
            } placeholder: {
                Rectangle().fill(Color.gray.opacity(0.3))
            }
            .frame(height: 200)
            .clipped()

            Text("L'équipe FRAM lors de la réunion de lancement, juillet 2026")
                .font(.caption)
                .foregroundStyle(.secondary)
        }
        // [AA] ✅ Le groupe entier est lu comme un seul élément
        .accessibilityElement(children: .combine)
        // VoiceOver : "L'équipe FRAM lors de la réunion de lancement, juillet 2026, image"
        // La légende sert d'alternative à l'image entière
    }

    // ❌ Mauvais — image avec son propre label + légende séparée — VIOLATION [AA] 1.9
    var badSeparatedCaptionedImage: some View {
        VStack {
            Image("team_photo")
                .resizable()
                .accessibilityLabel("Photo de l'équipe")  // Lu séparément
            Text("L'équipe FRAM, juillet 2026")            // Lu séparément
            // VoiceOver lit deux éléments distincts alors qu'ils forment un seul tout
        }
    }

    // ✅ Bon — vignette de carte avec image + titre fusionnés
    var articleCard: some View {
        HStack(spacing: 12) {
            AsyncImage(url: URL(string: "https://example.com/article.jpg")) { image in
                image.resizable().scaledToFill()
                    .accessibilityHidden(true) // [AA] L'image est décorative dans le contexte de la carte
            } placeholder: {
                Rectangle().fill(Color.gray.opacity(0.2))
            }
            .frame(width: 80, height: 80)
            .clipped()
            .clipShape(RoundedRectangle(cornerRadius: 8))

            VStack(alignment: .leading, spacing: 4) {
                Text("5 astuces pour une app accessible")
                    .font(.headline)
                Text("FRAM • 5 min de lecture")
                    .font(.caption)
                    .foregroundStyle(.secondary)
            }
        }
        .padding(12)
        // [AA] ✅ La carte entière est un seul élément de navigation
        .accessibilityElement(children: .combine)
        // VoiceOver : "5 astuces pour une app accessible, FRAM, 5 min de lecture"
    }

    var body: some View {
        VStack(spacing: 24) {
            goodCaptionedImage
            articleCard
        }
        .padding()
    }
}

// MARK: - 7. BOUTONS ICONIQUES [A] — Combinaison critères 1.1, 1.2, 1.3

struct IconButtonPatterns: View {
    @State private var isFavorite = false
    @State private var isShared = false

    var body: some View {
        HStack(spacing: 20) {

            // ✅ Bon — bouton favori avec état dans le label
            Button {
                isFavorite.toggle()
            } label: {
                Image(systemName: isFavorite ? "heart.fill" : "heart")
                    .foregroundStyle(isFavorite ? .red : .primary)
                    .accessibilityHidden(true) // [A] Icône décorative — le label du Button suffit
            }
            .accessibilityLabel(isFavorite ? "Retirer des favoris" : "Ajouter aux favoris")
            // VoiceOver : "Ajouter aux favoris, bouton"
            // Après tap : "Retirer des favoris, bouton"

            // ✅ Bon — bouton partage
            Button {
                isShared = true
            } label: {
                Image(systemName: "square.and.arrow.up")
                    .accessibilityHidden(true)
            }
            .accessibilityLabel("Partager cet article")

            // ❌ Mauvais — label du bouton = nom SF Symbol — VIOLATION [A] 1.3
            Button {
            } label: {
                Image(systemName: "trash")
                    // Pas de .accessibilityHidden + pas de label sur Button
                    // VoiceOver : "trash, bouton" — incompréhensible
            }

            // ✅ Fix du mauvais bouton
            Button {
            } label: {
                Image(systemName: "trash")
                    .accessibilityHidden(true)
            }
            .accessibilityLabel("Supprimer l'article")
        }
        .padding()
    }
}
