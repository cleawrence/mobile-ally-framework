---
tags:
  - guides
  - installation
---

# Installation détaillée

## Structure du framework

```
mobile-a11y-framework/
├── skills/            ← 12 skills × 5 fichiers
├── linting/           ← SwiftLint + Compose Lint
├── audit/             ← Grille d'audit interactive
├── docs/              ← Ce site de documentation
└── README.md
```

## iOS — SwiftLint

### Étape 1 : Installer SwiftLint

```bash
brew install swiftlint
```

!!! note "Alternative Homebrew"
    Si vous n'utilisez pas Homebrew, SwiftLint est aussi disponible via [SPM](https://github.com/realm/SwiftLint#swift-package-manager) ou [Mint](https://github.com/yonaskolb/Mint).

### Étape 2 : Copier la configuration

```bash
cp mobile-a11y-framework/linting/swiftlint/.swiftlint.yml ./MonProjet/
```

### Étape 3 : Intégrer à Xcode

1. Ouvrir votre projet dans Xcode
2. Sélectionner votre target → **Build Phases**
3. **+** → **New Run Script Phase**
4. Coller :

```bash
if which swiftlint > /dev/null; then
  swiftlint --config .swiftlint.yml
fi
```

### Étape 4 : Vérifier

Compiler (++cmd+b++). Les violations apparaissent comme warnings/errors dans Xcode.

!!! tip "Personnaliser"
    Pour désactiver une règle spécifique dans un fichier :
    ```swift
    // swiftlint:disable a11y_fixed_font_size
    .font(.system(size: 32)) // Cas exceptionnel justifié
    // swiftlint:enable a11y_fixed_font_size
    ```

---

## Android — Compose Lint

### Étape 1 : Ajouter le module

=== "settings.gradle.kts"

    ```kotlin
    include(":fram-a11y-lint")
    project(":fram-a11y-lint").projectDir =
        file("../mobile-a11y-framework/linting/compose-lint")
    ```

=== "settings.gradle (Groovy)"

    ```groovy
    include ':fram-a11y-lint'
    project(':fram-a11y-lint').projectDir =
        new File("../mobile-a11y-framework/linting/compose-lint")
    ```

### Étape 2 : Déclarer la dépendance

```kotlin title="build.gradle.kts (module :app)"
dependencies {
    lintChecks(project(":fram-a11y-lint"))
}
```

### Étape 3 : Exécuter

```bash
./gradlew lint
# ou pour un rapport HTML :
./gradlew lintDebug
```

Les résultats sont dans `app/build/reports/lint-results-debug.html`.

### Étape 4 : Configurer la sévérité

```xml title="lint.xml (optionnel — à la racine du module)"
<?xml version="1.0" encoding="UTF-8"?>
<lint>
    <!-- Passer un warning en error pour bloquer la CI -->
    <issue id="SmallTouchTarget" severity="error" />
    <!-- Ignorer temporairement une règle -->
    <issue id="HardcodedAccessibilityColor" severity="ignore" />
</lint>
```

!!! warning "Ne pas ignorer les Error"
    Les règles de sévérité `Error` correspondent au niveau `[A]`. Les ignorer revient à accepter une violation critique.

---

## Vérification de l'installation

Après installation, créez un fichier de test :

=== "SwiftUI"

    ```swift title="A11yTest.swift"
    import SwiftUI

    struct A11yTest: View {
        var body: some View {
            // ❌ Doit déclencher a11y_image_systemname_no_a11y
            Image(systemName: "star")

            // ❌ Doit déclencher a11y_empty_accessibility_label
            Button("") { }
                .accessibilityLabel("")
        }
    }
    ```

=== "Jetpack Compose"

    ```kotlin title="A11yTest.kt"
    @Composable
    fun A11yTest() {
        // ❌ Doit déclencher IconMissingContentDescription
        Icon(Icons.Default.Star)

        // ❌ Doit déclencher ClickableWithoutSemantics
        Box(modifier = Modifier.clickable { })
    }
    ```

Si les warnings/errors apparaissent → l'installation est réussie :material-check-circle:{ .text-green }.
