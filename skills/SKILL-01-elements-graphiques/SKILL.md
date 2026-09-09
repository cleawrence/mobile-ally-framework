# SKILL-01 — Éléments Graphiques

**Statut** : v1.0 — Couverture SwiftUI + Jetpack Compose  
**Dernière révision** : Juillet 2026  
**Auteur** : FRAM — Framework Référence Accessibilité Mobile

---

## Critères couverts

| Critère RAAM 1.1 | Intitulé | Niveau | Correspondance |
|---|---|---|---|
| 1.1 | Élément graphique de décoration ignoré par les TA | `[A]` | WCAG 1.1.1, EN 301 549 §11.1.1.1.1 |
| 1.2 | Élément graphique porteur d'info a une alternative accessible | `[A]` | WCAG 1.1.1 |
| 1.3 | L'alternative textuelle est pertinente | `[A]` | WCAG 1.1.1 |
| 1.4 | CAPTCHA : l'alternative permet d'identifier sa nature et sa fonction | `[A]` | WCAG 1.1.1 |
| 1.5 | CAPTCHA : il existe une alternative non graphique | `[A]` | WCAG 1.1.1 |
| 1.6 | Description détaillée fournie si nécessaire | `[A]` | WCAG 1.1.1 |
| 1.7 | Description détaillée pertinente | `[A]` | WCAG 1.1.1 |
| 1.8 | Images texte remplacées par du texte stylé | `[AA]` | WCAG 1.4.5, EN 301 549 §11.1.4.5.1 |
| 1.9 | Éléments graphiques légendés correctement restitués | `[AA]` | WCAG 1.1.1 |

---

## Pourquoi c'est important

Les éléments graphiques — icônes, photos, illustrations, graphiques de données — représentent une part massive de l'interface mobile. Pour les utilisateurs qui ne les voient pas :
- **Utilisateurs aveugles** (VoiceOver/TalkBack) : une icône sans label est silencieuse ou annonce son nom de fichier brut ("star.fill", "ic_delete") — incompréhensible
- **Utilisateurs malvoyants** : une image texte ne peut pas être agrandie sans devenir floue, contrairement au texte natif
- **Utilisateurs daltoniens** : une icône d'état uniquement différenciée par couleur (rouge/vert) sans label textuel est indistinguable

> **Règle d'or** :
> - Décoration → **caché** des technologies d'assistance  
> - Information → **label textuel concis et contextuel**  
> - Complexe → **description détaillée** accessible

---

## ✅ À faire / ❌ À éviter

| ✅ À faire | ❌ À éviter |
|---|---|
| `.accessibilityHidden(true)` / `contentDescription = null` pour la décoration | Laisser les icônes décoratives dans l'arbre de focus |
| Label contextuel : "Avertissement : batterie faible" | Préfixer par "image de", "icône de", "photo de" |
| Texte natif (`Text()`) pour les effets typographiques | Bitmap/image de texte (non reconfigurable) |
| Description détaillée pour les graphiques complexes | Nom du fichier brut : "chart.png", "graph1.jpg" |
| Regrouper image + légende pour une lecture unifiée | Lire séparément image et sa légende |
| SF Symbols adaptatifs (poids, taille selon Dynamic Type) | Images fixes ne respectant pas les préférences système |

---

## Types d'éléments couverts

1. **SF Symbols / Compose Icons** — icônes système décoratifs et informatifs
2. **Images réseau** — `AsyncImage` (SwiftUI) / Coil (Compose)
3. **Icônes d'état** — succès, erreur, avertissement, info
4. **Notation par étoiles** — rating accessible
5. **Graphiques / Charts** — Swift Charts, Compose charts
6. **Images texte** — bannières promotionnelles, titres stylisés
7. **CAPTCHA** — identification et alternatives
8. **Images légendées** — photo + légende combinées
9. **Thumbnails et cartes** — fusion image + contenu textuel
10. **Boutons iconiques** — icône cliquable seule

Voir les patterns complets dans :
- **iOS SwiftUI** → [`ios-swiftui/patterns.swift`](./ios-swiftui/patterns.swift)
- **Android Compose** → [`android-compose/patterns.kt`](./android-compose/patterns.kt)

---

## La règle décoration vs information

La question clé avant tout développement : **cette image apporte-t-elle une information que l'utilisateur ne peut pas obtenir autrement ?**

```
Scénario 1 : icône ⭐ à côté du texte "Favoris"
  → L'icône est DÉCORATIVE (le texte dit déjà tout)
  → accessibilityHidden(true) / contentDescription = null

Scénario 2 : icône ⚠️ seule indiquant un problème de connexion
  → L'icône est INFORMATIVE (sans elle, l'info est perdue)
  → accessibilityLabel("Avertissement : connexion réseau instable")

Scénario 3 : photo de profil de Marie Dupont
  → Dépend du contexte :
  - Dans une liste de contacts → informatif ("Photo de Marie Dupont")
  - En fond décoratif d'un écran → décoratif (caché)
```

---

## Règles de rédaction des labels graphiques

Un bon label d'élément graphique :
1. **Décrit l'information** (pas l'aspect visuel) : "Batterie faible" et non "icône de batterie rouge à 10%"
2. **Est concis** : 1 à 10 mots en général
3. **Ne commence pas par** "image de", "icône de", "photo de", "illustration de"
4. **N'est pas redondant** avec le texte adjacent déjà lisible

| ❌ Label non conforme | ✅ Label conforme |
|---|---|
| "star.fill" (nom SF Symbol) | "Noté 4 étoiles sur 5" |
| "Icône d'alerte triangulaire jaune" | "Avertissement" |
| "Photo de profil" (seul) | "Photo de profil de Marie Dupont" |
| "chart-sales-q3.png" | "Graphique des ventes — 3ᵉ trimestre 2026" |
| "Bouton supprimer" | "Supprimer l'article Jean slim" |

---

## Vérification manuelle

### Protocole VoiceOver (iOS)

1. Activer VoiceOver : *Réglages > Accessibilité > VoiceOver*
2. Balayer gauche/droite pour parcourir tous les éléments
3. Pour chaque élément graphique, vérifier :
   - **Décoration** → ne doit PAS recevoir le focus (silencieux)
   - **Information** → VoiceOver lit un label significatif
   - **Label** → ne commence pas par "image", "icône", "star.fill"…
4. Tester les étoiles de notation → doit annoncer "4 étoiles sur 5" et non 5 fois "étoile"
5. Tester un graphique complexe → vérifier qu'une description ou alternative est disponible
6. Tester les images légendées → VoiceOver doit lire image + légende en un seul passage

### Protocole TalkBack (Android)

1. Activer TalkBack : *Paramètres > Accessibilité > TalkBack*
2. Balayer gauche/droite, explorer par le toucher
3. Pour chaque `Icon` / `Image` :
   - `contentDescription = null` → ignoré ✅
   - `contentDescription = "star"` (nom ressource) → VIOLATION ❌
   - `contentDescription = "Noté 4 étoiles sur 5"` → correct ✅
4. Vérifier les cartes fusionnées (`mergeDescendants = true`) : l'image + le contenu doivent être lus ensemble

### Outils

```bash
# iOS — Accessibility Inspector (Xcode inclus)
# Xcode > Open Developer Tool > Accessibility Inspector
# → Cliquer sur un élément graphique → vérifier Label, Hint, Traits

# iOS 17+ — Audit automatique
try app.performAccessibilityAudit(for: [.sufficientElementDescription])

# Android — Accessibility Scanner (Play Store)
# → Signale les Image sans contentDescription

# Android — Layout Inspector (Android Studio)
# View > Tool Windows > Layout Inspector
# → Vérifier contentDescription dans le SemanticsNode
```

---

## Checklist Niveau A — Audit Simplifié `[A]`

### 1.1 — Éléments décoratifs masqués
- [ ] Les icônes purement décoratives sont masquées (`accessibilityHidden(true)` / `contentDescription = null`)
- [ ] Les icônes adjacentes à un texte répétant la même info sont masquées
- [ ] Les illustrations de fond, séparateurs, ornements sont masqués
- [ ] Les icônes décoratives dans les boutons sont masquées (le label est sur le bouton parent)

### 1.2 et 1.3 — Alternatives pertinentes
- [ ] Chaque icône d'état (succès, erreur, warning, info) a un label descriptif
- [ ] Les notations par étoiles annoncent le total ("4 étoiles sur 5") et non individuellement
- [ ] Les images réseau (`AsyncImage` / Coil) ont un label ou sont masquées si décoratives
- [ ] Aucun label ne commence par "image de", "icône de", "photo de"
- [ ] Les labels de boutons iconiques décrivent la **fonction** (pas l'icône) avec le contexte

### 1.4 et 1.5 — CAPTCHA
- [ ] Le CAPTCHA identifie sa nature et sa fonction dans son label accessible
- [ ] Une alternative non graphique est disponible (audio, SMS, défi cognitif)

### 1.6 et 1.7 — Description détaillée
- [ ] Les graphiques complexes ont une description détaillée ou un accès aux données brutes
- [ ] La description détaillée couvre **toutes** les informations transmises visuellement
- [ ] Si bouton "Voir la description" utilisé, il est clairement rattaché au graphique

---

## Checklist Niveau AA — Audit Complet `[AA]`

### 1.8 — Images texte
- [ ] Aucune image de texte n'est utilisée pour des informations (sauf logotypes)
- [ ] Les effets typographiques (dégradé, ombre, police custom) utilisent `Text()` natif
- [ ] Les bannières promotionnelles utilisent du texte natif stylisé, pas des bitmaps

### 1.9 — Éléments légendés
- [ ] Les images avec légende visible sont regroupées sémantiquement (`accessibilityElement(children: .combine)` / `mergeDescendants = true`)
- [ ] Le label de l'image n'entre pas en conflit ou ne répète pas la légende
- [ ] L'ordre de lecture image → légende est logique

---

## Audio Graphs — iOS 15+ (Bonus)

Pour les graphiques de données (Swift Charts), iOS 15+ offre l'**Audio Graph** natif :

```swift
// Swift Charts génère automatiquement un audio graph
// activable par VoiceOver : "Actions > Audio Graph"
Chart(data) {
    BarMark(x: .value("Mois", $0.month), y: .value("Ventes", $0.sales))
}
.chartAccessibilityLabel("Ventes par mois — Année 2026")
// VoiceOver peut jouer une tonalité représentant les données
```

---

## Évolution v2 — Cross-Platform

> 🔜 À activer après stabilisation v1

- Flutter : [`_evolution/flutter.dart.future`](./_evolution/flutter.dart.future)
- React Native : [`_evolution/react-native.tsx.future`](./_evolution/react-native.tsx.future)
- ML on-device pour description automatique de photos

---

*Références : [RAAM 1.1 §1](https://accessibilite.public.lu/fr/raam1.1/referentiel-technique.html#topic-1) · [WCAG 1.1.1](https://www.w3.org/Translations/WCAG21-fr/#non-text-content) · [WCAG 1.4.5](https://www.w3.org/Translations/WCAG21-fr/#images-of-text) · [Apple Audio Graphs](https://developer.apple.com/documentation/accessibility/audio_graphs)*
