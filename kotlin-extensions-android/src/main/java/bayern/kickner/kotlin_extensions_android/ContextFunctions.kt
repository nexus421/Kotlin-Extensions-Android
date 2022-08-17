package bayern.kickner.kotlin_extensions_android

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import com.google.android.material.snackbar.Snackbar


fun Context.isActivity() = this is Activity

/**
 * Eine Aktion dieses Objekts kann auf dem Main-Thread ausgeführt werden. Häufig nötig für GUI-Zugriffe.
 */
fun Context.showToastOnMainThread(s: String, length: Int = Toast.LENGTH_LONG) = runOnUiThread { Toast.makeText(this, s, length).show() }

fun Activity.getRootView() = findViewById<View>(android.R.id.content).rootView

fun Activity.startActivityAndFinishCurrent(destination: Class<*>, intentExtras: (Intent.() -> Unit)? = null) {
    val i = Intent(this, destination)
    intentExtras?.let { it(i) }
    startActivity(i)
    finish()
}

fun Context.showToast(msg: String, length: Int = Toast.LENGTH_LONG) = Toast.makeText(this, msg, length).show()

fun Context.hasCameraPermission() = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PERMISSION_GRANTED

fun Context.hasFineLocationPermission() = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED

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

fun Activity.successSnackbar(msg: String, timeLong: Boolean = true) {
    Snackbar.make(window.decorView.findViewById(android.R.id.content), msg, if(timeLong) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT)
        .setBackgroundTint(Color.parseColor("#0FCA3A"))
        .show()
}

fun Activity.errorSnackbar(msg: String, timeLong: Boolean = true) {
    Snackbar.make(window.decorView.findViewById(android.R.id.content), msg, if(timeLong) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT)
        .setBackgroundTint(Color.parseColor("#D21A1A"))
        .show()
}

fun Activity.infoSnackbar(msg: String, timeLong: Boolean = true) {
    Snackbar.make(window.decorView.findViewById(android.R.id.content), msg, if(timeLong) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT)
        .setBackgroundTint(Color.parseColor("#1985DA"))
        .show()
}

fun Activity.warningSnackbar(msg: String, timeLong: Boolean = true) {
    Snackbar.make(window.decorView.findViewById(android.R.id.content), msg, if(timeLong) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT)
        .setBackgroundTint(Color.parseColor("#E09C24"))
        .show()
}

fun Activity.snackbar(msg: String, timeLong: Boolean = true, @ColorInt backgroundColor: Int = Color.parseColor("#E09C24")): Snackbar {
    return Snackbar.make(window.decorView.findViewById(android.R.id.content), msg, if(timeLong) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT)
        .setBackgroundTint(backgroundColor)

}