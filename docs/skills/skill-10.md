---
tags:
  - niveau-a
  - niveau-aa
  - skill-10
---

# SKILL-10 — Navigation

:material-compass: **Ordre de focus, cohérence et positionnement dans l'application**

## Critères couverts

| Critère RAAM | Intitulé | Niveau | WCAG |
|---|---|---|---|
| 10.1 | L'utilisateur est informé de sa position dans l'application | `[A]` | WCAG 2.4.8 |
| 10.2 | L'ordre de focus est cohérent et logique | `[A]` | WCAG 1.3.2, 2.4.3 |
| 10.3 | Les raccourcis ne créent pas de conflits | `[A]` | WCAG 2.1.4 |
| 10.4 | La navigation est cohérente entre les écrans | `[AA]` | WCAG 3.2.3, 3.2.4 |
| 10.5 | L'indicateur de focus est visible | `[AA]` | WCAG 2.4.7 |
| 10.6 | Le focus ne peut pas être piégé | `[AA]` | WCAG 2.1.2 |

## Pourquoi c'est important

!!! warning "Le piège du focus"
    Une modale qui s'ouvre sans contraindre le focus est un défaut majeur. Le lecteur d'écran peut atteindre le contenu derrière et l'utilisateur est totalement perdu. 

La navigation concerne l'ordre et le cheminement. L'ordre de focus de haut en bas et de gauche à droite doit être respecté pour que la lecture par VoiceOver ou TalkBack soit logique.

## Patterns

### 1. Ordre de Focus `[A]`

=== "SwiftUI"
    ```swift title="✅ Bon"
    VStack(alignment: .leading) {
        Text("Titre de la carte")
            .font(.headline)
            .accessibilitySortPriority(1) // Lu en premier
        Text("Description détaillée...")
        Button("Action principale") { }
    }
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Titre de la carte",
            modifier = Modifier.semantics { traversalIndex = -1f } // Index petit = en premier
        )
        Text("Description...", modifier = Modifier.semantics { traversalIndex = 0f })
        Button(onClick = {}, modifier = Modifier.semantics { traversalIndex = 1f }) {
            Text("Action principale")
        }
    }
    ```

### 2. Position dans l'App `[A]`

=== "SwiftUI"
    ```swift title="✅ Bon"
    NavigationStack {
        List { Text("Contenu") }
        .navigationTitle("Accueil") // Titre unique annoncé
    }
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Accueil", modifier = Modifier.semantics { heading() }) }
            )
        }
    ) { padding -> /* Contenu */ }
    ```

### 3. Focus Non Piégé `[AA]`

=== "SwiftUI"
    ```swift title="✅ Bon"
    if showModal {
        VStack {
            Text("Contenu de la modale").accessibilityAddTraits(.isHeader)
            Button("Fermer") { showModal = false }
        }
        .accessibilityAddTraits(.isModal) // Contraint le focus
        .accessibilityAction(.escape) { showModal = false }
    }
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    if (showDialog) {
        AlertDialog( // Gère nativement le focus trap
            onDismissRequest = { showDialog = false },
            title = { Text("Information") },
            text = { Text("Modale accessible.") },
            confirmButton = { TextButton(onClick = { showDialog = false }) { Text("Fermer") } }
        )
    }
    ```

### 4. Navigation Cohérente `[AA]`

=== "SwiftUI"
    ```swift title="✅ Bon"
    TabView {
        Text("Contenu Accueil")
            .tabItem { Label("Accueil", systemImage: "house") }
            .accessibilityLabel("Onglet Accueil")
    }
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
            label = { Text("Accueil") },
            selected = true,
            onClick = { },
            modifier = Modifier.semantics { contentDescription = "Onglet Accueil" }
        )
    }
    ```

## Checklist [A] / [AA]

- [ ] L'ordre de focus (lecture) correspond à la structure visuelle.
- [ ] Les modales piègent correctement le focus (`isModal` / `AlertDialog`).
- [ ] Le focus revient sur le déclencheur après la fermeture d'une modale.
- [ ] Le titre des écrans est clair et annonce la position.

## Références
- [RAAM 1.1 §10 Navigation](https://accessibilite.public.lu/fr/raam1.1/referentiel-technique.html#topic-10)
- [WCAG 2.4.3 Focus Order](https://www.w3.org/Translations/WCAG21-fr/#focus-order)
