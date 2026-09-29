# Extensions WCAG 2.2

FRAM implémente le **RAAM 1.1**, dont la base officielle est **WCAG 2.1** et l'EN 301 549 v3.2.1. **WCAG 2.2** (octobre 2023) ajoute 9 critères et supprime le 4.1.1 (analyse syntaxique, obsolète). Cette page présente les **4 nouveautés les plus utiles au mobile**, à titre d'**extension**.

!!! info "Hors RAAM 1.1, hors scoring [A] / [AA]"
    Ces critères ne font pas partie du RAAM 1.1. Ils sont marqués **`[2.2]`** et **ne sont pas comptés** dans le score de conformité RAAM de la grille d'audit. Un audit RAAM 1.1 reste valide sans eux : ils servent à aller plus loin.

| Critère WCAG 2.2 | Niveau | Ce qu'il demande | Skill |
|---|---|---|---|
| **3.3.8** Authentification accessible (minimum) | AA | Pas de test cognitif à la connexion sans alternative | [SKILL-09](skills/skill-09.md) |
| **3.3.7** Saisie redondante | A | Ne pas redemander une information déjà saisie dans le parcours | [SKILL-09](skills/skill-09.md) |
| **2.4.11** Focus non masqué (minimum) | AA | Le focus n'est pas entièrement caché par une barre fixe ou le clavier | [SKILL-10](skills/skill-10.md) |
| **2.5.7** Mouvements de glissement | AA | Toute action par glissement a une alternative à un seul appui | [SKILL-11](skills/skill-11.md) |

!!! note "Non traités ici"
    3.2.6 (aide cohérente), 2.5.8 (taille de cible minimum) et les critères AAA (2.4.12, 2.4.13, 3.3.9). Pour la taille de cible, FRAM demande déjà 44 pt (iOS) / 48 dp (Android), plus strict que les 24 px de WCAG 2.5.8.

---

## 3.3.8 — Authentification accessible `[2.2 · AA]`

Se connecter ne doit pas exiger de mémoriser ou de retranscrire un secret **sans alternative** : le gestionnaire de mots de passe, le collage et la biométrie doivent fonctionner.

=== "SwiftUI"
    ```swift title="✅ Bon"
    TextField("ex. prenom@domaine.fr", text: $username)
        .accessibilityLabel("Identifiant")
        .textContentType(.username)

    SecureField("", text: $password)
        .accessibilityLabel("Mot de passe")
        .textContentType(.password) // .newPassword à l'inscription

    Button("Se connecter avec Face ID") { authenticateWithBiometrics() } // LAContext
    ```

    ```swift title="❌ Mauvais — collage bloqué"
    final class NoPasteTextField: UITextField {
        override func canPerformAction(_ action: Selector, withSender sender: Any?) -> Bool {
            if action == #selector(UIResponderStandardEditActions.paste(_:)) { return false }
            return super.canPerformAction(action, withSender: sender)
        }
    }
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    OutlinedTextField(
        value = username, onValueChange = { username = it },
        label = { Text("Identifiant") },
        modifier = Modifier.semantics { contentType = ContentType.Username }
    )
    OutlinedTextField(
        value = password, onValueChange = { password = it },
        label = { Text("Mot de passe") },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.semantics { contentType = ContentType.Password }
    )
    // Alternative sans mémorisation : BiometricPrompt ou passkeys (Credential Manager)
    ```

## 3.3.7 — Saisie redondante `[2.2 · A]`

Dans un même parcours (commande, inscription), ne pas redemander une information déjà fournie : la préremplir ou proposer de la réutiliser.

=== "SwiftUI"
    ```swift title="✅ Bon"
    Toggle("Identique à l'adresse de facturation", isOn: $sameAsBilling)
    if !sameAsBilling {
        TextField("", text: $shippingAddress)
            .accessibilityLabel("Adresse de livraison")
            .textContentType(.fullStreetAddress)
    }
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    Row(Modifier.toggleable(value = sameAsBilling, role = Role.Checkbox,
                            onValueChange = { sameAsBilling = it })) {
        Checkbox(checked = sameAsBilling, onCheckedChange = null)
        Text("Livraison identique à la facturation")
    }
    if (!sameAsBilling) { OutlinedTextField(/* adresse de livraison */) }
    ```

## 2.4.11 — Focus non masqué `[2.2 · AA]`

L'élément qui a le focus (VoiceOver, clavier matériel, Switch Control/Access) ne doit pas être **entièrement** caché par une barre fixe ou par le clavier à l'écran.

=== "SwiftUI"
    ```swift title="✅ Bon — la barre réserve sa place"
    ScrollView { /* champs */ }
        .safeAreaInset(edge: .bottom) {
            Button("Enregistrer le formulaire") { }
                .frame(maxWidth: .infinity, minHeight: 44)
                .background(.bar)
        }
    ```

    ```swift title="❌ Mauvais — barre en overlay, le dernier champ reste dessous"
    ZStack(alignment: .bottom) { ScrollView { /* champs */ }; Button("Envoyer") { } }
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    Scaffold(bottomBar = { BottomAppBar { /* bouton */ } }) { paddingValues ->
        Column(Modifier.padding(paddingValues).verticalScroll(rememberScrollState()).imePadding()) {
            /* champs */
        }
    }
    ```

    ```kotlin title="❌ Mauvais — Box superposée, ni padding ni gestion du clavier"
    Box { Column(Modifier.verticalScroll(rememberScrollState())) { /* champs */ }
          Button(onClick = { }, modifier = Modifier.align(Alignment.BottomCenter)) { Text("Enregistrer") } }
    ```

## 2.5.7 — Mouvements de glissement `[2.2 · AA]`

Toute action faite en glissant (réordonner, déplacer) doit pouvoir se faire d'un simple appui : Switch Control, Voice Control et de nombreux utilisateurs à mobilité réduite ne peuvent pas maintenir un glissement.

=== "SwiftUI"
    ```swift title="✅ Bon"
    Text(meal)
        .accessibilityAction(named: "Monter") { move(meal, by: -1) }
        .accessibilityAction(named: "Descendre") { move(meal, by: 1) }
        .contextMenu { Button("Monter") { move(meal, by: -1) }
                       Button("Descendre") { move(meal, by: 1) } }
    // .onMove reste disponible, mais n'est plus le seul moyen
    ```

=== "Jetpack Compose"
    ```kotlin title="✅ Bon — boutons visibles + actions TalkBack"
    Row(Modifier.semantics {
        customActions = listOf(
            CustomAccessibilityAction("Monter") { move(index, -1) },
            CustomAccessibilityAction("Descendre") { move(index, 1) }
        )
    }) {
        Text(meal, Modifier.weight(1f))
        TextButton(onClick = { move(index, -1) }) { Text("Monter") }
        TextButton(onClick = { move(index, 1) }) { Text("Descendre") }
    }
    ```

---

## Où trouver le code compilé

Les patterns complets (avec leurs ❌) sont dans les fichiers `patterns.swift` / `patterns.kt` de [SKILL-09](skills/skill-09.md), [SKILL-10](skills/skill-10.md) et [SKILL-11](skills/skill-11.md), sections marquées `[2.2 · A/AA]`. Ils sont compilés par la CI comme les autres exemples.

!!! warning "Limites"
    Les exemples sont validés à la compilation, pas au lecteur d'écran : le comportement réel de VoiceOver / TalkBack, de Switch Control / Switch Access et du gestionnaire de mots de passe reste à vérifier sur appareil.

## Références

- :material-link: [Nouveautés de WCAG 2.2 (W3C)](https://www.w3.org/WAI/standards-guidelines/wcag/new-in-22/)
- :material-link: [RAAM 1.1 — Référentiel technique](https://accessibilite.public.lu/fr/raam1.1/referentiel-technique.html)
