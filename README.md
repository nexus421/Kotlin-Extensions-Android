# Kotlin-Extensions-Android

[![](https://jitpack.io/v/nexus421/Kotlin-Extensions-Android.svg)](https://jitpack.io/#nexus421/Kotlin-Extensions-Android)

A comprehensive collection of Kotlin extensions and utility functions for Android development that simplifies common
tasks and reduces boilerplate code.

## Installation

### Step 1: Add the JitPack repository to your build file

Add it in your root `settings.gradle`:

```gradle
repositories {
    maven("https://jitpack.io")
}
```

### Step 2: Add the dependency

Add the dependency to your app's `build.gradle`:

```gradle
dependencies {
    implementation("com.github.nexus421:Kotlin-Extensions-Android:3.7.0")

    // Since 3.7.0, some extensions/functions require KotNexLib
    implementation("com.github.nexus421:KotNexLib:1.0.0") // Check for latest version
}
```

## Important Notes

**Breaking changes in Version >= 3**  
This repository now only contains Android-specific extensions. For general Kotlin-JVM extensions, please also implement
the [KotNexLib](https://github.com/nexus421/KotNexLib) repository.

## Features

### Context Extensions

- `showToast(msg, showLong)` - Display toast messages easily
- `hasCameraPermission()`, `hasFineLocationPermission()` - Quick permission checks
- `checkAndRequestPermission(permission, onResult)` - Simplified permission handling
- `checkAndRequestPermissions(permissions, onResult)` - Request multiple permissions at once
- `inflate(layoutId)` - Simplified layout inflation
- `vibrate(timeMillis, strength)` - Easy device vibration
- `isNetworkAvailable()` - Check network connectivity
- `copyToClipboard(text, label)` - Copy text to clipboard
- `getScreenSize()` - Get device screen dimensions
- `openAppSystemSettings()` - Open app settings page

### View Extensions

- `makeVisible()`, `makeGone()`, `makeInvisible()` - Simplified visibility control
- `visibleIf(condition)`, `goneIf(condition)`, `invisibleIf(condition)` - Conditional visibility
- `EditText.getTextAsString()`, `EditText.value` - Easy text extraction
- `TextView.setBold()` - Set text to bold

### Calendar Utilities

- `addEventToCalendar()` - Add events to device calendar
- `readEventsFromCalendar()` - Read calendar events
- `deleteEventFromCalendar()` - Delete calendar events
- `updateEventInCalendar()` - Update existing calendar events
- `queryCalendars()` - Get available calendars

### Dialog Utilities

- `showDialogWithTimer()` - Display a dialog with countdown timer
- `showSimpleDialog()`, `showSimpleDialogCompat()` - Easy dialog creation

### Other Utilities

- `Drawable.drawableToBitmap()` - Convert drawables to bitmaps
- `ComponentActivity.takePicture()` - Simplified camera functionality
- `File.getSize()` - Get file size in a version-compatible way
- `getHeapInfo()` - Monitor app memory usage

## Usage Examples

### Permission Handling

```kotlin
// Check and request a single permission
checkAndRequestPermission(Manifest.permission.CAMERA) { granted ->
    if (granted) {
        // Permission granted, proceed with camera operation
    } else {
        // Permission denied, show explanation or alternative
    }
}

// Check and request multiple permissions
checkAndRequestPermissions(
    listOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
) { results ->
    if (results.all { it.value }) {
        // All permissions granted
    } else {
        // Some permissions denied
    }
}
```

### View Manipulation

```kotlin
// Show/hide views
myView.makeVisible()
otherView.makeGone()

// Conditional visibility
myButton.visibleIf(isLoggedIn)
loadingIndicator.goneIf(isDataLoaded)

// Get text from EditText
val userInput = myEditText.value
```

### Calendar Operations

```kotlin
// Add event to calendar
addEventToCalendar(
    context = context,
    calendarId = calendarId,
    title = "Meeting",
    description = "Team sync-up",
    location = "Conference Room",
    startTimeMillis = startTime,
    endTimeMillis = endTime
)

// Read calendar events
readEventsFromCalendar(context, calendarId).onSuccess { events ->
    // Process events
}
```

## Contribution

Contributions are welcome! If you have any great extensions you wish to include, please submit a pull request.
