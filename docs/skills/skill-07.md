---
tags:
  - niveau-a
  - niveau-aa
  - skill-07
---

# SKILL-07 — Structuration

:material-format-list-bulleted: **Listes, citations et hiérarchie de titres**

## Critères couverts
| Critère RAAM | Intitulé | Niveau | WCAG |
|---|---|---|---|
| 7.1 | Les listes sont correctement structurées | `[A]` | 1.3.1 |
| 7.2 | Les citations sont correctement identifiées | `[A]` | 1.3.1 |
| 7.3 | La hiérarchie des titres est cohérente | `[AA]` | 1.3.1, 2.4.6 |
| 7.4 | Les titres sont pertinents | `[AA]` | 2.4.6 |

## Pourquoi c'est important
!!! tip "Navigation rapide"
    Le Rotor (iOS) et les Gestes locaux (TalkBack) permettent de naviguer directement d'un titre à l'autre (`En-têtes`). Si les titres ne sont pas marqués sémantiquement, cette fonctionnalité est inutilisable.

## Patterns

### 1. Hiérarchie de titres (Headings) `[AA]`
=== "SwiftUI"
    ```swift title="✅ Bon"
    Text("Page de profil")
        .font(.largeTitle)
        .accessibilityAddTraits(.isHeader) // (1)!
    ```

    ```swift title="❌ Mauvais"
    Text("Informations personnelles")
        .font(.title2)
        .fontWeight(.semibold) // (2)!
    ```

    1. Trait `.isHeader` ajouté pour marquer sémantiquement le titre.
    2. Titre visuel sans sémantique d'en-tête, VoiceOver ne le verra pas dans le Rotor.

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    Text(
        text = "Profil Utilisateur",
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.semantics { heading() } // (1)!
    )
    ```

    ```kotlin title="❌ Mauvais"
    Text(
        text = "Informations de contact",
        style = MaterialTheme.typography.titleLarge // (2)!
    )
    ```
    
    1. Sémantique `heading()` appliquée.
    2. Aucun indicateur sémantique d'en-tête pour TalkBack.

### 2. Listes structurées `[A]`
=== "SwiftUI"
    ```swift title="✅ Bon"
    List { // (1)!
        ForEach(contacts, id: \.self) { contact in
            Text(contact)
        }
    }
    ```

    ```swift title="❌ Mauvais"
    VStack { // (2)!
        ForEach(contacts, id: \.self) { contact in
            Text(contact)
        }
    }
    ```
    
    1. L'utilisation de `List` offre nativement la sémantique.
    2. `VStack` n'apporte pas de sémantique, l'utilisateur ne saura pas le nombre d'éléments.

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    LazyColumn(modifier = Modifier.fillMaxWidth()) { // (1)!
        items(itemsList.size) { index ->
            ListItem(
                headlineContent = { Text(itemsList[index]) }
            )
        }
    }
    ```
    
    1. `LazyColumn` gère la sémantique de liste nativement.

### 3. Groupement logique `[A]`
=== "SwiftUI"
    ```swift title="✅ Bon"
    Section(header: Text("Général").accessibilityAddTraits(.isHeader)) {
        Toggle("Notifications", isOn: .constant(true))
        Toggle("Sons", isOn: .constant(false))
    }
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    Card(
        modifier = Modifier
            .padding(16.dp)
            .semantics(mergeDescendants = true) {} // (1)!
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Offre spéciale", style = MaterialTheme.typography.titleMedium)
            Text("Profitez de -20% aujourd'hui.")
        }
    }
    ```
    
    1. La carte groupe ses enfants pour n'être lue qu'une fois globalement.

## Checklist [A]
- [ ] Les listes ordonnées sont structurées pour annoncer la numérotation.
- [ ] Les listes non ordonnées annoncent clairement chaque élément.
- [ ] Le nombre d'éléments d'une liste est compréhensible.
- [ ] Les empilements visuels tenant lieu de listes possèdent une sémantique de regroupement ou de liste.
- [ ] Les citations sont distinctement lues comme telles.
- [ ] Les éléments de listes interactifs regroupent toute leur surface cliquable.
- [ ] Le rôle de "liste" n'est pas utilisé abusivement pour le layout.

## Checklist [AA]
- [ ] Chaque écran possède au moins un titre principal (H1) l'identifiant.
- [ ] Tous les éléments visuellement stylés comme des titres possèdent le trait/rôle d'en-tête.
- [ ] La hiérarchie des titres (niveaux) est cohérente, sans sauts illogiques.
- [ ] Les titres décrivent de manière claire et pertinente la section qui suit.
- [ ] Les titres sont concis.
- [ ] L'utilisateur peut parcourir tous les titres via la navigation par en-têtes (Rotor/TalkBack).

## Références
*   [RAAM 1.1 §7](https://accessibilite.public.lu/fr/raam1.1/referentiel-technique.html#topic-7)
