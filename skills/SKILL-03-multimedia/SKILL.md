# SKILL-03 — Adaptation et Présentation, v1.0

Ce document détaille les pratiques d'accessibilité concernant l'adaptation de l'interface et la présentation de l'information sur mobile, conformément au référentiel RAAM 1.1 (Section 3).

## Critères RAAM / WCAG

| ID | Niveau | Description | Équivalent WCAG |
|:---|:-------|:------------|:----------------|
| 3.1 | [A] | L'information n'est pas véhiculée uniquement par la forme, la taille, la position ou l'orientation. | WCAG 1.3.3 |
| 3.2 | [AA] | L'information ou la fonctionnalité n'est pas perdue lorsque l'orientation de l'écran change (portrait/paysage). | WCAG 1.3.4 |
| 3.3 | [AA] | Le texte peut être redimensionné sans perte de lisibilité (Dynamic Type / Font Scale). | WCAG 1.4.4 |
| 3.4 | [AA] | Le contenu s'adapte à 200% de zoom sans nécessité de défilement horizontal (Reflow). | WCAG 1.4.10 |

## Pourquoi c'est important

- **Dynamic Type (Texte dynamique) :** Utilisé par environ 30% des utilisateurs iOS pour agrandir ou réduire la taille du texte selon leurs besoins visuels. Sur Android, le réglage d'affichage et de police de caractères est largement répandu.
- **Orientation :** De nombreux utilisateurs, notamment ceux utilisant un fauteuil roulant ou des supports fixes pour leur téléphone/tablette, ne peuvent pas physiquement tourner leur écran. Verrouiller l'orientation en portrait est une barrière majeure.
- **Indépendance de la forme :** Les instructions comme "cliquez sur le bouton rouge à droite" sont inaccessibles aux utilisateurs non-voyants, daltoniens, ou utilisant un lecteur d'écran avec une interface linéarisée.

## Dynamic Type & Font Scale

Les systèmes mobiles permettent à l'utilisateur de modifier la taille globale du texte (iOS: Dynamic Type, tailles de xSmall à Accessibility XXXLarge ; Android: sp units et Font Scale).

* **iOS :** Utilisez les styles sémantiques (ex: `.font(.body)`, `.font(.title)`) et le property wrapper `@ScaledMetric` pour que les dimensions (espacements, tailles d'icônes) grandissent proportionnellement à la taille du texte.
* **Android :** Utilisez exclusivement des unités `sp` (Scale-independent Pixels) pour les textes via `MaterialTheme.typography`. Évitez les tailles fixes en `dp`.

## Orientation et Layout Adaptatif

* Ne forcez pas l'orientation de l'écran (ex. `supportedInterfaceOrientations` limité à `.portrait`), sauf si c'est strictement indispensable (ex. jeu vidéo complexe, app appareil photo).
* Le contenu doit s'adapter intelligemment : utilisez des `GeometryReader`, des `WindowSizeClass` (Android) ou des `ViewThatFits` (SwiftUI) pour gérer les vues compactes, moyennes et étendues.

## Reflow (Pas de défilement horizontal)

* À grande taille de police, les textes peuvent devenir très longs. 
* L'interface doit redistribuer les éléments à la ligne (ex: passage d'une ligne d'icônes à une grille ou à une liste verticale) plutôt que d'introduire un défilement horizontal qui force l'utilisateur à faire des allers-retours pour lire une ligne.

## Information par la forme/position

* Rédigez les textes et instructions sans dépendre de caractéristiques sensorielles. 
* Au lieu de "Appuyez sur la flèche bleue", utilisez "Appuyez sur le bouton Suivant".

## À faire / À éviter

* ✅ **À faire :** Utiliser des polices de caractères dynamiques fournies par le système d'exploitation.
* ✅ **À faire :** S'assurer que les boutons, icônes et marges s'adaptent proportionnellement avec `@ScaledMetric` ou les équivalents.
* ❌ **À éviter :** Tronquer le texte arbitrairement à de grandes tailles de police.
* ❌ **À éviter :** Fixer la hauteur des cellules ou des conteneurs (`height: 50`) qui coupe le texte agrandi.

## Vérification manuelle

1. Activer les grandes tailles de texte dans les réglages d'accessibilité (iOS : Réglages > Accessibilité > Affichage et taille du texte ; Android : Paramètres > Accessibilité > Taille d'affichage et de police).
2. Vérifier que tout le texte est lisible, qu'aucun texte n'est coupé, superposé ou inaccessible.
3. Tourner l'appareil physiquement. Vérifier que l'interface pivote et que toutes les fonctionnalités sont utilisables.
4. Relire les textes, bulles d'aide et tutoriels de l'application pour s'assurer d'aucune référence purement spatiale ou visuelle (ex: "le carré à gauche").

## Checklist

### [A] Critère 3.1 (Forme/Position)
- [ ] Les instructions n'utilisent pas de mots comme "à droite", "en haut", "rouge", "carré" sans autre identification.
- [ ] Les retours d'erreur (ex: formulaire) n'utilisent pas que la couleur.
- [ ] Les éléments graphiques ont des labels clairs et non ambigus.
- [ ] L'ordre de tabulation/focus est logique, indépendamment de la disposition visuelle.
- [ ] Les icônes ont une alternative textuelle.
- [ ] Les graphiques (camemberts, courbes) ne dépendent pas uniquement des couleurs.
- [ ] Les boutons ont des libellés sémantiques compréhensibles ("Validation" plutôt que "Flèche").
- [ ] Un lecteur d'écran permet de comprendre l'état de chaque composant sans le voir.

### [AA] Critères 3.2, 3.3, 3.4 (Orientation, Dynamic Type, Reflow)
- [ ] L'application autorise l'orientation paysage.
- [ ] L'application autorise l'orientation portrait.
- [ ] L'interface reste fonctionnelle en mode paysage (pas de chevauchement masquant les actions).
- [ ] Le texte utilise des styles dynamiques (Dynamic Type / sp) et non fixes.
- [ ] Le redimensionnement du texte jusqu'à 200% est supporté.
- [ ] À 200% de zoom, aucun défilement bidirectionnel (horizontal + vertical en même temps) n'est nécessaire.
- [ ] Les conteneurs n'ont pas de hauteurs fixes qui tronqueraient le texte.
- [ ] Les icônes associées aux textes grossissent proportionnellement (`@ScaledMetric`).
- [ ] Aucun texte critique n'est caché avec `lineLimit(1)` à grande taille (ou a un équivalent pour le lire).
- [ ] Le mode paysage sur petit écran à grande police permet toujours d'accéder au contenu (scroll vertical autorisé).
- [ ] Les grilles/rangées (HStack/Row) passent en colonne (VStack/Column) si l'espace manque.
- [ ] Les éléments interactifs (boutons) gardent une taille cible d'au moins 44x44 (iOS) ou 48x48 (Android) même quand l'espace manque.
