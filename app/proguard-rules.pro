-keepattributes Signature, InnerClasses, EnclosingMethod, *Annotation*

-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken

-keepnames class com.google.gson.Gson
-keepnames class com.google.gson.JsonDeserializer
-keepnames class com.google.gson.JsonSerializer
