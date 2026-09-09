package com.fram.a11y.skill02

import android.content.Context
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

// =============================================================================
// SKILL-02 — Couleurs et Contrastes — Patterns Jetpack Compose
// FRAM : Framework Référence Accessibilité Mobile — v1.0
// Critères RAAM 1.1 : 2.1 [A], 2.2 [AA], 2.3 [AA]
// =============================================================================

// MARK: - 1. INFORMATION PAR COULEUR SEULE [A] — Critère 2.1

// ❌ Mauvais — statut uniquement par couleur — VIOLATION [A] 2.1
@Composable
fun BadColorOnlyStatus() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(Color(0xFF4CAF50)) // Vert seul pour "en ligne"
            // Un utilisateur daltonien (deutéranopie) ne distingue pas vert/rouge
            // TalkBack : rien — aucune info accessible
        )
    }
    // VIOLATION [A] 2.1 : info (statut en ligne) uniquement par couleur
}

// ✅ Bon — statut avec icône + couleur + texte [A]
@Composable
fun GoodStatusBadge(isOnline: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.semantics(mergeDescendants = true) {}
    ) {
        Icon(
            imageVector = if (isOnline) Icons.Default.Circle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null, // Décoratif dans ce groupe
            tint = if (isOnline) Color(0xFF009E73) else Color(0xFF767676), // Palette Okabe-Ito
            modifier = Modifier.size(10.dp)
        )
        Text(
            text = if (isOnline) "En ligne" else "Hors ligne",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
    // TalkBack : "En ligne" ou "Hors ligne" — icône + texte + couleur ✅
}

// ❌ Mauvais — erreur uniquement par bordure colorée — VIOLATION [A] 2.1
@Composable
fun BadErrorFieldColorOnly() {
    var email by remember { mutableStateOf("email-invalide") }
    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Email") },
        isError = true,
        // Pas de supportingText → bordure rouge seule → VIOLATION [A] 2.1
        modifier = Modifier.fillMaxWidth()
    )
}

// ✅ Bon — erreur avec icône + texte + isError [A]
@Composable
fun GoodErrorFieldMultiModal() {
    var email by remember { mutableStateOf("email-invalide") }
    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Email") },
        isError = true,
        leadingIcon = {
            Icon(
                Icons.Default.Warning,
                contentDescription = null, // Décoratif dans ce contexte (info dans error sémantique)
                tint = MaterialTheme.colorScheme.error
            )
        },
        supportingText = {
            // ✅ Texte d'erreur visible + sémantique
            Text(
                text = "L'adresse email doit contenir un @",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.semantics {
                    liveRegion = LiveRegionMode.Polite
                }
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                error("L'adresse email doit contenir un @")
            }
    )
    // TalkBack : "Email, en erreur : L'adresse email doit contenir un @"
    // ✅ Triple info : isError (rouge) + icône + texte
}

// ✅ Bon — lien avec soulignement + couleur [A]
@Composable
fun GoodLinkPattern() {
    Text(
        text = "Voir les conditions d'utilisation",
        color = MaterialTheme.colorScheme.primary,
        textDecoration = TextDecoration.Underline,  // ✅ Soulignement = indicateur non-couleur
        modifier = Modifier.semantics {
            role = Role.Button // Ou utiliser un vrai Button/TextButton
        }
    )
    // ✅ Distinguable par forme (soulignement) ET couleur [A] 2.1
}

// ✅ Bon — graphique avec formes différentes par série [A]
@Composable
fun ColorblindFriendlyChartLegend() {
    // Palette Okabe-Ito — distinguable pour tous les types de daltonisme
    val series = listOf(
        Triple("■", Color(0xFF0072B2), "Revenus"),    // ■ Carré bleu
        Triple("●", Color(0xFFE69F00), "Dépenses"),   // ● Rond orange
        Triple("▲", Color(0xFF009E73), "Bénéfices"),  // ▲ Triangle vert cyan
    )

    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        series.forEach { (shape, color, label) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.semantics(mergeDescendants = true) {}
            ) {
                Text(shape, color = color)  // Forme différente par série
                Text(label, style = MaterialTheme.typography.bodySmall)
            }
            // TalkBack : "■ Revenus", "● Dépenses", "▲ Bénéfices"
            // ✅ Forme + couleur + texte — daltonisme-friendly
        }
    }
}

// MARK: - 2. COULEURS ADAPTATIVES MATERIAL3 [A] — Critères 2.2, 2.3

// ✅ Bon — Material3 ColorScheme conforme WCAG par défaut
@Composable
fun AdaptiveColorPatterns() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        // ✅ onSurface sur Surface = conforme (~14:1 en light, ~13:1 en dark Material baseline)
        Text(
            "Texte principal",
            color = MaterialTheme.colorScheme.onSurface
        )

        // ✅ onSurfaceVariant sur SurfaceVariant = conforme (~4.5:1 Material baseline)
        Text(
            "Texte secondaire",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // ✅ error sur Surface = conforme (rouge Material3 ≥ 4.5:1 en light)
        Text(
            "Texte d'erreur",
            color = MaterialTheme.colorScheme.error
        )

        // ❌ Attention — couleur custom hardcodée
        Text(
            "Texte potentiellement non conforme",
            color = Color(0xFF888888)  // ❌ #888888 sur blanc = 3.54:1 — insuffisant texte normal
        )
    }
}

// ❌ Mauvais — couleur hardcodée qui casse en dark theme — VIOLATION [AA] 2.2
@Composable
fun BadHardcodedColor() {
    Surface(color = MaterialTheme.colorScheme.background) {
        Text(
            "Texte hardcodé",
            color = Color(0xFF333333)
            // En dark theme : fond très sombre + texte sombre → illisible
            // VIOLATION [AA] 2.2
        )
    }
}

// ✅ Bon — couleur custom adaptative via isSystemInDarkTheme
@Composable
fun GoodAdaptiveCustomColor() {
    val isDark = isSystemInDarkTheme()
    Text(
        "Texte adaptatif",
        color = if (isDark) Color(0xFFE0E0E0) else Color(0xFF333333)
        // Light: #333333 sur #FFFFFF = 12.6:1 ✅
        // Dark: #E0E0E0 sur #121212 = 13.9:1 ✅
    )
}

// MARK: - 3. CONTRASTE TEXTE [AA] — Critère 2.2

// ✅ Ratios conformes avec Material3
@Composable
fun TextContrastExamples() {
    val isDark = isSystemInDarkTheme()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        // ✅ Surface baseline Material3 — conforme en light et dark
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    "Titre — onSurface",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                    // Material3 baseline: ~14:1 en light ✅ AAA
                )

                Text(
                    "Corps — onSurface",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    "Secondaire — onSurfaceVariant",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                    // Material3 baseline: ~4.5:1 sur surfaceVariant ✅ AA
                )
            }
        }

        // ⚠️ Placeholder — souvent non conforme sans ajustement
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Email") },
            // Material3 OutlinedTextField : placeholder géré via label flottant
            // → le label flottant est conforme ✅
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// MARK: - 4. CONTRASTE COMPOSANTS UI [AA] — Critère 2.3

// ✅ Bon — bordure de champ conforme (≥ 3:1) [AA]
@Composable
fun ConformantBorderTextField() {
    var text by remember { mutableStateOf("") }
    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        label = { Text("Label") },
        // ✅ OutlinedTextField Material3 : bordure par défaut = onSurfaceVariant
        // onSurfaceVariant sur Surface ≈ 4.5:1 → conforme [AA] 2.3
        modifier = Modifier.fillMaxWidth()
    )
}

// ❌ Mauvais — bordure custom trop claire — VIOLATION [AA] 2.3
@Composable
fun NonConformantCustomBorder() {
    var text by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFCCCCCC), RoundedCornerShape(4.dp)) // ❌ ~1.6:1 sur blanc
            .padding(16.dp)
    ) {
        Text(text.ifEmpty { "Champ texte" }, color = MaterialTheme.colorScheme.onSurface)
    }
    // VIOLATION [AA] 2.3 : la bordure #CCCCCC sur #FFFFFF = 1.6:1 — invisible pour basse vision
}

// ✅ Bon — case à cocher conforme [AA]
@Composable
fun ConformantCheckbox() {
    var checked by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { checked = it },
            colors = CheckboxDefaults.colors(
                uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                // ✅ onSurfaceVariant ≈ 4.5:1 → bordure de la case visible [AA] 2.3
            )
        )
        Text(
            "J'accepte les conditions d'utilisation",
            modifier = Modifier.semantics { role = Role.Checkbox }
        )
    }
}

// ✅ Bon — icône informative avec contraste suffisant [AA]
@Composable
fun ConformantIconContrast() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        // ✅ Icône sur fond blanc — contraste ≥ 3:1
        Icon(
            Icons.Default.Notifications,
            contentDescription = "Notifications",
            tint = MaterialTheme.colorScheme.onSurface
            // onSurface sur Surface ≈ 14:1 ✅
        )

        // ✅ Icône secondaire — contraste ≥ 3:1
        Icon(
            Icons.Default.Info,
            contentDescription = "Informations complémentaires",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
            // onSurfaceVariant sur surfaceVariant ≈ 4.5:1 ✅
        )

        // ❌ Mauvais — icône trop claire — VIOLATION [AA] 2.3
        Icon(
            Icons.Default.Star,
            contentDescription = "Favori",
            tint = Color(0xFFBBBBBB) // ❌ ~1.8:1 sur blanc → icône quasi invisible
        )
    }
}

// MARK: - 5. DALTONISME — PATTERNS MULTI-MODAUX [A]

@Composable
fun ColorblindFriendlyStatusIcons() {
    // Palette Okabe-Ito — accessible pour deutéranopie, protanopie, tritanopie
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        // ✅ Succès : icône ✓ distincte + couleur compatible daltonisme + texte
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.semantics(mergeDescendants = true) {}
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF009E73), // Vert cyan Okabe-Ito
            )
            Text("Paiement accepté", color = MaterialTheme.colorScheme.onSurface)
        }
        // TalkBack : "Paiement accepté" — icône ✓ + couleur + texte ✅

        // ✅ Erreur : icône ✗ distincte + couleur compatible + texte
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.semantics(mergeDescendants = true) {}
        ) {
            Icon(
                Icons.Default.Cancel,
                contentDescription = null,
                tint = Color(0xFFD55E00), // Orange-rouge Okabe-Ito
            )
            Text("Carte refusée", color = MaterialTheme.colorScheme.onSurface)
        }

        // ✅ Avertissement : icône ⚠ + couleur jaune + texte
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.semantics(mergeDescendants = true) {}
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFE69F00), // Jaune Okabe-Ito
            )
            Text("Vérifiez vos informations", color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

// MARK: - Utilitaire — Helper contraste (usage interne/debug)

/**
 * Calcule le ratio de contraste WCAG entre deux couleurs.
 * Utilisation : ContrastChecker.ratio(Color(0xFF767676), Color(0xFFFFFFFF)) → 4.48
 * Référence : https://www.w3.org/TR/WCAG21/#dfn-relative-luminance
 */
object ContrastChecker {
    fun relativeLuminance(color: Color): Double {
        fun channel(c: Float): Double {
            val d = c.toDouble()
            return if (d <= 0.04045) d / 12.92 else Math.pow((d + 0.055) / 1.055, 2.4)
        }
        return 0.2126 * channel(color.red) +
                0.7152 * channel(color.green) +
                0.0722 * channel(color.blue)
    }

    fun ratio(foreground: Color, background: Color): Double {
        val l1 = relativeLuminance(foreground)
        val l2 = relativeLuminance(background)
        val lighter = maxOf(l1, l2)
        val darker = minOf(l1, l2)
        return (lighter + 0.05) / (darker + 0.05)
    }

    fun isAACompliant(foreground: Color, background: Color, isLargeText: Boolean = false): Boolean {
        val r = ratio(foreground, background)
        return if (isLargeText) r >= 3.0 else r >= 4.5
    }
}
