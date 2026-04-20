# Phase 4 — Data Layer (Room)

## Status: ⏳ Not started (requires Phase 3 complete)

## Goal

Replace legacy data infrastructure:
- Raw SQLite + ContentProvider → Room database
- Keep the data-layer scope limited to calculator history and constants only
- Currency is already being removed from the product; do not migrate any currency UI,
  database, sync, scraping, or worker code into the new architecture

## What to remove

| File/Component | Reason | Replacement |
|---|---|---|
| `data/LogDbHelper.java` | Raw SQLite | Room `AppDatabase` + `LogDao` |
| `data/ConstantDbHelper.java` | Raw SQLite | Room `AppDatabase` + `ConstantDao` |
| `data/LogProvider.java` | ContentProvider overkill for internal use | `LogDao` via ViewModel |
| `data/ConstantProvider.java` | ContentProvider overkill | `ConstantDao` via ViewModel |
| `data/CurrencyDbHelper.java` | Currency feature removed | Delete entirely; no Room replacement |
| `data/CurrencyProvider.java` | Currency feature removed | Delete entirely; no Room replacement |
| `sync/CurrencySyncAdapter.java` | Currency feature removed | Delete entirely |
| `sync/CurrencyAuthenticator.java` | Currency feature removed | Delete entirely |
| `sync/CurrencyAuthenticatorService.java` | Currency feature removed | Delete entirely |
| `sync/CurrencySyncService.java` | Currency feature removed | Delete entirely |
| `xml/authenticator.xml` | Currency sync metadata | Delete |
| `xml/syncadapter.xml` | Currency sync metadata | Delete |
| jsoup / HTML scraping helpers | Currency scraping is gone | Delete dependency and related code if still present |

## Room schema

### AppDatabase.kt
```kotlin
@Database(
    entities = [LogEntry::class, Constant::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun logDao(): LogDao
    abstract fun constantDao(): ConstantDao
}
```

### Entities (match existing SQLite column names for migration)

**LogEntry** — maps to existing `log` table
```kotlin
@Entity(tableName = "log")
data class LogEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)
```
**Constant** — maps to existing `constants` table

No currency entity, DAO, worker, or background sync should be introduced in this phase.

## AndroidManifest cleanup (after this phase)

Remove:
- Any currency-specific permissions, services, providers, and metadata
- `CurrencyAuthenticatorService` declaration
- `CurrencySyncService` declaration
- Any currency sync/account entries left in strings, manifest, or XML resources
- Eventually the ContentProvider declarations themselves (LogProvider, ConstantProvider)
  once their Room replacements are wired in

## Dependencies to add

```toml
[versions]
room = "2.6.1"

[libraries]
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }

[plugins]
ksp = { id = "com.google.devtools.ksp", version = "2.2.10-1.0.29" }
```

Use KSP (not KAPT) for Room annotation processing — KSP is the modern standard.

## Restart prompt (if conversation was cleared)

```
I'm modernizing an Android calculator app (42-calculator). Completed so far:
  Phase 1 — Build system ✅
  Phase 2 — Java → Kotlin ✅
  Phase 3 — Architecture (MVVM) ✅
  Phase 4 — Data layer (Room + WorkManager) ✅  (update when done)

Phase 5 is to migrate the XML UI to Jetpack Compose with Material Design 3:
- Start with the main calculator keypad (DialpadFragment → CalculatorScreen composable).
- Then scientific panel, then history, then drawer/navigation.
- Apply Material 3 theming with dynamic color (Material You) support for Android 12+.
- Add edge-to-edge display (enableEdgeToEdge()).
- Replace density-specific PNG button drawables with vector XML or Compose drawing.
- The dual flat/retro layout system can be collapsed into a single Compose theme.

Refer to refactor/phase-5-compose-ui.md for full instructions.
```
