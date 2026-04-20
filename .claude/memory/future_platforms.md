---
name: Cross-platform plans
description: Flutter port planned for iOS and web after Android redesign is complete. FUTURE_PLATFORMS.md has the full plan + session starter prompt.
type: project
---

iOS and web port is planned. Recommendation: Flutter (one codebase for all 3). Full plan in `FUTURE_PLATFORMS.md`.

**Why:** FUTURE_PLATFORMS.md — framework rationale, engine file inventory (~2,400 lines of portable Java logic), Flutter project structure, dependency list, platform-specific gotchas (web needs IndexedDB not SQLite, no IAP on web, no dynamic color on iOS/web).

**Key prerequisite during Android work:** decouple `Expression.java` from Android `Context` — it currently reads operator symbols from Android string resources, making it unportable. Fix is ~5 call sites.

**How to apply:** When user mentions iOS, web, or Flutter, point them to `FUTURE_PLATFORMS.md` for the full plan and the ready-made session starter prompt at the bottom of that file.
