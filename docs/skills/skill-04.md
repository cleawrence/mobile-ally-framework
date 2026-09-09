---
tags:
  - niveau-a
  - niveau-aa
  - skill-04
  - tableaux-listes
---

# SKILL-04 — Tableaux et Listes de données

:material-table: **Listes, grilles, en-têtes et sémantique**

## Critères couverts

| Critère RAAM | Intitulé | Niveau | WCAG |
|---|---|---|---|
| 4.1 | Chaque tableau de données a un rôle de tableau | `[A]` | 1.3.1 |
| 4.2 | Les en-têtes de colonnes/lignes sont définis | `[A]` | 1.3.1 |
| 4.3 | Chaque cellule est associée à ses en-têtes | `[A]` | 1.3.1 |
| 4.4 | Le tableau a une description ou un résumé | `[AA]` | 1.3.1 |

## Pourquoi c'est important

!!! warning "Impact utilisateurs"
    Dans un contexte mobile, l'espace d'écran limité rend les tableaux complexes. Les utilisateurs aveugles ou malvoyants peinent à lier mentalement une donnée à sa colonne/ligne sans la sémantique. Les défilements bi-directionnels (scroll vertical + horizontal) sont particulièrement difficiles pour ces profils.

!!! tip "Règle d'or"
    - Évitez les vrais tableaux visuels multi-colonnes. Préférez transformer chaque ligne en une **carte détaillée** (liste simple).
    - Toujours grouper (combine) le contenu d'une cellule pour éviter que le lecteur d'écran lise des données morcelées.
    - Annoncez explicitement les **en-têtes**.

## Patterns

### 1. Liste Accessible `[A]`

=== "SwiftUI"
    ```swift title="✅ Bon — Combine children + override de label"
    List(users, id: \.0) { user in
        VStack(alignment: .leading) {
            Text(user.0).font(.headline)
            HStack {
                Text(user.1); Text("-"); Text(user.2)
            }
        }
        .accessibilityElement(children: .combine)
        .accessibilityLabel("\(user.0), \(user.1) chez \(user.2)")
    }
    ```

    ```swift title="❌ Mauvais — Éléments séparés"
    List(users, id: \.0) { user in
        VStack {
            Text(user.0) // Lu séparément
            Text(user.1) // Lu séparément
        }
        // Sans .accessibilityElement(children: .combine)
    }
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon — CollectionItemInfo + Label combiné"
    LazyColumn(modifier = Modifier.semantics {
        collectionInfo = CollectionInfo(rowCount = users.size, columnCount = 1)
    }) {
        items(users.size) { index ->
            Column(modifier = Modifier.semantics(mergeDescendants = true) {
                collectionItemInfo = CollectionItemInfo(rowIndex = index, rowSpan = 1, columnIndex = 0, columnSpan = 1)
                contentDescription = "${user.name}, ${user.job} chez ${user.company}"
            }) {
                Text(text = user.name)
                Text(text = "${user.job} - ${user.company}")
            }
        }
    }
    ```

### 2. Tableau de Données avec En-têtes `[A]`

=== "SwiftUI"
    ```swift title="✅ Bon — Sémantique manuelle et .isHeader"
    Grid {
        GridRow {
            Text("Nom").accessibilityAddTraits(.isHeader)
            Text("Salaire").accessibilityAddTraits(.isHeader)
        }
        
        ForEach(employees) { emp in
            GridRow {
                Text(emp.name)
                    .accessibilityLabel("Nom: \(emp.name)")
                Text(emp.salary)
                    .accessibilityLabel("Salaire annuel pour \(emp.name) : \(emp.salary)")
            }
        }
    }
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon — heading() et CollectionItemInfo"
    Row {
        Text("Nom", modifier = Modifier.semantics {
            heading()
            collectionItemInfo = CollectionItemInfo(rowIndex = 0, rowSpan = 1, columnIndex = 0, columnSpan = 1)
        })
    }
    
    // Données...
    Text(emp.name, modifier = Modifier.semantics {
        collectionItemInfo = CollectionItemInfo(rowIndex = rowIndex, rowSpan = 1, columnIndex = 0, columnSpan = 1)
        contentDescription = "Nom: ${emp.name}"
    })
    ```

### 3. Tableau avec Description `[AA]`

=== "SwiftUI"
    ```swift title="✅ Bon — Label sur le conteneur global"
    VStack {
        Text("Tableau des salaires").accessibilityAddTraits(.isHeader)
        
        Grid { /* contenu */ }
        .accessibilityElement(children: .contain)
        .accessibilityLabel("Salaires des employés, contenant 2 colonnes et 2 lignes de données.")
    }
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon — contentDescription sur le Column parent"
    Column(
        modifier = Modifier.semantics {
            contentDescription = "Tableau des salaires des employés. ${employees.size} lignes."
            collectionInfo = CollectionInfo(rowCount = employees.size + 1, columnCount = 2)
        }
    ) {
        // En-têtes et données
    }
    ```

### 4. Liste Groupée `[A]`

=== "SwiftUI"
    ```swift title="✅ Bon — Section Header"
    List {
        Section(header: Text("Ressources Humaines")
                    .accessibilityAddTraits(.isHeader)) {
            Text("Alice")
        }
    }
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon — stickyHeader et heading()"
    LazyColumn {
        stickyHeader {
            Text(
                text = department,
                modifier = Modifier.semantics { heading() }
            )
        }
        // items...
    }
    ```

## Checklist Audit Simplifié `[A]`

- [ ] Les listes simples sont construites avec les composants natifs (`List`, `LazyColumn`).
- [ ] Les éléments de listes multi-colonnes regroupent leur texte pour une lecture fluide (combine/mergeDescendants).
- [ ] Les en-têtes de colonnes/lignes sont marqués sémantiquement.
- [ ] Les cellules rappellent leur contexte d'en-tête (le lecteur d'écran ne se perd pas).
- [ ] Si un tableau de données complexe est présenté, une version "vue en liste" ou CSV est proposée en alternative.

## Checklist Audit Complet `[AA]`

- [ ] Le tableau/grille de données possède une description globale de son objectif.
- [ ] Dans les listes groupées, les séparateurs sont marqués explicitement comme en-têtes.
- [ ] Le tableau respecte le contraste minimum pour ses bordures (≥ 3:1).
- [ ] Les cellules interactives (boutons, liens) ont une zone de clic d'au moins 44x44 points (iOS) ou 48x48 dp (Android).

## Outils

| Outil | Plateforme | Usage |
|---|---|---|
| VoiceOver | iOS | Tester le balayage dans la grille pour vérifier l'association cellule-en-tête |
| TalkBack | Android | Idem, vérifier la lecture des infos de collection |

## Références

- [RAAM 1.1 §4](https://accessibilite.public.lu/fr/raam1.1/referentiel-technique.html)
- [WCAG 1.3.1 (Info and Relationships)](https://www.w3.org/Translations/WCAG21-fr/#info-and-relationships)
