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
        UULog.d(javaClass, "uuGetShort", "", ex)
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
        UULog.d(javaClass, "uuGetInt", "", ex)
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
        UULog.d(javaClass, "uuGetLong", "", ex)
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
        UULog.d(javaClass, "uuGetFloat", "", ex)
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
        UULog.d(javaClass, "uuGetDouble", "", ex)
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
        UULog.d(javaClass, "uuGetBlob", "", ex)
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
        UULog.d(javaClass, "uuGetString", "", ex)
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
    return UUJson.fromString(uuGetString(column), jsonClass) ?: defaultValue
}

fun <T: Any> Cursor.uuSafeGetJsonObject(jsonClass: Class<T>, column: Any, defaultValue: T): T
{
    return uuGetJsonObject(jsonClass, column) ?: defaultValue
}

///**
// * Safely gets a column value based on the cursor field type
// *
// * @param cursor a database cursor
// * @param column column name to get
// * @param defaultValue the default value
// *
// * @return an Object of type String, Long, Double, byte[], or null
// */
//    @Nullable
//    public static Object safeGet(
//            @NonNull final Cursor cursor,
//            @NonNull final Object column,
//            @Nullable final Object defaultValue)
//    {
//        int index = cursor.getColumnIndex(column.toString());
//        return safeGet(cursor, index, defaultValue);
//    }

///**
// * Safely gets a column value based on the cursor field type
// *
// * @param cursor a database cursor
// * @param column column name to get
// * @param defaultValue the default value
// *
// * @return an Object of type String, Long, Double, byte[], or null
// */
//    @Nullable
//    public static Object safeGet(
//            @NonNull final Cursor cursor,
//            @NonNull final Object column,
//            @Nullable final Object defaultValue)
//    {
//        int index = cursor.getColumnIndex(column.toString());
//        return safeGet(cursor, index, defaultValue);
//    }
/**
 * Safely gets a column value based on the cursor field type
 *
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
        UULog.d(javaClass, "safeGet", "", ex)
        defaultValue
    }

    return result
}

/*

fun <T> safeGet(
    fieldType: Class<T>,
    cursor: Cursor,
    column: Any,
    defaultValue: T?
): Any? {
    try {
        if (fieldType == Long::class.javaPrimitiveType) {
            return safeGetLong(
                cursor,
                column,
                if (defaultValue != null) Long::class.javaPrimitiveType!!.cast(defaultValue) else 0L
            )
        } else if (fieldType == Int::class.javaPrimitiveType) {
            return safeGetInt(
                cursor,
                column,
                if (defaultValue != null) Int::class.javaPrimitiveType!!.cast(defaultValue) else 0
            )
        } else if (fieldType == Short::class.javaPrimitiveType) {
            return safeGetShort(
                cursor,
                column,
                if (defaultValue != null) Short::class.javaPrimitiveType!!.cast(defaultValue) else 0.toShort()
            )
        } else if (fieldType == Byte::class.javaPrimitiveType) {
            val `val` = safeGetInt(
                cursor,
                column,
                if (defaultValue != null) Int::class.javaPrimitiveType!!.cast(defaultValue) else 0
            )
            return `val`.toByte()
        } else if (fieldType == Float::class.javaPrimitiveType) {
            return safeGetFloat(
                cursor,
                column,
                if (defaultValue != null) Float::class.javaPrimitiveType!!.cast(defaultValue) else 0.0f
            )
        } else if (fieldType == Double::class.javaPrimitiveType) {
            return safeGetDouble(
                cursor,
                column,
                if (defaultValue != null) Double::class.javaPrimitiveType!!.cast(defaultValue) else 0.0
            )
        } else if (fieldType == Boolean::class.javaPrimitiveType) {
            return safeGetBoolean(
                cursor,
                column,
                if (defaultValue != null) Boolean::class.javaPrimitiveType!!.cast(defaultValue) else false
            )
        } else if (fieldType == Char::class.javaPrimitiveType) {
            val `val` = safeGetInt(
                cursor,
                column,
                if (defaultValue != null) Int::class.javaPrimitiveType!!.cast(defaultValue) else 0
            )
            return Char(`val`.toByte().toUShort())
        } else if (fieldType == Long::class.java) {
            return safeGetLongObject(cursor, column, defaultValue as Long?)
        } else if (fieldType == Int::class.java) {
            return safeGetIntObject(cursor, column, defaultValue as Int?)
        } else if (fieldType == Short::class.java) {
            return safeGetShortObject(cursor, column, defaultValue as Short?)
        } else if (fieldType == Byte::class.java) {
            val `val` = safeGetInt(
                cursor,
                column,
                if (defaultValue != null) Int::class.javaPrimitiveType!!.cast(defaultValue) else 0
            )
            return `val`.toByte()
        } else if (fieldType == Float::class.java) {
            return safeGetFloatObject(cursor, column, defaultValue as Float?)
        } else if (fieldType == Double::class.java) {
            return safeGetDoubleObject(cursor, column, defaultValue as Double?)
        } else if (fieldType == Boolean::class.java) {
            return safeGetBooleanObject(cursor, column, defaultValue as Boolean?)
        } else if (fieldType == Char::class.java) {
            val `val` = safeGetInt(
                cursor,
                column,
                if (defaultValue != null) Int::class.javaPrimitiveType!!.cast(defaultValue) else 0
            )
            return Char(`val`.toByte().toUShort())
        } else if (fieldType == String::class.java) {
            return uu.toolbox.data.UUCursor.safeGetString(cursor, column)
        } else if (Array<Byte>::class.java == fieldType) {
            val `val`: ByteArray = uu.toolbox.data.UUCursor.safeGetBlob(cursor, column)
            if (`val` != null) {
                val tmp = arrayOfNulls<Byte>(`val`.size)
                for (i in tmp.indices) {
                    tmp[i] = `val`[i]
                }
                return tmp
            }
        } else if (ByteArray::class.java == fieldType) {
            return uu.toolbox.data.UUCursor.safeGetBlob(cursor, column, defaultValue as ByteArray?)
        } else if (fieldType.isEnum) {
            val stringVal = safeGetString(cursor, column)
            if (UUString.isNotEmpty(stringVal)) {
                return java.lang.Enum.valueOf(fieldType as Class<Enum<*>>, stringVal)
            }
        }
    } catch (ex: Exception) {
        UULog.debug(UUDataModel::class.java, "getField", ex)
    }
    return defaultValue
}
*/