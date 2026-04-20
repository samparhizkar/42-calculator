# Phase 4 — Data Layer (Room + WorkManager)

## Status: ⏳ Not started (requires Phase 3 complete)

## Goal

Replace legacy data infrastructure:
- Raw SQLite + ContentProvider → Room database
- SyncAdapter + AccountAuthenticator → WorkManager `CoroutineWorker`
- jsoup HTML scraping for currency → proper JSON API call

## What to remove

| File/Component | Reason | Replacement |
|---|---|---|
| `data/LogDbHelper.java` | Raw SQLite | Room `AppDatabase` + `LogDao` |
| `data/ConstantDbHelper.java` | Raw SQLite | Room `AppDatabase` + `ConstantDao` |
| `data/CurrencyDbHelper.java` | Raw SQLite | Room `AppDatabase` + `CurrencyDao` |
| `data/LogProvider.java` | ContentProvider overkill for internal use | `LogDao` via ViewModel |
| `data/ConstantProvider.java` | ContentProvider overkill | `ConstantDao` via ViewModel |
| `data/CurrencyProvider.java` | ContentProvider overkill | `CurrencyDao` via ViewModel |
| `sync/CurrencySyncAdapter.java` | Deprecated SyncAdapter pattern | `CurrencyWorker : CoroutineWorker` |
| `sync/CurrencyAuthenticator.java` | Dummy authenticator for SyncAdapter | Delete entirely |
| `sync/CurrencyAuthenticatorService.java` | Required by SyncAdapter | Delete entirely |
| `sync/CurrencySyncService.java` | Required by SyncAdapter | Delete entirely |
| `xml/authenticator.xml` | SyncAdapter metadata | Delete |
| `xml/syncadapter.xml` | SyncAdapter metadata | Delete |
| jsoup dependency | HTML scraping is fragile | `HttpURLConnection` + `JSONObject` |

## Room schema

### AppDatabase.kt
```kotlin
@Database(
    entities = [LogEntry::class, Constant::class, CurrencyRate::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun logDao(): LogDao
    abstract fun constantDao(): ConstantDao
    abstract fun currencyDao(): CurrencyDao
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
**CurrencyRate** — maps to existing `currency` table (check exact column names in
`ConstantContract.kt` and `CurrencyContract.kt`)

## WorkManager for currency sync

```kotlin
class CurrencyWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        // fetch from JSON API (confirm endpoint with user)
        // update Room CurrencyDao
        return Result.success()
    }
}
```

Schedule periodic sync (e.g. every 6 hours) in `Application.onCreate()` using
`PeriodicWorkRequest`.

## AndroidManifest cleanup (after this phase)

Remove:
- `AUTHENTICATE_ACCOUNTS` permission
- `READ_SYNC_SETTINGS` / `WRITE_SYNC_SETTINGS` permissions
- `CurrencyAuthenticatorService` declaration
- `CurrencySyncService` declaration
- `syncable="true"` attributes on ContentProviders
- The ContentProvider declarations themselves (LogProvider, ConstantProvider, CurrencyProvider)

## Dependencies to add

```toml
[versions]
room = "2.6.1"
workmanager = "2.9.1"

[libraries]
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
workmanager-ktx = { group = "androidx.work", name = "work-runtime-ktx", version.ref = "workmanager" }

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
