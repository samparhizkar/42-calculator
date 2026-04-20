# Phase 5 — Jetpack Compose + Material Design 3

## Status: ⏳ Not started (requires Phase 3 complete, Phase 4 recommended)

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

## Restart prompt (if conversation was cleared)

```
I'm modernizing an Android calculator app (42-calculator). Completed so far:
  Phase 1 — Build system ✅
  Phase 2 — Java → Kotlin ✅
  Phase 3 — Architecture (MVVM) ✅
  Phase 4 — Data layer (Room + WorkManager) ✅
  Phase 5 — Compose UI + Material 3 ✅  (update when done)

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
