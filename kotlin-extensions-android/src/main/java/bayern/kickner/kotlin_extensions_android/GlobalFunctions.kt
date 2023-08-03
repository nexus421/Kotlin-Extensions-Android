package bayern.kickner.kotlin_extensions_android

import android.app.Activity
import android.nfc.NfcAdapter
import android.os.Handler
import android.os.Looper
import kotlin.system.measureTimeMillis


/**
 * Run anything everywhere on the main-thread!
 */
fun runOnUiThread(r: Runnable) = Handler(Looper.getMainLooper()).post(r)

/**
 * If true, this device has NFC support.
 */
fun Activity.hasNfc() = NfcAdapter.getDefaultAdapter(this) != null