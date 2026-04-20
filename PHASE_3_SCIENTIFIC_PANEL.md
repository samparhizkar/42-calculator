# Phase 3 — Scientific Panel Rework ✅ COMPLETE

> **Design: VOID v2 (locked April 2026).** Android implementation. Full spec in **`DESIGN_SPEC.md`**. Values for this screen: panel background `#0e0e0e`, top corner radius 20dp, `BottomSheetBehavior` at 72% height; fn keys use `operator` ghost panel style; INV/ARC/DEG/RAD as `MaterialButtonToggleGroup`; divider between logs and trig rows; CONST chip with teal border + DM Mono label. See `DESIGN_SPEC.md §Scientific panel`.

---

## Delivered (branch `claude/eloquent-pascal-38bbdc`, commit `6b420f0`)

### What was built

| Item | Status | Notes |
|------|--------|-------|
| `ModalBottomSheet` scientific panel | ✅ | Triggered by "SCI ▲" pill above keypad; auto-dismisses on key press |
| VOID ghost-key styling on fn keys | ✅ | `VoidKeyOpBg`/`VoidKeyOpBorder`, DM Mono, press-scale animation (0.88f spring, same as Phase 1) |
| Segmented mode row `INV \| ARC \| DEG⇄RAD` | ✅ | Border+clip approach, vertical dividers, `VoidKeyActiveOpBg` fill when active |
| Fixed INV semantics | ✅ | INV swaps √↔x² and ln↔eˣ only — does NOT affect trig labels (was computing cosecant before) |
| ARC mode label swap | ✅ | Prepends "a" prefix to all trig and hyperbolic functions |
| mXparser-compatible token emission | ✅ | `sqrt(`, `^2`, `^3`, `exp(`, `log10(`, `asin(`, `asinh(` etc. |
| Dividers between groups | ✅ | 1dp `Color.White 7%` between powers/logs and trig/hyperbolics |
| CONST chip | ✅ | Teal `VoidBrand` border, pill shape, DM Mono label — callback is a stub pending Phase 5 constants UI |
| Pager reduced 3→2 pages | ✅ | History \| Calculator only; `PageIndicator` auto-updates |
| "SCI ▲" pill button | ✅ | Above keypad, `Color.White 5%/10%` bg/border, opens ModalBottomSheet |
| mXparser artifact fix in `libs.versions.toml` | ✅ | Artifact ID corrected to `MathParser.org-mXparser` |

### Key decisions made

- **Discoverability → Option A (ModalBottomSheet).** Sheet opens via "SCI ▲" pill; scientific screen removed from pager entirely.
- **ViewPager swipe to scientific → removed.** Pager is now 2 pages (History | Calculator). Single source of truth.
- **INV scope → powers/logs only.** INV does NOT change trig labels. `log` stays as `log10(` in both modes (`10^x` requires binary syntax, deferred).
- **Factorial → already present.** Phase 3 doc was written against old XML where `!` was commented out; the Compose rewrite already had it.
- **Constants picker → CONST chip is a stub.** The old `ConstantUseFragment`/`ConstantSelectFragment` relied on XML layouts that were already deleted. These fragments are deleted as dead code. Constants feature will be reimplemented in a later phase when the Compose constants dialog is built.

### Pre-existing build errors fixed (not Phase 3 scope, but unblocked the build)

- `NumberToWordsConverter.kt` — `android.icu.text.RuleBasedNumberFormat` unresolvable at compile time with AGP 9.1.1 + Kotlin 2.2.10; replaced with reflection-based access (same runtime behaviour on API 33+, compiles cleanly).
- `ParallaxPagerActivity.kt:66` — spurious `context.` prefix on `getColor()` inside a `FragmentActivity`.
- `CalculatorFragment.kt` — removed call to `MainActivity.setAngleMode()` which no longer exists.

### Deleted dead code

`CustomDialogClass.kt`, `ExpandAnimation.kt`, `ScientificFragment.kt`, `ConstantUseFragment.kt`, `ConstantUseAdapter.kt`, `ConstantSelectFragment.kt`, `ConstantSelectAdapter.kt` — all referenced removed APIs or missing XML layouts.

---

Scope: modernize the scientific keypad ([fragment_scientific_flat.xml](app/src/main/res/layout/fragment_scientific_flat.xml)) and fix the discoverability problem — today it's reachable only via an undiscoverable swipe + 3dp page dots. Also rationalize the INV/ARC toggle visual feedback and the "Constant" feature entry point.

Depends on: Phase 1 (M3 theme + `Keypad.Button.*` styles) — this phase reuses the same button language.

Out of scope:
- Constant *editor* dialog ([constant_input_dialog.xml](app/src/main/res/layout/constant_input_dialog.xml)) internals — cosmetic M3 pass only.
- Calculation engine ([Expression.java](app/src/main/java/com/sepidsa/fortytwocalculator/Expression.java)).
- Journal, theme editor, currency.

---

## Current state (findings)

**Layout** — [fragment_scientific_flat.xml](app/src/main/res/layout/fragment_scientific_flat.xml)
- 5 horizontal rows of 4 buttons each, all weighted equally inside a vertical `LinearLayout`. Row contents:
  1. `Constant` (Persian label "ثابت" in the screenshot) • `INV` toggle • `ARC` toggle • `DEG/RAD` toggle
  2. `√` • `^` • `x²` • `x³`
  3. `!` • `π` • `ln` • `log`
  4. `sin` • `cos` • `tan` • `EXP`
  5. `sinh` • `cosh` • `tanh` • `rand`
- Row 1 uses `?android:attr/borderlessButtonStyle` + `ToggleButton` with `textOn`/`textOff` for state. The toggle-on state is basically invisible (no tint, no underline) — a big usability miss since INV/ARC completely change what buttons 2–5 do.
- One divider line between row 1 and row 2 only. No visual grouping for powers / logs / trig / hyperbolics.
- Rows 2–5 share `@style/CustomButton.SCIENTIFIC` → same empty-transparent selector as the dialpad (no press feedback).
- Commented-out factorial button on row 2 — dead code.

**Navigation** — [ViewPagerAdapter.java](app/src/main/java/com/sepidsa/fortytwocalculator/ViewPagerAdapter.java) + page-dots in screenshots
- Main calculator swipe pager has 3 panes: journal-ish • dialpad • scientific. Dots are ~3dp and low-contrast. User explicitly called this out: "page dots are tiny."
- No visible affordance suggesting the scientific keypad exists. New users never find it.

**Constants feature** — [ConstantSelectFragment.java](app/src/main/java/com/sepidsa/fortytwocalculator/ConstantSelectFragment.java), [ConstantUseFragment.java](app/src/main/java/com/sepidsa/fortytwocalculator/ConstantUseFragment.java)
- Two-state UI: **picker mode** (bottom sheet listing enabled constants; tap to insert) and **manage mode** (per-constant toggle on/off + delete; "New" button for custom constants).
- Predefined constants: Avogadro, Celsius offset, Euler, Golden ratio, π, Speed of light, Standard gravity, Magnetic constant, Molar gas constant, Stefan-Boltzmann, and more (~12 total).
- Users define custom constants with a name + value (e.g. a tax rate, a project constant).
- Currently accessed via the "Constant" button top-left of the scientific panel.
- Totally disconnected visually from the `π` button that already lives on row 3 — two overlapping mental models for the same concept.

**Toggle state** — INV and ARC
- `ToggleButton` with `textOn`/`textOff` both = "INV" (or "ARC") — i.e. label doesn't change, only a subtle background tint that's hard to see against the light theme.
- ARC+sin, ARC+cos, etc. produce arcsin/arccos. INV flips √ → x², etc. Without a clear active-state indicator the user can't tell which mode they're in.

---

## Target design

Two changes: **discoverability** (how users find the scientific panel) and **in-panel polish** (M3 + visual grouping + clear toggle states).

### 1. Discoverability — "SCI" expand toggle

Replace the silent swipe-to-scientific with an explicit, visible toggle. Two options worth considering:

**Option A — Expand/collapse drawer (recommended)**
- A small chevron/pill button above the keypad ("SCI ▲") that slides the scientific keypad up **over** the dialpad when tapped, like Google Calculator's bottom-sheet expansion. Tap again to collapse.
- Swipe still works for power users.
- Entry affordance is always visible.

**Option B — Tab strip**
- M3 `TabLayout` with two tabs: "Basic" • "Scientific". Loses some vertical space for the keypad but is dead-obvious.

I recommend **Option A** — preserves screen real estate and matches Android calculator conventions. The expand button doubles as the current-mode indicator.

### 2. Button language

Apply Phase 1 `Keypad.Button.*` styles. Role mapping:

| Role | Keys | Style |
|------|------|-------|
| Function (neutral) | `√ ^ x² x³ ! π ln log sin cos tan EXP sinh cosh tanh rand` | `Keypad.Button.Operator` (tonal `secondaryContainer`) |
| Mode toggle | `INV ARC DEG/RAD` | M3 `SegmentedButton` — see below |
| Special | `Constant` | `Keypad.Button.Operator` with an icon prefix (🧭 or function-symbol) to signal it opens a picker, not inputs a value |

### 3. Mode toggles — segmented buttons

Replace the three `ToggleButton`s on row 1 with a single `MaterialButtonToggleGroup` (segmented button row, M3 pattern):

```
┌──────┬──────┬────────────┐
│ INV  │ ARC  │ DEG ⇄ RAD  │
└──────┴──────┴────────────┘
```

- Active segment gets `colorSecondaryContainer` fill + bold weight. Inactive is outlined.
- `INV` and `ARC` are independently selectable; `DEG/RAD` is a 2-state segment (single selection within itself).
- On toggle, also swap button *labels* in rows 2–5 to reflect the new mode (e.g. `sin` → `sin⁻¹` when INV active). This is a fundamental calculator pattern the current UI doesn't support — today only the computation changes, not the label, so users have to remember the mode.

### 4. Visual grouping

Insert subtle group dividers (1dp, `colorOutlineVariant`) between logical sections:

```
┌──────────────────────────────────────┐
│  [INV] [ARC] [DEG ⇄ RAD]             │
├──────────────────────────────────────┤
│  √    ^    x²    x³                  │  ← powers & roots
│  !    π    ln    log                 │  ← constants & logs
├──────────────────────────────────────┤
│  sin  cos  tan   EXP                 │  ← trig
│  sinh cosh tanh  rand                │  ← hyperbolics + misc
└──────────────────────────────────────┘
```

Alternative: skip dividers and rely on a 12dp gap between the two groups. Cleaner, more M3.

### 5. Constant button redesign

Two paths — pick one:

**A. Keep as dedicated button, move it.** Move `Constant` out of the toggle row (where it looks like a mode toggle) into the function grid, labeled with an icon + "CONST". Opens the picker dialog.

**B. Merge π into Constants.** Long-press `π` → opens constants picker. Short-press inserts π. Kills one button, makes the relationship obvious. Discoverable via a small ⋯ glyph on the `π` key.

I recommend **A** for first pass — simpler, preserves current behavior; B can follow once we observe usage.

### 6. Constants picker (cosmetic M3 pass only)
- `MaterialAlertDialog` wrapping a `RecyclerView` of constants with value, unit, and optional description.
- Filled search bar at the top.
- "Define your own" as a persistent footer row with a `+` icon.

---

## Implementation plan

### Step 1 — SCI expand toggle
1. New compound view in the main calculator layout: `ScientificExpandButton` (M3 `FilledTonalButton` with a rotating chevron).
2. When tapped, animate the scientific panel up over the dialpad (`BottomSheetBehavior` or a manual `ValueAnimator` on `translationY`). Tap again to collapse.
3. Keep the existing ViewPager swipe working for back-compat — but remove the scientific pane from the pager so it's *only* reachable via the expand button (reduces surprise; two paths are confusing). Re-evaluate after shipping.
4. Bigger page dots for the remaining 2 panes: use M3 `TabLayout` with indicator mode `dot`, or just bump `viewpagerindicator` radius to 6dp.

### Step 2 — Port scientific layout to M3
1. Rewrite [fragment_scientific_flat.xml](app/src/main/res/layout/fragment_scientific_flat.xml):
   - Root: vertical `LinearLayout` (or constraint-based).
   - Top: `MaterialButtonToggleGroup` for `INV` + `ARC` + `DEG/RAD`.
   - Below: 4×4 grid of `MaterialButton` using `Keypad.Button.Operator`.
   - 12dp horizontal padding, 6dp button gaps (match Phase 1).
2. Move `Constant` button into the grid (replaces dead commented-out factorial slot or added as a 5th row if grid fills up).
3. Delete commented-out `buttonFactoriel` block.
4. Preserve every existing `android:id` so [ScientificFragment.java](app/src/main/java/com/sepidsa/fortytwocalculator/ScientificFragment.java) click handlers don't need rewiring.

### Step 3 — INV/ARC label swaps
1. In `ScientificFragment`, observe the toggle state.
2. When `INV` checked: update button text `√→x²`, `x²→√`, `ln→e^x`, `log→10^x`, `EXP→log₁₀`. (Match the computation that's already happening in code.)
3. When `ARC` checked: `sin→sin⁻¹`, `cos→cos⁻¹`, `tan→tan⁻¹`, plus hyperbolic variants.
4. Both checked: combined labels where applicable.
5. Store original labels in `android:tag` already-present, or in a companion map.

### Step 4 — Grouping + dividers
1. Insert a `View` divider (1dp, `colorOutlineVariant`) between row 3 and row 4 of the function grid. Or use 12dp margin-top on row 4.

### Step 5 — Landscape
1. [layout-land/fragment_scientific_flat.xml](app/src/main/res/layout-land/fragment_scientific_flat.xml) (if it exists) needs the same rewrite — check during implementation.

### Step 6 — Constants picker cosmetic pass
1. Swap dialog theme to `Theme.Material3.Light.Dialog`.
2. Rework [constant_select_item.xml](app/src/main/res/layout/constant_select_item.xml) row to M3 `ListItem` style.
3. Add a search `TextInputLayout` at top.
4. Defer functionality changes; just visual polish.

### Step 7 — Cleanup
1. Delete `@style/CustomButton.SCIENTIFIC` once no layout references it.
2. Delete the 1dp `@android:listDivider` view that only sits between row 1 and row 2 today.

---

## Open questions for the user

1. **Expand vs. tabs** — prefer Option A (slide-up expand button) or Option B (tab strip) for scientific panel entry? Recommending A.
2. **Keep ViewPager swipe to scientific?** — If we add an explicit expand button, do we *also* keep the swipe gesture (two ways in = redundant but friendly), or remove it (single source of truth, less clutter)? Recommending remove.
3. **INV/ARC label swapping** — are the inverse functions already computed correctly in [ScientificFragment.java](app/src/main/java/com/sepidsa/fortytwocalculator/ScientificFragment.java)? If yes, this is purely a label concern. If no, we'd be implementing behavior that users thought existed — worth separate verification.
4. **Constants integration with π** — merge into long-press on `π` (Option B above), or keep `Constant` as its own button (Option A)? Recommending A for minimum-change.
5. **Factorial button** — it's commented out in the current layout. Bring it back (users often ask)? If yes, I'll slot it into the grid.

---

## Acceptance criteria

- New users can find the scientific panel without swiping — the expand affordance is visible on first launch.
- INV and ARC toggles show clearly when active (filled tonal state in the segmented button).
- Function button labels update when INV/ARC change (e.g. `sin` ↔ `sin⁻¹`).
- All 20 scientific button IDs resolve without changes to `ScientificFragment.java`.
- Ripple + haptics on every scientific button (inherited from Phase 1 button styles).
- Dark mode + RTL (Persian, Arabic) both render correctly.
- Constants picker dialog matches M3 dialog styling.
- No regression in the dialpad (Phase 1) or journal (Phase 2).
