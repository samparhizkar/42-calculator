# Android Project Modernization Summary

Changes made to bring the 42 Calculator project up to date with current Android Studio (2023.2+).

## Build Infrastructure

| File | Change |
|------|--------|
| `build.gradle` | AGP `2.1.0` → `8.3.2`; repos `jcenter()` → `google()` + `mavenCentral()` (jcenter kept as fallback for old `iconics:1.0.2`) |
| `gradle/wrapper/gradle-wrapper.properties` | Gradle `2.10` → `8.4` |
| `gradle.properties` | New file — enables `android.useAndroidX=true` and `android.enableJetifier=true` |
| `app/build.gradle` | Removed duplicate `buildscript` block; added `namespace`; `compileSdk`/`targetSdk` → 34; `minSdk` → 21; Java 1.7 → 1.8; `compile` → `implementation`; replaced all support lib deps with AndroidX; jsoup `1.8.2` → `1.17.2` |
| `library/build.gradle` | Added `namespace`; `compileSdk` → 34; Java 1.8; `compile` → `implementation`; `lintOptions {}` → `lint {}`; support lib dep → AndroidX |

## AndroidManifest.xml

- Removed deprecated `<uses-sdk>` element (SDK versions are now managed in `build.gradle`)
- Removed `android:versionCode` and `android:versionName` from `<manifest>` (now in `build.gradle` `defaultConfig`)
- Added `android:exported="true"` to `MainActivity` (required for components with intent-filters since API 31)
- Added `android:exported="true"` to `CurrencyAuthenticatorService` (required for API 31+)

## AndroidX Migration

All `android.support.*` references replaced with `androidx.*` equivalents.

### Java Sources (18 files)

| Old import | New import |
|-----------|-----------|
| `android.support.annotation.*` | `androidx.annotation.*` |
| `android.support.v4.app.Fragment` | `androidx.fragment.app.Fragment` |
| `android.support.v4.app.FragmentActivity` | `androidx.fragment.app.FragmentActivity` |
| `android.support.v4.app.FragmentManager` | `androidx.fragment.app.FragmentManager` |
| `android.support.v4.app.FragmentStatePagerAdapter` | `androidx.fragment.app.FragmentStatePagerAdapter` |
| `android.support.v4.app.DialogFragment` | `androidx.fragment.app.DialogFragment` |
| `android.support.v4.app.LoaderManager` | `androidx.loader.app.LoaderManager` |
| `android.support.v4.app.NotificationCompat` | `androidx.core.app.NotificationCompat` |
| `android.support.v4.content.CursorLoader` | `androidx.loader.content.CursorLoader` |
| `android.support.v4.content.Loader` | `androidx.loader.content.Loader` |
| `android.support.v4.content.LocalBroadcastManager` | `androidx.localbroadcastmanager.content.LocalBroadcastManager` |
| `android.support.v4.view.ViewPager` | `androidx.viewpager.widget.ViewPager` |
| `android.support.v4.view.PagerAdapter` | `androidx.viewpager.widget.PagerAdapter` |
| `android.support.v4.view.ViewCompat` | `androidx.core.view.ViewCompat` |
| `android.support.v4.widget.DrawerLayout` | `androidx.drawerlayout.widget.DrawerLayout` |
| `android.support.v7.app.ActionBarDrawerToggle` | `androidx.appcompat.app.ActionBarDrawerToggle` |
| `android.support.v7.app.AlertDialog` | `androidx.appcompat.app.AlertDialog` |
| `android.support.v7.widget.SwitchCompat` | `androidx.appcompat.widget.SwitchCompat` |
| `android.support.v7.widget.Toolbar` | `androidx.appcompat.widget.Toolbar` |

### XML Layouts (7 files)

| Old class | New class |
|-----------|-----------|
| `android.support.v4.view.ViewPager` | `androidx.viewpager.widget.ViewPager` |
| `android.support.v4.widget.DrawerLayout` | `androidx.drawerlayout.widget.DrawerLayout` |
| `android.support.v7.widget.SwitchCompat` | `androidx.appcompat.widget.SwitchCompat` |

## Dependency Updates

| Dependency | Old | New |
|-----------|-----|-----|
| Android Gradle Plugin | `2.1.0` | `8.3.2` |
| Gradle wrapper | `2.10` | `8.4` |
| `com.android.support:support-v4` | `23.1.0` | replaced by `androidx.core:core:1.13.1` |
| `com.android.support:appcompat-v7` | `23.1.1` | replaced by `androidx.appcompat:appcompat:1.7.0` |
| `jsoup` | `1.8.2` (bundled JAR) | `org.jsoup:jsoup:1.17.2` (via Maven) |
| New: `androidx.fragment:fragment` | — | `1.8.1` |
| New: `androidx.loader:loader` | — | `1.1.0` |
| New: `androidx.localbroadcastmanager:localbroadcastmanager` | — | `1.1.0` |
| New: `androidx.viewpager:viewpager` | — | `1.0.0` |
| New: `androidx.drawerlayout:drawerlayout` | — | `1.2.0` |

> **Note:** `com.mikepenz:iconics:1.0.2` (used by the local MaterialDrawer library) is kept at its original version since the library source code targets its 1.x API. Jetifier handles the binary transformation of its support library references at build time.
