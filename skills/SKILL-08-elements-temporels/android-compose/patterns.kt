package com.fram.a11y.skill08

import android.content.Context
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// MARK: - 1. REDUCE MOTION [A] — Critère 8.6

// Helper pour lire le paramètre système de réduction des animations sur Android
@Composable
fun rememberReduceMotion(): Boolean {
    val context = LocalContext.current
    var reduceMotion by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        val transitionScale = Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.TRANSITION_ANIMATION_SCALE,
            1.0f
        )
        val animatorScale = Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1.0f
        )
        // Si l'échelle est à 0, l'utilisateur a demandé la suppression des animations
        reduceMotion = transitionScale == 0f || animatorScale == 0f
    }
    
    return reduceMotion
}

@Composable
fun ReduceMotionExample() {
    val reduceMotion = rememberReduceMotion()
    var isVisible by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { isVisible = !isVisible }) {
            Text("Basculer")
        }
        
        // [A] Si reduceMotion est vrai, utiliser un fondu simple, sinon glissement
        val enterTransition = if (reduceMotion) fadeIn() else fadeIn() + slideInVertically()
        val exitTransition = if (reduceMotion) fadeOut() else fadeOut() + slideOutVertically()
        
        AnimatedVisibility(
            visible = isVisible,
            enter = enterTransition,
            exit = exitTransition
        ) {
            Text("Contenu Animé", modifier = Modifier.padding(16.dp))
        }
    }
}

// MARK: - 2. TRANSCRIPTION AUDIO [A] — Critère 8.1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioWithTranscriptView() {
    var showTranscript by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Podcast Episode 1", style = MaterialTheme.typography.titleLarge)
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            IconButton(
                onClick = { /* Play */ },
                modifier = Modifier.semantics { contentDescription = "Lire le podcast" }
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null)
            }
            // Surcharge progress: () -> Float remplace progress: Float (déprécié) depuis Material3 1.3
            LinearProgressIndicator(progress = { 0.3f }, modifier = Modifier.weight(1f))
        }
        
        // [A] Bouton pour afficher la transcription
        Button(
            onClick = { showTranscript = true },
            modifier = Modifier.semantics { 
                contentDescription = "Lire la transcription texte du podcast" 
            }
        ) {
            Text("Lire la transcription")
        }
    }
    
    if (showTranscript) {
        ModalBottomSheet(
            onDismissRequest = { showTranscript = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Transcription", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Bonjour et bienvenue dans ce podcast. [bruit de porte] Aujourd'hui nous allons...")
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// MARK: - 3. CONTRÔLES MÉDIAS & CARROUSEL [A] — Critère 8.5

@Composable
fun AccessibleAutoPlayCarousel() {
    var isPlaying by remember { mutableStateOf(true) }
    var currentIndex by remember { mutableStateOf(0) }
    val items = listOf("Image 1", "Image 2", "Image 3")
    
    // Auto-play timer
    LaunchedEffect(isPlaying) {
        while(isPlaying) {
            delay(3000)
            currentIndex = (currentIndex + 1) % items.size
        }
    }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Mock Carrousel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text(items[currentIndex])
        }
        
        // [A] Bouton Pause obligatoire pour les contenus auto-play
        val playPauseDesc = if (isPlaying) "Mettre en pause le carrousel" else "Reprendre le carrousel"
        IconButton(
            onClick = { isPlaying = !isPlaying },
            modifier = Modifier
                .padding(16.dp)
                .semantics { contentDescription = playPauseDesc }
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = null, // Semantics applied to IconButton
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

// MARK: - 4. EXOPLAYER / MEDIA3 [A] & [AA]
// (Note: La configuration détaillée de track selection avec ExoPlayer se fait côté logique Kotlin pure, 
// mais on s'assure d'exposer les boutons pour la transcription et l'audiodescription dans l'UI).

// MARK: - 5. MESSAGES TEMPORAIRES [A] 2.2.1 · [AA] 4.1.3 (WCAG) — Snackbar, Toast, AlertDialog
// Un message qui disparaît seul est un contenu limité dans le temps : un utilisateur de TalkBack
// (lecture linéaire) ou de Switch Access peut ne pas avoir fini de le lire, ni atteint son action.

// ✅ Bon: Snackbar avec action « Annuler » et bouton de fermeture, sans disparition automatique.
// TalkBack annonce le Snackbar à son apparition ; l'utilisateur garde le contrôle de sa fermeture.
@Composable
fun SnackbarWithUndoPattern(onUndo: () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Button(
            modifier = Modifier.padding(paddingValues),
            onClick = {
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "Article supprimé",
                        actionLabel = "Annuler",
                        withDismissAction = true,
                        duration = SnackbarDuration.Indefinite
                    )
                    if (result == SnackbarResult.ActionPerformed) onUndo()
                }
            }
        ) {
            Text("Supprimer l'article")
        }
    }
}

// ✅ Bon: message d'état dans la page (sans overlay) annoncé par une région live
@Composable
fun InlineStatusMessagePattern(status: String) {
    Text(
        text = status, // ex. « 3 résultats trouvés »
        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
    )
}

// ❌ Mauvais: Toast pour une information importante
// Disparaît après ~2 s, sans action ni fermeture possible : VIOLATION [A] 2.2.1.
@Composable
fun BadToastPattern() {
    val context = LocalContext.current
    Button(onClick = {
        Toast.makeText(context, "Paiement refusé, veuillez réessayer", Toast.LENGTH_SHORT).show()
    }) {
        Text("Payer")
    }
}
