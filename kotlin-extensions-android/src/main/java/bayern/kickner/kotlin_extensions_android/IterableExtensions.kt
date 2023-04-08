package bayern.kickner.kotlin_extensions_android

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

inline fun <T> Iterable<T>.contains(predicate: (T) -> Boolean): Boolean {
    for (element in this) if (predicate(element)) return true
    return false
}

/**
 * Splits this list into two.
 *
 * @param predicate condition. On true fills trueList, on false fills falseList
 *
 * @return SplitList containing splitted list
 */
inline fun <T> Iterable<T>.splitFilter(predicate: (T) -> Boolean): SplitList<T> {
    val trueList = mutableListOf<T>()
    val falseList = mutableListOf<T>()

    forEach {
        if(predicate(it)) trueList.add(it) else falseList.add(it)
    }

    return SplitList(trueList, falseList)
}

data class SplitList<T>(val trueList: List<T>, val falseList: List<T>)