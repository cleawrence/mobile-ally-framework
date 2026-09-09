package com.fram.a11y.skill07

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp

// MARK: - 1. HEADINGS [AA] — Critère 7.3, 7.4
// Utiliser Modifier.semantics { heading() } pour définir les titres.

@Composable
fun HeadingsPattern() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // ✅ Bon: Titre principal avec sémantique heading
        Text(
            text = "Profil Utilisateur",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.semantics { heading() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ❌ Mauvais: Titre visuel mais pas de sémantique heading
        Text(
            text = "Informations de contact",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Bon: Sous-titre avec sémantique heading
        Text(
            text = "Préférences",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.semantics { heading() }
        )
    }
}

// MARK: - 2. LISTES [A] — Critère 7.1
// LazyColumn avec des items sémantiques.
// TalkBack annonce nativement les listes dans les LazyColumn,
// mais on peut renforcer la sémantique si nécessaire.

@Composable
fun ListPattern() {
    val itemsList = listOf("Pommes", "Bananes", "Cerises")

    // ✅ Bon: LazyColumn gère la sémantique de liste nativement
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(itemsList.size) { index ->
            ListItem(
                headlineContent = { Text(itemsList[index]) },
                modifier = Modifier.semantics {
                    // Optionnel : renforcement explicite (souvent géré par la LazyColumn)
                    collectionItemInfo = CollectionItemInfo(
                        rowIndex = index,
                        rowSpan = 1,
                        columnIndex = 0,
                        columnSpan = 1
                    )
                }
            )
            Divider()
        }
    }
}

// MARK: - 3. GROUPES
// Utilisation de groupes logiques (ex: Card) avec mergeDescendants

@Composable
fun GroupPattern() {
    // ✅ Bon: La carte groupe ses enfants pour n'être lue qu'une fois
    Card(
        modifier = Modifier
            .padding(16.dp)
            .semantics(mergeDescendants = true) {}
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Offre spéciale", style = MaterialTheme.typography.titleMedium)
            Text("Profitez de -20% aujourd'hui.")
        }
    }
}

// MARK: - 4 & 5. EXEMPLE COMPLET
// Article avec hiérarchie complète (TalkBack navigue par headings avec swipe haut+bas)

@Composable
fun ArticleStructurePattern() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // H1
        Text(
            text = "Comprendre Compose",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.semantics { heading() }
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // H2
        Text(
            text = "Introduction",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.semantics { heading() }
        )
        Text("Compose est un framework UI déclaratif.")

        Spacer(modifier = Modifier.height(16.dp))

        // H2
        Text(
            text = "Avantages",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.semantics { heading() }
        )
        
        // H3
        Text(
            text = "Performance",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() }
        )
        Text("Plus rapide et plus réactif.")
    }
}
