package com.silverpine.uu.core

import com.squareup.moshi.*
import java.util.*
@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class UULongDate

/**
 * Moshi date adapter
 */
class UULongDateAdapter(private val format: String = UUDate.RFC_3999_DATE_TIME_WITH_MILLIS_FORMAT,
                        private val timeZone: TimeZone = UUDate.UTC_TIME_ZONE,
                        private val locale: Locale = Locale.US)
{
    @ToJson
    fun toJson(@UULongDate value: Long): String
    {
        return value.uuFormatDate(format, timeZone, locale)
    }

    @FromJson @UULongDate fun fromJson(value: String): Long
    {
        return value.uuParseDate(format, timeZone, locale)?.time ?: 0
    }
}