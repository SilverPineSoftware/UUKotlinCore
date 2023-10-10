package com.silverpine.uu.core

import com.silverpine.uu.logging.UULog
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.serializer
import java.io.ByteArrayInputStream
import java.io.InputStream

object UUJson
{
    var json: Json = Json.Default

    fun configure(json: Json)
    {
        this.json = json
    }

    fun <T> toJson(obj: T?, strategy: SerializationStrategy<T>): String?
    {
        val nonNullObject = obj ?: return null

        return try
        {
            json.encodeToString(strategy, nonNullObject)
        }
        catch (ex: Exception)
        {
            UULog.d(javaClass, "toJson", "", ex)
            null
        }
    }

    fun <T: Any> fromJson(source: String?, strategy: DeserializationStrategy<T>): T?
    {
        val nonNullString = source ?: return null

        return try
        {
            json.decodeFromString(strategy, nonNullString)
        }
        catch (ex: Exception)
        {
            UULog.d(javaClass, "fromJson", "", ex)
            null
        }
    }

    fun <T: Any> fromBytes(bytes: ByteArray, strategy: DeserializationStrategy<T>): T?
    {
        return fromStream(ByteArrayInputStream(bytes), strategy)
    }

    fun <T: Any> fromStream(stream: InputStream, strategy: DeserializationStrategy<T>): T?
    {
        return try
        {
            json.decodeFromStream(strategy, stream)
        }
        catch (ex: Exception)
        {
            UULog.d(javaClass, "fromStream", "", ex)
            null
        }
    }
}


inline fun <reified T: Any> T.uuToJson(): String?
{
    return try
    {
        UUJson.toJson(this, serializer())
    }
    catch (ex: Exception)
    {
        UULog.d(javaClass, "uuToJsonString", "", ex)
        null
    }
}

inline fun <reified T: Any> String.uuFromJson(): T?
{
    return try
    {
        UUJson.fromJson(this, serializer())
    }
    catch (ex: Exception)
    {
        UULog.d(javaClass, "uuFromJson", "", ex)
        null
    }
}

inline fun <reified T: Any> InputStream.uuFromJson(): T?
{
    return try
    {
        UUJson.fromStream(this, serializer())
    }
    catch (ex: Exception)
    {
        UULog.d(javaClass, "uuFromJson", "", ex)
        null
    }
}

inline fun <reified T: Any> ByteArray.uuFromJson(): T?
{
    return try
    {
        UUJson.fromBytes(this, serializer())
    }
    catch (ex: Exception)
    {
        UULog.d(javaClass, "uuFromJson", "", ex)
        null
    }
}