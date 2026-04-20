# Phase 3 — Architecture (MVVM + ViewModel + StateFlow)

## Status: ⏳ Not started (requires Phase 2 complete)

## Goal

Decompose the monolithic `MainActivity` (~86KB) into a proper MVVM structure with
ViewModels holding all UI state. No UI framework changes in this phase — still XML layouts,
still Fragments. This phase is about separating concerns.

## New structure to create

```
com.sepidsa.fortytwocalculator/
  ui/
    calculator/
      CalculatorFragment.kt      ← renamed from DialpadFragment
      CalculatorViewModel.kt     ← owns expression state, button events
    scientific/
      ScientificFragment.kt
      ScientificViewModel.kt     ← can share CalculatorViewModel if scoped to activity
    history/
      HistoryFragment.kt         ← renamed from AnimatedLogFragment
      HistoryViewModel.kt        ← owns log/history list state
    currency/
      CurrencyFragment.kt        ← renamed from CurrencyUseFragment
      CurrencyViewModel.kt       ← owns currency rates, sync state
    favorites/
      FavoritesFragment.kt
      FavoritesViewModel.kt
    constants/
      ConstantSelectFragment.kt
      ConstantViewModel.kt
  MainActivity.kt                ← thin shell: navigation + drawer only
```

## Key changes

### CalculatorViewModel
- Holds: current expression string, result string, error state
- Exposes: `StateFlow<CalculatorUiState>`
- Input: button press events (digits, operators, equals, clear, backspace)
- Delegates math to `Expression.kt`

### HistoryViewModel
- Holds: list of past calculations
- Exposes: `StateFlow<List<LogEntry>>`
- Reads from Room (Phase 4) or SQLite (until then)

### CurrencyViewModel
- Holds: exchange rates, selected currencies, conversion result
- Exposes: `StateFlow<CurrencyUiState>`
- Triggers sync via WorkManager (Phase 4)

### Replace LocalBroadcastManager
- Remove `LocalBroadcastManager` usage throughout
- Use `SharedFlow` for one-shot events (errors, navigation triggers)
- Use `StateFlow` for persistent UI state

## Dependencies to add to libs.versions.toml

```toml
[versions]
lifecycle = "2.8.7"

[libraries]
androidx-lifecycle-viewmodel-ktx = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-ktx", version.ref = "lifecycle" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycle" }
androidx-lifecycle-livedata-ktx = { group = "androidx.lifecycle", name = "lifecycle-livedata-ktx", version.ref = "lifecycle" }
```

## Verification

After this phase: MainActivity is a thin shell (<200 lines), each Fragment observes its
own ViewModel via `viewLifecycleOwner.lifecycleScope.launch { ... collectLatest { } }`.
App behavior is identical.

## Restart prompt (if conversation was cleared)

```
I'm modernizing an Android calculator app (42-calculator). Completed so far:
  Phase 1 — Build system ✅
  Phase 2 — Java → Kotlin ✅
  Phase 3 — Architecture (MVVM) ✅  (update when done)

Phase 4 is to modernize the data layer:
- Replace raw SQLite + ContentProvider (LogDbHelper, ConstantDbHelper, CurrencyDbHelper
  and their corresponding providers) with Room.
- Replace SyncAdapter (CurrencySyncAdapter + CurrencyAuthenticatorService) with a
  WorkManager CoroutineWorker that fetches exchange rates from a JSON API.
- Remove jsoup-based HTML scraping for currency; use a proper REST endpoint instead
  (e.g. https://open.er-api.com or similar free exchange rate API — confirm with user).
- Keep the 3 ContentProviders' data contracts (same table structure) for migration safety,
  but replace the implementation with Room DAOs.

Refer to refactor/phase-4-data-layer.md for full instructions.
```
