# SKILL-02 — Couleurs et Contrastes

**Statut** : v1.0 — Couverture SwiftUI + Jetpack Compose  
**Dernière révision** : Juillet 2026  
**Auteur** : FRAM — Framework Référence Accessibilité Mobile

---

## Critères couverts

| Critère RAAM 1.1 | Intitulé | Niveau | Correspondance |
|---|---|---|---|
| 2.1 | L'information n'est pas donnée uniquement par la couleur | `[A]` | WCAG 1.4.1, EN 301 549 §11.1.4.1 |
| 2.2 | Rapport de contraste du texte suffisant | `[AA]` | WCAG 1.4.3, EN 301 549 §11.1.4.3 |
| 2.3 | Rapport de contraste des composants d'interface suffisant | `[AA]` | WCAG 1.4.11, EN 301 549 §11.1.4.11 |

---

## Pourquoi c'est important

Les problèmes de couleur et de contraste affectent une part bien plus large que les seuls utilisateurs aveugles :
- **Daltonisme** : environ **8 % des hommes** et 0,5 % des femmes ont une déficience chromatique (deutéranopie, protanopie, tritanopie) — une information "rouge = erreur, vert = succès" est invisible pour eux
- **Basse vision** : un texte gris clair sur fond blanc (`#888888` sur `#FFFFFF` = ratio 3.5:1) est illisible pour des millions d'utilisateurs
- **Environnement lumineux** : un écran en plein soleil ou sous un éclairage fort réduit le contraste perçu de 50 à 80%
- **Mode sombre** : des couleurs hardcodées qui passent en light mode peuvent devenir illisibles en dark mode

> **Règle d'or** :
> - Jamais d'information **uniquement** par la couleur → toujours doubler par icône, forme ou texte
> - Texte normal → ratio **≥ 4.5:1** sur le fond
> - Texte large (≥ 18pt ou ≥ 14pt gras) → ratio **≥ 3:1**
> - Composants UI (bordures, icônes) → ratio **≥ 3:1**

---

## Comprendre les ratios de contraste

Le **rapport de contraste** est calculé selon la formule WCAG : `(L1 + 0.05) / (L2 + 0.05)` où L1 est la luminance relative la plus élevée.

| Ratio | Niveau | Application |
|---|---|---|
| **21:1** | Maximum | Noir sur blanc |
| **≥ 7:1** | AAA | Texte normal (niveau supérieur) |
| **≥ 4.5:1** | AA ✅ | Texte normal (≤ 18pt ou ≤ 14pt gras) |
| **≥ 3:1** | AA ✅ | Texte large (≥ 18pt ou ≥ 14pt gras) + composants UI |
| **< 3:1** | ❌ | Non conforme pour tout usage |

### Texte "large" en mobile

| Plateforme | Texte normal | Texte large |
|---|---|---|
| iOS (pt) | < 18pt (ou < 14pt bold) | ≥ 18pt **ou** ≥ 14pt bold |
| Android (sp) | < 18sp (ou < 14sp bold) | ≥ 18sp **ou** ≥ 14sp bold |
| WCAG (px à 96dpi) | < 24px (ou < 18.67px bold) | ≥ 24px **ou** ≥ 18.67px bold |

---

## Types de daltonisme et solutions

| Type | Prévalence | Couleurs confondues | Solution |
|---|---|---|---|
| **Deutéranopie** | ~5% hommes | Rouge / Vert | Icône ✓/✗ + texte |
| **Protanopie** | ~1% hommes | Rouge / Vert (rouge perçu noir) | Forme + texte |
| **Tritanopie** | ~0.01% | Bleu / Jaune | Patron/hachures |
| **Achromatopsie** | Rare | Toutes couleurs | Niveaux de gris + forme |

**Palette daltonisme-friendly** : bleu (#0072B2), orange (#E69F00), vert cyan (#009E73), violet (#CC79A7) — palette Okabe-Ito recommandée.

---

## ✅ À faire / ❌ À éviter

| ✅ À faire | ❌ À éviter |
|---|---|
| Icône ✓ + texte "Validé" + couleur verte | Pastille verte seule pour "succès" |
| Bordure rouge + icône ⚠️ + texte erreur | Bordure rouge seule pour erreur |
| Lien souligné + couleur bleue | Lien distingué uniquement par couleur |
| `.foregroundStyle(.primary)` adaptatif | `Color(hex: "#555")` hardcodé |
| Tester en dark mode + light mode | Tester uniquement en light mode |
| `MaterialTheme.colorScheme.error` | `Color.Red` hardcodé |
| Contraste ≥ 4.5:1 texte normal | Gris #888 sur blanc #FFF (3.5:1) |
| Contraste ≥ 3:1 pour icônes et bordures | Icône gris clair sur fond blanc |

---

## Exemples de ratios courants

| Combinaison | Ratio | Conforme ? |
|---|---|---|
| #000000 sur #FFFFFF | 21:1 | ✅ AA + AAA |
| #333333 sur #FFFFFF | 12.6:1 | ✅ AA + AAA |
| **#767676 sur #FFFFFF** | **4.5:1** | ✅ AA minimum texte normal |
| #888888 sur #FFFFFF | 3.5:1 | ❌ Texte normal, ✅ texte large |
| #AAAAAA sur #FFFFFF | 2.3:1 | ❌ Non conforme |
| #0000FF sur #FFFFFF | 8.6:1 | ✅ Lien bleu sur blanc |
| #FF0000 sur #FFFFFF | 4.0:1 | ❌ Texte normal (rouge sur blanc) |
| #D32F2F sur #FFFFFF | 5.1:1 | ✅ Rouge sombre conforme |

---

## Vérification manuelle

### Outils de mesure du contraste

```bash
# iOS — Accessibility Inspector (Xcode)
# Xcode > Open Developer Tool > Accessibility Inspector
# → Run Audit > Contrast Issues

# iOS 17+ — performAccessibilityAudit
try app.performAccessibilityAudit(for: [.contrast])

# macOS — Colour Contrast Analyser (gratuit, CCA)
# https://www.tpgi.com/color-contrast-checker/
# Pipette sur l'écran → affiche le ratio en temps réel

# Android — Accessibility Scanner (Play Store)
# → Signale les problèmes de contraste insuffisant

# Android — Android Studio → Lint
# Analyze > Inspect Code → "Color Contrast" warnings

# Web — Chrome DevTools ou Figma Contrast Plugin
# Pour vérifier les maquettes avant développement
```

### Protocole de test manuel

1. **Vérifier en Light mode** : tous les textes et icônes ont-ils un contraste suffisant ?
2. **Basculer en Dark mode** : les éléments sont-ils toujours lisibles ?
3. **Augmenter la taille de police** (Dynamic Type / Accessibility Size) : les textes restent-ils lisibles ?
4. **Activer "Augmenter le contraste"** (iOS) / **"Supprimer les animations"** : l'interface s'adapte-t-elle ?
5. **Simuler le daltonisme** (iOS: Réglages > Accessibilité > Contenu affiché > Filtres de couleur) : les informations d'état restent-elles compréhensibles ?

---

## Checklist Niveau A — Audit Simplifié `[A]`

### 2.1 — Information non transmise uniquement par la couleur
- [ ] Les états success/erreur/warning utilisent **icône + couleur + texte** (jamais couleur seule)
- [ ] Les champs en erreur ont une **bordure + icône + message textuel** (pas seulement une bordure rouge)
- [ ] Les liens sont distingués du texte courant par **soulignement ou icône**, pas uniquement par couleur
- [ ] Les éléments "actif/inactif" ou "sélectionné/non sélectionné" ont un indicateur visuel non-couleur (forme, poids, position)
- [ ] Les graphiques de données utilisent des **formes ou hachures différentes** en plus des couleurs distinctes
- [ ] Les badges de statut (online/offline, nouveau/lu) incluent un texte ou une forme différente
- [ ] Les notifications, alertes et indicateurs urgents ne s'appuient pas uniquement sur la couleur rouge

---

## Checklist Niveau AA — Audit Complet `[AA]`

### 2.2 — Contraste texte
- [ ] Tout texte de corps (< 18pt ou < 14pt gras) a un ratio ≥ **4.5:1** sur son fond
- [ ] Tout texte large (≥ 18pt ou ≥ 14pt gras) a un ratio ≥ **3:1** sur son fond
- [ ] Les placeholders de champs de saisie ont un ratio ≥ **4.5:1** (souvent violé)
- [ ] Les textes d'aide (`supportingText`, caption) ont un ratio ≥ **4.5:1**
- [ ] Les textes sur images/photos ont un ratio ≥ **4.5:1** (superposition vérifiée)
- [ ] En Dark mode, tous les contrastes texte restent conformes
- [ ] Les boutons désactivés : si lisibles, ratio ≥ **4.5:1** (sinon accepter l'opacité réduite si clairement non interactif)

### 2.3 — Contraste composants UI
- [ ] Les **bordures de champs de saisie** ont un ratio ≥ **3:1** vs le fond de page
- [ ] Les **icônes informatives** (état, navigation) ont un ratio ≥ **3:1**
- [ ] Les **indicateurs de focus** (anneau VoiceOver, outline clavier) ont un ratio ≥ **3:1**
- [ ] Les **cases à cocher et boutons radio** non cochés ont un ratio ≥ **3:1** (leur contour est visible)
- [ ] Les **tabs, segments, chips** ont leur état actif/inactif distinguable à ≥ **3:1**
- [ ] Les **dividers** séparant du contenu fonctionnel ont un ratio ≥ **3:1**

---

## Évolution v2 — Cross-Platform

> 🔜 À activer après stabilisation v1

- Flutter : [`_evolution/flutter.dart.future`](./_evolution/flutter.dart.future)
- React Native : [`_evolution/react-native.tsx.future`](./_evolution/react-native.tsx.future)

---

*Références : [RAAM 1.1 §2](https://accessibilite.public.lu/fr/raam1.1/referentiel-technique.html#topic-2) · [WCAG 1.4.1](https://www.w3.org/Translations/WCAG21-fr/#use-of-color) · [WCAG 1.4.3](https://www.w3.org/Translations/WCAG21-fr/#contrast-minimum) · [WCAG 1.4.11](https://www.w3.org/Translations/WCAG21-fr/#non-text-contrast) · [Colour Contrast Analyser TPGI](https://www.tpgi.com/color-contrast-checker/)*
