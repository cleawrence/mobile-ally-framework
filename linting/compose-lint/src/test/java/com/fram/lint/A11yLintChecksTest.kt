// FRAM — Tests unitaires des règles de lint accessibilité
// Vérifie que chaque Detector détecte correctement les violations et ne lève pas de faux positifs

package com.fram.lint

import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestFiles.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.fram.lint.checks.*
import org.junit.Test

@Suppress("UnstableApiUsage")
class A11yLintChecksTest {

    // ══════════════════════════════════════════════════════════════════════
    //  Stubs Jetpack Compose minimaux
    //
    //  Le lint-tests harness résout réellement les imports/types (UAST/K2) : sans
    //  ces stubs, chaque test échoue avec "Couldn't resolve this import" avant
    //  même d'atteindre la logique du detector. Les signatures reproduisent la
    //  forme réelle de l'API Compose utilisée par les detectors et les tests.
    // ══════════════════════════════════════════════════════════════════════

    private fun composeStubs(): Array<TestFile> = arrayOf(
        kotlin(
            """
            package androidx.compose.runtime

            import kotlin.reflect.KProperty

            annotation class Composable

            interface MutableState<T> {
                var value: T
            }

            private class SimpleMutableState<T>(override var value: T) : MutableState<T>

            fun <T> mutableStateOf(value: T): MutableState<T> = SimpleMutableState(value)

            inline fun <T> remember(calculation: () -> T): T = calculation()

            operator fun <T> MutableState<T>.getValue(thisRef: Any?, property: KProperty<*>): T = value
            operator fun <T> MutableState<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                this.value = value
            }
            """
        ).indented(),
        kotlin(
            """
            package androidx.compose.ui

            interface Modifier {
                companion object : Modifier
            }
            """
        ).indented(),
        kotlin(
            """
            package androidx.compose.ui.unit

            data class Dp(val value: Float)
            val Int.dp: Dp get() = Dp(this.toFloat())
            val Double.dp: Dp get() = Dp(this.toFloat())

            data class TextUnit(val value: Float)
            val Int.sp: TextUnit get() = TextUnit(this.toFloat())
            val Double.sp: TextUnit get() = TextUnit(this.toFloat())
            val Float.sp: TextUnit get() = TextUnit(this)
            """
        ).indented(),
        kotlin(
            """
            package androidx.compose.ui.graphics

            class Color(val value: Long) {
                companion object {
                    val Unspecified = Color(0)
                    val Red = Color(0xFFFF0000)
                    val Green = Color(0xFF00FF00)
                    val Yellow = Color(0xFFFFFF00)
                }
            }
            """
        ).indented(),
        kotlin(
            """
            package androidx.compose.ui.graphics.vector

            class ImageVector
            """
        ).indented(),
        kotlin(
            """
            package androidx.compose.ui.semantics

            import androidx.compose.ui.Modifier

            class Role private constructor(private val name: String) {
                companion object {
                    val Button = Role("Button")
                    val Checkbox = Role("Checkbox")
                    val RadioButton = Role("RadioButton")
                    val Tab = Role("Tab")
                    val Image = Role("Image")
                    val Switch = Role("Switch")
                }
            }

            class SemanticsPropertyReceiver {
                var role: Role? = null
                var contentDescription: String = ""
            }

            fun Modifier.semantics(properties: SemanticsPropertyReceiver.() -> Unit): Modifier = this
            fun Modifier.clearAndSetSemantics(properties: SemanticsPropertyReceiver.() -> Unit): Modifier = this
            fun Modifier.heading(): Modifier = this
            """
        ).indented(),
        kotlin(
            """
            package androidx.compose.foundation

            import androidx.compose.runtime.Composable
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.graphics.vector.ImageVector

            fun Modifier.clickable(onClick: () -> Unit): Modifier = this
            fun Modifier.combinedClickable(onClick: () -> Unit): Modifier = this
            fun Modifier.selectable(selected: Boolean = false, onClick: () -> Unit): Modifier = this
            fun Modifier.toggleable(value: Boolean = false, onValueChange: (Boolean) -> Unit): Modifier = this

            @Composable
            fun Image(
                imageVector: ImageVector,
                contentDescription: String?,
                modifier: Modifier = Modifier
            ) {}
            """
        ).indented(),
        kotlin(
            """
            package androidx.compose.foundation.layout

            import androidx.compose.runtime.Composable
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.Dp

            @Composable
            fun Box(modifier: Modifier = Modifier, content: @Composable () -> Unit) {}

            fun Modifier.size(size: Dp): Modifier = this
            fun Modifier.size(width: Dp, height: Dp): Modifier = this
            fun Modifier.padding(all: Dp): Modifier = this
            """
        ).indented(),
        kotlin(
            """
            package androidx.compose.material.icons

            object Icons {
                object Filled
                object Default
            }
            """
        ).indented(),
        kotlin(
            """
            package androidx.compose.material.icons.filled

            import androidx.compose.material.icons.Icons
            import androidx.compose.ui.graphics.vector.ImageVector

            val Icons.Filled.Warning: ImageVector get() = ImageVector()
            val Icons.Filled.Star: ImageVector get() = ImageVector()
            val Icons.Filled.Favorite: ImageVector get() = ImageVector()
            val Icons.Filled.Person: ImageVector get() = ImageVector()
            val Icons.Filled.Close: ImageVector get() = ImageVector()
            val Icons.Filled.Error: ImageVector get() = ImageVector()

            val Icons.Default.Warning: ImageVector get() = ImageVector()
            val Icons.Default.Star: ImageVector get() = ImageVector()
            val Icons.Default.Favorite: ImageVector get() = ImageVector()
            val Icons.Default.Person: ImageVector get() = ImageVector()
            val Icons.Default.Close: ImageVector get() = ImageVector()
            val Icons.Default.Error: ImageVector get() = ImageVector()
            """
        ).indented(),
        kotlin(
            """
            package androidx.compose.material3

            import androidx.compose.runtime.Composable
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.graphics.Color
            import androidx.compose.ui.graphics.vector.ImageVector
            import androidx.compose.ui.unit.TextUnit
            import androidx.compose.ui.unit.sp

            @Composable
            fun Text(
                text: String,
                modifier: Modifier = Modifier,
                color: Color = Color.Unspecified,
                fontSize: TextUnit = 0.sp
            ) {}

            @Composable
            fun Icon(
                imageVector: ImageVector,
                contentDescription: String?,
                modifier: Modifier = Modifier,
                tint: Color = Color.Unspecified
            ) {}

            @Composable
            fun IconButton(
                onClick: () -> Unit,
                modifier: Modifier = Modifier,
                content: @Composable () -> Unit
            ) {}

            @Composable
            fun OutlinedTextField(
                value: String,
                onValueChange: (String) -> Unit,
                modifier: Modifier = Modifier,
                label: (@Composable () -> Unit)? = null,
                placeholder: (@Composable () -> Unit)? = null
            ) {}

            @Composable
            fun Scaffold(
                modifier: Modifier = Modifier,
                topBar: @Composable () -> Unit = {},
                content: @Composable (Any) -> Unit
            ) {}

            @Composable
            fun TopAppBar(title: @Composable () -> Unit) {}
            """
        ).indented(),
        kotlin(
            """
            package coil.compose

            import androidx.compose.runtime.Composable
            import androidx.compose.ui.Modifier

            @Composable
            fun AsyncImage(
                model: Any?,
                contentDescription: String?,
                modifier: Modifier = Modifier
            ) {}
            """
        ).indented()
    )

    // ══════════════════════════════════════════════════════════════════════
    //  SKILL-01 — IconContentDescriptionDetector
    // ══════════════════════════════════════════════════════════════════════

    @Test
    fun `Icon sans contentDescription — doit signaler une erreur`() {
        lint()
            .files(
                *composeStubs(),
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
            .expectErrorCount(1)
    }

    @Test
    fun `Icon avec contentDescription = null (décoratif) — pas d'erreur`() {
        lint()
            .files(
                *composeStubs(),
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
                *composeStubs(),
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
                *composeStubs(),
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

    @Test
    fun `AsyncImage sans contentDescription — doit signaler une erreur`() {
        // Régression : le detector filtrait "Icon", "AsyncImage", "Image" en entrée mais le
        // cas "aucun contentDescription du tout" ne testait que methodName == "Icon" -- AsyncImage
        // et Image pouvaient donc être totalement dépourvus de contentDescription sans jamais
        // être signalés.
        lint()
            .files(
                *composeStubs(),
                kotlin("""
                    package test
                    import coil.compose.AsyncImage
                    import androidx.compose.runtime.Composable

                    @Composable
                    fun TestView() {
                        AsyncImage(model = "https://example.com/photo.jpg") // ❌ Pas de contentDescription
                    }
                """).indented()
            )
            .issues(IconContentDescriptionDetector.ISSUE_ICON_MISSING_CONTENT_DESCRIPTION)
            .run()
            .expectErrorCount(1)
    }

    // ══════════════════════════════════════════════════════════════════════
    //  SKILL-05 — ClickableWithoutSemanticsDetector
    // ══════════════════════════════════════════════════════════════════════

    @Test
    fun `Modifier clickable sans semantics — doit signaler une erreur`() {
        lint()
            .files(
                *composeStubs(),
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
    fun `Modifier clickable puis semantics role et contentDescription — pas d'erreur`() {
        lint()
            .files(
                *composeStubs(),
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
                                .clickable { /* action */ }
                                .semantics { role = Role.Button; contentDescription = "Voir détails" }
                        ) // ✅ Sémantique complète
                    }
                """).indented()
            )
            .issues(ClickableWithoutSemanticsDetector.ISSUE_CLICKABLE_WITHOUT_SEMANTICS)
            .run()
            .expectClean()
    }

    @Test
    fun `Modifier semantics puis clickable — pas d'erreur (ordre inverse)`() {
        // Régression : le detector remonte l'arbre UAST (uastParent) depuis le noeud clickable
        // pour chercher "semantics" -- ça ne fonctionne que si .semantics{} est chaîné APRÈS
        // .clickable{} (semantics devient alors l'ancêtre). Dans l'ordre inverse
        // (.semantics{}.clickable{}), semantics est un descendant du noeud clickable, jamais
        // visible en remontant -- l'exemple ✅ du SKILL-05 lui-même utilise cet ordre.
        lint()
            .files(
                *composeStubs(),
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
                        ) // ✅ Sémantique complète, ordre inverse
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
                *composeStubs(),
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
                *composeStubs(),
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
                *composeStubs(),
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
                *composeStubs(),
                kotlin("""
                    package test
                    import androidx.compose.material3.Text
                    import androidx.compose.runtime.Composable
                    import androidx.compose.ui.unit.dp
                    import androidx.compose.ui.unit.sp

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
    //  SKILL-10 — NavigationSemanticsDetector
    // ══════════════════════════════════════════════════════════════════════

    @Test
    fun `Scaffold sans topBar ni heading — doit signaler un warning`() {
        lint()
            .files(
                *composeStubs(),
                kotlin("""
                    package test
                    import androidx.compose.material3.Scaffold
                    import androidx.compose.material3.Text
                    import androidx.compose.runtime.Composable

                    @Composable
                    fun TestView() {
                        Scaffold { padding -> // ❌ Pas de topBar, pas de heading()
                            Text("Contenu")
                        }
                    }
                """).indented()
            )
            .issues(NavigationSemanticsDetector.ISSUE_MISSING_SCREEN_TITLE)
            .run()
            .expectWarningCount(1)
    }

    @Test
    fun `Scaffold avec TopAppBar title — pas d'erreur`() {
        lint()
            .files(
                *composeStubs(),
                kotlin("""
                    package test
                    import androidx.compose.material3.Scaffold
                    import androidx.compose.material3.TopAppBar
                    import androidx.compose.material3.Text
                    import androidx.compose.runtime.Composable

                    @Composable
                    fun TestView() {
                        Scaffold(
                            topBar = { TopAppBar(title = { Text("Mon profil") }) } // ✅ Titre annoncé
                        ) { padding ->
                            Text("Contenu")
                        }
                    }
                """).indented()
            )
            .issues(NavigationSemanticsDetector.ISSUE_MISSING_SCREEN_TITLE)
            .run()
            .expectClean()
    }

    // ══════════════════════════════════════════════════════════════════════
    //  SKILL-11 — TouchTargetDetector
    // ══════════════════════════════════════════════════════════════════════

    @Test
    fun `IconButton avec size 24dp — doit signaler un warning`() {
        lint()
            .files(
                *composeStubs(),
                kotlin("""
                    package test
                    import androidx.compose.material3.Icon
                    import androidx.compose.material3.IconButton
                    import androidx.compose.material.icons.Icons
                    import androidx.compose.material.icons.filled.Close
                    import androidx.compose.runtime.Composable
                    import androidx.compose.ui.Modifier
                    import androidx.compose.foundation.layout.size
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
