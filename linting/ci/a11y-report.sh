#!/usr/bin/env bash
# ──────────────────────────────────────────────────────────────────────────────
# FRAM — Script de Rapport de Conformité Accessibilité
# Génère un rapport JSON + HTML à partir des résultats SwiftLint et Android Lint
# ──────────────────────────────────────────────────────────────────────────────
#
# Usage :
#   ./a11y-report.sh [--ios rapport-swiftlint.json] [--android rapport-lint.xml] [-o output/]
#
# Le rapport sépare les scores par niveau de conformité :
#   - Score [A]  : Audit simplifié  — critères de niveau A uniquement
#   - Score [AA] : Audit complet    — critères de niveau A + AA
#
# Sortie :
#   output/a11y-report.json   — Rapport machine-readable
#   output/a11y-report.html   — Rapport visuel imprimable

set -euo pipefail

# ── Configuration ────────────────────────────────────────────────────────────

# Règles de niveau [A] (critiques — audit simplifié)
LEVEL_A_RULES=(
    # iOS (SwiftLint)
    "a11y_image_systemname_no_a11y"          # SKILL-01 1.2
    "a11y_label_redundant_prefix"            # SKILL-01 1.3
    "a11y_image_name_as_label"               # SKILL-01 1.3
    "a11y_tap_gesture_no_accessibility"       # SKILL-05 5.1
    "a11y_button_image_only_no_label"        # SKILL-05 5.2
    "a11y_empty_accessibility_label"         # SKILL-05 5.2
    "a11y_textfield_placeholder_only"        # SKILL-09 9.1
    "a11y_securefield_no_label"              # SKILL-09 9.1
    "a11y_error_text_missing_announcement"   # SKILL-09 9.4
    "a11y_color_only_status"                 # SKILL-02 2.1
    "a11y_modal_without_is_modal"            # SKILL-10 10.6
    # Android (Compose Lint)
    "IconMissingContentDescription"          # SKILL-01 1.2
    "EmptyContentDescription"               # SKILL-01 1.2
    "RedundantContentDescriptionPrefix"      # SKILL-01 1.3
    "ClickableWithoutSemantics"              # SKILL-05 5.1
    "ClickableMissingRole"                   # SKILL-05 5.1
    "EmptySemanticsContentDescription"       # SKILL-05 5.2
    "TextFieldMissingLabel"                  # SKILL-09 9.1
    "TextFieldEmptyLabel"                    # SKILL-09 9.2
    "MissingScreenTitle"                     # SKILL-10 10.1
)

# Règles de niveau [AA] (avancées — audit complet)
LEVEL_AA_RULES=(
    # iOS (SwiftLint)
    "a11y_hardcoded_hex_color"               # SKILL-02 2.2
    "a11y_hardcoded_rgb_color"               # SKILL-02 2.2
    "a11y_fixed_font_size"                   # SKILL-03 3.3
    "a11y_fixed_frame_height_in_list"        # SKILL-03 3.3
    "a11y_small_touch_target"                # SKILL-11 11.5
    "a11y_accessibility_value_missing_context" # SKILL-05 5.3
    # Android (Compose Lint)
    "HardcodedAccessibilityColor"            # SKILL-02 2.2
    "ColorOnlyStatusCommunication"           # SKILL-02 2.1
    "FixedTextSizeDp"                        # SKILL-03 3.3
    "SmallTouchTarget"                       # SKILL-11 11.5
)

# ── Arguments ────────────────────────────────────────────────────────────────

IOS_REPORT=""
ANDROID_REPORT=""
OUTPUT_DIR="./output"

while [[ $# -gt 0 ]]; do
    case "$1" in
        --ios)     IOS_REPORT="$2"; shift 2 ;;
        --android) ANDROID_REPORT="$2"; shift 2 ;;
        -o)        OUTPUT_DIR="$2"; shift 2 ;;
        -h|--help)
            echo "Usage: $0 [--ios rapport.json] [--android rapport.xml] [-o output/]"
            exit 0 ;;
        *) echo "Option inconnue: $1"; exit 1 ;;
    esac
done

mkdir -p "$OUTPUT_DIR"

# ── Comptage des violations ──────────────────────────────────────────────────

count_violations_a=0
count_violations_aa=0
total_rules_a=${#LEVEL_A_RULES[@]}
total_rules_aa=${#LEVEL_AA_RULES[@]}

# Compter les violations dans le rapport iOS (JSON SwiftLint)
if [ -n "$IOS_REPORT" ] && [ -f "$IOS_REPORT" ]; then
    echo "📱 Analyse du rapport iOS : $IOS_REPORT"
    for rule in "${LEVEL_A_RULES[@]}"; do
        count=$(python3 -c "
import json, sys
data = json.load(open('$IOS_REPORT'))
print(len([v for v in data if v.get('rule_id') == '$rule']))
" 2>/dev/null || echo "0")
        count_violations_a=$((count_violations_a + count))
    done

    for rule in "${LEVEL_AA_RULES[@]}"; do
        count=$(python3 -c "
import json, sys
data = json.load(open('$IOS_REPORT'))
print(len([v for v in data if v.get('rule_id') == '$rule']))
" 2>/dev/null || echo "0")
        count_violations_aa=$((count_violations_aa + count))
    done
fi

# Compter les violations dans le rapport Android (XML Lint)
if [ -n "$ANDROID_REPORT" ] && [ -f "$ANDROID_REPORT" ]; then
    echo "🤖 Analyse du rapport Android : $ANDROID_REPORT"
    for rule in "${LEVEL_A_RULES[@]}"; do
        count=$(grep -c "id=\"$rule\"" "$ANDROID_REPORT" 2>/dev/null || echo "0")
        count_violations_a=$((count_violations_a + count))
    done

    for rule in "${LEVEL_AA_RULES[@]}"; do
        count=$(grep -c "id=\"$rule\"" "$ANDROID_REPORT" 2>/dev/null || echo "0")
        count_violations_aa=$((count_violations_aa + count))
    done
fi

# ── Calcul des scores ────────────────────────────────────────────────────────

# Heuristique : 100 - (violations × 2), minimum 0, maximum 100
# Ce calcul sera affiné quand on aura des données réelles
score_a=$(python3 -c "print(max(0, min(100, 100 - $count_violations_a * 2)))")
score_aa=$(python3 -c "print(max(0, min(100, 100 - ($count_violations_a + $count_violations_aa) * 2)))")

DATE=$(date +"%Y-%m-%d")
TIME=$(date +"%H:%M:%S")

echo ""
echo "╔═══════════════════════════════════════════╗"
echo "║   FRAM — Rapport de Conformité RAAM 1.1   ║"
echo "╠═══════════════════════════════════════════╣"
echo "║  Date : $DATE $TIME             ║"
echo "║                                           ║"
echo "║  Audit simplifié  [A]  : $score_a / 100         ║"
echo "║  Audit complet    [AA] : $score_aa / 100         ║"
echo "║                                           ║"
echo "║  Violations [A]  : $count_violations_a                      ║"
echo "║  Violations [AA] : $count_violations_aa                      ║"
echo "╚═══════════════════════════════════════════╝"
echo ""

# ── Génération du rapport JSON ───────────────────────────────────────────────

cat > "$OUTPUT_DIR/a11y-report.json" <<EOF
{
  "framework": "FRAM — Framework Référence Accessibilité Mobile",
  "referentiel": "RAAM 1.1",
  "date": "$DATE",
  "heure": "$TIME",
  "conformite": {
    "niveau_A": {
      "score": $score_a,
      "violations": $count_violations_a,
      "regles_verifiees": $total_rules_a,
      "label": "Audit simplifié"
    },
    "niveau_AA": {
      "score": $score_aa,
      "violations": $count_violations_aa,
      "regles_verifiees": $total_rules_aa,
      "label": "Audit complet"
    }
  },
  "sources": {
    "ios_report": "$([ -n "$IOS_REPORT" ] && echo "$IOS_REPORT" || echo "non fourni")",
    "android_report": "$([ -n "$ANDROID_REPORT" ] && echo "$ANDROID_REPORT" || echo "non fourni")"
  }
}
EOF

echo "📄 Rapport JSON : $OUTPUT_DIR/a11y-report.json"

# ── Génération du rapport HTML ───────────────────────────────────────────────

cat > "$OUTPUT_DIR/a11y-report.html" <<'HEREDOC'
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FRAM — Rapport Conformité Accessibilité</title>
    <style>
        :root {
            --bg: #0f1419;
            --surface: #1a1f25;
            --border: #2d333b;
            --text: #e6edf3;
            --text-secondary: #8b949e;
            --green: #3fb950;
            --yellow: #d29922;
            --red: #f85149;
            --blue: #58a6ff;
        }
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Inter', 'Segoe UI', sans-serif;
            background: var(--bg);
            color: var(--text);
            padding: 40px 20px;
            line-height: 1.6;
        }
        .container { max-width: 800px; margin: 0 auto; }
        h1 {
            font-size: 1.8rem;
            margin-bottom: 8px;
            background: linear-gradient(90deg, var(--blue), var(--green));
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }
        .subtitle { color: var(--text-secondary); margin-bottom: 32px; font-size: 0.9rem; }
        .scores {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-bottom: 32px;
        }
        .score-card {
            background: var(--surface);
            border: 1px solid var(--border);
            border-radius: 12px;
            padding: 24px;
            text-align: center;
        }
        .score-card .label {
            font-size: 0.85rem;
            color: var(--text-secondary);
            text-transform: uppercase;
            letter-spacing: 0.05em;
            margin-bottom: 8px;
        }
        .score-card .level { font-weight: 700; font-size: 0.9rem; margin-bottom: 12px; }
        .score-card .level.a { color: var(--blue); }
        .score-card .level.aa { color: var(--green); }
        .score-card .value {
            font-size: 3.5rem;
            font-weight: 800;
            font-variant-numeric: tabular-nums;
        }
        .score-card .value.high { color: var(--green); }
        .score-card .value.medium { color: var(--yellow); }
        .score-card .value.low { color: var(--red); }
        .score-card .violations { font-size: 0.85rem; color: var(--text-secondary); margin-top: 8px; }
        .bar { height: 6px; background: var(--border); border-radius: 3px; margin-top: 16px; overflow: hidden; }
        .bar-fill { height: 100%; border-radius: 3px; transition: width 1s ease; }
        .bar-fill.high { background: var(--green); }
        .bar-fill.medium { background: var(--yellow); }
        .bar-fill.low { background: var(--red); }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 16px;
            font-size: 0.85rem;
        }
        th, td {
            padding: 10px 14px;
            text-align: left;
            border-bottom: 1px solid var(--border);
        }
        th { color: var(--text-secondary); font-weight: 600; }
        .tag {
            display: inline-block;
            padding: 2px 8px;
            border-radius: 4px;
            font-size: 0.75rem;
            font-weight: 600;
        }
        .tag.a { background: rgba(88,166,255,0.15); color: var(--blue); }
        .tag.aa { background: rgba(63,185,80,0.15); color: var(--green); }
        .tag.error { background: rgba(248,81,73,0.15); color: var(--red); }
        .tag.warning { background: rgba(210,153,34,0.15); color: var(--yellow); }
        footer {
            margin-top: 40px;
            padding-top: 20px;
            border-top: 1px solid var(--border);
            color: var(--text-secondary);
            font-size: 0.8rem;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>FRAM — Rapport de Conformité</h1>
HEREDOC

# Injecter les valeurs dynamiques dans le HTML
cat >> "$OUTPUT_DIR/a11y-report.html" <<EOF
        <p class="subtitle">RAAM 1.1 · Généré le $DATE à $TIME · Framework Référence Accessibilité Mobile</p>

        <div class="scores">
            <div class="score-card">
                <div class="label">Audit Simplifié</div>
                <div class="level a">[A]</div>
                <div class="value $([ "$score_a" -ge 80 ] && echo "high" || ([ "$score_a" -ge 50 ] && echo "medium" || echo "low"))">$score_a</div>
                <div class="violations">$count_violations_a violations détectées</div>
                <div class="bar"><div class="bar-fill $([ "$score_a" -ge 80 ] && echo "high" || ([ "$score_a" -ge 50 ] && echo "medium" || echo "low"))" style="width: ${score_a}%"></div></div>
            </div>
            <div class="score-card">
                <div class="label">Audit Complet</div>
                <div class="level aa">[AA]</div>
                <div class="value $([ "$score_aa" -ge 80 ] && echo "high" || ([ "$score_aa" -ge 50 ] && echo "medium" || echo "low"))">$score_aa</div>
                <div class="violations">$((count_violations_a + count_violations_aa)) violations détectées</div>
                <div class="bar"><div class="bar-fill $([ "$score_aa" -ge 80 ] && echo "high" || ([ "$score_aa" -ge 50 ] && echo "medium" || echo "low"))" style="width: ${score_aa}%"></div></div>
            </div>
        </div>

        <h2 style="font-size: 1.2rem; margin-bottom: 8px;">Règles vérifiées</h2>
        <table>
            <thead>
                <tr><th>Règle</th><th>Skill</th><th>Niveau</th><th>Sévérité</th></tr>
            </thead>
            <tbody>
                <tr><td>IconMissingContentDescription</td><td>SKILL-01</td><td><span class="tag a">[A]</span></td><td><span class="tag error">Error</span></td></tr>
                <tr><td>ClickableWithoutSemantics</td><td>SKILL-05</td><td><span class="tag a">[A]</span></td><td><span class="tag error">Error</span></td></tr>
                <tr><td>TextFieldMissingLabel</td><td>SKILL-09</td><td><span class="tag a">[A]</span></td><td><span class="tag error">Error</span></td></tr>
                <tr><td>MissingScreenTitle</td><td>SKILL-10</td><td><span class="tag a">[A]</span></td><td><span class="tag warning">Warning</span></td></tr>
                <tr><td>HardcodedAccessibilityColor</td><td>SKILL-02</td><td><span class="tag aa">[AA]</span></td><td><span class="tag warning">Warning</span></td></tr>
                <tr><td>FixedTextSizeDp</td><td>SKILL-03</td><td><span class="tag aa">[AA]</span></td><td><span class="tag warning">Warning</span></td></tr>
                <tr><td>SmallTouchTarget</td><td>SKILL-11</td><td><span class="tag aa">[AA]</span></td><td><span class="tag warning">Warning</span></td></tr>
            </tbody>
        </table>

        <footer>
            FRAM v1.0 · Référentiel RAAM 1.1 · WCAG 2.1 · EN 301 549 v3.2.1
        </footer>
    </div>
</body>
</html>
EOF

echo "📊 Rapport HTML : $OUTPUT_DIR/a11y-report.html"
echo ""
echo "✅ Rapports générés dans $OUTPUT_DIR/"
