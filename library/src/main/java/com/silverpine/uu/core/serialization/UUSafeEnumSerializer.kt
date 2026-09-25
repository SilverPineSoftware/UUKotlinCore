package com.silverpine.uu.core.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder

/**
 * A Kotlin-X Serialization adapter that returns a non-null enum when decoding succeeds.
 *
 * This serializer encodes enums using the specified [UUEnumFormat], and ensures that deserialization
 * returns a non-null enum when decoding succeeds. If the input cannot be matched to any known value, the
 * [defaultDeserializeValue] is returned. Explicit JSON null also uses this fallback;
 * malformed input errors still propagate.
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
 * - Serialization writes the primitive through [UUEnumSerialization.serializeValue], without a nullable presence marker.
 * - JSON decoding uses [UUEnumSerialization.deserialize] to consume explicit null and apply the fallback.
 * - Other decoders use [UUEnumSerialization.deserializeValue] without reading a nullable presence marker.
 * - Explicit JSON null or an unknown decoded value returns [defaultDeserializeValue].
 * - Malformed input errors propagate instead of using the fallback.
 *
 * @since 1.0.0
 * @param T The enum type being serialized.
 * @param enumClass The Java class reference for the enum.
 * @param format The format to use during serialization. Defaults to [UUEnumFormat.Default].
 * @param defaultDeserializeValue The required fallback for explicit JSON null or unknown decoded values.
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
        UUEnumSerialization.serializeValue(encoder, format, value)
    }

    override fun deserialize(decoder: Decoder): T
    {
        // JSON permits an explicit null even for a non-nullable property. Preserve
        // its fallback behavior without reading presence bytes in binary formats.
        val converted = if (decoder is JsonDecoder)
        {
            UUEnumSerialization.deserialize(decoder, format, enumClass, null)
        }
        else
        {
            UUEnumSerialization.deserializeValue(decoder, format, enumClass)
        }
        return converted ?: defaultDeserializeValue
    }
}

/**
 * Factory for creating a UUSafeEnumSerializer with the given format and fallback.
 * Malformed input errors propagate instead of using the fallback.
 *
 * @since 1.0.0
 * @param T The enum type.
 * @param enumClass The enum class reference.
 * @param format The format to use for serialization. Defaults to Name.
 * @param defaultDeserializeValue The required fallback for explicit JSON null or unknown decoded values.
 * @return A UUSafeEnumSerializer instance for the given enum type.
 */
fun <T : Enum<T>> uuSafeEnumSerializer(
    enumClass: Class<T>,
    format: UUEnumFormat = UUEnumFormat.Default,
    defaultDeserializeValue: T
): UUSafeEnumSerializer<T> =
    object : UUSafeEnumSerializer<T>(enumClass, format, defaultDeserializeValue) {}
