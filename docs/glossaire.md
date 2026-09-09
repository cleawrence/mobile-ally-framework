---
tags:
  - glossaire
---

# Glossaire

## Termes d'accessibilité

`Accessibilité (a11y)`
:   Conception de produits utilisables par le plus grand nombre, y compris les personnes en situation de handicap.

`Alternative textuelle`
:   Texte décrivant un élément non textuel (image, icône) pour les technologies d'assistance.

`Arbre d'accessibilité`
:   Représentation parallèle de l'interface que les lecteurs d'écran utilisent pour naviguer. Distinct de l'arbre visuel.

`Audiodescription`
:   Narration audio décrivant les éléments visuels importants d'une vidéo pour les personnes aveugles.

`Contraste`
:   Ratio de luminance entre le premier plan et l'arrière-plan. Minimum 4.5:1 pour le texte normal, 3:1 pour le texte large.

`Dynamic Type`
:   Système iOS permettant à l'utilisateur d'ajuster la taille de police globale. Équivalent Android : font scale.

`Focus`
:   Élément actuellement sélectionné par le lecteur d'écran ou la navigation clavier. Voir ordre de focus.

`Label`
:   Texte associé à un élément d'interface, lu par le lecteur d'écran. `accessibilityLabel` (SwiftUI) / `contentDescription` (Compose).

`Live Region`
:   Zone de l'écran dont les changements sont automatiquement annoncés par le lecteur d'écran.

`RAAM 1.1`
:   Référentiel d'Accessibilité des Applications Mobiles, version 1.1. Publié par le Luxembourg, basé sur EN 301 549.

`Reduce Motion`
:   Paramètre système demandant la réduction des animations. iOS : `UIAccessibility.isReduceMotionEnabled`. Android : `Settings.Global.ANIMATOR_DURATION_SCALE`.

`Reflow`
:   Capacité du contenu à s'adapter sans défilement horizontal lorsque le texte est agrandi à 200%.

`Rôle`
:   Nature d'un composant d'interface (bouton, case à cocher, lien...) communiquée au lecteur d'écran.

`Semantics`
:   En Compose, bloc `Modifier.semantics { }` décrivant la signification d'un composant pour TalkBack.

`Switch Control / Switch Access`
:   Mode d'interaction par contacteurs physiques pour les personnes ne pouvant pas utiliser l'écran tactile.

`TalkBack`
:   Lecteur d'écran Android. Équivalent de VoiceOver sur iOS.

`Technologies d'assistance (TA)`
:   Logiciels ou matériels aidant les personnes handicapées : lecteurs d'écran, loupes, contacteurs, etc.

`Touch target`
:   Zone tactile d'un élément interactif. Minimum 44×44pt (iOS) / 48×48dp (Android).

`Traits`
:   En SwiftUI, caractéristiques d'accessibilité d'un élément : `.isButton`, `.isHeader`, `.isLink`, `.isModal`, etc.

`Voice Control / Voice Access`
:   Mode d'interaction vocale. L'utilisateur dit "Appuyer sur [label]" pour activer un élément.

`VoiceOver`
:   Lecteur d'écran iOS/macOS. Équivalent de TalkBack sur Android.

`WCAG`
:   Web Content Accessibility Guidelines. Standard international d'accessibilité. Version actuelle : 2.1.

## Abréviations

*[a11y]: Accessibility (a + 11 lettres + y)
*[TA]: Technologies d'Assistance
*[RAAM]: Référentiel d'Accessibilité des Applications Mobiles
*[WCAG]: Web Content Accessibility Guidelines
*[EN 301 549]: Norme européenne d'accessibilité des TIC
*[VO]: VoiceOver
*[TB]: TalkBack
*[HIG]: Human Interface Guidelines (Apple)
*[CC]: Closed Captions (sous-titres)
*[dp]: Density-independent Pixels (Android)
*[sp]: Scale-independent Pixels (Android)
*[pt]: Points (iOS)
