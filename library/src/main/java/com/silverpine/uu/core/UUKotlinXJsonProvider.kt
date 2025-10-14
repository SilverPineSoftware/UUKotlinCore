package com.silverpine.uu.core

import com.silverpine.uu.logging.UULog
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.serializer
import java.io.ByteArrayInputStream
import java.io.InputStream

/**
 * A [UUJsonProvider] implementation backed by [kotlinx.serialization] using a configurable [Json] instance.
 *
 * This class provides type-safe serialization and deserialization of objects to and from JSON,
 * supporting multiple input formats including strings, byte arrays, and input streams.
 *
 * @param json The [Json] instance used for encoding and decoding. Defaults to [Json.Default].
 */
class UUKotlinXJsonProvider(private val json: Json = Json.Default) : UUJsonProvider
{
    /**
     * Serializes the given object into a JSON string using the provided runtime class.
     *
     * @param obj The object to serialize.
     * @param objectClass The runtime class of the object, used to resolve the appropriate [kotlinx.serialization.SerializationStrategy].
     * @return A [Result] containing the JSON string if successful, or a failure with the encountered exception.
     */
    override fun <T : Any> toJson(obj: T, objectClass: Class<T>): Result<String>
    {
        return try
        {
            val strategy = serializer(objectClass)
            Result.success(json.encodeToString(strategy, obj))
        }
        catch (ex: Exception)
        {
            UULog.d(javaClass, "toJson", "", ex)
            Result.failure(ex)
        }
    }

    /**
     * Deserializes a JSON string into an object of the specified type.
     *
     * @param source The JSON string to parse.
     * @param objectClass The target class to deserialize into.
     * @return A [Result] containing the parsed object if successful, or a failure with the encountered exception.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> fromString(source: String, objectClass: Class<T>): Result<T>
    {
        return try
        {
            val strategy = serializer(objectClass) as DeserializationStrategy<T>
            Result.success(json.decodeFromString(strategy, source))
        }
        catch (ex: Exception)
        {
            UULog.d(javaClass, "fromString", "", ex)
            Result.failure(ex)
        }
    }

    /**
     * Deserializes JSON content from an [InputStream] into an object of the specified type.
     *
     * @param source The input stream containing JSON data.
     * @param objectClass The target class to deserialize into.
     * @return A [Result] containing the parsed object if successful, or a failure with the encountered exception.
     */
    @OptIn(ExperimentalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> fromStream(source: InputStream, objectClass: Class<T>): Result<T>
    {
        return try
        {
            val strategy = serializer(objectClass) as DeserializationStrategy<T>
            Result.success(json.decodeFromStream(strategy, source))
        }
        catch (ex: Exception)
        {
            UULog.d(javaClass, "fromStream", "", ex)
            Result.failure(ex)
        }
    }

    /**
     * Deserializes JSON content from a [ByteArray] into an object of the specified type.
     *
     * Internally delegates to [fromStream] using a [ByteArrayInputStream].
     *
     * @param source The byte array containing JSON data.
     * @param objectClass The target class to deserialize into.
     * @return A [Result] containing the parsed object if successful, or a failure with the encountered exception.
     */
    override fun <T : Any> fromBytes(source: ByteArray, objectClass: Class<T>): Result<T>
    {
        return fromStream(ByteArrayInputStream(source), objectClass)
    }
}