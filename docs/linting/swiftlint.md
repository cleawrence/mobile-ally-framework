---
tags:
  - linting
  - swiftlint
  - ios
---

# SwiftLint — iOS

## Configuration

Le fichier `.swiftlint.yml` contient **17 règles custom** ciblant les violations d'accessibilité SwiftUI les plus fréquentes.

### Installation

```bash
brew install swiftlint
cp mobile-ally-framework/linting/swiftlint/.swiftlint.yml ./MonProjetIOS/
```

### Intégration Xcode

**Build Phases → + → New Run Script Phase :**

```bash
if which swiftlint > /dev/null; then
  swiftlint --config .swiftlint.yml
fi
```

---

## Règles par skill

### SKILL-01 — Éléments Graphiques

| ID Règle | Sévérité | Description |
|---|---|---|
| `a11y_image_systemname_no_a11y` | :material-alert-circle:{ .text-red } Error | `Image(systemName:)` sans `.accessibilityHidden` ni `.accessibilityLabel` |
| `a11y_label_redundant_prefix` | :material-alert:{ .text-yellow } Warning | Label commençant par "image de", "icône de" |
| `a11y_image_name_as_label` | :material-alert-circle:{ .text-red } Error | Nom de fichier utilisé comme label (ex: "hero_banner.png") |

### SKILL-02 — Couleurs et Contrastes

| ID Règle | Sévérité | Description |
|---|---|---|
| `a11y_hardcoded_hex_color` | :material-alert:{ .text-yellow } Warning | `Color(hex: "#...")` hardcodé |
| `a11y_hardcoded_rgb_color` | :material-alert:{ .text-yellow } Warning | `Color(red:green:blue:)` hardcodé |
| `a11y_color_only_status` | :material-alert:{ .text-yellow } Warning | `.foregroundStyle(.red/.green)` sans icône/texte |

### SKILL-03 — Adaptation

| ID Règle | Sévérité | Description |
|---|---|---|
| `a11y_fixed_font_size` | :material-alert:{ .text-yellow } Warning | `.font(.system(size: N))` taille fixe |
| `a11y_fixed_frame_height_in_list` | :material-alert:{ .text-yellow } Warning | `.frame(height: N)` fixe dans une liste |

### SKILL-05 — Composants Interactifs

| ID Règle | Sévérité | Description |
|---|---|---|
| `a11y_tap_gesture_no_accessibility` | :material-alert:{ .text-yellow } Warning | `.onTapGesture` sans sémantique |
| `a11y_button_image_only_no_label` | :material-alert:{ .text-yellow } Warning | `Button { Image(...) }` sans label |
| `a11y_empty_accessibility_label` | :material-alert-circle:{ .text-red } Error | `.accessibilityLabel("")` vide |
| `a11y_accessibility_value_missing_context` | :material-alert:{ .text-yellow } Warning | `.accessibilityValue` sans `.accessibilityLabel` |

### SKILL-09 — Formulaires

| ID Règle | Sévérité | Description |
|---|---|---|
| `a11y_textfield_placeholder_only` | :material-alert:{ .text-yellow } Warning | `TextField` avec placeholder uniquement |
| `a11y_securefield_no_label` | :material-alert:{ .text-yellow } Warning | `SecureField` sans label |
| `a11y_error_text_missing_announcement` | :material-alert:{ .text-yellow } Warning | Erreur sans annonce VoiceOver |

### SKILL-10 — Navigation

| ID Règle | Sévérité | Description |
|---|---|---|
| `a11y_modal_without_is_modal` | :material-alert:{ .text-yellow } Warning | Modale sans `.accessibilityAddTraits(.isModal)` |

### SKILL-11 — Consultation

| ID Règle | Sévérité | Description |
|---|---|---|
| `a11y_small_touch_target` | :material-alert:{ .text-yellow } Warning | Zone de touche < 44pt |

---

## Personnaliser

### Désactiver une règle globalement

```yaml title=".swiftlint.yml"
disabled_rules:
  - a11y_fixed_frame_height_in_list
```

### Désactiver dans un fichier

```swift
// swiftlint:disable a11y_hardcoded_hex_color
let brandColor = Color(hex: "#FF6B35") // Couleur de marque
// swiftlint:enable a11y_hardcoded_hex_color
```

### Exclure des dossiers

```yaml title=".swiftlint.yml"
excluded:
  - Pods
  - Sources/DesignSystem/Theme.swift  # Définitions de couleurs
```
