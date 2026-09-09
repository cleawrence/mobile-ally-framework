---
tags:
  - niveau-a
  - niveau-aa
  - skill-09
---

# SKILL-09 — Formulaires

:material-form-textbox: **Formulaires accessibles, étiquettes, aides à la saisie et gestion des erreurs**

## Critères couverts

| Critère RAAM | Intitulé | Niveau | WCAG |
|---|---|---|---|
| 9.1 | Chaque champ de formulaire a une étiquette | `[A]` | WCAG 1.3.1, 3.3.2 |
| 9.2 | Chaque étiquette de champ est pertinente | `[A]` | WCAG 2.4.6 |
| 9.3 | Les champs obligatoires sont identifiés | `[A]` | WCAG 3.3.2 |
| 9.4 | Les messages d'erreur sont pertinents | `[A]` | WCAG 3.3.1, 3.3.3 |
| 9.5 | Les aides à la saisie sont pertinentes | `[A]` | WCAG 3.3.2 |
| 9.6 | Saisie automatique respecte les finalités | `[AA]` | WCAG 1.3.5 |

## Pourquoi c'est important

!!! warning "Règle d'or"
    Chaque champ doit avoir un **label visible et persistant**, les champs obligatoires doivent être **annoncés textuellement**, et chaque erreur doit être **liée au champ** qui la concerne.

Les formulaires constituent le point de blocage **le plus fréquent** pour les utilisateurs de technologies d'assistance. Un formulaire inaccessible empêche de comprendre ce qu'un champ attend ou comment corriger des erreurs de saisie.

## Patterns

### 1. TextField avec Label `[A]`

=== "SwiftUI"
    ```swift title="✅ Bon"
    VStack(alignment: .leading, spacing: 4) {
        Text("Prénom")
            .font(.caption)
            .foregroundStyle(.secondary)
        TextField("ex. Marie", text: $firstName)
            .accessibilityLabel("Prénom")
    }
    ```
    ```swift title="❌ Mauvais"
    // Placeholder seul (disparaît à la saisie)
    TextField("Prénom", text: $firstName)
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Prénom") }, // Label flottant persistant
        modifier = Modifier.fillMaxWidth()
    )
    ```
    ```kotlin title="❌ Mauvais"
    // Placeholder seul (disparaît à la saisie)
    OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        placeholder = { Text("Prénom") },
        modifier = Modifier.fillMaxWidth()
    )
    ```

### 2. Champs Obligatoires `[A]`

=== "SwiftUI"
    ```swift title="✅ Bon"
    VStack(alignment: .leading, spacing: 4) {
        HStack(spacing: 2) {
            Text("Email")
            Text("*").foregroundStyle(.red).accessibilityHidden(true)
        }
        TextField("Email", text: $email)
            .accessibilityLabel("Email, champ obligatoire")
    }
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Email") },
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Email, champ obligatoire" }
    )
    ```

### 3. Messages d'erreur `[A]`

=== "SwiftUI"
    ```swift title="✅ Bon"
    TextField("ex. prenom@domaine.fr", text: $email)
        .accessibilityLabel("Adresse email" + (emailError != nil ? ", en erreur" : ""))
        .accessibilityHint(emailError ?? "Saisir votre adresse email")
        .overlay(
            RoundedRectangle(cornerRadius: 8)
                .stroke(emailError != nil ? Color.red : Color.gray.opacity(0.5), lineWidth: 1)
        )

    if let error = emailError {
        HStack(spacing: 4) {
            Image(systemName: "exclamationmark.circle.fill").foregroundStyle(.red).accessibilityHidden(true)
            Text(error).font(.caption).foregroundStyle(.red)
        }
        .accessibilityHidden(true) // Info déjà dans accessibilityHint
    }
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Adresse email") },
        isError = emailError != null, 
        supportingText = {
            emailError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
            }
        },
        modifier = Modifier.fillMaxWidth().semantics {
            emailError?.let { error(it) } // Expose l'erreur
        }
    )
    ```

### 4. Aide à la saisie `[A]`

=== "SwiftUI"
    ```swift title="✅ Bon"
    VStack(alignment: .leading, spacing: 4) {
        Text("Nom d'utilisateur")
        TextField("ex. marie_dupont", text: $username)
            .accessibilityHint("Entre 5 et 20 caractères, lettres, chiffres et underscore uniquement")
    }
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    OutlinedTextField(
        value = username,
        onValueChange = { username = it },
        label = { Text("Nom d'utilisateur") },
        supportingText = { Text("Entre 5 et 20 caractères") },
        modifier = Modifier.fillMaxWidth()
    )
    ```

### 5. Saisie Automatique `[AA]`

=== "SwiftUI"
    ```swift title="✅ Bon"
    TextField("Email", text: $email)
        .textContentType(.emailAddress)
        .keyboardType(.emailAddress)
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    OutlinedTextField(
        value = email, onValueChange = { email = it },
        label = { Text("Email") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
    )
    ```

## Checklist [A] / [AA]

- [ ] Chaque champ a un label **visible et persistant**.
- [ ] Les champs obligatoires sont identifiés textuellement.
- [ ] Les messages d'erreur sont pertinents, textuels, et liés au champ.
- [ ] Les types de clavier (email, tel, etc.) et auto-complétion sont utilisés.

## Références
- [RAAM 1.1 §9](https://accessibilite.public.lu/fr/raam1.1/referentiel-technique.html#topic-9)
