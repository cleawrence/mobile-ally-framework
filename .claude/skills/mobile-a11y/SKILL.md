---
name: mobile-a11y
description: |
  Mobile accessibility patterns for SwiftUI and Jetpack Compose, covering all 12 RAAM 1.1 / WCAG 2.1 / EN 301 549 themes: graphics & images, color & contrast, adaptation & Dynamic Type, tables & lists, interactive components, required elements (language & screen titles), content structure (headings/lists), time-based media, forms, navigation & focus order, touch targets & gestures, and accessibility documentation. Use whenever writing or reviewing SwiftUI or Jetpack Compose UI code, adding accessibilityLabel/contentDescription/semantics, working with VoiceOver/TalkBack, checking color contrast ratios, sizing touch targets, structuring headings or forms, or auditing a mobile screen for [A]/[AA] conformance. Trigger on phrases like "accessibility", "a11y", "accessibilityLabel", "contentDescription", "VoiceOver", "TalkBack", "WCAG", "RAAM", "contrast ratio", "Dynamic Type", "touch target", "focus order", "semantics", "screen reader", "accessible".
---

# Mobile Accessibility (SwiftUI + Jetpack Compose)

Distilled from FRAM (Framework Référence Accessibilité Mobile), a 12-theme reference mapping RAAM 1.1 to WCAG 2.1 / EN 301 549, with verified SwiftUI + Compose patterns. Full docs: https://cleawrence.github.io/mobile-ally-framework/ — this skill is the condensed, standalone version for use in any SwiftUI/Compose codebase.

**`[A]`** = baseline conformance, must never be violated. **`[AA]`** = enhanced conformance, required for most public/enterprise apps.

## Usage

This skill also triggers automatically by context (see description above) — explicit invocation below is for when you want a specific theme or a standalone audit report instead.

```
/mobile-a11y                        # apply the cheat sheet below to the code you're currently writing/reviewing
/mobile-a11y <theme>                # jump straight to one theme — name or number, French or English
/mobile-a11y audit <path>           # read the file(s) at <path> and report real violations against all 12 themes
/mobile-a11y audit                  # same, against the file(s) currently open/discussed in this conversation
```

Theme aliases (number, English, French — any of these match): `1` images/graphics/`images`/`graphiques` · `2` contrast/`couleurs`/`contrastes` · `3` adaptation/`dynamic-type`/`presentation` · `4` tables/`tableaux`/`listes` · `5` interactive/`composants`/`interactifs` · `6` required/`langue`/`titres` · `7` structure/`structuration`/headings · `8` media/`multimedia`/`temporels` · `9` forms/`formulaires` · `10` navigation/`focus` · `11` `consultation`/touch-targets/gestures · `12` documentation.

**No arguments:** read the whole cheat sheet below and keep applying it for the rest of the task — don't produce a standalone report.

**A theme argument:** jump to that section only, apply/review specifically against those rules, and reference the criterion levels (`[A]`/`[AA]`) in whatever you say or change.

**`audit <path>` (or `audit` with no path):** read the given file(s) — or, with no path, the file(s) most recently discussed/edited in this conversation — and check them against every theme below. Report only concrete, real findings: `file:line`, which theme + criterion, `[A]`/`[AA]`, and a one-line fix. If a theme doesn't apply to that file (e.g. no images in a pure logic file), skip it silently — don't pad the report with non-findings.

---

## Core principle

Accessibility is not a separate pass at the end. Every interactive element needs a name (label), a role, and — if stateful — a value that updates. Every image needs a decision: decorative (hide it) or informative (label it). Every layout needs to survive larger text and screen reader linear reading order.

---

## Anti-patterns — scan for these before shipping

- Icon-only button/control with no accessible label (§5, §11)
- An image label starting with "Image de", "Icône de", "Photo de" (§1)
- Error/success/selected state conveyed by color alone (§2)
- A hardcoded fixed text size that ignores Dynamic Type / font scale (§3)
- A placeholder used as a form field's only label (§9)
- A touch target below 44×44pt (iOS) / 48×48dp (Android) (§5, §11)
- A complex multi-finger gesture with no single-tap alternative (§11)
- A data table with no row/column header association (§4)
- Skipped heading levels, or a heading that's meaningless out of context (§7)
- A floating error message with no programmatic link to its field (§9)
- A screen with no title, or a title not announced on navigation (§6, §10)
- Visual layout order diverging from reading/focus order (§10)
- A swipe/long-press action with no accessible equivalent exposed (`accessibilityAction`/`customActions`) (§12)
- Pre-recorded video/audio with no captions or transcript (§8)
- A session timeout or auto-advancing carousel with no way to extend or pause it (§11)
- **Using an accessibility API by name-guessing instead of verifying it exists** — `XCUIElement.customActions`, `XCUIElementQuery.allElements`, `XCUIAccessibilityAuditType.textClipping` and `XCUIElement.contentSize` all *sound* plausible and don't exist; every fabricated-API bug this skill's examples were built to avoid was exactly this failure mode. Grep the SDK or check the real symbol before writing an accessibility call you're not certain of.

---

## 1. Graphics & images `[A]` `[AA]`

Every image is either decorative or informative — never leave it unlabeled by default (screen readers announce raw filenames like `IMG_4821.png`).

- Purely decorative image → hide it from the accessibility tree entirely.
- Informative image → give it a label describing its *function*, not its literal appearance ("Avatar de Marie Dupont", not "photo.jpg" or "image d'une personne").
- Never start a label with "Image de", "Icône de", "Photo de" — the trait already tells the screen reader it's an image.
- Complex graphics (charts, diagrams) need a concise summary label *plus* a way to reach the detailed data (a button, an expandable text).
- An image with a visible caption should be one accessible element, not two separately-announced ones.

```swift
// ✅ Decorative — invisible to VoiceOver
Image(systemName: "star.fill")
    .accessibilityHidden(true)

// ✅ Informative — describes function, not appearance
Image("warning-icon")
    .accessibilityLabel("Avertissement : connexion réseau instable")

// ✅ Image + caption merged into one element
HStack {
    Image("profile"); Text("Jean Dupont, Directeur Technique")
}
.accessibilityElement(children: .combine)
```

```kotlin
// ✅ Decorative
Icon(Icons.Default.Star, contentDescription = null)

// ✅ Informative — function, not filename
Image(painter = ..., contentDescription = "Avertissement : connexion réseau instable")

// ✅ Complex chart: summary label + escape hatch to detail
Image(
    painter = chartPainter,
    contentDescription = "Évolution des ventes en 2026, croissance de 20% au T1"
)
Button(onClick = { /* show data table */ }) { Text("Voir les données détaillées") }
```

---

## 2. Color & contrast `[A]` `[AA]`

- Never convey state (error/success/selected) through color alone — pair it with an icon, text, or shape change.
- Text contrast ratio ≥ **4.5:1** (normal text), ≥ **3:1** (large text ≥18pt/24dp or bold ≥14pt/18.66dp).
- UI component / graphical object contrast ratio ≥ **3:1** against adjacent colors (borders, icons, focus indicators).

```swift
// ❌ Color alone
Text("Erreur !").foregroundColor(.red)

// ✅ Icon + color + explicit text
HStack {
    Image(systemName: "exclamationmark.triangle.fill")
    Text("Erreur : email invalide")
}
.foregroundColor(.red)
```

```kotlin
// ❌ Color-only status
Text("Erreur !", color = Color.Red)

// ✅ Icon + color + text — never color alone
Row {
    Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
    Text("Erreur : email invalide", color = MaterialTheme.colorScheme.error)
}
```

---

## 3. Adaptation & presentation `[A]` `[AA]`

- Never convey meaning through shape/size/position alone ("tap the button on the right" — say what it *is*).
- Layout and functionality must survive a portrait↔landscape rotation with no content loss.
- Text must respect the system font-scale setting (Dynamic Type / `fontScale`) — **never hardcode a fixed text size that ignores it.**
- Content must reflow (not require horizontal scrolling) at 200% zoom / largest accessibility text sizes.

```swift
// ❌ Fixed size ignores Dynamic Type
Text("Titre").font(.system(size: 17))

// ✅ Scales with system text size setting
Text("Titre").font(.body)          // or .dynamicTypeSize(...) if you need bounds
```

```kotlin
// ❌ dp for text size — does not respond to system font scale
Text("Titre", fontSize = 14.dp.value.sp)

// ✅ sp scales with the user's font-scale setting
Text("Titre", fontSize = 14.sp)
// or, preferably, a Material typography token:
Text("Titre", style = MaterialTheme.typography.bodyMedium)
```

---

## 4. Tables & lists of data `[A]` `[AA]`

- Data tables need a real table role with row/column headers — a screen reader user must be able to link a cell to its headers.
- iOS has no robust native table a11y role on iPhone (`Table` is iPad/Mac-oriented) — combine each row into one accessible "card" element, or prefix each value with its header: `"Ville : Paris"`.
- Compose has native collection semantics (`collectionInfo`/`collectionItemInfo`) — use them for grids so TalkBack announces row/column position.
- Large or complex tables should offer a summary of what they contain.

```swift
// ✅ iOS: one row = one merged accessible element with header context baked in
HStack { Text(city); Text(population) }
    .accessibilityElement(children: .combine)
    .accessibilityLabel("Ville : \(city), Population : \(population)")
```

```kotlin
// ✅ Android: native collection semantics for a grid
Modifier.semantics {
    collectionInfo = CollectionInfo(rowCount = rows, columnCount = columns)
}
// on each cell:
Modifier.semantics {
    collectionItemInfo = CollectionItemInfo(rowIndex = rowIndex, rowSpan = 1, columnIndex = columnIndex, columnSpan = 1)
}
```

---

## 5. Interactive components `[A]` `[AA]`

- Every interactive element needs an accessible **name** — never leave a button/icon-only control unlabeled.
- The name must be *specific*, never generic ("button", "icon", "ok", "tap here").
- Every element needs a **role** (button, switch, tab...) so the screen reader announces how to interact with it.
- State changes (toggled, expanded, selected) must be reflected in the accessible value/traits immediately.
- Touch target ≥ **44×44pt (iOS)** / **48×48dp (Android)** — even if the visible icon is smaller, pad the tappable area.

```swift
// ❌ Icon-only button, no label
Button(action: {}) { Image(systemName: "xmark") }

// ✅ Explicit label + role is implicit via Button
Button(action: {}) { Image(systemName: "xmark") }
    .accessibilityLabel("Fermer")

// ✅ Custom clickable view needs an explicit role + state
.accessibilityAddTraits(isChecked ? [.isButton, .isSelected] : [.isButton])
```

```kotlin
// ❌ Modifier.clickable with no semantics — TalkBack announces "unlabeled, button"
Box(modifier = Modifier.clickable { onDelete() })

// ✅ Role + label on the clickable chain
Box(
    modifier = Modifier
        .clickable { onDelete() }
        .semantics {
            role = Role.Button
            contentDescription = "Supprimer l'article $itemName"
        }
)

// ✅ IconButton: contentDescription belongs on the Icon (Material convention)
IconButton(onClick = { onFavorite() }) {
    Icon(Icons.Default.Favorite, contentDescription = if (isLiked) "Retirer des favoris" else "Ajouter aux favoris")
}
```

---

## 6. Required elements — language & screen titles `[A]`

- The app's primary language must be declared correctly (screen readers pick voice/pronunciation from it — wrong language = unintelligible speech, not just an accent).
- Any in-content language switch (e.g. a quote in another language) must be marked.
- Every screen needs a relevant, unique title, announced on navigation so the user can confirm where they landed.

```swift
// ✅ Mark a foreign-language passage explicitly
Text("Hello world").environment(\.locale, Locale(identifier: "en"))

// ✅ Screen title announced on appear
.navigationTitle("Paramètres du compte")
```

```kotlin
// ✅ Screen title as a heading, announced by TalkBack on screen entry
Text(
    "Paramètres du compte",
    modifier = Modifier.semantics { heading() },
    style = MaterialTheme.typography.headlineMedium
)
```

---

## 7. Content structure — headings, lists, quotes `[A]` `[AA]`

- Use real list semantics for lists — screen readers announce item count and position ("3 sur 8") only with proper list structure, not visual bullets.
- Mark quotations as quotations.
- Heading hierarchy must be logical (no skipped levels) — VoiceOver/TalkBack both offer "jump by heading" navigation that depends on this.
- Headings must be meaningful out of context (a blind user scanning headings shouldn't need surrounding text to understand one).

```swift
// ✅ Mark a heading so VoiceOver's rotor can jump to it
Text("Section titre").accessibilityAddTraits(.isHeader)
```

```kotlin
// ✅ Heading semantics for TalkBack's heading navigation
Text("Section titre", modifier = Modifier.semantics { heading() })
```

---

## 8. Time-based media `[A]` `[AA]`

- Audio/video with no accompanying image needs a text transcript.
- Pre-recorded synchronized media needs captions (deaf/hard-of-hearing) `[A]`.
- Pre-recorded synchronized media should offer audio description of visual-only content `[AA]`.
- Don't rely on color/animation alone to signal "now playing" — pair with an accessible label/state.

```swift
// ✅ Expose a transcript alongside a video/audio player
VStack {
    VideoPlayer(player: player)
    Text(transcriptText).accessibilityLabel("Transcription : \(transcriptText)")
}
```

```kotlin
// ✅ liveRegion for state that updates automatically (e.g. "Chargement de la photo…")
Modifier.semantics { liveRegion = LiveRegionMode.Polite }
```

---

## 9. Forms `[A]` `[AA]`

- Every field needs a label, programmatically associated with it (not just visually adjacent placeholder text).
- Labels must be specific enough to disambiguate the field's purpose.
- Required fields must be identified accessibly, not just with a visual `*`.
- Errors must be announced and programmatically associated with the field they belong to (not just shown as floating red text elsewhere on screen).

```swift
// ❌ Placeholder as the only label — disappears once typing starts, not read reliably
TextField("Email", text: $email)

// ✅ Error folded into the label + hint, and announced when it appears
TextField("", text: $email)
    .accessibilityLabel("Adresse email" + (emailError != nil ? ", en erreur" : ""))
    .accessibilityHint(emailError ?? "Saisir votre adresse email")
// plus a visible error Text next to the field (never rely on the red border alone),
// and post an announcement when the error first appears:
// UIAccessibility.post(notification: .announcement, argument: "Erreur dans le champ Email : \(error)")
```

```kotlin
// ✅ Required + error merged into one announced element
OutlinedTextField(
    value = email,
    onValueChange = { email = it },
    label = { Text("Email *") },
    isError = hasError,
    supportingText = { if (hasError) Text("Format d'email invalide") },
    modifier = Modifier.semantics(mergeDescendants = true) {}
)
```

---

## 10. Navigation & focus order `[A]`

- The user must always know their position in the app (screen title announced on navigation — see §6).
- Focus/reading order must follow logical visual order — never let the DOM/tree order diverge from what's on screen (a common bug: absolutely-positioned or overlay elements reading out of sequence).
- Custom keyboard shortcuts must not conflict with the screen reader's own shortcuts (VoiceOver/TalkBack reserve many single-key gestures).

```swift
// ✅ Group things that should be read together in visual order, ungroup things that shouldn't
VStack { header; content; footer }
    .accessibilityElement(children: .contain) // preserves reading order, doesn't merge into one
```

```kotlin
// ✅ Compose's semantics tree follows composition order by default — keep layout order
// matching visual order; avoid Modifier.zIndex tricks that visually reorder without
// reordering the composable tree, which is what TalkBack actually walks.
```

---

## 11. Consultation — touch targets, time limits, gestures `[A]` `[AA]`

- Touch target minimum: **44×44pt (iOS, Apple HIG)** / **48×48dp (Android, Material)** — the *tappable* area, not necessarily the *visible* icon (pad with transparent hit area if the icon must stay small).
- Any complex gesture (swipe, pinch, multi-finger) needs a single-tap/simple alternative — Switch Control and many motor-impaired users can't perform multi-touch gestures.
- Time limits (session timeout, auto-advancing carousels) must be adjustable, extendable, or disableable.
- Actions that are destructive or hard to reverse need a confirmation step or an undo, since accidental activation is more likely for screen-reader/switch-control users.

```swift
// ❌ Icon-only tap target with no padding — likely under 44pt
Button(action: {}) { Image(systemName: "xmark").font(.caption) }

// ✅ Explicit minimum frame regardless of icon size
Button(action: {}) { Image(systemName: "xmark") }
    .frame(minWidth: 44, minHeight: 44)
```

```kotlin
// ❌ IconButton shrunk below the safe minimum
IconButton(modifier = Modifier.size(24.dp), onClick = {}) { Icon(Icons.Default.Close, null) }

// ✅ Material's default IconButton already reserves 48dp — don't override it down
IconButton(onClick = {}) { Icon(Icons.Default.Close, contentDescription = "Fermer") }
// or, for a custom clickable, guarantee the minimum explicitly:
Modifier.minimumInteractiveComponentSize()
```

---

## 12. Accessibility documentation `[A]` `[AA]`

- Public-sector and large-enterprise apps need a reachable, in-app accessibility statement: conformance status + a way to report an accessibility problem.
- Document the app's own accessibility features (VoiceOver/TalkBack support, dynamic type support, any custom actions) — users need to discover what's supported, not guess.
- Support platform accessibility features properly: VoiceOver/TalkBack, Switch Control, Voice Control need real traits/roles/actions exposed — a control that *works visually* with a mouse-equivalent tap but exposes nothing semantically fails Switch Control and Voice Control even if VoiceOver alone seems fine.
- Custom accessibility actions (swipe actions, long-press menus) should be exposed as `accessibilityActions`/`customActions` so Switch Control and VoiceOver's rotor can reach them without the gesture.

```swift
// ✅ Expose a swipe action as a proper accessibility action too
.accessibilityAction(named: "Supprimer") { deleteItem() }
```

```kotlin
// ✅ Expose a swipe action as a semantics custom action
Modifier.semantics {
    customActions = listOf(
        CustomAccessibilityAction(label = "Supprimer") { onDelete(); true }
    )
}
```

---

## When you need more than this

This skill is a condensed cheat sheet. For exhaustive ✅/❌ pattern files (SwiftUI + Compose, one file per theme), full criteria tables, and working SwiftLint/Android-Lint rules that catch these mistakes automatically, see the FRAM repo: https://github.com/cleawrence/mobile-ally-framework — `skills/SKILL-01..12-*/` for patterns, `linting/` for the lint rules. If you're working inside a checkout of that repo, prefer reading the actual `skills/SKILL-XX-*/SKILL.md` and `patterns.swift`/`patterns.kt` files directly — they're the living source of truth this skill was distilled from, and may have grown since.
