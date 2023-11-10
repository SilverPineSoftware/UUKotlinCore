package com.silverpine.uu.core

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

abstract class UUEnumSerializer<T : Enum<T>>(private val enumClass: Class<T>, private val defaultDeserializeValue: T): KSerializer<T>
{
    override val descriptor = PrimitiveSerialDescriptor("UUEnumSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: T)
    {
        encoder.encodeString(value.name.uuToSnakeCase())
    }

    override fun deserialize(decoder: Decoder): T
    {
        val entry = decoder.decodeString()

        enumClass.enumConstants?.let()
        { values ->
            for (e in values)
            {
                if (e.name == entry)
                {
                    return e
                }

                if (e.name.lowercase() == entry.lowercase())
                {
                    return e
                }

                if (e.name.uuToSnakeCase() == entry.uuToSnakeCase())
                {
                    return e
                }
            }
        }

        return defaultDeserializeValue
    }
}