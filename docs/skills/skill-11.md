---
tags:
  - niveau-a
  - niveau-aa
  - skill-11
---

# SKILL-11 — Consultation

:material-gesture-tap: **Taille des zones de touche, contrôle du temps et erreurs irréversibles**

## Critères couverts

| Critère RAAM | Intitulé | Niveau | WCAG |
|---|---|---|---|
| 11.1 | L'utilisateur contrôle les limites de temps et éléments en mouvement | `[A]` | WCAG 2.2.1, 2.2.2 |
| 11.5 | Les zones de touche ont une dimension suffisante | `[AA]` | WCAG 2.5.5 |
| 11.6 | Les actions importantes ont une confirmation ou récupération | `[AA]` | WCAG 3.3.4 |

## Pourquoi c'est important

!!! tip "Zones de touche minimales"
    C'est l'une des règles les plus violées. iOS recommande **44x44 pt** et Android **48x48 dp** comme zone minimale de tap. Une icône peut être petite (ex: 20x20) tant que la zone interactive invisible autour est grande.

Ces règles impactent directement la motricité fine (cibler un bouton) et le stress lié au temps. Un utilisateur doit avoir le temps de corriger une action ou comprendre un message.

## Patterns

### 1. Zone de touche `[AA]`

=== "SwiftUI"
    ```swift title="✅ Bon"
    Button(action: { print("Action") }) {
        Image(systemName: "star")
            .resizable()
            .frame(width: 20, height: 20) // Visuel petit
    }
    .frame(minWidth: 44, minHeight: 44) // Zone cliquable large
    .contentShape(Rectangle()) // Indispensable pour rendre le padding cliquable
    .accessibilityLabel("Mettre en favori")
    ```
    ```swift title="❌ Mauvais"
    // Zone trop petite
    Button(action: { }) {
        Image(systemName: "star").frame(width: 20, height: 20)
    }
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    IconButton(
        onClick = { /* Action */ },
        modifier = Modifier.minimumInteractiveComponentSize() // Assure 48x48dp au minimum
    ) {
        Icon(painterResource(android.R.drawable.ic_menu_edit), contentDescription = "Éditer")
    }
    ```
    ```kotlin title="❌ Mauvais"
    IconButton(
        onClick = { },
        modifier = Modifier.size(24.dp) // Zone de touche insuffisante
    ) {
        Icon(painterResource(android.R.drawable.ic_menu_edit), contentDescription = "Éditer")
    }
    ```

### 2. Timeout et Expiration `[A]`

=== "SwiftUI"
    ```swift title="✅ Bon"
    .alert(isPresented: $isTimeoutWarningVisible) {
        Alert(
            title: Text("Expiration de session imminente"),
            message: Text("Votre session expirera dans \(timeRemaining) secondes."),
            primaryButton: .default(Text("Prolonger la session")) { resetSession() },
            secondaryButton: .cancel(Text("Me déconnecter"))
        )
    }
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Expiration de session imminente", modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive }) },
        text = { Text("Voulez-vous la prolonger ?") },
        confirmButton = { TextButton(onClick = { }) { Text("Prolonger") } },
        dismissButton = { TextButton(onClick = { }) { Text("Déconnecter") } }
    )
    ```

### 3. Erreurs Récupérables `[AA]`

=== "SwiftUI"
    ```swift title="✅ Bon"
    Button(role: .destructive) {
        isDeleteConfirmationPresented = true
    } label: { Text("Supprimer le compte") }
    .confirmationDialog("Cette action est irréversible.", isPresented: $isDeleteConfirmationPresented) {
        Button("Supprimer", role: .destructive) { deleteAccount() }
        Button("Annuler", role: .cancel) { }
    }
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    AlertDialog(
        onDismissRequest = { showDeleteConfirmation = false },
        title = { Text("Supprimer définitivement ?") },
        text = { Text("Cette action est irréversible.") },
        confirmButton = { Button(onClick = { /* Delete */ }) { Text("Oui, supprimer") } },
        dismissButton = { TextButton(onClick = { showDeleteConfirmation = false }) { Text("Annuler") } }
    )
    ```

### 4. Données conservées après erreur `[AA]`

!!! example "Règle"
    Si un utilisateur se trompe dans un champ email, n'effacez pas tous les autres champs du formulaire au moment de l'affichage de l'erreur ! Conservez sa saisie.

## Checklist [A] / [AA]

- [ ] Boutons tactiles font au moins 44x44 pt (iOS) ou 48x48 dp (Android).
- [ ] Les sessions expirantes alertent l'utilisateur et peuvent être prolongées.
- [ ] Les suppressions ou paiements exigent une confirmation.
- [ ] Les champs ne sont jamais vidés suite à une erreur de validation.

## Références
- [RAAM 1.1 §11 Consultation](https://accessibilite.public.lu/fr/raam1.1/referentiel-technique.html#topic-11)
- [Apple HIG : Touch Targets](https://developer.apple.com/design/human-interface-guidelines/foundations/accessibility)
