package com.silverpine.uu.core

import java.text.SimpleDateFormat
import java.util.*

object UUDate
{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Public Constants
    ////////////////////////////////////////////////////////////////////////////////////////////////
    const val SECONDS_IN_ONE_MINUTE: Long = 60
    const val MINUTES_IN_ONE_HOUR: Long = 60
    const val HOURS_IN_ONE_DAY: Long = 24
    const val DAYS_IN_ONE_WEEK: Long = 7
    const val MILLIS_IN_ONE_SECOND: Long = 1000
    const val MILLIS_IN_ONE_MINUTE = SECONDS_IN_ONE_MINUTE * MILLIS_IN_ONE_SECOND
    const val MILLIS_IN_ONE_HOUR = MINUTES_IN_ONE_HOUR * MILLIS_IN_ONE_MINUTE
    const val MILLIS_IN_ONE_DAY = HOURS_IN_ONE_DAY * MILLIS_IN_ONE_HOUR
    const val MILLIS_IN_ONE_WEEK = DAYS_IN_ONE_WEEK * MILLIS_IN_ONE_DAY
    const val MINUTES_IN_ONE_DAY = MINUTES_IN_ONE_HOUR * HOURS_IN_ONE_DAY

    val UTC_TIME_ZONE = TimeZone.getTimeZone("UTC")

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Date Formats
    ////////////////////////////////////////////////////////////////////////////////////////////////
    const val EXTENDED_FILE_NAME_FORMAT = "yyyy_MM_dd_HH_mm_ss"
    const val EXTENDED_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
    const val MONTH_DAY_YEAR_FORMAT = "MMM dd yyyy"
    const val TIME_FORMAT = "hh:mm a"
    const val TIME_FORMAT_NO_ZERO = "h:mm a"
    const val TIME_FORMAT_NO_AM_PM = "hh:mm"
    const val VERBOSE_DATE_FORMAT = "MMMM dd, yyyy"
    const val TIME_FORMAT_STAMP = "hh:mm:ss"
    const val MONTH_DAY_FORMAT = "MMMM dd"
    const val DATE_TIME_FORMAT = "MM-dd-yyyy hh:mm a"
    const val RFC_3999_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    const val RFC_3999_DATE_TIME_WITH_MILLIS_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val YEAR_MONTH_DAY_FORMAT = "yyyy-MM-dd"
    const val DAY_OF_WEEK_FULL_FORMAT = "EEEE"
    const val DAY_OF_WEEK_SHORT_FORMAT = "E"
    const val MONTH_DAY_YEAR_SLASH_FORMAT = "MM/dd/yyyy"
}

//    /**
//     * Create's a Date object filled with only an hour and minute
//     *
//     * @param hourOfDay hour of day (0-23)
//     * @param minute minute of hour (0-59)
//     * @param timeZone timezone
//     * @return a valid Date object
//     */
//    /**
//     * Create's a Date object filled with only an hour and minute
//     *
//     * @param hourOfDay hour of day (0-23)
//     * @param minute minute of hour (0-59)
//     * @return a valid Date object
//     */
//    @JvmOverloads
//    fun fromHourAndMinute(hourOfDay: Int, minute: Int, timeZone: TimeZone? = utcTimeZone()): Date {
//        val c = Calendar.getInstance(timeZone)
//        c[Calendar.HOUR_OF_DAY] = hourOfDay
//        c[Calendar.MINUTE] = minute
//        return c.time
//    }
//
//    /**
//     * Create's a Date object filled with only year, month, and day
//     * @param year the year
//     * @param month the month
//     * @param day the day
//     * @return a valid Date object
//     */
//    /**
//     * Create's a Date object filled with only year, month, and day
//     * @param year the year
//     * @param month the month
//     * @param day the day
//     * @return a valid Date object
//     */
//    @JvmOverloads
//    fun fromYearMonthDay(
//        year: Int,
//        month: Int,
//        day: Int,
//        timeZone: TimeZone? = utcTimeZone()
//    ): Date {
//        val c = Calendar.getInstance(timeZone)
//        c[Calendar.YEAR] = year
//        c[Calendar.MONTH] = month
//        c[Calendar.DAY_OF_MONTH] = day
//        return c.time
//    }
//
//    /**
//     * Create's a Date object with the date set to today and a specific Hour and Minute pulled from another date object
//     *
//     * @param date the date object to pull the hour and minute from
//     * @return a valid Date object
//     */
//    fun todayWithHourAndMinute(date: Date?): Date {
//        val c = Calendar.getInstance(utcTimeZone())
//        c.time = date
//        val hourOfDay = c[Calendar.HOUR_OF_DAY]
//        val minute = c[Calendar.MINUTE]
//        c.time = today()
//        c[Calendar.HOUR_OF_DAY] = hourOfDay
//        c[Calendar.MINUTE] = minute
//        c[Calendar.SECOND] = 0
//        return c.time
//    }
//
//    /**
//     * Create's a Date object with the time set to midnight and the day, month, year set from another date object
//     * @param date the other date object to pull the day, month, year from
//     * @param tz the timezone
//     * @return a valid date object
//     */
//    fun dateWithMidnight(date: Date?, tz: TimeZone?): Date {
//        val c = Calendar.getInstance(tz)
//        c.time = date
//        c[Calendar.HOUR_OF_DAY] = 0
//        c[Calendar.MINUTE] = 0
//        c[Calendar.SECOND] = 0
//        return c.time
//    }
//
//    /**
//     * Create's a Date object with the time set to midnight and the day, month, year set from another date object
//     * @param date the other date object to pull the day, month, year from
//     * @return a valid date object
//     */
//    fun utcDateWithMidnight(date: Date?): Date {
//        return dateWithMidnight(date, utcTimeZone())
//    }
//
//    /**
//     * Returns the current date
//     *
//     * @return a valid Date object
//     */
//    fun today(): Date {
//        val c = Calendar.getInstance(utcTimeZone())
//        c.time = Date()
//        return c.time
//    }
//
//    /**
//     * Returns a date object representing one day from the current time
//     * @return a valid date object.
//     */
//    fun tomorrow(): Date {
//        return daysFromToday(1)
//    }
//
//    /**
//     * Returns a date object representing a number of days from the current time.  Use a negavite value to indicate a time
//     * in the past
//     * @param days the days to offset
//     * @return a valid date object
//     */
//    fun daysFromToday(days: Int): Date {
//        val c = Calendar.getInstance(utcTimeZone())
//        c.time = today()
//        c.add(Calendar.DAY_OF_YEAR, days)
//        return c.time
//    }
//
//    /**
//     * Returns a date object representing a number of hours from the current time.  Use a negavite value to indicate a time
//     * in the past
//     * @param hours the hours to offset
//     * @return a valid date object
//     */
//    fun hoursFromNow(hours: Int): Date {
//        val c = Calendar.getInstance(utcTimeZone())
//        c.time = today()
//        c.add(Calendar.HOUR_OF_DAY, hours)
//        return c.time
//    }
//
//    /**
//     * Checks two dates to see if they are on the same day of the year
//     *
//     * @param d1 the first date to check
//     * @param d2 the second date to check
//     * @param tz timezone to use
//     * @return boolean if they are on the same day or not
//     */
//    fun areSameDay(d1: Date?, d2: Date?, tz: TimeZone?): Boolean {
//        val c1 = Calendar.getInstance(tz)
//        c1.time = d1
//        val c2 = Calendar.getInstance(tz)
//        c2.time = d2
//        val y1 = c1[Calendar.YEAR]
//        val day1 = c1[Calendar.DAY_OF_YEAR]
//        val y2 = c2[Calendar.YEAR]
//        val day2 = c2[Calendar.DAY_OF_YEAR]
//        return y1 == y2 && day1 == day2
//    }
//
//    /**
//     * Checks two java long dates to see if they are on the same day of the year
//     *
//     * @param time1 the first date to check
//     * @param time2 the second date to check
//     * @param tz timezone to use
//     * @return boolean if they are on the same day or not
//     */
//    fun areSameDay(time1: Long, time2: Long, tz: TimeZone?): Boolean {
//        val c1 = Calendar.getInstance(tz)
//        c1.timeInMillis = time1
//        val c2 = Calendar.getInstance(tz)
//        c2.timeInMillis = time2
//        val y1 = c1[Calendar.YEAR]
//        val day1 = c1[Calendar.DAY_OF_YEAR]
//        val y2 = c2[Calendar.YEAR]
//        val day2 = c2[Calendar.DAY_OF_YEAR]
//        return y1 == y2 && day1 == day2
//    }
//
//    fun isToday(time: Long, tz: TimeZone?): Boolean {
//        return areSameDay(time, System.currentTimeMillis(), tz)
//    }
//
//    fun parseDate(string: String, formatter: String): Date? {
//        return parseDate(string, TimeZone.getDefault(), formatter)
//    }
//
//    fun parseDate(string: String, formatters: Array<String>?): Date? {
//        return parseDate(string, TimeZone.getDefault(), formatters)
//    }
//
//    fun parseUtcDate(string: String, formatter: String): Date? {
//        return parseDate(string, utcTimeZone(), formatter)
//    }
//
//    fun parseUtcDate(string: String, formatters: Array<String>?): Date? {
//        return parseDate(string, utcTimeZone(), formatters)
//    }
//
//    @JvmOverloads
//    fun parseDate(
//        string: String,
//        timeZone: TimeZone?,
//        formatter: String,
//        defaultVal: Date? = null
//    ): Date? {
//        try {
//            if (UUString.isNotEmpty(string)) {
//                val df = SimpleDateFormat(formatter, Locale.US)
//                df.timeZone = timeZone
//                return df.parse(string)
//            }
//        } catch (ex: Exception) {
//            UULog.debug(UUDate::class.java, "parseDate", "Format: $formatter, Input: $string", ex)
//        }
//        return defaultVal
//    }
//
//    @JvmOverloads
//    fun parseDate(
//        string: String,
//        timeZone: TimeZone?,
//        formatters: Array<String>?,
//        defaultVal: Date? = null
//    ): Date? {
//        if (formatters != null) {
//            for (formatter in formatters) {
//                val `val` = parseDate(string, timeZone, formatter, null)
//                if (`val` != null) {
//                    return `val`
//                }
//            }
//        }
//        return defaultVal
//    }
//
//
//
//
//    fun utcTimeZone(): TimeZone {
//        return TimeZone.getTimeZone("UTC")
//    }
//
//
//
//    @JvmOverloads
//    fun formatShortDayOfWeek(javaDate: Long?, tz: TimeZone? = TimeZone.getDefault()): String? {
//        return formatDate(javaDate, DAY_OF_WEEK_SHORT_FORMAT, tz)
//    }
//
//    fun formatFullDayOfWeek(javaDate: Long?): String? {
//        return formatShortDayOfWeek(javaDate, TimeZone.getDefault())
//    }
//
//    fun formatFullDayOfWeekUtc(javaDate: Long?): String? {
//        return formatShortDayOfWeek(javaDate, utcTimeZone())
//    }
//
//    fun formatShortDayOfWeekUTc(javaDate: Long?): String? {
//        return formatShortDayOfWeek(javaDate, utcTimeZone())
//    }
//
//    fun currentUtcTime(): String? {
//        return formatDate(Date(), EXTENDED_DATE_FORMAT, utcTimeZone())
//    }
//
//    fun currentTime(): String? {
//        return formatDate(Date(), EXTENDED_DATE_FORMAT, TimeZone.getDefault())
//    }
//
//    fun currentTimeInFileNameFormat(): String {
//        val df = SimpleDateFormat(EXTENDED_FILE_NAME_FORMAT, Locale.US)
//        val d = Date()
//        return df.format(d)
//    }
//}




fun String.uuParseDate(
    formatter: String,
    timeZone: TimeZone = TimeZone.getDefault(),
    locale: Locale = Locale.getDefault()): Date?
{
    if (isNotEmpty())
    {
        val df = SimpleDateFormat(formatter, locale)
        df.timeZone = timeZone

        return df.parse(this)
    }

    return null
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
    val df = SimpleDateFormat(formatter, locale)
    df.timeZone = timeZone
    return df.format(this)
}