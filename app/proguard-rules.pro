# ProGuard / R8 rules for POS Ambulante

# Optimization and Obfuscation settings for Google Play compliance
-allowaccessmodification
-repackageclasses 'com.example.pos'

# Preserve line numbers and file attributes for crash reporting deobfuscation
-keepattributes SourceFile,LineNumberTable,*Annotation*,Signature,InnerClasses,EnclosingMethod
-renamesourcefileattribute SourceFile

# Room Database rules
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keepclassmembers class * {
    @androidx.room.TypeConverter *;
}

# Keep Data Models & Room entities fields
-keepclassmembers class com.example.data.model.** { *; }

# Coil image loader
-dontwarn coil.**

# Kotlin Coroutines
-dontwarn kotlinx.coroutines.**
