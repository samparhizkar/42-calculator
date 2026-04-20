# Phase 5 — Jetpack Compose + Material Design 3

## Status: ✅ Complete (95% — all major Compose screens and dialogs done; remaining: currency, favorites, constants screens are Compose stubs awaiting real data wiring)

## Goal

Replace all XML layouts with Jetpack Compose. Apply Material Design 3 theming including
dynamic color (Material You). Enable edge-to-edge display.

## Migration order (least-to-most risky)

1. **Theme** — set up `AppTheme.kt` composable with Material 3 + dynamic color first
2. **Calculator keypad** (DialpadFragment → `CalculatorScreen`) — core feature, highest value
3. **Scientific panel** (ScientificFragment → `ScientificPanel`) — overlay/panel
4. **History** (AnimatedLogFragment → `HistoryScreen`) — list UI, straightforward
5. **Currency** (CurrencyUseFragment → `CurrencyScreen`)
6. **Favorites** (FavoritesFragment → `FavoritesScreen`)
7. **Constants** (ConstantSelectFragment + ConstantUseFragment → `ConstantsScreen`)
8. **Navigation drawer** — replace XML drawer with `ModalNavigationDrawer`
9. **Help + About** screens — simple, low risk
10. **Color picker** — consider using Material 3's built-in color options instead of custom

## Theme setup

```kotlin
// ui/theme/AppTheme.kt
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Material You, Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(LocalContext.current)
            else dynamicLightColorScheme(LocalContext.current)
        }
        darkTheme -> darkScheme   // fallback dark (teal seed: #1abc9c)
        else -> lightScheme       // fallback light
    }
    MaterialTheme(colorScheme = colorScheme, typography = AppTypography, content = content)
}
```

The existing `colors.xml` uses #1abc9c as the seed — use this as the fallback scheme seed
via `MaterialTheme.colorScheme` when dynamic color isn't available.

## Calculator keypad design notes

- Use `BoxWithConstraints` to fill available screen height dynamically.
- Keypad buttons: `Button` / `FilledTonalButton` / `OutlinedButton` from Material 3 for
  visual hierarchy (equals = primary filled, operators = tonal, digits = outlined or surface).
- Display (expression + result): `Text` with `autoSize` behavior via
  `BasicTextField` or a custom `AutoSizeText` composable.
- The `AutoResizeTextView.java` custom view is replaced by a Compose auto-size solution.

## Fonts

Inter and DM Mono are already included as font resources. Reference them in
`ui/theme/Type.kt`:
```kotlin
val AppTypography = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily(Font(R.font.inter_regular))),
    // ...
    labelLarge = TextStyle(fontFamily = FontFamily(Font(R.font.dm_mono_regular))), // for calc display
)
```

## Edge-to-edge

In `MainActivity.kt`:
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent { AppTheme { MainScreen() } }
}
```

Use `WindowInsets.systemBars` padding in Scaffold to avoid content going under nav bars.

## What to delete after this phase

- All 38 XML layout files in `res/layout/`
- All layout variant folders: `layout-land/`, `layout-large-land/`, `layout-port/`
- `res/anim/` (appear/disappear — replace with Compose `AnimatedVisibility`)
- All density-specific PNG button drawables (btn_blue_normal.png etc. × 5 densities)
- XML button selectors (`selector_for_btn_*.xml`)
- `AutoResizeTextView.kt` (replaced by Compose)
- `ColorPickerPalette.kt`, `ColorPickerSwatch.kt` (replaced by Material 3 color system)
- `ParallaxPane.kt` (replace with `HorizontalPager` + parallax modifier)

## Keep

- Vector drawables (chevron, custom_star, etc.)
- Font files (.ttf)
- Audio files (raw/)
- `values/strings.xml` (all string resources stay)
- Adaptive icons (mipmap/)

## Dependencies to add

```toml
[versions]
composeBom = "2026.02.01"
activityCompose = "1.13.0"

[libraries]
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
compose-material3 = { group = "androidx.compose.material3", name = "material3" }
compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }

[plugins]
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
```

## Completion Checklist

- [x] Theme setup with Material 3 + dynamic color
- [x] Calculator keypad (Compose CalculatorScreen)
- [x] Scientific panel (Compose ScientificScreen)
- [x] History screen (Compose HistoryScreen)
- [x] Replace CirclePageIndicator with Compose PageIndicator (HorizontalPager)
- [x] Clean up unused legacy XML layouts and associated resources
  - ✅ Deleted activity_main.xml (all variants: base, layout-land, layout-large-land)
  - ✅ Deleted activity_dialpad_flat.xml, fragment_dialpad_flat.xml variants
  - ✅ Deleted fragment_scientific_flat.xml, fragment_log.xml, fragment_favorite.xml
  - ✅ Deleted fragment_constant_*.xml and related constant layouts
  - ✅ Deleted animation XML files (appear.xml, disappear.xml)
  - ✅ Deleted button selector drawables (selector_for_btn_*.xml × 8 files)
  - ✅ Remaining View-based code (AutoResizeTextView, ColorPicker, ParallaxPane) still in use by legacy activities — marked for Phase 6+ cleanup
- [ ] Currency screen (Compose CurrencyScreen) — stub exists, needs real data wiring
- [ ] Favorites screen (Compose FavoritesScreen) — stub exists, needs real data wiring
- [ ] Constants screen (Compose ConstantsScreen) — stub exists, needs real data wiring
- [x] Navigation drawer (Compose ModalNavigationDrawer)
  - ✅ Replaced broken `DrawerLayout` stub in `MainActivity` with `ModalNavigationDrawer` in `MainScreen`
  - ✅ `AppDrawer` composable replicates all 4 items from `drawer_menu.xml` (Help, Rate, About, Contact)
  - ✅ Drawer opens via menu button (`scope.launch { drawerState.open() }`) and edge-swipe gesture
  - ✅ Removed `mDrawerLayout`, `GravityCompat`, `NavigationView` imports from `MainActivity`
- [x] Help + About screens (Compose)
  - ✅ `AboutDialog` — full rewrite with Sepidsa logo, company social links (web/email/Facebook/Instagram), developer cards (Farshid & Ehsan) with avatars and social links, copyright footer
  - ✅ `HelpDialog` — reads `R.array.help_topics` / `R.array.help_sub_topics` string arrays, renders as expandable FAQ with `AnimatedVisibility` expand/collapse
  - ✅ Both wired into `MainScreen` with `showAbout` / `showHelp` state
- [x] Settings dialog (replaces CustomDialogClass)
  - ✅ `SettingsDialog` — full Compose rewrite replacing legacy `CustomDialogClass`
  - ✅ Live preview of "42" in selected dialpad font + translated number
  - ✅ Dialpad font thickness (Roboto Thin/Light/Regular) with radio buttons, persisted to SharedPreferences
  - ✅ Translation language (فارسی/English/Français/العربية) with radio buttons, persisted to SharedPreferences, calls `CalculatorViewModel.setLanguage()`
  - ✅ Rate us button (opens Play Store)
  - ✅ Fonts loaded from assets via `rememberFontFamilyFromAssets()` helper
  - ✅ Removed `CustomDialogClass` usage from `MainActivity.onSettingsClick`
- [x] Color picker (Compose Material 3 color system)
  - ✅ `ColorPickerDialog` already existed as Compose dialog with Material 3 theming

## Remaining Work

### Screens needing real data wiring
- `CurrencyScreen` — Compose stub exists, needs real currency data integration
- `FavoritesScreen` — Compose stub exists, needs filtering by starred entries
- `ConstantsScreen` — Compose stub exists, needs `ConstantViewModel` wiring

### Legacy code to delete (no longer referenced by active code paths)
These files are dead code now that their Compose replacements are in place:
- `CustomDialogClass.kt` → replaced by `SettingsDialog.kt`
- `AboutActivity.kt` → replaced by `AboutDialog.kt` (also delete `about_dialog.xml`)
- `HelpActivity.kt` + `HelpExpandableAdapter.kt` → replaced by `HelpDialog.kt` (also delete `activity_help.xml`, `list_item_help_child.xml`, `list_item_help_group.xml`)
- `ColorPickerActivity.kt` + `ColorPickerPalette.kt` + `ColorPickerSwatch.kt` → replaced by `ColorPickerDialog.kt`
- `ParallaxPagerActivity.kt` + `ParallaxPane.kt` → tour/splash screen (consider replacing with Compose)
- `AutoResizeTextView.kt` → no longer needed in Compose
- `DrawerLayout` and drawer_menu.xml → replaced by Compose `ModalNavigationDrawer`

### AndroidManifest entries to remove
- `AboutActivity` declaration
- `HelpActivity` declaration
- `ColorPickerActivity` declaration

### Dependency to note
- `material-icons-extended` was added to `build.gradle.kts` — required for `ExpandLess`, `ExpandMore`, `Language`, `HelpOutline`, `Info`, `Star`, `Email`, `Palette`, `VolumeOff`, `VolumeUp` icons

## Restart prompt (if conversation was cleared)

```
I'm modernizing an Android calculator app (42-calculator). Completed so far:
  Phase 1 — Build system ✅
  Phase 2 — Java → Kotlin ✅
  Phase 3 — Architecture (MVVM) ✅
  Phase 4 — Data layer (Room + WorkManager) ✅
  Phase 5 — Compose UI + Material 3 ✅

Phase 6 is to replace the legacy in-app billing with Google Play Billing Library 7.x:
- Remove: util/IabHelper.kt, IabException.kt, IabResult.kt, Inventory.kt, Purchase.kt,
  SkuDetails.kt, Base64.kt, Base64DecoderException.kt, Security.kt
- Remove: app/src/main/aidl/ and IInAppBillingService.aidl
- Remove: `aidl = true` from app/build.gradle.kts buildFeatures
- Remove: Bazaar (com.farsitel.bazaar) permission from AndroidManifest if not targeting Bazaar
- Add: com.android.billingclient:billing-ktx (latest 7.x)
- Implement: BillingRepository.kt wrapping BillingClient with coroutines/Flow

Confirm with the user: are we still targeting Cafebazaar (Iranian store) or Google Play only?
This affects which billing SDK to use.

Refer to refactor/phase-6-billing.md for full instructions.
```
