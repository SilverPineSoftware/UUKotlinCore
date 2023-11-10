package com.silverpine.uu.core

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

abstract class UUNullableEnumSerializer<T : Enum<T>>(private val enumClass: Class<T>, private val defaultDeserializeValue: T? = null): KSerializer<T?>
{
    override val descriptor = PrimitiveSerialDescriptor("UUNullableEnumSerializer", PrimitiveKind.STRING)

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: T?)
    {
        value?.let()
        { v ->
            encoder.encodeString(v.name.uuToSnakeCase())
        } ?: run()
        {
            encoder.encodeNull()
        }
    }

    override fun deserialize(decoder: Decoder): T?
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