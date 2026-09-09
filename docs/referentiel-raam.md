---
tags:
  - referentiel
  - raam
---

# RAAM 1.1 — Mapping complet

Le **Référentiel d'Accessibilité des Applications Mobiles** (RAAM 1.1) est publié par le [Service Information et Presse du Luxembourg](https://accessibilite.public.lu/fr/raam1.1/referentiel-technique.html). Il est basé sur la norme européenne EN 301 549 et les WCAG 2.1.

FRAM implémente l'intégralité des 12 thématiques du RAAM 1.1.

---

## Mapping RAAM → FRAM → WCAG

| Section RAAM | Skill FRAM | Critères [A] | Critères [AA] | WCAG principal |
|---|---|---|---|---|
| 1. Éléments graphiques | [SKILL-01](skills/skill-01.md) | 7 | 2 | 1.1.1, 1.4.5 |
| 2. Couleurs | [SKILL-02](skills/skill-02.md) | 1 | 8 | 1.4.1, 1.4.3, 1.4.11 |
| 3. Multimédia | [SKILL-03](skills/skill-03.md) | 1 | 5 | 1.3.4, 1.4.4, 1.4.10 |
| 4. Tableaux | [SKILL-04](skills/skill-04.md) | 3 | 1 | 1.3.1 |
| 5. Composants interactifs | [SKILL-05](skills/skill-05.md) | 5 | 1 | 4.1.2, 1.3.1 |
| 6. Éléments obligatoires | [SKILL-06](skills/skill-06.md) | 4 | 0 | 3.1.1, 3.1.2, 2.4.2 |
| 7. Structuration | [SKILL-07](skills/skill-07.md) | 2 | 2 | 1.3.1, 2.4.6 |
| 8. Éléments temporels | [SKILL-08](skills/skill-08.md) | 6 | 3 | 1.2.x, 2.2.2, 2.3.1 |
| 9. Formulaires | [SKILL-09](skills/skill-09.md) | 5 | 1 | 1.3.5, 3.3.x |
| 10. Navigation | [SKILL-10](skills/skill-10.md) | 3 | 3 | 2.4.3, 2.4.7, 2.1.2 |
| 11. Consultation | [SKILL-11](skills/skill-11.md) | 3 | 3 | 2.2.1, 2.5.5, 3.3.4 |
| 12. Documentation | [SKILL-12](skills/skill-12.md) | 2 | 2 | 4.1.2, 4.1.3 |
| **Total** | **12 skills** | **~42** | **~31** | |

---

## Correspondance EN 301 549

| Section EN 301 549 | Skill FRAM |
|---|---|
| §11.1.1.1.1 — Non-text content | SKILL-01 |
| §11.1.3.1.1 — Info and relationships | SKILL-04, SKILL-05, SKILL-07 |
| §11.1.4.1 — Use of color | SKILL-02 |
| §11.1.4.3 — Contrast (minimum) | SKILL-02 |
| §11.1.4.4.1 — Resize text | SKILL-03 |
| §11.1.4.5.1 — Images of text | SKILL-01 |
| §11.1.4.11 — Non-text contrast | SKILL-02 |
| §11.2.1.2 — No keyboard trap | SKILL-10 |
| §11.2.4.3 — Focus order | SKILL-10 |
| §11.2.4.7 — Focus visible | SKILL-10 |
| §11.2.5.5 — Target size | SKILL-11 |
| §11.3.1.1.1 — Language of page | SKILL-06 |
| §11.3.3.1.1 — Error identification | SKILL-09 |
| §11.4.1.2.1 — Name, role, value | SKILL-05, SKILL-12 |

---

## Références

- :material-link: [RAAM 1.1 — Référentiel technique](https://accessibilite.public.lu/fr/raam1.1/referentiel-technique.html)
- :material-link: [WCAG 2.1 (Français)](https://www.w3.org/Translations/WCAG21-fr/)
- :material-link: [EN 301 549 v3.2.1](https://www.etsi.org/deliver/etsi_en/301500_301599/301549/03.02.01_60/en_301549v030201p.pdf)
- :material-link: [Orange — Android](https://a11y-guidelines.orange.com/fr/mobile/android/)
- :material-link: [Orange — iOS](https://a11y-guidelines.orange.com/fr/mobile/ios/)
- :material-link: [Apple — Accessibility](https://developer.apple.com/documentation/swiftui/accessibility)
- :material-link: [Android — Compose Accessibility](https://developer.android.com/develop/ui/compose/accessibility)
