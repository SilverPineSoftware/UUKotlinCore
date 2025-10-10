package com.silverpine.uu.core

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Base64

object UUBase64ByteArraySerializer : KSerializer<ByteArray>
{
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUBase64ByteArray", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ByteArray)
    {
        val base64 = Base64.getEncoder().encodeToString(value)
        encoder.encodeString(base64)
    }

    override fun deserialize(decoder: Decoder): ByteArray
    {
        val base64 = decoder.decodeString()
        return Base64.getDecoder().decode(base64)
    }
}