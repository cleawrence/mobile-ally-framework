# SKILL-04 : Tableaux et Listes de données (RAAM 1.1 - Section 4)

## Critères
- **4.1 [A]** : Chaque tableau de données a un rôle de tableau (WCAG 1.3.1).
- **4.2 [A]** : Les en-têtes de colonnes/lignes sont définis (WCAG 1.3.1).
- **4.3 [A]** : Chaque cellule est associée à ses en-têtes (WCAG 1.3.1).
- **4.4 [AA]** : Le tableau a une description ou un résumé si nécessaire (WCAG 1.3.1).

## Pourquoi c'est important ?
Dans un contexte mobile, les tableaux sont particulièrement complexes à parcourir avec un lecteur d'écran en raison de la taille réduite de l'écran (qui entraîne souvent un défilement horizontal et vertical) et du manque de sémantique "Table" native (particulièrement sous iOS).
Sans structure appropriée, les utilisateurs de lecteurs d'écran ne peuvent pas lier mentalement la donnée d'une cellule à ses en-têtes de colonnes et de lignes, ce qui rend le tableau incompréhensible.

## Spécificités Mobiles
- **iOS** : Il n'y a pas de rôle "Table" natif robuste pour l'accessibilité sur iPhone dans SwiftUI (le composant `Table` est pensé pour macOS et iPadOS). La solution consiste souvent à combiner le contenu en "cartes" accessibles dans une `List`, ou d'ajouter une information manuelle par cellule : `[En-tête]: [Valeur]`.
- **Android (Compose)** : Compose fournit la sémantique native des collections (`collectionInfo` et `collectionItemInfo`), permettant d'annoncer la position (ligne, colonne) dans une grille.

## À Faire (Do)
- [x] Utiliser la sémantique de liste native pour les simples énumérations de données.
- [x] Identifier clairement les en-têtes (`isHeader` sous iOS, `heading()` sous Android).
- [x] Sur de petits écrans, privilégier une transformation du tableau en présentation par liste (chaque ligne devenant une carte ou "cellule" avec toutes les informations).
- [x] Ajouter un titre/description caché ou visible expliquant le rôle et la structure du tableau [AA].
- [x] S'assurer que le focus du lecteur d'écran suit un ordre logique de lecture.

## À Éviter (Don't)
- [ ] Utiliser de simples vues (Text/Box) visuellement disposées en tableau sans aucune sémantique.
- [ ] Forcer l'utilisateur à mémoriser les en-têtes de colonnes.
- [ ] Afficher des tableaux complexes non responsives qui requièrent un défilement bidimensionnel au lecteur d'écran.

## Checklist de Vérification

### Niveau [A] (Audit simplifié)
- [ ] 1. Les listes simples sont construites avec les composants natifs (`List`, `LazyColumn`).
- [ ] 2. Les éléments de listes multi-colonnes regroupent leur texte pour une lecture fluide en une seule fois (via `.accessibilityElement(children: .combine)` ou un seul conteneur `semantics`).
- [ ] 3. Les tableaux de données sont sémantisés comme des collections (Android) ou vocalisent leurs en-têtes pour chaque cellule (iOS).
- [ ] 4. Les en-têtes de colonnes sont marqués sémantiquement (ex: `.accessibilityAddTraits(.isHeader)`).
- [ ] 5. Les en-têtes de lignes sont marqués sémantiquement.
- [ ] 6. Lorsqu'un utilisateur lit une cellule, il a connaissance de l'en-tête de colonne correspondant.
- [ ] 7. L'ordre de balayage du tableau est cohérent (parcours ligne par ligne, ou colonne par colonne, mais pas de sauts aléatoires).
- [ ] 8. Si un tableau de données complexe est présenté, une version "vue en liste" ou un téléchargement CSV est proposé comme alternative accessible.
- [ ] 9. Aucun layout visuel en forme de tableau n'est utilisé pour faire de la mise en page simple (les tableaux sont réservés aux données).
- [ ] 10. L'utilisateur peut parcourir toutes les données du tableau sans être bloqué dans une boucle de défilement (scroll trap).

### Niveau [AA] (Audit complet)
- [ ] 1. Le tableau (ou la grille de données) possède une description / un titre expliquant son objectif (ex: `accessibilityLabel` ou `contentDescription`).
- [ ] 2. Un résumé de la structure ou du mode de lecture du tableau est fourni si le tableau est particulièrement complexe.
- [ ] 3. Dans les listes groupées, les séparateurs de groupes sont marqués comme en-têtes.
- [ ] 4. Le tableau respecte le contraste minimum pour ses bordures s'il n'y a pas d'autre séparateur (3:1).
- [ ] 5. Les interactions dans les cellules du tableau (boutons, liens) ont une zone de clic d'au moins 44x44 points (iOS) ou 48x48 dp (Android).
- [ ] 6. Si le tableau présente des données dynamiques (tri/filtre), le changement d'état est annoncé aux lecteurs d'écran.
