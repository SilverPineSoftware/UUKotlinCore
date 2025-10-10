package com.silverpine.uu.core

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object UUHexByteArraySerializer : KSerializer<ByteArray>
{
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUHexByteArray", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ByteArray)
    {
        val hex = value.uuToHex()
        encoder.encodeString(hex)
    }

    override fun deserialize(decoder: Decoder): ByteArray
    {
        val hex = decoder.decodeString()
        return hex.uuToHexData() ?: byteArrayOf()
    }
}