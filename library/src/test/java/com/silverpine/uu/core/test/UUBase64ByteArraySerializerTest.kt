package com.silverpine.uu.core.test

import com.silverpine.uu.core.UUBase64ByteArraySerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertEquals

class UUBase64ByteArraySerializerTest
{
    @Serializable
    data class TestData(
        @Serializable(with = UUBase64ByteArraySerializer::class)
        val payload: ByteArray
    ) {
        override fun equals(other: Any?): Boolean
        {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TestData

            if (!payload.contentEquals(other.payload)) return false

            return true
        }

        override fun hashCode(): Int
        {
            return payload.contentHashCode()
        }
    }

    private val json = Json { encodeDefaults = true }

    @Test
    fun `serialize simple byte array`()
    {
        val data = TestData("Hello".toByteArray())
        val encoded = json.encodeToString(data)

        // "Hello" in Base64 is "SGVsbG8="
        assertTrue(encoded.contains("SGVsbG8="))
    }

    @Test
    fun `deserialize base64 string`()
    {
        val jsonString = """{"payload":"SGVsbG8="}"""
        val decoded = json.decodeFromString<TestData>(jsonString)

        assertEquals("Hello", String(decoded.payload))
    }

    @Test
    fun `round trip serialization and deserialization`()
    {
        val original = TestData("KotlinX".toByteArray())
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<TestData>(encoded)

        assertArrayEquals(original.payload, decoded.payload)
    }

    @Test
    fun `empty byte array`()
    {
        val original = TestData(ByteArray(0))
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<TestData>(encoded)

        assertArrayEquals(original.payload, decoded.payload)
        assertTrue(encoded.contains("\"\"")) // empty string in JSON
    }

    @Test
    fun `binary data with non-text bytes`()
    {
        val bytes = byteArrayOf(0x00, 0x01, 0x02, 0x7F, -0x01) // includes 0xFF
        val original = TestData(bytes)
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<TestData>(encoded)

        assertArrayEquals(bytes, decoded.payload)
    }
}