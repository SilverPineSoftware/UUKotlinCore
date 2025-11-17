package com.silverpine.uu.core

import android.database.Cursor
import android.text.TextUtils
import androidx.core.database.getBlobOrNull
import androidx.core.database.getDoubleOrNull
import androidx.core.database.getFloatOrNull
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getShortOrNull
import androidx.core.database.getStringOrNull
import com.silverpine.uu.logging.UULog
import com.silverpine.uu.logging.logException

private const val LOG_TAG = "UUCursor"

fun Cursor.uuGetShort(column: Any, defaultValue: Short? = null): Short?
{
    var result = defaultValue

    try
    {
        val index = getColumnIndex(column.toString())
        if (index in 0 ..< columnCount)
        {
            result = getShortOrNull(index)
        }
    }
    catch (ex: Exception)
    {
        UULog.logException(LOG_TAG, "uuGetShort", ex)
    }

    return result
}

fun Cursor.uuSafeGetShort(column: Any, defaultValue: Short = 0): Short
{
    return uuGetShort(column) ?: defaultValue
}

fun Cursor.uuGetInt(column: Any, defaultValue: Int? = null): Int?
{
    var result = defaultValue

    try
    {
        val index = getColumnIndex(column.toString())
        if (index in 0 ..< columnCount)
        {
            result = getIntOrNull(index)
        }
    }
    catch (ex: Exception)
    {
        UULog.logException(LOG_TAG, "uuGetInt", ex)
    }

    return result
}

fun Cursor.uuSafeGetInt(column: Any, defaultValue: Int = 0): Int
{
    return uuGetInt(column) ?: defaultValue
}

fun Cursor.uuGetLong(column: Any, defaultValue: Long? = null): Long?
{
    var result = defaultValue

    try
    {
        val index = getColumnIndex(column.toString())
        if (index in 0 ..< columnCount)
        {
            result = getLongOrNull(index)
        }
    }
    catch (ex: Exception)
    {
        UULog.logException(LOG_TAG, "uuGetLong", ex)
    }

    return result
}

fun Cursor.uuSafeGetLong(column: Any, defaultValue: Long = 0): Long
{
    return uuGetLong(column) ?: defaultValue
}

fun Cursor.uuGetBoolean(column: Any, defaultValue: Boolean? = null): Boolean?
{
    val intVal = uuGetInt(column) ?: return defaultValue
    return (intVal == 1)
}

fun Cursor.uuSafeGetBoolean(column: Any, defaultValue: Boolean = false): Boolean
{
    return uuGetBoolean(column) ?: defaultValue
}


fun Cursor.uuGetFloat(column: Any, defaultValue: Float? = null): Float?
{
    var result = defaultValue

    try
    {
        val index = getColumnIndex(column.toString())
        if (index in 0 ..< columnCount)
        {
            result = getFloatOrNull(index)
        }
    }
    catch (ex: Exception)
    {
        UULog.logException(LOG_TAG, "uuGetFloat", ex)
    }

    return result
}

fun Cursor.uuSafeGetFloat(column: Any, defaultValue: Float = 0.0f): Float
{
    return uuGetFloat(column) ?: defaultValue
}


fun Cursor.uuGetDouble(column: Any, defaultValue: Double? = null): Double?
{
    var result = defaultValue

    try
    {
        val index = getColumnIndex(column.toString())
        if (index in 0 ..< columnCount)
        {
            result = getDoubleOrNull(index)
        }
    }
    catch (ex: Exception)
    {
        UULog.logException(LOG_TAG, "uuGetDouble", ex)
    }

    return result
}

fun Cursor.uuSafeGetDouble(column: Any, defaultValue: Double = 0.0): Double
{
    return uuGetDouble(column) ?: defaultValue
}


fun Cursor.uuGetBlob(column: Any, defaultValue: ByteArray? = null): ByteArray?
{
    var result = defaultValue

    try
    {
        val index = getColumnIndex(column.toString())
        if (index in 0 ..< columnCount)
        {
            result = getBlobOrNull(index)
        }
    }
    catch (ex: Exception)
    {
        UULog.logException(LOG_TAG, "uuGetBlob", ex)
    }

    return result
}

fun Cursor.uuSafeGetBlob(column: Any, defaultValue: ByteArray = byteArrayOf()): ByteArray
{
    return uuGetBlob(column) ?: defaultValue
}


fun Cursor.uuGetString(column: Any, defaultValue: String? = null): String?
{
    var result = defaultValue

    try
    {
        val index = getColumnIndex(column.toString())
        if (index in 0 ..< columnCount)
        {
            result = getStringOrNull(index)
        }
    }
    catch (ex: Exception)
    {
        UULog.logException(LOG_TAG, "uuGetString", ex)
    }

    return result
}

fun Cursor.uuSafeGetString(column: Any, defaultValue: String = ""): String
{
    return uuGetString(column) ?: defaultValue
}

fun <T: Enum<T>> Cursor.uuGetEnum(enumClass: Class<T>, column: Any, defaultValue: T? = null): T?
{
    val stringValue = uuGetString(column) ?: return defaultValue

    enumClass.enumConstants?.let()
    { values ->
        for (e in values)
        {
            if (e.name == stringValue)
            {
                return e
            }

            if (e.name.lowercase() == stringValue.lowercase())
            {
                return e
            }

            if (e.name.uuToSnakeCase() == stringValue.uuToSnakeCase())
            {
                return e
            }
        }
    }

    return defaultValue
}

fun <T: Enum<T>> Cursor.uuSafeGetEnum(enumClass: Class<T>, column: Any, defaultValue: T): T
{
    return uuGetEnum(enumClass, column) ?: defaultValue
}

fun Cursor.uuGetStringList(column: Any, defaultValue: List<String>? = null): List<String>?
{
    val stringValue = uuGetString(column) ?: return defaultValue
    return TextUtils.split(stringValue, ",").map { it.trim() }.toList()
}

fun Cursor.uuSafeGetStringList(column: Any, defaultValue: List<String> = listOf()): List<String>
{
    return uuGetStringList(column) ?: defaultValue
}

fun Cursor.uuGetStringSet(column: Any, defaultValue: Set<String>? = null): Set<String>?
{
    val list = uuGetStringList(column) ?: return defaultValue
    return list.toSet()
}

fun Cursor.uuSafeGetStringSet(column: Any, defaultValue: Set<String> = setOf()): Set<String>
{
    return uuGetStringSet(column) ?: defaultValue
}

fun <T: Any> Cursor.uuGetJsonObject(jsonClass: Class<T>, column: Any, defaultValue: T? = null): T?
{
    val json = uuGetString(column) ?: return null
    return UUJson.fromString(json, jsonClass).getOrNull() ?: defaultValue
}

fun <T: Any> Cursor.uuSafeGetJsonObject(jsonClass: Class<T>, column: Any, defaultValue: T): T
{
    return uuGetJsonObject(jsonClass, column) ?: defaultValue
}

/**
 * Safely gets a column value based on the cursor field type
 *
 * @since 1.0.0
 * @param index index to get
 * @param defaultValue the default value
 *
 * @return an Object of type String, Long, Double, byte[], or null
 */
fun Cursor.uuGet(index: Int, defaultValue: Any? = null): Any?
{
    val result = try
    {
        when (getType(index))
        {
            Cursor.FIELD_TYPE_INTEGER ->
            {
                getLong(index)
            }

            Cursor.FIELD_TYPE_FLOAT ->
            {
                getDouble(index)
            }

            Cursor.FIELD_TYPE_STRING ->
            {
                getString(index)
            }

            Cursor.FIELD_TYPE_NULL ->
            {
                null
            }

            Cursor.FIELD_TYPE_BLOB ->
            {
                getBlob(index)
            }

            else ->
            {
                defaultValue
            }
        }
    }
    catch (ex: Exception)
    {
        UULog.logException(LOG_TAG, "uuGet", ex)
        defaultValue
    }

    return result
}
