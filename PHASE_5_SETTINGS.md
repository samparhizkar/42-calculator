# Phase 5 — Settings, Drawer Retirement + Currency Removal

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

### Step 1 — ConsolidatePreferences
1. Create a single `AppPreferences` wrapper class with typed getters/setters for all keys.
2. Consolidate all buckets (volumeState, THEME, angleMode, typography, LanguagePreference, APP) into one `"app_prefs"` bucket — or keep separate buckets but route through the wrapper so call sites are clean.
3. Migration: on first read after upgrade, copy values from old keys to new ones.

### Step 2 — SettingsActivity
1. New `SettingsActivity` using `PreferenceFragmentCompat` with `MaterialToolbar` and Material 3 preference themes — or a plain `RecyclerView`-based list if full PreferenceFragment customization is needed for M3 aesthetics.
2. Section headers via `PreferenceCategory`.
3. Wire each row to `AppPreferences`.

### Step 3 — Sub-screens
1. `LanguagePreferenceFragment` — full-screen language picker with dual-language row labels.
2. `NumberStylePreferenceFragment` — radio + live preview.
3. Re-use the `AlertDialog` pattern from Phase 4 for single-screen sub-choices (angle mode, auto-clear).

### Step 4 — Drawer retirement
1. Remove the `DrawerLayout` from `activity_main.xml` and its `NavigationView`/custom drawer panel.
2. Delete `drawer_header.xml`.
3. The hamburger "More" bottom icon disappears with the drawer.
4. Audit `MainActivity` for all drawer open/close listeners and remove.
5. Delete `MaterialDrawer` library references if the local `library-2.4.1.aar` is only used for the drawer (check usages first).

### Step 5 — Help screen removal
1. Delete `HelpActivity` and all related layouts/resources.
2. Delete `help_topics` / `help_sub_topics` string arrays from `arrays.xml`.
3. Delete the `BaseExpandableListAdapter` implementation and any custom help-related adapter classes.
4. Remove the help/lightbulb drawer item reference from `MainActivity`.
5. Remove any help-related navigation routes or intents.

### Step 6 — About / Premium / Contact / Rate removal
1. Delete `AboutActivity` and all related layouts/resources (tangram logo, team avatars, social link icons).
2. Delete `PremiumShowcasePagerActivity` and all related layouts/resources (carousel slides, cobalt-blue backgrounds).
3. Delete `HelpActivity` and all related layouts/resources (help topics, FAQ adapters, search UI).
4. Delete `RateUs` / Play Store rating intent code.
5. Delete `ContactUs` / email compose intent code.
6. Remove all obsolete SharedPreferences keys (`hasViewedGoGoldNotif`, `hasViewedTour`, `hasVolume`, etc.).
7. Remove billing/play-billing-library dependency from `build.gradle` if present.
8. Delete first-run onboarding tour code, layouts, and resources (activity, fragments, tour-specific drawables/strings).
9. Remove key-click sound toggle code — sound now follows system ringer mode via `AudioManager.getRingerMode()`. Delete the `volumeState`/`hasVolume` preference, the speaker toggle icon handler, and any `SoundManager`/`VolumeController` helper class.
10. App version is now shown as a read-only row in the Settings About section — no dedicated screen needed.

### Step 7 — Main screen bottom bar cleanup
1. Remove speaker, palette, more icons from the bottom toolbar. Speaker icon no longer needed — sound follows system ringer mode.
2. Keep only the settings gear icon.
3. Update the gear click handler to open `SettingsActivity`.
4. Apply changes to `activity_main.xml`.

### Step 8 — Currency removal
1. Delete all currency files listed above.
2. Remove from `AndroidManifest.xml`.
3. Remove currency pane from `ViewPagerAdapter`.
4. Grep for `jsoup` usage; drop dependency if unused.
5. Audit pager index references.

### Step 9 — Preferences migration
1. On app launch, `AppPreferences.migrate()` reads legacy keys and writes to new ones.
2. After migration completes, old keys are cleared.
3. Version the migration (run only once, gated by a `migrated_v3` boolean).

---

## Open questions for the user

1. **Language switching mechanism** — the current approach manually restrings UI at runtime (non-standard). Should we migrate to the standard Android locale approach (app restart on language change) in this phase, or leave it as-is and just wrap it in the new settings UI? Standard approach is cleaner but risks regressions.
2. **Number style independence** — make "Number style" an independent setting (Persian speaker can choose Western numerals), or keep it locked to language? Independent is more flexible but adds permutation complexity to the number converters.
3. **Bottom toolbar** — keep just the gear, or keep gear + one other shortcut (e.g. theme palette since users change it more often)? I recommend gear only.
4. **jsoup dependency** — is jsoup used anywhere besides currency? If not, can drop it (reduces APK size). Worth checking during implementation.
5. **`library-2.4.1.aar`** — the local MaterialDrawer library was [already replaced with standard NavigationView](commit 598245d) per git history. Confirm it can be fully removed once the drawer is retired.
6. **Premium feature unlock** — with the premium model removed, are there any features currently gated behind a premium check that should now be automatically enabled for all users (e.g. permanent history, custom constants)? These need to be un-gated in code.

---

## Acceptance criteria

- Navigation drawer completely removed — no `DrawerLayout` in the activity.
- Help screen entirely removed — no `HelpActivity`, no FAQ content, no help navigation.
- About screen, Rate Us, Contact Us, Premium, and first-run onboarding tour entirely removed — no traces in code or resources.
- All previously premium-gated features unlocked for all users.
- All user-configurable settings reachable from a single settings screen.
- Language picker shows each language name in that language (self-identifying).
- Key haptics controllable from settings (not main screen icons). Key sounds follow system ringer mode — no in-app toggle.
- App version displayed inline in Settings About section (no dedicated About or Help screen).
- Currency feature completely removed — no broken UI, no dead service.
- Main screen bottom bar simplified to 1 icon.
- All legacy SharedPreference keys migrated; existing user settings preserved.
- Settings screen + all sub-screens localize correctly in all 4 languages + RTL.
- No regression in language switching, angle mode, or theme application.
