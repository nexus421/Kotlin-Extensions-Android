package bayern.kickner.kotlin_extensions_android

import androidx.annotation.IntRange
import java.security.MessageDigest

/**
 * Der übergebene String wird, abgesehen vom ersten und letzten Zeichen, durch Sternchen ersetzt.
 * Siehe Dokumentation -> [.coverString]
 * Bsp. Hallo wird zu H***o
 *
 * @param s String der verschleiert werden soll
 * @return verschleierten String oder leerer String falls s == null
 */
fun String.coverString() = coverString(1, this.length - 2)

/**
 * Der übergebene String wird zwischen start und end durch Sternchen ersetzt.
 * Dient zur Anonymisierung eines Strings
 *
 * @param s     String der verschleiert werden soll
 * @param start ab hier werden Sternchen eingefügt
 * @param end   bis hier werden Sternchen eingefügt
 * @return verschleierten String
 */
fun String.coverString(@IntRange(from = 0) start: Int, @IntRange(from = 1) end: Int): String {
    val length = this.length
    if (length < 1) return this
    require(!(end >= length || start < 0)) {
        "Start or end Argument is not allowed! start = $start end = $end"
    }
    val result = toCharArray()
    for (i in 0 until length) {
        if (i in start..end) {
            result[i] = '*'
        }
    }
    return String(result)
}

fun String.withNewLine() = this + "\n"

/**
 * Hashes a String with the selected HashAlgorithm. Default is SHA_256.
 *
 * SHA-512 is not guaranteed to work on all Java platforms.
 */
fun String.hash(hashAlgorithm: HashAlgorithm = HashAlgorithm.SHA_256) = MessageDigest
    .getInstance(hashAlgorithm.algorithm)
    .digest(toByteArray())
    .fold("") { str, it -> str + "%02x".format(it) }

/**
 * Possible Hash-Algorithms
 *
 * WARNING: Only MD5, SHA-1 and SHA-256 are guaranteed to work in all Java platforms
 * Refer: https://docs.oracle.com/javase/7/docs/api/java/security/MessageDigest.html
 */
enum class HashAlgorithm(val algorithm: String) {
    MD5("MD5"),
    SHA_1("SHA-1"),
    SHA_256("SHA-256"),
    SHA_512("SHA-512"),
}

inline fun String?.ifNullOrBlank(action: () -> Unit) {
    if(isNullOrBlank()) action()
}

inline fun String?.ifNotNullOrBlank(action: String.() -> Unit) {
    if(!isNullOrBlank()) action()
}

fun String?.isNotNullOrBlank() = !isNullOrBlank()

/**
 * If this string is null or blank, the action result is returned. Otherwise this will be returned
 *
 * "".ifNullOrBlank{ "Kadoffe"} -> Results: "Kadoffe"
 */
inline fun String?.ifNullOrBlank(action: () -> String): String {
    return if (isNullOrBlank()) action()
    else this
}
