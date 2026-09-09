# SKILL-10 — Navigation

**Statut** : v1.0 — Couverture SwiftUI + Jetpack Compose  
**Dernière révision** : Juillet 2026  
**Auteur** : FRAM — Framework Référence Accessibilité Mobile

---

## Critères couverts

| Critère RAAM 1.1 | Intitulé | Niveau | Correspondance |
|---|---|---|---|
| 10.1 | L'utilisateur est informé de sa position dans l'application | `[A]` | WCAG 2.4.8, EN 301 549 §11.2.4.8 |
| 10.2 | L'ordre de focus est cohérent et logique | `[A]` | WCAG 1.3.2, 2.4.3, EN 301 549 §11.1.3.2 |
| 10.3 | Les raccourcis ne créent pas de conflits | `[A]` | WCAG 2.1.4, EN 301 549 §11.2.1.4 |
| 10.4 | La navigation est cohérente entre les écrans | `[AA]` | WCAG 3.2.3, 3.2.4, EN 301 549 §11.3.2.3 |
| 10.5 | L'indicateur de focus est visible | `[AA]` | WCAG 2.4.7, EN 301 549 §11.2.4.7 |
| 10.6 | Le focus ne peut pas être piégé | `[AA]` | WCAG 2.1.2, EN 301 549 §11.2.1.2 |

---

## Pourquoi c'est important

La navigation concerne en priorité :
- **Utilisateurs Switch Control** (iOS) / **Switch Access** (Android) : naviguent élément par élément — si l'ordre est illogique ou le focus piégé, ils sont bloqués
- **Utilisateurs VoiceOver/TalkBack** : l'ordre de focus détermine l'ordre de lecture — un ordre incohérent = information incompréhensible
- **Utilisateurs Voice Control** / **Voice Access** : donnent des ordres vocaux basés sur les labels des éléments
- **Utilisateurs clavier Bluetooth** (tablettes, accessoires) : naviguent par Tab/flèches

> **Piège courant n°1** : une modale qui s'ouvre sans contraindre le focus — le lecteur d'écran peut atteindre le contenu derrière  
> **Piège courant n°2** : un HStack dont l'ordre VoiceOver est aléatoire (dépend de la position physique, pas logique)

---

## Ordre de focus — Règles

### Ordre naturel (SwiftUI / Compose)
- SwiftUI et Compose ordonnent le focus **de haut en bas, de gauche à droite** par défaut
- Ce comportement est correct pour la majorité des layouts

### Cas nécessitant un ordre forcé
| Scénario | Problème | Solution |
|---|---|---|
| HStack avec titre + bouton | Bouton lu avant le titre | `.accessibilitySortPriority()` / `traversalIndex` |
| Message d'erreur après soumission | Erreur lue après le champ | Priorité haute sur le message |
| Card complexe | Ordre aléatoire | `accessibilityElement(children: .contain)` |
| Z-stack / overlay | Fond lisible avec la modale | `isModal` trait sur la modale |

---

## Focus piégé — Modales et Dialogs

Le focus doit rester **à l'intérieur** des modales tant qu'elles sont ouvertes :
- iOS SwiftUI : `.accessibilityAddTraits(.isModal)` sur le conteneur → VoiceOver ne sort plus
- Android Compose : `AlertDialog` Material3 → comportement natif correct ; pour les modales custom, `Modifier.semantics { isDialog = true }`

Le focus doit retourner à l'élément **déclencheur** quand la modale se ferme.

---

## Position dans l'application

VoiceOver/TalkBack doivent toujours pouvoir répondre à "Où suis-je ?" :
- Titre de l'écran annoncé lors de l'arrivée (`navigationTitle`, `TopAppBar`)
- Fil d'Ariane ou indicateur de position dans un flux (étape 2/4)
- Annonce programmatique du changement d'écran si navigation sans NavigationStack

---

## ✅ À faire / ❌ À éviter

| ✅ À faire | ❌ À éviter |
|---|---|
| `accessibilitySortPriority` pour forcer l'ordre quand nécessaire | HStack complexes sans ordre de focus explicite |
| `.isModal` sur les modales pour contraindre le focus | Modale sans contrainte — fond accessible |
| Retourner le focus au déclencheur après fermeture modale | Focus perdu après fermeture (revient en haut) |
| `navigationTitle` unique et pertinent par écran | Titre identique sur plusieurs écrans |
| Annoncer le changement d'écran en navigation programmatique | Navigation silencieuse sans annonce |
| `TabView` avec `accessibilityLabel` sur chaque onglet | Onglets avec icônes seules sans label |
| Indicateur de focus visible (Switch Control) | Éléments trop petits pour être focusés |

---

## Vérification manuelle

### Switch Control iOS

1. *Réglages > Accessibilité > Contrôle de sélection* → ON
2. Scanner automatique : vérifier que les éléments sont mis en surbrillance dans l'ordre logique
3. Vérifier qu'une modale ouverte ne laisse pas les éléments derrière en surbrillance
4. Vérifier que le focus revient au bouton déclencheur après fermeture

### TalkBack — Ordre de focus

1. Activer TalkBack
2. Explorer par le toucher : vérifier l'ordre gauche→droite, haut→bas
3. Dans une grille complexe : l'ordre est-il logique ?
4. Dans une modale : TalkBack ne doit pas pouvoir "sortir" en swipant

---

## Checklist Niveau A — Audit Simplifié `[A]`

### 10.1 — Position dans l'application
- [ ] Chaque écran a un titre unique et pertinent (`navigationTitle` / `TopAppBar`)
- [ ] Le titre est annoncé par le lecteur d'écran à l'arrivée sur l'écran
- [ ] Dans un flux multi-étapes, l'étape courante est indiquée ("Étape 2 sur 4")
- [ ] La navigation programmatique est accompagnée d'une annonce VoiceOver/TalkBack

### 10.2 — Ordre de focus
- [ ] L'ordre de lecture VoiceOver/TalkBack suit l'ordre logique du contenu
- [ ] Dans les HStack/Row complexes, l'ordre de focus est forcé si nécessaire
- [ ] Les messages d'erreur sont lus avant ou immédiatement après le champ concerné
- [ ] Le titre d'une carte est lu avant ses actions (boutons)

### 10.3 — Raccourcis
- [ ] Les gestes d'accessibilité système (double-tap VoiceOver, etc.) ne sont pas interceptés par l'app
- [ ] Les raccourcis custom n'entrent pas en conflit avec les gestes TA

---

## Checklist Niveau AA — Audit Complet `[AA]`

### 10.4 — Cohérence navigation
- [ ] Les éléments de navigation (tab bar, sidebar) sont identiques entre les écrans
- [ ] L'ordre des items de navigation est constant
- [ ] Les onglets ont des `accessibilityLabel` cohérents entre les écrans

### 10.5 — Focus visible
- [ ] L'indicateur de focus Switch Control est visible sur tous les éléments
- [ ] L'indicateur de focus VoiceOver (anneau bleu) n'est pas masqué par des overlays
- [ ] Les éléments cliquables ont une zone de focus visible et suffisamment grande

### 10.6 — Focus non piégé
- [ ] Les modales contraignent le focus avec `.isModal` / `isDialog`
- [ ] Le focus retourne à l'élément déclencheur à la fermeture d'une modale
- [ ] Aucun composant custom ne piège le focus indéfiniment
- [ ] Les BottomSheet libèrent le focus quand elles sont fermées

---

## Évolution v2 — Cross-Platform

> 🔜 À activer après stabilisation v1

---

*Références : [RAAM 1.1 §10](https://accessibilite.public.lu/fr/raam1.1/referentiel-technique.html#topic-10) · [WCAG 2.4.3](https://www.w3.org/Translations/WCAG21-fr/#focus-order) · [WCAG 2.1.2](https://www.w3.org/Translations/WCAG21-fr/#no-keyboard-trap) · [Apple Switch Control](https://support.apple.com/fr-fr/guide/iphone/iph400b765e7/ios)*
