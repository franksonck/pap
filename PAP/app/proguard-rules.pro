# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\thoma\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
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
## PAS TOUCHE AUX DATAWRAPPER DB
-keepattributes InnerClasses
-keep class fr.jlm2017.pap.MongoDB.DataWrapperPortes** {*;}
-keep class fr.jlm2017.pap.MongoDB.DataWrapperMilitant** {*;}
-keep class fr.jlm2017.pap.MongoDB.Identifier {
                                                 public private *;
                                             }
-keep class fr.jlm2017.pap.MongoDB.Porte {
                                            public private *;
                                        }
-keep class fr.jlm2017.pap.MongoDB.Militant {
                                                public private *;
                                            }

## PAS TOUCHE AUX DATAWRAPPER GEO

-keep class fr.jlm2017.pap.GeoLocalisation.GeoData** {*;}
-keep class fr.jlm2017.pap.GeoLocalisation.GeoDataWrapper** {*;}

-dontwarn com.google.gson.**
-dontwarn java.nio.file.**
-dontwarn org.codehaus.mojo.animal_sniffer.**
-dontwarn com.squareup.okhttp.internal.huc.**

-keep class com.android.volley.error.** { *; }
-keep class com.squareup.okhttp.internal.huc.** { *; }
-keep class okio.** { *; }

-keep class android.support.design.widget.** { *; }
-keep interface android.support.design.widget.** { *; }
-dontwarn android.support.design.**