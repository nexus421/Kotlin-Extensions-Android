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






