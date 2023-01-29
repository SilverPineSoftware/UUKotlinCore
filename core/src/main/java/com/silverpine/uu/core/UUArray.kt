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