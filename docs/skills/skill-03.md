---
tags:
  - niveau-a
  - niveau-aa
  - skill-03
  - adaptation-presentation
---

# SKILL-03 — Adaptation et Présentation

:material-format-size: **Dynamic Type, Orientation, Reflow, Présentation**

## Critères couverts

| Critère RAAM | Intitulé | Niveau | WCAG |
|---|---|---|---|
| 3.1 | L'information n'est pas véhiculée uniquement par la forme, la taille, la position ou l'orientation | `[A]` | 1.3.3 |
| 3.2 | L'information n'est pas perdue lorsque l'orientation de l'écran change | `[AA]` | 1.3.4 |
| 3.3 | Le texte peut être redimensionné sans perte de lisibilité | `[AA]` | 1.4.4 |
| 3.4 | Le contenu s'adapte à 200% de zoom sans défilement horizontal (Reflow) | `[AA]` | 1.4.10 |

## Pourquoi c'est important

!!! warning "Impact utilisateurs"
    - **Dynamic Type** : Très utilisé pour agrandir le texte par les utilisateurs malvoyants. Un texte fixe (`14.dp` / `14pt`) devient vite illisible.
    - **Orientation** : Des utilisateurs avec handicap moteur utilisant un support fixe ne peuvent pas tourner leur appareil. Verrouiller le portrait est excluant.
    - **Indépendance visuelle** : L'instruction "Cliquez à droite" n'a aucun sens pour un non-voyant.

!!! tip "Règle d'or"
    - Évitez les contraintes de hauteur (hauteur fixe qui coupe le texte).
    - Laissez l'application tourner en mode paysage.
    - Utilisez les polices système dynamiques.

## Patterns

### 1. Information par position ou forme `[A]`

=== "SwiftUI"
    ```swift title="✅ Bon — Instructions explicites"
    Text("Appuyez sur le bouton 'Continuer' pour passer à l'étape suivante.")
    ```

    ```swift title="❌ Mauvais — Référence visuelle"
    Text("Cliquez sur le bouton vert en haut à droite pour continuer.")
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon — Texte clair"
    Text("Appuyez sur le bouton 'Ajouter'.")
    ```

    ```kotlin title="❌ Mauvais — Référence spatiale"
    Text("Appuyez sur le bouton flottant en bas à droite.")
    ```

### 2. Adaptation à l'orientation `[AA]`

=== "SwiftUI"
    ```swift title="✅ Bon — Utilisation de SizeClass"
    @Environment(\.horizontalSizeClass) var horizontalSizeClass
    
    Group {
        if horizontalSizeClass == .regular {
            HStack { /* Contenu côte-à-côte */ }
        } else {
            VStack { /* Contenu empilé */ }
        }
    }
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon — Vérification Configuration"
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    if (isLandscape) {
        Row { /* Contenu côte-à-côte */ }
    } else {
        Column { /* Contenu empilé */ }
    }
    ```

### 3. Textes dynamiques (Dynamic Type) `[AA]`

=== "SwiftUI"
    ```swift title="✅ Bon — Polices sémantiques et @ScaledMetric"
    @ScaledMetric(relativeTo: .body) var iconSize: CGFloat = 24
    
    HStack {
        Image(systemName: "star")
            .font(.system(size: iconSize))
        Text("Favori")
            .font(.body) // S'adapte au système
    }
    .frame(minHeight: 44) // minHeight au lieu de height
    ```

    ```swift title="❌ Mauvais — Tailles et hauteurs fixes"
    HStack {
        Image(systemName: "star").font(.system(size: 20))
        Text("Favori").font(.system(size: 16))
    }
    .frame(height: 44) // Coupe le texte s'il grandit
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon — Utilisation des sp"
    Text(
        text = "Texte qui grandit avec le système",
        style = MaterialTheme.typography.bodyLarge
    )
    ```

    ```kotlin title="❌ Mauvais — Utilisation des dp pour du texte"
    Text(
        text = "Texte fixe",
        fontSize = 16.dp.value.sp // Force une taille fixe
    )
    ```

### 4. Reflow (Passage à la ligne) `[AA]`

=== "SwiftUI"
    ```swift title="✅ Bon — ViewThatFits"
    ViewThatFits(in: .horizontal) {
        // Essaie d'abord en horizontal
        HStack { ActionButtons() }
        
        // Si pas de place, passe en vertical
        VStack { ActionButtons() }
    }
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon — FlowRow"
    @OptIn(ExperimentalLayoutApi::class)
    FlowRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(onClick = { }) { Text("Accepter") }
        OutlinedButton(onClick = { }) { Text("Refuser") }
    }
    ```

## Checklist Audit Simplifié `[A]`

- [ ] Les instructions ne font pas référence à la forme, couleur, taille ou position spatiale (ex: "en haut à droite").
- [ ] Les retours d'erreur ne sont pas uniquement indiqués par la couleur.
- [ ] Les éléments graphiques portent des noms sémantiques clairs.

## Checklist Audit Complet `[AA]`

- [ ] L'application n'est pas verrouillée en orientation Portrait ou Paysage sans raison légitime.
- [ ] Le texte grossit lorsque les préférences d'accessibilité du système sont ajustées (Dynamic Type / Font Scale).
- [ ] À 200% de zoom de texte, aucun texte n'est tronqué.
- [ ] À 200% de zoom, le contenu s'affiche sans nécessiter de défilement horizontal (Reflow).
- [ ] Les icônes proches du texte grandissent proportionnellement à celui-ci.

## Outils

| Outil | Plateforme | Usage |
|---|---|---|
| Environment Overrides | iOS | Xcode permet de modifier le Dynamic Type à la volée |
| Accessibilité | iOS / Android | Modifier la taille du texte dans les paramètres système de l'appareil pour tester manuellement |
