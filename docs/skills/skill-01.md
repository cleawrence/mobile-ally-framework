---
tags:
  - niveau-a
  - niveau-aa
  - skill-01
  - elements-graphiques
---

# SKILL-01 — Éléments Graphiques

:material-image: **Images, icônes, graphiques, CAPTCHA**

## Critères couverts

| Critère RAAM | Intitulé | Niveau | WCAG |
|---|---|---|---|
| 1.1 | Élément graphique de décoration ignoré par les TA | `[A]` | 1.1.1 |
| 1.2 | Élément graphique porteur d'info a une alternative accessible | `[A]` | 1.1.1 |
| 1.3 | L'alternative textuelle est pertinente | `[A]` | 1.1.1 |
| 1.4 | CAPTCHA : l'alternative permet d'identifier sa nature et sa fonction | `[A]` | 1.1.1 |
| 1.5 | CAPTCHA : il existe une alternative non graphique | `[A]` | 1.1.1 |
| 1.6 | Description détaillée fournie si nécessaire | `[A]` | 1.1.1 |
| 1.7 | Description détaillée pertinente | `[A]` | 1.1.1 |
| 1.8 | Images texte remplacées par du texte stylé | `[AA]` | 1.4.5 |
| 1.9 | Éléments graphiques légendés correctement restitués | `[AA]` | 1.1.1 |

## Pourquoi c'est important

!!! warning "Impact utilisateurs"
    Les éléments graphiques représentent une part massive de l'interface mobile. Pour les utilisateurs aveugles, une icône sans label est silencieuse ou annonce son nom de fichier brut. Pour les utilisateurs malvoyants, une image texte devient floue lors de l'agrandissement.

!!! tip "Règle d'or"
    - Décoration → **caché** des technologies d'assistance
    - Information → **label textuel concis et contextuel**
    - Complexe → **description détaillée** accessible

## Patterns

### 1. Éléments décoratifs `[A]`

=== "SwiftUI"
    ```swift title="✅ Bon — Décoration masquée"
    HStack {
        Image(systemName: "star.fill")
            .foregroundStyle(.yellow)
            .accessibilityHidden(true) 
        Text("Favoris")
    }
    ```

    ```swift title="❌ Mauvais — Décoration non masquée"
    HStack {
        Image(systemName: "star.fill")
            .foregroundStyle(.yellow)
            // Pas de .accessibilityHidden(true) !
        Text("Favoris")
    }
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon — contentDescription = null"
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null, // Ignoré par les TA
            tint = Color.Yellow
        )
        Text("Favoris")
    }
    ```

    ```kotlin title="❌ Mauvais — Description sur décoration"
    // Icon(Icons.Default.Star, contentDescription = "Étoile")
    ```

### 2. Éléments porteurs d'information `[A]`

=== "SwiftUI"
    ```swift title="✅ Bon — Label contextuel"
    Image(systemName: "exclamationmark.triangle.fill")
        .foregroundStyle(.yellow)
        .accessibilityLabel("Avertissement : connexion réseau instable")
    ```

    ```swift title="❌ Mauvais — Nom brut ou préfixe redondant"
    Image(systemName: "exclamationmark.triangle.fill")
        .accessibilityLabel("exclamationmark.triangle.fill")
    
    // OU
    Image(systemName: "checkmark.circle.fill")
        .accessibilityLabel("Icône de coche verte")
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon — Message contextuel"
    Icon(
        imageVector = Icons.Default.Warning,
        contentDescription = "Avertissement : connexion réseau instable",
        tint = Color.Red
    )
    ```

    ```kotlin title="❌ Mauvais — Préfixe redondant"
    // Icon(Icons.Default.Warning, contentDescription = "Icône d'avertissement")
    ```

### 3. Notation par étoiles `[A]`

=== "SwiftUI"
    ```swift title="✅ Bon — Groupe ignoré + Label global"
    HStack(spacing: 2) {
        ForEach(0..<total, id: \.self) { index in
            Image(systemName: index < rating ? "star.fill" : "star")
                .foregroundStyle(index < rating ? .yellow : .gray)
                .accessibilityHidden(true)
        }
    }
    .accessibilityElement(children: .ignore)
    .accessibilityLabel("Note : \(rating) étoile\(rating > 1 ? "s" : "") sur \(total)")
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon — mergeDescendants + description"
    Row(modifier = Modifier.semantics(mergeDescendants = true) {
        contentDescription = "Note : 4 sur 5 étoiles"
    }) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= 4) Icons.Default.Star else Icons.Default.Star,
                contentDescription = null,
                tint = if (i <= 4) Color.Yellow else Color.Gray
            )
        }
    }
    ```

### 4. Description détaillée `[A]`

=== "SwiftUI"
    ```swift title="✅ Bon — Graphique avec alternative"
    VStack {
        Chart(salesData) { item in
            BarMark(
                x: .value("Mois", item.month),
                y: .value("Ventes", item.sales)
            )
        }
        .chartAccessibilityLabel("Évolution des ventes — Janvier à Mai 2026")
        
        Button("Voir les données du graphique en tableau") {
            showDataTable = true
        }
        .accessibilityLabel("Voir les données de ventes en tableau")
    }
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon — Sémantique et bouton alternatif"
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .semantics {
                contentDescription = "Graphique des ventes annuelles. Évolution de +20% au premier trimestre."
            }
    )
    Button(onClick = { /* show accessible table */ }) {
        Text("Afficher les données du graphique sous forme de tableau")
    }
    ```

### 5. Boutons iconiques `[A]`

=== "SwiftUI"
    ```swift title="✅ Bon — État dans le label"
    Button {
        isFavorite.toggle()
    } label: {
        Image(systemName: isFavorite ? "heart.fill" : "heart")
            .accessibilityHidden(true)
    }
    .accessibilityLabel(isFavorite ? "Retirer des favoris" : "Ajouter aux favoris")
    ```

    ```swift title="❌ Mauvais — Pas de label"
    Button {
    } label: {
        Image(systemName: "trash")
    }
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon — Label dynamique"
    IconButton(onClick = { isLiked = !isLiked }) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = if (isLiked) "Retirer des favoris" else "Ajouter aux favoris"
        )
    }
    ```

### 6. Images texte `[AA]`

=== "SwiftUI"
    ```swift title="✅ Bon — Texte natif stylisé"
    Text("SOLDES -50%")
        .font(.largeTitle.bold())
        .foregroundStyle(
            LinearGradient(
                colors: [.orange, .red],
                startPoint: .leading,
                endPoint: .trailing
            )
        )
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon — Composant Text avec style"
    Text(
        text = "Offre Spéciale",
        style = MaterialTheme.typography.headlineLarge,
        color = Color.Red,
        fontWeight = FontWeight.Bold
    )
    ```

### 7. Images légendées `[AA]`

=== "SwiftUI"
    ```swift title="✅ Bon — Éléments fusionnés"
    VStack(spacing: 8) {
        AsyncImage(url: URL(string: "https://example.com/team-photo.jpg")) { image in
            image.resizable().scaledToFill()
                .accessibilityHidden(true)
        } placeholder: { Rectangle() }
        
        Text("L'équipe FRAM lors de la réunion de lancement, juillet 2026")
    }
    .accessibilityElement(children: .combine)
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon — mergeDescendants"
    Column(
        modifier = Modifier.semantics(mergeDescendants = true) {}
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )
        Text("Jean Dupont, Directeur Technique", style = MaterialTheme.typography.bodySmall)
    }
    ```

## Checklist Audit Simplifié `[A]`

- [ ] Les icônes purement décoratives sont masquées (`accessibilityHidden(true)` / `contentDescription = null`)
- [ ] Chaque icône d'état a un label descriptif et les notations par étoiles annoncent le total
- [ ] Aucun label ne commence par "image de", "icône de", "photo de"
- [ ] Le CAPTCHA identifie sa nature et sa fonction, et a une alternative non graphique
- [ ] Les graphiques complexes ont une description détaillée ou un accès aux données brutes

## Checklist Audit Complet `[AA]`

- [ ] Aucune image de texte n'est utilisée (sauf logotypes), privilégier le texte natif stylisé
- [ ] Les images avec légende visible sont regroupées sémantiquement
- [ ] L'ordre de lecture image → légende est logique

## Outils

| Outil | Plateforme | Usage |
|---|---|---|
| Accessibility Inspector | iOS | Vérifier Label, Hint, Traits |
| Accessibility Scanner | Android | Signale les Image sans contentDescription |
| Layout Inspector | Android | Vérifier contentDescription dans le SemanticsNode |

## Références

- [RAAM 1.1 §1](https://accessibilite.public.lu/fr/raam1.1/referentiel-technique.html#topic-1)
- [WCAG 1.1.1 (Non-text Content)](https://www.w3.org/Translations/WCAG21-fr/#non-text-content)
- [WCAG 1.4.5 (Images of Text)](https://www.w3.org/Translations/WCAG21-fr/#images-of-text)
