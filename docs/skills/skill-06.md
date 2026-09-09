---
tags:
  - niveau-a
  - skill-06
---

# SKILL-06 — Éléments Obligatoires

:material-format-title: **Langue principale, changement de langue et titres d'écrans**

## Critères couverts
| Critère RAAM | Intitulé | Niveau | WCAG |
|---|---|---|---|
| 6.1 | La langue principale de l'application est indiquée | `[A]` | 3.1.1 |
| 6.2 | Les changements de langue dans le contenu sont indiqués | `[A]` | 3.1.2 |
| 6.3 | Le titre de l'écran est pertinent | `[A]` | 2.4.2 |
| 6.4 | Le titre de l'écran est unique | `[A]` | 2.4.2 |

## Pourquoi c'est important
!!! warning "Attention à la prononciation"
    Les lecteurs d'écran (VoiceOver, TalkBack) utilisent l'information de langue pour sélectionner le bon synthétiseur vocal et la bonne prononciation. De plus, des titres d'écran pertinents et uniques permettent aux utilisateurs de s'orienter.

## Patterns

### 1. Langue principale `[A]`
=== "SwiftUI"
    ```swift title="✅ Bon"
    Text("Bonjour tout le monde")
        // Généralement géré via Info.plist (CFBundleDevelopmentRegion)
        // Mais on peut forcer une locale pour un environnement spécifique
        .environment(\.locale, Locale(identifier: "fr-FR"))
    ```
=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    // La locale principale est définie par le système Android
    // ou via AppCompatDelegate.setApplicationLocales
    // TalkBack utilise cette locale du système ou de l'application.
    ```

### 2. Changements de langue `[A]`
=== "SwiftUI"
    ```swift title="✅ Bon"
    // Terme technique anglais dans un texte français
    Text("Pour valider, cliquez sur le bouton ") + 
    Text("Submit").accessibilityLanguage("en-US") + // (1)!
    Text(" en bas de page.")
    ```

    ```swift title="❌ Mauvais"
    Text("Hello World, how are you?") // (2)!
    ```

    1. L'attribut `.accessibilityLanguage` indique le changement.
    2. Sans indication, l'anglais sera lu avec un accent français.

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    Text(
        text = "To be or not to be, that is the question.",
        style = LocalTextStyle.current.copy(
            localeList = LocaleList(Locale("en-US")) // (1)!
        )
    )
    ```

    ```kotlin title="✅ Bon (AnnotatedString)"
    Text(
        buildAnnotatedString {
            append("Le terme technique est ")
            withStyle(style = SpanStyle(localeList = LocaleList(Locale("en-US")))) {
                append("Smartphone")
            }
            append(".")
        }
    )
    ```
    
    1. Définit la locale spécifiquement pour ce texte.

### 3. Titres d'écrans `[A]`
=== "SwiftUI"
    ```swift title="✅ Bon"
    NavigationStack {
        List {
            NavigationLink("Voir les détails", destination: DetailView())
        }
        .navigationTitle("Liste des utilisateurs") // (1)!
    }
    ```
    
    1. Titre descriptif et unique.

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paramètres du compte") } // (1)!
            )
        }
    ) { paddingValues ->
        // ...
    }
    ```
    
    ```kotlin title="❌ Mauvais"
    TopAppBar(
        title = { Text("Écran") } // (2)!
    )
    ```
    
    1. Titre unique et pertinent, annoncé par TalkBack.
    2. Titre générique, ne décrit pas le contexte.

## Checklist [A]
- [ ] La langue par défaut de l'application est configurée dans le projet (`Info.plist` ou ressource).
- [ ] Tous les passages en langue étrangère utilisent un attribut de langue programmatiquement (`.accessibilityLanguage` ou `localeList`).
- [ ] Chaque écran possède un titre visuel ou sémantique pertinent.
- [ ] Les titres d'écran décrivent précisément le contenu de l'écran.
- [ ] Les titres d'écran sont uniques dans toute l'application.
- [ ] Lors d'un changement d'écran programmatique sans transition standard, une notification est envoyée.
- [ ] Les termes techniques étrangers sont correctement prononcés.
- [ ] La locale de l'application correspond aux textes affichés.
- [ ] Les alertes et modales ont des titres clairs et pertinents.
- [ ] Les titres d'écran ne sont pas redondants avec le nom de l'application.

## Références
*   [RAAM 1.1 §6](https://accessibilite.public.lu/fr/raam1.1/referentiel-technique.html#topic-6)
*   [WCAG 3.1.1 Langue de la page](https://www.w3.org/Translations/WCAG21-fr/#language-of-page)
