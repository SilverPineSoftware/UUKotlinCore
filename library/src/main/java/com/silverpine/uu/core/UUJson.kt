package com.silverpine.uu.core

import com.silverpine.uu.core.UUJson.fromStream
import com.silverpine.uu.core.UUJson.init
import kotlinx.serialization.json.Json
import java.io.InputStream

/**
 * Centralized JSON utility for serializing and deserializing objects using a pluggable [UUJsonProvider].
 *
 * This singleton acts as a façade over the underlying JSON implementation, allowing consumers to
 * serialize and deserialize objects from strings, byte arrays, or input streams without directly
 * depending on a specific library like `kotlinx.serialization`.
 *
 * The default provider is [UUKotlinXJsonProvider] backed by [Json.Default], but can be replaced via [init]
 * to support alternative implementations (e.g., for testing or using different serialization frameworks).
 *
 * @since 1.0.0
 */
object UUJson
{
    private var provider: UUJsonProvider = UUKotlinXJsonProvider(Json.Default)

    /**
     * Initializes the JSON provider used by this utility.
     *
     * This allows swapping out the default implementation with a custom or mock provider.
     * Should be called early in application setup if customization is needed.
     *
     * @since 1.0.0
     * @param provider The [UUJsonProvider] implementation to delegate all JSON operations to.
     */
    fun init(provider: UUJsonProvider)
    {
        this.provider = provider
    }

    /**
     * Serializes the given object into a JSON string.
     *
     * @since 1.0.0
     * @param obj The object to serialize.
     * @param objectClass The runtime class of the object, used to resolve the correct serialization strategy.
     * @return A [Result] containing the JSON string if successful, or a failure with the encountered exception.
     */
    fun <T : Any> toJson(obj: T, objectClass: Class<T>): Result<String>
    {
        return provider.toJson(obj, objectClass)
    }

    /**
     * Deserializes a JSON string into an object of the specified type.
     *
     * @since 1.0.0
     * @param source The JSON string to parse.
     * @param objectClass The target class to deserialize into.
     * @return A [Result] containing the parsed object if successful, or a failure with the encountered exception.
     */
    fun <T : Any> fromString(source: String, objectClass: Class<T>): Result<T>
    {
        return provider.fromString(source, objectClass)
    }

    /**
     * Deserializes JSON content from an [InputStream] into an object of the specified type.
     *
     * @since 1.0.0
     * @param source The input stream containing JSON data.
     * @param objectClass The target class to deserialize into.
     * @return A [Result] containing the parsed object if successful, or a failure with the encountered exception.
     */
    fun <T : Any> fromStream(source: InputStream, objectClass: Class<T>): Result<T>
    {
        return provider.fromStream(source, objectClass)
    }

    /**
     * Deserializes JSON content from a [ByteArray] into an object of the specified type.
     *
     * Internally delegates to [fromStream] using a [java.io.ByteArrayInputStream].
     *
     * @since 1.0.0
     * @param source The byte array containing JSON data.
     * @param objectClass The target class to deserialize into.
     * @return A [Result] containing the parsed object if successful, or a failure with the encountered exception.
     */
    fun <T : Any> fromBytes(source: ByteArray, objectClass: Class<T>): Result<T>
    {
        return provider.fromBytes(source, objectClass)
    }
}