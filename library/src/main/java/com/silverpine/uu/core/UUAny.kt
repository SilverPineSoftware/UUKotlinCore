package com.silverpine.uu.core

fun Any?.uuSafeToString(): String
{
    if (this == null)
    {
        return ""
    }

    return toString()
}