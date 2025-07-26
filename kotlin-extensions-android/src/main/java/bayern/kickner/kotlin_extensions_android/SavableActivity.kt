package bayern.kickner.kotlin_extensions_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import java.io.Serializable

/**
 * This activity is a helper for the case that the system kills the app.
 * Via [SavableActivity] and [getSavedData] objects can be stashed away and restored later. The objects must be serializable.
 * Via the variable [activityWasRecreated] one can find out if the activity was recreated (true) or not (false)
 */
@Deprecated("Use SavableActivitySerializable or SavableActivityJson instead.")
class SavableActivity<T : Serializable> : ComponentActivity() {

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

abstract class SavableActivitySerializable<T : Serializable> : ComponentActivity(), ISavableActivitySerializable<T> {

    /**
     * This field will be filled if the activity was recreated and you stored some data through [onSaveData]
     */
    var loadedSavableData: T? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadedSavableData = savedInstanceState?.getSerializable("savable_serial") as? T
        loadedSavableData?.let { onRestoreData(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("savable_serial", onSaveData())
    }
}

abstract class SavableActivityJson : ComponentActivity(), ISavableActivityJson<String> {
    /**
     * This field will be filled if the activity was recreated and you stored some data through [onSaveData]
     */
    var loadedSavableData: String? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.getString("savable_json")?.let { rawJson ->
            loadedSavableData = rawJson
            loadedSavableData?.let { onRestoreData(it) }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("savable_json", onSaveData())
    }
}

interface ISavableActivitySerializable<T : Serializable> : ISavableActivity<T>

interface ISavableActivityJson<T> : ISavableActivity<T>

interface ISavableActivity<T> {

    /**
     * Return all Data that need to be saved.
     * That will be called when the activity is destroyed.
     */
    fun onSaveData(): T

    /**
     * Will be called when the activity is recreated and data was stored before.
     *
     * @param restoredData The data that was stored before through [onSaveData].
     */
    fun onRestoreData(restoredData: T)
}




