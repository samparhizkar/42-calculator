# Phase 5 — Settings, Drawer Retirement + Currency Removal

> **Design: VOID v2 (locked April 2026).** Android implementation. Full spec in **`DESIGN_SPEC.md`**. Values: background `#080808`; section headers Inter 700 11px uppercase `primary` colour 1.6 letter-spacing; rows Inter 400 16px / values 13px `onSurfaceVariant`; min-height 56dp; padding 16dp; dividers 1px `rgba(255,255,255,0.06)` indented 56dp. Language picker, Help, About specs in `DESIGN_SPEC.md §Settings screen` onwards.

Scope: replace the scattered quick-access icon row at the bottom AND the navigation drawer with a single proper settings screen. Redistribute all drawer content (Help, About, Contact, Premium) into Settings sub-screens. Also folds in **currency feature removal** — pure deletion, no own phase needed.

Depends on: Phase 4 (theme editor) — "Appearance" in settings links out to the Phase 4 theme editor screen.

---

## Current state (findings)

**There is no settings screen.** Controls are scattered across three places:

**Bottom toolbar (4 icons):**
| Icon | What it does |
|------|-------------|
| Gear ⚙ | Opens a language picker dialog (just 4 language buttons, nothing else) |
| Speaker 🔊 | Toggles key-click sound on/off inline |
| Palette 🎨 | Opens `ColorPickerActivity` (Phase 4) |
| More ⋯ | Opens the navigation drawer |

**Navigation drawer** (opened via hamburger/More, slides in from right in RTL):
| Item | Icon | What it does |
|------|------|-------------|
| راهنما (Help) | lightbulb | Opens `HelpActivity` — expandable list of 12 FAQ topics |
| امتیاز و نظر (Rate & Review) | thumbs-up | Opens Play Store rating |
| درباره (About) | people group | Opens `AboutActivity` — company logo + 2 team members with social links |
| پیام به ما (Contact) | email | Opens email compose |
| نسخه طلایی (Premium) | 42-ribbon | Opens `PremiumShowcasePagerActivity` — 5-slide onboarding carousel |

**The drawer is the wrong pattern here.** Navigation drawers are for switching between app destinations. Every item in this drawer is utility/support content — none of them navigate to a calculator feature. The drawer exists purely because there was nowhere else to put these items in 2015. The gear icon already competes with it for "settings-ish" things.

**Help content** (`arrays.xml` → `help_topics` + `help_sub_topics`): 12 topics covering how the calculator works, ★ starring, labeling, currency/gold prices *(being removed)*, previous calculations, email summary, constants, percentage, memory M, free vs paid differences, restore purchase, Android 4 vs 5 *(outdated — remove)*. Built with `BaseExpandableListAdapter` + custom Persian font (`yekan.ttf`).

**About screen**: tangram-bird geometric logo (made of triangles), "سپیدسا" company brand with web/email/Facebook/Instagram links, two circular team member avatars (Farshid + Ehsan) each with LinkedIn/email/Instagram. Visually distinctive — worth keeping as a proper screen.

**Premium onboarding**: `PremiumShowcasePagerActivity` — 5-slide parallax carousel on a cobalt-blue background. One confirmed slide: "تاریخچه پایا" (Permanent History) — history never gets wiped. Others cover paid features. Has "بی خیال" (Skip) and "›" (Next) navigation.

**Preferences scattered across multiple SharedPreferences buckets** — no single source of truth, no organization:

| Bucket | Key | What it controls |
|--------|-----|-----------------|
| `LanguagePreference` | `LANGUAGE` | int 0–3 (Persian / English / French / Arabic) |
| `volumeState` | `hasVolume` | boolean — key-click sound enabled |
| `THEME` | `is_retro_theme_selected` | retro theme on/off (being dropped) |
| `THEME` | (color keys) | accent + keypad color ints (replaced by Phase 4) |
| `angleMode` | `isDeg` | boolean — last-used DEG/RAD state |
| `typography` | (font keys) | number glyph style, tied to language |
| `APP` | `hasViewedTour` | first-run onboarding flag |
| `APP` | `hasViewedGoGoldNotif` | paid upsell notification flag |
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
- Key sounds (on/off) — currently a main-screen icon toggle; should live here
- Key haptics (on/off) — new in Phase 1; logically belongs here too

**Appearance**
- → Links to the Phase 4 theme editor (does not duplicate it)

**History**
- Auto-clear history: Never / After 30 days / After 90 days / After 1 year — simple addition that prevents unbounded growth (Phase 2 open question resolved here)

**Constants**
- → Links to the constants management screen (Phase 3)

**About** *(previously drawer → درباره)*
- App version (read-only)
- Open source licenses (standard AndroidX OSS Activity)
- Rate the app → Play Store link *(previously drawer → امتیاز و نظر)*
- Share the app
- Contact us / email *(previously drawer → پیام به ما)*
- → "About 42" full-screen sub-screen (team + brand — see below)

**Help & FAQ** *(previously drawer → راهنما)*
- Searchable expandable FAQ list, same 12 topics minus 2 removed ones
- → Full-screen `HelpScreen`

**Premium** *(previously drawer → نسخه طلایی)*
- Restore purchase button
- What's included → opens the premium showcase (updated from the 5-slide carousel)

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
│  Key sounds             ●────      │  MaterialSwitch
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
│  Rate 42 Calculator            >   │
│  Share                         >   │
├────────────────────────────────────┤
│  PREMIUM                           │
│  Restore purchase              >   │
│  What's included               >   │
└────────────────────────────────────┘
```

**Updated settings list (drawer items now integrated):**

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
│  Key sounds             ●────      │
│  Key haptics            ●────      │
├────────────────────────────────────┤
│  APPEARANCE                        │
│  Theme                         >   │
├────────────────────────────────────┤
│  HISTORY                           │
│  Auto-clear           Never  >     │
├────────────────────────────────────┤
│  CONSTANTS                         │
│  Manage constants              >   │
├────────────────────────────────────┤
│  HELP                              │  ← was drawer راهنما
│  Help & FAQ                    >   │
├────────────────────────────────────┤
│  ABOUT                             │
│  About 42 Calculator           >   │  ← was drawer درباره (team screen)
│  Version               2.12.0      │
│  Open source licenses          >   │
│  Rate 42 Calculator            >   │  ← was drawer امتیاز و نظر
│  Share                         >   │
│  Contact us                    >   │  ← was drawer پیام به ما
├────────────────────────────────────┤
│  PREMIUM                           │  ← was drawer نسخه طلایی
│  Restore purchase              >   │
│  What's included               >   │
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

**Proposed:** keep only 1 icon — **settings gear** — which opens the full settings screen. Sound and haptics are now switches in settings. Theme is a row in settings. The "more" menu goes away entirely.

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

### Step 5 — Help screen
1. New `HelpActivity` (or reuse + redesign existing one): `MaterialToolbar` + `SearchView` + `RecyclerView` with expandable sections.
2. Replace `BaseExpandableListAdapter` with a `RecyclerView` + `ConcatAdapter` (section headers + collapsible rows). M3 styling.
3. Prune two topics: "Currency/gold prices" and "Android 4 vs 5 differences."
4. Update remaining 10 topics to reflect new features (constants picker, history unified view, etc.).
5. Keep the custom Persian font (`yekan.ttf`) for body text — it's part of the app's personality.

### Step 6 — About sub-screen
1. New `AboutActivity`: keep the tangram-bird logo (it's distinctive; deserves to stay), "سپیدسا" brand, social links.
2. Team section below: two `MaterialCardView`s for Farshid and Ehsan with avatar, name, LinkedIn/email/Instagram icon buttons.
3. Replace hard-coded icon button row with proper M3 `IconButton`s.
4. Remove Facebook/Instagram links that may be stale — confirm with user which social links are still active.

### Step 7 — Premium showcase update
1. `PremiumShowcasePagerActivity` 5-slide carousel: update slide content to reflect the new feature set (history never wiped, key color customization, custom constants, etc.).
2. Replace cobalt-blue hardcoded background with `colorPrimary` so it inherits the M3 theme.
3. Update "بی خیال" (Skip) and Next buttons to M3 `TextButton` / `FilledButton`.

### Step 8 — Main screen bottom bar cleanup
1. Remove speaker, palette, more icons from the bottom toolbar.
2. Keep only the settings gear icon.
3. Update the gear click handler to open `SettingsActivity`.
4. Apply changes to `activity_main.xml`.

### Step 9 — Currency removal
1. Delete all currency files listed above.
2. Remove from `AndroidManifest.xml`.
3. Remove currency pane from `ViewPagerAdapter`.
4. Grep for `jsoup` usage; drop dependency if unused.
5. Audit pager index references.

### Step 10 — Preferences migration
1. On app launch, `AppPreferences.migrate()` reads legacy keys and writes to new ones.
2. After migration completes, old keys are cleared.
3. Version the migration (run only once, gated by a `migrated_v3` boolean).

---

## Open questions for the user

1. **Language switching mechanism** — the current approach manually restrings UI at runtime (non-standard). Should we migrate to the standard Android locale approach (app restart on language change) in this phase, or leave it as-is and just wrap it in the new settings UI? Standard approach is cleaner but risks regressions.
2. **Number style independence** — make "Number style" an independent setting (Persian speaker can choose Western numerals), or keep it locked to language? Independent is more flexible but adds permutation complexity to the number converters.
3. **Bottom toolbar** — keep just the gear, or keep gear + one other shortcut (e.g. theme palette since users change it more often)? I recommend gear only.
4. **jsoup dependency** — is jsoup used anywhere besides currency? If not, can drop it (reduces APK size). Worth checking during implementation.
5. **Premium showcase** — keep the 5-slide cobalt-blue carousel (just updated content + M3 styling), or replace with a simpler paywall screen? The carousel is nice but heavy; a single well-designed screen might convert better.
6. **About screen social links** — which of these are still active: website (sepidsa.com), email, Facebook, Instagram, LinkedIn? Worth confirming before rebuilding the About screen.
7. **`library-2.4.1.aar`** — the local MaterialDrawer library was [already replaced with standard NavigationView](commit 598245d) per git history. Confirm it can be fully removed once the drawer is retired.
8. **Team section in About** — keep Farshid + Ehsan with their photos/links, or simplify to just the company brand? Personal touch is nice for an indie app.

---

## Acceptance criteria

- Navigation drawer completely removed — no `DrawerLayout` in the activity.
- All 5 drawer items accessible from Settings (Help, Rate, About, Contact, Premium).
- All user-configurable settings reachable from a single settings screen.
- Language picker shows each language name in that language (self-identifying).
- Key sounds and haptics controllable from settings (not main screen icons).
- Help screen has 10 topics (2 removed: currency, Android 4 vs 5); search works.
- About sub-screen shows company brand + team; all social links verified active.
- Currency feature completely removed — no broken UI, no dead service.
- Main screen bottom bar simplified to 1 icon.
- All legacy SharedPreference keys migrated; existing user settings preserved.
- Settings screen + all sub-screens localize correctly in all 4 languages + RTL.
- No regression in language switching, angle mode, or theme application.
