package com.silverpine.uu.core

inline fun <reified T> List<T>.uuSlice(start: Int, count: Int): List<T>
{
    if (start < 0)
    {
        return listOf()
    }

    if (start > size)
    {
        return listOf()
    }

    if (count <= 0)
    {
        return listOf()
    }

    var endIndex = start + count - 1
    if (endIndex >= size)
    {
        endIndex = (size - 1)
    }

    return slice(start..endIndex)
}

fun <T> List<T>.uuGetOrNull(index: Int): T?
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
 * Splits this list into a sequence of sublists, each with a maximum size of [chunkSize].
 *
 * The final chunk may contain fewer elements if the list size is not evenly divisible.
 * If [chunkSize] is zero or negative, an empty list is returned.
 *
 * @param chunkSize The maximum number of elements per chunk.
 * @return A list of sub lists, each containing up to [chunkSize] elements.
 *
 * @sample listOf(1, 2, 3, 4, 5).uuSplitIntoChunks(2) // returns [[1, 2], [3, 4], [5]]
 */
fun <T> List<T>.uuSplitIntoChunks(chunkSize: Int): List<List<T>>
{
    if (chunkSize <= 0) return listOf()

    val chunks = mutableListOf<List<T>>()
    var index = 0

    while (index < this.size)
    {
        val end = minOf(index + chunkSize, this.size)
        chunks.add(this.subList(index, end))
        index += chunkSize
    }

    return chunks
}