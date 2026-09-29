# Changelog

Toutes les évolutions notables de FRAM sont consignées ici.

Le format suit [Keep a Changelog](https://keepachangelog.com/fr/1.1.0/) et le projet adopte le [versionnage sémantique](https://semver.org/lang/fr/).

## [Non publié]

### Ajouté

- Références vers les projets CVS Health *iOS SwiftUI Accessibility Techniques* et *Android Compose Accessibility Techniques* (README et documentation).
- **Patterns prioritaires**, compilés par les harnesses de la CI (`swiftc -typecheck` iOS, `compileDebugKotlin` Android) :
  - iOS SKILL-01 §8 — cartes (`Map`) : résumé, repères nommés, alternative en liste, bouton réel pour l'ouverture plein écran ;
  - iOS SKILL-10 §8 — `@AccessibilityFocusState` : retour du focus VoiceOver après une sheet, focus sur le champ en erreur (`@FocusState` ne gère que le clavier) ;
  - Android SKILL-06 §5 — `paneTitle` pour les écrans à panneaux ;
  - Android SKILL-08 §5 — messages temporaires : Snackbar sans disparition automatique, `liveRegion`, et le contre-exemple `Toast`.
  - `SKILL.md` et pages du site correspondants, et skill condensé `mobile-a11y` mis à jour.
- Guide CI iOS : section optionnelle sur `a11y-check`, analyseur statique SwiftUI complémentaire à SwiftLint (avec `--no-trend`, et ses limites par rapport à FRAM).

## [1.0.0] — 2026-09-29

Première release publiée. Elle regroupe tout l'historique du dépôt (PR #1 à #28).

### Ajouté

- **12 skills** couvrant les 12 thématiques du RAAM 1.1, chacun avec sa documentation, ses patterns SwiftUI (`patterns.swift`) et Jetpack Compose (`patterns.kt`), et ses tests (XCUITest et Compose UI Test).
- **Linting statique** : 17 règles SwiftLint et 7 détecteurs Android Lint (module `compose-lint`).
- **Grille d'audit interactive** (`audit/grille-audit.html`) avec scoring [A] / [AA].
- **Site de documentation MkDocs**, publié sur GitHub Pages.
- **Skill Claude Code `mobile-a11y`**, installable avec `npx skills add`, qui condense les 12 thématiques pour un agent codant en SwiftUI / Compose (#16, #17) :
  - invocation explicite par thématique (`/mobile-a11y <thème>`, numéro ou nom FR/EN) et sous-commande `audit` (#20, #21) ;
  - section « anti-patterns » dédiée (#22, #23) ;
  - rapport d'audit en tableau et suivi par baseline `🆕 Nouveau` / `⚠️ Toujours ouvert` / `✅ Corrigé` (#24, #25) ;
  - lien vers le skill depuis le site MkDocs (#18, #19).
- **CI GitHub Actions** : validation de la doc (`mkdocs build --strict`, diagrammes Mermaid), compilation réelle des patterns et tests iOS (SwiftLint, `swiftc`, target XCUITest) et Android, tests des détecteurs Android Lint (#4, #5, #12, #13).
- Documentation du workflow gitflow, de la protection de branche et de la CI dans le README (#14, #15).
- Bloc « Utilisation » du skill dans le README : commandes, alias de thèmes, précision que les 12 thématiques s'utilisent une par une ou ensemble via un seul skill (#26, #27).

### Modifié

- **`/mobile-a11y audit` est en lecture seule par défaut** : l'écriture de la baseline `.a11y/*.json` dans le projet audité demande désormais confirmation. Options `--baseline` (écrire sans demander) et `--no-baseline` (ne jamais écrire) (#28).
- Migration des modules Compose vers le BOM Compose courant et de `compose-lint` vers `lint-api` / `lint-tests` 32.4.0 (#3, #10, #11).

### Corrigé

- Contenu de tous les skills Android et iOS vérifié par compilation : les exemples n'avaient jamais été compilés auparavant (#2, #3).
- Module `compose-lint` Android (#1).
- Références obsolètes au dépôt (#4, #5).
- Diagramme Mermaid cassé sur le site publié (nœud en losange avec crochet non quoté), avec ajout d'un contrôle en CI (#8, #9).
- Bugs de `a11y-report.sh` (#6, #7).

### Notes de mise à jour

- Les utilisateurs du skill déjà installé conservent l'ancien comportement de l'audit (écriture de la baseline sans confirmation) tant qu'ils n'ont pas lancé `npx skills update mobile-a11y`.
- Les `skills/SKILL-XX-*/SKILL.md` sont de la documentation de référence, pas des skills installables : `npx skills add` affiche des avertissements « missing required frontmatter » à leur sujet, sans conséquence.

[Non publié]: https://github.com/cleawrence/mobile-ally-framework/compare/v1.0.0...develop
[1.0.0]: https://github.com/cleawrence/mobile-ally-framework/releases/tag/v1.0.0
