# SKILL-07 — Structuration de l'Information, v1.0

## Objectif
Assurer une structuration logique et sémantique de l'information (titres, listes, citations) pour permettre aux utilisateurs de lecteurs d'écran de comprendre la hiérarchie et de naviguer rapidement dans les contenus.

## Critères RAAM 1.1 correspondants
| ID | Niveau | Description | WCAG |
|---|---|---|---|
| 7.1 | [A] | Les listes sont correctement structurées. | 1.3.1 |
| 7.2 | [A] | Les citations sont correctement identifiées. | 1.3.1 |
| 7.3 | [AA] | La hiérarchie des titres est cohérente. | 2.4.6, 1.3.1 |
| 7.4 | [AA] | Les titres sont pertinents. | 2.4.6 |

## Pourquoi c'est important ?
VoiceOver (iOS) et TalkBack (Android) offrent des modes de navigation rapides basés sur la structure du document :
*   **Le Rotor (iOS)** permet aux utilisateurs de naviguer directement d'un titre à l'autre (`En-têtes`). Si les titres ne sont pas marqués sémantiquement, cette fonctionnalité est inutilisable.
*   **Les Gestes locaux (TalkBack)** (Swipe haut puis bas, ou menu de navigation) permettent de passer de titre en titre.
*   Les **Listes** sémantiques informent l'utilisateur du nombre total d'éléments ("Élément 1 sur 5"), ce qui aide à appréhender la longueur du contenu.

## Principes de Structuration

### Hiérarchie de titres
Sur mobile, comme sur le web, la hiérarchie doit être respectée :
*   **H1 (Titre principal)** : Le titre de l'écran ou de la page.
*   **H2 (Section)** : Les grands blocs de contenu.
*   **H3 (Sous-section)** : Les subdivisions internes.
Les sauts de niveau (H1 vers H3 sans H2 intermédiaire) sont à proscrire.

### Listes (List vs VStack/Column)
*   Utilisez les composants natifs de listes (`List` en SwiftUI, `LazyColumn` en Compose) lorsque le contenu est une énumération logique d'éléments.
*   Évitez d'utiliser de simples empilements (`VStack`, `Column`) pour des listes longues ou complexes sans apporter de sémantique de liste.

## À faire / À éviter
✅ **À faire :**
*   Ajouter le trait `.isHeader` (iOS) ou la sémantique `heading()` (Android) sur tous les textes visuellement identifiés comme des titres.
*   S'assurer que l'ordre des titres est logique.
*   Utiliser les composants de liste natifs pour les collections d'items.

❌ **À éviter :**
*   Mettre un texte en gras ou augmenter sa taille pour faire un titre sans lui donner la sémantique correspondante.
*   Sauter des niveaux de titres.
*   Utiliser des éléments de liste purement visuels (puces dessinées à la main avec `Text("•")`) au lieu des listes sémantiques.

## Vérification manuelle
*   **VoiceOver (iOS)** : Activez VoiceOver. Utilisez le rotor (mouvement de rotation avec deux doigts) jusqu'à "En-têtes". Balayez vers le bas avec un doigt pour naviguer de titre en titre.
*   **TalkBack (Android)** : Activez TalkBack. Glissez vers le haut puis vers le bas en un seul mouvement continu (ou via les paramètres de navigation) pour naviguer par "En-têtes".

## Checklists

### Niveau [A]
- [ ] [A] Les listes ordonnées sont structurées pour annoncer la numérotation.
- [ ] [A] Les listes non ordonnées annoncent clairement chaque élément.
- [ ] [A] Le nombre d'éléments d'une liste est compréhensible (ou nativement annoncé).
- [ ] [A] Les empilements visuels tenant lieu de listes possèdent une sémantique de regroupement ou de liste.
- [ ] [A] Les citations sont distinctement lues comme telles (ou précédées de "Citation").
- [ ] [A] La fin d'une liste est perceptible.
- [ ] [A] Les éléments de listes interactifs regroupent toute leur surface cliquable (pas de bouton indépendant sauf si justifié).
- [ ] [A] Les changements de contexte (nouvelle section) sont structurés.
- [ ] [A] Les sections redondantes sont évitées.
- [ ] [A] Le rôle de "liste" n'est pas utilisé abusivement pour des éléments de layout.

### Niveau [AA]
- [ ] [AA] Chaque écran possède au moins un titre principal (H1) l'identifiant.
- [ ] [AA] Tous les éléments visuellement stylés comme des titres possèdent le trait/rôle d'en-tête.
- [ ] [AA] La hiérarchie des titres (niveaux) est cohérente, sans sauts illogiques.
- [ ] [AA] Les titres décrivent de manière claire et pertinente la section qui les suit.
- [ ] [AA] Les titres sont concis.
- [ ] [AA] Aucun élément interactif (bouton, lien) ne possède un trait d'en-tête de manière erronée.
- [ ] [AA] L'utilisateur peut parcourir tous les titres importants via la navigation par en-têtes (Rotor/TalkBack).
- [ ] [AA] Les titres cachés visuellement mais présents sémantiquement apportent une réelle plus-value (si utilisés).
