package bayern.kickner.kotlin_extensions_android

import android.os.Handler
import android.os.Looper


/**
 * Run anything everywhere on the main-thread!
 */
fun runOnUiThread(r: Runnable) = Handler(Looper.getMainLooper()).post(r)
