# Phase 1 — Step 2 Result: Button Styles + Press Animator

Completed: 2026-04-19

---

## What was done

### New files

| File | Purpose |
|------|---------|
| `app/src/main/res/interpolator/void_spring.xml` | PathInterpolator — cubic-bezier(0.1, 1.3, 0.6, 1.0) — Y > 1 gives ~6% overshoot on release, matching the 220ms spring spec |
| `app/src/main/res/animator/keypad_button_press.xml` | StateListAnimator: `scaleX/Y → 0.88` in 50ms (accelerate_quad) on press; `scaleX/Y → 1.0` in 220ms (void_spring) on release |

### Modified files

**`app/src/main/res/values/styles.xml`** — appended four VOID keypad styles:

| Style name | Role | Key colour token |
|-----------|------|-----------------|
| `Keypad.Button` | Digit (base) | `void_keyDigitBg` + `void_keyDigitBorder` |
| `Keypad.Button.Operator` | Operator / paren / % | `void_keyOpBg` + `void_keyOpBorder` |
| `Keypad.Button.Equals` | = key (corner lamp) | `void_keyEqBg` solid teal, `void_onBrand` text |
| `Keypad.Button.Error` | C / ⌫ | `void_keyErrorBg` + `void_keyErrorBorder` + `void_keyErrorText` |

All four styles share:
- `parent="Widget.Material3.Button"`
- `cornerRadius="14dp"` (per DESIGN_SPEC §Keypad §Button geometry)
- `android:insetTop/Bottom="0dp"` (removes M3 default vertical insets)
- `android:fontFamily="@font/dm_mono"` weight 400, `android:textSize="20sp"`
- `android:textAllCaps="false"`, `android:letterSpacing="0"`
- `android:stateListAnimator="@animator/keypad_button_press"`

**`app/src/main/res/values/colors.xml`** — added 6 missing light-mode key tokens:
- `void_keyDigitBorder`, `void_keyOpBorder`, `void_keyActiveOpBorder`
- `void_keyActiveOpText`, `void_keyErrorBorder`, `void_keyErrorText`

(Dark-mode equivalents were already in `values-night/colors.xml` from Step 1.)

### Build verification

`./gradlew :app:mergeDebugResources` → **BUILD SUCCESSFUL** — no resource errors.

---

## Design decisions

- **14dp corner radius** — DESIGN_SPEC §Keypad is ground truth (overrides the 28dp in the phase plan doc).
- **DM Mono on keys** — DESIGN_SPEC §Typography: "Keypad labels | DM Mono | 400 | 20px". The phase plan doc incorrectly listed Inter Medium — DESIGN_SPEC wins.
- **Opaque colour approximations** — Android `backgroundTint` does not composite semi-transparent values against the window background, so the rgba ghost panel values from DESIGN_SPEC are baked into opaque equivalents in `colors.xml` (light and night variants).
- **Spring interpolator** — PathInterpolator with controlY1 > 1 achieves the overshoot. True Android `OvershootInterpolator` is not addressable as an XML resource file, hence the custom path.
- **Bloom ripple placeholder** — `rippleColor` is set to a subtle white/red tint. The proper per-pixel radial bloom (DESIGN_SPEC §Animation §1) requires a custom `Drawable` set as button `foreground`; that is deferred to Step 3.
- **Active operator glow** — runtime state, not an XML style. `void_keyActiveOpBg/Border/Text` tokens are defined in both color files for use by `MainActivity` programmatically.

---

## What is NOT done yet (Step 3+)

- Layout rewrite — `fragment_dialpad_flat.xml` still uses old `<Button>` elements.
- MaterialButton tags + style application.
- 5-row LinearLayout restructure.
- Corner ambient glow view (200×200dp radial teal behind `=`).
- C / ⌫ label toggle wiring in `MainActivity`.
- Custom bloom `Drawable` foreground (replaces placeholder ripple).
- Manifest: `MainActivity` still uses old theme — switch to `Theme.FortyTwo`.

---

## Prompt for Step 3

> Read `PROJECT_CONTEXT.md`, `DESIGN_SPEC.md`, and `PHASE_1_MATERIAL3_KEYPAD.md`. Also read `PHASE_1_STEP_2_RESULT.md` for a full record of what Steps 1 and 2 produced.
>
> We are implementing **Phase 1, Step 3** of the Android 42 Calculator modernisation (VOID v2).
>
> **Step 3 — Rewrite `fragment_dialpad_flat.xml`**
>
> Do all of the following:
>
> 1. Replace the existing 4-column `LinearLayout` structure in `app/src/main/res/layout/fragment_dialpad_flat.xml` with a single root vertical `LinearLayout` containing **5 horizontal row `LinearLayout`s** (one per keypad row). Each row `LinearLayout` has `android:layout_weight="1"` so rows share height equally.
>
> 2. Change every `<Button>` to `<com.google.android.material.button.MaterialButton>`. Apply styles per role:
>    - Digits (`0–9`, `.`) → `style="@style/Keypad.Button"`
>    - Operators (`÷ × − + ( ) %`) → `style="@style/Keypad.Button.Operator"`
>    - Equals (`=`) → `style="@style/Keypad.Button.Equals"`
>    - Clear/backspace (`buttonClear`) → `style="@style/Keypad.Button.Error"`
>
>    **Preserve every existing `android:id` exactly.** IDs: `buttonClear`, `button0`–`button9`, `buttonPoint`, `openParen`, `closeParen`, `buttonPercent`, `buttonDevide`, `buttonTimes`, `buttonMinus`, `buttonPlus`, `buttonEquals`.
>
> 3. Add the **corner ambient glow view**: a 200×200dp `View` with a teal radial gradient background, positioned at the bottom-right corner of the keypad `FrameLayout` container (behind the key rows). Use a `FrameLayout` as the outer root to allow layering. The radial gradient drawable (`res/drawable/keypad_eq_glow.xml`) should be a `<shape>` or layer-list approximating `radial-gradient(brand 13% opacity)`; on Android use a `GradientDrawable` with `android:type="radial"`, `android:startColor="@color/void_brandGlow"`, `android:endColor="@android:color/transparent"`, `android:gradientRadius="100dp"`. Define `void_brandGlow` as `#21_1abc9c` in colors.xml (≈13% alpha of `#1abc9c`).
>
> 4. Delete the dead commented-out `button8` duplicate and stray whitespace.
>
> 5. After the layout rewrite, update **`AndroidManifest.xml`** to apply `Theme.FortyTwo` to `MainActivity` (the activity that hosts the dialpad fragment). Leave all other activities on their existing themes.
>
> 6. Do a Gradle build (`./gradlew :app:assembleDebug`) and fix any errors before reporting done.
>
> DESIGN_SPEC is ground truth. Key geometry: 5dp gap (2dp margin each side), 10dp horizontal keypad padding, 14dp corner radius, 18dp bottom padding.
