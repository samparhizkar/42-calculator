# Phase 2 — Journal / History Rework

> **Design: VOID v2 (locked April 2026).** Android implementation. Full spec in **`DESIGN_SPEC.md`**. Values for this screen: background `#080808`; result text Inter 300 34px `primary` colour; expression DM Mono 13px `onSurfaceVariant`; filter chips `brandMuted` bg + `brand` text when active; section headers Inter 700 11px uppercase `rgba(255,255,255,0.25)`; label chip `brandMuted` bg + `brand` text. See `DESIGN_SPEC.md §History screen`.

Scope: rethink the bookmark-centric "journal" as a unified **history tape** with starring and labeling as row-level actions. Fix the "label is dead until bookmarked" problem and make past calculations first-class.

Depends on: Phase 1 (M3 theme + styles) — this phase reuses those role tokens for list surfaces.

Out of scope:
- Scientific panel, theme editor, currency removal.
- Changes to the *calculation engine* or how results are computed — only how they're stored, surfaced, and interacted with.

---

## Current state (findings)

**Storage** — [LogContract.java](app/src/main/java/com/sepidsa/fortytwocalculator/data/LogContract.java), [LogDbHelper.java](app/src/main/java/com/sepidsa/fortytwocalculator/data/LogDbHelper.java)
- SQLite table `log` with columns: `_ID, result, result_no_comma, operation, tag, starred`.
- Backed by a `ContentProvider` ([LogProvider.java](app/src/main/java/com/sepidsa/fortytwocalculator/data/LogProvider.java)) at `content://com.sepidsa.fortytwocalculator/log`.
- **Every completed calculation is already inserted** via `sendLogMessage(...)` in [MainActivity.java:1877](app/src/main/java/com/sepidsa/fortytwocalculator/MainActivity.java:1877). So a full history already exists — the UI just doesn't treat it as one.

**UI fragments**
- [FavoritesFragment.java](app/src/main/java/com/sepidsa/fortytwocalculator/FavoritesFragment.java) + [fragment_favorite.xml](app/src/main/res/layout/fragment_favorite.xml) — bookmarked-only view.
- [AnimatedLogFragment.java](app/src/main/java/com/sepidsa/fortytwocalculator/AnimatedLogFragment.java) + [fragment_log.xml](app/src/main/res/layout/fragment_log.xml) — full log view (~316 lines, custom animations).
- Two separate adapters: [FavoritesAdapter.java](app/src/main/java/com/sepidsa/fortytwocalculator/FavoritesAdapter.java) + [LogAdapter.java](app/src/main/java/com/sepidsa/fortytwocalculator/LogAdapter.java) — bulk of the code is duplicated.

**Behavior today** (inferred from code + screenshot)
- Star button on main screen toggles `starred` on the **latest inserted row** ([MainActivity:2090-2097](app/src/main/java/com/sepidsa/fortytwocalculator/MainActivity.java:2090)).
- Pen button opens a dialog to set `tag` on the **latest inserted row** ([MainActivity:2114-2145](app/src/main/java/com/sepidsa/fortytwocalculator/MainActivity.java:2114)).
- The tag is *always* saved (non-null `TEXT NOT NULL` column, defaulting to empty string), but only surfaced in the starred view — that's the "dead label" problem the user called out.
- Journal is a two-pane swipe: favorites on one page, full log on another (the screenshot shows 2 page dots).
- Delete-all is wired (`getContentResolver().delete(... null, null)` at [MainActivity:706](app/src/main/java/com/sepidsa/fortytwocalculator/MainActivity.java:706)).

**Mental model mismatch**
- User thinks: "I bookmark a calculation, optionally labeling it."
- Data model is: "Every calc is recorded; starred is just a flag."
- The UI exposes the user's mental model, which is why labels feel orphaned — the data is richer than the UI admits.

---

## Target design

**One unified History view** with filter chips, replacing the favorites/log split.

### Layout (single fragment)

```
┌─────────────────────────────────────┐
│  [←]  History          [🔍]  [⋮]    │  ← top app bar
├─────────────────────────────────────┤
│  [ All ] [ ★ Starred ] [ 🏷 Labeled ]│  ← filter chips
├─────────────────────────────────────┤
│                                     │
│  Today                              │  ← date section header
│  ┌─────────────────────────────┐    │
│  │ 566 + 369                   │    │
│  │ = 935                   ★   │    │  ← tap row expands actions
│  │   nine hundred thirty-five  │    │
│  └─────────────────────────────┘    │
│  ┌─────────────────────────────┐    │
│  │ 65 × 67 + 3    🏷 receipt   │    │
│  │ = 4,358                 ☆   │    │
│  └─────────────────────────────┘    │
│                                     │
│  Yesterday                          │
│  …                                  │
└─────────────────────────────────────┘
```

### Row — default state
- `operation` top-left (dim, smaller).
- `result` large, left-aligned, `textAppearanceHeadlineSmall`.
- Optional label chip (shown on **every** labeled row — not just starred).
- Star toggle on the right (filled = starred, outlined = not).
- Spoken form (e.g. "nine hundred thirty-five") as a secondary line, muted. This is the app's signature feature — make it visible in history too.

### Row — expanded state (tap to expand, or always-on icons on the right)
Four actions, M3 icon buttons: `label`, `share`, `copy`, `delete`. Star stays visible outside so it's one-tap.

### Interactions
- **Tap row** → load expression back into the main calculator ("reuse this calculation"). New feature, high value.
- **Long-press row** → multi-select mode with batch delete/share. Standard M3.
- **Swipe left on row** → delete with undo snackbar. Standard M3.
- **Star tap** → toggles star inline, no dialog.
- **Label icon** → opens the same label dialog that currently exists, but usable on any row, not just the latest.

### Filter chips
- `All` (default)
- `★ Starred`
- `🏷 Labeled` — shows rows with a non-empty tag, regardless of star state. This is what unblocks the dead-label problem: labels become useful on their own.

### Search
Search icon already exists in [fragment_log.xml](app/src/main/res/layout/fragment_log.xml). Wire it to a `SearchView` in the app bar, query against `operation`, `result_no_comma`, and `tag` via `LIKE`.

### Empty states
- Empty history: illustration + "Your calculations will appear here."
- Empty starred: "Tap the star on any result to save it here."
- Empty labeled: "Add a label to any calculation to find it here."

### Entry point
Replace the dual-purpose journal icon with a single **History** icon. Star button on main screen stays (one-tap bookmark of latest result); label button becomes part of history rows only.

---

## Simplification: delete two fragments, keep one

**Delete** (after migration):
- `FavoritesFragment.java`, `FavoritesAdapter.java`, `fragment_favorite.xml`, `list_item_favorite.xml`.
- `AnimatedLogFragment.java` (keep logic we still need, but the 316-line custom-animation fragment goes; use `RecyclerView` + `DiffUtil` + default item animator).

**Create**:
- `HistoryFragment.kt` (or `.java` for consistency) — single fragment, M3 `RecyclerView`, filter chip group, search.
- `HistoryAdapter` — `ListAdapter<LogRow, VH>` with `DiffUtil`.
- `list_item_history.xml` exists ([layout/list_item_history.xml](app/src/main/res/layout/list_item_history.xml)) — rework it for the new row design rather than creating a new file.

**Keep**:
- `LogContract`, `LogDbHelper`, `LogProvider` — schema is fine. No migration needed.

---

## Implementation plan

### Step 1 — HistoryFragment shell
1. New `fragment_history.xml`: `MaterialToolbar` + `ChipGroup` (single-selection) + `RecyclerView` + empty-state `LinearLayout`.
2. New `HistoryFragment` that loads all rows via the existing `ContentProvider` (CursorLoader or migrate to Room later — out of scope).
3. Filter state held in a simple `enum Filter { ALL, STARRED, LABELED }`; re-query on chip change.

### Step 2 — Row redesign
1. Update `list_item_history.xml` to show: operation, result, spoken form, label chip (visibility gone if tag empty), star icon.
2. Inflate via `MaterialCardView` with `cardCornerRadius=16dp`, `cardElevation=0dp`, `strokeColor=colorOutlineVariant` for the M3 outlined-card look.
3. Row height target: ~88dp for comfortable touch.

### Step 3 — Per-row actions
1. Star tap → `ContentResolver.update(... COLUMN_STARRED ...)`. Reuse logic from [MainActivity:2090](app/src/main/java/com/sepidsa/fortytwocalculator/MainActivity.java:2090) but parameterize by `rowId`.
2. Label tap → extract the dialog from [MainActivity:2114](app/src/main/java/com/sepidsa/fortytwocalculator/MainActivity.java:2114) into a reusable `LabelDialogFragment` that takes a `rowId` arg.
3. Swipe-to-delete via `ItemTouchHelper` + snackbar with undo (cache the deleted row in memory for the snackbar duration; re-insert on undo).
4. Tap-to-reuse: pass `operation` string back to `MainActivity` via fragment result API, have `MainActivity` parse it into the current expression buffer.

### Step 4 — Search
1. Add `SearchView` to `MaterialToolbar` via menu XML.
2. Debounce 200ms, re-query with `LIKE` clauses across `operation`, `result_no_comma`, `tag`.

### Step 5 — Section headers by date
1. Add `COLUMN_CREATED_AT INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000)` — **schema change**, requires DB version bump and an `onUpgrade` path that adds the column for existing users (all previous rows get the migration timestamp; acceptable — they'll all land under one section on first open).
2. Group rows in the adapter by `Today / Yesterday / <date>`. Standard M3 list-with-headers pattern.

### Step 6 — Migration & cleanup
1. Delete the two old fragments, adapters, and layouts listed above.
2. Remove the two-page `ViewPager` inside the journal; journal is now a single screen.
3. Keep `sendLogMessage(...)` in `MainActivity` — it's still the insertion point. It already logs every equals-press, which is what we want.
4. Audit `MainActivity` for references to `FavoritesFragment` / `AnimatedLogFragment` and redirect to `HistoryFragment`.

### Step 7 — M3 polish
1. Apply Phase 1 M3 theme to history screen (it'll inherit automatically once Phase 1 lands).
2. Dark mode smoke test.
3. RTL smoke test (Persian + Arabic locales) — the filter chips, search bar, and row layout must mirror correctly. Use `start/end` instead of `left/right` throughout.

---

## Open questions for the user

1. **Default sort** — newest-first (current behavior) or group by date with "pinned starred at top"? I recommend chronological with date headers; pinning hides the temporal context.
2. **Tap-to-reuse** — should it *replace* the current expression in the calculator, or *append* to it (e.g. for continuing a running tally)? M3 / Google calc replaces.
3. **Auto-delete** — should we cap history (e.g. keep last 500 or last 90 days) or let it grow unbounded? Unbounded is simpler; a cap prevents accidental bloat.
4. **Schema migration** — OK to bump `DATABASE_VERSION` from 1 → 2 and backfill `created_at` with "now" for existing rows? Alternative is `NULL` and showing them under "Earlier."
5. **Room migration** — tempting to move from raw SQLite + ContentProvider to Room while we're in here, but it's a significant refactor. Defer to a separate phase, or fold in?

---

## Acceptance criteria

- Single unified History screen replaces both old fragments.
- Star, label, delete, share, copy all work per-row from history — not just on the latest entry.
- Labels visible on rows regardless of star state (the "dead label" problem is fixed).
- Filter chips correctly toggle All / Starred / Labeled.
- Search matches across operation, result, and label.
- Swipe-to-delete has undo via snackbar.
- Tap-to-reuse loads expression back into calculator.
- No regression in RTL languages (Persian, Arabic).
- Existing DB rows survive the schema bump (all appear under one date section, acceptable for v1).
- ~450 lines of fragment/adapter code deleted net of what we add.
