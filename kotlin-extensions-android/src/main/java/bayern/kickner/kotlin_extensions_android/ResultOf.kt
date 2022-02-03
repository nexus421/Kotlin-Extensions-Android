package bayern.kickner.kotlin_extensions_android

/**
 * Kotlin soll keine Exceptions direkt verwenden. Wenn es einen Fehler gibt, kann dafür einfach diese
 * sealed Class verwendet werden. Im Code kann dann geprüft werden, ob ResultOf is Success or Failure.
 *
 * Siehe: https://medium.com/swlh/kotlin-sealed-class-for-success-and-error-handling-d3054bef0d4e
 */
sealed class ResultOf<out T> {
    data class Success<out R>(val value: R): ResultOf<R>()
    data class Failure(val message: String?, val throwable: Throwable?): ResultOf<Nothing>()
}

