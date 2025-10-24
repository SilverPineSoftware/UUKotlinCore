package com.silverpine.uu.core

import android.os.SystemClock
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object UUDate
{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Public Constants
    ////////////////////////////////////////////////////////////////////////////////////////////////
    object Constants
    {
        const val SECONDS_IN_ONE_MINUTE : Long = 60
        const val MINUTES_IN_ONE_HOUR : Long = 60
        const val HOURS_IN_ONE_DAY : Long = 24
        const val DAYS_IN_ONE_WEEK : Long = 7
        const val MILLIS_IN_ONE_SECOND : Long = 1000
        const val MILLIS_IN_ONE_MINUTE : Long = MILLIS_IN_ONE_SECOND * SECONDS_IN_ONE_MINUTE

        const val SECONDS_IN_ONE_HOUR : Long = SECONDS_IN_ONE_MINUTE * MINUTES_IN_ONE_HOUR
        const val SECONDS_IN_ONE_DAY : Long = SECONDS_IN_ONE_HOUR * HOURS_IN_ONE_DAY
        const val SECONDS_IN_ONE_WEEK : Long = SECONDS_IN_ONE_DAY * DAYS_IN_ONE_WEEK
    }

    object TimeZones
    {
        val UTC: TimeZone
            get()
            {
                return TimeZone.getTimeZone("UTC")
            }

        val LOCAL: TimeZone
            get()
            {
                return TimeZone.getDefault()
            }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Date Formats
    ////////////////////////////////////////////////////////////////////////////////////////////////
    object Formats
    {
        const val RFC_3339                      = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        const val RFC_3339_WITH_MILLIS          = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        const val ISO_8601_DATE_ONLY            = "yyyy-MM-dd"
        const val ISO_8601_TIME_ONLY            = "HH:mm:ss"
        const val ISO_8601_DATE_AND_TIME        = "yyyy-MM-dd HH:mm:ss"
        const val TIME_OF_DAY                   = "h:mm a"
        const val DAY_OF_MONTH                  = "d"
        const val NUMERIC_MONTH_OF_YEAR         = "L"
        const val SHORT_MONTH_OF_YEAR           = "LLL"
        const val LONG_MONTH_OF_YEAR            = "LLLL"
        const val SHORT_DAY_OF_WEEK             = "EE"
        const val LONG_DAY_OF_WEEK              = "EEEE"
        const val TWO_DIGIT_YEAR                = "yy"
        const val FOUR_DIGIT_YEAR               = "yyyy"
        const val EXTENDED_FILE_NAME_FORMAT     = "yyyy_MM_dd_HH_mm_ss"
    }
}




fun String.uuParseDate(
    formatter: String,
    timeZone: TimeZone = TimeZone.getDefault(),
    locale: Locale = Locale.getDefault()): Date?
{
    return runCatching()
    {
        if (isNotEmpty())
        {
            val df = SimpleDateFormat(formatter, locale)
            df.timeZone = timeZone

            return df.parse(this)
        }

        return null

    }.getOrNull()
}

fun String.uuParseDate(
    formatters: Array<String>?,
    timeZone: TimeZone = TimeZone.getDefault(),
    locale: Locale = Locale.getDefault()): Date?
{
    if (formatters != null)
    {
        for (formatter in formatters)
        {
            val parsed = uuParseDate(formatter, timeZone, locale)
            if (parsed != null)
            {
                return parsed
            }
        }
    }

    return null
}


fun Long.uuFormatDate(formatter: String, timeZone: TimeZone = TimeZone.getDefault(), locale: Locale = Locale.getDefault()): String
{
    return Date(this).uuFormatDate(formatter, timeZone, locale)
}

fun Date.uuFormatDate(formatter: String, timeZone: TimeZone = TimeZone.getDefault(), locale: Locale = Locale.getDefault()): String
{
    return runCatching()
    {
        val df = SimpleDateFormat(formatter, locale)
        df.timeZone = timeZone
        return df.format(this)
    }.getOrDefault("")
}

fun Long.uuNanoToRealTime(): Long
{
    return System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(SystemClock.elapsedRealtimeNanos() - this, TimeUnit.NANOSECONDS)
}

fun Long.uuFormatAsRfc3339(timeZone: TimeZone = UUDate.TimeZones.LOCAL, locale: Locale = Locale.getDefault()): String
{
    return uuFormatDate(UUDate.Formats.RFC_3339, timeZone, locale)
}

fun Long.uuFormatAsRfc3339WithMillis(timeZone: TimeZone = UUDate.TimeZones.LOCAL, locale: Locale = Locale.getDefault()): String
{
    return uuFormatDate(UUDate.Formats.RFC_3339_WITH_MILLIS, timeZone, locale)
}

fun Long.uuFormatAsIso8601DateOnly(timeZone: TimeZone = UUDate.TimeZones.LOCAL, locale: Locale = Locale.getDefault()): String
{
    return uuFormatDate(UUDate.Formats.ISO_8601_DATE_ONLY, timeZone, locale)
}

fun Long.uuFormatAsIso8601TimeOnly(timeZone: TimeZone = UUDate.TimeZones.LOCAL, locale: Locale = Locale.getDefault()): String
{
    return uuFormatDate(UUDate.Formats.ISO_8601_TIME_ONLY, timeZone, locale)
}

fun Long.uuFormatAsIso8601DateTime(timeZone: TimeZone = UUDate.TimeZones.LOCAL, locale: Locale = Locale.getDefault()): String
{
    return uuFormatDate(UUDate.Formats.ISO_8601_DATE_AND_TIME, timeZone, locale)
}

fun Long.uuFormatAsExtendedFileName(timeZone: TimeZone = UUDate.TimeZones.LOCAL, locale: Locale = Locale.getDefault()): String
{
    return uuFormatDate(UUDate.Formats.EXTENDED_FILE_NAME_FORMAT, timeZone, locale)
}

fun Long.uuFormatAsRfc3339Utc(locale: Locale = Locale.getDefault()): String
{
    return uuFormatAsRfc3339(timeZone = UUDate.TimeZones.UTC, locale)
}

fun Long.uuFormatAsRfc3339WithMillisUtc(locale: Locale = Locale.getDefault()): String
{
    return uuFormatAsRfc3339WithMillis(timeZone = UUDate.TimeZones.UTC, locale)
}

fun Long.uuFormatAsIso8601DateOnlyUtc(locale: Locale = Locale.getDefault()): String
{
    return uuFormatAsIso8601DateOnly(timeZone = UUDate.TimeZones.UTC, locale)
}

fun Long.uuFormatAsIso8601TimeOnlyUtc(locale: Locale = Locale.getDefault()): String
{
    return uuFormatAsIso8601TimeOnly(timeZone = UUDate.TimeZones.UTC, locale)
}

fun Long.uuFormatAsIso8601DateTimeUtc(locale: Locale = Locale.getDefault()): String
{
    return uuFormatAsIso8601DateTime(timeZone = UUDate.TimeZones.UTC, locale)
}

fun Long.uuFormatAsExtendedFileNameUtc(locale: Locale = Locale.getDefault()): String
{
    return uuFormatAsExtendedFileName(timeZone = UUDate.TimeZones.UTC, locale)
}
