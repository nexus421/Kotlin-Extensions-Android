package bayern.kickner.kotlin_extensions_android

import java.util.*

/**
 * Iterates from startAt until this with <=
 */
fun Int.forEach(startAt: Int = 0, doThis: (Int) -> Unit) {
    if(this <= startAt) return
    (startAt..this).forEach(doThis)
}

fun Long.getCalendarFromMillis(locale: Locale = Locale.getDefault()) = Calendar.getInstance(locale).setTimeMillis(this)