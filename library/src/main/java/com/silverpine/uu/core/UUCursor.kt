package com.silverpine.uu.core

import android.database.Cursor
import androidx.core.database.getStringOrNull

fun Cursor.uuGetString(columnName: String, defaultValue: String = ""): String
{
    var result: String? = null
    val index = getColumnIndex(columnName)
    if (index in 0..< columnCount)
    {
        result = getStringOrNull(index)
    }

    return result ?: defaultValue
}

fun Cursor.uuGetStringOrNull(columnName: String, defaultValue: String? = null): String?
{
    var result: String? = null
    val index = getColumnIndex(columnName)
    if (index in 0..< columnCount)
    {
        result = getStringOrNull(index)
    }

    return result ?: defaultValue
}
