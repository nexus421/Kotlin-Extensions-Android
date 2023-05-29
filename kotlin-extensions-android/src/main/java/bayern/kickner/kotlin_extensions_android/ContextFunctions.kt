package bayern.kickner.kotlin_extensions_android

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Insets
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.DisplayMetrics
import android.util.Size
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import com.google.android.material.snackbar.Snackbar


fun Context.isActivity() = this is Activity

/**
 * Eine Aktion dieses Objekts kann auf dem Main-Thread ausgeführt werden. Häufig nötig für GUI-Zugriffe.
 */
fun Context.showToastOnMainThread(s: String, length: Int = Toast.LENGTH_LONG) = runOnUiThread { Toast.makeText(this, s, length).show() }

fun Activity.getRootView() = findViewById<View>(android.R.id.content).rootView

fun Activity.startActivity(destination: Class<*>, finishCallingActivity: Boolean = false, intentExtras: (Intent.() -> Unit)? = null) {
    val i = Intent(this, destination)
    intentExtras?.invoke(i)
    startActivity(i)
    if (finishCallingActivity) finish()
}

fun Context.showToast(msg: String, length: Int = Toast.LENGTH_LONG) = Toast.makeText(this, msg, length).show()

fun Context.hasCameraPermission() = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PERMISSION_GRANTED

fun Context.hasFineLocationPermission() = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED

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

@Deprecated("Use View.snackbar")
fun Activity.successSnackbar(msg: String, timeLong: Boolean = true) {
    Snackbar.make(window.decorView.findViewById(android.R.id.content), msg, if (timeLong) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT)
        .setBackgroundTint(Color.parseColor("#0FCA3A"))
        .show()
}

@Deprecated("Use View.snackbar")
fun Activity.errorSnackbar(msg: String, timeLong: Boolean = true) {
    Snackbar.make(window.decorView.findViewById(android.R.id.content), msg, if (timeLong) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT)
        .setBackgroundTint(Color.parseColor("#D21A1A"))
        .show()
}

@Deprecated("Use View.snackbar")
fun Activity.infoSnackbar(msg: String, timeLong: Boolean = true) {
    Snackbar.make(window.decorView.findViewById(android.R.id.content), msg, if (timeLong) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT)
        .setBackgroundTint(Color.parseColor("#1985DA"))
        .show()
}

@Deprecated("Use View.snackbar")
fun Activity.warningSnackbar(msg: String, timeLong: Boolean = true) {
    Snackbar.make(window.decorView.findViewById(android.R.id.content), msg, if (timeLong) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT)
        .setBackgroundTint(Color.parseColor("#E09C24"))
        .show()
}

@Deprecated("Use View.snackbar")
fun Activity.snackbar(msg: String, timeLong: Boolean = true, @ColorInt backgroundColor: Int = Color.parseColor("#E09C24")): Snackbar {
    return Snackbar.make(window.decorView.findViewById(android.R.id.content), msg, if (timeLong) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT)
        .setBackgroundTint(backgroundColor)
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
    if (ContextCompat.checkSelfPermission(this, manifestPermission) == PackageManager.PERMISSION_GRANTED) return onResult(true)
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
fun ComponentActivity.checkAndRequestPermissions(manifestPermissions: List<String>, onResult: (Map<String, Boolean>) -> Unit) {
    if (manifestPermissions.find { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED } == null) return onResult(manifestPermissions.associateBy({ it }, { true }))
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions(), onResult).launch(manifestPermissions.toTypedArray())
}

/**
 * Checks if the given Permissions are granted.
 *
 * @param permissions to check from [Manifest.permission]
 *
 * @return true if all are granted, false if at least one is not granted
 */
fun Activity.hasPermission(vararg permissions: String) = permissions.find { ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED } == null

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

fun Context.connectivityManager() = ContextCompat.getSystemService(this, ConnectivityManager::class.java)

fun Context.notificationManager() = ContextCompat.getSystemService(this, NotificationManager::class.java)

fun Context.downloadManager() = ContextCompat.getSystemService(this, DownloadManager::class.java)
