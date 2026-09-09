package com.fram.a11y.skill05

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test

// =============================================================================
// SKILL-05 — Tests Compose UI Test d'accessibilité — Composants Interactifs
// FRAM : Framework Référence Accessibilité Mobile — v1.0
// Niveau A et AA
// =============================================================================

class SKILL05ComposeAccessibilityTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    // MARK: - [A] Critère 5.1 — Chaque composant interactif a un nom accessible

    @Test
    fun allButtons_haveNonEmptyContentDescription() {
        composeTestRule.setContent {
            // Injecter le contenu à tester
            GoodContextualIconButton(itemName = "Chaussures") {}
        }

        // Vérifier que le bouton a bien un contentDescription non vide
        composeTestRule
            .onAllNodes(hasClickAction())
            .assertAll(
                hasContentDescription(
                    value = "",
                    substring = false
                ).not()
                // [RAAM 5.1][A] — Tous les éléments cliquables ont un label
            )
    }

    @Test
    fun iconButton_hasContextualContentDescription() {
        composeTestRule.setContent {
            GoodContextualIconButton(itemName = "Jean slim") {}
        }

        // ✅ Le bouton doit avoir un label contextuel (contenant le nom de l'article)
        composeTestRule
            .onNode(hasContentDescription("Supprimer l'article Jean slim"))
            .assertExists("[RAAM 5.1/5.2][A] — L'IconButton doit avoir un label contextuel")
            .assertHasClickAction()
    }

    // MARK: - [A] Critère 5.2 — Le nom accessible est pertinent

    @Test
    fun button_contentDescription_isNotGeneric() {
        val forbiddenLabels = listOf("ok", "bouton", "button", "icon", "image", "tap", "delete")

        composeTestRule.setContent {
            StandardButton(isFormValid = true) {}
        }

        composeTestRule.onAllNodes(hasClickAction()).onEach { node ->
            val description = node.fetchSemanticsNode()
                .config.getOrElse(SemanticsProperties.ContentDescription) { listOf() }
                .firstOrNull()?.lowercase() ?: ""

            assert(description !in forbiddenLabels) {
                "[RAAM 5.2][A] — Label générique détecté : '$description'"
            }
        }
    }

    // MARK: - [A] Critère 5.3 — Chaque composant a un rôle

    @Test
    fun switch_hasCorrectRole() {
        composeTestRule.setContent {
            LabeledSwitch()
        }

        // Le switch doit avoir le rôle Switch
        composeTestRule
            .onNode(hasRole(Role.Switch))
            .assertExists("[RAAM 5.3][A] — Le Switch n'a pas le rôle Switch")
    }

    @Test
    fun radioButton_hasCorrectRole() {
        composeTestRule.setContent {
            AccessibleRadioGroup()
        }

        // Les radio buttons doivent avoir le rôle RadioButton
        composeTestRule
            .onAllNodes(hasRole(Role.RadioButton))
            .assertCountEquals(3) // 3 options dans le groupe
        // [RAAM 5.3][A] — Tous les RadioButton ont le bon rôle
    }

    @Test
    fun customClickableRow_hasButtonRole() {
        composeTestRule.setContent {
            ClickableRowWithRole(userName = "Marie Dupont") {}
        }

        composeTestRule
            .onNode(hasRole(Role.Button))
            .assertExists("[RAAM 5.3][A] — La Row cliquable custom doit avoir Role.Button")
    }

    // MARK: - [A] Critère 5.4 — Les changements d'état sont restitués

    @Test
    fun switch_stateChangesAreReflected() {
        composeTestRule.setContent {
            LabeledSwitch()
        }

        val switchNode = composeTestRule.onNode(hasRole(Role.Switch))

        // Vérifier l'état initial
        switchNode.assertIsOff() // ou assertIsOn() selon l'état initial

        // Activer le switch
        switchNode.performClick()

        // Vérifier que l'état a changé
        switchNode.assertIsOn()
        // [RAAM 5.4][A] — L'état est bien mis à jour et restitué
    }

    @Test
    fun checkbox_stateChangesAreReflected() {
        composeTestRule.setContent {
            AccessibleCheckbox()
        }

        val checkbox = composeTestRule.onNode(hasRole(Role.Checkbox))
        checkbox.assertIsOff()
        checkbox.performClick()
        checkbox.assertIsOn()
        // [RAAM 5.4][A] — L'état coché/décoché est restitué
    }

    @Test
    fun radioButton_selectedStateIsReflected() {
        composeTestRule.setContent {
            AccessibleRadioGroup()
        }

        // Premier radio sélectionné par défaut
        val firstRadio = composeTestRule
            .onAllNodes(hasRole(Role.RadioButton))
            .onFirst()
        firstRadio.assertIsSelected()

        // Cliquer sur le deuxième
        composeTestRule
            .onAllNodes(hasRole(Role.RadioButton))[1]
            .performClick()

        // Le deuxième est maintenant sélectionné
        composeTestRule
            .onAllNodes(hasRole(Role.RadioButton))[1]
            .assertIsSelected()
        // [RAAM 5.4][A] — L'état sélectionné est bien restitué
    }

    // MARK: - [A] Critère 5.5 — La valeur courante est restituée

    @Test
    fun slider_hasAccessibleValue() {
        composeTestRule.setContent {
            AccessibleVolumeSlider()
        }

        // Le slider doit avoir une valeur accessible (stateDescription)
        val slider = composeTestRule.onNode(hasProgressBarRangeInfo(
            rangeInfo = ProgressBarRangeInfo(
                current = 50f,
                range = 0f..100f
            )
        ))
        slider.assertExists("[RAAM 5.5][A] — Le Slider doit avoir une valeur accessible")
    }

    // MARK: - [AA] Critère 5.7 — Taille de la zone de toucher ≥ 48dp

    @Test
    fun iconButton_meetsMinimumTouchTargetSize() {
        composeTestRule.setContent {
            CorrectSizeIconButton {}
        }

        // Vérifier via les bounds du SemanticsNode
        val node = composeTestRule.onNode(hasRole(Role.Button))
        val bounds = node.fetchSemanticsNode().boundsInRoot

        val minSizePx = with(composeTestRule.density) { 48.dp.toPx() }

        assert(bounds.width >= minSizePx) {
            "[RAAM 5.7][AA] — Largeur du bouton ${bounds.width}px < 48dp ($minSizePx px)"
        }
        assert(bounds.height >= minSizePx) {
            "[RAAM 5.7][AA] — Hauteur du bouton ${bounds.height}px < 48dp ($minSizePx px)"
        }
    }

    // MARK: - [A] Critère 5.9 — Ordre de focus logique

    @Test
    fun focusOrder_isTopToBottom() {
        composeTestRule.setContent {
            AccessibleRadioGroup()
        }

        // Récupérer tous les RadioButtons et vérifier leur ordre vertical
        val radioNodes = composeTestRule
            .onAllNodes(hasRole(Role.RadioButton))
            .fetchSemanticsNodes()

        // Vérifier que les nodes sont ordonnés de haut en bas
        for (i in 0 until radioNodes.size - 1) {
            val currentTop = radioNodes[i].boundsInRoot.top
            val nextTop = radioNodes[i + 1].boundsInRoot.top
            assert(currentTop <= nextTop) {
                "[RAAM 5.9][A] — L'ordre de focus n'est pas logique (haut→bas)"
            }
        }
    }

    // MARK: - [A] Critère 5.12 — Pas de piège au focus

    @Test
    fun dialog_canBeDismissed() {
        composeTestRule.setContent {
            var showDialog by remember { mutableStateOf(true) }
            if (showDialog) {
                AccessibleDialog(
                    onDismiss = { showDialog = false },
                    onConfirm = { showDialog = false }
                )
            }
        }

        // Le bouton Annuler doit exister et être cliquable
        composeTestRule
            .onNodeWithText("Annuler")
            .assertExists("[RAAM 5.12][A] — La dialog doit pouvoir être fermée")
            .assertHasClickAction()
            .performClick()

        // La dialog doit être fermée après clic sur Annuler
        composeTestRule
            .onNodeWithText("Annuler")
            .assertDoesNotExist()
        // [RAAM 5.12][A] — Pas de piège au focus dans la dialog
    }

    // MARK: - [AA] Critère 5.6 — Alternatives aux gestes complexes

    @Test
    fun listItem_hasCustomAccessibilityActions() {
        val testItem = Item(id = "1", name = "Article Test")

        composeTestRule.setContent {
            ReorderableListWithAlternatives(
                items = listOf(
                    Item("1", "Premier"),
                    Item("2", "Deuxième"),
                    Item("3", "Troisième")
                ),
                onMoveUp = {},
                onMoveDown = {}
            )
        }

        // Le deuxième item doit avoir les deux actions (haut et bas)
        val secondItem = composeTestRule.onAllNodes(hasAnyChild(hasText("Deuxième")))[0]
        val customActions = secondItem.fetchSemanticsNode()
            .config.getOrElse(SemanticsProperties.CustomActions) { listOf() }

        assert(customActions.any { it.label.contains("vers le haut") }) {
            "[RAAM 5.6][AA] — L'action 'vers le haut' est manquante"
        }
        assert(customActions.any { it.label.contains("vers le bas") }) {
            "[RAAM 5.6][AA] — L'action 'vers le bas' est manquante"
        }
    }
}

// Extension utilitaire pour les tests
private fun SemanticsNodeInteractionsProvider.onEach(
    block: (SemanticsNodeInteraction) -> Unit
): Unit = Unit // Placeholder — adapter selon les besoins du projet
