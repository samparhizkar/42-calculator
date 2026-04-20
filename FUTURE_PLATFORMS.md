# 42 Calculator — Platform Strategy

Three separate native projects, built sequentially. No cross-platform framework. Each platform gets the full VOID design system (see `DESIGN_SPEC.md`) implemented with its own native toolchain.

**Build order:** Android → iOS → Web

---

## Why native, not cross-platform

React Native and Flutter both impose a middle layer that shows — especially in animations. The VOID design's signature is its animation system (per-digit mechanical counter, sonar ring, spring physics, breathing idle). These need to feel *native* on each platform:

- **Android:** `SpringAnimation` from DynamicAnimation, `ValueAnimator`, Jetpack Compose `animateAsState`
- **iOS:** SwiftUI `.interactiveSpring()`, `matchedGeometryEffect`, `withAnimation`
- **Web:** CSS `animation` + Web Animations API (already prototyped in `design-preview/void-v2.html`)

A shared framework would mean either compromising the animations or writing three platform-specific wrappers anyway — at which point you've lost the only benefit.

---

## Shared across all three

| What | How |
|------|-----|
| Design spec | `DESIGN_SPEC.md` — single source of truth for colours, typography, animation values, screen specs |
| Colour tokens | Defined once in DESIGN_SPEC.md, implemented natively per platform |
| Fonts | Inter (UI) + DM Mono (calculator) — both available via Google Fonts on Android/Web, bundled `.ttf` on iOS |
| Logic | Ported separately per platform (see §Calculation engine below) |
| Feature parity | All 5 phases applied to each platform (keypad, history, scientific, theme, settings) |

---

## Project 1 — Android (current)

**Status:** Active. Phases 1–5 planned in detail. See `PHASE_1` through `PHASE_5` docs.

**Stack:**
- Language: Java (existing codebase). Kotlin migration is optional — can modernise file-by-file during Phase work.
- UI: Views + XML layouts (existing). Jetpack Compose opt-in per-screen is fine for new screens (History, Settings).
- Animations: `SpringAnimation` (DynamicAnimation library) for digit springs; `ObjectAnimator` / `ValueAnimator` for sonar ring and bloom; `StateListAnimator` XML for key press scale.
- Theme: `Theme.Material3.DayNight.NoActionBar` base, custom `Theme.FortyTwo` overlay with VOID tokens.
- Fonts: `res/font/dm_mono_light.ttf`, `res/font/inter_regular.ttf` etc — bundled in the APK.
- Haptics: `VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)` on API 29+, `vibrate(20ms)` fallback.

**Key Android-specific UX:**
- Predictive back gesture (Android 14+) — animate calculator sliding right as history comes in
- Edge-to-edge display (`WindowCompat.setDecorFitsSystemWindows(false)`)
- Dynamic color (`DynamicColors.applyToActivitiesIfAvailable`) — opt-in in Settings
- Per-app language (Android 13+) — complements the existing 4-language support
- Material `BottomSheetBehavior` for scientific panel
- `RecyclerView` + `DiffUtil` for history list with smooth item animations

**Calculation engine:** `Expression.java` (existing, 744 lines). Keep as-is for Android. The only prerequisite before iOS/Web port is decoupling the 5 Android `Context` call sites (for operator symbol localisation) — defer this to Phase 5 or a dedicated cleanup step.

---

## Project 2 — iOS

**Start after:** Android Phase 5 ships.

**Stack:**
- Language: Swift 6
- UI: SwiftUI (new project — no legacy UIKit baggage)
- Animations: SwiftUI `.animation(.interactiveSpring(response: 0.38, dampingFraction: 0.72))` for digit springs; `withAnimation` + `scaleEffect` / `opacity` for key press; custom `TimelineView` or `Canvas` for sonar ring
- Fonts: `Info.plist` font registration for `DMMono-Light.ttf`, `Inter-Regular.ttf` etc
- Haptics: `UIImpactFeedbackGenerator(style: .light)` for key press; `.medium` for `=`
- Data: SwiftData (iOS 17+) for history and constants; `UserDefaults` for preferences
- Architecture: MVVM with `@Observable` (iOS 17+)

**iOS-specific UX to adopt:**
- **Safe areas:** respect Dynamic Island / home indicator — `safeAreaInset` on keypad bottom
- **Swipe navigation:** `NavigationStack` for history push; swipe-back is native and free
- **Scientific panel:** `.sheet(isPresented:)` with custom detents (`.fraction(0.72)`)
- **Settings:** `Form` + `List` in SwiftUI — styled VOID (dark background, DM Mono values)
- **Context menu:** long-press on history row → share / label / delete (replaces expanded action row)
- **Spotlight search:** `CSSearchableItem` for history entries — lets users find past calculations from Spotlight
- **Widgets:** Lock screen / home screen widget showing last result (nice differentiator for v2)
- **App Icon:** tangram bird on `#080808` — dark icon looks premium on any wallpaper

**Calculation engine port:**
- Port `Expression.java` → `Expression.swift` (straightforward translation; `BigDecimal` → Swift's `Decimal`)
- Port 4 number-to-words converters → Swift extensions on `Decimal`
- Unit-test both ports against the same input/output fixture table

**Project structure:**
```
42-ios/
  App/
    FortyTwoApp.swift
    ContentView.swift
  Features/
    Calculator/
      CalculatorView.swift
      CalculatorViewModel.swift
      KeypadView.swift
      DisplayView.swift
      DigitCell.swift          ← per-digit animation unit
    Scientific/
      ScientificSheet.swift
    History/
      HistoryView.swift
      HistoryViewModel.swift
    Constants/
      ConstantsView.swift
    Settings/
      SettingsView.swift
    Theme/
      ThemeEditorView.swift
  Core/
    Expression.swift
    NumberToWords/
      WordsEN.swift  WordsFR.swift  WordsFA.swift  WordsAR.swift
  Design/
    Tokens.swift               ← VOID colour tokens as Swift constants
    Fonts.swift
    Animations.swift           ← spring presets, durations
  Data/
    HistoryStore.swift         ← SwiftData model
    ConstantsStore.swift
    Preferences.swift
  Resources/
    Fonts/                     ← DM Mono, Inter
```

---

## Project 3 — Web

**Start after:** iOS ships (or in parallel if resource allows — web is largely independent).

**Stack:**
- Framework: React 19 + TypeScript + Vite
- Styling: CSS Modules (no Tailwind — the VOID design needs precise animation control that utility classes fight)
- Animations: CSS `@keyframes` + Web Animations API (WAAPI). The `design-preview/void-v2.html` prototype is essentially the production-ready animation layer — adapt it into components.
- Data: `localStorage` for preferences; `IndexedDB` (via `idb` package) for history and constants
- PWA: `manifest.json` + service worker for installability and offline use

**Web-specific UX to adopt:**
- **Keyboard input:** full keyboard support — number keys, operators, Enter (`=`), Backspace, Escape (`C`). Highlight the corresponding on-screen key briefly when a physical key is pressed.
- **Responsive layout:** VOID design works on both mobile browser (375px) and desktop (calculator floats centred on `#050505` background, max-width 380px)
- **Copy on click:** clicking the result number copies to clipboard + brief teal flash confirmation
- **URL state:** `?expr=566%2B369` — shareable pre-loaded expressions
- **No haptics** — skip silently
- **Tab focus ring:** override with VOID-styled teal `outline`, not system blue

**Calculation engine port:**
- Port `Expression.java` → `expression.ts` (TypeScript). Use `Decimal.js` for precision.
- Port 4 number-to-words converters → TypeScript functions.
- Consider publishing as `@42-calc/engine` — importable and independently testable.

**Project structure:**
```
42-web/
  src/
    features/
      calculator/
        CalculatorPage.tsx
        Keypad.tsx
        KeyButton.tsx
        Display.tsx
        DigitCell.tsx          ← per-digit animation (port from void-v2.html)
      scientific/
        ScientificDrawer.tsx
      history/
        HistoryPage.tsx
        HistoryRow.tsx
      constants/
        ConstantsPage.tsx
      settings/
        SettingsPage.tsx
      theme/
        ThemeEditor.tsx
    core/
      expression.ts
      numberToWords/
        en.ts  fr.ts  fa.ts  ar.ts
    design/
      tokens.ts                ← VOID colour tokens as TS constants
      animations.ts            ← keyframe definitions, spring presets
    data/
      historyStore.ts          ← IndexedDB via idb
      constantsStore.ts
      preferences.ts
    App.tsx
    main.tsx
  public/
    manifest.json
    icons/                     ← PWA icons (tangram bird)
  index.html
```

---

## Calculation engine — porting checklist

Before starting each new platform port, verify the engine handles:

- [ ] Basic arithmetic, parentheses, `%`
- [ ] Trig: `sin`, `cos`, `tan` + inverse variants
- [ ] `log`, `ln`, `√`, `^`, `x²`, `x³`, `!`
- [ ] `E` notation, `π`, memory operations
- [ ] Decoupled from Android `Context` (~5 call sites in `Expression.java`)
- [ ] Number-to-words: integers, large (millions/billions), edge cases (0, negative, fractions → empty)
- [ ] Precision: `1/3 × 3 = 1`, not `0.999...`
- [ ] Same input → same output verified across all platform ports

---

## Feature parity matrix

| Feature | Android | iOS | Web |
|---------|---------|-----|-----|
| VOID keypad + display | Phase 1 | iOS Ph 1 | Web Ph 1 |
| Per-digit animation | Phase 1 | iOS Ph 1 | Web Ph 1 ✓ (prototype) |
| Active operator glow | Phase 1 | iOS Ph 1 | Web Ph 1 ✓ |
| Sonar ring on = | Phase 1 | iOS Ph 1 | Web Ph 1 ✓ |
| Scientific panel | Phase 3 | iOS Ph 3 | Web Ph 3 |
| History unified | Phase 2 | iOS Ph 2 | Web Ph 2 |
| Constants | Phase 3 | iOS Ph 3 | Web Ph 3 |
| Theme editor | Phase 4 | iOS Ph 4 | Web Ph 4 |
| Settings | Phase 5 | iOS Ph 5 | Web Ph 5 |
| Number-to-words | Existing | iOS Ph 1 | Web Ph 1 |
| RTL (FA/AR) | Existing | iOS Ph 1 | Web Ph 1 |
| Dynamic colour | Phase 1 | iOS 18+ | — |
| Keyboard input | — | — | Web Ph 1 |
| PWA / offline | — | — | Web Ph 5 |
| Widgets | — | iOS Ph 6 | — |
| Spotlight search | — | iOS Ph 6 | — |
| URL sharing | — | — | Web Ph 2 |

---

## Session starter prompts

### Android — Phase 1
> Read `PROJECT_CONTEXT.md`, `DESIGN_SPEC.md`, and `PHASE_1_MATERIAL3_KEYPAD.md`. We are implementing Phase 1 of the Android 42 Calculator modernisation. The design is VOID v2 — dark-first (`#080808`), DM Mono throughout, per-digit mechanical-counter animation, active operator glow, = key as corner lamp. Start with Step 1 (theme + VOID colour tokens in `values/colors.xml` and `values-night/colors.xml`) and Step 1b (bundle DM Mono + Inter fonts into `res/font/`).

### iOS — project start
> Read `PROJECT_CONTEXT.md`, `DESIGN_SPEC.md`, and `FUTURE_PLATFORMS.md §Project 2 — iOS`. We are starting the 42 Calculator iOS native app from scratch using Swift 6 + SwiftUI. The Android app (Phases 1–5) is the reference implementation. Create the Xcode project using the structure in FUTURE_PLATFORMS.md, port the VOID colour tokens to `Tokens.swift`, bundle DM Mono + Inter fonts, and implement the keypad view with `DigitCell.swift` for the per-digit spring animation.

### Web — project start
> Read `PROJECT_CONTEXT.md`, `DESIGN_SPEC.md`, and `FUTURE_PLATFORMS.md §Project 3 — Web`. We are starting the 42 Calculator web app using React 19 + TypeScript + Vite. The animation prototype is at `design-preview/void-v2.html` — the `renderDigits()` function and all keyframe animations there are production-ready and should be adapted into `DigitCell.tsx`. Scaffold the project, create `design/tokens.ts`, and implement the Calculator page with a working interactive keypad.
