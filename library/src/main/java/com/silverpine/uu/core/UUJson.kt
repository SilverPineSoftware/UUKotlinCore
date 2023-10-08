package com.silverpine.uu.core

import com.silverpine.uu.logging.UULog
import com.squareup.moshi.Moshi
import okio.buffer
import okio.source
import java.io.ByteArrayInputStream
import java.io.InputStream


object UUJson
{
    private var moshi: Moshi? = null

    fun init(moshi: Moshi)
    {
        this.moshi = moshi
    }

    private fun requireMoshi(): Moshi
    {
        if (moshi == null)
        {
            throw RuntimeException("Moshi is null. Must call init(Moshi) before use.")
        }

        return moshi!!
    }

    fun <T> toJson(obj: T?, objectClass: Class<T>): String?
    {
        return try
        {
            val moshi = requireMoshi()
            val jsonAdapter = moshi.adapter(objectClass)
            jsonAdapter.toJson(obj)
        }
        catch (ex: Exception)
        {
            UULog.d(javaClass, "toJson", "", ex)
            null
        }
    }

    fun <T> fromJson(source: String?, objectClass: Class<T>): T?
    {
        try
        {
            val moshi = requireMoshi()
            val src = source ?: return null

            val jsonAdapter = moshi.adapter(objectClass)
            return jsonAdapter.fromJson(src)
        }
        catch (ex: Exception)
        {
            UULog.d(javaClass, "fromJson", "", ex)
            return null
        }
    }

    fun <T> fromBytes(bytes: ByteArray, objectClass: Class<T>): T?
    {
        return fromStream(ByteArrayInputStream(bytes), objectClass)
    }

    fun <T> fromStream(stream: InputStream, objectClass: Class<T>): T?
    {
        return try
        {
            val moshi = requireMoshi()

            val jsonAdapter = moshi.adapter(objectClass)
            jsonAdapter.fromJson(stream.source().buffer())
        }
        catch (ex: Exception)
        {
            UULog.d(javaClass, "fromStream", "", ex)
            null
        }
    }
}

fun Any.uuToJson(): String?
{
    return UUJson.toJson(this, javaClass)
}

fun <T> Class<T>.uuFromJson(source: String?): T?
{
    return UUJson.fromJson(source, this)
}
