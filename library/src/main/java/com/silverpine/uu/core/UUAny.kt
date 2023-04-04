package com.silverpine.uu.core

import android.util.Log

fun Any?.uuSafeToString(): String
{
    if (this == null)
    {
        return ""
    }

    return toString()
}

val Any?.uuIsNull get() = this == null

fun Any?.uuIfNull(block: () -> Unit) = run {
    if (this == null) {
        block()
    }
}

fun Any?.uuPrintToLog(tag: String = "DEBUG_LOG") {
    Log.d(tag, toString())
}