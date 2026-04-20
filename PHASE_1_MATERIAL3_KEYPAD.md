# Phase 1 — Keypad Redesign

> **Status: COMPLETE** (April 2026, branch `claude/eloquent-pascal-38bbdc`)
>
> **Design: VOID v2 (locked April 2026).** Full spec in **`DESIGN_SPEC.md`** — that file is the ground truth. The app is dark-first (`#080808`), DM Mono throughout, gestural navigation (no tab bar / bottom toolbar).

> **Implementation note:** This phase was implemented in **Jetpack Compose**, not XML layouts. The original plan below assumed XML-based layouts (`fragment_dialpad_flat.xml`, `MaterialButton`, `StateListAnimator`), but the codebase had already migrated to Compose before Phase 1 began. All implementation details reflect the Compose approach.

---

## What was implemented

### Files changed

| File | Change |
|------|--------|
| `ui/theme/Color.kt` | Added 30 VOID key color tokens — dark ghost panels (digit/op/eq/activeOp/error), light mode opaque equivalents, brand colors |
| `ui/theme/Type.kt` | Changed `DmMono` and `Inter` from `private` to `internal` so they're accessible across the UI layer |
| `ui/theme/AppTheme.kt` | Changed `dynamicColor` default from `true` to `false` — VOID brand palette locks in on all devices |
| `ui/calculator/CalculatorViewModel.kt` | Added `activeOperator: String?` to `CalculatorUiState`; set on `÷ × − +` press, cleared on digit/C/=/backspace |
| `ui/calculator/CalculatorScreen.kt` | Full VOID keypad rewrite (see below) |
| `ui/main/MainScreen.kt` | Fixed `CalculatorDisplay` to VOID spec; removed `TranslationBar`; restructured weights |

---

### CalculatorScreen.kt — VOID keypad

**Key geometry:**
- Corner radius: `14.dp` (`RoundedCornerShape`)
- Gap between keys: `5.dp` (`Arrangement.spacedBy`)
- Keypad padding: `10.dp` horizontal, `4.dp` top, `18.dp` bottom

**Button roles and colors (dark mode):**
| Role | Keys | Background | Border | Text |
|------|------|-----------|--------|------|
| `Digit` | 0–9, `.` | `rgba(255,255,255,0.04)` | `rgba(255,255,255,0.08)` | `onSurface` |
| `Operator` | `÷ × − + ( ) %` | `rgba(255,255,255,0.065)` | `rgba(255,255,255,0.13)` | `onSurface` |
| `Equal` | `=` | `#1abc9c` solid | none | `#001F16` |
| `ActiveOp` | last pressed arithmetic op | `rgba(26,188,156,0.13)` | `rgba(26,188,156,0.38)` | `#1DDBB8` |
| `Danger` | `C` / `⌫` | `rgba(255,45,45,0.08)` | `rgba(255,80,80,0.20)` | `rgba(255,100,100,0.85)` |

**Label font sizes (DM Mono Normal):**
- 1-char labels: `20.sp`
- 2-char labels: `16.sp`
- 3+ char labels: `13.sp`

**Press animation:**
- `DOWN`: `scale(0.88)` in `50ms`, `LinearEasing` — via `animateFloatAsState` + `collectIsPressedAsState()`
- `UP`: spring release, `dampingRatio = 0.4f`, `StiffnessMediumLow` (~220ms with slight overshoot)
- Applied via `Modifier.graphicsLayer { scaleX = scale; scaleY = scale }`

**Active operator glow:**
- State `activeOperator: String?` in `CalculatorUiState`
- Set when `÷ × − +` pressed; cleared on next digit, `=`, `C`, or backspace
- Button switches to `ActiveOp` color role when `state.activeOperator == key.value`

**Corner ambient glow:**
- `220.dp × 220.dp` `Box` at `Alignment.BottomEnd`, behind all keys
- `Brush.radialGradient` from `#1abc9c` at 18% opacity → transparent

**Haptics:**
- `LocalHapticFeedback.current.performHapticFeedback(HapticFeedbackType.TextHandleMove)` on every press
- `HapticFeedbackType.LongPress` on long-press

---

### MainScreen.kt — display area

**CalculatorDisplay** (replaces the old `primaryContainer` surface):
- Background: `VoidDarkBackground` (`#080808`)
- Padding: `44.dp` top, `20.dp` sides, `14.dp` bottom
- **Expression line**: right-aligned, DM Mono Light 13sp, `onSurface @ 55%` opacity
- **Result**: right-aligned, DM Mono Light, dynamic size:
  - ≤4 chars → `72.sp` | ≤6 → `56.sp` | ≤8 → `44.sp` | ≤10 → `36.sp` | 11+ → `28.sp`
- **Words line**: right-aligned, DM Mono Light Italic 11sp, `onSurface @ 20%` opacity — shown only when `isCalculationPerformed`
- **DEG/RAD**: DM Mono 11sp, top-right, tappable (toggles angle mode)
- **History icon**: dim, top-left

**TranslationBar removed** — words line now lives inside `CalculatorDisplay`.

Layout weights updated: display `0.38f`, keypad column `0.62f`.

---

## What was NOT implemented (deferred)

These remain for later phases:

- **Per-digit mechanical counter animation** (changed digits slide up from below) — complex custom composable, deferred to a polish pass
- **Equals press sonar ring** — `ValueAnimator`-style ring expanding from `=` — deferred
- **Idle breathing** (result pulses `opacity 1↔0.78` after 2s of no input) — deferred
- **Clear animation** (digits enter "0" from below on C press) — deferred
- **Typewriter words reveal** (26ms/char after `=`) — currently words appear instantly
- **Retro theme** — untouched per spec; stays in `fragment_dialpad_retro.xml`
- **Scientific panel** — Phase 3
- **DEG/RAD in keypad** — removed from main keypad (was a FilterChip); now tappable in display area; full toggle moves to Scientific panel in Phase 3

---

## Open questions resolved

1. **Seed color**: teal `#1abc9c` confirmed — hardcoded as `VoidBrand` in `Color.kt`
2. **Dynamic color**: opted OUT by default (`dynamicColor = false` in `AppTheme`) — VOID identity takes priority
3. **Paid key-colour override**: not implemented yet — Phase 4 (Theme Editor)
4. **Operator contrast**: VOID ghost-panel approach (not bold teal outlines) — matches `DESIGN_SPEC.md` exactly

---

## Acceptance criteria — status

| Criterion | Status |
|-----------|--------|
| Ghost panel key colors in dark mode | ✅ |
| 14dp corners, 5dp gaps, correct padding | ✅ |
| DM Mono key labels, correct font sizes | ✅ |
| Scale(0.88) + spring press animation | ✅ |
| Active operator glow (state-driven) | ✅ |
| Corner ambient teal glow | ✅ |
| Haptics on every key press | ✅ |
| VOID brand palette locked (no dynamic color) | ✅ |
| Display: #080808 background | ✅ |
| Display: expression + result + words | ✅ |
| Display: dynamic result font size | ✅ |
| Per-digit counter animation | ⏳ deferred |
| Sonar ring on = | ⏳ deferred |
| Idle breathing | ⏳ deferred |
| No visual regression on scientific / history screens | ✅ |
