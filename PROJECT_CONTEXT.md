# 42 Calculator — Project Context

Read this file first in any new session. It summarises every major decision made so far.

---

## What this is

A 2015 Android calculator app being fully modernised and extended to iOS and web. Codebase: `/Users/samparhizkar/Developer/42-calculator`. Current version: 2.11, targetSdk 36, minSdk 21, Java.

**Platform strategy:** Three separate native projects — Android first, then iOS, then web. No cross-platform framework. See `FUTURE_PLATFORMS.md` for full rationale and per-platform specs.

---

## Design — VOID v2 (locked April 2026)

**`DESIGN_SPEC.md` is the ground truth.** Read it before implementing anything. Summary:

- **Dark-first, full-bleed.** Background `#080808`. No teal header. No tab bar. No persistent chrome. The screen is the calculator.
- **DM Mono throughout** — expression, result, key labels, words. The slashed zero `⊘` is a visual signature.
- **Inter** for non-calculator screens only (settings, history rows, FAQ).
- **= key as corner lamp.** A permanent radial teal glow bleeds from the bottom-right corner. The keypad has directionality.
- **Active operator glow.** The last-pressed operator key holds a teal glow until the next digit clears it. No other calculator does this — it's the signature interaction.
- **Ghost key panels.** Keys are barely-there: faint fill, subtle border, 1px inset top highlight (glass depth effect).
- **Gestural navigation.** Swipe right on display → History. Swipe up on keypad → Scientific panel. Long-press `=` → Settings. No tab bar, no bottom toolbar.

### Animation system (core identity)
| Animation | Behaviour |
|-----------|-----------|
| Key press | `scale(0.88)` in 50ms + radial white bloom; spring release 220ms |
| Result update | Per-digit mechanical counter — unchanged digits stay locked, changed/new digits slide up from below (180ms spring) |
| Equals press | Per-digit spring staggered R→L + `scale(1.06)` overshoot + teal warmup flash + sonar ring from `=` corner + words typewriter |
| Active operator | Teal glow held on key until next digit clears it |
| Idle breathing | Result pulses `opacity 1↔0.78` over 4s after 2s of no input |
| Words | Typewriter reveal, 26ms per character |

### Colour tokens (key VOID values — dark mode)
```
background:        #080808
brand / = key:     #1abc9c
keyDigit bg:       rgba(255,255,255,0.04)
keyOp bg:          rgba(255,255,255,0.065)
keyActiveOp bg:    rgba(26,188,156,0.13)
keyError bg:       rgba(255,45,45,0.08)
```
Full 48-token set in `DESIGN_SPEC.md §Color tokens`.

---

## Key app features

- **4 languages:** English, French, Persian, Arabic (RTL throughout)
- **Live result + number-to-words:** result updates as you type; after `=` shows spoken form ("nine hundred thirty-five") — app's signature feature
- **Constants:** predefined + user-defined named values, insertable into expressions
- **History:** every calculation stored in SQLite (already); UI under-surfaces it
- **Scientific panel:** √, trig, INV/ARC/DEG/RAD, EXP, hyperbolics, rand
- **Theme editor:** seed-based colour system, 6 presets + custom; paid key-colour override
- **Classic retro theme → dropped**
- **Currency converter → removed**

---

## Android tech stack

- Java, AndroidX, `com.google.android.material:material:1.12.0` on classpath
- Theme: `Theme.AppCompat.Light` → migrating to `Theme.Material3.DayNight.NoActionBar` + `Theme.FortyTwo` overlay (Phase 1)
- SQLite via ContentProvider (`LogContract` / `LogDbHelper` / `LogProvider`)
- ViewPager (3 panes: journal | dialpad | scientific) → being retired across phases
- `Expression.java` (744 lines) — calculation engine with BigDecimal precision

---

## Android modernisation — 5 phases

Each phase has a detailed doc in the repo root. All phase docs have a design-pivot warning pointing to `DESIGN_SPEC.md`.

### Phase 1 — Keypad (`PHASE_1_MATERIAL3_KEYPAD.md`)
Full VOID keypad implementation. Replace plain `Button`s with `MaterialButton`. Apply VOID colour tokens. Bundle DM Mono + Inter fonts. Implement per-digit animation system via `ValueAnimator` / `ObjectAnimator`. Active operator glow. Scale + bloom press animation. Haptics.

**Button roles:**
- Digit → `rgba(255,255,255,0.04)` ghost panel
- Operator → `rgba(255,255,255,0.065)` ghost panel
- Equals → `#1abc9c` solid + corner glow
- Error (C/⌫) → `rgba(255,45,45,0.08)` red-tinted panel

**Open questions:**
1. Confirm teal `#1abc9c` seed (DESIGN_SPEC already uses it)
2. Dynamic color opt-in or opt-out for Android 12+?
3. Paid key-colour override — `colorSecondaryContainer` only?

### Phase 2 — History (`PHASE_2_JOURNAL_HISTORY.md`)
Replace `FavoritesFragment` + `AnimatedLogFragment` with one unified `HistoryFragment`. Filter chips (All / ★ Starred / 🏷 Labeled). Fix "dead label" bug. Swipe-to-delete + undo. Tap-to-reuse. Date section headers (requires DB schema bump `created_at` column, version 1→2).

**Deletes ~880 LOC** (`FavoritesFragment`, `FavoritesAdapter`, `AnimatedLogFragment`).

**Open questions:**
1. Default sort: newest-first or pinned-starred?
2. Tap-to-reuse: replace expression or append?
3. History cap (N rows) or unbounded?
4. Room migration now or defer?

### Phase 3 — Scientific Panel (`PHASE_3_SCIENTIFIC_PANEL.md`) ✅ COMPLETE

`ModalBottomSheet` via "SCI ▲" pill (pager reduced to 2 pages). VOID ghost-key styling. Segmented `INV | ARC | DEG⇄RAD` row. Fixed INV semantics (powers/logs only). mXparser-compatible token emission. Dividers between groups. CONST chip stub. Deleted 7 dead-code files. Pre-existing build errors fixed. Branch: `claude/eloquent-pascal-38bbdc`, commit `6b420f0`.

### Phase 4 — Theme Editor (`PHASE_4_THEME_EDITOR.md`)
Replace 2-colour raw picker with seed-based theming. Mini calc live-preview card. 6 seed presets + custom. Light/Dark/System segmented. Dynamic color switch. Paid "Key colour" advanced section. Migrate legacy SharedPrefs colours to nearest preset.

**Removes ~350 LOC** (`ColorPickerPalette`, `ColorPickerSwatch`, `ColorStateDrawable`). Fixes hardcoded Persian strings in XML (localization bug for EN/FR/AR users).

**Open questions:**
1. Retro theme — fully deleted including the layout file?
2. Custom seed paid or free?
3. Migration: map to nearest preset or preserve as CUSTOM?

### Phase 5 — Settings + Drawer Retirement + Currency Removal (`PHASE_5_SETTINGS.md`)
Build proper `SettingsActivity`. Retire navigation drawer entirely (wrong pattern — all items are utility, not destinations). Consolidate 6 scattered SharedPrefs buckets. Remove currency feature (dead). Simplify main screen bottom bar to 1 icon (settings gear only — or remove entirely once gestural navigation lands).

**Settings structure:** General → Calculator → Appearance → History → Constants → Help → About → Premium.

**Open questions:**
1. Language switching: runtime-restring (current) or standard Android locale restart?
2. `jsoup` — only used by currency? Drop it.
3. `library-2.4.1.aar` — confirm droppable once drawer gone.
4. About social links: which are still active?

---

## What's next (implementation order)

1. **Answer Phase 1 open questions** (seed confirm, dynamic color default, paid boundary)
2. **Implement Phase 1** — VOID keypad on Android
3. **Phases 2–5** sequentially
4. **iOS project** — start fresh SwiftUI project per `FUTURE_PLATFORMS.md §Project 2`
5. **Web project** — React 19 + Vite per `FUTURE_PLATFORMS.md §Project 3`; animation prototype already built at `design-preview/void-v2.html`

---

## Design + planning files

| File | Purpose |
|------|---------|
| `DESIGN_SPEC.md` | **Ground truth.** VOID v2 colours, typography, animations, per-screen specs |
| `FUTURE_PLATFORMS.md` | Native platform strategy, iOS/Web project structures, engine porting checklist, session starter prompts |
| `PHASE_1_MATERIAL3_KEYPAD.md` | Android keypad implementation plan |
| `PHASE_2_JOURNAL_HISTORY.md` | Android history rework plan |
| `PHASE_3_SCIENTIFIC_PANEL.md` | Android scientific panel plan |
| `PHASE_4_THEME_EDITOR.md` | Android theme editor plan |
| `PHASE_5_SETTINGS.md` | Android settings + drawer retirement + currency removal |
| `design-preview/void-v2.html` | **Interactive prototype** — working VOID calculator with all animations. Open at `http://localhost:7842/void-v2.html` |
