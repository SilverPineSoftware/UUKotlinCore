package com.silverpine.uu.core.serialization

import com.silverpine.uu.core.UUValueBackedEnum
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.nullable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A flexible and idiomatic Kotlin-X Serialization adapter for enums, supporting multiple serialization formats
 * and optional fallback behavior during deserialization.
 *
 * This serializer delegates format-specific logic to [UUEnumSerialization], allowing enums to be encoded and decoded
 * using one of several strategies defined by [UUEnumFormat]. It supports both nullable and non-nullable enum types
 * and falls back for null or unknown decoded values. Malformed input errors propagate.
 *
 * ### Supported Formats
 * - [UUEnumFormat.Name] — exact enum name (e.g. `"RED_BLUE"`)
 * - [UUEnumFormat.NameLower] — lowercase name (e.g. `"red_blue"`)
 * - [UUEnumFormat.NameSnakeCase] — snake_case conversion (e.g. `"red_blue"`)
 * - [UUEnumFormat.Ordinal] — ordinal index (e.g. `0`, `1`, `2`)
 *
 * ### Nullability
 * - If value is `null`, serialization will encode a JSON `null`.
 * - If deserialization fails to match any enum constant, [defaultDeserializeValue] is returned.
 *
 * @since 1.0.0
 * @param T The enum type being serialized.
 * @param enumClass The Java class reference for the enum.
 * @param format The format to use during serialization. Defaults to [UUEnumFormat.Default].
 * @param defaultDeserializeValue The fallback value to use if deserialization fails. Can be `null`.
 */
abstract class UUEnumSerializer<T : Enum<T>>(
    private val enumClass: Class<T>,
    private val format: UUEnumFormat = UUEnumFormat.Default,
    private val defaultDeserializeValue: T? = null
) : KSerializer<T?>
{
    override val descriptor = PrimitiveSerialDescriptor("UUEnumSerializer-$format", format.primitiveKind).nullable

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: T?)
    {
        UUEnumSerialization.serialize(encoder, format, value)
    }

    override fun deserialize(decoder: Decoder): T?
    {
        return UUEnumSerialization.deserialize(decoder, format, enumClass, null) ?: defaultDeserializeValue
    }
}

/**
 * Serializes nullable enums through a serializer for their explicit backing values.
 *
 * The backing serializer controls the wire representation and descriptor, including unsigned
 * and structured values. Null input and unknown values use [defaultDeserializeValue].
 * Malformed input and exceptions from [fromValue] propagate to the caller.
 *
 * @param V The non-null backing value type.
 * @param T The enum type implementing [UUValueBackedEnum].
 * @param valueSerializer Serializer for the backing value, not the enum.
 * @param fromValue Maps a decoded value to an enum, or null if unknown; never called for null input.
 * @param defaultDeserializeValue Fallback for null or unknown input; defaults to null.
 */
abstract class UUValueBackedEnumSerializer<V: Any, T>(
    private val valueSerializer: KSerializer<V>,
    private val fromValue: (V) -> T?,
    private val defaultDeserializeValue: T? = null
) : KSerializer<T?>
    where T: Enum<T>,
          T: UUValueBackedEnum<V>
{
    override val descriptor = valueSerializer.descriptor.nullable

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: T?)
    {
        UUEnumSerialization.serializeValueBacked(encoder, valueSerializer, value)
    }

    override fun deserialize(decoder: Decoder): T?
    {
        return UUEnumSerialization.deserializeValueBacked(decoder, valueSerializer, fromValue, defaultDeserializeValue)
    }

}

/**
 * Factory for creating a UUEnumSerializer with the given format and fallback.
 *
 * @since 1.0.0
 * @param T The enum type.
 * @param enumClass The enum class reference.
 * @param format The format to use for serialization. Defaults to Name.
 * @param defaultDeserializeValue The fallback value if deserialization fails. Can be null.
 * @return A UUEnumSerializer instance for the given enum type.
 */
fun <T : Enum<T>> uuEnumSerializer(
    enumClass: Class<T>,
    format: UUEnumFormat = UUEnumFormat.Default,
    defaultDeserializeValue: T? = null
): UUEnumSerializer<T> =
    object : UUEnumSerializer<T>(enumClass, format, defaultDeserializeValue) {}

/**
 * Creates a nullable enum serializer using its backing type's wire representation.
 *
 * @param valueSerializer Serializer for non-null backing values.
 * @param fromValue Reverse lookup; returns null for unknown values. Exceptions propagate.
 * @param defaultDeserializeValue Result for explicit null or unknown values; defaults to null.
 * @return A serializer whose descriptor is the nullable backing-value descriptor.
 */
fun <V: Any, T> uuValueBackedEnumSerializer(
    valueSerializer: KSerializer<V>,
    fromValue: (V) -> T?,
    defaultDeserializeValue: T? = null
): KSerializer<T?>
        where T: Enum<T>,
              T: UUValueBackedEnum<V> =
    object : UUValueBackedEnumSerializer<V, T>(valueSerializer, fromValue, defaultDeserializeValue) {}
