package bayern.kickner.kotlin_extensions_android

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable


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
fun <D> Any.callMethodByName(methodName: String, vararg args: Any?) = this.javaClass.getMethod(methodName).let { if (args.isEmpty()) it.invoke(this) else it.invoke(this, args) } as? D?

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
