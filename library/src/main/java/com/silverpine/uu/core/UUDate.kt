package com.silverpine.uu.core

import android.os.SystemClock
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

/**
 * Date/time utilities providing:
 *
 * - Common **time constants** (seconds, minutes, hours, etc.)
 * - Convenient **time zones** (UTC and system local)
 * - Standard **format strings** (RFC-3339, ISO-8601, file-name safe)
 * - Extensions to **parse** and **format** dates
 * - A helper to convert a **monotonic** timestamp to wall-clock time
 *
 * @since 1.0.0
 */
object UUDate
{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Public Constants
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Common time-amount constants (seconds, minutes, hours, days, weeks and their millisecond/second
     * conversions). These are provided as [Long] to avoid accidental integer overflow in arithmetic.
     *
     * @since 1.0.0
     */
    object Constants
    {
        /** Number of seconds in one minute (60). */
        const val SECONDS_IN_ONE_MINUTE: Long = 60

        /** Number of minutes in one hour (60). */
        const val MINUTES_IN_ONE_HOUR: Long = 60

        /** Number of hours in one day (24). */
        const val HOURS_IN_ONE_DAY: Long = 24

        /** Number of days in one week (7). */
        const val DAYS_IN_ONE_WEEK: Long = 7

        /** Number of milliseconds in one second (1,000). */
        const val MILLIS_IN_ONE_SECOND: Long = 1000

        /** Number of milliseconds in one minute (60,000). */
        const val MILLIS_IN_ONE_MINUTE: Long = MILLIS_IN_ONE_SECOND * SECONDS_IN_ONE_MINUTE

        /** Number of seconds in one hour (3,600). */
        const val SECONDS_IN_ONE_HOUR: Long = SECONDS_IN_ONE_MINUTE * MINUTES_IN_ONE_HOUR

        /** Number of seconds in one day (86,400). */
        const val SECONDS_IN_ONE_DAY: Long = SECONDS_IN_ONE_HOUR * HOURS_IN_ONE_DAY

        /** Number of seconds in one week (604,800). */
        const val SECONDS_IN_ONE_WEEK: Long = SECONDS_IN_ONE_DAY * DAYS_IN_ONE_WEEK
    }

    /**
     * Commonly used time zone references.
     *
     * @since 1.0.0
     */
    object TimeZones
    {
        /** Coordinated Universal Time (UTC). */
        val UTC: TimeZone
            get() = TimeZone.getTimeZone("UTC")

        /** The system’s current default time zone. */
        val LOCAL: TimeZone
            get() = TimeZone.getDefault()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Date Formats
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Standard date/time format strings used for parsing and formatting.
     *
     * These patterns are compatible with [SimpleDateFormat]. All RFC-3339 patterns here use a
     * literal `'Z'` designator, so they should be formatted with a UTC [TimeZone] for canonical
     * output.
     *
     * @since 1.0.0
     */
    object Formats
    {
        /** RFC-3339 without milliseconds, e.g. `2025-10-23T12:34:56Z`. */
        @Suppress("SpellCheckingInspection")
        const val RFC_3339                      = "yyyy-MM-dd'T'HH:mm:ss'Z'"

        /** RFC-3339 with milliseconds, e.g. `2025-10-23T12:34:56.789Z`. */
        @Suppress("SpellCheckingInspection")
        const val RFC_3339_WITH_MILLIS          = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

        /** ISO-8601 date-only, e.g. `2025-10-23`. */
        const val ISO_8601_DATE_ONLY            = "yyyy-MM-dd"

        /** ISO-8601 time-only, e.g. `12:34:56`. */
        const val ISO_8601_TIME_ONLY            = "HH:mm:ss"

        /** ISO-8601 using a space separator, e.g. `2025-10-23 12:34:56`. */
        const val ISO_8601_DATE_AND_TIME        = "yyyy-MM-dd HH:mm:ss"

        /** 12-hour clock, e.g. `1:45 PM`. */
        const val TIME_OF_DAY                   = "h:mm a"

        /** Day of month, e.g. `23`. */
        const val DAY_OF_MONTH                  = "d"

        /** Numeric month, e.g. `10`. */
        const val NUMERIC_MONTH_OF_YEAR         = "L"

        /** Short month name, e.g. `Oct`. */
        const val SHORT_MONTH_OF_YEAR           = "LLL"

        /** Long month name, e.g. `October`. */
        const val LONG_MONTH_OF_YEAR            = "LLLL"

        /** Short weekday, e.g. `Thu`. */
        const val SHORT_DAY_OF_WEEK             = "EE"

        /** Long weekday, e.g. `Thursday`. */
        const val LONG_DAY_OF_WEEK              = "EEEE"

        /** Two-digit year, e.g. `25`. */
        const val TWO_DIGIT_YEAR                = "yy"

        /** Four-digit year, e.g. `2025`. */
        const val FOUR_DIGIT_YEAR               = "yyyy"

        /** File-name safe timestamp, e.g. `2025_10_23_12_34_56`. */
        const val EXTENDED_FILE_NAME_FORMAT     = "yyyy_MM_dd_HH_mm_ss"
    }
}

/**
 * Attempts to parse this string into a [Date] using a single [formatter] pattern.
 *
 * Parsing is performed with the provided [timeZone] and [locale]. If this string is empty or
 * parsing fails, `null` is returned.
 *
 * @param formatter The [SimpleDateFormat] pattern to use.
 * @param timeZone The time zone assumed for parsing (defaults to [TimeZone.getDefault]).
 * @param locale The locale used for language-sensitive fields (defaults to [Locale.getDefault]).
 * @return A parsed [Date], or `null` on failure.
 *
 * @since 1.0.0
 */
fun String.uuParseDate(
    formatter: String,
    timeZone: TimeZone = TimeZone.getDefault(),
    locale: Locale = Locale.getDefault()
): Date?
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

/**
 * Attempts to parse this string into a [Date] by trying multiple [formatters] in order.
 *
 * For each format in [formatters], this calls [uuParseDate] with the same [timeZone] and [locale].
 * The first successful parse result is returned. If all formats fail or [formatters] is `null`,
 * `null` is returned.
 *
 * @since 1.0.0
 * @param formatters An array of [SimpleDateFormat] patterns to try in order.
 * @param timeZone The time zone assumed for parsing (defaults to [TimeZone.getDefault]).
 * @param locale The locale used for language-sensitive fields (defaults to [Locale.getDefault]).
 * @return A parsed [Date], or `null` if none of the formats match.
 */
fun String.uuParseDate(
    formatters: Array<String>?,
    timeZone: TimeZone = TimeZone.getDefault(),
    locale: Locale = Locale.getDefault()
): Date?
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

/**
 * Formats this epoch timestamp (milliseconds since Unix epoch) using the given [formatter].
 *
 * This is a convenience wrapper around [Date.uuFormatDate].
 *
 * @since 1.0.0
 * @param formatter The [SimpleDateFormat] pattern to use.
 * @param timeZone The time zone applied for formatting (defaults to [TimeZone.getDefault]).
 * @param locale The locale used for language-sensitive formatting (defaults to [Locale.getDefault]).
 * @return The formatted date/time string.
 */
fun Long.uuFormatDate(
    formatter: String,
    timeZone: TimeZone = TimeZone.getDefault(),
    locale: Locale = Locale.getDefault()
): String
{
    return Date(this).uuFormatDate(formatter, timeZone, locale)
}

/**
 * Formats this [Date] using the given [formatter].
 *
 * If formatting throws, an empty string is returned.
 *
 * @since 1.0.0
 * @param formatter The [SimpleDateFormat] pattern to use.
 * @param timeZone The time zone applied for formatting (defaults to [TimeZone.getDefault]).
 * @param locale The locale used for language-sensitive formatting (defaults to [Locale.getDefault]).
 * @return The formatted string, or `""` on error.
 */
fun Date.uuFormatDate(
    formatter: String,
    timeZone: TimeZone = TimeZone.getDefault(),
    locale: Locale = Locale.getDefault()
): String
{
    return runCatching()
    {
        val df = SimpleDateFormat(formatter, locale)
        df.timeZone = timeZone
        return df.format(this)
    }.getOrDefault("")
}

/**
 * Converts a monotonic clock reading (as returned by [SystemClock.elapsedRealtimeNanos]) to
 * approximate **wall-clock time** in milliseconds since epoch.
 *
 * The value of `this` must be a timestamp in **nanoseconds** captured from
 * [SystemClock.elapsedRealtimeNanos]. The conversion uses the current wall clock to align
 * monotonic elapsed time to epoch time.
 *
 * @since 1.0.0
 * @return Corresponding wall-clock time in **milliseconds** since epoch.
 */
fun Long.uuNanoToRealTime(): Long
{
    return System.currentTimeMillis() -
            TimeUnit.MILLISECONDS.convert(SystemClock.elapsedRealtimeNanos() - this, TimeUnit.NANOSECONDS)
}

/**
 * Formats this epoch timestamp as RFC-3339 without milliseconds using the provided [timeZone] and [locale].
 *
 * @since 1.0.0
 * @param timeZone The time zone to format in (defaults to [UUDate.TimeZones.LOCAL]).
 * @param locale The locale for language-sensitive fields (defaults to [Locale.getDefault]).
 * @return An RFC-3339 string like `2025-10-23T12:34:56Z` (when [timeZone] is UTC).
 */
fun Long.uuFormatAsRfc3339(
    timeZone: TimeZone = UUDate.TimeZones.LOCAL,
    locale: Locale = Locale.getDefault()
): String
{
    return uuFormatDate(UUDate.Formats.RFC_3339, timeZone, locale)
}

/**
 * Formats this epoch timestamp as RFC-3339 **with milliseconds** using the provided [timeZone] and [locale].
 *
 * @since 1.0.0
 * @param timeZone The time zone to format in (defaults to [UUDate.TimeZones.LOCAL]).
 * @param locale The locale for language-sensitive fields (defaults to [Locale.getDefault]).
 * @return An RFC-3339 string like `2025-10-23T12:34:56.789Z` (when [timeZone] is UTC).
 */
fun Long.uuFormatAsRfc3339WithMillis(
    timeZone: TimeZone = UUDate.TimeZones.LOCAL,
    locale: Locale = Locale.getDefault()
): String
{
    return uuFormatDate(UUDate.Formats.RFC_3339_WITH_MILLIS, timeZone, locale)
}

/**
 * Formats this epoch timestamp as ISO-8601 **date only**, e.g. `2025-10-23`.
 *
 * @since 1.0.0
 * @param timeZone The time zone to format in (defaults to [UUDate.TimeZones.LOCAL]).
 * @param locale The locale for language-sensitive fields (defaults to [Locale.getDefault]).
 * @return The formatted date string.
 */
fun Long.uuFormatAsIso8601DateOnly(
    timeZone: TimeZone = UUDate.TimeZones.LOCAL,
    locale: Locale = Locale.getDefault()
): String
{
    return uuFormatDate(UUDate.Formats.ISO_8601_DATE_ONLY, timeZone, locale)
}

/**
 * Formats this epoch timestamp as ISO-8601 **time only**, e.g. `12:34:56`.
 *
 * @since 1.0.0
 * @param timeZone The time zone to format in (defaults to [UUDate.TimeZones.LOCAL]).
 * @param locale The locale for language-sensitive fields (defaults to [Locale.getDefault]).
 * @return The formatted time string.
 */
fun Long.uuFormatAsIso8601TimeOnly(
    timeZone: TimeZone = UUDate.TimeZones.LOCAL,
    locale: Locale = Locale.getDefault()
): String
{
    return uuFormatDate(UUDate.Formats.ISO_8601_TIME_ONLY, timeZone, locale)
}

/**
 * Formats this epoch timestamp as ISO-8601 **date and time** with a space separator,
 * e.g. `2025-10-23 12:34:56`.
 *
 * @since 1.0.0
 * @param timeZone The time zone to format in (defaults to [UUDate.TimeZones.LOCAL]).
 * @param locale The locale for language-sensitive fields (defaults to [Locale.getDefault]).
 * @return The formatted date-time string.
 */
fun Long.uuFormatAsIso8601DateTime(
    timeZone: TimeZone = UUDate.TimeZones.LOCAL,
    locale: Locale = Locale.getDefault()
): String
{
    return uuFormatDate(UUDate.Formats.ISO_8601_DATE_AND_TIME, timeZone, locale)
}

/**
 * Formats this epoch timestamp as a file-name-safe string, e.g. `2025_10_23_12_34_56`.
 *
 * @since 1.0.0
 * @param timeZone The time zone to format in (defaults to [UUDate.TimeZones.LOCAL]).
 * @param locale The locale for language-sensitive fields (defaults to [Locale.getDefault]).
 * @return The formatted file-name-safe string.
 */
fun Long.uuFormatAsExtendedFileName(
    timeZone: TimeZone = UUDate.TimeZones.LOCAL,
    locale: Locale = Locale.getDefault()
): String
{
    return uuFormatDate(UUDate.Formats.EXTENDED_FILE_NAME_FORMAT, timeZone, locale)
}

/**
 * Formats this epoch timestamp as RFC-3339 (no millis) **in UTC**.
 *
 * Equivalent to [uuFormatAsRfc3339] with [UUDate.TimeZones.UTC].
 *
 * @since 1.0.0
 * @param locale The locale for language-sensitive fields (defaults to [Locale.getDefault]).
 * @return A UTC RFC-3339 string like `2025-10-23T12:34:56Z`.
 */
fun Long.uuFormatAsRfc3339Utc(locale: Locale = Locale.getDefault()): String
{
    return uuFormatAsRfc3339(timeZone = UUDate.TimeZones.UTC, locale)
}

/**
 * Formats this epoch timestamp as RFC-3339 **with milliseconds** **in UTC**.
 *
 * Equivalent to [uuFormatAsRfc3339WithMillis] with [UUDate.TimeZones.UTC].
 *
 * @since 1.0.0
 * @param locale The locale for language-sensitive fields (defaults to [Locale.getDefault]).
 * @return A UTC RFC-3339 string like `2025-10-23T12:34:56.789Z`.
 */
fun Long.uuFormatAsRfc3339WithMillisUtc(locale: Locale = Locale.getDefault()): String
{
    return uuFormatAsRfc3339WithMillis(timeZone = UUDate.TimeZones.UTC, locale)
}

/**
 * Formats this epoch timestamp as ISO-8601 **date only** **in UTC**, e.g. `2025-10-23`.
 *
 * Equivalent to [uuFormatAsIso8601DateOnly] with [UUDate.TimeZones.UTC].
 *
 * @since 1.0.0
 * @param locale The locale for language-sensitive fields (defaults to [Locale.getDefault]).
 * @return The formatted UTC date string.
 */
fun Long.uuFormatAsIso8601DateOnlyUtc(locale: Locale = Locale.getDefault()): String
{
    return uuFormatAsIso8601DateOnly(timeZone = UUDate.TimeZones.UTC, locale)
}

/**
 * Formats this epoch timestamp as ISO-8601 **time only** **in UTC**, e.g. `12:34:56`.
 *
 * Equivalent to [uuFormatAsIso8601TimeOnly] with [UUDate.TimeZones.UTC].
 *
 * @since 1.0.0
 * @param locale The locale for language-sensitive fields (defaults to [Locale.getDefault]).
 * @return The formatted UTC time string.
 */
fun Long.uuFormatAsIso8601TimeOnlyUtc(locale: Locale = Locale.getDefault()): String
{
    return uuFormatAsIso8601TimeOnly(timeZone = UUDate.TimeZones.UTC, locale)
}

/**
 * Formats this epoch timestamp as ISO-8601 **date and time** (space separator) **in UTC**,
 * e.g. `2025-10-23 12:34:56`.
 *
 * Equivalent to [uuFormatAsIso8601DateTime] with [UUDate.TimeZones.UTC].
 *
 * @since 1.0.0
 * @param locale The locale for language-sensitive fields (defaults to [Locale.getDefault]).
 * @return The formatted UTC date-time string.
 */
fun Long.uuFormatAsIso8601DateTimeUtc(locale: Locale = Locale.getDefault()): String
{
    return uuFormatAsIso8601DateTime(timeZone = UUDate.TimeZones.UTC, locale)
}

/**
 * Formats this epoch timestamp as a file-name-safe string **in UTC**, e.g. `2025_10_23_12_34_56`.
 *
 * Equivalent to [uuFormatAsExtendedFileName] with [UUDate.TimeZones.UTC].
 *
 * @since 1.0.0
 * @param locale The locale for language-sensitive fields (defaults to [Locale.getDefault]).
 * @return The formatted UTC file-name-safe string.
 */
fun Long.uuFormatAsExtendedFileNameUtc(locale: Locale = Locale.getDefault()): String
{
    return uuFormatAsExtendedFileName(timeZone = UUDate.TimeZones.UTC, locale)
}
