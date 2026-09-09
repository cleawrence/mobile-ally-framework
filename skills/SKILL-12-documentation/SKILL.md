# SKILL-12 : Documentation et Fonctionnalités d'Accessibilité

## 1. Critères RAAM 1.1 - Section 12

| ID | Niveau | Description | Composants |
|---|---|---|---|
| 12.1 | [A] | La documentation de l'app est accessible (WCAG 1.1.1, 4.1.2) | Déclaration d'accessibilité, pages d'aide |
| 12.2 | [A] | Les fonctionnalités d'accessibilité de l'app sont documentées (WCAG 3.3.5) | Tutoriels, aide intégrée, Release Notes |
| 12.3 | [AA] | Les fonctionnalités d'accessibilité système sont supportées (WCAG 1.3.4, 4.1.3) | VoiceOver, TalkBack, Switch Control, Voice Control |
| 12.4 | [AA] | Conformité aux APIs d'accessibilité de la plateforme (WCAG 4.1.2) | Traits, Roles, States, Properties, Actions |

## 2. Déclaration d'Accessibilité

Toute application mobile du secteur public ou de grandes entreprises doit inclure une déclaration d'accessibilité accessible depuis les paramètres de l'application.

**Contenu requis :**
- Niveau de conformité (RAAM 1.1 / RGAA 4.1) : Totalement conforme, Partiellement conforme, ou Non conforme.
- Date de l'audit et version de l'application évaluée.
- Technologies utilisées (ex: iOS 16+, SwiftUI, FRAM).
- Liste des non-conformités éventuelles (contenus non accessibles).
- Coordonnées de contact pour signaler un problème (email, formulaire, téléphone).
- Voies de recours (Défenseur des Droits).

## 3. Support VoiceOver Complet (iOS)

L'API SwiftUI offre une panoplie de modificateurs pour configurer finement l'arbre d'accessibilité.

* **Identification & Contenu :** `.accessibilityLabel()`, `.accessibilityValue()`, `.accessibilityHint()`, `.accessibilityIdentifier()`, `.accessibilityInputLabels()`
* **Sémantique & Comportement :** `.accessibilityAddTraits()`, `.accessibilityRemoveTraits()`, `.accessibilityElement(children:)`
* **Actions & Focus :** `.accessibilityAction()`, `.accessibilityCustomContent()`, `.accessibilitySortPriority()`, `.accessibilityActivationPoint()`, `.accessibilityFocused()`
* **Annonces & Tactile :** `.accessibilityAnnouncement()` (iOS 15+), `.accessibilityDirectTouch()`

## 4. Support TalkBack Complet (Android)

Jetpack Compose utilise le modifier `semantics` pour construire l'arbre d'accessibilité.

* **Identification & Contenu :** `contentDescription`, `stateDescription`, `testTag`
* **Sémantique & Comportement :** `role`, `heading`, `liveRegion`, `error`
* **États :** `isEnabled`, `isFocused`, `isSelected`, `expandedState`
* **Actions & Listes :** `onClick`, `onLongClick`, `customActions`, `collectionInfo`, `collectionItemInfo`, `dismissAction`
* **Défilement & Navigation :** `traversalIndex`, `isTraversalGroup`, `horizontalScrollAxisRange`, `verticalScrollAxisRange`

## 5. Voice Control iOS / Voice Access Android

Pour le contrôle vocal, l'utilisateur dicte le texte visible à l'écran. 
* **iOS :** Utilisez `.accessibilityInputLabels([Text])` si le `.accessibilityLabel` de l'élément diffère fortement du texte visible (ex: une icône accompagnée de texte visible). Voice Control utilise les labels pour créer les zones cliquables.
* **Android :** Si un bouton contient une icône dont le `contentDescription` est long, mais le texte visible est court, Voice Access peut se tromper. Assurez-vous que le composant complet fusionne ses sémantiques et que le texte visible prédomine pour l'activation vocale.

## 6. Switch Control iOS / Switch Access Android

La navigation par contacteur (switch) requiert :
1. Un ordre de focus logique (de haut en bas, de gauche à droite).
2. Que tous les éléments interactifs soient focusables et regroupés correctement (pas de zones mortes).
3. L'utilisation d'actions personnalisées (`accessibilityAction` / `customActions`) pour remplacer des gestes complexes impossibles à réaliser avec un bouton poussoir (ex: glisser pour supprimer).

## 7. Tableau de Conformité des APIs (WCAG vs Plateformes)

| Concept WCAG | iOS SwiftUI | Android Compose |
|---|---|---|
| Nom (Name) | `accessibilityLabel` | `contentDescription` |
| Rôle (Role) | `accessibilityAddTraits` | `role` |
| Valeur (Value) | `accessibilityValue` | `stateDescription` / `RangeInfo` |
| État (State) | `accessibilityAddTraits(.isSelected)` | `isSelected`, `isEnabled`, `expandedState` |
| Instructions | `accessibilityHint` | Non natif (utiliser customActions ou description enrichie) |

## 8. Checklist d'Évaluation

### Niveau [A]
- [ ] L'application contient une page "Accessibilité" ou "Déclaration d'accessibilité" dans ses paramètres.
- [ ] La déclaration d'accessibilité est rédigée de manière claire et compréhensible.
- [ ] La déclaration d'accessibilité liste l'état de conformité selon le RAAM.
- [ ] Un moyen de contact est fourni pour signaler les problèmes d'accessibilité.
- [ ] Les fonctionnalités spécifiques d'accessibilité (ex: mode sombre, ajustement texte) sont documentées.
- [ ] Tous les éléments graphiques informatifs ont un texte alternatif de base (Name/Label).
- [ ] L'ordre de navigation de base au lecteur d'écran est séquentiel.
- [ ] L'application ne bloque pas les gestes systèmes (ex: retour arrière système).
- [ ] Le redimensionnement du texte dynamique du système est activé.
- [ ] Les pages d'aide et de documentation sont utilisables au lecteur d'écran.

### Niveau [AA]
- [ ] Les contrôles interactifs supportent le contrôle vocal (le label correspond au texte visible).
- [ ] Des `accessibilityInputLabels` (iOS) sont fournis pour le contrôle vocal lorsque c'est pertinent.
- [ ] La navigation par Switch Control (iOS) / Switch Access (Android) est testée et fonctionnelle.
- [ ] Les éléments interactifs sur-mesure déclarent correctement leur `Role` et `State` natif.
- [ ] Les gestes complexes (ex: Swipe-to-delete) proposent une `Custom Action` accessible.
- [ ] Les `Live Regions` (Android) ou `Announcements` (iOS) sont utilisés pour les changements de contexte asynchrones.
- [ ] Les éléments non pertinents pour l'accessibilité sont explicitement ignorés (`accessibilityHidden` / `clearAndSetSemantics`).
- [ ] Les listes et collections annoncent leur structure via des `Traits` ou `CollectionInfo`.
- [ ] Les modales retiennent correctement le focus d'accessibilité et annoncent leur fermeture.
- [ ] Les erreurs de validation de formulaires sont transmises sémantiquement à l'API d'accessibilité.
