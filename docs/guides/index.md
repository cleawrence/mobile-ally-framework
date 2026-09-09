---
tags:
  - guides
  - installation
---

# Démarrage rapide

Bienvenue dans **FRAM** ! Ce guide vous accompagne de l'installation à votre premier audit.

## Prérequis

| Plateforme | Version minimale | Outil |
|---|---|---|
| :material-apple: iOS | Xcode 15+ / iOS 16+ | SwiftUI |
| :material-android: Android | Android Studio Hedgehog+ / API 24+ | Jetpack Compose (Material3) |

---

## Installation

### 1. Cloner le framework

```bash
git clone https://github.com/votre-org/mobile-a11y-framework.git
```

### 2. Configurer le linting

=== ":material-apple: iOS — SwiftLint"

    ```bash
    # Installer SwiftLint
    brew install swiftlint

    # Copier la config dans votre projet iOS
    cp mobile-a11y-framework/linting/swiftlint/.swiftlint.yml ./MonProjetIOS/
    ```

    Puis dans Xcode : **Build Phases → + → New Run Script Phase** :

    ```bash
    if which swiftlint > /dev/null; then
      swiftlint --config .swiftlint.yml
    fi
    ```

=== ":material-android: Android — Compose Lint"

    ```kotlin title="settings.gradle.kts"
    include(":fram-a11y-lint")
    project(":fram-a11y-lint").projectDir =
        file("../mobile-a11y-framework/linting/compose-lint")
    ```

    ```kotlin title="build.gradle.kts (module app)"
    dependencies {
        lintChecks(project(":fram-a11y-lint"))
    }
    ```

!!! success "Vérification"
    Après installation, compilez votre projet. Les violations d'accessibilité apparaîtront directement dans l'IDE.
