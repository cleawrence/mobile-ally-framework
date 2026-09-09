// FRAM — Tests unitaires des règles de lint accessibilité
// Vérifie que chaque Detector détecte correctement les violations et ne lève pas de faux positifs

package com.fram.lint

import com.android.tools.lint.checks.infrastructure.TestFiles.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.fram.lint.checks.*
import org.junit.Test

@Suppress("UnstableApiUsage")
class A11yLintChecksTest {

    // ══════════════════════════════════════════════════════════════════════
    //  SKILL-01 — IconContentDescriptionDetector
    // ══════════════════════════════════════════════════════════════════════

    @Test
    fun `Icon sans contentDescription — doit signaler une erreur`() {
        lint()
            .files(
                kotlin("""
                    package test
                    import androidx.compose.material3.Icon
                    import androidx.compose.material.icons.Icons
                    import androidx.compose.material.icons.filled.Warning
                    import androidx.compose.runtime.Composable

                    @Composable
                    fun TestView() {
                        Icon(Icons.Default.Warning) // ❌ Pas de contentDescription
                    }
                """).indented()
            )
            .issues(IconContentDescriptionDetector.ISSUE_ICON_MISSING_CONTENT_DESCRIPTION)
            .run()
            .expect("""
                src/test/TestView.kt:9: Error: Icon() sans contentDescription [IconMissingContentDescription]
                        Icon(Icons.Default.Warning) // ❌ Pas de contentDescription
                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~
                1 errors, 0 warnings
            """.trimIndent())
    }

    @Test
    fun `Icon avec contentDescription = null (décoratif) — pas d'erreur`() {
        lint()
            .files(
                kotlin("""
                    package test
                    import androidx.compose.material3.Icon
                    import androidx.compose.material.icons.Icons
                    import androidx.compose.material.icons.filled.Star
                    import androidx.compose.runtime.Composable

                    @Composable
                    fun TestView() {
                        Icon(Icons.Default.Star, contentDescription = null) // ✅ Décoration explicite
                    }
                """).indented()
            )
            .issues(IconContentDescriptionDetector.ISSUE_ICON_MISSING_CONTENT_DESCRIPTION)
            .run()
            .expectClean()
    }

    @Test
    fun `Icon avec contentDescription vide — doit signaler une erreur`() {
        lint()
            .files(
                kotlin("""
                    package test
                    import androidx.compose.material3.Icon
                    import androidx.compose.material.icons.Icons
                    import androidx.compose.material.icons.filled.Favorite
                    import androidx.compose.runtime.Composable

                    @Composable
                    fun TestView() {
                        Icon(Icons.Default.Favorite, contentDescription = "") // ❌ Vide ≠ null
                    }
                """).indented()
            )
            .issues(IconContentDescriptionDetector.ISSUE_EMPTY_CONTENT_DESCRIPTION)
            .run()
            .expectErrorCount(1)
    }

    @Test
    fun `Icon avec contentDescription prefixe redondant — doit signaler un warning`() {
        lint()
            .files(
                kotlin("""
                    package test
                    import androidx.compose.material3.Icon
                    import androidx.compose.material.icons.Icons
                    import androidx.compose.material.icons.filled.Person
                    import androidx.compose.runtime.Composable

                    @Composable
                    fun TestView() {
                        Icon(Icons.Default.Person, contentDescription = "icône de profil") // ❌ Redondant
                    }
                """).indented()
            )
            .issues(IconContentDescriptionDetector.ISSUE_REDUNDANT_PREFIX_CONTENT_DESCRIPTION)
            .run()
            .expectWarningCount(1)
    }

    // ══════════════════════════════════════════════════════════════════════
    //  SKILL-05 — ClickableWithoutSemanticsDetector
    // ══════════════════════════════════════════════════════════════════════

    @Test
    fun `Modifier clickable sans semantics — doit signaler une erreur`() {
        lint()
            .files(
                kotlin("""
                    package test
                    import androidx.compose.foundation.clickable
                    import androidx.compose.foundation.layout.Box
                    import androidx.compose.runtime.Composable
                    import androidx.compose.ui.Modifier

                    @Composable
                    fun TestView() {
                        Box(modifier = Modifier.clickable { /* action */ }) // ❌ Pas de semantics
                    }
                """).indented()
            )
            .issues(ClickableWithoutSemanticsDetector.ISSUE_CLICKABLE_WITHOUT_SEMANTICS)
            .run()
            .expectErrorCount(1)
    }

    @Test
    fun `Modifier clickable avec semantics role et contentDescription — pas d'erreur`() {
        lint()
            .files(
                kotlin("""
                    package test
                    import androidx.compose.foundation.clickable
                    import androidx.compose.foundation.layout.Box
                    import androidx.compose.runtime.Composable
                    import androidx.compose.ui.Modifier
                    import androidx.compose.ui.semantics.*

                    @Composable
                    fun TestView() {
                        Box(
                            modifier = Modifier
                                .semantics { role = Role.Button; contentDescription = "Voir détails" }
                                .clickable { /* action */ }
                        ) // ✅ Sémantique complète
                    }
                """).indented()
            )
            .issues(ClickableWithoutSemanticsDetector.ISSUE_CLICKABLE_WITHOUT_SEMANTICS)
            .run()
            .expectClean()
    }

    // ══════════════════════════════════════════════════════════════════════
    //  SKILL-09 — TextFieldWithoutLabelDetector
    // ══════════════════════════════════════════════════════════════════════

    @Test
    fun `OutlinedTextField sans label — doit signaler une erreur`() {
        lint()
            .files(
                kotlin("""
                    package test
                    import androidx.compose.material3.OutlinedTextField
                    import androidx.compose.runtime.*

                    @Composable
                    fun TestView() {
                        var text by remember { mutableStateOf("") }
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            placeholder = { Text("votre@email.fr") } // ❌ Placeholder seul
                        )
                    }
                """).indented()
            )
            .issues(TextFieldWithoutLabelDetector.ISSUE_TEXTFIELD_WITHOUT_LABEL)
            .run()
            .expectErrorCount(1)
    }

    @Test
    fun `OutlinedTextField avec label — pas d'erreur`() {
        lint()
            .files(
                kotlin("""
                    package test
                    import androidx.compose.material3.OutlinedTextField
                    import androidx.compose.material3.Text
                    import androidx.compose.runtime.*

                    @Composable
                    fun TestView() {
                        var text by remember { mutableStateOf("") }
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            label = { Text("Email") } // ✅ Label visible et persistant
                        )
                    }
                """).indented()
            )
            .issues(TextFieldWithoutLabelDetector.ISSUE_TEXTFIELD_WITHOUT_LABEL)
            .run()
            .expectClean()
    }

    // ══════════════════════════════════════════════════════════════════════
    //  SKILL-02 — HardcodedColorDetector
    // ══════════════════════════════════════════════════════════════════════

    @Test
    fun `Color(0xFF) hardcodé hors Theme — doit signaler un warning`() {
        lint()
            .files(
                kotlin("""
                    package test
                    import androidx.compose.material3.Text
                    import androidx.compose.runtime.Composable
                    import androidx.compose.ui.graphics.Color

                    @Composable
                    fun TestView() {
                        Text("Titre", color = Color(0xFF333333)) // ❌ Hardcodé
                    }
                """).indented()
            )
            .issues(HardcodedColorDetector.ISSUE_HARDCODED_COLOR)
            .run()
            .expectWarningCount(1)
    }

    // ══════════════════════════════════════════════════════════════════════
    //  SKILL-03 — FixedTextSizeDetector
    // ══════════════════════════════════════════════════════════════════════

    @Test
    fun `fontSize en dp — doit signaler un warning`() {
        lint()
            .files(
                kotlin("""
                    package test
                    import androidx.compose.material3.Text
                    import androidx.compose.runtime.Composable
                    import androidx.compose.ui.unit.dp

                    @Composable
                    fun TestView() {
                        Text("Titre", fontSize = 14.dp.value.sp) // ❌ dp converti en sp
                    }
                """).indented()
            )
            .issues(FixedTextSizeDetector.ISSUE_FIXED_TEXT_SIZE_DP)
            .run()
            .expectWarningCount(1)
    }

    // ══════════════════════════════════════════════════════════════════════
    //  SKILL-11 — TouchTargetDetector
    // ══════════════════════════════════════════════════════════════════════

    @Test
    fun `IconButton avec size 24dp — doit signaler un warning`() {
        lint()
            .files(
                kotlin("""
                    package test
                    import androidx.compose.material3.Icon
                    import androidx.compose.material3.IconButton
                    import androidx.compose.material.icons.Icons
                    import androidx.compose.material.icons.filled.Close
                    import androidx.compose.runtime.Composable
                    import androidx.compose.ui.Modifier
                    import androidx.compose.ui.unit.dp

                    @Composable
                    fun TestView() {
                        IconButton(
                            onClick = {},
                            modifier = Modifier.size(24.dp) // ❌ < 48dp
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Fermer")
                        }
                    }
                """).indented()
            )
            .issues(TouchTargetDetector.ISSUE_SMALL_TOUCH_TARGET)
            .run()
            .expectWarningCount(1)
    }
}
