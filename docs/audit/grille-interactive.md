---
tags:
  - audit
  - grille
---

# Grille d'audit interactive

La grille d'audit FRAM est un fichier HTML autonome contenant les **70+ critères** du RAAM 1.1 avec scoring en temps réel.

## Fonctionnalités

| Fonctionnalité | Description |
|---|---|
| :material-filter: **Filtrage par niveau** | Afficher uniquement [A] ou [AA] |
| :material-filter-variant: **Filtrage par statut** | Non testé / Conforme / Non conforme / N/A |
| :material-chart-donut: **Scoring live** | Score [A], [AA] et global avec anneaux animés |
| :material-content-save: **Persistance** | Sauvegarde automatique dans localStorage |
| :material-download: **Export JSON** | Télécharger le rapport complet |
| :material-printer: **Impression** | Mode print CSS intégré |

## Utilisation

### Ouvrir la grille

```bash
# Ouvrir dans le navigateur par défaut
open audit/grille-audit.html        # macOS
xdg-open audit/grille-audit.html    # Linux
```

### Remplir la grille

1. Renseigner le **nom de l'application** et la **date** dans la barre de contrôle
2. Sélectionner le **niveau** : Audit simplifié [A] ou Audit complet [AA]
3. Pour chaque critère :
    - Cocher la case = **Conforme** ✅
    - Utiliser le sélecteur de statut pour **Non conforme** ❌ ou **N/A**
4. Les scores se mettent à jour en temps réel

### Exporter

Cliquer **📥 Exporter JSON** pour télécharger le rapport :

```json
{
  "framework": "FRAM v1.0",
  "referentiel": "RAAM 1.1",
  "application": "MonApp",
  "date": "2026-07-29",
  "resultats": {
    "SKILL-01": {
      "nom": "Éléments Graphiques",
      "criteres": [
        { "ref": "1.1", "text": "...", "level": "a", "status": "conforme" }
      ]
    }
  }
}
```

!!! tip "Reprendre un audit"
    La grille **sauvegarde automatiquement** votre progression dans le navigateur (localStorage). Fermez et rouvrez — tout est conservé.

!!! warning "Changement de navigateur"
    Les données sont stockées dans le localStorage du navigateur. Si vous changez de navigateur, exportez d'abord en JSON.
