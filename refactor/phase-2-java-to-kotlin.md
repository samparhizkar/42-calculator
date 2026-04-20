# Phase 2 — Java → Kotlin Conversion

## Status: ⏳ Not started

## Goal

Convert all 65 Java source files to Kotlin. This is a prerequisite for Phase 3
(architecture) and Phase 5 (Compose UI), which both assume Kotlin.

## Scope

All files under `app/src/main/java/com/sepidsa/fortytwocalculator/`:

### Root package (37 files)
- Activities: MainActivity, AboutActivity, HelpActivity, ColorPickerActivity,
  ParallaxPagerActivity, PremiumShowcasePagerActivity
- Fragments: DialpadFragment, ScientificFragment, AnimatedLogFragment,
  ConstantSelectFragment, ConstantUseFragment, CurrencyUseFragment, FavoritesFragment
- Adapters: LogAdapter, ConstantSelectAdapter, ConstantUseAdapter, FavoritesAdapter,
  HelpExpandableAdapter, ViewPagerAdapter
- Custom Views: AutoResizeTextView, ColorPickerPalette, ColorPickerSwatch,
  ColorStateDrawable, ParallaxPane, MyWidgetProvider
- Core logic: Expression, BigDecimalUtils  ← **convert language only, no logic changes**
- Number converters: NumberConveterAmerican, NumberConveterAmericanPartII,
  NumberConveterPersianPartI, NumberConverterPersianPartII, NumberConverterArabic,
  NumberConverterArabicPartII, NumberConverterFrench, NumberConverterFrenchPartII
- Utilities: Utils, ViewUtils, ExpandAnimation, CustomDialogClass

### data package (10 files)
- Contracts: LogContract, ConstantContract, CurrencyContract
- DbHelpers: LogDbHelper, ConstantDbHelper, CurrencyDbHelper
- Providers: LogProvider, ConstantProvider, CurrencyProvider
- Adapter: CurrencyUseAdapter

### sync package (4 files)
- CurrencySyncAdapter, CurrencySyncService, CurrencyAuthenticator,
  CurrencyAuthenticatorService

### util package (14 files)
- Billing: IabHelper, IabException, IabResult, Inventory, Purchase, SkuDetails
  ← these will be deleted in Phase 6; minimal conversion effort here
- Security: Base64, Base64DecoderException, Security
- UI: SystemUiHider, SystemUiHiderBase, SystemUiHiderHoneycomb

## Conversion rules

1. Use `val`/`var` appropriately; prefer `val`.
2. Use Kotlin null safety (`?`, `?.`, `?:`, `!!` only where truly non-null guaranteed).
3. Convert anonymous inner classes to lambdas where possible.
4. Convert static utility methods to top-level functions or companion objects.
5. Use `data class` for plain model/contract classes where appropriate.
6. Use string templates instead of concatenation.
7. Replace `TextUtils.isEmpty()` with `.isNullOrEmpty()`.
8. Keep `Expression` and `BigDecimalUtils` logic identical — only syntax changes.
9. The `util/` billing package needs only minimal conversion (it will be deleted in Phase 6).

## Build file changes needed

In `app/build.gradle.kts`, `kotlinOptions { jvmTarget = "11" }` is already present from
Phase 1. No further build changes needed for this phase.

## Verification

After conversion, the app should compile and run identically to before. No behavior
changes in this phase.

## Restart prompt (if conversation was cleared)

```
I'm modernizing an Android calculator app (42-calculator). We completed:
  Phase 1 — Build system (Kotlin DSL + version catalog) ✅
  Phase 2 — Java → Kotlin conversion ✅  (update this when done)

Phase 3 is to introduce MVVM architecture:
- Add ViewModel and StateFlow to replace direct UI state management in MainActivity.
- The 86KB MainActivity.java (now .kt) needs to be broken apart:
  * Calculator logic/state → CalculatorViewModel (uses Expression.kt)
  * History/log state → HistoryViewModel
  * Currency state → CurrencyViewModel
- Replace LocalBroadcastManager (deprecated) with StateFlow/SharedFlow.
- No UI changes yet (Compose comes in Phase 5).
- Add lifecycle-viewmodel-ktx and lifecycle-runtime-ktx to libs.versions.toml.

Refer to refactor/phase-3-architecture.md for full instructions.
```
