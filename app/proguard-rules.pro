-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod

-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type
-keep class com.google.gson.** { *; }
-keep interface com.google.gson.** { *; }

-keep class com.opennotes.feature_node.domain.model.** { *; }

-keep class com.opennotes.feature_node.data.repository.GsonJsonHandler { *; }
-keep interface com.opennotes.feature_node.data.repository.JsonHandler { *; }