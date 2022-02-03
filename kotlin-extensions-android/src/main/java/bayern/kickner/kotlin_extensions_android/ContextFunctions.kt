package bayern.kickner.kotlin_extensions_android

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED


fun Context.isActivity() = this is Activity

/**
 * Eine Aktion dieses Objekts kann auf dem Main-Thread ausgeführt werden. Häufig nötig für GUI-Zugriffe.
 */
fun Context.showToastOnMainThread(s: String, length: Int = Toast.LENGTH_LONG) = runOnUiThread { Toast.makeText(this, s, length).show() }

fun Activity.getRootView() = findViewById<View>(android.R.id.content).rootView

fun Activity.startActivityAndFinish(destination: Class<*>) {
    startActivity(Intent(this, destination))
    finish()
}

fun Activity.getNewDialog() = AlertDialog.Builder(this)

fun AppCompatActivity.getNewCompatDialog() = androidx.appcompat.app.AlertDialog.Builder(this)

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