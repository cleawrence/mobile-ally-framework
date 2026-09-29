package com.fram.a11y.skill10

import androidx.compose.foundation.focusable
import androidx.compose.ui.Alignment
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp

// MARK: - 1. ORDRE DE FOCUS [A] — Critère 10.2

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FocusOrderCardPattern() {
    Card(modifier = Modifier.padding(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Dans une disposition complexe, nous pouvons forcer l'ordre de navigation avec traversalIndex
            // Un index plus petit est lu en premier
            Text(
                text = "Titre de la carte",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.semantics { traversalIndex = -1f }
            )
            
            Text(
                text = "Description détaillée...",
                modifier = Modifier.semantics { traversalIndex = 0f }
            )
            
            Button(
                onClick = { /* Action */ },
                modifier = Modifier.semantics { traversalIndex = 1f }
            ) {
                Text("Action principale")
            }
        }
    }
}

// MARK: - 2. POSITION DANS L'APP [A] — Critère 10.1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPositionPattern() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Accueil", modifier = Modifier.semantics { heading() }) 
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text("Contenu de l'écran principal")
            
            // Titre de section accessible
            Text(
                text = "Section importante",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.semantics { heading() }
            )
        }
    }
}

// MARK: - 3. FOCUS NON PIÉGÉ [AA] — Critère 10.6

@Composable
fun NonTrappedFocusPattern() {
    var showDialog by remember { mutableStateOf(false) }

    Button(onClick = { showDialog = true }) {
        Text("Ouvrir la boîte de dialogue")
    }

    if (showDialog) {
        // Compose AlertDialog gère nativement le focus trap de façon correcte
        // Le reste de l'écran derrière (arrière-plan) n'est pas atteignable par TalkBack
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Information") },
            text = { Text("Ceci est une modale accessible.") },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Fermer")
                }
            },
            // Important: properties permet de définir des comportements accessibles supplémentaires
            properties = androidx.compose.ui.window.DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        )
    }
}

// MARK: - 4. NAVIGATION COHÉRENTE [AA] — Critère 10.4

@Composable
fun ConsistentNavigationPattern() {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Accueil", "Paramètres")
    val icons = listOf(Icons.Filled.Home, Icons.Filled.Settings)

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = null) },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = { selectedItem = index },
                // Les labels sont lus, et s'ils ne l'étaient pas assez clairement,
                // on pourrait utiliser contentDescription
                modifier = Modifier.semantics {
                    contentDescription = "Onglet $item"
                }
            )
        }
    }
}

// MARK: - 5. FOCUS RETOUR

@Composable
fun FocusReturnPattern() {
    var showDetails by remember { mutableStateOf(false) }
    val triggerFocusRequester = remember { FocusRequester() }

    Column {
        Button(
            onClick = { showDetails = true },
            modifier = Modifier.focusRequester(triggerFocusRequester)
        ) {
            Text("Voir détails")
        }

        if (showDetails) {
            // Note: En mode Dialog (cf. Pattern 3), le système restaure souvent le focus automatiquement.
            // Pour des vues in-line personnalisées, on peut utiliser un bouton "Fermer" qui requête le focus.
            Surface(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Column {
                    Text("Détails affichés")
                    Button(
                        onClick = {
                            showDetails = false
                            // Retourne le focus au bouton déclencheur (surtout utile pour la navigation clavier)
                            triggerFocusRequester.requestFocus()
                        }
                    ) {
                        Text("Fermer les détails")
                    }
                }
            }
        }
    }
}

// MARK: - 6. FOCUS NON MASQUÉ [2.2 · AA] — WCAG 2.4.11 (extension, hors RAAM 1.1)
// L'élément qui a le focus (TalkBack, clavier, Switch Access) ne doit pas être entièrement caché
// par une barre fixe ou par le clavier à l'écran.

// ✅ Bon: Scaffold réserve la place de la barre du bas (paddingValues) et imePadding() fait remonter
// le contenu au-dessus du clavier.
@Composable
fun FocusNotObscuredPattern() {
    Scaffold(
        bottomBar = {
            BottomAppBar {
                Button(onClick = { /* action */ }, modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("Enregistrer")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(16.dp)
        ) {
            repeat(12) { index ->
                var value by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("Champ ${index + 1}") }
                )
            }
        }
    }
}

// ❌ Mauvais: barre superposée sans réserver d'espace ni gérer le clavier : VIOLATION [2.2 · AA] 2.4.11
@Composable
fun FocusObscuredByStickyBarPattern() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
            repeat(12) { index ->
                var value by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("Champ ${index + 1}") }
                )
            }
        }
        Button(
            onClick = { /* action */ },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text("Enregistrer")
        }
    }
}
