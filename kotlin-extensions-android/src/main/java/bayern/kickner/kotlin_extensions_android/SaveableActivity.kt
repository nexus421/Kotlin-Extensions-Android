package bayern.kickner.kotlin_extensions_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import org.jetbrains.annotations.ApiStatus.Experimental
import java.io.Serializable

/**
 * This activity is a helper for the case that the system kills the app.
 * Via [addNewSaveableData] and [getSavedData] objects can be stashed away and restored later. The objects must be serializable.
 * Via the variable [activityWasRecreated] one can find out if the activity was recreated (true) or not (false)
 */
@Experimental
class SaveableActivity : ComponentActivity() {

    private val savedData = HashMap<String, Serializable>()
    var activityWasRecreated = false
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedData.clear()
        if (savedInstanceState?.getBoolean("activityKilled") == true) {
            savedData.putAll(savedInstanceState.getSerializable("saveable") as? Map<String, Serializable> ?: emptyMap())
            activityWasRecreated = true
        }
    }

    fun <T : Serializable> addNewSaveableData(key: String, value: T) {
        savedData[key] = value
    }

    fun <T : Serializable> getSavedData(key: String): T? = savedData[key]?.let { it as? T }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("saveable", savedData)
        outState.putBoolean("activityKilled", true)
    }

}




