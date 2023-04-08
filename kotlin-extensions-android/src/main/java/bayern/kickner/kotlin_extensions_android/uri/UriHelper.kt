package bayern.kickner.kotlin_extensions_android.uri

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import android.provider.OpenableColumns
import androidx.annotation.RequiresApi
import bayern.kickner.kotlin_extensions_android.ResultOf2
import bayern.kickner.kotlin_extensions_android.ifFalse
import java.io.File
import java.io.FileNotFoundException

/**
 * Reads the content from a file through the given URI.
 * Make sure, that you are allowed to read from this URI.
 *
 * @param context Context to get the contentResolver
 *
 * @return SUCCESS: Input as ByteArray, FAILURE: UriExtensionError
 */
fun Uri.readContentFromUri(context: Context): ResultOf2<ByteArray, UriExtensionsError> {
    try {
        val input = context.contentResolver.openInputStream(this)?.use { it.readBytes() } ?: return ResultOf2.Failure(UriExtensionsError.InputErrorProviderCrashed)
        return ResultOf2.Success(input)
    } catch (e: FileNotFoundException) {
        return ResultOf2.Failure(UriExtensionsError.FileNotFound)
    }
}

/**
 * Creates a file through the contentResolver. If the file exists, it will automatically added an increasing number to the file name. test.txt -> text (1).txt
 *
 * Important: As long as your App is installed, you have unrestricted access to this generated files. (Store the URI or find them with [getAllAccessibleFilesFromPublic])
 * If your app will be reinstalled, you will lose any access to all of those files! This files will not be deleted on app uninstall.
 *
 * @param path for the possible public folders
 * @param folderPath if you want sub folders, add them here. Do not start with a seperator. Example: "myFolder/mySecondFolder". Default is an empty String (no sub folders)
 * @param displayName filename including type. Example: "Test.txt"
 * @param mimeType for the file. Default "text/plain"
 *
 * @return SUCCESS: Uri for the created file. FAILURE: UriExtensionError
 */
@RequiresApi(Build.VERSION_CODES.Q)
fun Context.createFileInPublicAndWriteData(path: Path = Path.Documents, folderPath: String = "", displayName: String, mimeType: String? = null, bytesToWrite: ByteArray): ResultOf2<Uri, UriExtensionsError> {
    val uri = MediaStore.Files.getContentUri("external")
    val relativePath = "${path.name}${if(folderPath.isBlank()) "" else File.separator + folderPath}"
    val contentValues = ContentValues().apply {
        put(MediaColumns.DISPLAY_NAME, displayName)
        mimeType?.let { put(MediaColumns.MIME_TYPE, it) }
        put(MediaColumns.SIZE, bytesToWrite.size)
        put(MediaColumns.RELATIVE_PATH, relativePath)
        put(MediaColumns.IS_PENDING, 0) //Wenn 1, dann muss das am Schluss auf 0 gesetzt werden. Erst dann kann es vom System indiziert werden. Also immer bei 0 lassen :D
    }

    val fileUri = contentResolver.insert(uri, contentValues) ?: return ResultOf2.Failure(UriExtensionsError.InsertionFailed)
    fileUri.writeDataToFile(this, bytesToWrite).ifFalse { return ResultOf2.Failure(UriExtensionsError.WriteFailed) }
    return ResultOf2.Success(fileUri)
}

fun Context.deleteFileThroughUri(uri: Uri) = contentResolver.delete(uri, null, null)

/**
 * Writes some data to the given URI.
 * Make sure, that you are allowed to write to this uri
 *
 * @param context Context to get the contentResolver
 * @param data to write to the file
 *
 * @return true if successful otherwise false
 */
fun Uri.writeDataToFile(context: Context, data: ByteArray) :Boolean {
    context.contentResolver.openOutputStream(this)?.use {
        it.write(data)
    } ?: return false
    return true
}

/**
 * This will search and return a list with all files you created through contentResolver.insert like [createFileInPublicAndWriteData] does.
 * Hint: This will only return files, stored in [Path]. App specific files (internal and external) will not be found here.
 *
 * @return List with [AndroidFile]
 */
@RequiresApi(Build.VERSION_CODES.Q)
fun Context.getAllAccessibleFilesFromPublic(): ResultOf2<List<AndroidFile>, List<UriExtensionsError>> {
    val uri = MediaStore.Files.getContentUri("external")
    val files = mutableListOf<AndroidFile>()
    val error = mutableListOf<UriExtensionsError>()
    //Das Array mit den Spaltennamen ist night notwendig. Dadurch wird aber der query effizienter/schneller und es sind dann auch nur diese Columns verfügbar mit Daten.
    //Werden weitere Columns benötigt, entweder hier hinzufügen oder das array komplett entfernen, um alle Infos zur Verfügung zu haben.
    contentResolver.query(uri, arrayOf(BaseColumns._ID, OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE, MediaColumns.RELATIVE_PATH), null, null)?.use { cursor ->
        cursor.moveToFirst()
        do {
            val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            val uriId = cursor.getColumnIndex(BaseColumns._ID)
            val relativePathId = cursor.getColumnIndex(MediaColumns.RELATIVE_PATH)
            if(displayNameIndex < 0 || sizeIndex < 0 || uriId < 0 || relativePathId < 0) {
                error.add(UriExtensionsError.InvalidColumnIndex)
                continue
            }
            val displayName = cursor.getString(displayNameIndex)
            val (name, type) = displayName.let {
                val indexToSplit = it.lastIndexOf(".")
                if(indexToSplit == -1 ) Pair(it, "")
                else Pair(it.substring(0, indexToSplit), it.substring(indexToSplit))
            }
            val sizeInBytes = cursor.getLong(sizeIndex)
            val fileUri = ContentUris.withAppendedId(uri, cursor.getLong(uriId))
            val relativePaths = cursor.getString(relativePathId)
            files.add(AndroidFile(fileUri, name, type, sizeInBytes, relativePaths))
        } while (cursor.moveToNext())
    }

    return if(error.isEmpty() || files.isNotEmpty()) ResultOf2.Success(files) else ResultOf2.Failure(error)
}

//ToDo: getAllAccessableFiles --> Search for specific file through name and or extension

