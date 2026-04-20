# 42 Calculator — Modernization Refactor

This folder tracks each refactor phase. If you start a fresh conversation, paste the
prompt from the relevant phase file to resume exactly where you left off.

## Phase Overview

| # | Phase | Status | File |
|---|-------|--------|------|
| 1 | Build system (Kotlin DSL + version catalog) | ✅ Done | [phase-1-build-system.md](phase-1-build-system.md) |
| 1b | Feature removal (retro theme, billing, currency) | ✅ Done | _(this session)_ |
| 2 | Java → Kotlin conversion | ⏳ Next | [phase-2-java-to-kotlin.md](phase-2-java-to-kotlin.md) |
| 3 | Architecture (MVVM + ViewModel + StateFlow) | ⏳ Pending | [phase-3-architecture.md](phase-3-architecture.md) |
| 4 | Data layer (Room + WorkManager) | ⏳ Pending | [phase-4-data-layer.md](phase-4-data-layer.md) |
| 5 | UI (Jetpack Compose + Material 3) | ⏳ Pending | [phase-5-compose-ui.md](phase-5-compose-ui.md) |
| 6 | Billing (Play Billing Library 7.x) | ⏳ Pending | [phase-6-billing.md](phase-6-billing.md) |
| 7 | Cleanup (permissions, dead resources, PNGs → vectors) | ⏳ Pending | [phase-7-cleanup.md](phase-7-cleanup.md) |

## Key Decisions (agreed with user)

- Java → Kotlin conversion is approved for all source files.
- Keep the `Expression.java` calculator logic and number localization converters — they are
  the app's core differentiators; just convert them to Kotlin.
- minSdk stays at 21 for now; can raise to 24 in Phase 7 cleanup after verifying reach.
- The app targets Iranian/Persian users as a primary audience (strings, RTL).
- **Retro theme removed** — flat theme only going forward.
- **Billing removed** — app is now free; all premium gates removed; constants are freely editable.
- **Currency conversion removed** — entire feature deleted (sync adapter, data layer, fragment).
- jsoup dependency removed (was used only for currency scraping).
- `AIDL` build feature removed (was used only for billing).
