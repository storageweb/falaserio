# ProGuard rules for FalaSerio

# Keep Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ComponentSupplier { *; }

# Keep Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Keep Billing
-keep class com.android.vending.billing.**

# Keep AdMob
-keep class com.google.android.gms.ads.** { *; }

# Keep Compose
-dontwarn androidx.compose.**

# Keep model classes
-keep class br.com.webstorage.falaserio.domain.model.** { *; }
-keep class br.com.webstorage.falaserio.data.local.entity.** { *; }
