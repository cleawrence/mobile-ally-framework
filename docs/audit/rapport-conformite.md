---
tags:
  - audit
  - rapport
---

# Rapport de conformité

Le script `a11y-report.sh` génère un rapport combinant les résultats du linting automatique (SwiftLint + Android Lint) en un score de conformité [A] / [AA].

## Utilisation

```bash
./linting/ci/a11y-report.sh \
    --ios swiftlint-report.json \
    --android app/build/reports/lint-results-debug.xml \
    -o ./rapport/
```

## Sortie

| Fichier | Format | Usage |
|---|---|---|
| `a11y-report.json` | JSON | Intégration CI, métriques, dashboards |
| `a11y-report.html` | HTML | Visualisation, impression, archivage |

## Format JSON

```json
{
  "framework": "FRAM — Framework Référence Accessibilité Mobile",
  "referentiel": "RAAM 1.1",
  "date": "2026-07-29",
  "conformite": {
    "niveau_A": {
      "score": 87,
      "violations": 13,
      "regles_verifiees": 20,
      "label": "Audit simplifié"
    },
    "niveau_AA": {
      "score": 72,
      "violations": 28,
      "regles_verifiees": 10,
      "label": "Audit complet"
    }
  }
}
```

## Dashboard HTML

Le rapport HTML inclut :

- 🔵 Score [A] avec barre de progression
- 🟢 Score [AA] avec barre de progression
- Tableau des règles vérifiées avec sévérité
- Code couleur : vert (≥ 80) / jaune (≥ 50) / rouge (< 50)

!!! note "Calcul du score"
    `Score = 100 - (violations × 2)`, plafonné entre 0 et 100. Ce calcul heuristique sera affiné avec des données réelles d'utilisation.
