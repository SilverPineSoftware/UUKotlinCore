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

/**
 * A [KSerializer] for [Long] that serializes epoch milliseconds to formatted date strings
 * and deserializes date strings back to epoch milliseconds.
 *
 * This serializer converts Unix epoch timestamps (milliseconds since January 1, 1970 UTC)
 * into human-readable date strings for JSON serialization. By default, it uses RFC 3339
 * format with milliseconds in UTC timezone.
 *
 * **Usage:**
 * ```
 * @Serializable
 * data class MyData(
 *     @Serializable(with = UULongDateSerializer::class)
 *     val timestamp: Long
 * )
 * ```
 *
 * **Serialization:** Epoch milliseconds ([Long]) → Formatted date string → JSON string
 * **Deserialization:** JSON string → Date string → Epoch milliseconds ([Long])
 *
 * The serializer is configurable and can be extended to support different date formats,
 * timezones, locales, and multiple decode formats for maximum compatibility.
 *
 * @since 1.0.0
 */
open class UULongDateSerializer(): KSerializer<Long>
{
    /**
     * The date format string used for encoding (serialization).
     *
     * Defaults to [UUDate.Formats.RFC_3339_WITH_MILLIS].
     *
     * @since 1.0.0
     */
    private var encodeFormat: String = UUDate.Formats.RFC_3339_WITH_MILLIS

    /**
     * An array of date format strings to try when decoding (deserialization).
     *
     * The formats are tried in order until one successfully parses the input string.
     * Defaults to a single-element array containing [encodeFormat].
     *
     * @since 1.0.0
     */
    private var decodeFormats: Array<String> = arrayOf(encodeFormat)

    /**
     * The timezone used for formatting and parsing dates.
     *
     * Defaults to [UUDate.TimeZones.UTC].
     *
     * @since 1.0.0
     */
    private var timeZone: TimeZone = UUDate.TimeZones.UTC

    /**
     * The locale used for formatting and parsing dates.
     *
     * Defaults to [Locale.US].
     *
     * @since 1.0.0
     */
    private var locale: Locale = Locale.US

    /**
     * The default value to return if deserialization fails.
     *
     * Defaults to `0L` (Unix epoch).
     *
     * @since 1.0.0
     */
    private var decodeDefault: Long = 0

    /**
     * The name used for the serial descriptor.
     *
     * Defaults to "UULongDateSerializer".
     *
     * @since 1.0.0
     */
    private var serializerName: String = "UULongDateSerializer"

    /**
     * Creates a configured instance of [UULongDateSerializer].
     *
     * @param encodeFormat The date format string used for encoding. Defaults to
     *                     [UUDate.Formats.RFC_3339_WITH_MILLIS].
     * @param decodeFormats An array of date format strings to try when decoding, in order
     *                      of preference. Defaults to a single-element array containing
     *                      [encodeFormat].
     * @param timeZone The timezone used for formatting and parsing. Defaults to
     *                 [UUDate.TimeZones.UTC].
     * @param locale The locale used for formatting and parsing. Defaults to [Locale.US].
     * @param decodeDefault The default value to return if deserialization fails.
     *                      Defaults to `0L`.
     * @param serializerName The name used for the serial descriptor. Defaults to
     *                       "UULongDateSerializer".
     *
     * @since 1.0.0
     */
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

    /**
     * The serial descriptor for this serializer.
     *
     * Describes the serialized form as a long primitive with the configured [serializerName].
     *
     * @since 1.0.0
     */
    override val descriptor = PrimitiveSerialDescriptor(serializerName, PrimitiveKind.LONG)

    /**
     * Serializes epoch milliseconds to a formatted date string.
     *
     * The long value (epoch milliseconds) is formatted using [encodeFormat], [timeZone],
     * and [locale], then written as a string to the encoder.
     *
     * @param encoder The encoder to write the serialized value to.
     * @param value The epoch milliseconds (long) to serialize.
     *
     * @since 1.0.0
     */
    override fun serialize(encoder: Encoder, value: Long)
    {
        encoder.encodeString(value.uuFormatDate(encodeFormat, timeZone, locale))
    }

    /**
     * Deserializes a formatted date string back to epoch milliseconds.
     *
     * The string is read from the decoder, then parsed using each format in [decodeFormats]
     * in order until one succeeds. If all formats fail to parse, [decodeDefault] is returned.
     *
     * @param decoder The decoder to read the serialized value from.
     * @return The epoch milliseconds (long), or [decodeDefault] if parsing fails.
     *
     * @since 1.0.0
     */
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