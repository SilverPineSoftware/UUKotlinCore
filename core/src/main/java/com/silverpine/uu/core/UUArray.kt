package com.silverpine.uu.core

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