package bayern.kickner.kotlin_extensions_android

import java.util.*
import java.util.concurrent.TimeUnit


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
