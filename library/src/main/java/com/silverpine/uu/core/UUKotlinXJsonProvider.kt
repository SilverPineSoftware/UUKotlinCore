package com.silverpine.uu.core

import com.silverpine.uu.logging.UULog
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.serializer
import java.io.ByteArrayInputStream
import java.io.InputStream

class UUKotlinXJsonProvider(private val json: Json = Json.Default): UUJsonProvider
{
    override fun <T: Any> toJson(obj: T?, objectClass: Class<T>): String?
    {
        val nonNullObject = obj ?: return null

        return try
        {
            val strategy = serializer(objectClass)
            json.encodeToString(strategy, nonNullObject)
        }
        catch (ex: Exception)
        {
            UULog.d(javaClass, "toJson", "", ex)
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T: Any> fromString(source: String?, objectClass: Class<T>): T?
    {
        val nonNullSource = source ?: return null

        return try
        {
            val strategy = serializer(objectClass) as DeserializationStrategy<T>
            json.decodeFromString(strategy, nonNullSource)
        }
        catch (ex: Exception)
        {
            UULog.d(javaClass, "fromString", "", ex)
            null
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    override fun <T: Any> fromStream(source: InputStream?, objectClass: Class<T>): T?
    {
        val nonNullSource = source ?: return null

        return try
        {
            val strategy = serializer(objectClass) as DeserializationStrategy<T>
            json.decodeFromStream(strategy, nonNullSource)
        }
        catch (ex: Exception)
        {
            UULog.d(javaClass, "fromStream", "", ex)
            null
        }
    }

    override fun <T: Any> fromBytes(source: ByteArray?, objectClass: Class<T>): T?
    {
        return fromStream(ByteArrayInputStream(source), objectClass)
    }
}