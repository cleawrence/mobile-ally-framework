---
tags:
  - linting
  - compose-lint
  - android
---

# Compose Lint — Android

## Architecture

Le module `fram-a11y-lint` est un module Gradle Java/Kotlin qui enregistre **7 détecteurs UAST** analysant l'arbre syntaxique Kotlin en temps réel.

```
compose-lint/
├── build.gradle.kts
└── src/
    ├── main/java/com/fram/lint/
    │   ├── A11yLintRegistry.kt                  ← Registre central
    │   └── checks/
    │       ├── IconContentDescriptionDetector.kt  ← SKILL-01
    │       ├── HardcodedColorDetector.kt          ← SKILL-02
    │       ├── FixedTextSizeDetector.kt           ← SKILL-03
    │       ├── ClickableWithoutSemanticsDetector.kt ← SKILL-05
    │       ├── TextFieldWithoutLabelDetector.kt   ← SKILL-09
    │       ├── NavigationSemanticsDetector.kt     ← SKILL-10
    │       └── TouchTargetDetector.kt             ← SKILL-11
    └── test/java/com/fram/lint/
        └── A11yLintChecksTest.kt                  ← 10 tests unitaires
```

---

## Installation

=== "settings.gradle.kts"

    ```kotlin
    include(":fram-a11y-lint")
    project(":fram-a11y-lint").projectDir =
        file("../mobile-ally-framework/linting/compose-lint")
    ```

=== "build.gradle.kts (module :app)"

    ```kotlin
    dependencies {
        lintChecks(project(":fram-a11y-lint"))
    }
    ```

---

## Détecteurs

### `IconContentDescriptionDetector` — SKILL-01

| Issue ID | Sévérité | Détecte |
|---|---|---|
| `IconMissingContentDescription` | :material-alert-circle:{ .text-red } Error | `Icon()` sans `contentDescription` |
| `EmptyContentDescription` | :material-alert-circle:{ .text-red } Error | `contentDescription = ""` (vide ≠ null) |
| `RedundantContentDescriptionPrefix` | :material-alert:{ .text-yellow } Warning | `contentDescription` commençant par "image de" |

!!! example "Exemple de détection"
    ```kotlin
    // ❌ Déclenche IconMissingContentDescription
    Icon(Icons.Default.Warning) // (1)

    // ✅ Correct — décoratif
    Icon(Icons.Default.Star, contentDescription = null)

    // ✅ Correct — informatif
    Icon(Icons.Default.Warning, contentDescription = "Attention")
    ```

    1. TalkBack lit "Warning" (nom de la ressource) au lieu d'une description utile.

### `ClickableWithoutSemanticsDetector` — SKILL-05

| Issue ID | Sévérité | Détecte |
|---|---|---|
| `ClickableWithoutSemantics` | :material-alert-circle:{ .text-red } Error | `Modifier.clickable` sans `semantics` |
| `ClickableMissingRole` | :material-alert:{ .text-yellow } Warning | `semantics` sans `role` |
| `EmptySemanticsContentDescription` | :material-alert-circle:{ .text-red } Error | `contentDescription = ""` dans semantics |

!!! note "Smart exclusion"
    Les composables Material3 natifs (`Button`, `IconButton`, `TextButton`, `OutlinedButton`) sont **automatiquement exclus** — ils ont la sémantique intégrée.

### `HardcodedColorDetector` — SKILL-02

| Issue ID | Sévérité | Détecte |
|---|---|---|
| `HardcodedAccessibilityColor` | :material-alert:{ .text-yellow } Warning | `Color(0xFF...)` hors MaterialTheme |
| `ColorOnlyStatusCommunication` | :material-alert:{ .text-yellow } Warning | `Color.Red` / `Color.Green` sans icône |

!!! tip "Fichiers de thème exclus"
    Les fichiers contenant "Theme", "Color", "Colors" ou "Palette" dans leur nom sont automatiquement ignorés.

### `FixedTextSizeDetector` — SKILL-03

| Issue ID | Sévérité | Détecte |
|---|---|---|
| `FixedTextSizeDp` | :material-alert:{ .text-yellow } Warning | `fontSize` en `dp` au lieu de `sp` |

### `TextFieldWithoutLabelDetector` — SKILL-09

| Issue ID | Sévérité | Détecte |
|---|---|---|
| `TextFieldMissingLabel` | :material-alert-circle:{ .text-red } Error | `TextField`/`OutlinedTextField` sans `label` |
| `TextFieldEmptyLabel` | :material-alert-circle:{ .text-red } Error | `label = { Text("") }` vide |

### `NavigationSemanticsDetector` — SKILL-10

| Issue ID | Sévérité | Détecte |
|---|---|---|
| `MissingScreenTitle` | :material-alert:{ .text-yellow } Warning | `Scaffold` sans `topBar` ni `heading()` |

### `TouchTargetDetector` — SKILL-11

| Issue ID | Sévérité | Détecte |
|---|---|---|
| `SmallTouchTarget` | :material-alert:{ .text-yellow } Warning | Zone de touche < 48dp |

!!! tip "Auto-fix"
    Ajouter `.minimumInteractiveComponentSize()` dans la chaîne Modifier pour garantir 48dp.

---

## Tests

```bash
cd linting/compose-lint
./gradlew test
```

Les 10 tests vérifient :

- [x] Détection correcte des violations
- [x] Absence de faux positifs sur le code correct
- [x] Exclusion des composables Material3 natifs
