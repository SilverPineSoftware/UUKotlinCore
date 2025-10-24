package com.silverpine.uu.core.test.serialization

import com.silverpine.uu.core.UUDate
import com.silverpine.uu.core.serialization.UULongDateSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val json = Json {
    encodeDefaults = true
    ignoreUnknownKeys = false
    isLenient = false
}

@Serializable
private data class Wrapper(
    @Serializable(with = UULongDateSerializer::class)
    val date: Long
)

class UULongDateSerializerTests
{
    @Test
    fun `descriptor is LONG with expected name`()
    {
        val d = UULongDateSerializer().descriptor
        assertEquals(PrimitiveKind.LONG, d.kind, "Descriptor kind should be LONG (custom serializer over Long)")
        assertEquals("UULongDateSerializer", d.serialName, "Unexpected serializer name")
    }

    @Test
    fun `serialize epoch millis 0 to RFC3339 with millis UTC`()
    {
        val encoded = json.encodeToString(Wrapper(0L))
        // Expect explicit milliseconds and Zulu time
        // Example: {"date":"1970-01-01T00:00:00.000Z"}
        val expected = """{"date":"1970-01-01T00:00:00.000Z"}"""
        assertEquals(expected, encoded, "Epoch 0 should format to RFC3339 with .000Z")
    }

    @Test
    fun `serialize known instant with millis`()
    {
        // 2021-07-01T12:34:56.789Z
        val ts = 1625142896789L
        val encoded = json.encodeToString(Wrapper(ts))
        val expected = """{"date":"2021-07-01T12:34:56.789Z"}"""
        assertEquals(expected, encoded, "Known timestamp should format to expected RFC3339 string")
    }

    @Test
    fun `deserialize valid string to epoch millis`()
    {
        val input = """{"date":"2021-07-01T12:34:56.789Z"}"""
        val decoded = json.decodeFromString<Wrapper>(input)
        assertEquals(1625142896789L, decoded.date, "Should parse RFC3339 with millis back to epoch millis")
    }

    @Test
    fun `round trip preserves value`()
    {
        val values = listOf(
            1L,
            1735689600000L, // 2025-01-01T00:00:00.000Z
            915148800123L,  // 1999-01-01T00:00:00.123Z
            253402300799999L // 9999-12-31T23:59:59.999Z (boundary-ish)
        )

        values.forEach { v ->
            val encoded = json.encodeToString(Wrapper(v))
            val decoded = json.decodeFromString<Wrapper>(encoded)
            assertEquals(v, decoded.date, "Round-trip should preserve value for $v")
        }
    }

    @Test
    fun `deserialize invalid string falls back to 0`()
    {
        val input = """{"date":"not-a-date"}"""
        val decoded = json.decodeFromString<Wrapper>(input)
        assertEquals(0L, decoded.date, "Invalid date strings should deserialize to 0")
    }


    /**
     * A derived serializer that still encodes a Long epoch millis as RFC3339-with-millis (UTC),
     * but can decode from multiple string formats (in order) to maximize compatibility.
     */
    object UULongDateSerializerMulti : UULongDateSerializer(
        encodeFormat = UUDate.Formats.RFC_3339_WITH_MILLIS,
        decodeFormats = arrayOf(
            UUDate.Formats.RFC_3339_WITH_MILLIS, // e.g. 2003-01-02T04:05:06.789Z
            UUDate.Formats.RFC_3339,           // e.g. 2003-01-02T04:05:06Z
            UUDate.Formats.ISO_8601_DATE_AND_TIME    // e.g. 2003-01-02 04:05:06
        ),
        serializerName = "UULongDateSerializerMulti"
    )

    @Serializable
    private data class MultiWrapper(
        @Serializable(with = UULongDateSerializerMulti::class)
        val whenMs: Long
    )

    @Nested
    inner class UULongDateSerializerMultiTests
    {
        // Fixed instant: 2003-01-02T04:05:06.789Z
        private val fixedMsWithMillis = 1_041_480_306_789L
        private val fixedMsNoMillis   = 1_041_480_306_000L

        private val json = Json { ignoreUnknownKeys = true }

        @Test
        fun decodes_rfc3339_with_millis()
        {
            val input = """{"whenMs":"2003-01-02T04:05:06.789Z"}"""
            val obj = json.decodeFromString(MultiWrapper.serializer(), input)
            assertEquals(fixedMsWithMillis, obj.whenMs, "RFC3339-with-millis should parse to exact epoch millis")
        }

        @Test
        fun decodes_rfc3339_no_millis()
        {
            val input = """{"whenMs":"2003-01-02T04:05:06Z"}"""
            val obj = json.decodeFromString(MultiWrapper.serializer(), input)
            assertEquals(fixedMsNoMillis, obj.whenMs, "RFC3339 without millis should parse correctly")
        }

        @Test
        fun decodes_iso8601_date_time_space_separator()
        {
            val input = """{"whenMs":"2003-01-02 04:05:06"}"""
            val obj = json.decodeFromString(MultiWrapper.serializer(), input)
            assertEquals(fixedMsNoMillis, obj.whenMs, "ISO8601 'yyyy-MM-dd HH:mm:ss' (UTC) should parse")
        }

        @Test
        fun encodes_as_rfc3339_with_millis_utc()
        {
            val obj = MultiWrapper(fixedMsWithMillis)
            val out = json.encodeToString(MultiWrapper.serializer(), obj)
            // Expect serialized field as RFC3339-with-millis UTC literal Z
            assertTrue(
                out.contains("\"whenMs\":\"2003-01-02T04:05:06.789Z\""),
                "Serializer must encode as RFC3339-with-millis in UTC"
            )
        }

        @Test
        fun unknown_format_decodes_to_zero()
        {
            val input = """{"whenMs":"not-a-date"}"""
            val obj = json.decodeFromString(MultiWrapper.serializer(), input)
            assertEquals(0L, obj.whenMs, "Unknown formats should decode to 0L to match base serializer behavior")
        }
    }
}
