package bayern.kickner.kotlin_extensions_android

import android.os.Handler
import android.os.Looper
import kotlin.system.measureTimeMillis


/**
 * Run anything everywhere on the main-thread!
 */
fun runOnUiThread(r: Runnable) = Handler(Looper.getMainLooper()).post(r)

/**
 * Measures the time like measureTimeMillis. But this Method can return anything with the Time for executing the block.
 */
fun <T> measureTimeMillisAndReturn(block: () -> T): ResultTimeMeasure<T> {
    val result: T
    val time = measureTimeMillis {
        result = block()
    }
    return ResultTimeMeasure(result, time)
}

data class ResultTimeMeasure<T>(val result: T, val timeMillis: Long)

/**
 * Executes action as long as not null is returned.
 * If action returns null, the callback onNull will be called.
 *
 * @return The first Object, which ist not null.
 */
inline fun <reified T> doUntilNotNull(noinline onNull: (() -> Unit)? = null, action: () -> T?): T {
    while (true) {
        action()?.let {
            return it
        } ?: onNull?.invoke()
    }
}








