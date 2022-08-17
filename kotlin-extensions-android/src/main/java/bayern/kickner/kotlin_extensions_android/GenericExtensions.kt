package bayern.kickner.kotlin_extensions_android

/**
 * Ersetzt if/else
 *
 * @param isNull Wird aufgerufen, wenn der Caller null ist
 * @param notNull Wird aufgerufen, wenn der Caller nicht null ist. Das Objekt kann dann sicher als notNull verwendet werden.
 */
inline fun <T, R> T?.ifNull(
    isNull: () -> R,
    notNull: T.() -> R,
): R {
    return if(this == null) isNull()
    else notNull()
}

inline fun <T> T?.ifNotNull(
    thisObj: T.() -> Unit,
){
    if(this != null) thisObj()
}

fun <T> T?.isNull() = this == null
fun <T> T?.isNotNull() = this != null

/**
 * Default-Wert angeben. Wenn ein Objekt null sein kann, kann damit ein default-Wert angegeben werden.
 * @return Default-Wert, wenn this == null
 */
fun <T> T?.default(value: T) = this ?: value

/**
 * Ersetzt if/else
 *
 * @param isNull Wird aufgerufen, wenn der Caller null ist
 * @param notNull Wird aufgerufen, wenn der Caller nicht null ist. Das Objekt kann dann sicher als notNull verwendet werden.
 *
 * @return Returned ein beliebiges Ergebnis innerhalb der Blöcke, nullable
 */
inline fun <T, K> T?.ifNullAndReturnNullable(
    isNull: () -> K?,
    notNull: T.() -> K?,
): K? {
    return if (this == null) isNull()
    else notNull(this)
}

/**
 * Ersetzt if/else
 *
 * @param isNull Wird aufgerufen, wenn der Caller null ist
 * @param notNull Wird aufgerufen, wenn der Caller nicht null ist. Das Objekt kann dann sicher als notNull verwendet werden.
 *
 * @return Returned ein beliebiges Ergebnis innerhalb der Blöcke
 */
inline fun <T, K> T?.ifNullAndReturn(
    isNull: () -> K,
    notNull: T.() -> K,
): K {
    return if (this == null) isNull()
    else notNull(this)
}
