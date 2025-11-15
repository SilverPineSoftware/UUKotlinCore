package com.silverpine.uu.core

import android.os.Parcel
import android.os.Parcelable
import com.silverpine.uu.logging.UULog
import com.silverpine.uu.logging.logException

private const val LOG_TAG = "UUParcel"

/**
 * Safely serializes a parcelable into a byte array
 *
 * @since 1.0.0
 * @param parcelable the object to serialize
 *
 * @return an array of bytes on success or null if an error occurs
 */
fun Parcelable.uuSerializeParcel(): ByteArray?
{
    var result: ByteArray?
    var p: Parcel? = null

    try
    {
        p = Parcel.obtain()
        writeToParcel(p, 0)
        result = p.marshall()
    }
    catch (ex: Exception)
    {
        UULog.logException(LOG_TAG, "uuSerializeParcel", ex)
        result = null
    }
    finally
    {
        p?.uuSafeRecycle()
    }

    return result
}

/**
 * Safely deserializes an array of bytes into a Parcelable object
 *
 * @since 1.0.0
 * @param parcelableCreator the object creator
 * @param bytes the raw bytes to deserialize
 * @param <T> type of object to be returned
 * @return the deserialized object or null if an error occurs.
*/
fun <T : Parcelable?> ByteArray.uuDeserializeParcel(parcelableCreator: Parcelable.Creator<T>): T?
{
    var result: T?
    var p: Parcel? = null

    try
    {
        p = Parcel.obtain()
        p.unmarshall(this, 0, this.size)
        p.setDataPosition(0)
        result = parcelableCreator.createFromParcel(p)
    }
    catch (ex: Exception)
    {
        UULog.logException(LOG_TAG, "uuDeserializeParcel", ex)
        result = null
    }
    finally
    {
        p?.uuSafeRecycle()
    }

    return result
}

/**
 * Safely calls the Parcel.recycle method
 *
 * @since 1.0.0
 */
fun Parcel.uuSafeRecycle()
{
    try
    {
        recycle()
    }
    catch (ex: Exception)
    {
        UULog.logException(LOG_TAG, "uuSafeRecycle", ex)
    }
}