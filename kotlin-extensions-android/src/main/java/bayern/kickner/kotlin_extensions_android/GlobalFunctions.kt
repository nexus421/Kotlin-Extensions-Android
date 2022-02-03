package bayern.kickner.kotlin_extensions_android

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Material Button Library ist komplett verbuggt. Background wird ignoriert. Mit diesem
 * Workaround funktioniert es aber trotzdem.
 * https://github.com/material-components/material-components-android/issues/889#issuecomment-708160551
 */
fun Activity.workaroundSetBackgroundToButton(btnIDs: Array<Int>, @DrawableRes drawableRes: Int) {
    btnIDs.forEach {
        val btn = findViewById<Button>(it)
        btn.background = ContextCompat.getDrawable(this, drawableRes)
        btn.backgroundTintList = null
    }
}

/**
 * Material Button Library ist komplett verbuggt. Background wird ignoriert. Mit diesem
 * Workaround funktioniert es aber trotzdem.
 * https://github.com/material-components/material-components-android/issues/889#issuecomment-708160551
 */
fun Button.workaroundSetBackgroundToButton(@DrawableRes drawableRes: Int): Button {
    background = ContextCompat.getDrawable(context, drawableRes)
    backgroundTintList = null
    return this
}

fun Date.toCalendar(locale: Locale = Locale.getDefault()) =
    Calendar.getInstance(locale).setTimeMillis(time)

fun Any.runOnUiThread(r: Runnable) = Handler(Looper.getMainLooper()).post(r)

fun EditText.getTextAsString() = text.toString()

fun Pair<Calendar, Calendar>.calculateTimeDiffInMinutes(): Long {
    return TimeUnit.MILLISECONDS.toMinutes((second.time.time ?: 0) - (first.time.time ?: 0))
}

infix fun Date.addMillis(millis: Long) {
    time += millis
}


/**
 * Der übergebene String wird, abgesehen vom ersten und letzten Zeichen, durch Sternchen ersetzt.
 * Siehe Dokumentation -> [.coverString]
 * Bsp. Hallo wird zu H***o
 *
 * @param s String der verschleiert werden soll
 * @return verschleierten String oder leerer String falls s == null
 */
fun String.coverString() = coverString(1, this.length - 2)

/**
 * Der übergebene String wird zwischen start und end durch Sternchen ersetzt.
 * Dient zur Anonymisierung eines Strings
 *
 * @param s     String der verschleiert werden soll
 * @param start ab hier werden Sternchen eingefügt
 * @param end   bis hier werden Sternchen eingefügt
 * @return verschleierten String
 */
fun String.coverString(@IntRange(from = 0) start: Int, @IntRange(from = 1) end: Int): String {
    val length = this.length
    if (length < 1) return this
    require(!(end >= length || start < 0)) {
        "Start or end Argument is not allowed! start = $start end = $end"
    }
    val result = toCharArray()
    for (i in 0 until length) {
        if (i in start..end) {
            result[i] = '*'
        }
    }
    return String(result)
}

/**
 * Ersetzt if/else
 *
 * @param isNull Wird aufgerufen, wenn der Caller null ist
 * @param notNull Wird aufgerufen, wenn der Caller nicht null ist. Das Objekt kann dann sicher als notNull verwendet werden.
 */
inline fun <T> T?.ifNull(
    isNull: () -> Unit,
    notNull: T.() -> Unit,
    ){
    if(this == null) isNull()
    else notNull()
}

inline fun <T> T?.ifNotNull(
    thisObj: T.() -> Unit,
){
    if(this != null) thisObj()
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


inline fun String?.ifNullOrBlank(action: () -> Unit) {
    if(isNullOrBlank()) action()
}

inline fun String?.ifNotNullOrBlank(action: String.() -> Unit) {
    if(!isNullOrBlank()) action()
}

fun String?.isNotNullOrBlank() = !isNullOrBlank()

fun <T> T?.isNull() = this == null
fun <T> T?.isNotNull() = this != null

/**
 * Default-Wert angeben. Wenn ein Objekt null sein kann, kann damit ein default-Wert angegeben werden.
 * @return Default-Wert, wenn this == null
 */
fun <T> T?.default(value: T) = this ?: value

fun String.withNewLine() = this + "\n"

val Any.TAG: String
    get() = javaClass.simpleName

fun String.md5(): String {
    return hashString(this, "MD5")
}

fun String.sha256(): String {
    return hashString(this, "SHA-256")
}

private fun hashString(input: String, algorithm: String): String {
    return MessageDigest
        .getInstance(algorithm)
        .digest(input.toByteArray())
        .fold("") { str, it -> str + "%02x".format(it) }
}

/**
 * Ruft eine Methode auf, ohne die Methode direkt zu kennen.
 * Sucht die Methode anhand des methodName.
 * Diese Methode wird dann ausgeführt und das Ergebnis wird dann nach D geparst und returned.
 *
 * @param methodName Name der Methode die aufgerufen werden soll.
 * @param args Argumente, falls die aufzurufende Methode noch Parameter besitzt
 * @param D Rückgabewert der aufzurufenden Methode
 * @return Den Rückgabewert der aufgerufenen Methode oder null
 */
fun <D> Any.callMethodByName(methodName: String, vararg args: Any?) = this.javaClass.getMethod(methodName).let { if (args.isEmpty()) it.invoke(this) else it.invoke(this, args) } as? D?


fun TextView.setBold(keepPreviousTypeface: Boolean = false) = setTypeface(if (keepPreviousTypeface) typeface else null, Typeface.BOLD)

