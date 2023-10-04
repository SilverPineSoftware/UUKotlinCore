package com.silverpine.uu.core

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.util.*

class UUDateJsonAdapter(
    private val format: String = UUDate.RFC_3999_DATE_TIME_WITH_MILLIS_FORMAT,
    private val timeZone: TimeZone = UUDate.UTC_TIME_ZONE,
    private val locale: Locale = Locale.US) : JsonAdapter<Date?>()
{
    override fun fromJson(reader: JsonReader): Date?
    {
        if (reader.peek() == JsonReader.Token.NULL)
        {
            return reader.nextNull()
        }

        val string = reader.nextString()
        return string.uuParseDate(format, timeZone, locale)
    }

    override fun toJson(writer: JsonWriter, value: Date?)
    {
        if (value == null)
        {
            writer.nullValue()
        }
        else
        {
            val string = value.uuFormatDate(format, timeZone, locale)
            writer.value(string)
        }
    }
}