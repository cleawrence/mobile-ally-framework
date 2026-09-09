---
tags:
  - niveau-a
  - niveau-aa
  - skill-08
---

# SKILL-08 — Éléments Temporels

:material-motion-pause: **Média, animations, et contrôle du mouvement**

## Critères couverts
| Critère RAAM | Intitulé | Niveau | WCAG |
|---|---|---|---|
| 8.1 | Média audio/vidéo sans image : transcription | `[A]` | 1.2.1 |
| 8.2 | Média synchronisé : sous-titres pour sourds | `[A]` | 1.2.2 |
| 8.3 | Média synchronisé : audiodescription | `[AA]` | 1.2.5 |
| 8.4 | Média en direct : sous-titres en temps réel | `[AA]` | 1.2.4 |
| 8.5 | Contenus en mouvement automatiques : contrôles | `[A]` | 2.2.2 |
| 8.6 | Animations respectent Reduce Motion | `[A]` | 2.3.1, 2.3.3 |

## Pourquoi c'est important
!!! warning "Réduire les animations (Reduce Motion)"
    Les troubles vestibulaires touchent environ 35% des adultes de plus de 40 ans. Une animation de parallaxe ou un zoom rapide peut provoquer des vertiges réels et des nausées. Pensez toujours à désactiver ces animations lorsque l'utilisateur a configuré "Reduce Motion" dans son OS.

## Patterns

### 1. Reduce Motion (Animations adaptées) `[A]`
=== "SwiftUI"
    ```swift title="✅ Bon"
    @Environment(\.accessibilityReduceMotion) private var reduceMotion

    // ...
    RoundedRectangle(cornerRadius: 12)
        .transition(reduceMotion
            ? .opacity // (1)!
            : .move(edge: .bottom).combined(with: .opacity)
        )
    ```

    ```swift title="❌ Mauvais"
    Button("Ouvrir") {
        withAnimation(.spring(response: 0.3, dampingFraction: 0.2)) { // (2)!
            isExpanded.toggle()
        }
    }
    ```

    1. Fondu sécurisé pour les utilisateurs sensibles, au lieu du mouvement.
    2. Animation avec effet de ressort forcé (vertige potentiel).

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    // Voir Helper rememberReduceMotion() dans patterns.kt
    val reduceMotion = rememberReduceMotion()
    
    val enterTransition = if (reduceMotion) fadeIn() else fadeIn() + slideInVertically() // (1)!
    val exitTransition = if (reduceMotion) fadeOut() else fadeOut() + slideOutVertically()
    
    AnimatedVisibility(
        visible = isVisible,
        enter = enterTransition,
        exit = exitTransition
    ) {
        Text("Contenu Animé")
    }
    ```
    
    1. Utilise l'animation système ou détecte si l'échelle est à 0.

### 2. Transcription textuelle (Média audio seul) `[A]`
=== "SwiftUI"
    ```swift title="✅ Bon"
    Button {
        showTranscript = true // (1)!
    } label: {
        Label("Transcription", systemImage: "text.alignleft")
    }
    .accessibilityLabel("Lire la transcription de l'épisode")
    ```
    
    1. Un bouton facilement accessible révèle la transcription complète.

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    Button(
        onClick = { showTranscript = true },
        modifier = Modifier.semantics { 
            contentDescription = "Lire la transcription texte du podcast" 
        }
    ) {
        Text("Lire la transcription")
    }
    ```

### 3. Contrôle des contenus automatiques (Carousels) `[A]`
=== "SwiftUI"
    ```swift title="✅ Bon"
    Button {
        isAutoPlaying.toggle() // (1)!
    } label: {
        Label(
            isAutoPlaying ? "Mettre en pause" : "Reprendre",
            systemImage: isAutoPlaying ? "pause.fill" : "play.fill"
        )
    }
    .accessibilityLabel(isAutoPlaying ? "Mettre en pause" : "Reprendre le défilement")
    ```
    
    1. Présence d'un bouton explicite pour stopper le carrousel. Label adaptatif.

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    val playPauseDesc = if (isPlaying) "Mettre en pause le carrousel" else "Reprendre"
    IconButton(
        onClick = { isPlaying = !isPlaying },
        modifier = Modifier.semantics { contentDescription = playPauseDesc }
    ) {
        Icon(
            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
            contentDescription = null
        )
    }
    ```

### 4. Vidéos et Sous-titres (AVPlayer/ExoPlayer) `[A]`
=== "SwiftUI"
    ```swift title="✅ Bon"
    VideoPlayer(player: player)
        .accessibilityLabel("Lecteur vidéo : Présentation")
    // Note: Utiliser le lecteur vidéo natif de la plateforme permet 
    // l'activation automatique des sous-titres (CC) système.
    ```

## Checklist [A]
- [ ] Tout contenu audio seul a une transcription textuelle accessible.
- [ ] Toutes les vidéos avec parole ont des sous-titres (Closed Captions préférés).
- [ ] Les carousels auto-play ont un bouton Pause/Arrêt accessible.
- [ ] Le bouton Play/Pause a un `accessibilityLabel` adaptatif.
- [ ] L'application lit la préférence Reduce Motion du système.
- [ ] Les animations slide/spring/bounce sont remplacées par fade si Reduce Motion est actif.
- [ ] Aucun élément ne clignote plus de 3 fois par seconde (prévention crise épilepsie).

## Checklist [AA]
- [ ] Les vidéos avec informations visuelles critiques ont une piste d'audiodescription sélectionnable.
- [ ] Les vidéos en direct avec parole ont des sous-titres en temps réel.
- [ ] Une option in-app permet de désactiver les autoplay indépendamment du système.

## Références
*   [RAAM 1.1 §8](https://accessibilite.public.lu/fr/raam1.1/referentiel-technique.html#topic-8)
