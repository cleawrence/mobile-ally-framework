package com.fram.a11y.skill05

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp

// =============================================================================
// SKILL-05 — Composants Interactifs — Patterns Jetpack Compose
// FRAM : Framework Référence Accessibilité Mobile — v1.0
// Critères RAAM 1.1 : 5.1 à 5.12
// =============================================================================

// MARK: - 1. BUTTON / ICONBUTTON [A] — Critères 5.1, 5.2, 5.3

// ✅ Bon — IconButton avec contentDescription contextuel
@Composable
fun GoodContextualIconButton(itemName: String, onDelete: () -> Unit) {
    IconButton(
        onClick = onDelete,
        modifier = Modifier.semantics {
            contentDescription = "Supprimer l'article $itemName"
            // Role.Button est automatiquement fourni par IconButton
        }
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null // null car défini dans le parent via semantics
        )
    }
    // TalkBack annonce : "Supprimer l'article Chaussures, bouton"
}

// ❌ Mauvais — contentDescription générique (violation critère 5.2)
@Composable
fun BadGenericIconButton(onDelete: () -> Unit) {
    IconButton(onClick = onDelete) {
        Icon(Icons.Default.Delete, contentDescription = "Supprimer")
        // TalkBack : "Supprimer, bouton" — quel article ?
    }
}

// ❌ Mauvais — contentDescription nul sur un icon seul (violation critère 5.1)
@Composable
fun BadNullContentDescription(onDelete: () -> Unit) {
    IconButton(onClick = onDelete) {
        Icon(Icons.Default.Delete, contentDescription = null)
        // TalkBack : peut dire "delete" (nom de la ressource) — non accessible
        // VIOLATION [A]
    }
}

// ✅ Bon — Button standard (rôle automatique via Material3)
@Composable
fun StandardButton(isFormValid: Boolean, onSubmit: () -> Unit) {
    Button(
        onClick = onSubmit,
        enabled = isFormValid
    ) {
        Text("Valider")
    }
    // TalkBack : "Valider, bouton" ou "Valider, bouton, désactivé"
}

// ✅ Bon — élément cliquable custom avec rôle explicite (critère 5.3)
@Composable
fun ClickableRowWithRole(userName: String, onNavigate: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClickLabel = "Voir le profil de $userName"
            ) { onNavigate() }
            .padding(16.dp)
            .semantics {
                role = Role.Button // [A] ✅ Rôle déclaré explicitement
                // contentDescription héritée du onClickLabel
            }
    ) {
        Text("Voir le profil", modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null)
    }
}

// MARK: - 2. SWITCH [A] — Critères 5.3, 5.4

// ✅ Bon — Switch standalone (état restitué automatiquement)
@Composable
fun StandaloneSwitch() {
    var isEnabled by remember { mutableStateOf(false) }

    Switch(
        checked = isEnabled,
        onCheckedChange = { isEnabled = it }
    )
    // TalkBack : "Activé/Désactivé, interrupteur"
    // Le rôle Switch et l'état checked sont automatiques
}

// ✅ Bon — Switch avec label visible, fusionné via mergeDescendants
@Composable
fun LabeledSwitch() {
    var notificationsEnabled by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {} // ✅ Fusionne label + switch
            .clickable { notificationsEnabled = !notificationsEnabled }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Notifications push",
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = notificationsEnabled,
            onCheckedChange = { notificationsEnabled = it }
        )
    }
    // TalkBack : "Notifications push, activé, interrupteur"
}

// MARK: - 3. CHECKBOX [A] — Critères 5.3, 5.4

// ✅ Bon — Checkbox fusionnée avec son label
@Composable
fun AccessibleCheckbox() {
    var isChecked by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .semantics(mergeDescendants = true) {} // ✅ Fusionne visuellement et sémantiquement
            .clickable { isChecked = !isChecked }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { isChecked = it }
        )
        Text(
            text = "Accepter les conditions d'utilisation",
            modifier = Modifier.padding(start = 8.dp)
        )
    }
    // TalkBack : "Accepter les conditions d'utilisation, case à cocher, cochée/décochée"
}

// MARK: - 4. RADIOBUTTON [A] — Critères 5.3, 5.4

// ✅ Bon — RadioButton avec état sélectionné restitué
@Composable
fun AccessibleRadioGroup() {
    val options = listOf("Livraison standard", "Livraison express", "Retrait en magasin")
    var selectedOption by remember { mutableStateOf(options[0]) }

    Column {
        Text(
            text = "Mode de livraison",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() } // [AA] marquer comme titre
        )

        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics(mergeDescendants = true) {}
                    .selectable(
                        selected = selectedOption == option,
                        onClick = { selectedOption = option },
                        role = Role.RadioButton // [A] ✅ Rôle RadioButton
                    )
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedOption == option,
                    onClick = null // géré par le parent selectable
                )
                Text(text = option, modifier = Modifier.padding(start = 8.dp))
            }
            // TalkBack : "Livraison standard, sélectionné, bouton radio" ou "...bouton radio"
        }
    }
}

// MARK: - 5. SLIDER [A] — Critère 5.5

// ✅ Bon — Slider avec valeur courante annoncée
@Composable
fun AccessibleVolumeSlider() {
    var volume by remember { mutableFloatStateOf(50f) }

    Column {
        Text("Volume : ${volume.toInt()}%")
        Slider(
            value = volume,
            onValueChange = { volume = it },
            valueRange = 0f..100f,
            modifier = Modifier.semantics {
                contentDescription = "Volume"
                stateDescription = "${volume.toInt()}%"
                // TalkBack : "Volume, 50%, curseur"
                // Geste : glisser gauche/droite pour ajuster
            }
        )
    }
}

// ✅ Bon — ProgressBar non interactive
@Composable
fun AccessibleProgressBar(progress: Float) {
    LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Progression de l'envoi"
                stateDescription = "${(progress * 100).toInt()}%"
                // [AA] liveRegion pour mises à jour automatiques
                liveRegion = LiveRegionMode.Polite
            }
    )
    // TalkBack annonce la progression au focus et lors des mises à jour
}

// MARK: - 6. TABS [A] — Critère 5.4

// ✅ Bon — TabRow (état sélectionné restitué automatiquement)
@Composable
fun AccessibleTabRow() {
    data class TabItem(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

    val tabs = listOf(
        TabItem("Accueil", Icons.Default.Home),
        TabItem("Recherche", Icons.Default.Search),
        TabItem("Profil", Icons.Default.Person)
    )
    var selectedTab by remember { mutableIntStateOf(0) }

    TabRow(selectedTabIndex = selectedTab) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = selectedTab == index,
                onClick = { selectedTab = index },
                text = { Text(tab.label) },
                icon = { Icon(tab.icon, contentDescription = null) }
                // selected = true → TalkBack annonce "sélectionné"
                // TalkBack : "Accueil, onglet 1 sur 3, sélectionné"
            )
        }
    }
}

// MARK: - 7. GESTES COMPLEXES — ALTERNATIVES [AA] — Critère 5.6

data class Item(val id: String, val name: String)

// ✅ Alternative au swipe-to-dismiss
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissWithAlternative(
    item: Item,
    onDelete: (Item) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState()

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            }
        }
    ) {
        ListItem(
            headlineContent = { Text(item.name) },
            modifier = Modifier.semantics {
                // [AA] ✅ Action alternative accessible au swipe
                customActions = listOf(
                    CustomAccessibilityAction(
                        label = "Supprimer ${item.name}",
                        action = { onDelete(item); true }
                    )
                )
                // TalkBack : menu des actions → "Supprimer Article 1"
            }
        )
    }
}

// ✅ Alternative au drag & drop pour réordonner
@Composable
fun ReorderableListWithAlternatives(
    items: List<Item>,
    onMoveUp: (Int) -> Unit,
    onMoveDown: (Int) -> Unit
) {
    LazyColumn {
        itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
            ListItem(
                headlineContent = { Text(item.name) },
                modifier = Modifier.semantics {
                    customActions = buildList {
                        // [AA] ✅ Actions alternatives au drag & drop
                        if (index > 0) {
                            add(CustomAccessibilityAction("Déplacer ${item.name} vers le haut") {
                                onMoveUp(index); true
                            })
                        }
                        if (index < items.size - 1) {
                            add(CustomAccessibilityAction("Déplacer ${item.name} vers le bas") {
                                onMoveDown(index); true
                            })
                        }
                    }
                }
            )
        }
    }
}

// MARK: - 8. TAILLE DE CIBLE [AA] — Critère 5.7

// ❌ Mauvais — zone de toucher trop petite
@Composable
fun TooSmallButton(onClose: () -> Unit) {
    Icon(
        Icons.Default.Close,
        contentDescription = "Fermer",
        modifier = Modifier
            .size(20.dp) // VIOLATION [AA] : 20dp < 48dp minimum
            .clickable { onClose() }
    )
}

// ✅ Bon — minimumInteractiveComponentSize() (Material 3)
@Composable
fun CorrectSizeIconButton(onClose: () -> Unit) {
    IconButton(
        onClick = onClose,
        modifier = Modifier
            .minimumInteractiveComponentSize() // ✅ Garantit 48×48dp minimum
            .semantics { contentDescription = "Fermer" }
    ) {
        Icon(
            Icons.Default.Close,
            contentDescription = null, // Défini dans le parent
            modifier = Modifier.size(20.dp) // Taille visuelle petite mais zone de tap correcte
        )
    }
}

// ✅ Bon — composant custom avec padding pour atteindre 48dp
@Composable
fun PaddedClickableIcon(onAction: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp) // [AA] ✅ Zone de toucher ≥ 48×48dp
            .clickable(onClickLabel = "Confirmer") { onAction() }
            .semantics { role = Role.Button },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Check,
            contentDescription = null, // Défini via onClickLabel
            modifier = Modifier.size(24.dp) // Icône 24dp dans une zone 48dp
        )
    }
}

// MARK: - 9. FOCUS ET DIALOG [A] — Critère 5.12

// ✅ Bon — Dialog avec gestion du focus (Compose Material3 gère automatiquement)
@Composable
fun AccessibleDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Supprimer l'article ?") },
        text = { Text("Cette action est irréversible.") },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Supprimer") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
    // ✅ Compose Material3 gère automatiquement :
    // - le focus entre dans le Dialog à l'ouverture
    // - le contenu derrière est inaccessible au focus (critère 5.12)
    // - le focus revient au déclencheur à la fermeture
}

// MARK: - 10. ANNONCES D'ÉTAT [A] — Critère 5.4

// ✅ Bon — Live Region pour changements d'état asynchrones
@Composable
fun StateAnnouncementExample() {
    var uploadState by remember { mutableStateOf<UploadState>(UploadState.Idle) }

    Column {
        Button(onClick = { uploadState = UploadState.Uploading }) {
            Text("Envoyer")
        }

        // ✅ Live Region — TalkBack annonce les changements automatiquement
        val statusText = when (uploadState) {
            is UploadState.Idle -> ""
            is UploadState.Uploading -> "Envoi en cours…"
            is UploadState.Success -> "Envoi réussi"
            is UploadState.Failure -> "Échec de l'envoi. Veuillez réessayer."
        }

        if (statusText.isNotEmpty()) {
            Text(
                text = statusText,
                modifier = Modifier.semantics {
                    liveRegion = if (uploadState is UploadState.Failure) {
                        LiveRegionMode.Assertive // Erreur critique — annonce immédiate
                    } else {
                        LiveRegionMode.Polite   // Succès — annonce après fin de l'activité
                    }
                }
            )
        }
    }
}

sealed class UploadState {
    object Idle : UploadState()
    object Uploading : UploadState()
    object Success : UploadState()
    data class Failure(val error: String = "") : UploadState()
}
