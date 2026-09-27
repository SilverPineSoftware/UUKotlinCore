package com.silverpine.uu.core.serialization

import com.silverpine.uu.core.UUValueBackedEnum
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder

/**
 * Serializes non-null enums using their explicit backing values.
 *
 * The backing serializer controls the representation, including unsigned and structured values.
 * Unknown decoded values and explicit JSON null return [defaultDeserializeValue]. Malformed input
 * and exceptions from [fromValue] propagate. Other formats decode the backing value directly,
 * without reading or writing an enum-level nullable presence marker.
 *
 * @param V The non-null backing value type.
 * @param T The enum implementing [UUValueBackedEnum].
 * @param valueSerializer Serializer for non-null backing values.
 * @param fromValue Reverse lookup; returns null for unknown values and is not called for JSON null.
 * @param defaultDeserializeValue Required non-null fallback for JSON null or unknown values.
 */
abstract class UUSafeValueBackedEnumSerializer<V: Any, T>(
    private val valueSerializer: KSerializer<V>,
    private val fromValue: (V) -> T?,
    private val defaultDeserializeValue: T
) : KSerializer<T>
    where T: Enum<T>, T: UUValueBackedEnum<V>
{
    override val descriptor = valueSerializer.descriptor

    override fun serialize(encoder: Encoder, value: T)
    {
        valueSerializer.serialize(encoder, value.value)
    }

    override fun deserialize(decoder: Decoder): T
    {
        val converted = if (decoder is JsonDecoder)
        {
            UUEnumSerialization.deserializeValueBacked(decoder, valueSerializer, fromValue, null)
        }
        else
        {
            fromValue(valueSerializer.deserialize(decoder))
        }
        return converted ?: defaultDeserializeValue
    }
}

/**
 * Creates a non-null enum serializer using its backing value's representation and descriptor.
 *
 * @param valueSerializer Serializer for non-null backing values.
 * @param fromValue Reverse lookup; returns null for unknown values. Exceptions propagate.
 * @param defaultDeserializeValue Required fallback for explicit JSON null or unknown decoded values.
 * @return A serializer that returns a non-null enum when decoding succeeds.
 */
fun <V: Any, T> uuSafeValueBackedEnumSerializer(
    valueSerializer: KSerializer<V>,
    fromValue: (V) -> T?,
    defaultDeserializeValue: T
): UUSafeValueBackedEnumSerializer<V, T>
    where T: Enum<T>, T: UUValueBackedEnum<V> =
    object : UUSafeValueBackedEnumSerializer<V, T>(valueSerializer, fromValue, defaultDeserializeValue) {}
