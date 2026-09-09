// FRAM — Android Lint A11y Registry
// Enregistrement de tous les checks d'accessibilité FRAM
// Version : 1.0 — Juillet 2026

package com.fram.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import com.fram.lint.checks.*

/**
 * Registre central des règles de lint accessibilité FRAM.
 *
 * Pour activer dans votre projet Android :
 * ```kotlin
 * // build.gradle.kts (module app)
 * dependencies {
 *     lintChecks(project(":fram-a11y-lint"))
 * }
 * ```
 */
@Suppress("UnstableApiUsage")
class A11yLintRegistry : IssueRegistry() {

    override val issues: List<Issue> = listOf(
        // SKILL-01 — Éléments Graphiques [A]
        IconContentDescriptionDetector.ISSUE_ICON_MISSING_CONTENT_DESCRIPTION,
        IconContentDescriptionDetector.ISSUE_EMPTY_CONTENT_DESCRIPTION,
        IconContentDescriptionDetector.ISSUE_REDUNDANT_PREFIX_CONTENT_DESCRIPTION,

        // SKILL-02 — Couleurs et Contrastes [A]/[AA]
        HardcodedColorDetector.ISSUE_HARDCODED_COLOR,
        HardcodedColorDetector.ISSUE_COLOR_ONLY_STATUS,

        // SKILL-03 — Adaptation / Dynamic Type [AA]
        FixedTextSizeDetector.ISSUE_FIXED_TEXT_SIZE_DP,

        // SKILL-05 — Composants Interactifs [A]
        ClickableWithoutSemanticsDetector.ISSUE_CLICKABLE_WITHOUT_SEMANTICS,
        ClickableWithoutSemanticsDetector.ISSUE_MISSING_ROLE,
        ClickableWithoutSemanticsDetector.ISSUE_EMPTY_SEMANTICS_LABEL,

        // SKILL-09 — Formulaires [A]
        TextFieldWithoutLabelDetector.ISSUE_TEXTFIELD_WITHOUT_LABEL,
        TextFieldWithoutLabelDetector.ISSUE_EMPTY_LABEL,

        // SKILL-10 — Navigation [A]
        NavigationSemanticsDetector.ISSUE_MISSING_SCREEN_TITLE,

        // SKILL-11 — Consultation [AA]
        TouchTargetDetector.ISSUE_SMALL_TOUCH_TARGET,
    )

    override val api: Int = CURRENT_API

    override val minApi: Int = 14

    override val vendor: Vendor = Vendor(
        vendorName = "FRAM — Framework Référence Accessibilité Mobile",
        feedbackUrl = "https://fram.internal/issues",
        contact = "a11y@votre-entreprise.fr"
    )
}
