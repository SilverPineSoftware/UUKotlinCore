package com.silverpine.uu.core

import com.silverpine.uu.logging.UULog
import com.silverpine.uu.logging.logException
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

private const val LOG_TAG = "UUSerializable"

fun <T : Serializable?> ByteArray.uuDeserialize(type: Class<T>): T?
{
    var bis: ByteArrayInputStream? = null
    var ois: ObjectInputStream? = null
    var result: T?

    try
    {
        bis = ByteArrayInputStream(this)
        ois = ObjectInputStream(bis)
        result = UUObject.safeCast(type, ois.readObject())
    }
    catch (ex: Exception)
    {
        UULog.logException(LOG_TAG, "uuDeserialize", ex)
        result = null
    }
    finally
    {
        ois?.uuSafeClose()
        bis?.uuSafeClose()
    }

    return result
}

/**
 * Safely serializes an object
 *
 * @since 1.0.0
 * @return an array of bytes
 */
fun Serializable.uuSerialize(): ByteArray?
{
    var bos: ByteArrayOutputStream? = null
    var oos: ObjectOutputStream? = null
    var result: ByteArray?

    try
    {
        bos = ByteArrayOutputStream()
        oos = ObjectOutputStream(bos)
        oos.writeObject(this)
        oos.flush()
        result = bos.toByteArray()
    }
    catch (ex: Exception)
    {
        UULog.logException(LOG_TAG, "uuSerialize", ex)
        result = null
    }
    finally
    {
        oos?.uuSafeClose()
        bos?.uuSafeClose()
    }

    return result
}