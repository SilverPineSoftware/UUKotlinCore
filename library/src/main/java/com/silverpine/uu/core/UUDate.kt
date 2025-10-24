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
        const val secondsInOneMinute : Long = 60
        const val minutesInOneHour : Long = 60
        const val hoursInOneDay : Long = 24
        const val daysInOneWeek : Long = 7
        const val millisInOneSecond : Long = 1000
        const val millisInOneMinute : Long = millisInOneSecond * secondsInOneMinute

        const val secondsInOneHour : Long = secondsInOneMinute * minutesInOneHour
        const val secondsInOneDay : Long = secondsInOneHour * hoursInOneDay
        const val secondsInOneWeek : Long = secondsInOneDay * daysInOneWeek
    }

    object TimeZones
    {
        val utc: TimeZone
            get()
            {
                return TimeZone.getTimeZone("UTC")
            }
        val local: TimeZone
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
        const val rfc3339               = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        const val rfc3339WithMillis     = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        const val iso8601DateOnly       = "yyyy-MM-dd"
        const val iso8601TimeOnly       = "HH:mm:ss"
        const val iso8601DateTime       = "yyyy-MM-dd HH:mm:ss"
        const val timeOfDay             = "h:mm a"
        const val dayOfMonth            = "d"
        const val numericMonthOfYear    = "L"
        const val shortMonthOfYear      = "LLL"
        const val longMonthOfYear       = "LLLL"
        const val shortDayOfWeek        = "EE"
        const val longDayOfWeek         = "EEEE"
        const val twoDigitYear          = "yy"
        const val fourDigitYear         = "yyyy"
        const val extendedFileNameFormat = "yyyy_MM_dd_HH_mm_ss"
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

fun Long.uuFormatAsRfc3339(timeZone: TimeZone = UUDate.TimeZones.local, locale: Locale = Locale.getDefault()): String
{
    return uuFormatDate(UUDate.Formats.rfc3339, timeZone, locale)
}

fun Long.uuFormatAsRfc3339WithMillis(timeZone: TimeZone = UUDate.TimeZones.local, locale: Locale = Locale.getDefault()): String
{
    return uuFormatDate(UUDate.Formats.rfc3339WithMillis, timeZone, locale)
}

fun Long.uuFormatAsIso8601DateOnly(timeZone: TimeZone = UUDate.TimeZones.local, locale: Locale = Locale.getDefault()): String
{
    return uuFormatDate(UUDate.Formats.iso8601DateOnly, timeZone, locale)
}

fun Long.uuFormatAsIso8601TimeOnly(timeZone: TimeZone = UUDate.TimeZones.local, locale: Locale = Locale.getDefault()): String
{
    return uuFormatDate(UUDate.Formats.iso8601TimeOnly, timeZone, locale)
}

fun Long.uuFormatAsIso8601DateTime(timeZone: TimeZone = UUDate.TimeZones.local, locale: Locale = Locale.getDefault()): String
{
    return uuFormatDate(UUDate.Formats.iso8601DateTime, timeZone, locale)
}

fun Long.uuFormatAsExtendedFileName(timeZone: TimeZone = UUDate.TimeZones.local, locale: Locale = Locale.getDefault()): String
{
    return uuFormatDate(UUDate.Formats.extendedFileNameFormat, timeZone, locale)
}

fun Long.uuFormatAsRfc3339Utc(locale: Locale = Locale.getDefault()): String
{
    return uuFormatAsRfc3339(timeZone = UUDate.TimeZones.utc, locale)
}

fun Long.uuFormatAsRfc3339WithMillisUtc(locale: Locale = Locale.getDefault()): String
{
    return uuFormatAsRfc3339WithMillis(timeZone = UUDate.TimeZones.utc, locale)
}

fun Long.uuFormatAsIso8601DateOnlyUtc(locale: Locale = Locale.getDefault()): String
{
    return uuFormatAsIso8601DateOnly(timeZone = UUDate.TimeZones.utc, locale)
}

fun Long.uuFormatAsIso8601TimeOnlyUtc(locale: Locale = Locale.getDefault()): String
{
    return uuFormatAsIso8601TimeOnly(timeZone = UUDate.TimeZones.utc, locale)
}

fun Long.uuFormatAsIso8601DateTimeUtc(locale: Locale = Locale.getDefault()): String
{
    return uuFormatAsIso8601DateTime(timeZone = UUDate.TimeZones.utc, locale)
}

fun Long.uuFormatAsExtendedFileNameUtc(locale: Locale = Locale.getDefault()): String
{
    return uuFormatAsExtendedFileName(timeZone = UUDate.TimeZones.utc, locale)
}
