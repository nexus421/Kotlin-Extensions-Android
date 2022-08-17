package bayern.kickner.kotlin_extensions_android

import android.graphics.Typeface
import android.widget.EditText
import android.widget.TextView

fun EditText.getTextAsString() = text.toString()

fun TextView.setBold(keepPreviousTypeface: Boolean = false) = setTypeface(if (keepPreviousTypeface) typeface else null, Typeface.BOLD)