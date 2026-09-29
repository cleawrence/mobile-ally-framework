---
tags:
  - guides
  - ci
---

# Intégration CI/CD

FRAM fournit des workflows GitHub Actions prêts à l'emploi pour bloquer les PRs contenant des violations d'accessibilité critiques.

## Architecture

```mermaid
graph LR
    PR["Pull Request"] --> iOS["SwiftLint<br/>17 règles a11y"]
    PR --> Android["Compose Lint<br/>7 détecteurs"]
    iOS --> Report["Rapport"]
    Android --> Report
    Report --> Score["Score [A] / [AA]"]
    Score -->|"[A] ≥ 90%"| Merge["✅ Merge autorisé"]
    Score -->|"[A] < 90%"| Block["❌ Merge bloqué"]
```

---

## GitHub Actions — iOS

Copier le fichier dans `.github/workflows/` :

```bash
cp mobile-ally-framework/linting/ci/a11y-lint-ios.yml .github/workflows/
```

!!! info "Comportement"
    - Se déclenche sur les PRs modifiant des fichiers `.swift`
    - Exécute SwiftLint avec les 17 règles FRAM
    - **Bloque la PR** si des violations de sévérité `Error` (niveau [A]) sont détectées
    - Publie un résumé dans le **PR Summary**
    - Archive le rapport JSON en artifact (30 jours)

### Complément optionnel : `a11y-check`

[`a11y-check`](https://github.com/cvs-health/ios-swiftui-accessibility-techniques) (CVS Health, Apache 2.0) est un analyseur statique SwiftUI qui complète SwiftLint : 45 règles mappées sur WCAG 2.2, score de 0 à 100, sortie SARIF pour le code scanning GitHub.

```bash
# Installation (selon le README du projet)
brew tap cvs-health/ios-swiftui-accessibility-techniques https://github.com/cvs-health/ios-swiftui-accessibility-techniques.git
brew install --HEAD cvs-health/ios-swiftui-accessibility-techniques/a11y-check

# Analyse (depuis la racine du projet)
a11y-check . --no-trend --only error
a11y-check . --no-trend --min-score 80      # code de sortie non nul sous le seuil
a11y-check . --no-trend --format sarif > results.sarif
```

!!! warning "Toujours ajouter `--no-trend`"
    Par défaut, l'outil écrit un fichier de suivi `.a11y-scores.json` dans les dossiers analysés. `--no-trend` évite d'ajouter des fichiers au projet.

!!! note "Ce que l'outil ne remplace pas"
    - Il ne couvre que **SwiftUI** (pas UIKit) et raisonne en **WCAG 2.2**, pas en RAAM 1.1 : les niveaux [A] / [AA] de FRAM ne s'y retrouvent pas tels quels.
    - Analyse statique uniquement : les ratios de contraste, l'information portée par la couleur seule ou l'association d'un libellé à ses valeurs (tableaux) ne sont pas vérifiés.
    - Des faux positifs sont possibles (par exemple des `Text` en `.subheadline` signalés comme titres) : relire les résultats avant de bloquer une PR.

---

## GitHub Actions — Android

```bash
cp mobile-ally-framework/linting/ci/a11y-lint-android.yml .github/workflows/
```

!!! info "Comportement"
    - Se déclenche sur les PRs modifiant des fichiers `.kt` / `.kts`
    - Exécute `./gradlew lint` incluant les 7 détecteurs FRAM
    - Extrait les issues FRAM du rapport XML
    - **Bloque la PR** si des violations `Error` sont trouvées
    - Archive le rapport Lint (XML + HTML)

---

## Rapport de conformité

Le script `a11y-report.sh` génère un rapport combiné JSON + HTML :

```bash
# Générer après les deux lints
./linting/ci/a11y-report.sh \
    --ios swiftlint-report.json \
    --android app/build/reports/lint-results-debug.xml \
    -o ./rapport-a11y/
```

Sortie :

| Fichier | Format | Contenu |
|---|---|---|
| `a11y-report.json` | JSON | Scores machine-readable, nombre de violations par niveau |
| `a11y-report.html` | HTML | Dashboard visuel avec barres de progression et tableau des règles |

---

## Seuils recommandés

| Environnement | Score [A] minimum | Score [AA] minimum | Action si sous le seuil |
|---|---|---|---|
| **PR** | 100% (0 error) | — | Bloquer le merge |
| **Nightly** | ≥ 90% | ≥ 70% | Notification Slack |
| **Release** | 100% | ≥ 85% | Bloquer le déploiement |

!!! warning "Règle d'or"
    **Aucune violation de niveau [A] ne doit être fusionnée.** Ces violations rendent l'application inaccessible aux utilisateurs de lecteurs d'écran.
