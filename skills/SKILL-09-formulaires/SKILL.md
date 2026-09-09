# SKILL-09 — Formulaires

**Statut** : v1.0 — Couverture SwiftUI + Jetpack Compose  
**Dernière révision** : Juillet 2026  
**Auteur** : FRAM — Framework Référence Accessibilité Mobile

---

## Critères couverts

| Critère RAAM 1.1 | Intitulé | Niveau | Correspondance |
|---|---|---|---|
| 9.1 | Chaque champ de formulaire a une étiquette | `[A]` | WCAG 1.3.1, 3.3.2, EN 301 549 §11.1.3.1.1 |
| 9.2 | Chaque étiquette de champ est pertinente | `[A]` | WCAG 2.4.6 |
| 9.3 | Les champs obligatoires sont identifiés | `[A]` | WCAG 3.3.2 |
| 9.4 | Les messages d'erreur sont pertinents | `[A]` | WCAG 3.3.1, 3.3.3 |
| 9.5 | Les aides à la saisie sont pertinentes | `[A]` | WCAG 3.3.2 |
| 9.6 | Saisie automatique respecte les finalités | `[AA]` | WCAG 1.3.5, EN 301 549 §11.1.3.5 |

---

## Pourquoi c'est important

Les formulaires constituent le point de blocage **le plus fréquent** pour les utilisateurs de technologies d'assistance. Un formulaire inaccessible empêche :
- Les **utilisateurs aveugles** (VoiceOver/TalkBack) de savoir ce qu'un champ attend
- Les **utilisateurs déficients cognitifs** de comprendre les erreurs sans message clair
- Les **utilisateurs à mobilité réduite** (Switch Control/Voice Control) d'entrer des données sans aide autocomplete
- Les **utilisateurs malvoyants** de voir un message d'erreur visible uniquement par la couleur

> **Règle d'or** : chaque champ doit avoir un **label visible et persistant**, les champs obligatoires doivent être **annoncés textuellement**, et chaque erreur doit être **liée au champ** qui la concerne.

---

## ✅ À faire / ❌ À éviter

| ✅ À faire | ❌ À éviter |
|---|---|
| Label visible et persistant au-dessus/côté du champ | Placeholder seul (disparaît à la saisie) |
| Mention "(obligatoire)" dans le label ou `.accessibilityLabel` | Astérisque `*` seul sans explication |
| Message d'erreur textuel lié au champ (`isError` / `accessibilityValue`) | Bordure rouge seule (info via couleur uniquement) |
| Erreur annoncée via live region ou `UIAccessibility.post` | Erreur silencieuse → utilisateur ne sait pas |
| `.textContentType(.emailAddress)` + `.keyboardType(.emailAddress)` | Type clavier par défaut pour un email |
| `.accessibilityHint` pour les formats attendus | Aucune aide pour un format complexe (IBAN, date) |
| Champs regroupés dans une `Section` avec en-tête | Champs dispersés sans contexte de groupe |

---

## Composants couverts

1. **TextField** — champ texte standard
2. **TextField email / téléphone / numérique** — clavier adapté
3. **SecureField / mot de passe** — avec toggle afficher/masquer
4. **Picker / Select / DropdownMenu** — liste de choix
5. **DatePicker** — sélecteur de date
6. **Regroupement de champs** — sections et groupes
7. **Messages d'erreur** — inline et annoncés
8. **Champs obligatoires** — indication sémantique
9. **Formulaire de connexion** — exemple complet
10. **Formulaire d'inscription** — exemple complet avec validation

Voir les patterns complets dans :
- **iOS SwiftUI** → [`ios-swiftui/patterns.swift`](./ios-swiftui/patterns.swift)
- **Android Compose** → [`android-compose/patterns.kt`](./android-compose/patterns.kt)

---

## Cas particulier — Le placeholder n'est pas un label

> C'est la **violation la plus fréquente** en formulaire.

```
❌ TextField("Adresse email", text: $email)   // placeholder disparaît à la saisie
                                              // VoiceOver perd l'info après premier caractère

✅ VStack {
       Text("Adresse email")                  // label persistant, toujours visible
       TextField("ex. prenom@domaine.fr", text: $email)
           .accessibilityLabel("Adresse email")
   }
```

Sur Android, `OutlinedTextField(label = { Text("Email") })` flotte au-dessus — c'est le **pattern recommandé** car le label reste visible même après saisie.

---

## Messages d'erreur — Règles de rédaction

Un message d'erreur accessible doit :
1. **Identifier le champ** : "Le champ Email est invalide" (pas juste "Erreur")
2. **Expliquer le problème** : "L'adresse email doit contenir un @"
3. **Proposer une correction si possible** : "Exemple : prenom@domaine.fr"

| ❌ Message non conforme | ✅ Message conforme |
|---|---|
| "Erreur" | "Le champ Email contient une erreur" |
| "Champ requis" | "Le prénom est obligatoire" |
| "Format invalide" | "Le téléphone doit contenir 10 chiffres. Exemple : 0612345678" |
| "Mot de passe trop court" | "Le mot de passe doit contenir au moins 8 caractères, dont une majuscule" |

---

## Vérification manuelle

### Protocole VoiceOver (iOS)

1. Activer VoiceOver : *Réglages > Accessibilité > VoiceOver*
2. Naviguer vers un formulaire
3. Pour chaque champ, vérifier que VoiceOver annonce :
   - Le **label** du champ (pas le placeholder)
   - Si le champ est **obligatoire** ("champ obligatoire")
   - La **valeur saisie** en cours de frappe
4. Soumettre le formulaire avec des erreurs, vérifier :
   - L'**annonce vocale** de l'erreur (notification ou live region)
   - Le **focus** revient au champ en erreur ou sur le message
5. Vérifier l'**aide à la saisie** : taper dans un champ complexe (IBAN, date) → VoiceOver doit lire le hint
6. Vérifier le type de clavier : un champ email doit ouvrir le clavier email (avec `@`)

### Protocole TalkBack (Android)

1. Activer TalkBack : *Paramètres > Accessibilité > TalkBack*
2. Naviguer vers un formulaire
3. Pour chaque `OutlinedTextField`, vérifier que TalkBack annonce :
   - Le **label flottant** (pas le hint/placeholder)
   - Si le champ est **requis** (via `semantics { isRequired = true }`)
4. Déclencher une erreur, vérifier :
   - Le **message `isError` + `supportingText`** est annoncé
   - La **live region** annonce l'erreur automatiquement
5. Vérifier les **types de clavier** (email, numérique, téléphone)
6. Vérifier que l'**autocomplete** propose des suggestions via clavier Android

### Outils complémentaires

```bash
# iOS — Accessibility Inspector
# Xcode > Open Developer Tool > Accessibility Inspector
# → Inspecter les propriétés de chaque champ (label, value, hint, traits)

# iOS 17+ — performAccessibilityAudit
try app.performAccessibilityAudit(for: [.sufficientElementDescription])

# Android — Layout Inspector
# Android Studio > View > Tool Windows > Layout Inspector
# → Vérifier les SemanticsNode : contentDescription, isRequired, error, liveRegion
```

---

## Checklist Niveau A — Audit Simplifié `[A]`

### 9.1 et 9.2 — Labels
- [ ] Chaque champ a un label **visible et persistant** (pas seulement un placeholder)
- [ ] Le label décrit précisément la donnée attendue (pas "Champ 1" ni "Saisie")
- [ ] Le label reste lisible après saisie (ne disparaît pas avec le placeholder)
- [ ] Les champs custom ont un `.accessibilityLabel` / `contentDescription` explicite
- [ ] Les Pickers et DatePickers ont un label décrivant ce qu'ils sélectionnent
- [ ] Les champs dans un groupe ont une étiquette de groupe (section/heading)

### 9.3 — Champs obligatoires
- [ ] Les champs obligatoires sont identifiés **textuellement** (pas uniquement par `*` ou couleur)
- [ ] VoiceOver/TalkBack annonce le caractère obligatoire du champ
- [ ] Si astérisque utilisé, une légende explicative est présente au-dessus du formulaire

### 9.4 — Messages d'erreur
- [ ] Les erreurs sont affichées en **texte visible** (pas seulement bordure rouge)
- [ ] Les erreurs sont **annoncées vocalement** (notification d'accessibilité ou live region)
- [ ] Chaque message identifie **quel champ** est en erreur
- [ ] Chaque message explique **pourquoi** c'est une erreur
- [ ] Le focus revient sur le champ en erreur ou sur le récapitulatif des erreurs

### 9.5 — Aides à la saisie
- [ ] Les formats attendus sont indiqués avant la saisie (ex : "JJ/MM/AAAA")
- [ ] Les contraintes sont lisibles par VoiceOver/TalkBack (`.accessibilityHint` / `supportingText`)
- [ ] Les placeholders servent d'**exemples** de format, pas de labels

---

## Checklist Niveau AA — Audit Complet `[AA]`

### 9.6 — Saisie automatique

- [ ] Les champs email utilisent `.textContentType(.emailAddress)` + `.keyboardType(.emailAddress)` (iOS) / `KeyboardType.Email` (Android)
- [ ] Les champs téléphone utilisent `.textContentType(.telephoneNumber)` + `.keyboardType(.phonePad)` / `KeyboardType.Phone`
- [ ] Les champs mot de passe utilisent `.textContentType(.password)` / `KeyboardType.Password`
- [ ] Les champs prénom/nom utilisent `.textContentType(.givenName)` / `.familyName`
- [ ] Les champs adresse utilisent `.textContentType(.streetAddressLine1)` / `streetAddressLine2`
- [ ] Les champs code postal utilisent `.keyboardType(.numberPad)` / `KeyboardType.NumberPassword`
- [ ] Les champs URL utilisent `.textContentType(.URL)` / `KeyboardType.Uri`
- [ ] Les champs date de naissance utilisent `.textContentType(.dateTime)` ou un DatePicker

---

## Évolution v2 — Cross-Platform

> 🔜 À activer après stabilisation v1

- Flutter : [`_evolution/flutter.dart.future`](./_evolution/flutter.dart.future)
- React Native : [`_evolution/react-native.tsx.future`](./_evolution/react-native.tsx.future)

---

*Références : [RAAM 1.1 §9](https://accessibilite.public.lu/fr/raam1.1/referentiel-technique.html#topic-9) · [WCAG 1.3.5](https://www.w3.org/Translations/WCAG21-fr/#identify-input-purpose) · [WCAG 3.3.1](https://www.w3.org/Translations/WCAG21-fr/#error-identification) · [WCAG 3.3.2](https://www.w3.org/Translations/WCAG21-fr/#labels-or-instructions) · [Orange Mobile Forms](https://a11y-guidelines.orange.com/fr/mobile/android/)*
