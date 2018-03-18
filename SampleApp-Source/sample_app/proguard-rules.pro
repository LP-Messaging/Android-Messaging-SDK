# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.support.v4.app.DialogFragment
-keep public class * extends android.app.Fragment

-dontwarn com.squareup.picasso.*
-dontwarn com.squareup.okhttp.*
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# from the king himself 9.2017
# https://github.com/square/picasso/issues/1410
### OKHTTP
-dontwarn okhttp3.**

### OKIO
-dontwarn okio.**

### PICASSO
# Checks for OkHttp versions on the classpath to determine Downloader to use.
-dontnote com.squareup.picasso.Utils
# Downloader used only when OkHttp 2.x is present on the classpath.
-dontwarn com.squareup.picasso.OkHttpDownloader
# Downloader used only when OkHttp 3.x is present on the classpath.
-dontwarn com.squareup.picasso.OkHttp3Downloader