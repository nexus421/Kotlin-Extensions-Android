package bayern.kickner.kotlin_extensions_android

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

data class FileInfo(val name: String, val type: String, val sizeInBytes: Long)
data class FileInfoLarge(val name: String, val type: String, val sizeInBytes: Long, val fileInput: ByteArray)

/**
 * Copy a file from the given URI to another file.
 * You need full write access to the file, otherwise this will fail.
 *
 * As this uses the JVM, we are limited due to the JVM-Heap-Size. Keep that in mind for large files.
 *
 * @param contentResolver Current contentResolver from Context to load the file
 * @param fileDestination File to write the copy to
 * @param fileCheck if you want, you can check the file (name, type, size) before copy it. default: null (no checks)
 *
 * @return true if succes, false for error and null if the check was not successfull
 */
fun Uri.copyUriToFile(contentResolver: ContentResolver, fileDestination: File, fileCheck: ((FileInfo) -> Boolean)? = null): Boolean? {
    val bytes = contentResolver.openInputStream(this)?.use { it.readBytes() } ?: return false
    val (name, type, sizeInBytes) = contentResolver.query(this, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        cursor.moveToFirst()
        val (name, type) = cursor.getString(nameIndex).let {
            val indexToSplit = it.lastIndexOf(".")
            Pair(it.substring(0, indexToSplit), it.substring(indexToSplit))
        }
        val sizeInBytes = cursor.getLong(sizeIndex)
        Triple(name, type, sizeInBytes)
    } ?: return false

    return if(fileCheck == null || fileCheck(FileInfo(name, type, sizeInBytes))) {
        fileDestination.writeBytes(bytes)
        true
    } else null
}

/**
 * Reads the input from a URI.
 *
 * @param contentResolver Current contentResoler from Context to load the file
 * @param fileCheck if you want, you can check the file (name, type, size) before copy it. default: null (no checks)
 *
 * As this uses the JVM, we are limited due to the JVM-Heap-Size. Keep that in mind for large files.
 *
 * @return ResultOf: Success contains FileInfoLarge with file infos and the data as ByteArray. Failure contains the failing result as String
 */
fun Uri.loadDataFromUri(contentResolver: ContentResolver, fileCheck: ((FileInfo) -> Boolean)? = null): ResultOf<FileInfoLarge> {
    val bytes = contentResolver.openInputStream(this)?.use { it.readBytes() } ?: return ResultOf.Failure("Error while readin the file. NULL")
    val (name, type, sizeInBytes) = contentResolver.query(this, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        cursor.moveToFirst()
        val (name, type) = cursor.getString(nameIndex).let {
            val indexToSplit = it.lastIndexOf(".")
            Pair(it.substring(0, indexToSplit), it.substring(indexToSplit))
        }
        val sizeInBytes = cursor.getLong(sizeIndex)
        Triple(name, type, sizeInBytes)
    } ?: return ResultOf.Failure("Error while reading file informations. NULL")

    return if(fileCheck == null || fileCheck(FileInfo(name, type, sizeInBytes))) {
        ResultOf.Success(FileInfoLarge(name, type, sizeInBytes, bytes))
    } else ResultOf.Failure("FileCheck was not successfull")

}
