---
tags:
  - niveau-a
  - niveau-aa
  - skill-02
  - couleurs-contrastes
---

# SKILL-02 — Couleurs et Contrastes

:material-palette: **Couleurs, contrastes, dark mode, daltonisme**

## Critères couverts

| Critère RAAM | Intitulé | Niveau | WCAG |
|---|---|---|---|
| 2.1 | L'information n'est pas donnée uniquement par la couleur | `[A]` | 1.4.1 |
| 2.2 | Rapport de contraste du texte suffisant | `[AA]` | 1.4.3 |
| 2.3 | Rapport de contraste des composants d'interface suffisant | `[AA]` | 1.4.11 |

## Pourquoi c'est important

!!! warning "Impact utilisateurs"
    - **Daltonisme** : L'information "rouge = erreur, vert = succès" est invisible pour les utilisateurs daltoniens.
    - **Basse vision** : Un texte gris clair sur blanc (`#888888` sur `#FFFFFF`) est illisible.
    - **Environnement lumineux** : Le contraste perçu diminue fortement en plein soleil.

!!! tip "Règle d'or"
    - Jamais d'information **uniquement** par la couleur → ajouter icône/forme/texte.
    - Texte normal → ratio **≥ 4.5:1**.
    - Texte large et Composants UI → ratio **≥ 3:1**.

## Patterns

### 1. Information par la couleur seule `[A]`

=== "SwiftUI"
    ```swift title="✅ Bon — Multi-modal (icône + couleur + texte)"
    HStack(spacing: 6) {
        Image(systemName: "circle.fill")
            .foregroundStyle(.green)
            .accessibilityHidden(true)
        Text("En ligne")
            .foregroundStyle(.green)
    }
    .accessibilityElement(children: .combine)
    .accessibilityLabel("Statut : En ligne")
    ```

    ```swift title="❌ Mauvais — Couleur seule"
    Circle()
        .fill(Color.green) // Vert seul pour "en ligne"
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon — Multi-modal"
    Row(modifier = Modifier.semantics(mergeDescendants = true) {}) {
        Icon(
            imageVector = Icons.Default.Circle,
            contentDescription = null,
            tint = Color(0xFF009E73)
        )
        Text("En ligne")
    }
    ```

    ```kotlin title="❌ Mauvais — Couleur seule"
    // Box(modifier = Modifier.background(Color(0xFF4CAF50))) 
    ```

### 2. Liens dans le texte `[A]`

=== "SwiftUI"
    ```swift title="✅ Bon — Lien souligné et coloré"
    Text("Voir les conditions")
        .underline()
        .foregroundStyle(.blue)
        .accessibilityAddTraits(.isLink)
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon — TextDecoration.Underline"
    Text(
        text = "Voir les conditions",
        color = MaterialTheme.colorScheme.primary,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier.semantics { role = Role.Button }
    )
    ```

### 3. Contraste et Couleurs Adaptatives `[AA]`

=== "SwiftUI"
    ```swift title="✅ Bon — Couleurs sémantiques"
    VStack {
        Text("Titre principal")
            .foregroundStyle(.primary) // Adaptatif
        Text("Texte secondaire")
            .foregroundStyle(.secondary)
    }
    ```

    ```swift title="❌ Mauvais — Couleurs en dur"
    Text("Texte sombre")
        .foregroundStyle(Color(hex: "#333333")) // Illisible en mode sombre
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon — MaterialTheme.colorScheme"
    Column {
        Text("Texte principal", color = MaterialTheme.colorScheme.onSurface)
        Text("Texte secondaire", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    ```

    ```kotlin title="❌ Mauvais — Couleurs en dur"
    Text("Texte", color = Color(0xFF333333))
    ```

### 4. Contraste des composants UI `[AA]`

=== "SwiftUI"
    ```swift title="✅ Bon — Bordures visibles"
    TextField("Label", text: $text)
        .overlay(
            RoundedRectangle(cornerRadius: 8)
                .stroke(Color(hex: "#767676"), lineWidth: 1) // Ratio ≥ 3:1
        )
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon — Utilisation des couleurs du thème"
    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        label = { Text("Label") }
        // La bordure utilise onSurfaceVariant (conforme)
    )
    ```

### 5. Palette Daltonisme-Friendly `[A]`

=== "SwiftUI"
    ```swift title="✅ Bon — Palette Okabe-Ito"
    let okabeIto = [
        Color(hex: "#0072B2"), // Bleu
        Color(hex: "#E69F00"), // Orange
        Color(hex: "#009E73"), // Vert cyan
        Color(hex: "#D55E00")  // Orange-rouge
    ]
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon — Palette Okabe-Ito"
    val colorSafeGreen = Color(0xFF009E73)
    val colorSafeRed = Color(0xFFD55E00)
    ```

## Checklist Audit Simplifié `[A]`

- [ ] L'information d'état (succès/erreur/avertissement) est transmise par une icône ET une couleur ET un texte.
- [ ] Les liens dans les paragraphes sont soulignés ou ont un style distinct non basé uniquement sur la couleur.
- [ ] Les graphiques utilisent des couleurs daltonisme-friendly (ex. Okabe-Ito) et des formes variées.

## Checklist Audit Complet `[AA]`

- [ ] Tout texte normal (< 18pt) a un ratio de contraste ≥ 4.5:1.
- [ ] Tout texte grand (≥ 18pt ou gras ≥ 14pt) a un ratio ≥ 3:1.
- [ ] Les icônes informatives, les bordures de champs et les cases à cocher ont un ratio ≥ 3:1 par rapport au fond.
- [ ] L'application reste lisible et accessible en mode sombre.

## Outils

| Outil | Plateforme | Usage |
|---|---|---|
| Colour Contrast Analyser | macOS / Windows | Vérifier le ratio de contraste n'importe où à l'écran |
| Accessibility Inspector | iOS | Audit de contraste intégré à Xcode |
| Android Accessibility Scanner | Android | Détection automatique des problèmes de contraste |

## Références

- [RAAM 1.1 §2](https://accessibilite.public.lu/fr/raam1.1/referentiel-technique.html#topic-2)
- [Colour Contrast Analyser (TPGI)](https://www.tpgi.com/color-contrast-checker/)
