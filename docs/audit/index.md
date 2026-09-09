---
tags:
  - audit
---

# Audit

L'audit d'accessibilité FRAM combine trois approches complémentaires :

<div class="grid cards" markdown>

-   :material-robot:{ .lg .middle } __Linting automatique__

    ---

    Détecte ~40% des violations [A] **au moment de l'écriture**. Zéro effort humain.

    [:octicons-arrow-right-24: Configurer le linting](../linting/index.md)

-   :material-clipboard-check:{ .lg .middle } __Grille d'audit manuelle__

    ---

    **70+ critères** avec scoring live. Couvre ce que le linting ne peut pas détecter.

    [:octicons-arrow-right-24: Grille interactive](grille-interactive.md)

-   :material-file-chart:{ .lg .middle } __Rapport de conformité__

    ---

    Score combiné [A] / [AA] en **JSON + HTML**. Généré par script.

    [:octicons-arrow-right-24: Générer un rapport](rapport-conformite.md)

</div>

---

## Workflow d'audit recommandé

```mermaid
graph TD
    A["1. Linting CI<br/>Automatique"] --> B["2. Tests UI<br/>XCUITest / Compose"]
    B --> C["3. Audit VoiceOver/TalkBack<br/>Manuel avec grille"]
    C --> D["4. Rapport de conformité<br/>Score [A] / [AA]"]
    D --> E{Score [A] ≥ 90% ?}
    E -->|Oui| F["✅ Release autorisée"]
    E -->|Non| G["🔧 Corriger les violations"]
    G --> A

    style A fill:#1a1f25,stroke:#58a6ff
    style B fill:#1a1f25,stroke:#3fb950
    style C fill:#1a1f25,stroke:#d29922
    style D fill:#1a1f25,stroke:#bc8cff
    style F fill:#1a1f25,stroke:#3fb950
    style G fill:#1a1f25,stroke:#f85149
```
