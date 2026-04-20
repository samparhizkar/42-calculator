# Phase 4 — Theme Editor Rework

> **Design: VOID v2 (locked April 2026).** Android implementation. Full spec in **`DESIGN_SPEC.md`**. Values for this screen: mini preview card 16dp radius shows VOID display + keypad grid; 6 seed presets = teal `#1abc9c`, blue `#2563eb`, green `#16a34a`, purple `#7c3aed`, orange `#ea580c`, pink `#db2777`; paid key-colour swatches = `#222222 #86efac #bef264 #fde047 #6ee7b7 #1abc9c` (36dp). See `DESIGN_SPEC.md §Theme editor`.

Scope: replace the current two-color ("accent" + "keypad") picker with a **seed-based Material 3 theming** flow. Resolves the open Phase 1 questions about seed color, dynamic color, and how paid numpad recoloring fits into the M3 role system.

Depends on: Phase 1 (M3 role tokens + `Theme.FortyTwo`) — this phase configures what those tokens resolve to at runtime.

Out of scope:
- Classic retro theme (explicitly dropped per earlier discussion).
- Currency feature (separate phase).
- IAP / billing plumbing changes — we keep whatever billing code exists working, only changing what's gated.

---

## Current state (findings)

**Entry point** — "palette" icon on the main calculator screen opens [ColorPickerActivity.java](app/src/main/java/com/sepidsa/fortytwocalculator/ColorPickerActivity.java) (244 lines).

**Layout** — [activity_color_picker.xml](app/src/main/res/layout/activity_color_picker.xml)
- Split-screen: top half = "display" (accent / header) swatch grid; bottom half = "keypad" swatch grid.
- A row at the bottom with a `Switch` to enable the **classic** (retro physical calculator) theme.
- Labels are **hard-coded Persian strings** in the layout XML (`صفحه نمایش`, `صفحه کلید`, `تم کلاسیک`, `موارد ستاره دار فقط در نسخه طلایی`) — not referenced via `@string/...`, so they don't localize for English / French / Arabic users. This is a bug, not a design choice.
- Paid swatches are marked with a `*` glyph in gold (`#FFC107`) + a legend line "starred items are gold-version-only."

**Custom views** — `ColorPickerPalette`, `ColorPickerSwatch`, `ColorStateDrawable` (~350 LOC total). Hand-rolled swatch grid and selection indicator (white checkmark on active swatch).

**How the WYSIWYG preview actually works** — the *entire top half of the screen* (the `RelativeLayout` containing the display palette) has its background color set to whichever swatch is currently selected. So the preview IS the color picker header itself changing color in realtime. There is no separate mini-preview widget — the header region is the preview. This is clever but means you can't see the full calculator context (keypad, buttons) while picking.

**Swatch counts** — 18 swatches per section (3 rows × 6 columns). Display palette: teal, dark teal, slate, cyan, blue, orange, yellow, brown, black, gray, navy, pink, hot pink, lavender, purple, magenta, crimson. Keypad palette: different set — black, greens, yellow-greens, yellow, mint, pink, light blue, teal, peach, orange, light yellow, purple, hot pink, light purple, violet, periwinkle, sky blue.

**Persistence** — the chosen colors are stored in SharedPreferences and re-read at activity creation by `MainActivity` to tint the header `RelativeLayout` and the button text/backgrounds. No theme-attribute indirection; colors are applied directly to views.

**Problems with this model vs. M3:**
1. **No dark mode concept.** User picks one background color and one keypad color, period. Can't adapt to system dark mode.
2. **No contrast guarantees.** User can pick black text on black background; nothing stops them. The keypad palette includes black as the first option, which would make digit labels invisible against a dark keypad background.
3. **Two colors ≠ a theme.** Real M3 themes have ~48 role tokens (primary, on-primary, surface, surface-variant, error, etc.). Recoloring only two of them leaves outlines, disabled states, dividers, and ripple tints untouched — they end up looking out of place against a custom background.
4. **Limited WYSIWYG.** The preview only shows the header background changing. The user can't see how the keypad will look with their selected keypad color until they exit.
5. **Fights Phase 1.** Phase 1 introduces role tokens and optional dynamic color. If the theme editor keeps overwriting raw `background` attributes on specific views, Phase 1's role-token work gets bypassed and dark mode breaks selectively.

---

## Target design

**One seed color → entire palette is derived.** Same mental model as Material Theme Builder / Android 12 "Wallpaper colors." User picks what they care about (hue/vibe); the system handles contrast, dark mode, and role consistency.

### Editor structure

```
┌─────────────────────────────────────┐
│  ← Appearance                       │
├─────────────────────────────────────┤
│                                     │
│  Live preview                       │
│  ┌───────────────────────────────┐  │
│  │ 566 + 369 = 935               │  │  ← mini calculator preview
│  │ [7][8][9][÷]                  │  │     that recolors in realtime
│  │ [4][5][6][×]                  │  │
│  └───────────────────────────────┘  │
│                                     │
│  Theme color                        │
│  ● ● ● ● ● ● + Custom               │  ← 6 preset seeds + color-picker
│                                     │
│  Appearance                         │
│  ( ) Light  ( ) Dark  (•) System    │  ← segmented buttons
│                                     │
│  [✓] Dynamic color (Android 12+)   │  ← checkbox, disabled on <API 31
│      Match your wallpaper           │
│                                     │
│  ─── Advanced (★ paid) ───          │
│                                     │
│  Key color                          │  ← paid: override colorSecondaryContainer
│  ● ● ● ● ● + Custom                 │     (affects only operator/digit keys)
│                                     │
│  [Reset to defaults]                │
└─────────────────────────────────────┘
```

### Three layers of customization

| Layer | Free? | What it controls |
|-------|-------|------------------|
| Theme color (seed) | ✅ Free | Full palette derivation — primary, secondary, surface tones. Replaces today's "accent" picker. |
| Appearance (light/dark/system) | ✅ Free | Forces dark mode on, off, or follows system. |
| Dynamic color (wallpaper-based) | ✅ Free | Android 12+ only. Overrides the seed with wallpaper colors. |
| Key color | ⭐ Paid | Overrides `colorSecondaryContainer` for the operator/digit keys only. Replaces today's "keypad" picker. |

**Why keep the paid "key color" feature:** the user explicitly mentioned this was paid and presumably revenue-relevant. The new design keeps it — just re-expresses it as overriding one M3 role token, so it stays consistent with the rest of the theme and doesn't break dark mode.

### Preset seed colors

Six curated presets covering the main hue regions: teal (current brand, keeps existing users' identity intact), blue, green, purple, orange, pink. Plus a "Custom" chip that opens an M3 `ColorPickerDialog`.

### Live preview

A compact, non-interactive calculator card at the top that recolors in realtime as the user changes seed / appearance / key color. Lets them see the result without committing. The current editor doesn't have this — changes are only visible after exiting back to the main calculator.

### Removed

- **Classic retro theme** toggle — fully dropped per prior decision.
- **Hardcoded Persian labels** in the layout — all replaced with `@string/` references that already exist (or new ones added to `strings.xml` + translations).

---

## Implementation plan

### Step 1 — Theme application layer

Instead of reading SharedPreferences and poking view backgrounds, wire everything through **theme overlays**.

1. Define a base `Theme.FortyTwo` from Phase 1.
2. Define overlay themes for each preset seed: `ThemeOverlay.FortyTwo.Teal`, `ThemeOverlay.FortyTwo.Blue`, etc. — each sets `colorPrimary`, `colorSecondary`, `colorSurface`, etc. to the derived M3 palette for that seed.
3. In each `Activity.onCreate`, before `super.onCreate`, read the stored seed key and `setTheme(R.style.ThemeOverlay_FortyTwo_Xxx)`.
4. For custom seed: generate the palette at runtime using the `com.google.android.material:material` `MaterialColors.harmonize(...)` + `Scheme` APIs, and apply via a `Theme` object programmatically.
5. Light/dark: call `AppCompatDelegate.setDefaultNightMode(...)` based on the user's appearance setting.
6. Dynamic color: `DynamicColors.applyToActivitiesIfAvailable(...)` with an opt-in predicate that reads the preference.

### Step 2 — ThemePreferences

New small class wrapping SharedPreferences with typed accessors:

```java
enum SeedPreset { TEAL, BLUE, GREEN, PURPLE, ORANGE, PINK, CUSTOM }
enum Appearance { LIGHT, DARK, SYSTEM }

boolean isDynamicColorEnabled()
SeedPreset getSeedPreset()
int getCustomSeedColor()
Appearance getAppearance()
Integer getKeyColorOverride()   // null if not set; paid-only
```

On change → broadcast a local intent so open activities recreate themselves with the new theme. Or use `ActivityCompat.recreate()` directly after a commit.

### Step 3 — New editor screen

1. Replace [activity_color_picker.xml](app/src/main/res/layout/activity_color_picker.xml) contents. Use `ScrollView` + M3 building blocks:
   - `MaterialToolbar` for the app bar.
   - `MaterialCardView` for the preview.
   - `ChipGroup` (single selection) for preset swatches.
   - `MaterialButtonToggleGroup` for Light/Dark/System.
   - `MaterialSwitch` for dynamic color.
   - Section headers with `textAppearanceTitleMedium`.
   - Paid "Advanced" section visually separated with a divider + gold star icon.
2. Preview card: mount a stripped-down version of the dialpad + result strip. Wrap it in a `ContextThemeWrapper` so applying a test theme doesn't affect the rest of the activity. Debounce seed changes 150ms before re-rendering.
3. Delete [ColorPickerPalette.java](app/src/main/java/com/sepidsa/fortytwocalculator/ColorPickerPalette.java), [ColorPickerSwatch.java](app/src/main/java/com/sepidsa/fortytwocalculator/ColorPickerSwatch.java), [ColorStateDrawable.java](app/src/main/java/com/sepidsa/fortytwocalculator/ColorStateDrawable.java), [color_picker_swatch.xml](app/src/main/res/layout/color_picker_swatch.xml) — replaced by `ChipGroup` + `AlertDialog` color picker. Saves ~350 LOC.

### Step 4 — Custom color picker dialog

Use an existing M3 color picker library (e.g. `com.github.skydoves:colorpickerview`) or write a small HSV-slider dialog (~80 LOC) using M3 `Slider`. Prefer the latter for dependency hygiene.

### Step 5 — Strings

Extract all hardcoded Persian strings from the old layout into `strings.xml` with translations already supported in the app:
- `appearance_title` → "Appearance" / "ظاهر" / "Apparence" / "مظهر"
- `theme_color` → "Theme color"
- `key_color` → "Key color"
- `appearance_light` / `appearance_dark` / `appearance_system`
- `dynamic_color` / `dynamic_color_subtitle`
- `paid_feature_hint` (replaces "starred items are gold version only")

### Step 6 — Migration for existing users

Users currently have raw color ints stored in SharedPreferences under the old keys.

1. On first launch of the new version, detect the legacy keys.
2. Map legacy accent color → nearest preset seed (or preserve as CUSTOM).
3. Map legacy keypad color → `keyColorOverride` (preserved for paid users only).
4. Write the new preference keys and delete the old ones.
5. If the user previously enabled classic retro theme: silently drop it (theme removed) and show a one-time snackbar "Classic theme retired" with a link to the release notes.

### Step 7 — Live-preview wiring

1. The preview card is a `View` hierarchy rendered under a `ContextThemeWrapper(themeResIdForCurrentEditorSelection)`.
2. On every chip / slider / toggle change: compute the new theme res id (or runtime `Theme`) and call `preview.setBackgroundTintList(...)` / inflate a fresh mini layout. Cheap — no IPC, no activity restart.
3. When the user taps "Apply" (or the screen is popped), commit to `ThemePreferences` and trigger `recreate()` on the stack.

### Step 8 — Cleanup

- Remove `ColorPicker*` classes (see Step 3).
- Remove any SharedPreferences key reads scattered through `MainActivity` that directly set view backgrounds — they become dead code once themes flow through role tokens.
- Remove any references to the classic retro theme from activities and layouts (separate sub-task; retro keypad fragment removal was already foreseen).

---

## Open questions for the user

1. **Is the "classic retro theme" fully dropped from the app**, including the retro dialpad fragment ([fragment_dialpad_retro.xml](app/src/main/res/layout/fragment_dialpad_retro.xml)) and paid gating? Or just hidden from the theme editor while keeping the feature accessible elsewhere?
2. **Paid feature boundary** — today both "keypad color" and "classic theme" are paid. If classic is dropped, do we (a) make key color the sole paid theming feature, (b) add something new to paid (e.g. custom seed color), or (c) make all theming free and move the paywall elsewhere? Option (a) is simplest.
3. **Preset seed list** — is "teal, blue, green, purple, orange, pink" a good starting set, or do you want a specific brand palette? The first preset should match the current #1abc9c teal so existing users see no change by default.
4. **Dynamic color default** — opt-in (current proposal) or opt-out (on by default for Android 12+)? Opt-in is safer for brand identity.
5. **Migration** — OK to best-effort map old accent colors to the nearest preset? Or preserve *exactly* as CUSTOM so the user sees no color change? Exact preservation is friendlier but means most users never discover presets.
6. **Legacy color picker library** — is anything else in the app using `ColorPickerPalette` / `ColorPickerSwatch`? I'll grep during implementation, but worth asking if there's historical reason to keep them.

---

## Acceptance criteria

- A single seed selection updates the entire UI consistently (no orphan dividers or mismatched outlines).
- Dark mode works correctly for every preset + custom seed.
- Dynamic color, when enabled on Android 12+, overrides the seed and tracks wallpaper changes.
- Paid "key color" continues to work for entitled users and is gated with a clear paid-feature hint for free users.
- Live preview updates in realtime as the user changes any theming option.
- All editor strings are localized (English, French, Persian, Arabic) — no hardcoded Persian in the XML.
- Existing users' chosen colors are migrated gracefully; nobody opens the app to a theme they didn't choose.
- ~350 LOC of custom color-picker code is removed.
- No regression on main dialpad (Phase 1), journal (Phase 2), or scientific (Phase 3).
