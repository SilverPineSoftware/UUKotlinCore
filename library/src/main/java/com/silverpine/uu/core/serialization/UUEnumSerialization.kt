package com.silverpine.uu.core.serialization

import com.silverpine.uu.core.UUValueBackedEnum
import com.silverpine.uu.core.uuToSnakeCase
import kotlinx.serialization.KSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Shared utility for serializing and deserializing enums using flexible format strategies.
 *
 * This object provides reusable logic for encoding and decoding enum values based on [UUEnumFormat],
 * supporting both nullable and non-nullable use cases. It is used by [UUEnumSerializer] and [UUSafeEnumSerializer]
 * to centralize format-specific behavior and reduce duplication.
 *
 * ### Supported Formats
 * - [UUEnumFormat.Name] — exact enum name (e.g. `"RED_BLUE"`)
 * - [UUEnumFormat.NameLower] — lowercase name (e.g. `"red_blue"`)
 * - [UUEnumFormat.NameSnakeCase] — snake_case conversion (e.g. `"red_blue"`)
 * - [UUEnumFormat.Ordinal] — ordinal index (e.g. `0`, `1`, `2`)
 * - [serializeValueBacked] / [deserializeValueBacked] — explicit values via a backing serializer.
 *
 * @since 1.0.0
 */
object UUEnumSerialization
{
    /**
     * Serializes an enum value using the specified [format].
     *
     * If [value] is `null`, this function encodes a JSON `null`. Otherwise, it encodes the enum
     * using the format-specific representation:
     * - `Name` → `value.name`
     * - `NameLower` → `value.name.lowercase()`
     * - `NameSnakeCase` → `value.name.uuToSnakeCase()`
     * - `Ordinal` → `value.ordinal`
     *
     * @since 1.0.0
     * @param encoder The Kotlinx encoder to write to.
     * @param format The format to use for serialization.
     * @param value The enum value to serialize, or `null`.
     */
    @OptIn(ExperimentalSerializationApi::class)
    fun <T: Enum<T>> serialize(encoder: Encoder, format: UUEnumFormat, value: T?)
    {
        if (value == null)
        {
            encoder.encodeNull()
        }
        else
        {
            encoder.encodeNotNullMark()
            serializeValue(encoder, format, value)
        }
    }

    /** Writes a non-null enum primitive without a nullable presence marker. */
    internal fun <T: Enum<T>> serializeValue(encoder: Encoder, format: UUEnumFormat, value: T)
    {
        when (format)
        {
            UUEnumFormat.Name ->
                encoder.encodeString(value.name)

            UUEnumFormat.NameLower ->
                encoder.encodeString(value.name.lowercase())

            UUEnumFormat.NameSnakeCase ->
                encoder.encodeString(value.name.uuToSnakeCase())

            UUEnumFormat.Ordinal ->
                encoder.encodeInt(value.ordinal)
        }
    }

    /**
     * Encodes null or a presence marker followed by the enum's backing value.
     *
     * @param encoder Destination encoder.
     * @param valueSerializer Controls the backing value's wire representation without numeric coercion.
     * @param value Enum to encode, or null.
     */
    @OptIn(ExperimentalSerializationApi::class)
    fun <V: Any, T> serializeValueBacked(encoder: Encoder, valueSerializer: KSerializer<V>, value: T?)
    where T: Enum<T>,
          T: UUValueBackedEnum<V>
    {
        if (value == null)
        {
            encoder.encodeNull()
        }
        else
        {
            encoder.encodeNotNullMark()
            valueSerializer.serialize(encoder, value.value)
        }
    }

    /**
     * Deserializes an enum value using the specified [format], optionally falling back to [defaultDeserializeValue].
     *
     * This function reads either a string or integer from the [decoder], depending on the format,
     * and attempts to match it to a constant in [enumClass]. If no match is found, it returns
     * [defaultDeserializeValue] (which may be `null`). Explicit null also uses that fallback;
     * malformed input errors propagate.
     *
     * Matching is format-specific:
     * - `Name` → exact match on `name`
     * - `NameLower` → case-insensitive match on `name`
     * - `NameSnakeCase` → match on `uuToSnakeCase()` transformation
     * - `Ordinal` → match on `ordinal`
     *
     * @since 1.0.0
     * @param decoder The Kotlinx decoder to read from.
     * @param format The format to use for deserialization.
     * @param enumClass The enum class to match against.
     * @param defaultDeserializeValue The fallback value if no match is found. Can be `null`.
     * @return The matched enum constant, or the fallback value.
     */
    @OptIn(ExperimentalSerializationApi::class)
    fun <T: Enum<T>> deserialize(
        decoder: Decoder,
        format: UUEnumFormat,
        enumClass: Class<T>,
        defaultDeserializeValue: T?): T?
    {
        val converted: T? = if (decoder.decodeNotNullMark())
        {
            deserializeValue(decoder, format, enumClass)
        }
        else
        {
            decoder.decodeNull()
        }

        return converted ?: defaultDeserializeValue
    }

    /** Reads an enum primitive without consuming a nullable presence marker; returns null if unknown. */
    internal fun <T: Enum<T>> deserializeValue(
        decoder: Decoder,
        format: UUEnumFormat,
        enumClass: Class<T>): T?
    {
        return when (format)
        {
            UUEnumFormat.Name ->
            {
                val decoded = decoder.decodeString()
                enumClass.enumConstants?.firstOrNull { it.name == decoded }
            }

            UUEnumFormat.NameLower ->
            {
                val decoded = decoder.decodeString()
                enumClass.enumConstants?.firstOrNull { it.name.equals(decoded, ignoreCase = true) }
            }

            UUEnumFormat.NameSnakeCase ->
            {
                val decoded = decoder.decodeString()
                enumClass.enumConstants?.firstOrNull { it.name.uuToSnakeCase() == decoded.uuToSnakeCase() }
            }

            UUEnumFormat.Ordinal ->
            {
                val decoded = decoder.decodeInt()
                enumClass.enumConstants?.firstOrNull { it.ordinal == decoded }
            }
        }
    }

    /**
     * Decodes a nullable backing value and resolves it to an enum.
     *
     * @param decoder Source decoder.
     * @param valueSerializer Decodes non-null backing values; malformed input errors propagate.
     * @param fromValue Reverse lookup; not called for null input. Exceptions propagate.
     * @param defaultDeserializeValue Result when input is null or reverse lookup returns null.
     * @return The matched enum or the configured fallback.
     */
    @OptIn(ExperimentalSerializationApi::class)
    fun <V: Any, T> deserializeValueBacked(
        decoder: Decoder,
        valueSerializer: KSerializer<V>,
        fromValue: (V) -> T?,
        defaultDeserializeValue: T?): T?
        where T: Enum<T>,
              T: UUValueBackedEnum<V>
    {
        val converted: T? = if (decoder.decodeNotNullMark())
        {
            fromValue(valueSerializer.deserialize(decoder))
        }
        else
        {
            decoder.decodeNull()
        }

        return converted ?: defaultDeserializeValue
    }
}