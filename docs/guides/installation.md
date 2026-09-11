---
tags:
  - guides
  - installation
---

# Installation détaillée

## Structure du framework

```
mobile-ally-framework/
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
cp mobile-ally-framework/linting/swiftlint/.swiftlint.yml ./MonProjet/
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
        file("../mobile-ally-framework/linting/compose-lint")
    ```

=== "settings.gradle (Groovy)"

    ```groovy
    include ':fram-a11y-lint'
    project(':fram-a11y-lint').projectDir =
        new File("../mobile-ally-framework/linting/compose-lint")
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

## Skill Claude Code (agents IA)

FRAM distribue aussi un [skill](https://code.claude.com/docs/en/skills) condensé — les 12 thématiques résumées en règles + un exemple SwiftUI/Compose vérifié chacune — pour qu'un agent (Claude Code, ou tout agent compatible) applique spontanément les bonnes pratiques d'accessibilité en écrivant du code, sans avoir à ouvrir les 12 `SKILL.md` un par un.

### Installer

```bash
npx skills add https://github.com/cleawrence/mobile-ally-framework
```

L'outil [`skills`](https://skills.sh/) scanne tout le repo et propose `mobile-a11y` (les `SKILL.md` des 12 thématiques dans `skills/` sont ignorés — ce sont des docs, pas des skills agent).

### Utiliser

Le skill se déclenche automatiquement selon le contexte (dès que vous écrivez du SwiftUI/Compose), mais peut aussi être invoqué explicitement :

```
/mobile-a11y                # applique le pense-bête au code en cours
/mobile-a11y formulaires    # va directement à une thématique (nom ou numéro, FR ou EN)
/mobile-a11y audit <path>   # audite un fichier contre les 12 thématiques, remonte les vraies violations
```

### Mettre à jour

```bash
npx skills update mobile-a11y
```

!!! note "Version condensée, pas la source de vérité"
    Le skill est un résumé pour un usage agent. Pour l'exhaustivité — critères complets, patterns ❌ commentés, règles de lint — les [12 skills](../skills/index.md) de ce site restent la référence.

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
