package bayern.kickner.kotlin_extensions_android

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit


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

fun Date.toCalendar(locale: Locale = Locale.getDefault()) = Calendar.getInstance(locale).setTimeMillis(time)

/**
 * If you have a Pair with two Calendars in it, you can easily calculate the difference in milliseconds with this method.
 */
fun Pair<Calendar, Calendar>.calculateTimeDiffInMinutes(): Long {
    return TimeUnit.MILLISECONDS.toMinutes((second.time.time ?: 0) - (first.time.time ?: 0))
}

infix fun Date.addMillis(millis: Long) {
    time += millis
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalTime.toHHMM() = format(DateTimeFormatter.ofPattern("HH:mm"))