import SwiftUI

// MARK: - 1. INFO PAR FORME/TAILLE/POSITION [A] — Critère 3.1
// ❌ Mauvais: Instructions dépendant de l'apparence
struct BadInstructionView: View {
    var body: some View {
        Text("Cliquez sur le bouton vert en haut à droite pour continuer.")
    }
}

// ✅ Bon: Instructions basées sur la fonction ou le texte
struct GoodInstructionView: View {
    var body: some View {
        Text("Appuyez sur le bouton 'Continuer' pour passer à l'étape suivante.")
    }
}


// MARK: - 2. ORIENTATION [AA] — Critère 3.2
// Pattern: layout adaptatif avec size class
struct AdaptiveOrientationView: View {
    @Environment(\.horizontalSizeClass) var horizontalSizeClass
    @Environment(\.verticalSizeClass) var verticalSizeClass
    
    var body: some View {
        Group {
            // Si l'écran est large (ex: iPad ou iPhone en paysage)
            if horizontalSizeClass == .regular {
                HStack(alignment: .top, spacing: 20) {
                    Image(systemName: "photo.artframe")
                        .resizable()
                        .scaledToFit()
                        .frame(maxWidth: 300)
                    
                    Text("Description détaillée de l'image qui s'affiche très bien en mode paysage avec de la place sur la droite.")
                }
            } else {
                // Mode portrait standard (iPhone)
                VStack(spacing: 16) {
                    Image(systemName: "photo.artframe")
                        .resizable()
                        .scaledToFit()
                    
                    Text("Description détaillée de l'image qui s'affiche sous la photo en mode portrait pour utiliser toute la largeur.")
                }
            }
        }
        .padding()
    }
}


// MARK: - 3. DYNAMIC TYPE [AA] — Critère 3.3
// ❌ Mauvais: Tailles fixes
struct BadDynamicTypeView: View {
    var body: some View {
        HStack {
            Image(systemName: "star.fill")
                .font(.system(size: 20)) // Fixe
            Text("Favori")
                .font(.system(size: 16)) // Fixe
        }
        .frame(height: 44) // Hauteur fixe, coupe le texte si très grand
    }
}

// ✅ Bon: @ScaledMetric et polices sémantiques
struct GoodDynamicTypeView: View {
    @ScaledMetric(relativeTo: .body) var iconSize: CGFloat = 24
    
    var body: some View {
        HStack(alignment: .center) {
            Image(systemName: "star.fill")
                .font(.system(size: iconSize))
            Text("Favori")
                .font(.body) // S'adapte au réglage système
        }
        .frame(minHeight: 44) // minHeight au lieu de height fixe
        .padding(.vertical, 8)
    }
}


// MARK: - 4. REFLOW [AA] — Critère 3.4
// Le contenu ne doit pas avoir de scroll horizontal à grande taille
// Pattern: ViewThatFits ou Layout dynamique
struct ReflowViewThatFits: View {
    var body: some View {
        // ViewThatFits choisira le premier layout qui rentre sans déborder
        ViewThatFits(in: .horizontal) {
            // Essaie d'abord en horizontal
            HStack {
                ActionButtons()
            }
            
            // Si pas assez de place (ex: grande police), passe en vertical
            VStack(alignment: .leading) {
                ActionButtons()
            }
        }
    }
}

struct ActionButtons: View {
    var body: some View {
        Group {
            Button("Accepter les conditions") {}
                .buttonStyle(.borderedProminent)
            Button("Refuser") {}
                .buttonStyle(.bordered)
        }
    }
}


// MARK: - 5. EXAMPLE COMPLET — Carte adaptive Dynamic Type
struct AdaptiveCardView: View {
    let title: String
    let description: String
    
    @ScaledMetric(relativeTo: .title3) var iconSize: CGFloat = 30
    @Environment(\.dynamicTypeSize) var dynamicTypeSize
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            // Entête
            if dynamicTypeSize > .accessibility2 {
                // Reflow total pour très grandes tailles
                VStack(alignment: .leading, spacing: 8) {
                    Image(systemName: "newspaper.fill")
                        .font(.system(size: iconSize))
                        .foregroundColor(.blue)
                        .accessibilityHidden(true)
                    
                    Text(title)
                        .font(.title3.bold())
                }
            } else {
                HStack(alignment: .top) {
                    Image(systemName: "newspaper.fill")
                        .font(.system(size: iconSize))
                        .foregroundColor(.blue)
                        .accessibilityHidden(true)
                    
                    Text(title)
                        .font(.title3.bold())
                    
                    Spacer()
                }
            }
            
            Text(description)
                .font(.body)
                .foregroundColor(.secondary)
                .fixedSize(horizontal: false, vertical: true) // Force le wrap multiline
        }
        .padding()
        .background(Color(uiColor: .secondarySystemGroupedBackground))
        .cornerRadius(12)
        // Limiter la taille maximale si l'UI casse vraiment, mais idéalement supporter tout
        .dynamicTypeSize(...DynamicTypeSize.accessibility3) 
    }
}

// Previews pour tester les extrêmes
struct AdaptiveCardView_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            AdaptiveCardView(title: "Nouvelle importante", description: "Voici le détail de la nouvelle, qui s'affiche sur plusieurs lignes.")
                .previewDisplayName("Taille Normale")
            
            AdaptiveCardView(title: "Nouvelle importante", description: "Voici le détail de la nouvelle, qui s'affiche sur plusieurs lignes.")
                .environment(\.dynamicTypeSize, .accessibility3)
                .previewDisplayName("Accessibilité 3 (Très Grand)")
        }
        .padding()
        .previewLayout(.sizeThatFits)
    }
}
