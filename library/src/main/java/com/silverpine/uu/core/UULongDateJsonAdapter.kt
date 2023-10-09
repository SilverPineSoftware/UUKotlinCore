package com.silverpine.uu.core

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Locale
import java.util.TimeZone

class UULongDateJsonAdapter(
    private val format: String = UUDate.RFC_3999_DATE_TIME_WITH_MILLIS_FORMAT,
    private val timeZone: TimeZone = UUDate.UTC_TIME_ZONE,
    private val locale: Locale = Locale.US): KSerializer<Long>
{
    override val descriptor = PrimitiveSerialDescriptor("UULongDateJsonAdapter", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: Long)
    {
        encoder.encodeString(value.uuFormatDate(format, timeZone, locale))
    }

    override fun deserialize(decoder: Decoder): Long
    {
        return decoder.decodeString().uuParseDate(format, timeZone, locale)?.time ?: 0
    }
}