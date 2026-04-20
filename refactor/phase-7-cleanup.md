# Phase 7 — Final Cleanup

## Status: ⏳ Not started (requires all prior phases complete)

## Goal

Remove all dead code, unused resources, deprecated API usage, and unnecessary permissions.
Raise minSdk if appropriate. Leave the codebase clean and ready for ongoing development.

## Permissions to remove from AndroidManifest.xml

| Permission | Why |
|---|---|
| `READ_PHONE_STATE` | Was used for old billing device ID — no longer needed |
| `READ_EXTERNAL_STORAGE` (maxSdk 18) | Dead code, maxSdk is below minSdk |
| `WRITE_EXTERNAL_STORAGE` (maxSdk 18) | Dead code |
| `AUTHENTICATE_ACCOUNTS` | Removed with SyncAdapter in Phase 4 |
| `READ_SYNC_SETTINGS` | Removed with SyncAdapter in Phase 4 |
| `WRITE_SYNC_SETTINGS` | Removed with SyncAdapter in Phase 4 |
| `com.farsitel.bazaar.permission.PAY_THROUGH_BAZAAR` | Remove if not targeting Cafebazaar |

**Keep**: `INTERNET`, `POST_NOTIFICATIONS`

## Dead resource folders to delete

| Folder | Reason |
|---|---|
| `res/values-v11/` | minSdk 21 covers this |
| `res/values-v14/` | minSdk 21 covers this |
| `res/values-v16/` | minSdk 21 covers this |
| `res/values-small-land/` | Covered by Compose adaptive layout |
| `res/values-small-port/` | Covered by Compose adaptive layout |
| `res/layout-*/` | All XML layouts removed in Phase 5 |
| `res/anim/` | Replaced by Compose animations in Phase 5 |

## PNG button drawables to delete (after Phase 5)

The following density-specific PNG button backgrounds (× 5 density buckets each) become
obsolete once Compose handles drawing:

- `btn_blue_normal/pressed`
- `btn_black_normal/pressed`
- `btn_c_normal/pressed`
- `btn_equally_normal/pressed`
- `btn_grey_normal/pressed`
- All corresponding `selector_for_btn_*.xml` selectors

## minSdk consideration

Current: 21 (Android 5.0, released 2014).
Suggested: raise to 24 (Android 7.0, released 2016).
- Android 7.0+ coverage: ~96% of active devices as of 2025.
- Raising to 24 allows removing `multidex` workarounds (if any) and some compat code.
- **Confirm with user** before raising — depends on their audience (Iranian market may
  have older device distribution).

## Kotlin/build hygiene

- Run `./gradlew lint` and fix all warnings.
- Run `./gradlew dependencies` to verify no duplicate/conflicting transitive deps.
- Verify ProGuard rules still cover all remaining code.
- Remove any `@SuppressWarnings` or `@Suppress` annotations that are no longer needed.
- Delete any `.java` files if the Java → Kotlin conversion somehow left any behind.

## Restart prompt (if conversation was cleared)

```
I'm modernizing an Android calculator app (42-calculator). All 6 prior phases are
complete. This is the final cleanup phase.

Tasks:
1. Remove dead permissions from AndroidManifest.xml (READ_PHONE_STATE, storage permissions
   with maxSdk 18, AUTHENTICATE_ACCOUNTS, sync permissions).
2. Delete dead resource folders: values-v11, v14, v16, layout-land etc.
3. Delete density-specific PNG button drawables (they were replaced by Compose in Phase 5).
4. Consider raising minSdk from 21 to 24 (confirm with user first).
5. Run ./gradlew lint and fix warnings.
6. Verify ProGuard rules.

See refactor/phase-7-cleanup.md for the full checklist.
```
