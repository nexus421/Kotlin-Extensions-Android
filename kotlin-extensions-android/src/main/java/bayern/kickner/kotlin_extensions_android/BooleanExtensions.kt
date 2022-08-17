package bayern.kickner.kotlin_extensions_android

/**
 * Executes doThis if this Boolean is ture. Returns this again
 */
inline fun Boolean.ifTrue(doThis: () -> Unit): Boolean {
    if (this) doThis()
    return this
}

/**
 * Executes doThis if this Boolean is false. Returns this again
 */
inline fun Boolean.ifFalse(doThis: () -> Unit): Boolean {
    if (this.not()) doThis()
    return this
}