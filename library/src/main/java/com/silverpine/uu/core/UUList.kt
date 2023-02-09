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