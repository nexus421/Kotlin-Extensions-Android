package bayern.kickner.kotlin_extensions_android

import android.graphics.Typeface
import android.view.View
import android.widget.EditText
import android.widget.TextView

/**
 * Get the Text from this EditText directly.
 */
fun EditText.getTextAsString() = text.toString()

/**
 * Does the same as [EditText.getTextAsString].
 */
val EditText.value
    get() = text?.toString() ?: ""

fun TextView.setBold(keepPreviousTypeface: Boolean = false) = setTypeface(if (keepPreviousTypeface) typeface else null, Typeface.BOLD)

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

fun View.makeInvisible() {
    visibility = View.INVISIBLE
}

/**
 * If [condition] is true, this View will be [View.VISIBLE] else [View.GONE]
 */
fun View.visibleIf(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.GONE
}

/**
 * If [condition] is true, this View will be [View.GONE] else [View.VISIBLE]
 */
fun View.goneIf(condition: Boolean) {
    visibility = if (condition) View.GONE else View.VISIBLE
}

/**
 * If [condition] is true, this View will be [View.INVISIBLE] else [View.VISIBLE]
 */
fun View.invisibleIf(condition: Boolean) {
    visibility = if (condition) View.INVISIBLE else View.VISIBLE
}
