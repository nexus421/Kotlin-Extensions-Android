package bayern.kickner.kotlin_extensions_android

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import java.io.File
import java.nio.file.Files


fun Drawable.drawableToBitmap(): Bitmap {
    if (this is BitmapDrawable && bitmap != null) return bitmap

    val bitmap = if (intrinsicWidth <= 0 || intrinsicHeight <= 0) Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    else Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)

    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}

fun ComponentActivity.takePicture(destinationUri: Uri, onResult: (Boolean) -> Unit) {
    registerForActivityResult(ActivityResultContracts.TakePicture()) {
        onResult(it)
    }.launch(destinationUri)
}

fun ComponentActivity.takePicture(destinationFile: File, fileProvider: String, onResult: (Boolean, Uri) -> Unit) {
    val fileUri = FileProvider.getUriForFile(this, fileProvider, destinationFile)
    registerForActivityResult(ActivityResultContracts.TakePicture()) {
        onResult(it, fileUri)
    }.launch(fileUri)
}


/**
 * File-size in Bytes, depending on used OS-Version
 */
fun File.getSize() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    Files.size(toPath())
} else {
    length()
}



