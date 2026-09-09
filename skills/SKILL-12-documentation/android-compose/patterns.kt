package com.fram.a11y.skill12

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// MARK: - 1 & 5. DÉCLARATION D'ACCESSIBILITÉ IN-APP [A]

@Composable
fun AccessibilitySettingsScreen() {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Déclaration d'accessibilité",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.semantics { heading() } // [A] Déclare ce texte comme titre
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Nous nous engageons à rendre notre application accessible à tous, conformément aux normes du RAAM 1.1 et RGAA 4.1.")
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "État de conformité",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.semantics { heading() }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("L'application est Totalement conforme avec les critères du RAAM niveau A et AA.")
                Text("Dernière mise à jour : 24 Juillet 2026")
                Text("Propulsé par FRAM 1.0")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Retour d'information et contact",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.semantics { heading() }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text("Si vous n'arrivez pas à accéder à un contenu, vous pouvez nous contacter via le formulaire ci-dessous.")
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com/a11y-contact"))
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Signaler un problème d'accessibilité via un formulaire en ligne"
                    role = Role.Button
                }
        ) {
            Icon(Icons.Filled.Email, contentDescription = null) // Icône purement décorative
            Spacer(modifier = Modifier.width(8.dp))
            Text("Signaler un problème")
        }
    }
}

// MARK: - 2. VOICE ACCESS (Accès vocal Android) [AA]

@Composable
fun VoiceAccessOptimizedButton(onSave: () -> Unit) {
    // Pour Voice Access (commande vocale), l'utilisateur dit "Appuyer sur Enregistrer".
    // Si le contentDescription diffère de "Enregistrer", Voice Access ne le trouvera pas.
    // L'icône est décorative, le texte "Enregistrer" fournit la sémantique textuelle naturellement.
    Button(
        onClick = onSave,
        modifier = Modifier.padding(16.dp)
        // Pas besoin de contentDescription complexe qui casserait Voice Access
    ) {
        Icon(Icons.Filled.Save, contentDescription = null) // [AA] Toujours null quand le bouton a du texte visible
        Spacer(modifier = Modifier.width(8.dp))
        Text("Enregistrer")
    }
}

// MARK: - 3. SWITCH ACCESS (Accès par bouton Android) [AA]

@Composable
fun SwitchAccessFriendlyListItem(title: String, onDelete: () -> Unit) {
    // Dans une liste, la navigation par contacteur passera d'un élément focusable à l'autre.
    // Utiliser CustomAccessibilityAction permet d'ajouter des actions contextuelles au niveau de la rangée.
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Action principale */ }
            .padding(16.dp)
            .semantics(mergeDescendants = true) { // Regroupe pour Switch Access / TalkBack
                contentDescription = title
                customActions = listOf(
                    CustomAccessibilityAction(
                        label = "Supprimer l'élément",
                        action = {
                            onDelete()
                            true
                        }
                    )
                )
            }
    ) {
        Text(text = title, modifier = Modifier.weight(1f))
        
        // Ce bouton d'icône visuel est caché aux services d'accessibilité,
        // car l'action a été promue comme customAction au niveau du Row parent.
        IconButton(
            onClick = onDelete,
            modifier = Modifier.clearAndSetSemantics { } // [AA] Cache l'élément de l'arbre sémantique
        ) {
            Icon(Icons.Filled.Delete, contentDescription = "Supprimer") // Le contentDescription visuel mais invisible A11y
        }
    }
}

// MARK: - 4. APIS COMPLÈTES SEMANTICS COMPOSE [AA]

@Composable
fun ComprehensiveSemanticsView(
    hasError: Boolean = false,
    errorMessage: String = "Saisie incorrecte",
    progress: Float = 0.5f
) {
    Column(modifier = Modifier.padding(16.dp)) {
        
        // States, Live Regions et Error
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(if (hasError) Color.Red.copy(alpha = 0.1f) else Color.Transparent)
                .semantics {
                    // Les LiveRegions forcent TalkBack à annoncer les changements
                    liveRegion = LiveRegionMode.Polite
                    if (hasError) {
                        error(errorMessage)
                    }
                }
        ) {
            Text(if (hasError) errorMessage else "Prêt pour la saisie")
        }
        
        // Progress Bar accessible avec StateDescription et RangeInfo
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Progression du téléchargement"
                    stateDescription = "${(progress * 100).toInt()}% terminé"
                    progressBarRangeInfo = ProgressBarRangeInfo(
                        current = progress,
                        range = 0f..1f
                    )
                },
        )
    }
}
