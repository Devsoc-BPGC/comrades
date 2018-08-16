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
# https://firebase.google.com/docs/crashlytics/customize-crash-reports
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-printmapping mapping.txt
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**


# https://gist.github.com/brownsoo/9b11a823360c9cf184263df3e669375c
-keep public class * extends androidx.lifecycle.AndroidViewModel {
    <init>(...);
}

-keepclassmembers class androidx.lifecycle.Lifecycle$State{ *; }
-keepclassmembers class androidx.lifecycle.Lifecycle$Event{ *; }
-keepclassmembers class androidx.** { *; }
-keep class androidx.** { *; }
-dontwarn androidx.**

-keep class android.support.v7.widget.SearchView { *; }
