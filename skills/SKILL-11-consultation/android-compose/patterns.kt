package com.fram.a11y.skill11

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp

// MARK: - 1. ZONE DE TOUCHE [AA] — Critère 11.5
// Minimum 48×48dp (Material Design recommandation)
// .minimumInteractiveComponentSize() ou .size(48.dp)

@Composable
fun TouchTargetPattern() {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        // Inaccessible : trop petit, difficile à toucher (ex: 24.dp sans padding cliquable)
        IconButton(
            onClick = { /* Action */ },
            modifier = Modifier.size(24.dp) // Mauvais
        ) {
            Icon(painterResource(android.R.drawable.ic_menu_edit), contentDescription = "Éditer (Inaccessible)")
        }

        // Accessible : zone de touche minimale assurée (IconButton utilise minimumInteractiveComponentSize par défaut,
        // mais pour des custom views, il faut l'imposer)
        IconButton(
            onClick = { /* Action */ },
            modifier = Modifier.minimumInteractiveComponentSize() // Assure 48x48dp au minimum
        ) {
            Icon(painterResource(android.R.drawable.ic_menu_edit), contentDescription = "Éditer")
        }
    }
}

// MARK: - 2. TIMEOUT [A] — Critère 11.1
// AlertDialog avant expiration de session

@Composable
fun SessionTimeoutPattern() {
    var showTimeoutDialog by remember { mutableStateOf(false) }

    Column {
        Button(
            onClick = { showTimeoutDialog = true },
            modifier = Modifier.minimumInteractiveComponentSize()
        ) {
            Text("Simuler l'expiration")
        }
    }

    if (showTimeoutDialog) {
        AlertDialog(
            onDismissRequest = { /* Empêche la fermeture sans action explicite si sécurité critique */ },
            title = { 
                Text(
                    text = "Expiration de session imminente",
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive }
                ) 
            },
            text = { Text("Votre session expirera dans 2 minutes. Voulez-vous la prolonger ?") },
            confirmButton = {
                TextButton(
                    onClick = { 
                        showTimeoutDialog = false
                        // reset session logic
                    },
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Text("Prolonger")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showTimeoutDialog = false
                        // logout logic
                    },
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Text("Me déconnecter")
                }
            }
        )
    }
}

// MARK: - 3. CONFIRMATION AVANT ACTION IRREVÉRSIBLE [AA]
// AlertDialog pour supprimer/annuler

@Composable
fun RecoverableErrorConfirmationPattern() {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Button(
        onClick = { showDeleteConfirmation = true },
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
        modifier = Modifier.minimumInteractiveComponentSize()
    ) {
        Text("Supprimer le compte")
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Supprimer définitivement ?") },
            text = { Text("Cette action est irréversible. Toutes vos données seront perdues.") },
            confirmButton = {
                Button(
                    onClick = { 
                        showDeleteConfirmation = false
                        // Delete logic
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Text("Oui, supprimer")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = false },
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Text("Annuler")
                }
            }
        )
    }
}

// MARK: - 4. DONNÉES CONSERVÉES APRÈS ERREUR [AA]
// Ne pas réinitialiser les champs de formulaire

@Composable
fun InputRetentionPattern() {
    var email by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = email,
            onValueChange = { 
                email = it
                isError = false // Réinitialiser l'erreur quand l'utilisateur corrige
            },
            label = { Text("Adresse e-mail") },
            isError = isError,
            supportingText = {
                if (isError) {
                    Text(
                        text = "Veuillez entrer une adresse e-mail valide.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                if (!email.contains("@")) {
                    isError = true
                    // La saisie utilisateur (email) reste présente, on ne la vide pas !
                }
            },
            modifier = Modifier.minimumInteractiveComponentSize()
        ) {
            Text("Valider")
        }
    }
}
