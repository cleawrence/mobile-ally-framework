import SwiftUI

// MARK: - 1. DÉCLARATION D'ACCESSIBILITÉ IN-APP [A]

/// Vue SwiftUI: Écran de Déclaration d'Accessibilité
struct AccessibilityStatementView: View {
    let frameworkVersion = "FRAM 1.0"
    let conformanceLevel = "Totalement conforme"
    let updateDate = "24 Juillet 2026"
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text("Déclaration d'accessibilité")
                    .font(.largeTitle)
                    .accessibilityAddTraits(.isHeader)
                
                Text("Nous nous engageons à rendre notre application accessible à tous, conformément aux normes du RAAM 1.1 et RGAA 4.1.")
                
                VStack(alignment: .leading, spacing: 8) {
                    Text("État de conformité")
                        .font(.headline)
                        .accessibilityAddTraits(.isHeader)
                    
                    Text("L'application est **\(conformanceLevel)** avec les critères du RAAM niveau A et AA.")
                    Text("Dernière mise à jour : \(updateDate)")
                    Text("Propulsé par \(frameworkVersion)")
                }
                .padding()
                .background(Color.secondary.opacity(0.1))
                .cornerRadius(8)
                
                Text("Retour d'information et contact")
                    .font(.headline)
                    .accessibilityAddTraits(.isHeader)
                
                Text("Si vous n'arrivez pas à accéder à un contenu ou à un service, vous pouvez nous contacter pour être orienté vers une alternative accessible ou obtenir le contenu sous une autre forme.")
                
                Link(destination: URL(string: "https://example.com/a11y-contact")!) {
                    Label("Signaler un problème d'accessibilité", systemImage: "envelope.fill")
                        .padding()
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(8)
                }
                .accessibilityHint("Ouvre le formulaire de contact dans le navigateur web.")
            }
            .padding()
        }
        .navigationTitle("Accessibilité")
        .navigationBarTitleDisplayMode(.inline)
    }
}

// MARK: - 2. VOICE CONTROL (Contrôle vocal iOS) [AA]

struct VoiceControlOptimizedButton: View {
    var body: some View {
        Button(action: {
            // Action pour sauvegarder
        }) {
            HStack {
                Image(systemName: "square.and.arrow.down")
                Text("Enregistrer")
            }
            .padding()
            .background(Color.green)
            .foregroundColor(.white)
            .cornerRadius(8)
        }
        // [AA] Assure que Voice Control puisse réagir à "Appuyer sur Enregistrer"
        // Le label principal est lu par VoiceOver. Les inputLabels sont pour Voice Control.
        .accessibilityLabel("Enregistrer le document")
        .accessibilityInputLabels(["Enregistrer", "Sauvegarder", "Enregistrer le document"])
    }
}

// MARK: - 3. SWITCH CONTROL (Accès par bouton) [AA]

struct SwitchControlFriendlyRow: View {
    let itemTitle: String
    let onDelete: () -> Void
    
    var body: some View {
        HStack {
            Text(itemTitle)
            Spacer()
            // Ce bouton serait difficile à atteindre si noyé dans la cellule
            // Mais grâce à accessibilityAction, on l'expose au niveau du parent.
            Button(action: onDelete) {
                Image(systemName: "trash")
                    .foregroundColor(.red)
            }
            // Le bouton interne est caché de l'A11y car l'action est reportée sur la ligne
            .accessibilityHidden(true)
        }
        .padding()
        // Combine tout en un seul élément focusable pour Switch Control
        .accessibilityElement(children: .combine)
        .accessibilityLabel(itemTitle)
        // [AA] Action accessible exposée pour VoiceOver et Switch Control
        .accessibilityAction(named: "Supprimer") {
            onDelete()
        }
        // Indique formellement que l'élément répond aux interactions utilisateurs custom (Switch Control)
        .accessibilityRespondsToUserInteraction(true)
    }
}

// MARK: - 4. APIS COMPLÈTES VOICEOVER [AA]

struct ComprehensiveAccessibilityView: View {
    @State private var value: Double = 50.0
    
    var body: some View {
        VStack(spacing: 20) {
            // Label, Hint, Value, Traits
            VStack {
                Text("Volume")
                Slider(value: $value, in: 0...100)
            }
            .accessibilityElement(children: .ignore)
            .accessibilityLabel("Volume sonore")
            .accessibilityValue("\(Int(value)) pourcent")
            .accessibilityHint("Ajustez pour modifier le volume de l'application")
            .accessibilityAddTraits(.updatesFrequently)
            
            // Custom Content & Identifier (pour le test)
            Text("Données météo")
                .accessibilityIdentifier("weather_data_text")
                .accessibilityCustomContent("Température ressentie", "24 degrés")
                .accessibilityCustomContent("Risque de pluie", "10%")
            
            // Sort Priority & Activation Point
            HStack {
                Text("Bouton de validation")
                    .padding()
                    .background(Color.blue)
                    .accessibilitySortPriority(1) // Sera lu en premier dans ce groupe
                    .accessibilityActivationPoint(CGPoint(x: 50, y: 50)) // Utile si l'UI custom est décalée
            }
        }
    }
}

// MARK: - 5. EXEMPLE D'ÉCRAN DE DÉCLARATION
// (Déjà fourni en 1: AccessibilityStatementView)
