# FRAM — Linting Statique Accessibilité Mobile
**Version** : 1.0 — Juillet 2026  
**Couverture** : SwiftLint (iOS/SwiftUI) + Android Lint (Jetpack Compose)

---

## Vue d'ensemble

Le linting statique FRAM détecte automatiquement les violations d'accessibilité les plus fréquentes **au moment de l'écriture du code**, avant même de compiler. Il couvre environ **40% des violations de niveau [A]** détectables statiquement.

```
mobile-ally-framework/linting/
├── swiftlint/
│   ├── .swiftlint.yml              ← Config principale + 17 règles custom (copier à la racine du projet iOS)
│   └── README.md
├── compose-lint/
│   ├── build.gradle.kts            ← Module Gradle à inclure dans le projet Android
│   ├── src/main/java/com/fram/lint/checks/
│   │   ├── A11yLintRegistry.kt     ← Registre des règles
│   │   ├── IconContentDescriptionDetector.kt
│   │   ├── ClickableWithoutSemanticsDetector.kt
│   │   ├── HardcodedColorDetector.kt
│   │   └── TextFieldWithoutLabelDetector.kt
│   └── src/test/java/com/fram/lint/
│       └── A11yLintChecksTest.kt
├── ci/
│   ├── a11y-lint-ios.yml           ← GitHub Actions iOS
│   ├── a11y-lint-android.yml       ← GitHub Actions Android
│   └── a11y-report.sh              ← Script rapport de conformité
└── README.md                       ← Ce fichier
```

---

## Règles SwiftLint — Résumé

| ID Règle | Sévérité | Skill | Critère RAAM | Description |
|---|---|---|---|---|
| `a11y_image_missing_label` | error | SKILL-01 | 1.2 [A] | `Image(systemName:)` sans `.accessibilityHidden` ni `.accessibilityLabel` |
| `a11y_image_decorative_explicit` | warning | SKILL-01 | 1.1 [A] | `Image` sans décision explicite décoration/information |
| `a11y_label_starts_with_image` | warning | SKILL-01 | 1.3 [A] | Label qui commence par "image de" ou "icône de" |
| `a11y_button_empty_label` | error | SKILL-05 | 5.2 [A] | `Button` dont le label est vide ou un seul caractère spécial |
| `a11y_tap_gesture_without_semantics` | warning | SKILL-05 | 5.1 [A] | `.onTapGesture` sans `.accessibilityElement` ni role |
| `a11y_textfield_placeholder_only` | error | SKILL-09 | 9.1 [A] | `TextField` avec placeholder uniquement (pas de label visible) |
| `a11y_error_color_only` | warning | SKILL-02 | 2.1 [A] | `.foregroundStyle(.red)` sans icône ni texte d'accompagnement |
| `a11y_hardcoded_color` | warning | SKILL-02 | 2.2 [AA] | `Color(hex:)` ou `Color(red:green:blue:)` hardcodé |
| `a11y_fixed_font_size` | warning | SKILL-03 | 3.3 [AA] | `.font(.system(size: N))` avec taille fixe |
| `a11y_missing_navigation_title` | warning | SKILL-10 | 10.1 [A] | Vue sans `.navigationTitle()` dans une `NavigationStack` |

---

## Règles Android Lint — Résumé

| ID Règle | Sévérité | Skill | Critère RAAM | Description |
|---|---|---|---|---|
| `IconMissingContentDescription` | Error | SKILL-01 | 1.2 [A] | `Icon()` sans `contentDescription` (ni null ni string) |
| `ClickableWithoutSemantics` | Error | SKILL-05 | 5.1 [A] | `Modifier.clickable` sans `Modifier.semantics { role; contentDescription }` |
| `HardcodedAccessibilityColor` | Warning | SKILL-02 | 2.2 [AA] | `Color(0xFF...)` hardcodé hors MaterialTheme.colorScheme |
| `TextFieldMissingLabel` | Error | SKILL-09 | 9.1 [A] | `TextField`/`OutlinedTextField` sans paramètre `label` |
| `EmptyContentDescription` | Error | SKILL-01 | 1.2 [A] | `contentDescription = ""` (vide — TalkBack lit le nom de la fonction) |
| `FixedTextSizeDp` | Warning | SKILL-03 | 3.3 [AA] | Taille de texte en `dp` au lieu de `sp` |
| `MissingAccessibilityRole` | Warning | SKILL-05 | 5.1 [A] | `Modifier.clickable` sans `role` dans semantics |

---

## Installation rapide

### iOS
```bash
# 1. Installer SwiftLint
brew install swiftlint

# 2. Copier la config dans votre projet iOS
cp mobile-ally-framework/linting/swiftlint/.swiftlint.yml ./MonProjet/

# 3. Ajouter à Xcode Build Phases
# + New Run Script Phase :
# if which swiftlint > /dev/null; then
#   swiftlint --config .swiftlint.yml
# fi
```

### Android
```kotlin
// settings.gradle.kts — inclure le module lint
include(":fram-a11y-lint")
project(":fram-a11y-lint").projectDir = file("../mobile-ally-framework/linting/compose-lint")

// build.gradle.kts (module app)
dependencies {
    lintChecks(project(":fram-a11y-lint"))
}
```

---

## Score de conformité

Le script `ci/a11y-report.sh` génère un rapport JSON + HTML avec :
- Score `[A]` : violations des règles de niveau A
- Score `[AA]` : violations des règles de niveau AA
- Détail par fichier avec lien vers le skill concerné

```json
{
  "conformite": {
    "niveau_A": { "score": 87, "violations": 13 },
    "niveau_AA": { "score": 72, "violations": 28 }
  }
}
```
