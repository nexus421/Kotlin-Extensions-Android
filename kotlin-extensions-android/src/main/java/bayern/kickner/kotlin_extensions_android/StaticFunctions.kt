package bayern.kickner.kotlin_extensions_android

/**
 * Calculates the Heap-Memory aka RAM for this App in Megabyte
 * If the Heap is full and your App requests more than is available, the App will throw an OutOfMemory-Exception
 */
fun getHeapInfo(): HeapInfo {
    val runtime = Runtime.getRuntime()
    val usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L
    val maxHeapSizeInMB = runtime.maxMemory() / 1048576L
    val availHeapSizeInMB = maxHeapSizeInMB - usedMemInMB

    return HeapInfo(usedMemInMB, maxHeapSizeInMB, availHeapSizeInMB)
}

data class HeapInfo(val usedMemInMB: Long, val maxHeapSizeInMB: Long, val availableHeapSizeInMB: Long)
