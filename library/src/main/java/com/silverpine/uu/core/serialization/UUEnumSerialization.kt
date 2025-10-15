package com.silverpine.uu.core.serialization

import com.silverpine.uu.core.uuToSnakeCase
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Internal utility for serializing and deserializing enums using flexible format strategies.
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
     * @param encoder The KotlinX encoder to write to.
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
    }

    /**
     * Deserializes an enum value using the specified [format], optionally falling back to [defaultDeserializeValue].
     *
     * This function reads either a string or integer from the [decoder], depending on the format,
     * and attempts to match it to a constant in [enumClass]. If no match is found, it returns
     * [defaultDeserializeValue] (which may be `null`).
     *
     * Matching is format-specific:
     * - `Name` → exact match on `name`
     * - `NameLower` → case-insensitive match on `name`
     * - `NameSnakeCase` → match on `uuToSnakeCase()` transformation
     * - `Ordinal` → match on `ordinal`
     *
     * @param decoder The KotlinX decoder to read from.
     * @param format The format to use for deserialization.
     * @param enumClass The enum class to match against.
     * @param defaultDeserializeValue The fallback value if no match is found. Can be `null`.
     * @return The matched enum constant, or the fallback value.
     */
    fun <T: Enum<T>> deserialize(
        decoder: Decoder,
        format: UUEnumFormat,
        enumClass: Class<T>,
        defaultDeserializeValue: T?): T?
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
        } ?: defaultDeserializeValue
    }
}