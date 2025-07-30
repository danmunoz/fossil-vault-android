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