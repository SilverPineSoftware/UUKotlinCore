package com.silverpine.uu.core.serialization

import com.silverpine.uu.core.uuToHex
import com.silverpine.uu.core.uuToHexData
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A [KSerializer] for [ByteArray] that serializes to and deserializes from hexadecimal-encoded strings.
 *
 * This serializer converts binary data (byte arrays) into hexadecimal strings for JSON
 * serialization, which is necessary because JSON cannot directly represent binary data.
 * Hexadecimal encoding provides a human-readable representation of binary data and is
 * commonly used for debugging and inspection purposes.
 *
 * **Usage:**
 * ```
 * @Serializable
 * data class MyData(
 *     @Serializable(with = UUHexByteArraySerializer::class)
 *     val binaryData: ByteArray
 * )
 * ```
 *
 * **Serialization:** [ByteArray] → Hexadecimal string → JSON string
 * **Deserialization:** JSON string → Hexadecimal string → [ByteArray]
 *
 * **Note:** If deserialization fails (invalid hex string), an empty [ByteArray] is returned.
 *
 * @since 1.0.0
 */
object UUHexByteArraySerializer : KSerializer<ByteArray>
{
    /**
     * The serial descriptor for this serializer.
     *
     * Describes the serialized form as a string primitive with the name "UUHexByteArray".
     *
     * @since 1.0.0
     */
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUHexByteArray", PrimitiveKind.STRING)

    /**
     * Serializes a [ByteArray] to a hexadecimal-encoded string.
     *
     * The byte array is first encoded to hexadecimal using [uuToHex], then written
     * as a string to the encoder.
     *
     * @param encoder The encoder to write the serialized value to.
     * @param value The byte array to serialize.
     *
     * @since 1.0.0
     */
    override fun serialize(encoder: Encoder, value: ByteArray)
    {
        val hex = value.uuToHex()
        encoder.encodeString(hex)
    }

    /**
     * Deserializes a hexadecimal-encoded string back to a [ByteArray].
     *
     * The string is read from the decoder, then decoded from hexadecimal using [uuToHexData]
     * to produce the original byte array. If the hex string is invalid or cannot be decoded,
     * an empty [ByteArray] is returned.
     *
     * @param decoder The decoder to read the serialized value from.
     * @return The deserialized byte array, or an empty array if decoding fails.
     *
     * @since 1.0.0
     */
    override fun deserialize(decoder: Decoder): ByteArray
    {
        val hex = decoder.decodeString()
        return hex.uuToHexData() ?: byteArrayOf()
    }
}