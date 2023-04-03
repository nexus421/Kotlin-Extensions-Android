package bayern.kickner.kotlin_extensions_android

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.content.edit

@RequiresApi(Build.VERSION_CODES.Q)
object StorageHelper {

    private const val SP_STORAGE_HELPER = "storageHelper"

    /**
     * Writes a file to the download folder from this device.
     * The so generated URI grants a lifetime access to the file. This method stores the URI in the shared preferences for you to access the file again.
     * The key is for the shared preferences is the given filename
     *
     * @param context Context
     * @param filename filename for the file to write
     * @param mimeType MimeType for the input
     * @param input Input to write to the file
     */
    fun writeFileAndStore(context: Context, filename: String, mimeType: String = "text/plain", input: String): ResultOf<Uri> {
        return when (val result = writeFile(context, filename, mimeType, input)) {
            is ResultOf.Failure -> result
            is ResultOf.Success -> {
                context.getSharedPreferences(SP_STORAGE_HELPER, Context.MODE_PRIVATE).edit {
                    putString(filename, result.value.toString())
                }
                result
            }
        }
    }

    fun writeFile(context: Context, filename: String, mimeType: String = "text/plain", input: String) = StorageHelper.writeFile(context, filename, mimeType, input.toByteArray())

    fun writeFile(context: Context, filename: String, mimeType: String = "text/plain", input: ByteArray): ResultOf<Uri> {
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, filename)
            put(MediaStore.Downloads.MIME_TYPE, mimeType)
            put(MediaStore.Downloads.IS_PENDING, 1) //Damit in der Zwischenzeit niemand anderes auf die Datei zugreift.
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues) ?: return ResultOf.Failure("null if the underlying content provider returns null, or if it crashes", null)
        resolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(input)
        }
        contentValues.clear()
        contentValues.put(MediaStore.Downloads.IS_PENDING, 0) //Input fertig geschrieben, Datei freigeben
        resolver.update(uri, contentValues, null, null)

        return ResultOf.Success(uri)
    }

    fun shareUsableUriWithOtherApp(activity: Activity, mimeType: String = "text/plain", uri: Uri){
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = mimeType
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        //ToDo: Andere App über Intent aufrufen.
        activity.startActivity(Intent.createChooser(intent, "Share PDF"))
    }

    fun readFileByUri(context: Context, uri: Uri): String? {
        return context.contentResolver.openInputStream(uri)?.use {
            it.bufferedReader().readText()
        }
    }

    fun readFileByStoredFilename(context: Context, filename: String): String? {
        return getUriForStoredFile(context, filename)?.let {
            readFileByUri(context, it)
        }
    }

    /**
     * Uri for stored filename or null if not exist
     */
    fun getUriForStoredFile(context: Context, filename: String): Uri? {
        val uri = context.getSharedPreferences(SP_STORAGE_HELPER, Context.MODE_PRIVATE).getString(filename, null) ?: return null
        return Uri.parse(uri)
    }

    fun getAllStoredURIs(context: Context) = context.getSharedPreferences(SP_STORAGE_HELPER, Context.MODE_PRIVATE).all.mapValues { it.value.toString() }

    fun removeStoredUri(context: Context, filename: String) = context.getSharedPreferences(SP_STORAGE_HELPER, Context.MODE_PRIVATE).edit {
        remove(filename)
    }


}