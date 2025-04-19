package com.silverpine.uu.core

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Locale
import java.util.TimeZone

/*
class UULongDateSerializer(
    private val format: String = UUDate.Formats.rfc3339WithMillis,
    private val timeZone: TimeZone = UUDate.TimeZones.utc,
    private val locale: Locale = Locale.US): KSerializer<Long>
{
    override val descriptor = PrimitiveSerialDescriptor("UULongDateSerializer", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: Long)
    {
        encoder.encodeString(value.uuFormatDate(format, timeZone, locale))
    }

    override fun deserialize(decoder: Decoder): Long
    {
        return decoder.decodeString().uuParseDate(format, timeZone, locale)?.time ?: 0
    }
}*/

object UULongDateSerializer : KSerializer<Long>
{
    private const val format: String = UUDate.Formats.rfc3339WithMillis
    private val timeZone: TimeZone = UUDate.TimeZones.utc
    private val locale: Locale = Locale.US

    override val descriptor = PrimitiveSerialDescriptor("UULongDateSerializer", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: Long)
    {
        encoder.encodeString(value.uuFormatDate(format, timeZone, locale))
    }

    override fun deserialize(decoder: Decoder): Long
    {
        return decoder.decodeString().uuParseDate(format, timeZone, locale)?.time ?: 0
    }
}