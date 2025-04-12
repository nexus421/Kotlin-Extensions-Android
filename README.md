# Kotlin-Extensions-Android

***Now Available on Jitpack!***
In settings.gradle, add:

    repositories {
        maven("https://jitpack.io")
    }

In your build.gradle add:

    implementation ("com.github.nexus421:Kotlin-Extensions-Android:3.7.0")

(Check Releases for other versions)

**Warning: Breaking changes in Version >= 3**
This Repo now only contains Android-specific extensions, etc. If you want to use the general
Kotlin-JVM-Extensions, also implement my repository "KotNexLib"!

**Since 3.7.0 some extensions/functions need KotNexLib. You may also add it to your build.gradle**

**Examples:**
**View.makeVisible()**
**Context.hasCameraPermission()** -> Quick check for camera permissions (and others)
**Context.showToast()** -> Quicker way to show a toast
**Context.inflate()** -> Quicker way to inflate XML-Layouts
**checkAndRequestPermission(...)** -> Easy to use permission check and request if necessary
**Context.vibrate()**
**Easy calendar event handler with CalendarHelper**

Do you have any other great extensions you wish to include? No Problem! 

