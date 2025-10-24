@file:Suppress("SpellCheckingInspection")

package com.silverpine.uu.core.test

import android.os.SystemClock
import com.silverpine.uu.core.UUDate
import com.silverpine.uu.core.uuFormatAsExtendedFileName
import com.silverpine.uu.core.uuFormatAsExtendedFileNameUtc
import com.silverpine.uu.core.uuFormatAsIso8601DateOnly
import com.silverpine.uu.core.uuFormatAsIso8601DateOnlyUtc
import com.silverpine.uu.core.uuFormatAsIso8601DateTime
import com.silverpine.uu.core.uuFormatAsIso8601DateTimeUtc
import com.silverpine.uu.core.uuFormatAsIso8601TimeOnly
import com.silverpine.uu.core.uuFormatAsIso8601TimeOnlyUtc
import com.silverpine.uu.core.uuFormatAsRfc3339
import com.silverpine.uu.core.uuFormatAsRfc3339Utc
import com.silverpine.uu.core.uuFormatAsRfc3339WithMillis
import com.silverpine.uu.core.uuFormatAsRfc3339WithMillisUtc
import com.silverpine.uu.core.uuFormatDate
import com.silverpine.uu.core.uuNanoToRealTime
import com.silverpine.uu.core.uuParseDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class UUDateTests
{
    // Fixed instant: 2003-01-02T04:05:06.789Z
    private val fixedEpochMs = 1_041_480_306_789L
    private val fixedDateUTC = Date(fixedEpochMs)

    private fun fmtUTC(date: Date, pattern: String): String
    {
        val df = SimpleDateFormat(pattern, Locale.US)
        df.timeZone = TimeZone.getTimeZone("UTC")
        return df.format(date)
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Constants
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    fun constants_are_correct()
    {
        with(UUDate.Constants) {
            assertEquals(60L, SECONDS_IN_ONE_MINUTE, "secondsInOneMinute")
            assertEquals(60L, MINUTES_IN_ONE_HOUR, "minutesInOneHour")
            assertEquals(24L, HOURS_IN_ONE_DAY, "hoursInOneDay")
            assertEquals(7L, DAYS_IN_ONE_WEEK, "daysInOneWeek")
            assertEquals(1000L, MILLIS_IN_ONE_SECOND, "millisInOneSecond")
            assertEquals(60_000L, MILLIS_IN_ONE_MINUTE, "millisInOneMinute")
            assertEquals(3600L, SECONDS_IN_ONE_HOUR, "secondsInOneHour")
            assertEquals(86_400L, SECONDS_IN_ONE_DAY, "secondsInOneDay")
            assertEquals(604_800L, SECONDS_IN_ONE_WEEK, "secondsInOneWeek")
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    // TimeZones
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    fun timezones_return_expected_ids()
    {
        val utc = UUDate.TimeZones.UTC
        val local = UUDate.TimeZones.LOCAL

        assertEquals("UTC", utc.id, "UTC id")
        assertNotNull(local, "Local timezone not null")
        assertTrue(local.id.isNotEmpty(), "Local timezone id non-empty")
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Formats object (string values)
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    fun formats_have_expected_values()
    {
        with(UUDate.Formats)
        {
            assertEquals("yyyy-MM-dd'T'HH:mm:ss'Z'", RFC_3339, "rfc3339")
            assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", RFC_3339_WITH_MILLIS, "rfc3339WithMillis")
            assertEquals("yyyy-MM-dd", ISO_8601_DATE_ONLY, "iso8601DateOnly")
            assertEquals("HH:mm:ss", ISO_8601_TIME_ONLY, "iso8601TimeOnly")
            assertEquals("yyyy-MM-dd HH:mm:ss", ISO_8601_DATE_AND_TIME, "iso8601DateTime")
            assertEquals("h:mm a", TIME_OF_DAY, "timeOfDay")
            assertEquals("d", DAY_OF_MONTH, "dayOfMonth")
            assertEquals("L", NUMERIC_MONTH_OF_YEAR, "numericMonthOfYear")
            assertEquals("LLL", SHORT_MONTH_OF_YEAR, "shortMonthOfYear")
            assertEquals("LLLL", LONG_MONTH_OF_YEAR, "longMonthOfYear")
            assertEquals("EE", SHORT_DAY_OF_WEEK, "shortDayOfWeek")
            assertEquals("EEEE", LONG_DAY_OF_WEEK, "longDayOfWeek")
            assertEquals("yy", TWO_DIGIT_YEAR, "twoDigitYear")
            assertEquals("yyyy", FOUR_DIGIT_YEAR, "fourDigitYear")
            assertEquals("yyyy_MM_dd_HH_mm_ss", EXTENDED_FILE_NAME_FORMAT, "extendedFileNameFormat")
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    // String.uuParseDate (single formatter)
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    fun string_parseDate_singleFormatter_success_utc()
    {
        val s = "2003-01-02T04:05:06.789Z"
        val parsed = s.uuParseDate(
            formatter = UUDate.Formats.RFC_3339_WITH_MILLIS,
            timeZone = TimeZone.getTimeZone("UTC"),
            locale = Locale.US
        )
        assertNotNull(parsed, "Parsed date not null")
        assertEquals(fixedEpochMs, parsed!!.time, "Parsed epoch ms")
    }

    @Test
    fun string_parseDate_singleFormatter_empty_returnsNull()
    {
        val s = ""
        val parsed = s.uuParseDate(UUDate.Formats.RFC_3339)
        assertNull(parsed, "Empty string returns null")
    }

    @Test
    fun string_parseDate_singleFormatter_wrongPattern_returnsNull()
    {
        val s = "2003-01-02"
        val parsed = s.uuParseDate(UUDate.Formats.RFC_3339_WITH_MILLIS, TimeZone.getTimeZone("UTC"))
        assertNull(parsed, "Wrong pattern returns null")
    }

    ////////////////////////////////////////////////////////////////////////////////
    // String.uuParseDate (multiple formatters)
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    fun string_parseDate_multipleFormatters_firstMatches()
    {
        val s = "2003-01-02T04:05:06.789Z"
        val parsed = s.uuParseDate(
            formatters = arrayOf(
                UUDate.Formats.RFC_3339_WITH_MILLIS,
                UUDate.Formats.RFC_3339
            ),
            timeZone = TimeZone.getTimeZone("UTC"),
            locale = Locale.US
        )
        assertNotNull(parsed, "Parsed with multiple formatters")
        assertEquals(fixedEpochMs, parsed!!.time, "Parsed epoch ms")
    }

    @Test
    fun string_parseDate_multipleFormatters_noneMatch_returnsNull()
    {
        val s = "not-a-date"
        val parsed = s.uuParseDate(arrayOf(UUDate.Formats.ISO_8601_DATE_ONLY))
        assertNull(parsed, "None match returns null")
    }

    @Test
    fun string_parseDate_multipleFormatters_nullArray_returnsNull()
    {
        val parsed = "2003-01-02".uuParseDate(formatters = null)
        assertNull(parsed, "Null formatters returns null")
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Long.uuFormatDate & Date.uuFormatDate
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    fun date_formatDate_basic_utc()
    {
        val expected = fmtUTC(fixedDateUTC, "yyyy-MM-dd HH:mm:ss")
        val actual = fixedDateUTC.uuFormatDate(UUDate.Formats.ISO_8601_DATE_AND_TIME, UUDate.TimeZones.UTC, Locale.US)
        assertEquals(expected, actual, "Date.uuFormatDate with iso8601DateTime UTC")
    }

    @Test
    fun long_formatDate_basic_utc()
    {
        val expected = fmtUTC(fixedDateUTC, "HH:mm:ss")
        val actual = fixedEpochMs.uuFormatDate(UUDate.Formats.ISO_8601_TIME_ONLY, UUDate.TimeZones.UTC, Locale.US)
        assertEquals(expected, actual, "Long.uuFormatDate with iso8601TimeOnly UTC")
    }

    @Test
    fun date_formatDate_returnsEmpty_onFormatError()
    {
        // Use an invalid pattern to trigger the catch path
        val actual = fixedDateUTC.uuFormatDate("invalid-pattern-[", UUDate.TimeZones.UTC, Locale.US)
        assertEquals("", actual, "Invalid pattern returns empty string")
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Convenience Long formatters (UTC variants are exact)
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    fun long_formatAsRfc3339Utc_exact()
    {
        val expected = "2003-01-02T04:05:06Z"
        val actual = fixedEpochMs.uuFormatAsRfc3339Utc(Locale.US)
        assertEquals(expected, actual, "RFC3339 UTC (no millis)")
    }

    @Test
    fun long_formatAsRfc3339WithMillisUtc_exact()
    {
        val expected = "2003-01-02T04:05:06.789Z"
        val actual = fixedEpochMs.uuFormatAsRfc3339WithMillisUtc(Locale.US)
        assertEquals(expected, actual, "RFC3339 UTC with millis")
    }

    @Test
    fun long_formatAsIso8601DateOnlyUtc_exact()
    {
        val expected = "2003-01-02"
        val actual = fixedEpochMs.uuFormatAsIso8601DateOnlyUtc(Locale.US)
        assertEquals(expected, actual, "ISO8601 date only UTC")
    }

    @Test
    fun long_formatAsIso8601TimeOnlyUtc_exact()
    {
        val expected = "04:05:06"
        val actual = fixedEpochMs.uuFormatAsIso8601TimeOnlyUtc(Locale.US)
        assertEquals(expected, actual, "ISO8601 time only UTC")
    }

    @Test
    fun long_formatAsIso8601DateTimeUtc_exact()
    {
        val expected = "2003-01-02 04:05:06"
        val actual = fixedEpochMs.uuFormatAsIso8601DateTimeUtc(Locale.US)
        assertEquals(expected, actual, "ISO8601 date time UTC")
    }

    @Test
    fun long_formatAsExtendedFileNameUtc_exact()
    {
        val expected = "2003_01_02_04_05_06"
        val actual = fixedEpochMs.uuFormatAsExtendedFileNameUtc(Locale.US)
        assertEquals(expected, actual, "Extended filename UTC")
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Convenience Long formatters (local tz) – non-empty sanity checks
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    fun long_formatters_local_returnNonEmpty()
    {
        assertFalse(fixedEpochMs.uuFormatAsRfc3339(locale = Locale.US).isEmpty(), "uuFormatAsRfc3339 non-empty")
        assertFalse(fixedEpochMs.uuFormatAsRfc3339WithMillis(locale = Locale.US).isEmpty(), "uuFormatAsRfc3339WithMillis non-empty")
        assertFalse(fixedEpochMs.uuFormatAsIso8601DateOnly(locale = Locale.US).isEmpty(), "uuFormatAsIso8601DateOnly non-empty")
        assertFalse(fixedEpochMs.uuFormatAsIso8601TimeOnly(locale = Locale.US).isEmpty(), "uuFormatAsIso8601TimeOnly non-empty")
        assertFalse(fixedEpochMs.uuFormatAsIso8601DateTime(locale = Locale.US).isEmpty(), "uuFormatAsIso8601DateTime non-empty")
        assertFalse(fixedEpochMs.uuFormatAsExtendedFileName(locale = Locale.US).isEmpty(), "uuFormatAsExtendedFileName non-empty")
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Long.uuNanoToRealTime
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    fun long_nanoToRealTime_backCalculatesWallClockWithinTolerance()
    {
        val deltaMs = 1234L
        val mockedNow = 1_000_000_000L
        // Use Mockito's static mocking for SystemClock
        @Suppress("RemoveExplicitTypeArguments")
        Mockito.mockStatic(SystemClock::class.java).use { mock ->
            mock.`when`<Long> { SystemClock.elapsedRealtimeNanos() }.thenReturn(mockedNow)
            val thenElapsed = mockedNow - TimeUnit.MILLISECONDS.toNanos(deltaMs)
            val approxWallClock = thenElapsed.uuNanoToRealTime()
            val expected = System.currentTimeMillis() - deltaMs
            val toleranceMs = 50L
            assertTrue(
                abs(approxWallClock - expected) <= toleranceMs,
                "uuNanoToRealTime is within tolerance"
            )
        }
    }


}