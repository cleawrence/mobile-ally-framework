package com.fram.a11y.skill09

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp

// =============================================================================
// SKILL-09 — Formulaires — Patterns Jetpack Compose
// FRAM : Framework Référence Accessibilité Mobile — v1.0
// Critères RAAM 1.1 : 9.1 à 9.6
// =============================================================================

// MARK: - 1. OUTLINEDTEXTFIELD AVEC LABEL [A] — Critères 9.1, 9.2

// ✅ Bon — label flottant (visible et persistant même après saisie)
@Composable
fun GoodLabeledTextField() {
    var name by remember { mutableStateOf("") }

    OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Prénom") }, // ✅ Label flottant persistant — pattern recommandé Material3
        modifier = Modifier.fillMaxWidth()
    )
    // TalkBack avant saisie : "Prénom, champ de saisie, vide"
    // TalkBack pendant saisie : "Prénom, Marie" — label TOUJOURS visible et lu
}

// ❌ Mauvais — placeholder seul (label disparaît à la saisie) — VIOLATION [A] 9.1
@Composable
fun BadPlaceholderOnlyTextField() {
    var name by remember { mutableStateOf("") }

    OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        placeholder = { Text("Prénom") }, // ❌ Disparaît dès que l'utilisateur tape
        // Après saisie : TalkBack dit "Marie, champ de saisie" — l'utilisateur a perdu le label
        modifier = Modifier.fillMaxWidth()
    )
    // VIOLATION [A] 9.1 : l'étiquette n'est plus disponible pendant la saisie
}

// ✅ Bon — champ avec icône décorative (icône masquée)
@Composable
fun TextFieldWithLeadingIcon() {
    var email by remember { mutableStateOf("") }

    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Adresse email") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email, // placeholder icon
                contentDescription = null // ✅ [A] Icône décorative — null = ignorée TalkBack
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = Modifier.fillMaxWidth()
    )
    // TalkBack : "Adresse email, champ de saisie" (icône silencieuse)
}

// MARK: - 2. CHAMPS OBLIGATOIRES [A] — Critère 9.3

// ✅ Bon — mention "(obligatoire)" dans le label flottant
@Composable
fun RequiredFieldWithLabelText() {
    var lastName by remember { mutableStateOf("") }

    OutlinedTextField(
        value = lastName,
        onValueChange = { lastName = it },
        label = { Text("Nom de famille (obligatoire)") },
        // ✅ TalkBack : "Nom de famille (obligatoire), champ de saisie"
        modifier = Modifier.fillMaxWidth()
    )
}

// ✅ Bon — semantics isRequired (Compose 1.5+)
@Composable
fun RequiredFieldWithSemantics() {
    var email by remember { mutableStateOf("") }

    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Email") },
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Email, champ obligatoire"
                // [A] ✅ Propriété sémantique explicite
            }
    )
}

// ❌ Mauvais — astérisque seul non masqué — VIOLATION [A] 9.3
@Composable
fun BadAsteriskOnlyRequired() {
    var email by remember { mutableStateOf("") }

    Column {
        Row {
            Text("Email")
            Text("*", color = MaterialTheme.colorScheme.error)
            // ❌ TalkBack peut lire "astérisque" — confus et non informatif
        }
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth()
        )
        // VIOLATION [A] 9.3 : caractère obligatoire exprimé uniquement par astérisque sans explication
    }
}

// MARK: - 3. MESSAGES D'ERREUR [A] — Critère 9.4

// ✅ Bon — erreur textuelle + isError + live region
@Composable
fun GoodErrorMessage() {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    OutlinedTextField(
        value = email,
        onValueChange = {
            email = it
            emailError = validateEmail(it)
        },
        label = { Text("Adresse email") },
        isError = emailError != null, // ✅ Met le champ en état d'erreur (bordure rouge)
        supportingText = {
            emailError?.let { error ->
                // ✅ Texte d'erreur visible + live region pour annonce TalkBack
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.semantics {
                        liveRegion = LiveRegionMode.Polite // [A] ✅ Annoncé par TalkBack automatiquement
                    }
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                emailError?.let { error ->
                    this.error(error) // [A] ✅ Erreur exposée dans l'arbre d'accessibilité
                    // TalkBack : "Adresse email, en erreur : L'email doit contenir un @"
                }
            }
    )
}

// ❌ Mauvais — erreur uniquement visuelle — VIOLATION [A] 9.4
@Composable
fun BadVisualOnlyError() {
    var email by remember { mutableStateOf("") }
    val hasError = email.isNotEmpty() && !email.contains("@")

    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Email") },
        isError = hasError, // Bordure rouge uniquement
        // ❌ Pas de supportingText avec message → TalkBack ne sait pas l'erreur
        modifier = Modifier.fillMaxWidth()
    )
    // VIOLATION [A] 9.4 : erreur portée uniquement par la couleur (bordure rouge)
}

// ❌ Mauvais — message d'erreur générique — VIOLATION [A] 9.4
@Composable
fun BadGenericErrorMessage() {
    var email by remember { mutableStateOf("") }

    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Email") },
        isError = true,
        supportingText = { Text("Erreur") }, // ❌ Trop vague — que doit corriger l'utilisateur ?
        modifier = Modifier.fillMaxWidth()
    )
    // VIOLATION [A] 9.4 : le message ne permet pas à l'utilisateur de corriger sa saisie
}

private fun validateEmail(email: String): String? {
    return when {
        email.isEmpty() -> null
        !email.contains("@") -> "L'adresse email doit contenir un @. Exemple : prenom@domaine.fr"
        !email.contains(".") -> "L'adresse email doit contenir un point après le @"
        else -> null
    }
}

// MARK: - 4. AIDE À LA SAISIE [A] — Critère 9.5

// ✅ Bon — aide visible + accessible via supportingText
@Composable
fun InputHintPatterns() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var iban by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        // ✅ Hint via supportingText (toujours visible)
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nom d'utilisateur") },
            placeholder = { Text("ex. marie_dupont") }, // Exemple de format
            supportingText = { Text("Entre 5 et 20 caractères, lettres, chiffres et _ uniquement") },
            modifier = Modifier.fillMaxWidth()
            // TalkBack : lit le label flottant puis le supportingText
        )

        // ✅ Hint via placeholder d'exemple (complète, ne remplace pas le label)
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe") },
            supportingText = { Text("Minimum 8 caractères, une majuscule, un chiffre") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        // ✅ Champ IBAN avec format et aide
        OutlinedTextField(
            value = iban,
            onValueChange = { iban = it },
            label = { Text("IBAN") },
            placeholder = { Text("FR76 0000 0000 0000 0000 0000 000") },
            supportingText = { Text("Code bancaire international, 27 caractères pour la France") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Characters
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// MARK: - 5. MOT DE PASSE [A]

// ✅ Bon — SecureField avec toggle afficher/masquer accessible
@Composable
fun AccessiblePasswordField() {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Mot de passe") },
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    // ✅ Label du bouton change selon l'état [A]
                    contentDescription = if (passwordVisible) {
                        "Masquer le mot de passe"
                    } else {
                        "Afficher le mot de passe"
                    }
                )
            }
            // TalkBack : "Afficher le mot de passe, bouton"
            // Après tap : "Masquer le mot de passe, bouton"
        },
        supportingText = { Text("Minimum 8 caractères, une majuscule, un chiffre") },
        modifier = Modifier.fillMaxWidth()
    )
}

// MARK: - 6. EXPOSEDDROPDOWNMENU / SELECT [A]

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessibleDropdownMenu() {
    val deliveryOptions = listOf("Standard (3-5 jours)", "Express (24h)", "Retrait en magasin")
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(deliveryOptions[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            readOnly = true,
            value = selectedOption,
            onValueChange = {},
            label = { Text("Mode de livraison") }, // [A] ✅ Label descriptif
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            // TalkBack : "Mode de livraison, Standard (3-5 jours), menu déroulant"
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            deliveryOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        selectedOption = option
                        expanded = false
                    }
                )
            }
        }
    }
}

// MARK: - 7. SAISIE AUTOMATIQUE [AA] — Critère 9.6

// ✅ Tableau complet des KeyboardType recommandés par type de champ [AA]
@Composable
fun AutocompletePatternsFull() {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Identité
        Text("Identité", style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() })

        OutlinedTextField(
            value = firstName, onValueChange = { firstName = it },
            label = { Text("Prénom") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,           // [AA] Clavier texte standard
                capitalization = KeyboardCapitalization.Words, // [AA] Majuscule en début de mot
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = lastName, onValueChange = { lastName = it },
            label = { Text("Nom de famille") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Coordonnées
        Text("Coordonnées", style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() })

        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,          // [AA] ✅ Clavier email (avec @)
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phone, onValueChange = { phone = it },
            label = { Text("Téléphone") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,          // [AA] ✅ Clavier téléphone
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = website, onValueChange = { website = it },
            label = { Text("Site web") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,            // [AA] ✅ Clavier URL
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Adresse
        Text("Adresse", style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() })

        OutlinedTextField(
            value = postalCode, onValueChange = { postalCode = it },
            label = { Text("Code postal") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,         // [AA] ✅ Clavier numérique
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Sécurité
        Text("Sécurité", style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() })

        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Mot de passe") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,       // [AA] ✅ Clavier sécurisé (masque le texte)
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// MARK: - 8. REGROUPEMENT DE CHAMPS [A]

// ✅ Bon — section avec en-tête déclaré comme heading
@Composable
fun FieldGroupingPatterns() {
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ✅ En-tête de groupe — TalkBack annonce "Adresse de facturation, en-tête"
        Text(
            text = "Adresse de facturation",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() } // [A] ✅ Rôle heading pour le groupe
        )

        OutlinedTextField(
            value = street, onValueChange = { street = it },
            label = { Text("Numéro et rue") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = postalCode, onValueChange = { postalCode = it },
                label = { Text("Code postal") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = city, onValueChange = { city = it },
                label = { Text("Ville") },
                modifier = Modifier.weight(2f)
            )
        }
    }
}

// MARK: - 9. FORMULAIRE DE CONNEXION COMPLET [A + AA]

@Composable
fun AccessibleLoginForm() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // En-tête de formulaire
        Text(
            "Connexion",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.semantics { heading() } // [A] ✅
        )

        // Légende des champs obligatoires
        Text(
            "Les champs marqués (obligatoire) sont requis",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Champ Email
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
            },
            label = { Text("Email (obligatoire)") },    // [A] ✅ Label persistant + obligatoire
            isError = emailError != null,
            supportingText = {
                emailError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.semantics {
                            liveRegion = LiveRegionMode.Polite // [A] ✅ Annonce automatique
                        }
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,          // [AA] ✅
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { passwordFocusRequester.requestFocus() }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(emailFocusRequester)
                .semantics {
                    emailError?.let { this.error(it) }      // [A] ✅ Erreur sémantique
                }
        )

        // Champ Mot de passe
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
            },
            label = { Text("Mot de passe (obligatoire)") }, // [A] ✅
            isError = passwordError != null,
            visualTransformation = if (isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            supportingText = {
                passwordError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.semantics {
                            liveRegion = LiveRegionMode.Polite // [A] ✅
                        }
                    )
                }
            },
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) {
                            Icons.Filled.VisibilityOff
                        } else {
                            Icons.Filled.Visibility
                        },
                        contentDescription = if (isPasswordVisible) {  // [A] ✅ Label adaptatif
                            "Masquer le mot de passe"
                        } else {
                            "Afficher le mot de passe"
                        }
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,        // [AA] ✅
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    submitLoginForm(email, password,
                        onEmailError = { emailError = it },
                        onPasswordError = { passwordError = it },
                        onSuccess = { isSubmitting = true }
                    )
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(passwordFocusRequester)
                .semantics {
                    passwordError?.let { this.error(it) }   // [A] ✅
                }
        )

        // Bouton de soumission
        Button(
            onClick = {
                keyboardController?.hide()
                submitLoginForm(email, password,
                    onEmailError = {
                        emailError = it
                        emailFocusRequester.requestFocus() // [A] ✅ Focus sur le champ en erreur
                    },
                    onPasswordError = {
                        passwordError = it
                        passwordFocusRequester.requestFocus() // [A] ✅
                    },
                    onSuccess = { isSubmitting = true }
                )
            },
            enabled = !isSubmitting,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Se connecter")
        }
        // TalkBack : "Se connecter, bouton" (ou "Se connecter, bouton, désactivé" si en cours)
    }
}

private fun submitLoginForm(
    email: String,
    password: String,
    onEmailError: (String) -> Unit,
    onPasswordError: (String) -> Unit,
    onSuccess: () -> Unit
) {
    when {
        email.isEmpty() -> onEmailError("L'adresse email est obligatoire")
        !email.contains("@") -> onEmailError("L'adresse email doit contenir un @. Exemple : prenom@domaine.fr")
        password.isEmpty() -> onPasswordError("Le mot de passe est obligatoire")
        password.length < 8 -> onPasswordError("Le mot de passe doit contenir au moins 8 caractères")
        else -> onSuccess()
    }
}
