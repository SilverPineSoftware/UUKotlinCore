package com.silverpine.uu.core.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A strict KotlinX Serialization adapter for enums that guarantees non-null deserialization.
 *
 * This serializer encodes enums using the specified [UUEnumFormat], and ensures that deserialization
 * always returns a valid enum constant. If the input cannot be matched to any known value, the
 * [defaultDeserializeValue] is returned instead of throwing an exception.
 *
 * This is ideal for use with non-nullable enum fields in data models, especially when consuming
 * external JSON that may contain unexpected or invalid values.
 *
 * ### Supported Formats
 * - [UUEnumFormat.Name] — exact enum name (e.g. `"RED_BLUE"`)
 * - [UUEnumFormat.NameLower] — lowercase name (e.g. `"red_blue"`)
 * - [UUEnumFormat.NameSnakeCase] — snake_case conversion (e.g. `"red_blue"`)
 * - [UUEnumFormat.Ordinal] — ordinal index (e.g. `0`, `1`, `2`)
 *
 * ### Behavior
 * - Serialization delegates to [UUEnumSerialization.serialize] using the configured [format].
 * - Deserialization attempts to match the input using [UUEnumSerialization.deserialize].
 * - If no match is found, [defaultDeserializeValue] is returned.
 *
 * @param T The enum type being serialized.
 * @param enumClass The Java class reference for the enum.
 * @param format The format to use during serialization. Defaults to [UUEnumFormat.Default].
 * @param defaultDeserializeValue The fallback value to use if deserialization fails.
 */
abstract class UUSafeEnumSerializer<T : Enum<T>>(
    private val enumClass: Class<T>,
    private val format: UUEnumFormat = UUEnumFormat.Default,
    private val defaultDeserializeValue: T
) : KSerializer<T>
{
    override val descriptor = PrimitiveSerialDescriptor("UUSafeEnumSerializer-$format", format.primitiveKind)

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: T)
    {
        UUEnumSerialization.serialize(encoder, format, value)
    }

    override fun deserialize(decoder: Decoder): T
    {
        return UUEnumSerialization.deserialize(decoder, format, enumClass, null) ?: defaultDeserializeValue
    }
}

/**
 * Factory for creating a UUSafeEnumSerializer with the given format and fallback.
 *
 * @param T The enum type.
 * @param enumClass The enum class reference.
 * @param format The format to use for serialization. Defaults to NameSnakeCase.
 * @param defaultDeserializeValue The fallback value if deserialization fails. Can be null.
 * @return A UUEnumSerializer instance for the given enum type.
 */
fun <T : Enum<T>> uuSafeEnumSerializer(
    enumClass: Class<T>,
    format: UUEnumFormat = UUEnumFormat.Default,
    defaultDeserializeValue: T
): UUSafeEnumSerializer<T> =
    object : UUSafeEnumSerializer<T>(enumClass, format, defaultDeserializeValue) {}