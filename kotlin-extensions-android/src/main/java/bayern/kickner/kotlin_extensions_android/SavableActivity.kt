package bayern.kickner.kotlin_extensions_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import org.jetbrains.annotations.ApiStatus.Experimental
import java.io.Serializable

/**
 * This activity is a helper for the case that the system kills the app.
 * Via [SavableActivity] and [getSavedData] objects can be stashed away and restored later. The objects must be serializable.
 * Via the variable [activityWasRecreated] one can find out if the activity was recreated (true) or not (false)
 */
@Experimental
class SavableActivity : ComponentActivity() {

    private val savedData = HashMap<String, Serializable>()
    var activityWasRecreated = false
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState?.getBoolean("activityKilled") == true) {
            savedData.clear()
            savedData.putAll(savedInstanceState.getSerializable("saveable") as? Map<String, Serializable> ?: emptyMap())
            activityWasRecreated = true
        }
    }

    fun <T : Serializable> addNewSavableData(key: String, value: T) {
        savedData[key] = value
    }

    fun <T : Serializable> getSavedData(key: String): T? = savedData[key]?.let { it as? T }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("savable", savedData)
        outState.putBoolean("activityKilled", true)
    }

}




