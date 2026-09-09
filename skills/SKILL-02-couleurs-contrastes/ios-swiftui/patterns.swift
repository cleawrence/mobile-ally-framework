import SwiftUI

// =============================================================================
// SKILL-02 — Couleurs et Contrastes — Patterns SwiftUI
// FRAM : Framework Référence Accessibilité Mobile — v1.0
// Critères RAAM 1.1 : 2.1 [A], 2.2 [AA], 2.3 [AA]
// =============================================================================

// MARK: - 1. INFORMATION PAR COULEUR SEULE [A] — Critère 2.1

struct ColorOnlyViolationPatterns: View {

    // ❌ Mauvais — succès/erreur uniquement par couleur — VIOLATION [A] 2.1
    var badStatusBadge: some View {
        HStack {
            Circle()
                .fill(Color.green) // Vert seul pour "en ligne"
                .frame(width: 10, height: 10)
            // Un utilisateur daltonien (deutéranopie) ne distingue pas vert/rouge
            // Aucune information alternative pour VoiceOver
        }
    }

    // ✅ Bon — icône + forme + couleur + texte [A]
    var goodStatusBadge: some View {
        HStack(spacing: 6) {
            Image(systemName: "circle.fill")    // Forme ●
                .foregroundStyle(.green)         // Couleur
                .font(.system(size: 10))
                .accessibilityHidden(true)
            Text("En ligne")                     // Texte
                .font(.caption)
                .foregroundStyle(.green)
        }
        .accessibilityElement(children: .combine)
        .accessibilityLabel("Statut : En ligne")
        // ✅ Triple redondance : couleur + forme + texte [A]
    }

    // ❌ Mauvais — champ erreur uniquement par bordure rouge — VIOLATION [A] 2.1
    var badErrorField: some View {
        TextField("Email", text: .constant("email-invalide"))
            .padding(10)
            .overlay(
                RoundedRectangle(cornerRadius: 8)
                    .stroke(Color.red, lineWidth: 2) // Couleur seule
            )
        // Daltonien protanope : le rouge peut sembler noir/foncé indistinct
    }

    // ✅ Bon — champ erreur avec icône + texte + couleur [A]
    var goodErrorField: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack {
                TextField("Email", text: .constant("email-invalide"))
                    .padding(8)
                Spacer()
                Image(systemName: "exclamationmark.circle.fill") // Icône
                    .foregroundStyle(.red)
                    .padding(.trailing, 8)
                    .accessibilityHidden(true)
            }
            .overlay(
                RoundedRectangle(cornerRadius: 8)
                    .stroke(Color.red, lineWidth: 2)
            )
            // Texte d'erreur visible
            Label("L'adresse email est invalide", systemImage: "exclamationmark.triangle")
                .font(.caption)
                .foregroundStyle(.red)
                .accessibilityHidden(true) // Géré via accessibilityHint du TextField
        }
    }

    // ❌ Mauvais — lien distingué uniquement par couleur — VIOLATION [A] 2.1
    var badLink: some View {
        Text("Voir les conditions d'utilisation")
            .foregroundStyle(.blue) // Couleur seule = insuffisant si fond bleu ou pour daltoniens
            .onTapGesture {}
    }

    // ✅ Bon — lien avec soulignement + couleur [A]
    var goodLink: some View {
        Text("Voir les conditions d'utilisation")
            .underline()             // Soulignement non-couleur
            .foregroundStyle(.blue)  // Couleur en plus du soulignement
            .onTapGesture {}
            .accessibilityAddTraits(.isLink)
    }

    // ❌ Mauvais — graphique à secteurs couleurs seules — VIOLATION [A] 2.1
    var badPieChartLegend: some View {
        HStack {
            HStack { Rectangle().fill(Color.red).frame(width: 12, height: 12); Text("Revenus") }
            HStack { Rectangle().fill(Color.green).frame(width: 12, height: 12); Text("Dépenses") }
            // Rouge/vert indistinguables pour 8% des hommes
        }
    }

    // ✅ Bon — graphique avec formes + hachures + couleurs [A]
    var goodChartLegend: some View {
        HStack(spacing: 16) {
            HStack(spacing: 4) {
                Image(systemName: "square.fill")   // ■ Carré plein
                    .foregroundStyle(.blue)
                    .accessibilityHidden(true)
                Text("Revenus")
            }
            HStack(spacing: 4) {
                Image(systemName: "circle.fill")   // ● Rond plein
                    .foregroundStyle(.orange)       // Palette daltonisme-friendly
                    .accessibilityHidden(true)
                Text("Dépenses")
            }
        }
        // ✅ Forme différente ET couleur daltonisme-friendly (bleu/orange vs rouge/vert)
    }
}

// MARK: - 2. COULEURS ADAPTATIVES SYSTÈME [A] — Critères 2.2, 2.3

struct AdaptiveColorPatterns: View {
    @Environment(\.colorScheme) private var colorScheme

    // ✅ Bon — couleurs système adaptatives (dark + light automatique)
    var adaptiveTextColors: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("Titre principal")
                .foregroundStyle(.primary)          // ✅ Adaptatif — noir en light, blanc en dark
            Text("Texte secondaire")
                .foregroundStyle(.secondary)        // ✅ Adaptatif — gris conforme dans les deux modes
            Text("Texte tertiaire")
                .foregroundStyle(.tertiary)         // ⚠️ Vérifier le contraste en dark mode
        }
    }

    // ✅ Bon — couleurs UIColor adaptatives (garantit conformité WCAG)
    var uiColorAdaptive: some View {
        VStack {
            Text("Label principal")
                .foregroundStyle(Color(UIColor.label))           // ✅ Conforme — adaptatif
            Text("Label secondaire")
                .foregroundStyle(Color(UIColor.secondaryLabel))  // ✅ ~4.5:1 en light
            Text("Label tertiaire")
                .foregroundStyle(Color(UIColor.tertiaryLabel))   // ⚠️ ~2.5:1 — à utiliser avec parcimonie
        }
    }

    // ❌ Mauvais — couleur hardcodée qui casse en dark mode — VIOLATION [AA] 2.2
    var badHardcodedColor: some View {
        VStack {
            Text("Texte sombre")
                .foregroundStyle(Color(hex: "#333333"))
            // En dark mode sur fond sombre : ratio #333333 sur #1C1C1E ≈ 1.2:1 → ILLISIBLE
            // VIOLATION [AA] 2.2

            Text("Texte gris moyen")
                .foregroundStyle(Color(hex: "#888888"))
            // Sur blanc #FFFFFF : ratio 3.54:1 → insuffisant pour texte normal
            // VIOLATION [AA] 2.2
        }
    }

    // ❌ Mauvais — couleur RGB hardcodée, même problème qu'avec hex — VIOLATION [AA] 2.2
    var badHardcodedRGBColor: some View {
        Text("Texte sombre")
            .foregroundStyle(Color(red: 0.2, green: 0.2, blue: 0.2))
        // Statique : ne change pas en dark mode, même risque de contraste insuffisant
        // que Color(hex:) — préférer une couleur système ou un Asset Color adaptatif
    }

    // ✅ Bon — couleur custom adaptative avec colorScheme
    var goodAdaptiveCustomColor: some View {
        Text("Texte personnalisé")
            .foregroundStyle(
                colorScheme == .dark
                    ? Color(hex: "#E0E0E0")  // ✅ ~12:1 sur fond dark #121212
                    : Color(hex: "#333333")  // ✅ ~12.6:1 sur fond blanc
            )
        // Les deux valeurs sont WCAG conformes dans leur mode respectif
    }

    // ✅ Bon — Asset Color adaptatif (recommandé via xcassets)
    // Dans xcassets : définir "Any Appearance" + "Dark" pour chaque couleur custom
    // Avantage : le système gère automatiquement le basculement
    var xcassetColorNote: some View {
        Text("Texte avec couleur xcassets")
            .foregroundStyle(Color("BrandPrimary")) // ✅ Défini en Light + Dark dans Assets.xcassets
    }

    var body: some View {
        VStack {
            adaptiveTextColors
            adaptiveTextColors
            goodAdaptiveCustomColor
        }
    }
}

// MARK: - 3. CONTRASTE TEXTE [AA] — Critère 2.2

struct TextContrastPatterns: View {

    // Référence : ratios mesurés avec Color Contrast Analyser (CCA)

    // ✅ Bon — texte noir sur fond blanc (21:1)
    var maximumContrast: some View {
        Text("Texte parfaitement contrasté")
            .foregroundStyle(.black)
            .background(.white)
        // Ratio : 21:1 ✅ AAA
    }

    // ✅ Bon — texte conforme minimum (4.5:1) — #767676 sur #FFFFFF
    var minimumConformantText: some View {
        Text("Texte gris conforme")
            .foregroundStyle(Color(hex: "#767676"))
            .background(.white)
        // Ratio : exactement 4.5:1 ✅ AA — limite minimum pour texte normal
    }

    // ❌ Mauvais — texte sous le seuil — VIOLATION [AA] 2.2
    var nonConformantText: some View {
        Text("Texte gris trop clair")
            .foregroundStyle(Color(hex: "#AAAAAA"))
            .background(.white)
        // Ratio : 2.3:1 ❌ — non conforme pour texte normal ET texte large
    }

    // ⚠️ Attention — placeholder TextField (souvent violé)
    var placeholderContrast: some View {
        VStack(alignment: .leading) {
            // Le placeholder par défaut SwiftUI utilise .tertiaryLabel (~1.5:1 en light)
            // VIOLATION [AA] 2.2 fréquente pour les placeholders
            TextField("placeholder par défaut", text: .constant(""))
            // ✅ Fix : utiliser un placeholder plus sombre
            TextField("", text: .constant(""))
                .overlay(
                    Group {
                        if true { // condition champ vide
                            Text("ex. prenom@domaine.fr")
                                .foregroundStyle(Color(hex: "#767676")) // ✅ 4.5:1 minimum
                                .padding(.leading, 4)
                        }
                    },
                    alignment: .leading
                )
        }
    }

    // ✅ Bon — texte sur image avec couche semi-opaque garantissant le contraste
    var textOnImage: some View {
        ZStack {
            AsyncImage(url: URL(string: "https://example.com/hero.jpg")) { img in
                img.resizable().scaledToFill()
            } placeholder: { Color.gray }
            .frame(height: 200)

            // Couche sombre garantissant le contraste
            LinearGradient(
                colors: [.clear, Color.black.opacity(0.7)],
                startPoint: .top,
                endPoint: .bottom
            )
            // ✅ Noir à 70% sur image → ratio effectif >> 4.5:1

            VStack {
                Spacer()
                Text("Titre de l'article")
                    .font(.headline)
                    .foregroundStyle(.white)    // ✅ Blanc sur fond sombre
                    .padding()
            }
        }
    }
}

// MARK: - 4. CONTRASTE COMPOSANTS UI [AA] — Critère 2.3

struct ComponentContrastPatterns: View {
    @State private var text = ""
    @State private var isChecked = false

    // ✅ Bon — bordure de TextField conforme (≥ 3:1)
    var conformantTextFieldBorder: some View {
        TextField("Label", text: $text)
            .padding(10)
            .overlay(
                RoundedRectangle(cornerRadius: 8)
                    .stroke(Color(hex: "#767676"), lineWidth: 1)
                    // #767676 sur #FFFFFF = 4.5:1 ✅ — bien au-dessus du minimum 3:1 pour composant
            )
        // [AA] ✅ Critère 2.3 : la bordure est visible (≥ 3:1)
    }

    // ❌ Mauvais — bordure trop claire — VIOLATION [AA] 2.3
    var nonConformantBorder: some View {
        TextField("Label", text: $text)
            .padding(10)
            .overlay(
                RoundedRectangle(cornerRadius: 8)
                    .stroke(Color(hex: "#CCCCCC"), lineWidth: 1)
                    // #CCCCCC sur #FFFFFF = 1.6:1 ❌ — la bordure est quasi invisible
                    // VIOLATION [AA] 2.3
            )
    }

    // ✅ Bon — icône informative avec contraste suffisant (≥ 3:1)
    var conformantIcon: some View {
        Image(systemName: "bell.fill")
            .foregroundStyle(Color(hex: "#767676")) // ✅ 4.5:1 sur blanc
            .font(.title2)
        // [AA] ✅ Critère 2.3
    }

    // ❌ Mauvais — icône trop claire — VIOLATION [AA] 2.3
    var nonConformantIcon: some View {
        Image(systemName: "bell.fill")
            .foregroundStyle(Color(hex: "#BBBBBB")) // ❌ ~1.8:1 sur blanc
            .font(.title2)
    }

    // ✅ Bon — checkbox custom avec bordure visible
    var conformantCheckbox: some View {
        Button(action: { isChecked.toggle() }) {
            HStack {
                RoundedRectangle(cornerRadius: 4)
                    .stroke(Color(hex: "#767676"), lineWidth: 2) // ✅ ≥ 3:1 sur fond blanc
                    .frame(width: 22, height: 22)
                    .overlay(
                        isChecked ? Image(systemName: "checkmark")
                            .foregroundStyle(Color.blue)
                            .font(.system(size: 14, weight: .bold)) : nil
                    )
                Text("J'accepte les conditions")
            }
        }
        .buttonStyle(.plain)
        // [AA] ✅ La case à cocher est visible même non cochée (≥ 3:1)
    }
}

// MARK: - 5. REDUCE CONTRAST / TRANSPARENCY [A]

struct AccessibilityPreferencePatterns: View {
    @Environment(\.accessibilityReduceTransparency) private var reduceTransparency
    @Environment(\.colorScheme) private var colorScheme

    // ✅ Bon — fond glassmorphism adaptatif selon préférence
    var adaptiveGlassmorphism: some View {
        Text("Contenu")
            .padding()
            .background {
                if reduceTransparency {
                    // ✅ Fond opaque si "Réduire la transparence" est activé
                    Color(UIColor.systemBackground)
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                } else {
                    // Glassmorphism classique pour les autres utilisateurs
                    RoundedRectangle(cornerRadius: 12)
                        .fill(.ultraThinMaterial)
                }
            }
        // Réglages > Accessibilité > Affichage et taille du texte > Réduire la transparence
    }

    // ✅ Bon — vérifier si le contraste augmenté est activé
    // (iOS : Réglages > Accessibilité > Augmenter le contraste)
    // SwiftUI expose .accessibilityDifferentiateWithoutColor et .accessibilityReduceTransparency
    // mais pas directement "Increase Contrast"
    // → Utiliser UIAccessibility.isDarkerSystemColorsEnabled
    var increaseContrastAdaptation: some View {
        let isHighContrast = UIAccessibility.isDarkerSystemColorsEnabled
        return Text("Texte")
            .foregroundStyle(
                isHighContrast
                    ? .black                           // ✅ Contraste maximum si demandé
                    : Color(hex: "#767676")             // Normal conforme
            )
    }
}

// MARK: - 6. DARK MODE — CONFORMITÉ COMPLÈTE [AA]

struct DarkModeContrastPatterns: View {
    @Environment(\.colorScheme) private var colorScheme

    // ✅ Bon — composant conforme en light ET dark
    var conformantInBothModes: some View {
        VStack(spacing: 12) {
            // .primary = noir (light) / blanc (dark) — toujours conforme
            Text("Titre")
                .font(.headline)
                .foregroundStyle(.primary)

            // Fond adaptatif
            RoundedRectangle(cornerRadius: 12)
                .fill(Color(UIColor.secondarySystemBackground))
                // Light: #F2F2F7 | Dark: #2C2C2E
                // Les couleurs système sont conçues pour être conformes

            // Bouton primaire avec texte blanc
            Button("Action principale") {}
                .buttonStyle(.borderedProminent)
                // Material Design / iOS : vérifié pour être conforme dans les deux modes
        }
    }

    // ❌ Mauvais — fond et texte custom qui se fondent en dark mode
    var badDarkModeColors: some View {
        Text("Ceci peut devenir illisible")
            .foregroundStyle(Color(hex: "#1A1A1A"))  // Très sombre en light ✅
            .background(Color(hex: "#2D2D2D"))        // En dark mode : texte ≈ fond → ❌
        // ratio #1A1A1A sur #2D2D2D = ~1.3:1 → VIOLATION [AA] 2.2
    }
}

// MARK: - 7. DALTONISME — PATTERNS MULTI-MODAUX [A]

struct ColorblindFriendlyPatterns: View {

    // ✅ Bon — palette Okabe-Ito (daltonisme-friendly)
    let okabeIto = [
        ("Bleu", Color(hex: "#0072B2")),       // Distinguable par tous
        ("Orange", Color(hex: "#E69F00")),     // Distinguable par tous
        ("Vert cyan", Color(hex: "#009E73")),  // OK pour deutéranopes
        ("Violet", Color(hex: "#CC79A7")),     // OK pour protanopes
        ("Ocre", Color(hex: "#D55E00")),       // OK pour tritanopes
    ]

    // ✅ Bon — indicateurs d'état multi-modaux
    var multiModalStatusIndicators: some View {
        VStack(alignment: .leading, spacing: 12) {

            // ✅ Succès : icône ✓ + couleur verte + texte
            HStack(spacing: 8) {
                Image(systemName: "checkmark.circle.fill")
                    .foregroundStyle(Color(hex: "#009E73")) // Vert daltonisme-friendly
                    .accessibilityHidden(true)
                Text("Paiement accepté")
                    .foregroundStyle(.primary)
            }
            .accessibilityElement(children: .combine)
            .accessibilityLabel("Succès : Paiement accepté")

            // ✅ Erreur : icône ✗ + couleur rouge + texte
            HStack(spacing: 8) {
                Image(systemName: "xmark.circle.fill")
                    .foregroundStyle(Color(hex: "#D55E00")) // Orange-rouge daltonisme-friendly
                    .accessibilityHidden(true)
                Text("Carte refusée")
                    .foregroundStyle(.primary)
            }
            .accessibilityElement(children: .combine)
            .accessibilityLabel("Erreur : Carte refusée")

            // ✅ Warning : icône ⚠ + couleur orange + texte
            HStack(spacing: 8) {
                Image(systemName: "exclamationmark.triangle.fill")
                    .foregroundStyle(Color(hex: "#E69F00")) // Jaune-orange
                    .accessibilityHidden(true)
                Text("Vérifiez vos informations")
                    .foregroundStyle(.primary)
            }
            .accessibilityElement(children: .combine)
            .accessibilityLabel("Avertissement : Vérifiez vos informations")
        }
    }
}

// MARK: - Utilitaire — Extension Color pour hex

extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 3: (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6: (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8: (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default: (a, r, g, b) = (1, 1, 1, 0)
        }
        self.init(.sRGB, red: Double(r) / 255, green: Double(g) / 255, blue: Double(b) / 255, opacity: Double(a) / 255)
    }
}
