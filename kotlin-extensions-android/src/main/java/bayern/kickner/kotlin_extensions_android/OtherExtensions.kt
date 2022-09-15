package bayern.kickner.kotlin_extensions_android

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.core.content.FileProvider
import java.io.File


/**
 * This gets the name of the current Class.
 * I use this for my Logs. So I always have an identical TAG to use
 */
val Any.TAG: String
    get() = javaClass.simpleName

/**
 * Ruft eine Methode auf, ohne die Methode direkt zu kennen.
 * Sucht die Methode anhand des methodName.
 * Diese Methode wird dann ausgeführt und das Ergebnis wird dann nach D geparst und returned.
 *
 * @param methodName Name der Methode die aufgerufen werden soll.
 * @param args Argumente, falls die aufzurufende Methode noch Parameter besitzt
 * @param D Rückgabewert der aufzurufenden Methode
 * @return Den Rückgabewert der aufgerufenen Methode oder null
 */
fun <D> Any.callMethodByName(methodName: String, vararg args: Any?) = this.javaClass.getMethod(methodName).let { if (args.isEmpty()) it.invoke(this) else it.invoke(this, *args) } as? D?

/**
 * Castet this in C. Force-Cast. Wenn es fehlschlägt, gibt es eine Exception
 */
inline fun <reified C> Any.cast(): C = this as C

/**
 * Castet this in C. Wenn der Cast fehlschlägt, ist das Ergebnis null.
 */
inline fun <reified C> Any.safeCast(): C? = this as? C

fun Drawable.drawableToBitmap(): Bitmap {
    if (this is BitmapDrawable && bitmap != null) return bitmap

    val bitmap = if (intrinsicWidth <= 0 || intrinsicHeight <= 0) Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    else Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)

    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}

/**
 * Iterates this Iterable and calls action.
 * If the current iteration is the last one, the Boolean will be true, false otherwise.
 *
 * @param action Callback for each iteration. T == Element, Boolean == IsLastElement
 */
inline fun <T> Iterable<T>.forEachDoLast(action: (T, Boolean) -> Unit) {
    val end = count()
    forEachIndexed { index, t ->
        action(t, index == end - 1)
    }
}


fun Context.inflate(@LayoutRes layoutId: Int) = View.inflate(this, layoutId, null)

inline fun <reified C, R> Any.letCast(block: (C) -> R): R = (this as C).let(block)

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
 * Sucht für eine Variable den Getter und gibt das Ergebnis für den Getter zurück.
 * Das Ergebnis wird sofort nach D geparst. Bei einem Fehler wird null returned.
 *
 * Wichtig: Es muss einen public Getter geben!
 *
 * @param varName Name der Variable nach dessen Getter im aktuellen Objekt gesucht werden soll
 * @return Ergebnis des Getters. Null kann das Ergebnis des Getters, aber auch ein Fehler sein!
 */
fun <D> Any.get(varName: String): D? {
    val getterName = "get${varName.replace(".", "").replaceFirstChar { it.toString().uppercase() }}"
    return callMethodByName<D>(getterName)
}

/**
 * Sucht für eine Variable den Setter und führt diesen mit dem entsprechenden valueToSet aus.
 *
 * Wichtig: Es muss einen public Setter geben!
 *
 * @param varName Name der Variable nach dessen Setter im aktuellen Objekt gesucht werden soll
 * @param valueToSet Dieser Wert wird über den Setter gesetzt.
 * @return Nichts, wie ein Setter eben auch
 */
fun <D> Any.set(varName: String, valueToSet: D) {
    val setterName = "set${varName.replace(".", "").replaceFirstChar { it.toString().uppercase() }}"
    callMethodByName<D>(setterName, valueToSet)
}
