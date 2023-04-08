package bayern.kickner.kotlin_extensions_android.uri

import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File

/**
 * Used to store importan file infos
 *
 * @param uri to access this file
 * @param filename filename without type
 * @param filetype starting with a dot like .txt
 * @param fileSizeInBytes size of this file in Bytes
 */
data class AndroidFile(val uri: Uri, val filename: String, val filetype: String, val fileSizeInBytes: Long, val relativePath: String) {

    /**
     * Shortcut for [readContentFromUri]
     */
    fun readFile(context: Context) = uri.readContentFromUri(context)

    fun toFile(): File = File(Environment.getExternalStorageDirectory().absolutePath+ "/$relativePath/$filename$filetype")
}