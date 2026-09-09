---
tags:
  - niveau-a
  - niveau-aa
  - skill-05
---

# SKILL-05 — Composants Interactifs

:material-cursor-pointer: **Gestion des boutons, interrupteurs et zones interactives**

## Critères couverts
| Critère RAAM | Intitulé | Niveau | WCAG |
|---|---|---|---|
| 5.1 | Chaque composant interactif a un nom accessible | `[A]` | 4.1.2 |
| 5.2 | Le nom accessible est pertinent | `[A]` | 4.1.2 |
| 5.3 | Chaque composant a un rôle | `[A]` | 4.1.2 |
| 5.4 | Les changements d'état sont restitués | `[A]` | 4.1.2 |
| 5.5 | La valeur courante est restituée | `[A]` | 4.1.2 |
| 5.6 | Les gestes complexes ont une alternative | `[AA]` | 2.5.1 |
| 5.7 | Taille de la zone de toucher minimale | `[AA]` | 2.5.5 |
| 5.8 | Le focus est visible | `[AA]` | 2.4.7 |
| 5.9 | L'ordre de focus est logique | `[A]` | 2.4.3 |
| 5.10 | Les composants identiques ont des noms cohérents | `[AA]` | 3.2.4 |
| 5.11 | Les composants sont opérables au clavier / switch | `[AA]` | 2.1.1 |
| 5.12 | Pas de piège au clavier / focus | `[A]` | 2.1.2 |

## Pourquoi c'est important
!!! warning "Règle d'or"
    Tout élément interactif doit avoir un **nom, un rôle et un état** correctement exposés aux technologies d'assistance. Un composant mal configuré rend l'application inutilisable pour les utilisateurs naviguant à l'ouïe ou utilisant des contacteurs (switch).

## Patterns

### 1. Boutons et IconButton `[A]`
=== "SwiftUI"
    ```swift title="✅ Bon"
    Button(action: { deleteItem() }) {
        Image(systemName: "trash")
    }
    .accessibilityLabel("Supprimer l'article Chaussures") // (1)!
    ```

    ```swift title="❌ Mauvais"
    Button(action: { deleteItem() }) {
        Image(systemName: "trash")
    }
    .accessibilityLabel("Supprimer") // (2)!
    ```

    1. Label descriptif en contexte. Le rôle "bouton" est fourni automatiquement.
    2. Label générique sans contexte. L'utilisateur ne sait pas quel article il va supprimer.

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    IconButton(
        onClick = onDelete,
        modifier = Modifier.semantics {
            contentDescription = "Supprimer l'article $itemName"
        }
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null
        )
    }
    ```

    ```kotlin title="❌ Mauvais"
    IconButton(onClick = onDelete) {
        Icon(Icons.Default.Delete, contentDescription = "Supprimer")
    }
    ```

### 2. Switch / Toggle `[A]`
=== "SwiftUI"
    ```swift title="✅ Bon"
    Toggle("Notifications push", isOn: $isEnabled) // (1)!
    ```

    ```swift title="❌ Mauvais"
    Button(action: { isEnabled.toggle() }) {
        Image(systemName: isEnabled ? "toggle.on" : "toggle.off")
    } // (2)!
    ```
    
    1. L'état ON/OFF et le rôle sont restitués automatiquement par Toggle.
    2. Simule un toggle avec un Button. Perd le rôle interrupteur.

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {} // (1)!
            .clickable { notificationsEnabled = !notificationsEnabled }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Notifications push", modifier = Modifier.weight(1f))
        Switch(checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it })
    }
    ```
    
    1. Fusionne visuellement et sémantiquement le texte et le switch.

### 3. Gestes complexes et alternatives `[AA]`
=== "SwiftUI"
    ```swift title="✅ Bon"
    Text(item.name)
        .swipeActions(edge: .trailing) {
            Button(role: .destructive) { delete(item) } label: {
                Label("Supprimer", systemImage: "trash")
            }
        }
        .accessibilityAction(named: "Supprimer \(item.name)") { // (1)!
            delete(item)
        }
    ```
    
    1. Alternative accessible au swipe via le menu d'actions de VoiceOver.

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    ListItem(
        headlineContent = { Text(item.name) },
        modifier = Modifier.semantics {
            customActions = listOf(
                CustomAccessibilityAction(
                    label = "Supprimer ${item.name}",
                    action = { onDelete(item); true }
                )
            )
        }
    )
    ```

### 4. Taille de cible `[AA]`
=== "SwiftUI"
    ```swift title="✅ Bon"
    Button(action: { dismiss() }) {
        Image(systemName: "xmark")
            .font(.system(size: 14))
            .frame(minWidth: 44, minHeight: 44) // (1)!
    }
    .contentShape(Rectangle())
    ```
    
    1. Zone de toucher de 44x44pt minimum requise sur iOS.

=== "Jetpack Compose"
    ```kotlin title="✅ Bon"
    IconButton(
        onClick = onClose,
        modifier = Modifier
            .minimumInteractiveComponentSize() // (1)!
            .semantics { contentDescription = "Fermer" }
    ) {
        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(20.dp))
    }
    ```
    
    1. Garantit une taille minimale interactive (généralement 48x48dp sur Android).

## Checklist [A]
- [ ] Chaque bouton a un label non vide (pas `""`, pas `null`)
- [ ] Les boutons icône seuls ont un label explicite
- [ ] Le label donne le contexte (ex: "Supprimer l'article Jean")
- [ ] Le label ne contient pas le rôle (ex: "Valider" et non "Bouton Valider")
- [ ] Les éléments cliquables custom ont un rôle déclaré
- [ ] Les Toggles/Switches/Checkboxes annoncent leur état
- [ ] Les Sliders annoncent la valeur courante
- [ ] L'ordre de navigation est logique (haut→bas, gauche→droite)
- [ ] Pas de piège au focus (modales, popovers)

## Checklist [AA]
- [ ] Les actions swipe, drag&drop, pinch ont une alternative
- [ ] Toutes les zones de toucher sont ≥ 44×44pt (iOS) / ≥ 48×48dp (Android)
- [ ] Le focus VoiceOver/TalkBack est visible et non masqué
- [ ] Les boutons effectuant la même action ont le même label partout
- [ ] Tous les composants sont activables via Switch Control / Switch Access

## Références
*   [RAAM 1.1 §5](https://accessibilite.public.lu/fr/raam1.1/referentiel-technique.html#topic-5)
*   [WCAG 4.1.2 Name, Role, Value](https://www.w3.org/Translations/WCAG21-fr/#name-role-value)
