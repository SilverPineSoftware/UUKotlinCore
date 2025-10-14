package com.silverpine.uu.core

import java.io.InputStream

/**
 * Provides serialization and deserialization utilities for converting objects to and from JSON.
 *
 * This interface abstracts JSON handling to support pluggable implementations (e.g., kotlinx.serialization, Gson, Moshi)
 * and enables testable, type-safe conversion between structured data and textual representations.
 */
interface UUJsonProvider
{
    /**
     * Serializes the given object into a JSON string.
     *
     * @param obj The object to serialize.
     * @param objectClass The runtime class of the object, used to resolve the correct serialization strategy.
     * @return A [Result] containing the JSON string if successful, or a failure with the encountered exception.
     */
    fun <T : Any> toJson(obj: T, objectClass: Class<T>): Result<String>

    /**
     * Deserializes a JSON string into an object of the specified type.
     *
     * @param source The JSON string to parse.
     * @param objectClass The target class to deserialize into.
     * @return A [Result] containing the parsed object if successful, or a failure with the encountered exception.
     */
    fun <T : Any> fromString(source: String, objectClass: Class<T>): Result<T>

    /**
     * Deserializes JSON content from an [InputStream] into an object of the specified type.
     *
     * @param source The input stream containing JSON data.
     * @param objectClass The target class to deserialize into.
     * @return A [Result] containing the parsed object if successful, or a failure with the encountered exception.
     */
    fun <T : Any> fromStream(source: InputStream, objectClass: Class<T>): Result<T>

    /**
     * Deserializes JSON content from a [ByteArray] into an object of the specified type.
     *
     * @param source The byte array containing JSON data.
     * @param objectClass The target class to deserialize into.
     * @return A [Result] containing the parsed object if successful, or a failure with the encountered exception.
     */
    fun <T : Any> fromBytes(source: ByteArray, objectClass: Class<T>): Result<T>
}