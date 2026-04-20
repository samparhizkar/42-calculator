# Phase 1 — Build System (Kotlin DSL + Version Catalog)

## Status: ✅ Complete

## What was done

Migrated the entire Gradle build system from Groovy DSL to Kotlin DSL and introduced a
centralized version catalog.

### Files changed

| Old file | New file | Action |
|----------|----------|--------|
| `settings.gradle` | `settings.gradle.kts` | Replaced |
| `build.gradle` | `build.gradle.kts` | Replaced |
| `app/build.gradle` | `app/build.gradle.kts` | Replaced |
| _(new)_ | `gradle/libs.versions.toml` | Created |
| `gradle.properties` | `gradle.properties` | Updated (added `kotlin.code.style`) |

### Key changes

- All dependency versions live in `gradle/libs.versions.toml` — one place to bump them.
- `settings.gradle.kts` now uses `dependencyResolutionManagement` with `FAIL_ON_PROJECT_REPOS`
  so module-level repository declarations are blocked (modern best practice).
- Kotlin plugin (`org.jetbrains.kotlin.android`) added to the catalog and applied in
  `app/build.gradle.kts` — ready for Phase 2 Java → Kotlin conversion.
- Removed the `configurations.all { exclude kotlin-stdlib-jdk7/jdk8 }` hack; it is not
  needed with Kotlin 2.x (stdlib is unified).
- Switched `core` → `core-ktx` and `fragment` → `fragment-ktx` in deps; these are the
  Kotlin extension variants and have the same artifact coordinates after AndroidX unification.
- `aidl = true` kept because `IInAppBillingService.aidl` still exists; remove in Phase 6
  when billing is replaced.

## Restart prompt (if conversation was cleared)

```
I'm modernizing an Android calculator app (42-calculator) that was originally written in
2015. We completed Phase 1 (build system migration to Kotlin DSL + version catalog).

Phase 2 is to convert all Java source files to Kotlin. The source files are under:
  app/src/main/java/com/sepidsa/fortytwocalculator/

The user has approved Java → Kotlin conversion for all files.
Rules:
- Use Android Studio's "Convert Java File to Kotlin File" conventions.
- Prefer idiomatic Kotlin (data classes, extension functions, null safety, coroutines
  where applicable) rather than a mechanical line-by-line translation.
- Do NOT refactor architecture yet (that's Phase 3). Just convert the language.
- Keep file structure the same; just change .java → .kt.
- The Expression.java and BigDecimalUtils.java core logic files must be preserved
  functionally — convert language only, no logic changes.
- After conversion, remove the `kotlin-stdlib` exclusion block if it still exists.

Refer to refactor/phase-2-java-to-kotlin.md for full instructions.
```
