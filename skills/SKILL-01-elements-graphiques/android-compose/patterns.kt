package com.fram.a11y.skill01

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
// import coil.compose.AsyncImage

@Composable
fun GraphicElementsPatterns() {
    var isLiked by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {

        // MARK: - 1. ÉLÉMENTS DÉCORATIFS [A] — Critère 1.1
        // ❌ Mauvais : Fournir une description pour une icône de décoration
        // Icon(Icons.Default.Star, contentDescription = "Étoile")

        // ✅ Bon : contentDescription = null masque l'élément pour TalkBack
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null, // Ignoré par les TA
                tint = Color.Yellow
            )
            Text("Favoris")
        }

        // MARK: - 2. ÉLÉMENTS PORTEURS D'INFORMATION [A] — Critères 1.2, 1.3
        // ❌ Mauvais : Préfixe redondant
        // Icon(Icons.Default.Warning, contentDescription = "Icône d'avertissement")

        // ✅ Bon : Message contextuel et concis
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Avertissement : connexion réseau instable",
            tint = Color.Red
        )

        // Rating Stars (Informative)
        Row(modifier = Modifier.semantics(mergeDescendants = true) {
            contentDescription = "Note : 4 sur 5 étoiles"
        }) {
            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= 4) Icons.Default.Star else Icons.Default.Star, // StarOutline ideally
                    contentDescription = null, // handled by parent semantics
                    tint = if (i <= 4) Color.Yellow else Color.Gray
                )
            }
        }

        // MARK: - 3. DESCRIPTION DÉTAILLÉE [A] — Critères 1.6, 1.7
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color.LightGray)
                .semantics {
                    contentDescription = "Graphique des ventes annuelles. Évolution de +20% au premier trimestre."
                }
        )
        Button(onClick = { /* show accessible table */ }) {
            Text("Afficher les données du graphique sous forme de tableau")
        }

        // MARK: - 4. CAPTCHA [A] — Critères 1.4, 1.5
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color.Gray)
                .semantics {
                    contentDescription = "Test de sécurité CAPTCHA. Saisissez les lettres affichées dans l'image."
                }
        )
        TextButton(onClick = { /* play audio */ }) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Text("Écouter l'alternative audio")
        }

        // MARK: - 5. IMAGES TEXTE [AA] — Critère 1.8
        // ❌ Mauvais : Image représentant du texte promotionnel
        // ✅ Bon : Utiliser le composant Text avec du style
        Text(
            text = "Offre Spéciale",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.Red,
            fontWeight = FontWeight.Bold
        )

        // MARK: - 6. IMAGES LÉGENDÉES [AA] — Critère 1.9
        Column(
            modifier = Modifier.semantics(mergeDescendants = true) {} // Combine l'image et sa légende
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null, // Laissé null car la légende porte le sens
                modifier = Modifier.size(64.dp)
            )
            Text("Jean Dupont, Directeur Technique", style = MaterialTheme.typography.bodySmall)
        }

        // MARK: - 7. ICÔNES DANS DES BOUTONS
        // ❌ Mauvais : contentDescription sur l'icône à l'intérieur d'un IconButton
        // IconButton(onClick = {}) { Icon(..., contentDescription = "Favoris") } -> crée parfois des redondances

        // ✅ Bon : Utiliser le contentDescription sur l'Icon (standard Material) ou sur le IconButton si on le surcharge
        IconButton(onClick = { isLiked = !isLiked }) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = if (isLiked) "Retirer des favoris" else "Ajouter aux favoris"
            )
        }

        // MARK: - 8. COIL / ASYNC IMAGE
        // Exemple (Coil commenté pour éviter erreur de compilation sans dépendance)
        /*
        AsyncImage(
            model = "https://example.com/image.jpg",
            contentDescription = "Photo du produit de face",
            modifier = Modifier.fillMaxWidth()
        )
        */

        // MARK: - 9. GRAPHIQUES (Résumé d'accessibilité)
        Box(
            modifier = Modifier
                .semantics {
                    contentDescription = "Répartition du budget : 50% R&D, 30% Marketing, 20% RH"
                }
        ) {
            // Placeholder for a complex Chart library like Vico or MPAndroidChart
        }
    }
}
