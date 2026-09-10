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

if [ -n "$IOS_REPORT" ] && [ -f "$IOS_REPORT" ]; then
    echo "📱 Analyse du rapport iOS : $IOS_REPORT"
fi
if [ -n "$ANDROID_REPORT" ] && [ -f "$ANDROID_REPORT" ]; then
    echo "🤖 Analyse du rapport Android : $ANDROID_REPORT"
fi

# Tout le calcul (comptage par règle, scores, génération JSON + HTML) est fait en
# Python plutôt qu'en bash : plus fiable pour parser JSON/XML et construire un
# tableau HTML dynamique à partir des vraies données, sans dépendre des tableaux
# associatifs bash (absents de bash 3.2, celui livré par défaut sur macOS —
# pertinent puisque ce script tourne potentiellement sur des runners CI macOS).
IOS_REPORT="$IOS_REPORT" ANDROID_REPORT="$ANDROID_REPORT" OUTPUT_DIR="$OUTPUT_DIR" python3 - <<'PYEOF'
import json
import os
import re
from datetime import datetime

ios_report = os.environ.get("IOS_REPORT", "")
android_report = os.environ.get("ANDROID_REPORT", "")
output_dir = os.environ["OUTPUT_DIR"]

# Catalogue des règles : (id, plateforme, skill, critère, niveau, sévérité)
# Doit rester synchronisé avec docs/linting/catalogue-regles.md et les fichiers
# de config réels (linting/swiftlint/.swiftlint.yml, linting/compose-lint/).
RULES = [
    # ── SwiftLint (iOS) — 17 règles ──────────────────────────────────────────
    ("a11y_image_systemname_no_a11y", "ios", "SKILL-01", "1.1, 1.2", "A", "Error"),
    ("a11y_label_redundant_prefix", "ios", "SKILL-01", "1.3", "A", "Warning"),
    ("a11y_image_name_as_label", "ios", "SKILL-01", "1.3", "A", "Error"),
    ("a11y_color_only_status", "ios", "SKILL-02", "2.1", "A", "Warning"),
    ("a11y_hardcoded_hex_color", "ios", "SKILL-02", "2.2", "AA", "Warning"),
    ("a11y_hardcoded_rgb_color", "ios", "SKILL-02", "2.2", "AA", "Warning"),
    ("a11y_fixed_font_size", "ios", "SKILL-03", "3.3", "AA", "Warning"),
    ("a11y_fixed_frame_height_in_list", "ios", "SKILL-03", "3.3", "AA", "Warning"),
    ("a11y_tap_gesture_no_accessibility", "ios", "SKILL-05", "5.1", "A", "Warning"),
    ("a11y_button_image_only_no_label", "ios", "SKILL-05", "5.2", "A", "Warning"),
    ("a11y_empty_accessibility_label", "ios", "SKILL-05", "5.2", "A", "Error"),
    ("a11y_accessibility_value_missing_context", "ios", "SKILL-05", "5.3", "A", "Warning"),
    ("a11y_textfield_placeholder_only", "ios", "SKILL-09", "9.1", "A", "Warning"),
    ("a11y_securefield_no_label", "ios", "SKILL-09", "9.1", "A", "Warning"),
    ("a11y_error_text_missing_announcement", "ios", "SKILL-09", "9.4", "A", "Warning"),
    ("a11y_modal_without_is_modal", "ios", "SKILL-10", "10.6", "AA", "Warning"),
    ("a11y_small_touch_target", "ios", "SKILL-11", "11.5", "AA", "Warning"),
    # ── Compose Lint (Android) — 13 issues ───────────────────────────────────
    ("IconMissingContentDescription", "android", "SKILL-01", "1.2", "A", "Error"),
    ("EmptyContentDescription", "android", "SKILL-01", "1.2", "A", "Error"),
    ("RedundantContentDescriptionPrefix", "android", "SKILL-01", "1.3", "A", "Warning"),
    ("HardcodedAccessibilityColor", "android", "SKILL-02", "2.2", "AA", "Warning"),
    ("ColorOnlyStatusCommunication", "android", "SKILL-02", "2.1", "A", "Warning"),
    ("FixedTextSizeDp", "android", "SKILL-03", "3.3", "AA", "Warning"),
    ("ClickableWithoutSemantics", "android", "SKILL-05", "5.1", "A", "Error"),
    ("ClickableMissingRole", "android", "SKILL-05", "5.1", "A", "Warning"),
    ("EmptySemanticsContentDescription", "android", "SKILL-05", "5.2", "A", "Error"),
    ("TextFieldMissingLabel", "android", "SKILL-09", "9.1", "A", "Error"),
    ("TextFieldEmptyLabel", "android", "SKILL-09", "9.2", "A", "Error"),
    ("MissingScreenTitle", "android", "SKILL-10", "10.1", "A", "Warning"),
    ("SmallTouchTarget", "android", "SKILL-11", "11.5", "AA", "Warning"),
]

has_ios = bool(ios_report and os.path.isfile(ios_report))
has_android = bool(android_report and os.path.isfile(android_report))

# Ne compter/reporter que les règles des plateformes réellement fournies --
# sinon "regles_verifiees" prétend avoir vérifié des règles Android alors
# qu'aucun rapport Android n'a été fourni (et inversement).
active_rules = [r for r in RULES if (r[1] == "ios" and has_ios) or (r[1] == "android" and has_android)]

ios_violations = []
if has_ios:
    with open(ios_report, encoding="utf-8") as f:
        ios_violations = json.load(f)

android_xml = ""
if has_android:
    with open(android_report, encoding="utf-8") as f:
        android_xml = f.read()

counts = {}
for rule_id, platform, skill, critere, level, severity in active_rules:
    if platform == "ios":
        count = sum(1 for v in ios_violations if v.get("rule_id") == rule_id)
    else:
        count = len(re.findall(rf'id="{re.escape(rule_id)}"', android_xml))
    counts[rule_id] = count

rules_a = [r for r in active_rules if r[4] == "A"]
rules_aa = [r for r in active_rules if r[4] == "AA"]

count_violations_a = sum(counts[r[0]] for r in rules_a)
count_violations_aa_only = sum(counts[r[0]] for r in rules_aa)
count_violations_aa = count_violations_a + count_violations_aa_only

total_rules_a = len(rules_a)
total_rules_aa = len(rules_a) + len(rules_aa)

# Heuristique : 100 - (violations x 2), minimum 0, maximum 100.
# Ce calcul sera affiné quand on aura des données réelles de terrain.
score_a = max(0, min(100, 100 - count_violations_a * 2))
score_aa = max(0, min(100, 100 - count_violations_aa * 2))

now = datetime.now()
date_str = now.strftime("%Y-%m-%d")
time_str = now.strftime("%H:%M:%S")

print()
print("╔═══════════════════════════════════════════╗")
print("║   FRAM — Rapport de Conformité RAAM 1.1   ║")
print("╠═══════════════════════════════════════════╣")
print(f"║  Date : {date_str} {time_str}             ║")
print("║                                           ║")
print(f"║  Audit simplifié  [A]  : {score_a} / 100         ║")
print(f"║  Audit complet    [AA] : {score_aa} / 100         ║")
print("║                                           ║")
print(f"║  Violations [A]  : {count_violations_a}                      ║")
print(f"║  Violations [AA] : {count_violations_aa}                      ║")
print("╚═══════════════════════════════════════════╝")
print()

# ── Rapport JSON ──────────────────────────────────────────────────────────────

report = {
    "framework": "FRAM — Framework Référence Accessibilité Mobile",
    "referentiel": "RAAM 1.1",
    "date": date_str,
    "heure": time_str,
    "conformite": {
        "niveau_A": {
            "score": score_a,
            "violations": count_violations_a,
            "regles_verifiees": total_rules_a,
            "label": "Audit simplifié",
        },
        "niveau_AA": {
            "score": score_aa,
            "violations": count_violations_aa,
            "regles_verifiees": total_rules_aa,
            "label": "Audit complet",
        },
    },
    "sources": {
        "ios_report": ios_report if has_ios else "non fourni",
        "android_report": android_report if has_android else "non fourni",
    },
    "regles": [
        {
            "id": rule_id,
            "plateforme": platform,
            "skill": skill,
            "critere": critere,
            "niveau": level,
            "severite": severity,
            "violations": counts[rule_id],
        }
        for rule_id, platform, skill, critere, level, severity in active_rules
    ],
}

json_path = os.path.join(output_dir, "a11y-report.json")
with open(json_path, "w", encoding="utf-8") as f:
    json.dump(report, f, ensure_ascii=False, indent=2)
print(f"📄 Rapport JSON : {json_path}")

# ── Rapport HTML ──────────────────────────────────────────────────────────────

def score_class(score):
    if score >= 80:
        return "high"
    if score >= 50:
        return "medium"
    return "low"

def escape(s):
    return (
        str(s)
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
    )

rows = []
for rule_id, platform, skill, critere, level, severity in active_rules:
    n = counts[rule_id]
    level_tag = level.lower()
    severity_tag = severity.lower()
    status = f'{n} violation(s)' if n else "✅ 0"
    rows.append(
        f"                <tr><td>{escape(rule_id)}</td><td>{escape(skill)}</td>"
        f'<td><span class="tag {level_tag}">[{level}]</span></td>'
        f'<td><span class="tag {severity_tag}">{escape(severity)}</span></td>'
        f"<td>{status}</td></tr>"
    )
rules_table_rows = "\n".join(rows) if rows else (
    '                <tr><td colspan="5">Aucun rapport fourni — lancer avec --ios et/ou --android.</td></tr>'
)

html = f"""<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FRAM — Rapport Conformité Accessibilité</title>
    <style>
        :root {{
            --bg: #0f1419;
            --surface: #1a1f25;
            --border: #2d333b;
            --text: #e6edf3;
            --text-secondary: #8b949e;
            --green: #3fb950;
            --yellow: #d29922;
            --red: #f85149;
            --blue: #58a6ff;
        }}
        * {{ margin: 0; padding: 0; box-sizing: border-box; }}
        body {{
            font-family: -apple-system, BlinkMacSystemFont, 'Inter', 'Segoe UI', sans-serif;
            background: var(--bg);
            color: var(--text);
            padding: 40px 20px;
            line-height: 1.6;
        }}
        .container {{ max-width: 800px; margin: 0 auto; }}
        h1 {{
            font-size: 1.8rem;
            margin-bottom: 8px;
            background: linear-gradient(90deg, var(--blue), var(--green));
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }}
        .subtitle {{ color: var(--text-secondary); margin-bottom: 32px; font-size: 0.9rem; }}
        .scores {{
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-bottom: 32px;
        }}
        .score-card {{
            background: var(--surface);
            border: 1px solid var(--border);
            border-radius: 12px;
            padding: 24px;
            text-align: center;
        }}
        .score-card .label {{
            font-size: 0.85rem;
            color: var(--text-secondary);
            text-transform: uppercase;
            letter-spacing: 0.05em;
            margin-bottom: 8px;
        }}
        .score-card .level {{ font-weight: 700; font-size: 0.9rem; margin-bottom: 12px; }}
        .score-card .level.a {{ color: var(--blue); }}
        .score-card .level.aa {{ color: var(--green); }}
        .score-card .value {{
            font-size: 3.5rem;
            font-weight: 800;
            font-variant-numeric: tabular-nums;
        }}
        .score-card .value.high {{ color: var(--green); }}
        .score-card .value.medium {{ color: var(--yellow); }}
        .score-card .value.low {{ color: var(--red); }}
        .score-card .violations {{ font-size: 0.85rem; color: var(--text-secondary); margin-top: 8px; }}
        .bar {{ height: 6px; background: var(--border); border-radius: 3px; margin-top: 16px; overflow: hidden; }}
        .bar-fill {{ height: 100%; border-radius: 3px; transition: width 1s ease; }}
        .bar-fill.high {{ background: var(--green); }}
        .bar-fill.medium {{ background: var(--yellow); }}
        .bar-fill.low {{ background: var(--red); }}
        table {{
            width: 100%;
            border-collapse: collapse;
            margin-top: 16px;
            font-size: 0.85rem;
        }}
        th, td {{
            padding: 10px 14px;
            text-align: left;
            border-bottom: 1px solid var(--border);
        }}
        th {{ color: var(--text-secondary); font-weight: 600; }}
        .tag {{
            display: inline-block;
            padding: 2px 8px;
            border-radius: 4px;
            font-size: 0.75rem;
            font-weight: 600;
        }}
        .tag.a {{ background: rgba(88,166,255,0.15); color: var(--blue); }}
        .tag.aa {{ background: rgba(63,185,80,0.15); color: var(--green); }}
        .tag.error {{ background: rgba(248,81,73,0.15); color: var(--red); }}
        .tag.warning {{ background: rgba(210,153,34,0.15); color: var(--yellow); }}
        footer {{
            margin-top: 40px;
            padding-top: 20px;
            border-top: 1px solid var(--border);
            color: var(--text-secondary);
            font-size: 0.8rem;
        }}
    </style>
</head>
<body>
    <div class="container">
        <h1>FRAM — Rapport de Conformité</h1>
        <p class="subtitle">RAAM 1.1 · Généré le {date_str} à {time_str} · Framework Référence Accessibilité Mobile</p>

        <div class="scores">
            <div class="score-card">
                <div class="label">Audit Simplifié</div>
                <div class="level a">[A]</div>
                <div class="value {score_class(score_a)}">{score_a}</div>
                <div class="violations">{count_violations_a} violations détectées sur {total_rules_a} règles vérifiées</div>
                <div class="bar"><div class="bar-fill {score_class(score_a)}" style="width: {score_a}%"></div></div>
            </div>
            <div class="score-card">
                <div class="label">Audit Complet</div>
                <div class="level aa">[AA]</div>
                <div class="value {score_class(score_aa)}">{score_aa}</div>
                <div class="violations">{count_violations_aa} violations détectées sur {total_rules_aa} règles vérifiées</div>
                <div class="bar"><div class="bar-fill {score_class(score_aa)}" style="width: {score_aa}%"></div></div>
            </div>
        </div>

        <h2 style="font-size: 1.2rem; margin-bottom: 8px;">Règles vérifiées</h2>
        <table>
            <thead>
                <tr><th>Règle</th><th>Skill</th><th>Niveau</th><th>Sévérité</th><th>Résultat</th></tr>
            </thead>
            <tbody>
{rules_table_rows}
            </tbody>
        </table>

        <footer>
            FRAM v1.0 · Référentiel RAAM 1.1 · WCAG 2.1 · EN 301 549 v3.2.1
        </footer>
    </div>
</body>
</html>
"""

html_path = os.path.join(output_dir, "a11y-report.html")
with open(html_path, "w", encoding="utf-8") as f:
    f.write(html)
print(f"📊 Rapport HTML : {html_path}")
print()
print(f"✅ Rapports générés dans {output_dir}/")
PYEOF
