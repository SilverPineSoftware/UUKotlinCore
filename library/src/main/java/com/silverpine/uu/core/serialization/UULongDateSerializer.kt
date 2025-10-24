package com.silverpine.uu.core.serialization

import com.silverpine.uu.core.UUDate
import com.silverpine.uu.core.uuFormatDate
import com.silverpine.uu.core.uuParseDate
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Locale
import java.util.TimeZone

open class UULongDateSerializer(): KSerializer<Long>
{
    private var encodeFormat: String = UUDate.Formats.RFC_3339_WITH_MILLIS
    private var decodeFormats: Array<String> = arrayOf(encodeFormat)
    private var timeZone: TimeZone = UUDate.TimeZones.UTC
    private var locale: Locale = Locale.US
    private var decodeDefault: Long = 0
    private var serializerName: String = "UULongDateSerializer"

    constructor(
        encodeFormat: String = UUDate.Formats.RFC_3339_WITH_MILLIS,
        decodeFormats: Array<String> = arrayOf(encodeFormat),
        timeZone: TimeZone = UUDate.TimeZones.UTC,
        locale: Locale = Locale.US,
        decodeDefault: Long = 0,
        serializerName: String = "UULongDateSerializer"): this()
    {
        this.encodeFormat = encodeFormat
        this.decodeFormats = decodeFormats
        this.timeZone = timeZone
        this.locale = locale
        this.decodeDefault = decodeDefault
        this.serializerName = serializerName
    }

    override val descriptor = PrimitiveSerialDescriptor(serializerName, PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Long)
    {
        encoder.encodeString(value.uuFormatDate(encodeFormat, timeZone, locale))
    }

    override fun deserialize(decoder: Decoder): Long
    {
        return runCatching()
        {
            val string = decoder.decodeString()

            for (format in decodeFormats)
            {
                val date = string.uuParseDate(format, timeZone, locale)?.time
                if (date != null) {
                    return date
                }
            }

            return decodeDefault

        }.getOrDefault(decodeDefault)
    }
}