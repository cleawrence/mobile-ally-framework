---
hide:
  - navigation
  - toc
tags:
  - accueil
---

# FRAM — Framework Référence Accessibilité Mobile

<div class="grid cards" markdown>

-   :material-book-open-variant:{ .lg .middle } __12 Skills__

    ---

    Couvrant les 12 thématiques du référentiel **RAAM 1.1** :
    images, couleurs, formulaires, navigation, multimédia…

    [:octicons-arrow-right-24: Explorer les skills](skills/index.md)

-   :material-language-swift:{ .lg .middle } __SwiftUI + Compose__

    ---

    Patterns **prêts à copier-coller** avec exemples
    ✅ Bon / ❌ Mauvais pour chaque plateforme.

    [:octicons-arrow-right-24: Démarrage rapide](guides/installation.md)

-   :material-magnify:{ .lg .middle } __Linting statique__

    ---

    **17 règles SwiftLint** + **7 détecteurs Android Lint**
    détectent les violations au moment de l'écriture.

    [:octicons-arrow-right-24: Configurer le linting](linting/index.md)

-   :material-clipboard-check-outline:{ .lg .middle } __Grille d'audit__

    ---

    **70+ critères** avec scoring live [A] / [AA],
    export JSON et persistance localStorage.

    [:octicons-arrow-right-24: Lancer un audit](audit/index.md)

-   :material-robot-outline:{ .lg .middle } __Skill Claude Code__

    ---

    Les 12 thématiques condensées en **skill installable**
    (`npx skills add`) pour un agent codant en SwiftUI/Compose.

    [:octicons-arrow-right-24: Installer le skill](guides/installation.md#skill-claude-code-agents-ia)

</div>

---

## Niveaux de conformité

| Niveau | Usage | Description |
|---|---|---|
| :material-shield-check:{ .text-blue } **[A]** | **Audit simplifié** | Base obligatoire — ~45 critères. Chaque app doit passer ce niveau. |
| :material-shield-star:{ .text-green } **[AA]** | **Audit complet** | Conformité cible — ~25 critères supplémentaires. Objectif pour les apps publiques. |

!!! tip "Par où commencer ?"
    Commencez par le skill le plus pertinent pour votre écran actuel :

    | Vous développez... | Skill recommandé |
    |---|---|
    | Un écran avec des images/icônes | [SKILL-01 — Éléments Graphiques](skills/skill-01.md) |
    | Un formulaire de login/inscription | [SKILL-09 — Formulaires](skills/skill-09.md) |
    | Des boutons, toggles, actions | [SKILL-05 — Composants Interactifs](skills/skill-05.md) |
    | La navigation (tab bar, modales) | [SKILL-10 — Navigation](skills/skill-10.md) |
    | Un thème couleur / dark mode | [SKILL-02 — Couleurs et Contrastes](skills/skill-02.md) |

---

## En un coup d'œil

=== "SwiftUI"

    ```swift title="Exemple — Image accessible (SKILL-01)"
    // ✅ Image informative avec label contextuel
    Image(systemName: "exclamationmark.triangle")
        .accessibilityLabel("Avertissement : connexion instable")

    // ✅ Image décorative explicitement masquée
    Image(systemName: "sparkles")
        .accessibilityHidden(true)
    ```

=== "Jetpack Compose"

    ```kotlin title="Exemple — Icon accessible (SKILL-01)"
    // ✅ Icône informative avec description
    Icon(
        Icons.Default.Warning,
        contentDescription = "Avertissement : connexion instable"
    )

    // ✅ Icône décorative explicitement ignorée
    Icon(
        Icons.Default.Star,
        contentDescription = null // TalkBack ignore cet élément
    )
    ```

---

## Référentiel

FRAM implémente le **RAAM 1.1** (Référentiel d'Accessibilité des Applications Mobiles) publié par le Luxembourg, basé sur :

- [WCAG 2.1](https://www.w3.org/Translations/WCAG21-fr/) — Web Content Accessibility Guidelines
- [EN 301 549 v3.2.1](https://www.etsi.org/deliver/etsi_en/301500_301599/301549/03.02.01_60/en_301549v030201p.pdf) — Norme européenne d'accessibilité
- [Guidelines Orange Android](https://a11y-guidelines.orange.com/fr/mobile/android/) / [iOS](https://a11y-guidelines.orange.com/fr/mobile/ios/)

[:octicons-arrow-right-24: Mapping complet RAAM → FRAM](referentiel-raam.md)

---

<div style="text-align: center; color: var(--md-default-fg-color--lighter); font-size: 0.85rem; margin-top: 2rem;">
    <strong>FRAM v1.0</strong> — Juillet 2026 · Usage interne · Phase de test
</div>
