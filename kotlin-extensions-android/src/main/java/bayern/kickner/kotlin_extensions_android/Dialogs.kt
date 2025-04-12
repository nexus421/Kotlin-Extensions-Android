package bayern.kickner.kotlin_extensions_android

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * This will show a simple dialog with a timer.
 * The user can cancel or continue after the timer has finished.
 */
fun showDialogWithTimer(
    activity: ComponentActivity,
    title: String,
    message: String,
    timeInSeconds: Int = 5,
    cancelable: Boolean = false,
    textColor: Int = Color.BLACK,
    positiveButton: (View) -> Unit
) {

    val btn = Button(activity).apply {
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        setTextColor(Color.GRAY)
        setBackgroundColor(Color.TRANSPARENT)
    }

    val rootView = LinearLayoutCompat(activity).apply {
        orientation = LinearLayoutCompat.VERTICAL
        setPadding(16)
        addView(TextView(activity).apply {
            setPadding(16)
            setTextColor(textColor)
            text = message
            width = MATCH_PARENT
        })
        addView(LinearLayoutCompat(activity).apply {
            orientation = LinearLayoutCompat.HORIZONTAL
            gravity = Gravity.END
            addView(btn)
        })
    }

    activity.lifecycleScope.launch(Dispatchers.IO) {
        for (i in timeInSeconds downTo 0) {
            withContext(Dispatchers.Main) {
                btn.text = if (i == 0) {
                    btn.setOnClickListener(positiveButton)
                    btn.setTextColor(Color.RED)
                    "OK"
                } else "OK ($i)"
            }
            delay(1000)
        }
    }

    AlertDialog.Builder(activity).setTitle(title).setView(rootView).setCancelable(cancelable).show()
}
