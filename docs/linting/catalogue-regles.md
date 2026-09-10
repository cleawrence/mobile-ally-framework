---
tags:
  - linting
  - catalogue
---

# Catalogue complet des règles

## Résumé

| Plateforme | Règles | Error | Warning |
|---|---|---|---|
| :material-apple: SwiftLint | 17 | 3 | 14 |
| :material-android: Compose Lint | 13 issues (7 détecteurs) | 6 | 7 |
| **Total** | **30** | **9** | **21** |

---

## Toutes les règles — SwiftLint (iOS)

| # | ID Règle | Skill | Critère | Niveau | Sévérité |
|---|---|---|---|---|---|
| 1 | `a11y_image_systemname_no_a11y` | SKILL-01 | 1.1, 1.2 | `[A]` | Error |
| 2 | `a11y_label_redundant_prefix` | SKILL-01 | 1.3 | `[A]` | Warning |
| 3 | `a11y_image_name_as_label` | SKILL-01 | 1.3 | `[A]` | Error |
| 4 | `a11y_color_only_status` | SKILL-02 | 2.1 | `[A]` | Warning |
| 5 | `a11y_hardcoded_hex_color` | SKILL-02 | 2.2 | `[AA]` | Warning |
| 6 | `a11y_hardcoded_rgb_color` | SKILL-02 | 2.2 | `[AA]` | Warning |
| 7 | `a11y_fixed_font_size` | SKILL-03 | 3.3 | `[AA]` | Warning |
| 8 | `a11y_fixed_frame_height_in_list` | SKILL-03 | 3.3 | `[AA]` | Warning |
| 9 | `a11y_tap_gesture_no_accessibility` | SKILL-05 | 5.1 | `[A]` | Warning |
| 10 | `a11y_button_image_only_no_label` | SKILL-05 | 5.2 | `[A]` | Warning |
| 11 | `a11y_empty_accessibility_label` | SKILL-05 | 5.2 | `[A]` | Error |
| 12 | `a11y_accessibility_value_missing_context` | SKILL-05 | 5.3 | `[A]` | Warning |
| 13 | `a11y_textfield_placeholder_only` | SKILL-09 | 9.1 | `[A]` | Warning |
| 14 | `a11y_securefield_no_label` | SKILL-09 | 9.1 | `[A]` | Warning |
| 15 | `a11y_error_text_missing_announcement` | SKILL-09 | 9.4 | `[A]` | Warning |
| 16 | `a11y_modal_without_is_modal` | SKILL-10 | 10.6 | `[AA]` | Warning |
| 17 | `a11y_small_touch_target` | SKILL-11 | 11.5 | `[AA]` | Warning |

---

## Toutes les règles — Compose Lint (Android)

| # | Issue ID | Skill | Critère | Niveau | Sévérité |
|---|---|---|---|---|---|
| 1 | `IconMissingContentDescription` | SKILL-01 | 1.2 | `[A]` | Error |
| 2 | `EmptyContentDescription` | SKILL-01 | 1.2 | `[A]` | Error |
| 3 | `RedundantContentDescriptionPrefix` | SKILL-01 | 1.3 | `[A]` | Warning |
| 4 | `HardcodedAccessibilityColor` | SKILL-02 | 2.2 | `[AA]` | Warning |
| 5 | `ColorOnlyStatusCommunication` | SKILL-02 | 2.1 | `[A]` | Warning |
| 6 | `FixedTextSizeDp` | SKILL-03 | 3.3 | `[AA]` | Warning |
| 7 | `ClickableWithoutSemantics` | SKILL-05 | 5.1 | `[A]` | Error |
| 8 | `ClickableMissingRole` | SKILL-05 | 5.1 | `[A]` | Warning |
| 9 | `EmptySemanticsContentDescription` | SKILL-05 | 5.2 | `[A]` | Error |
| 10 | `TextFieldMissingLabel` | SKILL-09 | 9.1 | `[A]` | Error |
| 11 | `TextFieldEmptyLabel` | SKILL-09 | 9.2 | `[A]` | Error |
| 12 | `MissingScreenTitle` | SKILL-10 | 10.1 | `[A]` | Warning |
| 13 | `SmallTouchTarget` | SKILL-11 | 11.5 | `[AA]` | Warning |
