# FRAM — SwiftLint Accessibilité

## Règles Custom

Ce dossier contient la configuration SwiftLint avec **17 règles custom** détectant les violations d'accessibilité les plus fréquentes en SwiftUI.

### Installation

```bash
# 1. Installer SwiftLint
brew install swiftlint

# 2. Copier la config
cp .swiftlint.yml /chemin/vers/votre-projet-ios/

# 3. Ajouter à Xcode > Build Phases > Run Script :
if which swiftlint > /dev/null; then
  swiftlint --config .swiftlint.yml
fi
```

### Règles par Skill

| Skill | Règles | Sévérité |
|---|---|---|
| **SKILL-01** Éléments Graphiques | `a11y_image_systemname_no_a11y`, `a11y_label_redundant_prefix`, `a11y_image_name_as_label` | Error / Warning |
| **SKILL-02** Couleurs | `a11y_hardcoded_hex_color`, `a11y_hardcoded_rgb_color`, `a11y_color_only_status` | Warning |
| **SKILL-03** Adaptation | `a11y_fixed_font_size`, `a11y_fixed_frame_height_in_list` | Warning |
| **SKILL-05** Composants | `a11y_tap_gesture_no_accessibility`, `a11y_button_image_only_no_label`, `a11y_empty_accessibility_label`, `a11y_accessibility_value_missing_context` | Error / Warning |
| **SKILL-09** Formulaires | `a11y_textfield_placeholder_only`, `a11y_securefield_no_label`, `a11y_error_text_missing_announcement` | Warning / Error |
| **SKILL-10** Navigation | `a11y_modal_without_is_modal` | Warning |
| **SKILL-11** Consultation | `a11y_small_touch_target` | Warning |

### Sévérité

- **Error** : bloque la compilation (violation critique [A]) — **à corriger obligatoirement**
- **Warning** : signale une violation potentielle [A] ou [AA] — **à investiguer**

### Exemple de sortie

```
⚠️ [a11y_image_systemname_no_a11y]
   Image(systemName:) doit avoir soit .accessibilityHidden(true) (décoration)
   soit .accessibilityLabel('...') (information). Voir SKILL-01 [A] critère 1.1/1.2.

❌ [a11y_empty_accessibility_label]
   .accessibilityLabel("") est invalide — VoiceOver ignorera l'élément.
   Utiliser .accessibilityHidden(true) pour masquer, ou fournir un label descriptif.
   Voir SKILL-05 [A] critère 5.2.
```

### Personnaliser

Chaque règle peut être désactivée dans le `.swiftlint.yml` si elle génère un faux positif dans votre projet :

```yaml
# Désactiver une règle spécifique
disabled_rules:
  - a11y_fixed_frame_height_in_list

# Exclure un fichier
excluded:
  - Sources/DesignSystem/Theme.swift  # Les couleurs hardcodées y sont normales
```

### Limitations

Les règles regex SwiftLint ont des limites :
- Ne peuvent pas suivre le flux de données (une variable couleur définie ailleurs)
- Faux positifs possibles sur les chaînes multi-lignes complexes
- Ne remplacent PAS un audit VoiceOver/TalkBack manuel

Pour une couverture complète, combiner avec :
1. `performAccessibilityAudit()` (Xcode 15+ / iOS 17+)
2. Accessibility Inspector (Xcode)
3. Audit VoiceOver manuel
