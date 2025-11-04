# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Hilt ProGuard Rules
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp
-keep @dagger.hilt.android.HiltAndroidApp class * {
    <init>(...);
}

# Keep all Hilt generated classes
-keep class **_HiltModules { *; }
-keep class **_HiltComponents { *; }
-keep class **_GeneratedInjector { *; }
-keep class **Hilt_** { *; }

# Keep classes that use Hilt annotations
-keep @dagger.hilt.android.AndroidEntryPoint class * {
    <init>(...);
}
-keep @dagger.Module class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }

# Preserve reflection for Hilt
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations

# Keep Application class and its Hilt generated subclass
-keep class com.dmdev.fossilvaultanda.FossilVaultApplication { *; }
-keep class com.dmdev.fossilvaultanda.Hilt_FossilVaultApplication { *; }

# ================================
# Firebase/Firestore Keep Rules
# ================================

# Keep all Firestore model classes and their fields
# This is CRITICAL for Firestore deserialization to work in release builds
-keep class com.dmdev.fossilvaultanda.data.models.** { *; }
-keepclassmembers class com.dmdev.fossilvaultanda.data.models.** { *; }

# Keep geological time classes (different package)
-keep class com.fossilVault.geological.** { *; }
-keepclassmembers class com.fossilVault.geological.** { *; }

# Keep Room database entities
-keep class com.dmdev.fossilvaultanda.data.local.** { *; }
-keepclassmembers class com.dmdev.fossilvaultanda.data.local.** { *; }

# Keep all enum classes and their fields
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
    <fields>;
}

# Keep Kotlin data class generated methods
-keep class kotlin.Metadata { *; }
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable <fields>;
}

# Keep attributes needed for reflection
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Keep Firestore DocumentSnapshot conversion
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
    @com.google.firebase.firestore.Exclude <fields>;
    @com.google.firebase.firestore.ServerTimestamp <fields>;
}

# Keep serialization annotations
-keepattributes AnnotationDefault

# Kotlinx Serialization support
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.dmdev.fossilvaultanda.**$$serializer { *; }
-keepclassmembers class com.dmdev.fossilvaultanda.** {
    *** Companion;
}
-keepclasseswithmembers class com.dmdev.fossilvaultanda.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep companion objects for enums with serialization
-keepclassmembers class * {
    public static ** Companion;
}

# Keep source file names and line numbers for better crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Firebase specific rules (defensive - Firebase includes its own, but be explicit)
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**