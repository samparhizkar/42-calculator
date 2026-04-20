# Phase 1 — Keypad Redesign

> **Design: VOID v2 (locked April 2026).** This is the Android implementation. Full spec in **`DESIGN_SPEC.md`** — that file is the ground truth. The app is dark-first (`#080808`), DM Mono throughout, gestural navigation (no tab bar / bottom toolbar). M3 token names are used as the implementation vocabulary but the visual output is VOID — ghost key panels, per-digit mechanical counter animation, active operator glow, = key as corner lamp.

Scope: modernize the primary (non-retro) dialpad at [fragment_dialpad_flat.xml](app/src/main/res/layout/fragment_dialpad_flat.xml) using the design system from `DESIGN_SPEC.md`. Keep behavior and IDs identical so the existing `MainActivity` / fragment wiring continues to work.

Out of scope (tracked separately in later phases):
- Retro/classic theme ([fragment_dialpad_retro.xml](app/src/main/res/layout/fragment_dialpad_retro.xml)) — stays untouched.
- Scientific panel redesign.
- Journal / bookmarks rework.
- Theme editor changes.
- Currency feature removal.

---

## Current state (findings)

**Layout** — [fragment_dialpad_flat.xml](app/src/main/res/layout/fragment_dialpad_flat.xml)
- 4 vertical `LinearLayout` columns inside a horizontal `LinearLayout`, each column weighted 1.
- Rows per column are 5 × weighted `Button` elements. Column 1 is misnamed `SecondRow` but holds `C / 7 / 4 / 1 / .`.
- Every button is a plain `android.widget.Button` styled via `@style/CustomButton` or `@style/CustomButton.DIGIT`.
- Two buttons override `android:background` inline: `buttonClear` → `@drawable/selector_for_btn_clear`, `buttonEquals` → `@drawable/selector_for_btn_equal`.
- Lots of dead whitespace and a commented-out duplicate `button8`.

**Styles** — [styles.xml](app/src/main/res/values/styles.xml:25)
- `CustomButton` sets a transparent background, `android:soundEffectsEnabled=false`, and the digit color `@color/gray_buttons_text_color`.
- `CustomButton.DIGIT` swaps in `@drawable/selector_for_btn_dialpad` — which is just a single transparent item (no pressed/focused state at all, so there's no visible feedback today).

**Theme** — [themes.xml](app/src/main/res/values/themes.xml)
- Activity inherits `Theme.AppCompat.Light`. **Not** a Material 3 theme. No `colorPrimary`, `colorSecondary`, `colorSurface`, dynamic color, or Material Components theming attributes.

**Material library** — `com.google.android.material:material:1.12.0` is already on the classpath ([app/build.gradle:48](app/build.gradle:48)). We can use `MaterialButton`, `Theme.Material3.*`, `DynamicColors`, etc. without adding dependencies.

**Min/target SDK** — `minSdk 21`, `targetSdk 36`. Material 3 + MaterialButton fully support API 21+. Dynamic color (`DynamicColors.applyToActivitiesIfAvailable`) is a no-op pre-Android 12 and gracefully falls back to the static seed palette we define.

**Keys touched on keypad** (IDs to preserve):
`buttonClear, button0-9, buttonPoint, openParen, closeParen, buttonPercent, buttonDevide, buttonTimes, buttonMinus, buttonPlus, buttonEquals`.

---

## Target design

Visual language: custom brand-forward buttons (see `DESIGN_SPEC.md`), rounded corners, scale-press animation, haptic feedback, semantic color roles so dark mode "just works."

**Button roles**
| Role | Keys | Background | Text |
|------|------|-----------|------|
| Digit | `0–9`, `.` | `surfaceContainerHigh` | `onSurface` |
| Operator | `÷ × − +`, `(`, `)`, `%` | `secondaryContainer` (accent teal) | `onSecondaryContainer` |
| Equals | `=` | `primary` (high-emphasis) | `onPrimary` |
| Error | `C` / `⌫` | `errorContainer` | `error` |

**C / ⌫ toggle** — when the expression is empty, show `C`; when the expression has content, show `⌫`. Both use the `error` role.

**Shape** — 28dp corner radius on all four corners (matches DESIGN_SPEC — pill feel on square-ish buttons). No per-corner variants.

**Spacing** — 5dp gap between buttons (2–3dp `layout_margin` on each button). Edge padding: 10dp horizontal, 6dp top around the whole keypad. (DESIGN_SPEC §Keypad)

**Typography** — Inter Medium (weight 500) for all keypad labels, not system default. Font sizes per role:
- Single-char operators / digit keys: 22sp
- 2-char labels (e.g. `ln`, `x²`): 17sp
- 3+ char labels (e.g. `sin`, `EXP`): 14sp

Set via `android:fontFamily="@font/inter_medium"` in button styles. Download Inter from Google Fonts or bundle the `.ttf` in `res/font/`.

**Motion / feedback**
- **Key press scale:** `scale(0.88)` in 50ms on `onTouchEvent ACTION_DOWN`; spring release in 220ms on `ACTION_UP`. Implement via `ViewPropertyAnimator` in a shared `OnTouchListener`, or via `StateListAnimator` XML (see Step 2 — the `keypad_button_press.xml` animator).
- **Key bloom:** radial white `RadialGradient` overlay fades in on press, out on release. Implement as a custom `Drawable` set as `foreground` on each `MaterialButton`.
- **Per-digit result animation:** custom `DigitCell` view wrapping a pair of `TextView`s with `clipChildren=true`. On result change, diff old vs new string, slide changed digits via `ObjectAnimator(translationY)`. See DESIGN_SPEC.md §Animation system for the full diff algorithm.
- **Active operator glow:** toggle `backgroundTint` + `strokeColor` + `elevation` programmatically when an operator is pressed/cleared.
- **Sonar ring on `=`:** `ValueAnimator` driving a custom `View`'s `radius` and `alpha`; overlaid on the keypad container.
- **Haptics:** `VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)` on API 29+; `vibrator.vibrate(20)` fallback. One call in the shared `OnTouchListener`.
- Re-enable `android:soundEffectsEnabled="true"` (currently disabled globally in `CustomButton`).

**Color tokens** — exact hex values are in `DESIGN_SPEC.md §Color tokens`. Summary for the keypad roles:
- Light: `surfaceContainerHigh=#e3e9e7`, `secondaryContainer=#cce8e2`, `primary=#006a5e`, `errorContainer=#ffdad6`, `error=#ba1a1a`
- Dark: `surfaceContainerHigh=#252b2a`, `secondaryContainer=#334b48`, `primary=#80d5c6`, `errorContainer=#93000a`, `error=#ffb4ab`

**Dark mode** — define `values-night/colors.xml` using the dark token set above so role tokens flip automatically.

**Dynamic color (Android 12+)** — opt in via `DynamicColors.applyToActivitiesIfAvailable(application)` in `Application.onCreate`. Falls back to static palette on older devices.

---

## Implementation plan

### Step 1 — App theme + color tokens
1. Create [values/themes.xml](app/src/main/res/values/themes.xml) entry `Theme.FortyTwo` parenting `Theme.Material3.DayNight.NoActionBar`.
2. Define role-token colors in `values/colors.xml` and `values-night/colors.xml` using the **exact hex values from `DESIGN_SPEC.md §Color tokens`** (seeded from `#1abc9c`). Key light values: `primary=#006a5e`, `secondaryContainer=#cce8e2`, `surface=#f5faf9`, `errorContainer=#ffdad6`, `error=#ba1a1a`, `brand=#1abc9c`. Dark equivalents in `values-night/`.
3. Point `android:theme` in [AndroidManifest.xml](app/src/main/AndroidManifest.xml) at `Theme.FortyTwo` for `MainActivity`. Leave other activities on the old theme for now.
4. Add a minimal `FortyTwoApp : Application` class (if one doesn't exist) and call `DynamicColors.applyToActivitiesIfAvailable(this)` in `onCreate`.

### Step 1b — Inter font
1. Download `Inter-Medium.ttf` (weight 500) and `Inter-Regular.ttf` from [rsms/inter](https://github.com/rsms/inter/releases). Place in `app/src/main/res/font/`.
2. Declare in `res/font/inter_medium.xml` as a `<font-family>` if needed, or reference directly as `@font/inter_medium`.
3. Also download `DMMono-Regular.ttf` for the display/expression area (referenced in Phase 2+ display work).
4. All keypad button styles reference `android:fontFamily="@font/inter_medium"` (see Step 2).

### Step 2 — Button styles
Add to [styles.xml](app/src/main/res/values/styles.xml) (or a new `keypad_styles.xml`). Font sizes match DESIGN_SPEC §Typography; colors match DESIGN_SPEC §Keypad:

```xml
<style name="Keypad.Button" parent="Widget.Material3.Button.TonalButton">
    <item name="android:layout_width">match_parent</item>
    <item name="android:layout_height">0dp</item>
    <item name="android:layout_weight">1</item>
    <item name="android:layout_margin">2dp</item>
    <item name="android:insetTop">0dp</item>
    <item name="android:insetBottom">0dp</item>
    <item name="cornerRadius">28dp</item>
    <item name="android:fontFamily">@font/inter_medium</item>
    <item name="android:textSize">22sp</item>
    <item name="android:soundEffectsEnabled">true</item>
    <item name="android:hapticFeedbackEnabled">true</item>
    <item name="backgroundTint">?attr/colorSurfaceContainerHigh</item>
    <item name="android:textColor">?attr/colorOnSurface</item>
    <item name="android:stateListAnimator">@animator/keypad_button_press</item>
</style>

<style name="Keypad.Button.Operator" parent="Keypad.Button">
    <item name="backgroundTint">?attr/colorSecondaryContainer</item>
    <item name="android:textColor">?attr/colorOnSecondaryContainer</item>
</style>

<style name="Keypad.Button.Equals" parent="Keypad.Button">
    <item name="backgroundTint">?attr/colorPrimary</item>
    <item name="android:textColor">?attr/colorOnPrimary</item>
</style>

<!-- Error role: filled on errorContainer, label in error color -->
<style name="Keypad.Button.Error" parent="Keypad.Button">
    <item name="backgroundTint">?attr/colorErrorContainer</item>
    <item name="android:textColor">?attr/colorError</item>
</style>
```

Also add `res/animator/keypad_button_press.xml` — the `scale(0.91)` + `alpha 0.85` press animation:

```xml
<!-- res/animator/keypad_button_press.xml -->
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_pressed="true">
        <set>
            <objectAnimator android:propertyName="scaleX"
                android:valueTo="0.91" android:duration="80"
                android:interpolator="@android:interpolator/fast_out_slow_in"/>
            <objectAnimator android:propertyName="scaleY"
                android:valueTo="0.91" android:duration="80"
                android:interpolator="@android:interpolator/fast_out_slow_in"/>
            <objectAnimator android:propertyName="alpha"
                android:valueTo="0.85" android:duration="80"/>
        </set>
    </item>
    <item>
        <set>
            <objectAnimator android:propertyName="scaleX"
                android:valueTo="1.0" android:duration="80"
                android:interpolator="@android:interpolator/fast_out_slow_in"/>
            <objectAnimator android:propertyName="scaleY"
                android:valueTo="1.0" android:duration="80"
                android:interpolator="@android:interpolator/fast_out_slow_in"/>
            <objectAnimator android:propertyName="alpha"
                android:valueTo="1.0" android:duration="80"/>
        </set>
    </item>
</selector>
```

### Step 3 — Rewrite `fragment_dialpad_flat.xml`
- Replace the 4-column `LinearLayout` with a single root `LinearLayout` + 5 horizontal row `LinearLayout`s (or a 5×4 `GridLayout`), which reads closer to the visual mental model and makes it easier to reason about row heights.
- Change every `<Button>` to `<com.google.android.material.button.MaterialButton>`.
- Apply styles per role table above. Preserve all `android:id` values exactly.
- Delete commented-out duplicate `button8` and stray whitespace.
- **C / ⌫ toggle**: `buttonClear` uses `Keypad.Button.Error`. In `MainActivity`, set `buttonClear.setText("⌫")` when `expression.length() > 0`, `"C"` when empty. Both states share the same ID and style — only the label string changes.

### Step 4 — Landscape + tablet layouts
Check [layout-land](app/src/main/res/layout-land) and any `layout-sw*` folders — apply the same style swap so orientation doesn't regress.

### Step 5 — Haptics
In `MainActivity`'s button click handler (shared listener for digits), add:
```java
v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
```
One call site. Drop the `soundEffectsEnabled=false` from `CustomButton` so system key-click sounds work too (user can disable at OS level).

### Step 6 — Clean up
- Remove now-unused drawables: `selector_for_btn_dialpad.xml`, `selector_for_btn_clear.xml`, `selector_for_btn_equal.xml` (only if no other layout still references them — grep first; retro layout uses its own `selector_for_btn_*` set).
- Keep `gray_buttons_text_color` / `red_buttons_text_color` / `orange_buttons_text_color` for now (referenced elsewhere); retire in a later phase.

---

## Open questions for the user

1. **Seed color** — keep teal `#1abc9c` as the brand seed, or pick a fresh one? (Affects the whole M3 palette.)
2. **Digit vs. operator contrast** — M3 convention gives operators a *tonal* accent (subtle). Your current design has operators in bold teal outlines. Want subtle (M3 default) or keep strong accent?
3. **Paid numpad color feature** — should recoloring still apply on top of M3 roles? Cleanest path: let paid users override `colorSecondaryContainer` only, so the role system keeps contrast correct.
4. **Dynamic color** — opt in by default (Android 12+ users get wallpaper-matched colors), or keep a fixed brand palette for identity?

---

## Acceptance criteria

- App builds and runs on API 21, 28, 34 emulators.
- Main keypad shows ripple on press and haptic click.
- Dark mode (system setting) flips keypad correctly without bleached text or invisible buttons.
- On Android 12+ device with a non-teal wallpaper, keypad adopts wallpaper-matched colors (when dynamic color is enabled).
- No visual regression on scientific panel, journal, settings screens — they keep current look until their own phases.
- All 16 keypad button IDs resolve in `MainActivity` without code changes.
