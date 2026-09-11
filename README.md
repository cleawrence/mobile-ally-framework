# FRAM — Framework Référence Accessibilité Mobile

<p align="center">
    <strong>SwiftUI · Jetpack Compose · RAAM 1.1 · WCAG 2.1 · EN 301 549</strong>
</p>

---

## Qu'est-ce que FRAM ?

FRAM est un framework interne de référence pour l'**accessibilité mobile native**. Il fournit :

- **12 skills** couvrant les 12 thématiques du [référentiel RAAM 1.1](https://accessibilite.public.lu/fr/raam1.1/referentiel-technique.html)
- **Patterns SwiftUI** et **Jetpack Compose** prêts à copier-coller
- **Tests automatisés** (XCUITest + Compose UI Test)
- **Linting statique** avec 17 règles SwiftLint + 7 détecteurs Android Lint
- **Grille d'audit interactive** avec scoring [A] / [AA]
- **Pipeline CI** GitHub Actions

---

## Structure

```
mobile-ally-framework/
├── skills/                          ← 12 skills × 5 fichiers = 60 fichiers
│   ├── SKILL-01-elements-graphiques/
│   │   ├── SKILL.md                 ← Doc : critères, À faire/éviter, checklists [A]/[AA]
│   │   ├── ios-swiftui/patterns.swift   ← Patterns SwiftUI ✅/❌
│   │   ├── android-compose/patterns.kt  ← Patterns Compose Material3
│   │   └── tests/
│   │       ├── ios-tests.swift          ← XCUITest + performAccessibilityAudit
│   │       └── android-tests.kt        ← Compose UI Test + semantics assertions
│   ├── SKILL-02-couleurs-contrastes/
│   ├── SKILL-03-multimedia/             (Dynamic Type, orientation, Reflow)
│   ├── SKILL-04-tableaux-listes/
│   ├── SKILL-05-composants-interactifs/
│   ├── SKILL-06-elements-obligatoires/  (Langue, titres d'écran)
│   ├── SKILL-07-structuration/
│   ├── SKILL-08-elements-temporels/
│   ├── SKILL-09-formulaires/
│   ├── SKILL-10-navigation/
│   ├── SKILL-11-consultation/
│   └── SKILL-12-documentation/
│
├── linting/                         ← Linting statique
│   ├── swiftlint/
│   │   ├── .swiftlint.yml          ← 17 règles custom (copier à la racine du projet iOS)
│   │   └── README.md
│   ├── compose-lint/
│   │   ├── build.gradle.kts        ← Module Gradle (7 détecteurs)
│   │   ├── src/main/java/com/fram/lint/
│   │   │   ├── A11yLintRegistry.kt
│   │   │   └── checks/             ← 7 détecteurs par skill
│   │   └── src/test/java/com/fram/lint/
│   │       └── A11yLintChecksTest.kt
│   ├── ci/
│   │   ├── a11y-lint-ios.yml       ← GitHub Actions iOS (à copier chez le consommateur)
│   │   ├── a11y-lint-android.yml   ← GitHub Actions Android (à copier chez le consommateur)
│   │   ├── a11y-report.sh          ← Script rapport de conformité JSON + HTML
│   │   ├── check-mermaid.py        ← Valide les diagrammes Mermaid des docs (rendu réel navigateur)
│   │   ├── android-content-check/  ← Harness interne : compile patterns.kt + android-tests.kt en place
│   │   └── ios-content-check/      ← Harness interne : compile ios-tests.swift dans un vrai target XCUITest
│   └── README.md
│
├── audit/
│   └── grille-audit.html           ← Grille d'audit interactive (70+ critères, scoring live)
│
├── .github/workflows/               ← CI interne au repo FRAM (voir « Développement & CI » ci-dessous)
│
└── README.md                       ← Ce fichier
```

---

## Niveaux de conformité

| Niveau | Usage | Critères |
|---|---|---|
| **[A]** | **Audit simplifié** — base obligatoire | ~45 critères |
| **[AA]** | **Audit complet** — conformité cible | ~25 critères supplémentaires |

Chaque skill tag ses patterns `[A]` ou `[AA]`. Les linters marquent les violations comme `Error` (A) ou `Warning` (AA).

---

## Démarrage rapide

### 1. Explorer les skills

Chaque `SKILL.md` est autonome. Commencer par le skill le plus pertinent :

| Vous développez... | Commencez par |
|---|---|
| Un écran avec des images/icônes | [SKILL-01](skills/SKILL-01-elements-graphiques/SKILL.md) |
| Un formulaire de login/inscription | [SKILL-09](skills/SKILL-09-formulaires/SKILL.md) |
| Des boutons, toggles, actions | [SKILL-05](skills/SKILL-05-composants-interactifs/SKILL.md) |
| La navigation (tab bar, modales) | [SKILL-10](skills/SKILL-10-navigation/SKILL.md) |
| Un thème couleur / dark mode | [SKILL-02](skills/SKILL-02-couleurs-contrastes/SKILL.md) |

### 2. Copier les patterns

Chaque fichier `patterns.swift` / `patterns.kt` contient du code prêt à l'emploi :

```swift
// ✅ Bon — SwiftUI (SKILL-01)
Image(systemName: "exclamationmark.triangle")
    .accessibilityLabel("Avertissement : connexion instable")
```

```kotlin
// ✅ Bon — Compose (SKILL-01)
Icon(
    Icons.Default.Warning,
    contentDescription = "Avertissement : connexion instable"
)
```

### 3. Activer le linting

**iOS :**
```bash
brew install swiftlint
cp linting/swiftlint/.swiftlint.yml ./MonProjetIOS/
```

**Android :**
```kotlin
// settings.gradle.kts
include(":fram-a11y-lint")
project(":fram-a11y-lint").projectDir = file("path/to/linting/compose-lint")

// build.gradle.kts (module app)
dependencies { lintChecks(project(":fram-a11y-lint")) }
```

### 4. Auditer

Ouvrir `audit/grille-audit.html` dans un navigateur. Cocher les critères, obtenir un score.

---

## Référentiel RAAM 1.1 — Mapping complet

| Section RAAM | Skill FRAM | Critères [A] | Critères [AA] |
|---|---|---|---|
| 1. Éléments graphiques | SKILL-01 | 7 | 2 |
| 2. Couleurs | SKILL-02 | 1 | 8 |
| 3. Multimédia / Adaptation | SKILL-03 | 1 | 5 |
| 4. Tableaux | SKILL-04 | 3 | 1 |
| 5. Composants interactifs | SKILL-05 | 5 | 1 |
| 6. Éléments obligatoires | SKILL-06 | 4 | 0 |
| 7. Structuration | SKILL-07 | 2 | 2 |
| 8. Éléments temporels | SKILL-08 | 6 | 3 |
| 9. Formulaires | SKILL-09 | 5 | 1 |
| 10. Navigation | SKILL-10 | 3 | 3 |
| 11. Consultation | SKILL-11 | 3 | 3 |
| 12. Documentation | SKILL-12 | 2 | 2 |

---

## Conventions

- **Documentation** en français 🇫🇷
- **Identifiants de code** en anglais
- **Patterns** : `✅ Bon` / `❌ Mauvais` avec explication du pourquoi
- **Tags** : `[A]` audit simplifié, `[AA]` audit complet
- **Plateformes** : SwiftUI (iOS 16+) et Jetpack Compose (Material3)
- **Cross-platform** (Flutter, React Native, KMP) : 🔜 Évolution v2

---

## Développement & CI

Le repo FRAM suit un workflow **gitflow** : `main` (production, déployé sur GitHub Pages) ← `develop` (intégration) ← `feature/*`. `main` est protégé — PR obligatoire (même pour les admins), pas de force-push, pas de suppression de branche.

Cinq workflows GitHub Actions valident chaque changement avant merge :

| Workflow | Déclencheur (chemins) | Ce qu'il vérifie |
|---|---|---|
| `ios-skills.yml` | `skills/**/ios-swiftui/**`, `skills/**/tests/ios-tests.swift`, `.swiftlint.yml` | SwiftLint (17 règles `a11y_*` doivent toutes se déclencher au moins une fois sur le corpus) + `swiftc -typecheck` de chaque `patterns.swift` + compilation réelle de chaque `ios-tests.swift` dans un vrai target XCUITest (`linting/ci/ios-content-check`) |
| `android-skills.yml` | `skills/**/android-compose/**`, `skills/**/tests/android-tests.kt` | Compilation réelle de `patterns.kt` + `android-tests.kt` (`linting/ci/android-content-check`) |
| `compose-lint.yml` | `linting/compose-lint/**` | Tests des 7 détecteurs Android Lint (17 tests unitaires) |
| `docs.yml` | `docs/**`, `mkdocs.yml` | `mkdocs build --strict` + validation de chaque diagramme Mermaid dans un vrai moteur mermaid.js (Chromium headless) |
| `pages.yml` | `docs/**`, `mkdocs.yml` (push sur `main`) | Build + déploiement GitHub Pages |

Les workflows sont **path-filtrés** : une PR qui ne touche aucun des chemins ci-dessus ne déclenche aucun check. C'est pourquoi la protection de branche sur `main` n'exige pas de check spécifique — vérifier manuellement que les checks pertinents à la PR sont verts avant de merger.

Les harnesses `linting/ci/*-content-check/` ne font pas partie du framework distribué (les consommateurs n'en ont pas besoin) : ils compilent les vrais fichiers `skills/*/...` en place — sans copie — pour garantir qu'aucun exemple de pattern ou de test ne casse silencieusement.

---

## Outils complémentaires

| Outil | Plateforme | Usage |
|---|---|---|
| Accessibility Inspector | Xcode | Inspecter les labels, traits, hiérarchie |
| `performAccessibilityAudit()` | iOS 17+ | Audit automatique dans les XCUITests |
| Accessibility Scanner | Android | Scan automatique des problèmes |
| Layout Inspector | Android Studio | Vérifier l'arbre sémantique Compose |
| Color Contrast Analyzer | macOS/Windows | Vérifier les ratios de contraste |

---

## Références

- [RAAM 1.1 — Référentiel technique](https://accessibilite.public.lu/fr/raam1.1/referentiel-technique.html)
- [Orange — Guidelines Android](https://a11y-guidelines.orange.com/fr/mobile/android/)
- [Orange — Guidelines iOS](https://a11y-guidelines.orange.com/fr/mobile/ios/)
- [WCAG 2.1 (Français)](https://www.w3.org/Translations/WCAG21-fr/)
- [EN 301 549 v3.2.1](https://www.etsi.org/deliver/etsi_en/301500_301599/301549/03.02.01_60/en_301549v030201p.pdf)
- [Apple — Accessibility for SwiftUI](https://developer.apple.com/documentation/swiftui/accessibility)
- [Android — Accessibility in Compose](https://developer.android.com/develop/ui/compose/accessibility)

---

**FRAM v1.0** — Juillet 2026  
*Usage interne — Phase de test*
