package com.silverpine.uu.core.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Base64

/**
 * A [KSerializer] for [ByteArray] that serializes to and deserializes from Base64-encoded strings.
 *
 * This serializer converts binary data (byte arrays) into Base64-encoded strings for JSON
 * serialization, which is necessary because JSON cannot directly represent binary data.
 * The Base64 encoding ensures the data can be safely transmitted and stored as text.
 *
 * **Usage:**
 * ```
 * @Serializable
 * data class MyData(
 *     @Serializable(with = UUBase64ByteArraySerializer::class)
 *     val binaryData: ByteArray
 * )
 * ```
 *
 * **Serialization:** [ByteArray] → Base64 string → JSON string
 * **Deserialization:** JSON string → Base64 string → [ByteArray]
 *
 * @since 1.0.0
 */
object UUBase64ByteArraySerializer : KSerializer<ByteArray>
{
    /**
     * The serial descriptor for this serializer.
     *
     * Describes the serialized form as a string primitive with the name "UUBase64ByteArray".
     *
     * @since 1.0.0
     */
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUBase64ByteArray", PrimitiveKind.STRING)

    /**
     * Serializes a [ByteArray] to a Base64-encoded string.
     *
     * The byte array is first encoded to Base64 using the standard Base64 encoder,
     * then written as a string to the encoder.
     *
     * @param encoder The encoder to write the serialized value to.
     * @param value The byte array to serialize.
     *
     * @since 1.0.0
     */
    override fun serialize(encoder: Encoder, value: ByteArray)
    {
        val base64 = Base64.getEncoder().encodeToString(value)
        encoder.encodeString(base64)
    }

    /**
     * Deserializes a Base64-encoded string back to a [ByteArray].
     *
     * The string is read from the decoder, then decoded from Base64 to produce
     * the original byte array.
     *
     * @param decoder The decoder to read the serialized value from.
     * @return The deserialized byte array.
     *
     * @since 1.0.0
     */
    override fun deserialize(decoder: Decoder): ByteArray
    {
        val base64 = decoder.decodeString()
        return Base64.getDecoder().decode(base64)
    }
}