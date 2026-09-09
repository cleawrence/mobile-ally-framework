# SKILL-05 — Composants Interactifs

**Statut** : v1.0 — Couverture SwiftUI + Jetpack Compose  
**Dernière révision** : Juillet 2026  
**Auteur** : FRAM — Framework Référence Accessibilité Mobile

---

## Critères couverts

| Critère RAAM 1.1 | Intitulé | Niveau | Correspondance |
|---|---|---|---|
| 5.1 | Chaque composant interactif a un nom accessible | `[A]` | WCAG 4.1.2, EN 301 549 §11.5.2.5 |
| 5.2 | Le nom accessible est pertinent | `[A]` | WCAG 4.1.2 |
| 5.3 | Chaque composant a un rôle | `[A]` | WCAG 4.1.2, EN 301 549 §11.5.2.6 |
| 5.4 | Les changements d'état sont restitués | `[A]` | WCAG 4.1.2, EN 301 549 §11.5.2.9 |
| 5.5 | La valeur courante est restituée | `[A]` | WCAG 4.1.2 |
| 5.6 | Les gestes complexes ont une alternative | `[AA]` | WCAG 2.5.1, EN 301 549 §11.2.5.1 |
| 5.7 | Taille de la zone de toucher minimale | `[AA]` | WCAG 2.5.5, EN 301 549 §11.2.5.5 |
| 5.8 | Le focus est visible | `[AA]` | WCAG 2.4.7 |
| 5.9 | L'ordre de focus est logique | `[A]` | WCAG 2.4.3 |
| 5.10 | Les composants identiques ont des noms cohérents | `[AA]` | WCAG 3.2.4 |
| 5.11 | Les composants sont opérables au clavier / switch | `[AA]` | WCAG 2.1.1 |
| 5.12 | Pas de piège au clavier / focus | `[A]` | WCAG 2.1.2 |

---

## Pourquoi c'est important

Les composants interactifs sont le cœur de toute application. Un bouton sans label, un switch
sans état restitué ou une zone de toucher trop petite rendent l'application **inutilisable** pour :
- Les **utilisateurs aveugles** (VoiceOver / TalkBack) qui naviguent à l'ouïe
- Les **utilisateurs à mobilité réduite** utilisant Switch Control ou Voice Control
- Les **utilisateurs ayant une déficience motrice fine** (cibles trop petites)

> **Règle d'or** : tout élément interactif doit avoir un **nom, un rôle et un état**
> correctement exposés aux technologies d'assistance.

---

## ✅ À faire / ❌ À éviter

| ✅ À faire | ❌ À éviter |
|---|---|
| Label descriptif en contexte ("Supprimer l'article Chaussures") | Label générique ("Supprimer", "OK") |
| Rôle natif via composant SwiftUI/Compose | `onTapGesture {}` / `clickable {}` sans rôle |
| Annoncer les changements d'état | Changer visuellement sans notifier l'AT |
| Zone de toucher ≥ 44pt (iOS) / ≥ 48dp (Android) | Bouton icône de 20×20pt non agrandi |
| Alternative aux gestes swipe, pinch, drag | Fonctionnalité accessible uniquement via geste |
| Texte du label != redondance du rôle | Préfixer "Bouton :" (le rôle est déjà annoncé) |

---

## Composants couverts

1. Button / IconButton
2. Toggle / Switch
3. Checkbox
4. RadioButton
5. Slider / ProgressBar
6. Tab / SegmentedControl
7. TextField (focus et rôle)
8. FAB / ExtendedFAB
9. Chip
10. Dialog / BottomSheet (gestion du focus)
11. Gestes complexes et alternatives

Voir les patterns complets dans :
- **iOS SwiftUI** → [`ios-swiftui/patterns.swift`](./ios-swiftui/patterns.swift)
- **Android Compose** → [`android-compose/patterns.kt`](./android-compose/patterns.kt)

---

## Vérification manuelle

### Protocole VoiceOver (iOS)

1. Activer VoiceOver : *Réglages > Accessibilité > VoiceOver* (ou triple-clic)
2. Naviguer par glissement gauche/droite
3. Pour chaque composant, vérifier que VoiceOver annonce :
   - **Nom** : label descriptif en contexte
   - **Rôle** : "bouton", "interrupteur", "curseur", "onglet"…
   - **Valeur/État** : "activé", "coché", "42%"…
4. Activer (double-tap) et vérifier l'annonce du changement
5. Tester les **actions personnalisées** (balayer vers le haut → menu d'actions)
6. Vérifier que la zone de tap est confortable (pas de raté)

### Protocole TalkBack (Android)

1. Activer TalkBack : *Paramètres > Accessibilité > TalkBack*
2. Naviguer par glissement gauche/droite
3. Pour chaque composant, vérifier que TalkBack annonce :
   - **Nom** : `contentDescription` ou label visible fusionné
   - **Rôle** : "bouton", "interrupteur", "case à cocher"…
   - **Valeur/État** : "activé", "coché", "50%"…
4. Activer (double-tap) et vérifier l'annonce du changement
5. Accéder aux **actions personnalisées** (menu des actions TalkBack)
6. Vérifier la taille de cible via Layout Inspector

### Outils automatiques

```bash
# iOS — Accessibility Inspector (macOS inclus avec Xcode)
# Xcode > Open Developer Tool > Accessibility Inspector
# → Run Audit sur l'écran courant

# Android — Accessibility Scanner
# Play Store : installer "Accessibility Scanner"
# → Activer, naviguer dans l'app, consulter les rapports

# Android — Layout Inspector (Android Studio)
# View > Tool Windows > Layout Inspector
# → Inspecter les Semantics nodes dans l'arbre de vues
```

---

## Checklist Niveau A — Audit Simplifié `[A]`

> Tous ces items doivent être validés pour la conformité minimale.

### 5.1 et 5.2 — Nom accessible et pertinence
- [ ] Chaque bouton a un label non vide (pas `""`, pas `null`)
- [ ] Les boutons icône seuls ont un label explicite (`.accessibilityLabel` / `contentDescription`)
- [ ] Le label donne le contexte ("Supprimer l'article Jean slim" et non juste "Supprimer")
- [ ] Le label ne contient pas le rôle ("Valider" et non "Bouton Valider")
- [ ] Les labels ne commencent pas par "image de", "icône de" (redondance)

### 5.3 — Rôle
- [ ] Les éléments cliquables custom ont un rôle déclaré (Button, Checkbox, Switch…)
- [ ] Les zones `onTapGesture` / `clickable` non sémantiques ont `.isButton` / `Role.Button`

### 5.4 et 5.5 — État et valeur
- [ ] Les Toggles/Switches annoncent "activé" ou "désactivé"
- [ ] Les Checkboxes annoncent "cochée" ou "décochée"
- [ ] Les Sliders annoncent la valeur courante (ex : "50%", "3 étoiles")
- [ ] Les onglets / tabs indiquent lequel est sélectionné
- [ ] Les boutons désactivés sont identifiables comme tels

### 5.9 — Ordre de focus
- [ ] L'ordre de navigation VoiceOver/TalkBack est logique (haut→bas, gauche→droite)
- [ ] Un composant apparu dynamiquement reçoit le focus ou est annoncé

### 5.12 — Pas de piège au focus
- [ ] On peut toujours quitter un composant (sheet, popover, dialog) avec VoiceOver/TalkBack
- [ ] Les modales capturent le focus à l'ouverture et le restituent à la fermeture

---

## Checklist Niveau AA — Audit Complet `[AA]`

> Tout le niveau A **+** les éléments ci-dessous.

### 5.6 — Gestes complexes alternatifs
- [ ] Les actions swipe (supprimer, archiver) ont une alternative via menu contextuel accessible
- [ ] Les drag & drop ont des actions alternatives (monter/descendre)
- [ ] Les pinch/zoom ont des boutons + / - accessibles
- [ ] Les carousels ont des boutons suivant/précédent

### 5.7 — Taille de cible
- [ ] Toutes les zones de toucher sont ≥ 44×44pt (iOS)
- [ ] Toutes les zones de toucher sont ≥ 48×48dp (Android)
- [ ] Les zones petites visuellement utilisent `contentShape(Rectangle())` / `minimumInteractiveComponentSize()`

### 5.8 — Focus visible
- [ ] L'anneau de focus VoiceOver/TalkBack est visible sur tous les composants
- [ ] Le composant focusé est toujours visible à l'écran (non masqué)

### 5.10 — Cohérence des noms
- [ ] Les boutons effectuant la même action ont le même label dans toute l'app
- [ ] Les icônes identiques ont des labels identiques dans toute l'app

### 5.11 — Switch Control / Switch Access
- [ ] Tous les composants sont activables via Switch Control (iOS)
- [ ] Tous les composants sont activables via Switch Access (Android)
- [ ] L'ordre de scanning est logique

---

## Évolution v2 — Cross-Platform

> 🔜 À activer après stabilisation v1

- Flutter : [`_evolution/flutter.dart.future`](./_evolution/flutter.dart.future)
- React Native : [`_evolution/react-native.tsx.future`](./_evolution/react-native.tsx.future)

---

*Références : [RAAM 1.1 §5](https://accessibilite.public.lu/fr/raam1.1/referentiel-technique.html#topic-5) · [WCAG 4.1.2](https://www.w3.org/Translations/WCAG21-fr/#name-role-value) · [WCAG 2.5.1](https://www.w3.org/Translations/WCAG21-fr/#pointer-gestures) · [APPT](https://appt.org/en/guidelines/wcag/success-criterion-4-1-2)*
