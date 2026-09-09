# SKILL-08 — Éléments Temporels

**Statut** : v1.0 — Couverture SwiftUI + Jetpack Compose  
**Dernière révision** : Juillet 2026  
**Auteur** : FRAM — Framework Référence Accessibilité Mobile

---

## Critères couverts

| Critère RAAM 1.1 | Intitulé | Niveau | Correspondance |
|---|---|---|---|
| 8.1 | Média audio/vidéo sans image : transcription textuelle accessible | `[A]` | WCAG 1.2.1, EN 301 549 §11.1.2.1 |
| 8.2 | Média synchronisé pré-enregistré : sous-titres pour sourds/malentendants | `[A]` | WCAG 1.2.2, EN 301 549 §11.1.2.2 |
| 8.3 | Média synchronisé pré-enregistré : audiodescription disponible | `[AA]` | WCAG 1.2.5, EN 301 549 §11.1.2.5 |
| 8.4 | Média en direct : sous-titres en temps réel | `[AA]` | WCAG 1.2.4, EN 301 549 §11.1.2.4 |
| 8.5 | Contenus en mouvement automatiques : contrôles pause/arrêt | `[A]` | WCAG 2.2.2, EN 301 549 §11.2.2.2 |
| 8.6 | Animations respectent les préférences Reduce Motion | `[A]` | WCAG 2.3.1, 2.3.3, EN 301 549 §11.2.3.1 |

---

## Pourquoi c'est important

| Population | Impact sans accessibilité |
|---|---|
| **Sourds / malentendants** | Vidéo sans sous-titres = contenu totalement inaccessible |
| **Aveugles / malvoyants** | Vidéo sans audiodescription = actions visuelles perdues |
| **Troubles vestibulaires** | Parallaxe, zoom, rotations peuvent provoquer nausées et vertiges réels |
| **Épilepsie photosensible** | Plus de 3 flashs/seconde peut déclencher une crise — risque vital |
| **TDAH / troubles cognitifs** | Animations continues fragmentent l'attention et bloquent la lecture |
| **Sans son** (transport, bureau, bébé) | Vidéo sans sous-titres = contexte inadapté |

---

## ✅ À faire / ❌ À éviter

| ✅ À faire | ❌ À éviter |
|---|---|
| Sous-titres (CC) sur toute vidéo avec parole | Vidéo parlée sans sous-titres |
| Transcription textuelle pour tout audio seul | Podcast/audio sans texte alternatif |
| Sous-titres fermés (CC) — paramétrables | Sous-titres incrustés (burn-in) non paramétrables |
| Audiodescription pour les vidéos avec info visuelle | Vidéo où des informations cruciales sont uniquement visuelles |
| Respecter `accessibilityReduceMotion` (iOS) / `TRANSITION_ANIMATION_SCALE = 0` (Android) | Animation spring intense sans option de désactivation |
| Remplacer slide/spring par fade si Reduce Motion actif | Parallaxe, zoom profond toujours actifs |
| Bouton Pause/Stop accessible pour tout contenu auto | Carousel infini sans possibilité de pause |
| `accessibilityLabel` adaptatif sur bouton play/pause | Bouton play fixe (TalkBack dit "lecture" même si en pause) |
| Aucun contenu ne clignote > 3 fois/seconde | Effet stroboscopique dans les transitions |

---

## Reduce Motion — Explication détaillée

**Pourquoi c'est critique** : Les troubles vestibulaires touchent ~35% des adultes de plus de 40 ans. Une animation de parallaxe ou un zoom rapide peut provoquer des vertiges réels, des nausées, des maux de tête — rendant l'app inutilisable pendant des minutes.

### Animations sûres (Reduce Motion actif ou non)
- Fondu enchaîné (fade in/out)
- Changement de couleur progressif
- Progression de barre de chargement linéaire
- Opacité

### Animations à désactiver si Reduce Motion actif
| Animation | Risque | Alternative |
|---|---|---|
| Parallaxe | Vertiges | Supprimer |
| Spring/bounce | Inconfort | Fade |
| Translation (slide) | Inconfort | Fade |
| Rotation | Vertiges | Supprimer |
| Zoom (scale) | Nausées | Opacité |
| GIF/Lottie animé | Variable | Image statique |

### Détecter Reduce Motion

```swift
// iOS SwiftUI
@Environment(\.accessibilityReduceMotion) var reduceMotion

// iOS UIKit
UIAccessibility.isReduceMotionEnabled
```

```kotlin
// Android — lire l'échelle d'animation système
val animationScale = Settings.Global.getFloat(
    context.contentResolver,
    Settings.Global.TRANSITION_ANIMATION_SCALE,
    1f
)
val reduceMotion = animationScale == 0f
```

---

## Sous-titres — Guide d'implémentation

### Formats supportés
| Format | iOS AVPlayer | Android Media3/ExoPlayer |
|---|---|---|
| WebVTT (.vtt) | ✅ Natif | ✅ Natif |
| SRT (.srt) | ✅ Via AVAsset | ✅ Natif |
| TTML/DFXP | ⚠️ | ✅ |
| CEA-608/708 | ✅ (broadcast) | ✅ |

### Closed Captions vs Burn-in

| | Closed Captions (CC) | Burn-in (incrustés) |
|---|---|---|
| Paramétrables | ✅ Taille, police, contraste | ❌ |
| Compatibles paramètres OS | ✅ | ❌ |
| Conformité WCAG | ✅ | ⚠️ Partielle |
| **Recommandation FRAM** | **✅ Préféré** | ❌ À éviter |

### Activer les sous-titres par défaut
- iOS : respecter `AVAudioSessionMode` et les préférences sous-titres utilisateur
- Android : respecter `Settings.Secure.ACCESSIBILITY_CAPTIONING_ENABLED`

---

## Audiodescription — Guide

L'**audiodescription** est une narration audio supplémentaire décrivant les actions visuelles importantes non comprises dans le dialogue :

- "Marie entre dans la pièce et pose un couteau sur la table"
- "Le graphique montre une hausse de 40% en mars"

**Quand est-ce obligatoire [AA]** : dès que des informations visuelles (gestes, texte affiché, actions) ne sont pas verbalement décrites dans la bande son originale.

**Comment implémenter** :
- Piste audio alternative dans le fichier vidéo (multi-track)
- Bouton "Activer l'audiodescription" dans les contrôles du lecteur
- iOS : `AVMediaSelectionGroup` avec `characteristic: .auditory`
- Android Media3 : `TrackSelectionParameters` filtrant les pistes audio alternatives

---

## Vérification manuelle

### Protocole Reduce Motion

1. **iOS** : *Réglages > Accessibilité > Animation > Réduire les animations* → ON
2. **Android** : *Paramètres > Accessibilité > Couleur et mouvement > Supprimer les animations* → ON
3. Naviguer dans l'application :
   - Les transitions d'écran utilisent-elles un fondu ? (pas de glissement)
   - Les animations d'apparition sont-elles un fondu ? (pas de slide/spring)
   - Les carousels auto-play se stoppent-ils ou ont-ils un bouton Pause ?
   - Les Lottie/GIF animés sont-ils remplacés par une image statique ?

### Protocole Sous-titres / Vidéo

1. Lancer une vidéo avec contenu parlé
2. Activer les sous-titres depuis les contrôles du lecteur
3. Vérifier : les sous-titres s'affichent-ils correctement ?
4. Activer VoiceOver/TalkBack → le bouton play/pause a-t-il un label adaptatif ?
5. Vérifier le bouton audiodescription (si implémenté)
6. Vérifier les contrôles clavier/switch access

### Protocole Contenu Automatique

1. Repérer tous les carousels, bannières défilantes, vidéos en autoplay
2. Vérifier qu'un bouton Pause/Stop est présent et accessible
3. Activer VoiceOver/TalkBack → le bouton Pause est-il announcé correctement ?
4. Tester que le bouton play/pause change son label selon l'état

---

## Checklist Niveau A — Audit Simplifié `[A]`

### 8.1 — Transcription
- [ ] Tout contenu audio seul (podcast, message vocal) a une transcription textuelle accessible
- [ ] La transcription identifie les interlocuteurs ("Présentateur : ...", "Invitée : ...")
- [ ] La transcription inclut les bruitages importants ("[applaudissements]", "[porte qui claque]")
- [ ] La transcription est accessible depuis le même écran (pas sur une autre page obscure)

### 8.2 — Sous-titres vidéo
- [ ] Toutes les vidéos avec parole ont des sous-titres
- [ ] Les sous-titres sont des Closed Captions (CC), pas du burn-in
- [ ] Les sous-titres incluent les bruitages pertinents
- [ ] Le bouton d'activation des sous-titres est accessible au lecteur d'écran

### 8.5 — Contrôles mouvements automatiques
- [ ] Les carousels auto-play ont un bouton Pause/Arrêt accessible
- [ ] Les vidéos auto-play peuvent être mises en pause
- [ ] Le bouton Play/Pause a un `accessibilityLabel` adaptatif ("Lire" / "Mettre en pause")
- [ ] Après pause manuelle, le contenu ne reprend pas automatiquement

### 8.6 — Reduce Motion
- [ ] L'app lit la préférence Reduce Motion du système
- [ ] Les animations slide/spring/bounce sont remplacées par fade si Reduce Motion actif
- [ ] Les parallaxes et zooms sont désactivés si Reduce Motion actif
- [ ] Les animations Lottie complexes sont remplacées par image statique si Reduce Motion actif
- [ ] Aucun élément ne clignote plus de 3 fois par seconde

---

## Checklist Niveau AA — Audit Complet `[AA]`

### 8.3 — Audiodescription
- [ ] Les vidéos avec informations visuelles critiques ont une piste d'audiodescription
- [ ] L'audiodescription est sélectionnable via le lecteur (pas uniquement via paramètres OS)
- [ ] Le bouton d'activation de l'audiodescription est accessible et labellisé

### 8.4 — Sous-titres en direct
- [ ] Les vidéos en direct avec parole ont des sous-titres en temps réel
- [ ] Un mécanisme de repli est prévu (transcription différée) si le direct n'est pas sous-titrable

### 8.5 — Options avancées
- [ ] Une option in-app permet de désactiver les autoplay (indépendamment du système)
- [ ] L'état lecture/pause est conservé lors de la navigation entre écrans

### 8.6 — Transitions d'écrans
- [ ] Les transitions entre écrans utilisent un fondu si Reduce Motion actif
- [ ] Les animations de navigation complexes sont désactivées si Reduce Motion actif

---

## Évolution v2 — Cross-Platform

> 🔜 À activer après stabilisation v1

- Flutter : [`_evolution/flutter.dart.future`](./_evolution/flutter.dart.future)
- React Native : [`_evolution/react-native.tsx.future`](./_evolution/react-native.tsx.future)

---

*Références : [RAAM 1.1 §8](https://accessibilite.public.lu/fr/raam1.1/referentiel-technique.html#topic-8) · [WCAG 1.2.1](https://www.w3.org/Translations/WCAG21-fr/#audio-only-and-video-only-prerecorded) · [WCAG 1.2.5](https://www.w3.org/Translations/WCAG21-fr/#audio-description-prerecorded) · [WCAG 2.2.2](https://www.w3.org/Translations/WCAG21-fr/#pause-stop-hide) · [WCAG 2.3.3](https://www.w3.org/Translations/WCAG21-fr/#animation-from-interactions) · [Apple HIG Motion](https://developer.apple.com/design/human-interface-guidelines/motion)*
