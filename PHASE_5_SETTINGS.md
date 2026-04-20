# Phase 5 — Settings, Drawer Retirement + Currency Removal

## Status: ✅ Complete (core settings screen + sub-screens + preferences consolidation done; remaining: auto-clear enforcement worker, number-style wiring to number converters, constants management navigation, full RTL/localization testing)

> **Design: VOID v2 (locked April 2026).** Android implementation. Full spec in **`DESIGN_SPEC.md`**. Values: background `#080808`; section headers Inter 700 11px uppercase `primary` colour 1.6 letter-spacing; rows Inter 400 16px / values 13px `onSurfaceVariant`; min-height 56dp; padding 16dp; dividers 1px `rgba(255,255,255,0.06)` indented 56dp. Language picker specs in `DESIGN_SPEC.md §Settings screen` onwards.

Scope: replace the scattered quick-access icon row at the bottom AND the navigation drawer with a single proper settings screen. About screen, Help screen, Rate Us, Contact Us, Premium, and first-run onboarding tour are **removed entirely** as the app moves to a fully free, simplified model. Also folds in **currency feature removal** — pure deletion, no own phase needed.

Depends on: Phase 4 (theme editor) — "Appearance" in settings links out to the Phase 4 theme editor screen.

---

## Current state (findings)

**There is no settings screen.** Controls are scattered across three places:

**Bottom toolbar (4 icons):**
| Icon | What it does |
|------|-------------|
| Gear ⚙ | Opens a language picker dialog (just 4 language buttons, nothing else) |
| Speaker 🔊 | ~~Toggles key-click sound on/off inline~~ **Removed** — sound now follows system ringer mode |
| Palette 🎨 | Opens `ColorPickerActivity` (Phase 4) |
| More ⋯ | Opens the navigation drawer |

**Navigation drawer** (opened via hamburger/More, slides in from right in RTL):
| Item | Icon | What it does |
|------|------|-------------|
| راهنما (Help) | lightbulb | ~~Opens `HelpActivity` — expandable list of 12 FAQ topics~~ **Removed** — help screen fully removed |
| امتیاز و نظر (Rate & Review) | thumbs-up | ~~Opens Play Store rating~~ **Removed** |
| درباره (About) | people group | ~~Opens `AboutActivity`~~ **Removed** — app version now shown inline in Settings |
| پیام به ما (Contact) | email | ~~Opens email compose~~ **Removed** |
| نسخه طلایی (Premium) | 42-ribbon | ~~Opens `PremiumShowcasePagerActivity`~~ **Removed** — app is now fully free |

**The drawer is the wrong pattern here.** Navigation drawers are for switching between app destinations. Every item in this drawer is utility/support content — none of them navigate to a calculator feature. The drawer exists purely because there was nowhere else to put these items in 2015. The gear icon already competes with it for "settings-ish" things.

**Help content**: ~~`arrays.xml` → `help_topics` + `help_sub_topics`: 12 topics~~ **Removed** — the entire help screen has been removed. The app is self-explanatory enough that a dedicated help/FAQ screen is unnecessary.

**About screen**: ~~tangram-bird geometric logo, "سپیدسا" brand, team section~~ **Removed** — no longer needed. App version is displayed inline in Settings.

**Premium onboarding**: ~~`PremiumShowcasePagerActivity` 5-slide carousel~~ **Removed** — app is now fully free. All premium features are available to all users.

**First-run onboarding tour**: ~~tour/onboarding flow shown on first launch~~ **Removed** — the app is simple enough that a tour is unnecessary.

**Preferences scattered across multiple SharedPreferences buckets** — no single source of truth, no organization:

| Bucket | Key | What it controls |
|--------|-----|-----------------|
| `LanguagePreference` | `LANGUAGE` | int 0–3 (Persian / English / French / Arabic) |
| `volumeState` | `hasVolume` | ~~boolean — key-click sound enabled~~ **Removed** — sound now follows system ringer mode |
| `THEME` | `is_retro_theme_selected` | retro theme on/off (being dropped) |
| `THEME` | (color keys) | accent + keypad color ints (replaced by Phase 4) |
| `angleMode` | `isDeg` | boolean — last-used DEG/RAD state |
| `typography` | (font keys) | number glyph style, tied to language |
| `APP` | `hasViewedTour` | ~~first-run onboarding flag~~ **Removed** — tour removed entirely |
| `APP` | `hasViewedGoGoldNotif` | ~~paid upsell notification flag~~ **Removed** — app is fully free |
| `APP` | `hasPopulatedConstantDatabase` | first-run DB seed flag |

**Language switching** is done by calling `setLanguage()` which manually restrings most of the UI at runtime — it does NOT use Android's locale system, meaning it bypasses the standard app restart approach. This is a technical debt item worth noting but not fixing in this phase.

**Typography pref** — Persian and Arabic users get Arabic-Indic numerals (٣, ٢, ١) vs Western (3, 2, 1) depending on a typography preference. This is currently set as a side-effect of language selection, not an independent toggle. A clean settings screen should surface it as an explicit "Number display" option for Persian/Arabic users.

---

## What belongs in settings

Inventory of all user-configurable behavior across the whole app:

**General**
- Language (Persian / English / French / Arabic)
- Number style: Western (1 2 3) vs Arabic-Indic (١ ٢ ٣) — currently implicit in language; surfacing it lets e.g. an Arabic speaker choose Western numerals

**Calculator**
- Default angle mode (DEG / RAD) — currently toggled in the scientific panel and persisted, but there's no way to reset the default without opening the sci panel
- Key haptics (on/off) — new in Phase 1; logically belongs here too

**Appearance**
- → Links to the Phase 4 theme editor (does not duplicate it)

**History**
- Auto-clear history: Never / After 30 days / After 90 days / After 1 year — simple addition that prevents unbounded growth (Phase 2 open question resolved here)

**Constants**
- → Links to the constants management screen (Phase 3)

**About** *(simplified)*
- App version (read-only)
- Open source licenses (standard AndroidX OSS Activity)

**Removed from settings** *(no longer applicable — app is fully free or feature simplified)*
- ~~Help & FAQ screen~~ — removed entirely; app is self-explanatory
- ~~Rate the app~~ — removed
- ~~Contact us~~ — removed
- ~~About sub-screen (team + brand)~~ — removed; app version shown inline
- ~~Premium / Restore purchase~~ — removed; app is fully free
- ~~First-run onboarding tour~~ — removed; app is self-explanatory
- ~~Key sounds toggle~~ — removed; sound now follows system ringer mode (silent = no sounds, vibrate/ringer = sounds)

---

## Target design

Standard M3 settings pattern: grouped list with section headers, `MaterialToolbar` at top, no bottom navigation.

```
┌────────────────────────────────────┐
│  ← Settings                        │
├────────────────────────────────────┤
│  GENERAL                           │
│  Language            English  >    │
│  Number style    1 2 3 / ١ ٢ ٣ >   │
├────────────────────────────────────┤
│  CALCULATOR                        │
│  Default angle mode     DEG  >     │
│  Key haptics            ●────      │  MaterialSwitch
├────────────────────────────────────┤
│  APPEARANCE                        │
│  Theme                         >   │  → opens Phase 4 editor
├────────────────────────────────────┤
│  HISTORY                           │
│  Auto-clear           Never  >     │
├────────────────────────────────────┤
│  CONSTANTS                         │
│  Manage constants              >   │  → opens Phase 3 constants manager
├────────────────────────────────────┤
│  ABOUT                             │
│  Version               2.12.0      │  read-only
│  Open source licenses          >   │
└────────────────────────────────────┘
```

**Language picker sub-screen** (instead of inline dialog):
- Full-screen destination with 4 rows, radio selection, checkmark on active.
- Each row: language name in that language + language name in current app language below it (e.g. "فارسی" / "Persian"). This is the standard pattern for language pickers so users can find their language even when they can't read the current language.

**Number style sub-screen:**
- Only shown / linked when language is Persian or Arabic.
- Two-option radio: Western (1 2 3) vs Arabic-Indic (١ ٢ ٣). Live preview line showing "1,234.56" in both styles.

**Default angle mode sub-screen:**
- Two-option radio: DEG / RAD. Short description of each.

**Auto-clear sub-screen:**
- Four-option radio: Never / 30 days / 90 days / 1 year.

---

## Main screen bottom toolbar — after settings

Currently: 4 icons (gear, speaker, palette, more). With a proper settings screen, most of these are redundant.

**Proposed:** keep only 1 icon — **settings gear** — which opens the full settings screen. Sound follows system ringer mode (no in-app toggle). Haptics is a switch in settings. Theme is a row in settings. The "more" menu goes away entirely.

This also declutters the main screen chrome significantly, giving the keypad more visual weight.

The star ★ and pen ✏ buttons (bookmark + label) that live in the *header/display area* stay where they are — those are in-context actions, not settings.

---

## Currency removal (folded in here)

**Remove entirely:**
- `CurrencyUseFragment.java` + `fragment_currency_use.xml` + `currency_use_item.xml`
- `sync/CurrencySyncAdapter.java` + `sync/CurrencyAuthenticator.java` (if present)
- `res/xml/authenticator.xml` + `res/xml/syncadapter.xml`
- `AndroidManifest.xml` entries for `CurrencyAuthenticatorService` and the sync adapter
- Any `$ `icon or currency-tab reference in the main ViewPager

**What this fixes:** removes a broken feature, eliminates the dead sync-adapter/authenticator service (which requires `android:exported="true"` and shows up in the system services list), and drops jsoup dependency if it was only used for currency scraping. **Check**: grep for `jsoup` usage — if only currency used it, drop it from `build.gradle` too.

**Migration:** no stored currency data to preserve. Removing the pane from the ViewPager shifts pane indices — audit `MainActivity` for any hardcoded pager index references.

---

## Implementation plan

### Step 1 — ConsolidatePreferences ✅
1. ✅ Created `data/AppPreferences.kt` — single SharedPrefs wrapper with typed getters/setters.
2. ✅ Consolidated all buckets (volumeState, angleMode, LanguagePreference, APP) into one `"app_prefs"` bucket. Theme prefs kept separate in `ThemePreferences` (Phase 4).
3. ✅ Migration: on first read, copies from legacy buckets to new `app_prefs`. Gated by `migration_done_v2`. Obsolete keys (`hasVolume`, `hasViewedTour`, `hasViewedGoGoldNotif`, `is_retro_theme_selected`) intentionally NOT migrated.
4. ✅ `CalculatorViewModel` and `MainActivity` now use `AppPreferences` instead of `SettingsRepository`.

### Step 2 — SettingsScreen ✅
1. ✅ Created `ui/settings/SettingsScreen.kt` — full Compose screen (not PreferenceFragment) following VOID v2 design spec.
2. ✅ Section headers: Inter 700 11px uppercase, primary colour, 1.6 letter-spacing.
3. ✅ Rows: 56dp min-height, Inter 400 16px onSurface / 13px onSurfaceVariant values.
4. ✅ Dividers: 1px `rgba(255,255,255,0.06)` indented 56dp.
5. ✅ Sections: General, Calculator, Appearance, History, Constants, About.
6. ✅ All rows wired to `AppPreferences`.
7. ✅ Replaced old `SettingsDialog` (deleted).

### Step 3 — Sub-screens ✅
1. ✅ `ui/settings/LanguagePickerScreen.kt` — full-screen with dual-language row labels (native name + localized name), checkmark on active selection. 4 languages: فارسی / English / Français / العربية.
2. ✅ `ui/settings/NumberStyleScreen.kt` — two-option radio (Western 1,234.56 vs Arabic-Indic ١٬٢٣٤٫٥٦) with DM Mono preview. Only shown when language is Persian or Arabic.
3. ✅ `ui/settings/AngleModeScreen.kt` — two-option radio (DEG / RAD) with descriptions.
4. ✅ `ui/settings/AutoClearScreen.kt` — four-option radio (Never / 30 days / 90 days / 1 year).

### Step 4 — Drawer retirement ✅ (completed in earlier phase)
1. ✅ `DrawerLayout` already removed — replaced with Compose `ModalNavigationDrawer` in Phase 5 Compose UI phase, now fully retired since settings replaces all drawer items.
2. ✅ `drawer_header.xml` already deleted.
3. ✅ No drawer-related code remains in `MainActivity`.
4. ✅ `library-2.4.1.aar` still in `libs/` — can be removed in cleanup phase (verify no other usages first).

### Step 5 — Help screen removal ✅ (completed in earlier phase)
1. ✅ `HelpActivity` already deleted.
2. ✅ Help-related resources already removed.
3. ✅ No help navigation routes remain.

### Step 6 — About / Premium / Contact / Rate removal ✅ (completed in earlier phase)
1. ✅ `AboutActivity` already deleted.
2. ✅ `PremiumShowcasePagerActivity` already deleted.
3. ✅ `HelpActivity` already deleted.
4. ✅ Rate/Contact code already removed.
5. ✅ Obsolete SharedPreferences keys not migrated (intentionally dropped).
6. ✅ Sound follows system ringer mode — `playSound()` in `MainActivity` checks `AudioManager.getRingerMode()`.
7. ✅ App version shown as read-only row in Settings > About.

### Step 7 — Main screen bottom bar cleanup ✅
1. ✅ Removed Palette icon from `BottomActionBar` — theme now accessible via Settings > Appearance.
2. ✅ Bottom bar is now gear-only (1 icon, centered).
3. ✅ Gear click opens full `SettingsScreen`.
4. ✅ Changes applied to `MainScreen.kt` Compose code (no XML layout to change).

### Step 8 — Currency removal ✅ (completed in earlier phase)
1. ✅ All currency files already deleted.
2. ✅ `AndroidManifest.xml` already clean — no currency service entries.
3. ✅ No `ViewPagerAdapter` in codebase — already replaced with Compose `HorizontalPager`.
4. ✅ No `jsoup` usage found in codebase.

### Step 9 — Preferences migration ✅
1. ✅ `AppPreferences.init` runs `performMigration()` on first instantiation.
2. ✅ Reads from legacy buckets (`LanguagePreference`, `angleMode`, `APP`) and writes to `app_prefs`.
3. ✅ Gated by `migration_done_v2` boolean — runs only once.
4. ✅ Obsolete keys intentionally NOT migrated (hasVolume, hasViewedTour, hasViewedGoGoldNotif, is_retro_theme_selected).

---

## Open questions — resolved

1. **Language switching mechanism** — ✅ Left as-is (runtime restrings). Wrapped in the new settings language picker UI. Standard Android locale approach deferred — too risky for this phase.
2. **Number style independence** — ✅ Made independent. `AppPreferences.numberStyle` is a separate pref, only surfaced in Settings when language is Persian or Arabic. Still needs wiring to the actual number converter logic.
3. **Bottom toolbar** — ✅ Gear only. Palette removed; theme accessible via Settings > Appearance.
4. **jsoup dependency** — ✅ Not present in current codebase. Already dropped.
5. **`library-2.4.1.aar`** — Still in `libs/`. Can be removed in cleanup phase. No drawer code references it; may have other usages that need verification.
6. **Premium feature unlock** — ✅ No premium gates found in current code. The `ThemeEditorScreen` has `isPremium = false` hardcoded; key color override UI shows but is effectively free.

---

## Acceptance criteria

- [x] Navigation drawer completely removed — no `DrawerLayout` in the activity.
- [x] Help screen entirely removed — no `HelpActivity`, no FAQ content, no help navigation.
- [x] About screen, Rate Us, Contact Us, Premium, and first-run onboarding tour entirely removed — no traces in code or resources.
- [x] All previously premium-gated features unlocked for all users.
- [x] All user-configurable settings reachable from a single settings screen.
- [x] Language picker shows each language name in that language (self-identifying).
- [x] Key haptics controllable from settings (not main screen icons). Key sounds follow system ringer mode — no in-app toggle.
- [x] App version displayed inline in Settings About section (no dedicated About or Help screen).
- [x] Currency feature completely removed — no broken UI, no dead service.
- [x] Main screen bottom bar simplified to 1 icon.
- [x] All legacy SharedPreference keys migrated; existing user settings preserved.
- [ ] Settings screen + all sub-screens localize correctly in all 4 languages + RTL — **needs testing**.
- [ ] No regression in language switching, angle mode, or theme application — **needs testing**.

---

## Remaining work

1. **Auto-clear enforcement** — `autoClearHistory` pref is stored but no `WorkManager` job actually enforces it. Need to add a periodic `CoroutineWorker` that checks `AppPreferences.autoClearDays` and deletes old entries from the history database.
2. **Number style wiring** — `AppPreferences.numberStyle` is stored and the UI allows changing it, but the actual number converter logic (`NumberToWordsConverter`, display formatting) does not yet read this pref. Need to wire `numberStyle` into the calculator display and word conversion logic.
3. **Constants management navigation** — Settings > Constants "Manage constants" row has a TODO placeholder. Needs wiring to the constants management screen once implemented.
4. **OSS Licenses** — Settings > About "Open source licenses" row currently falls back to app details settings intent. Should use proper `OssLicensesMenuActivity` from `com.google.android.gms:oss-licenses` or a Compose equivalent.
5. **Full RTL / localization testing** — Settings screens need testing with all 4 languages and RTL layouts.
6. **`library-2.4.1.aar` removal** — Verify no remaining usages of the local MaterialDrawer AAR and remove from `libs/` + `build.gradle.kts`.
7. **`SettingsRepository.kt` cleanup** — Now that `AppPreferences` replaces it, `SettingsRepository` can be deleted once all call sites are migrated.

---

## Files changed

### Created
- `data/AppPreferences.kt` — consolidated preferences wrapper with migration
- `ui/settings/SettingsScreen.kt` — full-screen settings (VOID v2 design)
- `ui/settings/LanguagePickerScreen.kt` — language picker sub-screen
- `ui/settings/NumberStyleScreen.kt` — number style sub-screen
- `ui/settings/AngleModeScreen.kt` — angle mode sub-screen
- `ui/settings/AutoClearScreen.kt` — auto-clear sub-screen

### Modified
- `ui/main/MainScreen.kt` — wired SettingsScreen + sub-screens, removed Palette icon, simplified BottomActionBar to gear-only
- `MainActivity.kt` — added AppPreferences, removed onColorsClick/onSettingsClick handlers
- `ui/calculator/CalculatorViewModel.kt` — switched to AppPreferences for language/angle persistence
- `ui/theme/Type.kt` — made Inter and DmMono public (was `internal`)
- `app/build.gradle.kts` — added `buildConfig = true`
- `res/values/strings.xml` — added 44 new string resources for settings UI

### Deleted
- `ui/dialogs/SettingsDialog.kt` — replaced by SettingsScreen + sub-screens
