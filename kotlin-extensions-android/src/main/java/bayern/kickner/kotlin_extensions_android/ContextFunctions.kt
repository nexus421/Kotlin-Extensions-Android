package bayern.kickner.kotlin_extensions_android

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Insets
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.nfc.NfcAdapter
import android.os.*
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Size
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IntRange
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import com.google.android.material.snackbar.Snackbar
import kotnexlib.tryOrNull


fun Context.isActivity() = this is Activity

/**
 * Shows this toast on the main thread. So you can use it everywhere!
 */
fun Context.showToastOnMainThread(s: String, showLong: Boolean = true) =
    runOnUiThread { showToast(s, showLong) }

fun Activity.getRootView() = findViewById<View>(android.R.id.content).rootView

fun Activity.startActivity(
    destination: Class<*>,
    finishCallingActivity: Boolean = false,
    intentExtras: (Intent.() -> Unit)? = null
) {
    val i = Intent(this, destination)
    intentExtras?.invoke(i)
    startActivity(i)
    if (finishCallingActivity) finish()
}

fun Context.showToast(msg: String, showLong: Boolean = true) =
    Toast.makeText(this, msg, if (showLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()

fun Context.hasCameraPermission() =
    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PERMISSION_GRANTED

fun Context.hasFineLocationPermission() =
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED

fun Context.inflate(@LayoutRes layoutId: Int) = View.inflate(this, layoutId, null)

fun Context.getApplicationName(): String {
    val stringId = applicationInfo.labelRes
    return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else getString(stringId)
}

fun AppCompatActivity.showSimpleDialogCompat(title: String, msg: String) {
    androidx.appcompat.app.AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(msg)
        .setPositiveButton("OK", null)
        .show()
}

fun Activity.showSimpleDialog(title: String, msg: String) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(msg)
        .setPositiveButton("OK", null)
        .show()
}

fun View.snackbar(message: String, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(this, message, duration).show()
}

/**
 * Checks if a permission is already granted with a fast check against ContextCompat.checkSelfPermission.
 * If this is false, the permission will be requested from the user via registerForActivityResult.
 *
 * Hint: If the user denies the permission request more than 2 times, this method will always return false!
 *
 * @param manifestPermission Permission String to check from Manifest.permission.*
 * @param onResult the result from the permission check.
 */
fun ComponentActivity.checkAndRequestPermission(manifestPermission: String, onResult: (Boolean) -> Unit) {
    if (ContextCompat.checkSelfPermission(
            this,
            manifestPermission
        ) == PackageManager.PERMISSION_GRANTED
    ) return onResult(true)
    registerForActivityResult(ActivityResultContracts.RequestPermission(), onResult).launch(manifestPermission)
}

/**
 * Checks if a permission is already granted with a fast check against ContextCompat.checkSelfPermission.
 * If all permissions are granted, this method calls the callback with the permissions and true for each entry.
 * Therefore the less performant way of registerForActivity can be ignored.
 * If any is false, all permissions will be requested from the user via registerForActivityResult.
 *
 * Hint: If the user denies the permission request more than 2 times, this method will always return false!
 *
 * @param manifestPermissions Permission Strings to check from Manifest.permission.*
 * @param onResult the result from the permission check. Map<PermissionString, Granted>
 */
fun ComponentActivity.checkAndRequestPermissions(
    manifestPermissions: List<String>,
    onResult: (Map<String, Boolean>) -> Unit
) {
    if (manifestPermissions.find {
            ContextCompat.checkSelfPermission(
                this,
                it
            ) != PackageManager.PERMISSION_GRANTED
        } == null) return onResult(manifestPermissions.associateBy({ it }, { true }))
    registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
        onResult
    ).launch(manifestPermissions.toTypedArray())
}

/**
 * Checks if the given Permissions are granted.
 *
 * @param permissions to check from [Manifest.permission]
 *
 * @return true if all are granted, false if at least one is not granted
 */
fun Activity.hasPermission(vararg permissions: String) =
    permissions.find { ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED } == null

fun Activity.hideKeyboard() {
    val imm: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = currentFocus ?: View(this)
    imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

@RequiresApi(Build.VERSION_CODES.M)
@RequiresPermission("android.permission.ACCESS_NETWORK_STATE")
fun Context.isNetworkAvailable(): Boolean {
    val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
    return if (capabilities != null) {
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    } else false
}

fun Context.getScreenSize(): Size {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val metrics = windowManager.currentWindowMetrics
        val windowInsets = metrics.windowInsets
        val insets: Insets = windowInsets.getInsetsIgnoringVisibility(
            WindowInsets.Type.navigationBars()
                    or WindowInsets.Type.displayCutout()
        )

        val insetsWidth: Int = insets.right + insets.left
        val insetsHeight: Int = insets.top + insets.bottom
        val bounds: Rect = metrics.bounds
        Size(
            bounds.width() - insetsWidth,
            bounds.height() - insetsHeight
        )
    } else {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay?.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        Size(width, height)
    }
}

fun Context.windowManager() = ContextCompat.getSystemService(this, WindowManager::class.java)

fun Context.connectivityManager() =
    ContextCompat.getSystemService(this, ConnectivityManager::class.java)

fun Context.notificationManager() =
    ContextCompat.getSystemService(this, NotificationManager::class.java)

fun Context.downloadManager() = ContextCompat.getSystemService(this, DownloadManager::class.java)

/**
 * If true, this device has NFC support.
 */
fun Activity.hasNfc() = NfcAdapter.getDefaultAdapter(this) != null

/**
 * Opens the system settings from this app.
 * Here the user can edit permissions, cache, etc.
 */
fun Activity.openAppSystemSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.parse("package:$packageName")
    }
    startActivity(intent)
}

/**
 * Checks if the current instance was installed from Google Play or was installed through an APK directly.
 */
fun Context.installedFromGooglePlay(): Boolean {
    return try { // A list with valid installers package name
        val validInstallers = listOf("com.android.vending", "com.google.android.feedback")

        // The package name of the app that has installed your app
        val installer = packageManager.getInstallerPackageName(packageName)

        // true if your app has been downloaded from Play Store
        installer != null && validInstallers.contains(installer)
    } catch (e: Exception) {
        System.err.println(e.stackTraceToString())
        false
    }
}

/**
 * Vibrates for [timeMillis].
 * WARNING: requires the vibration Permission in your manifest!
 * <uses-permission android:name="android.permission.VIBRATE" />
 *
 * @param timeMillis the time, how long the device should vibrate. Note: Some devices don't support short times. Samsung won't vibrate below 30ms as far as I know!
 * @param strength the strength of the device vibration. Defaults to the default device vibration strength! Must between 1 and 255 oder -1 for the device default.
 */
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("MissingPermission")
fun Context.vibrate(
    timeMillis: Long = 50,
    @IntRange(from = 1, to = 255) strength: Int = VibrationEffect.DEFAULT_AMPLITUDE
) {
    val vibrator = getDefaultVibrator()

    vibrator.vibrate(
        VibrationEffect.createOneShot(
            timeMillis,
            strength
        )
    )
}

/**
 * Get the default vibrator from the System.
 * For simple vibration you may use [vibrate]. Otherwise, you may use a variant of [Vibrator.vibrate].
 */
fun Context.getDefaultVibrator() =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator else getSystemService(
        Context.VIBRATOR_SERVICE
    ) as Vibrator

/**
 * Copies the specified text to the clipboard.
 *
 * @param text The text to be copied to the clipboard.
 * @param label An optional label to be assigned to the copied text.
 */
fun Context.copyToClipboard(text: String, label: String = "") {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    clipboard?.setPrimaryClip(ClipData.newPlainText(label, text))
}

/**
 * Returns true if the device is in an interactive state.
 * When this method returns true, the device is awake and ready to interact with the user (although this is not a guarantee that the user is actively interacting with the device just this moment). The main screen is usually turned on while in this state. Certain features, such as the proximity sensor, may temporarily turn off the screen while still leaving the device in an interactive state. Note in particular that the device is still considered to be interactive while dreaming (since dreams can be interactive) but not when it is dozing or asleep.
 * When this method returns false, the device is dozing or asleep and must be awoken before it will become ready to interact with the user again. The main screen is usually turned off while in this state. Certain features, such as "ambient mode" may cause the main screen to remain on (albeit in a low power state) to display system-provided content while the device dozes.
 */
fun Context.isScreenOn() = (getSystemService(Context.POWER_SERVICE) as PowerManager).isInteractive

fun Context.openDisplaySettings() = tryOrNull { startActivity(Intent(Settings.ACTION_DISPLAY_SETTINGS)) } != null
fun Context.openWifiSettings() = tryOrNull { startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) } != null
fun Context.openBluetoothSettings() = tryOrNull { startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS)) } != null
fun Context.openAirplaneModeSettings() =
    tryOrNull { startActivity(Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS)) } != null

fun Context.openDateSettings() = tryOrNull { startActivity(Intent(Settings.ACTION_DATE_SETTINGS)) } != null
fun Context.openLocaleSettings() = tryOrNull { startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS)) } != null
fun Context.openInputMethodSettings() =
    tryOrNull { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) } != null

fun Context.openAccessibilitySettings() =
    tryOrNull { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) } != null

fun Context.isAutoRotationActive() =
    Settings.System.getInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION) == 1
