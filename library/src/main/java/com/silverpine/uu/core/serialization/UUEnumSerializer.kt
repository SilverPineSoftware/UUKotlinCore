package com.silverpine.uu.core.serialization

import com.silverpine.uu.core.UUNumberBackedEnum
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.nullable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A flexible and idiomatic KotlinX Serialization adapter for enums, supporting multiple serialization formats
 * and optional fallback behavior during deserialization.
 *
 * This serializer delegates format-specific logic to [UUEnumSerialization], allowing enums to be encoded and decoded
 * using one of several strategies defined by [UUEnumFormat]. It supports both nullable and non-nullable enum types
 * and can gracefully fall back to a default value if deserialization fails.
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

abstract class UUNumberBackedEnumSerializer<N: Number, T>(
    private val converter: (Long)->T?,
    private val defaultDeserializeValue: T? = null
) : KSerializer<T?>
    where T: Enum<T>,
          T: UUNumberBackedEnum<N>
{
    override val descriptor = PrimitiveSerialDescriptor("UUNumberBackedEnumSerializer",PrimitiveKind.LONG).nullable

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: T?)
    {
        UUEnumSerialization.serializeNumberBacked(encoder, value)
    }

    override fun deserialize(decoder: Decoder): T?
    {
        return UUEnumSerialization.deserializeNumberBacked(decoder, converter, defaultDeserializeValue )
    }

}

/**
 * Factory for creating a UUEnumSerializer with the given format and fallback.
 *
 * @since 1.0.0
 * @param T The enum type.
 * @param enumClass The enum class reference.
 * @param format The format to use for serialization. Defaults to NameSnakeCase.
 * @param defaultDeserializeValue The fallback value if deserialization fails. Can be null.
 * @return A UUEnumSerializer instance for the given enum type.
 */
fun <T : Enum<T>> uuEnumSerializer(
    enumClass: Class<T>,
    format: UUEnumFormat = UUEnumFormat.Default,
    defaultDeserializeValue: T? = null
): UUEnumSerializer<T> =
    object : UUEnumSerializer<T>(enumClass, format, defaultDeserializeValue) {}

fun <N: Number, T> uuNumberBackedEnumSerializer(
    converter: (Long)->T?,
    defaultDeserializeValue: T? = null
): KSerializer<T?>
        where T: Enum<T>,
              T: UUNumberBackedEnum<N> =
    object : UUNumberBackedEnumSerializer<N, T>(converter, defaultDeserializeValue) {}




