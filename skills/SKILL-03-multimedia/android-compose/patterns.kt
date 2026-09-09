package com.fram.a11y.skill03

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// MARK: - 1. INFO PAR POSITION [A] — Critère 3.1
// ❌ Mauvais
@Composable
fun BadInstructionText() {
    Text("Appuyez sur le bouton flottant en bas à droite.")
}

// ✅ Bon
@Composable
fun GoodInstructionText() {
    Text("Appuyez sur le bouton 'Ajouter'.")
}


// MARK: - 2. ORIENTATION [AA] — Critère 3.2
// Pattern: layout qui s'adapte à l'orientation / WindowSizeClass
@Composable
fun AdaptiveOrientationLayout() {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(Modifier.weight(1f).fillMaxHeight().background(Color.LightGray)) // Image/Media
            Column(Modifier.weight(1f)) {
                Text("Titre de l'article", style = MaterialTheme.typography.headlineSmall)
                Text("Description qui prend tout l'espace disponible à droite.", style = MaterialTheme.typography.bodyMedium)
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(Modifier.fillMaxWidth().height(200.dp).background(Color.LightGray)) // Image/Media
            Text("Titre de l'article", style = MaterialTheme.typography.headlineSmall)
            Text("Description en dessous en mode portrait.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}


// MARK: - 3. DYNAMIC TYPE / FONT SCALE [AA] — Critère 3.3
// ❌ Mauvais: utiliser dp pour le texte
@Composable
fun BadDynamicType() {
    Text(
        text = "Texte fixe non accessible",
        fontSize = 16.dp.value.sp // anti-pattern, force une taille fixe
    )
}

// ✅ Bon: utiliser MaterialTheme ou sp
@Composable
fun GoodDynamicType() {
    Text(
        text = "Texte qui grandit avec le système",
        style = MaterialTheme.typography.bodyLarge
        // Ou fontSize = 16.sp
    )
}


// MARK: - 4. REFLOW [AA] — Critère 3.4
// Pas de scroll horizontal, passage à la ligne (Reflow)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReflowLayout() {
    // FlowRow distribue les éléments à la ligne suivante s'il n'y a plus de place,
    // ce qui est parfait pour l'accessibilité à grande taille de texte.
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = { }) {
            Text("Accepter les conditions")
        }
        OutlinedButton(onClick = { }) {
            Text("Refuser")
        }
    }
}


// MARK: - 5. EXAMPLE COMPLET — Carte adaptive
@Composable
fun AdaptiveCard(title: String, description: String) {
    // Obtenir le font scale actuel
    val fontScale = LocalDensity.current.fontScale
    
    // Adapter la taille de l'icône selon le texte
    val iconSize = (24 * fontScale).dp
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Reflow adaptatif de l'entête
            if (fontScale > 1.5f) {
                // Très grande taille : empiler icône et titre
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            } else {
                // Taille normale : aligner sur la même ligne
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(iconSize),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                // On ne limite pas le nombre de lignes (maxLines) pour permettre la lecture totale
            )
        }
    }
}
