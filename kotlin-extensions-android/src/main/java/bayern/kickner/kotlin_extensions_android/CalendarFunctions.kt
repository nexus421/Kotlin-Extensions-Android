package bayern.kickner.kotlin_extensions_android

import java.text.SimpleDateFormat
import java.util.*


fun Calendar.setTimeMillis(time: Long): Calendar {
    timeInMillis = time
    return this
}

/**
 * Prüft, ob es sich um den heutigen Tag handelt
 */
fun Calendar.isToday(locale: Locale = Locale.getDefault()): Boolean {
    val today = Calendar.getInstance(locale)
    return get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH) &&
            get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
            get(Calendar.YEAR) == today.get(Calendar.YEAR)
}

fun Calendar.isSameDay(calendar: Calendar) = dayOfYear() == calendar.dayOfYear() && year() == calendar.dayOfYear()

fun Calendar.year() = get(Calendar.YEAR)

fun Calendar.month() = get(Calendar.MONTH)

fun Calendar.dayOfMonth() = get(Calendar.DAY_OF_MONTH)

fun Calendar.dayOfYear() = get(Calendar.DAY_OF_YEAR)

fun Date.format(pattern: String = "dd.MM.yyyy"): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(this)
}

fun String.toDate(pattern: String  = "dd.MM.yyyy"): Date? {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return try {
        sdf.parse(this)
    } catch (e: Exception){
        null
    }
}

fun getCalendarFromMillis(millis: Long, locale: Locale = Locale.GERMANY) = Calendar.getInstance(locale).setTimeMillis(millis)