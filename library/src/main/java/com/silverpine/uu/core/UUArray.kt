package com.silverpine.uu.core

inline fun <reified T> Array<T>.uuSlice(start: Int, count: Int): Array<T>
{
    if (start < 0)
    {
        return arrayOf()
    }

    if (start > size)
    {
        return arrayOf()
    }

    if (count <= 0)
    {
        return arrayOf()
    }

    var endIndex = start + count - 1
    if (endIndex >= size)
    {
        endIndex = (size - 1)
    }

    return slice(start..endIndex).toTypedArray()
}

fun <T> Array<T>.uuGetOrNull(index: Int): T?
{
    return if (index in indices)
    {
        this[index]
    }
    else
    {
        null
    }
}

/**
 * Splits this array into a sequence of sub arrays, each with a maximum size of [chunkSize].
 *
 * The final chunk may contain fewer elements if the array size is not evenly divisible.
 * If [chunkSize] is zero or negative, an empty list is returned.
 *
 * @param chunkSize The maximum number of elements per chunk.
 * @return A list of arrays, each containing up to [chunkSize] elements.
 *
 * @sample arrayOf("a", "b", "c", "d").uuSplitIntoChunks(3) // returns [["a", "b", "c"], ["d"]]
 */
fun <T> Array<T>.uuSplitIntoChunks(chunkSize: Int): List<Array<T>>
{
    if (chunkSize <= 0) return listOf()

    val chunks = mutableListOf<Array<T>>()
    var index = 0

    while (index < this.size) {
        val end = minOf(index + chunkSize, this.size)
        chunks.add(this.copyOfRange(index, end))
        index += chunkSize
    }

    return chunks
}