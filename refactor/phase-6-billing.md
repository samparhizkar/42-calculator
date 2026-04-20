# Phase 6 — Billing Removal (Free App)

## Status: ⏳ Not started (requires Phase 2 complete)

## Goal

Remove the legacy billing system entirely and keep the app fully free.

- Do not migrate the old AIDL-based Google Play Billing v1 implementation
- Do not replace it with Google Play Billing Library, Bazaar billing, ads SDKs,
  subscriptions, or any other monetization stack
- Remove premium gating so all calculator functionality is available without
  purchase, entitlement restoration, or store integration

## Direction

Billing is no longer a product requirement.

- No store-specific billing decision is needed
- No billing abstraction layer is needed
- No purchase flow, restore flow, SKU catalog, or entitlement persistence should remain
- If code exists only to support premium unlocks, delete it rather than modernizing it

## What to remove

| File | Reason |
|---|---|
| `util/IabHelper.kt` | Legacy AIDL billing |
| `util/IabException.kt` | Legacy AIDL billing |
| `util/IabResult.kt` | Legacy AIDL billing |
| `util/Inventory.kt` | Legacy AIDL billing |
| `util/Purchase.kt` | Legacy AIDL billing |
| `util/SkuDetails.kt` | Legacy AIDL billing |
| `util/Base64.kt` | Used only by billing security check |
| `util/Base64DecoderException.kt` | Used only by billing |
| `util/Security.kt` | Purchase signature verification |
| `app/src/main/aidl/` | Entire AIDL billing directory |
| `IInAppBillingService.aidl` | AIDL billing interface |
| Billing-related premium UI flows | App is now fully free |
| Purchase state persistence | No entitlement state is needed |
| Restore-purchase and upsell strings/resources | No billing UX should remain |
| Store billing permissions/metadata | No store billing integration should remain |

Delete `util/SystemUiHider*.kt` here too if those files are still only retained by
legacy premium/onboarding flows and are otherwise unused.

## Build file changes

In `app/build.gradle.kts`:
```kotlin
buildFeatures {
    // Remove: aidl = true
    // Keep only the features still used by the app
}
```

In `gradle/libs.versions.toml`:

- Do not add `com.android.billingclient:billing-ktx`
- Remove any billing-related version or library alias if one still exists

## App behavior after removal

After this phase:

- The app launches and functions without any billing initialization
- All previously premium-gated features are available for free
- No purchase checks run at startup, in settings, or when opening calculator features
- No buy/upgrade/restore UI remains in menus, dialogs, onboarding, or settings
- No code path depends on store services or billing callbacks

## Verification checklist

- Search the project for billing, purchase, premium, subscription, and SKU references
  and remove or rewrite the remaining app logic
- Confirm there is no `billing-ktx` dependency and no AIDL billing interface left
- Confirm `aidl = true` is removed if billing was its only remaining use
- Confirm all constants and calculator features are usable without any payment flow
- Confirm manifest permissions and metadata no longer reference Google Play or Bazaar
  billing

## Restart prompt (if conversation was cleared)

```
I'm modernizing an Android calculator app (42-calculator). Completed so far:
  Phase 1 — Build system ✅
  Phase 2 — Java → Kotlin ✅
  Phase 3 — Architecture (MVVM) ✅
  Phase 4 — Data layer (Room + WorkManager) ✅
  Phase 5 — Compose UI + Material 3 ✅
  Phase 6 — Billing removal / free app ✅  (update when done)

Phase 7 is the final cleanup pass. See refactor/phase-7-cleanup.md for full instructions.
```
