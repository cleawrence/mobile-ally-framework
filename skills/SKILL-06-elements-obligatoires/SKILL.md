# SKILL-06 — Éléments Obligatoires, v1.0

## Critères d'Accessibilité (RAAM 1.1)

| Critère | Niveau | Description | Correspondance WCAG |
|---------|--------|-------------|---------------------|
| 6.1     | [A]    | La langue principale de l'application est indiquée. | 3.1.1 Langue de la page |
| 6.2     | [A]    | Les changements de langue dans le contenu sont indiqués. | 3.1.2 Langue d'un passage |
| 6.3     | [A]    | Le titre de l'écran est pertinent. | 2.4.2 Titre de page |
| 6.4     | [A]    | Le titre de l'écran est unique. | 2.4.2 Titre de page |

## Pourquoi c'est important ?

Les lecteurs d'écran (VoiceOver, TalkBack) utilisent l'information de langue pour sélectionner le bon synthétiseur vocal et la bonne prononciation. Si la langue est incorrecte, le texte français peut être lu avec un accent anglais (ou vice-versa), le rendant totalement incompréhensible. De plus, les titres d'écran permettent aux utilisateurs de lecteurs d'écran et aux personnes souffrant de troubles cognitifs de s'orienter dans l'application et de confirmer qu'ils sont arrivés sur la bonne vue.

## Langue Principale

La langue principale de l'application doit être déclarée au niveau du projet. 
- **iOS** : Configurer la clé `CFBundleDevelopmentRegion` dans le fichier `Info.plist` (ex: `fr` pour français). L'application utilise la langue du système si elle est supportée par le bundle.
- **Android** : La locale par défaut est gérée par les ressources Android (dossier `values-fr`). Pour forcer une locale, utilisez `AppCompatDelegate.setApplicationLocales`.

## Changements de Langue Inline (Texte Multilingue)

Lorsqu'un mot, une phrase ou un bloc de texte est dans une langue différente de la langue principale, cette langue doit être indiquée programmatiquement pour que le lecteur d'écran change sa prononciation.
- **iOS (SwiftUI)** : `.accessibilityLanguage("en-US")` ou `.environment(\.locale, Locale(identifier: "en-US"))`
- **Android (Compose)** : L'utilisation de `SpanStyle(localeList = LocaleList(Locale("en-US")))` pour un texte, ou via des modificateurs de sémantique spécifiques.

## Titres d'Écrans

Le titre d'un écran doit décrire de manière concise et pertinente la fonction ou le contenu de l'écran. Il doit également être unique parmi les différents écrans de l'application pour éviter toute confusion.
- **iOS (SwiftUI)** : `.navigationTitle("Nom de l'écran")` pour les écrans dans une `NavigationStack`.
- **Android (Compose)** : Utiliser un composant comme `TopAppBar(title = { Text("Nom de l'écran") })` ou injecter sémantiquement le titre de la fenêtre (`windowTitle`).

## À faire / À éviter

✅ **À faire** :
- Déclarer la langue par défaut dans la configuration du projet (`Info.plist` pour iOS, ressources pour Android).
- Ajouter un attribut de langue spécifique pour les citations étrangères, les termes techniques en anglais, etc.
- Donner un titre unique à chaque écran, par exemple "Détails du produit - Chaussures", et non juste "Détails".

❌ **À éviter** :
- Omettre le titre de la barre de navigation.
- Laisser un mot étranger sans indication de langue (ex: "Download" sans `accessibilityLanguage("en")` lu par une voix française).
- Utiliser le nom de l'application comme titre pour tous les écrans.

## Vérification Manuelle

1. Activez VoiceOver (iOS) ou TalkBack (Android).
2. Vérifiez que la voix utilisée correspond bien à la langue du texte principal.
3. Naviguez vers un texte contenant des mots étrangers et écoutez si la prononciation change correctement.
4. Naviguez entre plusieurs écrans et vérifiez que chaque écran annonce un titre différent et pertinent.

## Checklist [A]
- [ ] [A] La langue par défaut de l'application est configurée dans le projet.
- [ ] [A] Tous les passages en langue étrangère (citations, termes techniques courants si non lexicalisés) utilisent un attribut de langue programmatiquement (`.accessibilityLanguage` ou `localeList`).
- [ ] [A] Chaque écran possède un titre visuel ou sémantique pertinent.
- [ ] [A] Les titres d'écran décrivent précisément le contenu de l'écran.
- [ ] [A] Les titres d'écran sont uniques dans toute l'application.
- [ ] [A] Lors d'un changement d'écran programmatique (sans transition standard), une notification d'accessibilité est envoyée.
- [ ] [A] Les termes techniques étrangers sont correctement prononcés.
- [ ] [A] La locale de l'application correspond aux textes affichés.
- [ ] [A] Les alertes et modales ont des titres clairs et pertinents.
- [ ] [A] Les titres d'écran ne sont pas redondants avec le nom de l'application.
