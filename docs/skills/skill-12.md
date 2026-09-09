---
tags:
  - niveau-a
  - niveau-aa
  - skill-12
---

# SKILL-12 — Documentation

:material-book-open: **Déclaration d'accessibilité et APIs système (Voice Control, Switch)**

## Critères couverts

| Critère RAAM | Intitulé | Niveau | WCAG |
|---|---|---|---|
| 12.1 | La documentation de l'app est accessible | `[A]` | WCAG 1.1.1, 4.1.2 |
| 12.2 | Les fonctionnalités d'accessibilité de l'app sont documentées | `[A]` | WCAG 3.3.5 |
| 12.3 | Les fonctionnalités d'accessibilité système sont supportées | `[AA]` | WCAG 1.3.4, 4.1.3 |
| 12.4 | Conformité aux APIs d'accessibilité de la plateforme | `[AA]` | WCAG 4.1.2 |

## Pourquoi c'est important

L'accessibilité n'est pas uniquement pour VoiceOver et TalkBack. Les utilisateurs de Switch Control / Switch Access naviguent par un contacteur externe. Les utilisateurs de Voice Control / Voice Access contrôlent l'application par la voix. Et la loi (comme le RAAM) exige souvent une **Déclaration d'Accessibilité** claire et disponible dans l'application.

## Patterns

### 1. Déclaration d'Accessibilité In-App `[A]`

=== "SwiftUI"
    ```swift title="✅ Bon"
    ScrollView {
        VStack(alignment: .leading, spacing: 16) {
            Text("Déclaration d'accessibilité").font(.largeTitle).accessibilityAddTraits(.isHeader)
            Text("L'application est **Totalement conforme**.")
            
            Link(destination: URL(string: "https://example.com/a11y-contact")!) {
                Label("Signaler un problème d'accessibilité", systemImage: "envelope.fill")
            }
        }
    }
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("Déclaration d'accessibilité", modifier = Modifier.semantics { heading() })
        Text("L'application est Totalement conforme.")
        
        Button(
            onClick = { /* Ouvre intent */ },
            modifier = Modifier.semantics { contentDescription = "Signaler un problème" }
        ) { Text("Signaler un problème") }
    }
    ```

### 2. Voice Control / Voice Access `[AA]`

!!! warning "Commande vocale"
    Pour Voice Control (iOS) ou Voice Access (Android), le texte lu sur le bouton doit correspondre au nom d'accessibilité, pour que l'utilisateur puisse dire "Appuyer sur [Texte]".

=== "SwiftUI"
    ```swift title="✅ Bon"
    Button(action: { }) {
        HStack { Image(systemName: "square.and.arrow.down"); Text("Enregistrer") }
    }
    .accessibilityLabel("Enregistrer le document")
    .accessibilityInputLabels(["Enregistrer", "Sauvegarder", "Enregistrer le document"]) // Optimisé pour la voix
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    Button(onClick = { }) {
        Icon(Icons.Filled.Save, contentDescription = null) // Toujours null quand le bouton a du texte visible
        Text("Enregistrer")
    }
    ```

### 3. Switch Control / Switch Access `[AA]`

=== "SwiftUI"
    ```swift title="✅ Bon"
    HStack {
        Text(itemTitle)
        Spacer()
        Button(action: onDelete) { Image(systemName: "trash") }
            .accessibilityHidden(true) // Caché, l'action est reportée au niveau de la ligne
    }
    .accessibilityElement(children: .combine)
    .accessibilityAction(named: "Supprimer") { onDelete() }
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    Row(
        modifier = Modifier
            .clickable { }
            .semantics(mergeDescendants = true) {
                contentDescription = title
                customActions = listOf(CustomAccessibilityAction("Supprimer l'élément") { onDelete(); true })
            }
    ) {
        Text(title)
        IconButton(
            onClick = onDelete,
            modifier = Modifier.clearAndSetSemantics { } // Cache l'élément de l'arbre sémantique
        ) { Icon(Icons.Filled.Delete, contentDescription = "Supprimer") }
    }
    ```

### 4. Utilisation avancée des APIs (`[AA]`)

=== "SwiftUI"
    ```swift
    // Mise à jour fréquente (Live Region)
    Text("Mise à jour: \(progress)")
        .accessibilityAddTraits(.updatesFrequently)
    ```

=== "Jetpack Compose"
    ```kotlin
    // Politesse (Live Region)
    Text(
        text = errorMessage,
        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite; error(errorMessage) }
    )
    ```

## Checklist [A] / [AA]

- [ ] L'application contient une Déclaration d'accessibilité dans les paramètres.
- [ ] Les contrôles interactifs supportent le contrôle vocal (le label correspond au texte visible).
- [ ] La navigation par Switch Control / Switch Access fonctionne (éléments non piégés, ordre logique, actions sur-mesure pour les swipes).
- [ ] Les icônes accompagnées de texte visible ne définissent pas de `contentDescription` / `accessibilityLabel` redondants.

## Références
- [RAAM 1.1 §12 Documentation](https://accessibilite.public.lu/fr/raam1.1/referentiel-technique.html#topic-12)
- [Apple: Accessibility Input Labels](https://developer.apple.com/documentation/swiftui/view/accessibilityinputlabels(_:)-657we)
