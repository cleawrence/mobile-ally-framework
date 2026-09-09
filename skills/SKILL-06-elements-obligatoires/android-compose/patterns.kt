package com.fram.a11y.skill06

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

// MARK: - 1. LANGUE PRINCIPALE [A] — Critère 6.1
// La locale principale est définie par le système Android ou via AppCompatDelegate.setApplicationLocales
// TalkBack utilise la locale du système ou de l'application (resources/configuration locale)

// MARK: - 2. CHANGEMENT DE LANGUE [A] — Critère 6.2
@Composable
fun LanguageChangePattern() {
    Column(modifier = Modifier.padding(16.dp)) {
        // ✅ Bon: Langue du système (français)
        Text("Voici une phrase en français.")

        // ✅ Bon: Utilisation d'AnnotatedString pour des changements de langue dans une même phrase
        // Modifier.semantics { contentLocale = Locale("en") } peut être utilisé (Compose 1.5+) sur un bloc
        // Mais pour du texte en ligne, SpanStyle est préféré.
        Text(
            text = "To be or not to be, that is the question.",
            // Indique explicitement que c'est de l'anglais américain
            style = LocalTextStyle.current.copy(
                localeList = LocaleList(Locale("en-US"))
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Autre approche pour séparer les blocs : termes techniques
        Text(
            buildAnnotatedString {
                append("Le terme technique est ")
                withStyle(style = SpanStyle(localeList = LocaleList(Locale("en-US")))) {
                    append("Smartphone")
                }
                append(".")
            }
        )
    }
}

// MARK: - 3. TITRE D'ÉCRAN [A] — Critères 6.3, 6.4
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenTitlePattern() {
    Scaffold(
        topBar = {
            TopAppBar(
                // ✅ Bon: Titre unique et pertinent, annoncé par TalkBack.
                // Le NavController peut aussi utiliser windowTitle sémantique pour l'écran.
                title = { Text("Paramètres du compte") }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            Text("Contenu des paramètres")
        }
    }
}

// ❌ Mauvais: Titre manquant ou non pertinent
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadScreenTitlePattern() {
    Scaffold(
        topBar = {
            TopAppBar(
                // ❌ Mauvais: "Écran" n'est pas descriptif
                title = { Text("Écran") }
            )
        }
    ) { paddingValues ->
        Text("Contenu", modifier = Modifier.padding(paddingValues))
    }
}

// MARK: - 4. ANNONCE DE NAVIGATION
// Pour une Single Page Application ou changement de contexte
@Composable
fun ScreenChangeAnnouncementPattern() {
    var step by remember { mutableStateOf(1) }
    
    Column(modifier = Modifier.padding(16.dp)) {
        // Le changement de titre dans un TopAppBar annonce souvent la vue, 
        // mais on peut ajouter une sémantique pour forcer l'annonce de la page
        Text(
            text = if (step == 1) "Étape 1 : Panier" else "Étape 2 : Paiement",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.semantics {
                // Donne un repère au lecteur d'écran
                contentDescription = if (step == 1) "Écran du Panier" else "Écran de Paiement"
            }
        )
        
        Button(onClick = { step = 2 }) {
            Text("Passer au paiement")
        }
    }
}
