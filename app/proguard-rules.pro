-keepattributes Signature, InnerClasses, EnclosingMethod, *Annotation*

-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken

-keepnames class com.google.gson.Gson
-keepnames class com.google.gson.JsonDeserializer
-keepnames class com.google.gson.JsonSerializer

-keep class com.opennotes.feature_node.domain.model.** { *; }

-keep class com.opennotes.feature_node.data.repository.GsonJsonHandler { *; }
-keep interface com.opennotes.feature_node.data.repository.JsonHandler { *; }