package com.silverpine.uu.core

import com.silverpine.uu.logging.UULog
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.serializer
import java.io.ByteArrayInputStream
import java.io.InputStream

/*
object UUJson
{
    fun <T> toJson(obj: T?, objectClass: Class<T>): String?
    {
        val nonNullObject = obj ?: return null

        return try
        {
            Json.encodeToString(serializer(objectClass), nonNullObject)
        }
        catch (ex: Exception)
        {
            UULog.d(javaClass, "toJson", "", ex)
            null
        }
    }

    inline fun <reified T> fromJson(source: String?): T?
    {
        val nonNullString = source ?: return null
        return try
        {
            Json.decodeFromString<T>(nonNullString)
        }
        catch (ex: Exception)
        {
            UULog.d(javaClass, "fromJson", "", ex)
            null
        }
    }

    inline fun <reified T> fromBytes(bytes: ByteArray): T?
    {
        return fromStream(ByteArrayInputStream(bytes))
    }

    inline fun <reified T> fromStream(stream: InputStream): T?
    {
        return try
        {
            Json.decodeFromStream<T>(stream)
        }
        catch (ex: Exception)
        {
            UULog.d(javaClass, "fromStream", "", ex)
            null
        }
    }
}*/

inline fun <reified T: Any> T.uuToJson(): String?
{
    return try
    {
        Json.encodeToString(serializer(javaClass), this)
    }
    catch (ex: Exception)
    {
        UULog.d(javaClass, "uuToJsonString", "", ex)
        null
    }
}

inline fun <reified T> String.uuFromJson(): T?
{
    return try
    {
        Json.decodeFromString<T>(this)
    }
    catch (ex: Exception)
    {
        UULog.d(javaClass, "uuFromJson", "", ex)
        null
    }
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> InputStream.uuFromJson(): T?
{
    return try
    {
        Json.decodeFromStream<T>(this)
    }
    catch (ex: Exception)
    {
        UULog.d(javaClass, "uuFromJson", "", ex)
        null
    }
}

inline fun <reified T> ByteArray.uuFromJson(): T?
{
    return try
    {
        ByteArrayInputStream(this).uuFromJson<T>()
    }
    catch (ex: Exception)
    {
        UULog.d(javaClass, "uuFromJson", "", ex)
        null
    }
}