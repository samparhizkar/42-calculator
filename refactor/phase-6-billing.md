# Phase 6 — Billing (Play Billing Library 7.x)

## Status: ⏳ Not started (requires Phase 2 complete)

## Goal

Replace the 2013-era AIDL-based Google Play Billing v1 implementation with the modern
Google Play Billing Library 7.x (Kotlin + coroutines).

## ⚠️ Decision required before starting

**Which store(s) is this app targeting?**

- **Google Play only** → use `com.android.billingclient:billing-ktx`
- **Cafebazaar (Iranian store) only** → use Bazaar's own billing SDK
- **Both** → need a billing abstraction layer

The manifest currently has `com.farsitel.bazaar.permission.PAY_THROUGH_BAZAAR` which
suggests Cafebazaar targeting. Confirm with user before proceeding.

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
| `util/Security.kt` | Purchase signature verification (replaced by BillingClient) |
| `util/SystemUiHider*.kt` | Deprecated system UI hider — delete here too |
| `app/src/main/aidl/` | Entire AIDL directory |
| `IInAppBillingService.aidl` | AIDL billing interface |

## Build file changes

In `app/build.gradle.kts`:
```kotlin
buildFeatures {
    // Remove: aidl = true
    // (only keep compose = true after Phase 5)
}
```

In `gradle/libs.versions.toml`:
```toml
[versions]
billing = "7.1.1"

[libraries]
billing-ktx = { group = "com.android.billingclient", name = "billing-ktx", version.ref = "billing" }
```

## New BillingRepository (Google Play)

```kotlin
class BillingRepository(private val context: Context) {
    private val billingClient = BillingClient.newBuilder(context)
        .setListener { billingResult, purchases -> /* handle purchases */ }
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    suspend fun queryProductDetails(productId: String): ProductDetails? { ... }
    suspend fun launchBillingFlow(activity: Activity, productDetails: ProductDetails) { ... }
    fun observePurchases(): Flow<List<Purchase>> { ... }
}
```

## Restart prompt (if conversation was cleared)

```
I'm modernizing an Android calculator app (42-calculator). Completed so far:
  Phase 1 — Build system ✅
  Phase 2 — Java → Kotlin ✅
  Phase 3 — Architecture (MVVM) ✅
  Phase 4 — Data layer (Room + WorkManager) ✅
  Phase 5 — Compose UI + Material 3 ✅
  Phase 6 — Billing (Play Billing Library 7) ✅  (update when done)

Phase 7 is the final cleanup pass. See refactor/phase-7-cleanup.md for full instructions.
```
