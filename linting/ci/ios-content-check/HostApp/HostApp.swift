// FRAM — Application hôte minimale pour le target UI Testing.
//
// Xcode exige qu'un bundle UI Testing cible une vraie application ("Target to be
// Tested") pour pouvoir la lancer via XCUIApplication(). Ce hôte n'a aucun rapport
// avec les patterns d'accessibilité des skills : c'est un point d'ancrage vide,
// nécessaire uniquement pour que `xcodebuild build-for-testing` puisse résoudre
// réellement `import XCTest` / `XCUIApplication` / `XCUIElement` dans les fichiers
// skills/*/tests/ios-tests.swift — chose que `swiftc` seul ne peut pas faire.

import SwiftUI

@main
struct HostApp: App {
    var body: some Scene {
        WindowGroup {
            Text("FRAM — hôte de vérification UI Testing")
        }
    }
}
