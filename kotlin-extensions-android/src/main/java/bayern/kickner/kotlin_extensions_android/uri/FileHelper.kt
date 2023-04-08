package bayern.kickner.kotlin_extensions_android.uri

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import java.io.File

object FileHelper {

    /**
     * You can read/write/delete everything inside this folder, as long you created the files/folders you want to access with your current app installation.
     * This files/folders will not be deleted after your app will be uninstalled or reinstalled. But you will lose any access to this files!
     *
     * This files/folders can bee seen and edited by the user.
     *
     * @return public documents file-path
     */
    fun getPublicDocumentsFolder() = Environment.getExternalStoragePublicDirectory("Documents")

    /**
     * You can read/write/delete everything inside this folder, as long you created the files/folders you want to access with your current app installation.
     * This files/folders will not be deleted after your app will be uninstalled or reinstalled. But you will lose any access to this files!
     *
     * This files/folders can bee seen and edited by the user.
     *
     * @return public download file-path
     */
    fun getPublicDownloadFolder() = Environment.getExternalStoragePublicDirectory("Download")

    /**
     * App internal directory which is only accessible through your app. This folder and all containing files will be deleted on app uninstall.
     * The files in this folder can only be viewed through your app.
     *
     * @return internal app-specific root-folder
     */
    fun getAppInternalDirectory(context: Context) = context.filesDir

    /**
     * App external directory which is only accessible through your app. This folder and all containing files will be deleted on app uninstall.
     * The files in this folder can only be viewed through your app.
     *
     * Hint: The user can see the files in this folder when connected to an computer. This is also possible on some devices through an file explorer.
     * Other apps can't access this files.
     *
     * @return external app-specific root-folder
     */
    fun getAppExternalDirectory(context: Context) = context.getExternalFilesDir(null)

    /**
     * List all files you created an have access to in the public non-app folders Documents and Download.
     * Recommended way is [listAllPublicAccessibleFiles]. Why? It works. I don't know why to use URI and contentResolver.
     *
     * See [getAllAccessibleFilesFromPublic]
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun listAllFilesStoredInNonAppPublicFolders(context: Context) = context.getAllAccessibleFilesFromPublic()

    /**
     * Lists all files/folders from the root of Documents and Download.
     * Only accessible (read/write) are listed. These are typically files you created.
     *
     * @return files created by your app but stored in public folders
     */
    fun listAllPublicAccessibleFiles(): List<File> {
        val filesInDownload = getPublicDownloadFolder()?.listFiles()?.mapNotNull { it } ?: emptyList()
        val filesInDocuments = getPublicDocumentsFolder()?.listFiles()?.mapNotNull { it } ?: emptyList()
        return filesInDocuments + filesInDownload
    }
}