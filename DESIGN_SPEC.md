# 42 Calculator — Design Specification

**VOID v2** — locked April 2026. This is the ground truth for all implementation work across Android, iOS, and web. Platform-specific implementation notes are in `FUTURE_PLATFORMS.md`.

---

## Design direction

**Dark-first. No chrome. Numbers are the UI.**

The calculator occupies the full screen. There is no persistent navigation bar, no tab bar, no toolbar. The result number is the visual hero — everything else is negative space. The single moment of colour is the `=` key in teal, which doubles as the ambient light source for the whole keypad.

Key decisions:
- **VOID palette** — near-black canvas (`#080808`), ghost key panels, teal `=` as corner lamp
- **DM Mono throughout** — expression, result, key labels, words. One typeface, different weights/sizes. The slashed zero `⊘` is intentional and becomes a visual signature.
- **Inter** for settings, history, and non-calculator UI screens only
- **No persistent navigation chrome** — all navigation is gestural or contextual (see Navigation section)
- **Drawer retired** — all utility content lives in Settings
- **Dark default** — light mode is a future option, not the primary target

---

## Color tokens

Seeded from `#1abc9c`. Same 48-role M3 system used as implementation vocabulary on Android.

### Light mode (future — not primary)
```
primary:                  #006a5e
onPrimary:                #ffffff
primaryContainer:         #9ef2e2
onPrimaryContainer:       #00201b
secondary:                #4a6360
onSecondary:              #ffffff
secondaryContainer:       #cce8e2
onSecondaryContainer:     #052020
error:                    #ba1a1a
onError:                  #ffffff
errorContainer:           #ffdad6
onErrorContainer:         #410002
surface:                  #f5faf9
onSurface:                #171d1c
surfaceVariant:           #dae5e3
onSurfaceVariant:         #3f4948
surfaceContainer:         #e9efed
surfaceContainerHigh:     #e3e9e7
surfaceContainerHighest:  #dde3e1
surfaceContainerLow:      #eff5f3
outline:                  #6f7978
outlineVariant:           #bec9c7
brand:                    #1abc9c
onBrand:                  #ffffff
brandMuted:               rgba(26,188,156,0.12)
```

### Dark mode (primary — the VOID palette)
```
background:               #080808   ← the true VOID black (not surface)
primary:                  #80d5c6   ← used in settings/history text accents
onPrimary:                #00372f
primaryContainer:         #005048
onPrimaryContainer:       #9ef2e2
secondary:                #b1ccca
onSecondary:              #1c3533
secondaryContainer:       #334b48
onSecondaryContainer:     #cce8e2
error:                    #ffb4ab
onError:                  #690005
errorContainer:           #93000a
onErrorContainer:         #ffdad6
surface:                  #0e1413
onSurface:                #dde4e2
surfaceVariant:           #3f4948
onSurfaceVariant:         #bec9c7
surfaceContainer:         #1b2120
surfaceContainerHigh:     #252b2a
surfaceContainerHighest:  #303635
surfaceContainerLow:      #171d1c
outline:                  #899392
outlineVariant:           #3f4948
brand:                    #1abc9c   ← = key fill, corner glow source
onBrand:                  #001f16   ← = key label text
brandMuted:               rgba(26,188,156,0.13)
brandGlow:                rgba(26,188,156,0.22)  ← active-op box-shadow
```

**Key-specific VOID colours:**
```
keyDigit bg:              rgba(255,255,255,0.040)
keyDigit border:          rgba(255,255,255,0.080)
keyDigit topHighlight:    rgba(255,255,255,0.060)  ← inset top 1px (glass depth)
keyOp bg:                 rgba(255,255,255,0.065)
keyOp border:             rgba(255,255,255,0.130)
keyOp topHighlight:       rgba(255,255,255,0.090)
keyEq bg:                 #1abc9c
keyEq glow:               0 0 30px rgba(26,188,156,0.50), 0 0 10px rgba(26,188,156,0.25), 0 0 70px rgba(26,188,156,0.09)
keyEq topHighlight:       rgba(255,255,255,0.280)
keyActiveOp bg:           rgba(26,188,156,0.130)
keyActiveOp border:       rgba(26,188,156,0.380)
keyActiveOp color:        #1ddbb8
keyActiveOp glow:         0 0 14px rgba(26,188,156,0.22), 0 0 4px rgba(26,188,156,0.12)
keyError bg:              rgba(255,45,45,0.080)
keyError border:          rgba(255,80,80,0.200)
keyError color:           rgba(255,100,100,0.850)
keyBloom:                 radial-gradient(circle at center, rgba(255,255,255,0.22) 0%, transparent 68%)
keyEqBloom:               radial-gradient(circle at center, rgba(255,255,255,0.35) 0%, transparent 68%)
```

---

## Typography

| Use | Font | Weight | Size |
|-----|------|--------|------|
| Result display | DM Mono | 300 | 80px (≤4 digits) → 64px → 50px → 40px → 32px (>10) |
| Expression line | DM Mono | 300 | 13px |
| Words (spoken form) | DM Mono | 300 italic | 11px |
| Keypad labels | DM Mono | 400 | 20px (single char), 16px (2-char), 13px (3+ char) |
| Scientific key labels | DM Mono | 400 | 14px |
| Settings section headers | Inter | 700 | 11px, uppercase, 1.6 letter-spacing, `primary` colour |
| Settings row titles | Inter | 400 | 16px |
| Settings row values | Inter | 400 | 13px, `onSurfaceVariant` |
| History result | Inter | 300 | 34px, `primary` colour |
| History expression | Inter | 400 | 13px, `onSurfaceVariant` |
| Constant names | Inter | 500 | 16px, `primary` colour, RTL |
| Constant values | DM Mono | 400 | 13px |
| FAQ question | Inter | 500 | 15px |
| FAQ answer | Inter | 400 | 14px, line-height 1.65 |

**DM Mono zero** — the slashed `⊘` glyph is intentional and a visual signature of the app. Do not substitute with a system font.

---

## Keypad

### Layout
5 rows × 4 columns. Full-bleed on the screen — no bottom bar, no tab bar beneath it.
```
Row 1:  C/⌫  (  )  ÷
Row 2:  7    8   9  ×
Row 3:  4    5   6  −
Row 4:  1    2   3  +
Row 5:  .    0   %  =
```

### Button roles
| Role | Keys | Visual |
|------|------|--------|
| `digit` | 0–9, . | Ghost panel — faint fill + border |
| `operator` | ÷ × − + % ( ) | Slightly brighter ghost panel |
| `equal` | = | Solid teal fill, corner-lamp glow |
| `error` | C / ⌫ | Red-tinted ghost panel |
| `fn` | Scientific keys | Same as `operator` |
| `activeOp` | Last pressed operator (state) | Teal-tinted panel with glow |

### Button geometry
- Corner radius: **14dp / 14px**
- Gap between keys: **5dp / 5px**
- Keypad horizontal padding: **10dp / 10px**
- Keypad top padding: **4dp / 4px**
- Keypad bottom padding: **18dp / 18px**
- Glass depth: **1px inset top highlight** on every key (see `keyDigit topHighlight` token)
- Bottom edge shadow: `inset 0 -1px 0 rgba(0,0,0,0.20)`

### C / ⌫ toggle
When expression is empty → label is `C` (clear). When expression has content → label is `⌫` (backspace). Same ID, same style, only the label string changes.

### Corner ambient glow
A `200×200dp` radial gradient (`brand` colour, 13% opacity) is permanently positioned at the bottom-right corner of the keypad container, behind the keys. It is not a key state — it's always present. This makes the `=` key feel like a light source for the whole keypad.

---

## Animation system

This is the core of the VOID identity. Every interaction has a physical analogue.

### 1. Key press — bloom + scale
- **Trigger:** `pointerdown` / `touchstart`
- **Scale:** `1.0 → 0.88` in `50ms ease-in` on press; `0.88 → 1.0` in `220ms cubic-bezier(0.22,1,0.36,1)` (spring overshoot) on release
- **Bloom:** radial white gradient (see `keyBloom` token) fades in on press (`opacity: 0 → 1`, 0ms), fades out on release (`opacity: 1 → 0`, 220ms)
- **= key bloom:** uses `keyEqBloom` (stronger, 35% white)

### 2. Per-digit result animation — mechanical counter
The result number is rendered as individual digit cells, each with `overflow: hidden`. On every result update:

**Diff algorithm:**
- Right-align old and new strings
- For each position: compare old character vs new character
- **Unchanged digit** → no animation, stays locked in place
- **Changed digit** → old exits upward (`digitOut`), new enters from below (`digitIn`)
- **New digit (number grew)** → enters from below (`digitIn`)
- **Removed digit (number shrank)** → exits upward (`digitOut`)

**`digitIn` keyframe:**
```
from: translateY(100%) opacity(0)
to:   translateY(0)    opacity(1)
duration: 180ms, cubic-bezier(0.22, 1, 0.36, 1)
```

**`digitOut` keyframe:**
```
from: translateY(0)     opacity(1)
to:   translateY(-110%) opacity(0)
duration: 120ms, ease-in
```

**Font-size transition:** when digit count crosses a size threshold, `font-size` transitions with `220ms cubic-bezier(0.22,1,0.36,1)`. Digits that were already in the right size don't jump — the container resizes smoothly around them.

### 3. Equals press — spring + sonar + warmup
Triggered when `=` is pressed and a valid result exists.

**Per-digit spring (staggered):**
- Each result digit plays `digitIn` (180ms spring) staggered left-to-right: `delay = rightIndex × 30ms`
- Immediately after, plays `digitSpring`:
  ```
  0%:   translateY(0)    scale(1.00)
  40%:  translateY(-6px) scale(1.06)
  70%:  translateY(2px)  scale(0.97)
  100%: translateY(0)    scale(1.00)
  duration: 380ms, cubic-bezier(0.22,1,0.36,1)
  delay: same rightIndex stagger
  ```
- Simultaneously plays `digitWarm`:
  ```
  0%:   color #ffffff
  35%:  color #d4ffef  ← brief teal-white warmup
  100%: color #ffffff
  duration: 500ms, ease
  ```

**Expression line:**
- On `=` press: `opacity → 0` over 160ms
- After 160ms: text changes to `"expr ="`, `opacity → 0.22` (becomes a faint echo)

**Sonar ring (2 rings):**
- Origin: centre of the `=` key
- Ring 1 starts at 0ms, Ring 2 at 110ms
- Each ring: 20px × 20px circle, 1.5px teal border (55% opacity), expands to cover full keypad + display (`scale ≈ maxRadius × 2.2 / 20`)
- Animation: `transform + opacity`, 750ms, `cubic-bezier(0.2,0.8,0.3,1)`, fades to `opacity: 0`

**Words typewriter:**
- Starts 120ms after `=` press
- Reveals characters one at a time, 26ms per character
- Font: DM Mono 300 italic 11px, `rgba(255,255,255,0.20)`

### 4. Active operator glow
- **Trigger:** operator key pressed (`÷ × − +`)
- **State:** the pressed key switches to `activeOp` visual (teal-tinted panel + glow)
- **Clear:** immediately when the next digit key is pressed, or when `=` or `C` is pressed
- **Only one** operator can be active at a time; pressing a second operator moves the glow

### 5. Idle breathing
- **Trigger:** 2000ms after the last key press
- **Animation:** result digits slowly pulse `opacity: 1 → 0.78 → 1`, period 4s, `ease-in-out`, loops
- **Cancel:** immediately on next key press (opacity snaps to 1, animation removed)
- Communicates that the app is alive, not frozen

### 6. Clear animation
- C key pressed: result digits play `digitIn` staggered (same as typing), all entering `"0"` from below
- This "resets" the display with a clean wipe feel

---

## Display area

- **Background:** `#080808` — seamless with the keypad, no visual separation between header and keypad zones
- **No teal header** — the old teal header is retired. The whole screen is the VOID black.
- **Expression line:** right-aligned, top of display, fades when result is shown
- **Result:** right-aligned, DM Mono 300, dynamic size (see typography table)
- **Words line:** right-aligned, DM Mono italic, fades in via typewriter after `=`
- **History icon / star icon:** small, `rgba(255,255,255,0.28)`, float in top-left. Tap history → swipe transition to history screen.
- **Minimum display height:** 195dp
- **Display padding:** `44dp top, 20dp sides, 14dp bottom`

---

## Navigation

**No persistent chrome. All gestural.**

| Destination | Trigger |
|-------------|---------|
| History | Swipe right on the display area — OR — tap the dim history icon top-left |
| Scientific panel | Swipe up on the keypad — OR — tap the small `∧` chevron above row 1 |
| Settings | Long-press `=` for 500ms — OR — tap the tiny `⚙` that fades in at display top-right after 3s idle |
| Back to calculator | Swipe left (history), dismiss sheet (scientific), back gesture (settings) |

The `⚙` settings icon appears at `opacity: 0` and fades to `rgba(255,255,255,0.22)` after 3s idle, fades back out when typing resumes. This keeps the screen clean during active use but makes settings discoverable.

**Platform adaptations:**
- **Android:** `swipe right = shared element transition` to HistoryActivity; scientific = `BottomSheetBehavior`; settings = `long-press listener` + `Intent`
- **iOS:** `swipe right = NavigationStack push`; scientific = `.sheet()` modifier; settings = `longPressGesture` + `NavigationLink`
- **Web:** `swipe right = CSS translateX transition`; scientific = `bottom: -72%` drawer with spring; settings = modal overlay

---

## Scientific panel

- **Trigger:** swipe up on keypad or chevron tap
- **Height:** 72% of screen
- **Background:** `#0e0e0e` (slightly lighter than `#080808` to read as a layer above)
- **Top corners:** 20dp radius
- **Drag handle:** 32×4px pill, `rgba(255,255,255,0.15)`
- **Segmented control:** `INV | ARC | DEG | RAD` — DM Mono 13px, active segment teal-tinted
- **4 rows of 4 fn keys:** same ghost panel style as `operator` keys
- **Divider** between row 2 and row 3: 1px `rgba(255,255,255,0.07)`
- **CONST chip** at bottom: 1px teal border, teal text, `science` icon, DM Mono 12px
- **Rows:**
  ```
  √   ^   x²  x³
  !   π   ln  log
  ─────────────────
  sin cos tan EXP
  sinh cosh tanh rand
  ```

---

## History screen

- **Background:** `#080808` — seamless with calculator
- **Entry:** slide in from right (calculator slides partially left)
- **Toolbar:** back arrow + "History" (Inter 500 17px) + search + delete-sweep icons, all `rgba(255,255,255,0.75)`
- **Filter chips:** `All | ★ Starred | 🏷 Labeled` — DM Mono 12px, active = `brandMuted` bg + `brand` text
- **Section headers:** date groups, Inter 700 11px uppercase, `rgba(255,255,255,0.25)`
- **Row:**
  - Expression: DM Mono 300 13px, `rgba(255,255,255,0.28)`
  - Result: Inter 300 34px, `primary` colour
  - Words: DM Mono italic 11px, `rgba(255,255,255,0.18)`
  - Star: `rgba(255,255,255,0.25)` unfilled / `#f59e0b` amber filled
  - Label chip: `brandMuted` bg, `brand` text, DM Mono 11px
  - Tap to expand → action row: delete / label / share / insert-to-calc

---

## Settings screen

Background `#080808`. Section headers: Inter 700 11px uppercase, `primary` colour. Rows: Inter 400 16px `onSurface`, value Inter 400 13px `onSurfaceVariant`. Dividers: 1px `rgba(255,255,255,0.06)`, indented 56dp.

```
GENERAL
  Language           value: current lang    →
  Number style       value: 1 2 3           →

CALCULATOR
  Default angle mode value: DEG             →
  Key sounds                               ⦾
  Key haptics                              ⦾

APPEARANCE
  Theme              seed circle + chevron  →

HISTORY
  Auto-clear         value: Never           →

CONSTANTS
  Manage constants                          →

HELP
  Help & FAQ                               →

ABOUT
  About 42 Calculator                      →
  Version            2.12.0
  Rate 42 Calculator                       →
  Share                                    →
  Contact                                  →

PREMIUM
  Restore purchase                         →
  What's included                          →
```

---

## Theme editor

- **Mini calc preview card:** 20dp radius, live-updates. Shows display (expression + result in VOID style) + 4-row keypad grid.
- **6 seed presets:** Teal `#1abc9c`, Blue `#2563eb`, Green `#16a34a`, Purple `#7c3aed`, Orange `#ea580c`, Pink `#db2777`
- **Appearance segmented:** Light / Dark / System
- **Dynamic color switch** (Android 12+ / iOS 18+ only)
- **Advanced (★ paid):** Key colour override — 6 swatches: `#222222 #86efac #bef264 #fde047 #6ee7b7 #1abc9c`

---

## Constants screen

**Pick mode:** list of enabled constants, DM Mono values, tap inserts to expression.
**Manage mode:** toggles + delete, "+ New" button.
Both modes: `#080808` background, Inter names, DM Mono values.

---

## Help & FAQ

- Search bar: pill, `rgba(255,255,255,0.07)` bg, `search` icon
- 10 expandable topics, Inter 500 15px questions, Inter 400 14px answers
- Active question: `primary` colour

---

## About screen

- Top half (`#080808`): tangram-bird SVG + "سپیدسا" + 4 social icons
- Bottom half (`brand` colour): two team cards — `rgba(255,255,255,0.10)` fill, `rgba(255,255,255,0.30)` border, 16dp radius
- Version: `v2.12.0`, DM Mono 12px, `rgba(255,255,255,0.35)`

### Tangram bird SVG
```svg
<polygon points="20,110 20,20 120,65"  fill="#1abc9c" />
<polygon points="60,20 120,65 60,65"   fill="#0e8a6e" />
<polygon points="20,110 50,125 35,90"  fill="#0e8a6e" />
<polygon points="120,65 150,50 135,80" fill="#1abc9c" />
<polygon points="120,65 145,72 130,82" fill="#0e8a6e" opacity="0.7" />
```
Viewbox `0 0 160 130`. Fills are theme-aware — swap with seed colour.

---

## Spacing system

| Token | Value |
|-------|-------|
| Key gap | 5dp |
| Key corner radius | 14dp |
| Keypad horizontal padding | 10dp |
| Bottom sheet corner radius | 20dp |
| Card corner radius | 16dp |
| Chip corner radius | 8dp |
| Settings row min-height | 56dp |
| Settings row padding | 16dp horizontal |
| Section header top padding | 20dp |
| Icon touch target | 44dp |

---

## Sample data

### History
```
{ expr: '566+369',  result: '935',  starred: false, label: null }
{ expr: '777',      result: '777',  starred: true,  label: 'my first number' }
{ expr: '1024÷2',   result: '512',  starred: false, label: null }
{ expr: '1000×8',   result: '8000', starred: true,  label: null }
```

### Constants (predefined)
```
ثابت آووگادرو      6.02214129E23    enabled: true
دمای سلسیوس       273.15           enabled: false
عدد اویلر          2.71828182846    enabled: false
نسبت طلایی         1.61803398875    enabled: true
عدد پی             3.14159265359    enabled: true
سرعت نور           2.99792458E8     enabled: true
شتاب استاندارد جاذبه 9.80665        enabled: true
ثابت بولتزمن       5.670373E-8      enabled: false
```
