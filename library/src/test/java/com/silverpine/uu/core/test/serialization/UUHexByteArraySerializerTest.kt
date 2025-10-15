package com.silverpine.uu.core.test.serialization

import com.silverpine.uu.core.serialization.UUHexByteArraySerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UUHexByteArraySerializerTest
{
    @Serializable
    data class TestData(
        @Serializable(with = UUHexByteArraySerializer::class)
        val nonNullPayload: ByteArray,

        @Serializable(with = UUHexByteArraySerializer::class)
        val nullablePayload: ByteArray? = null
    )
    {
        override fun equals(other: Any?): Boolean
        {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TestData

            if (!nonNullPayload.contentEquals(other.nonNullPayload)) return false
            if (!nullablePayload.contentEquals(other.nullablePayload)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = nonNullPayload.contentHashCode()
            result = 31 * result + (nullablePayload?.contentHashCode() ?: 0)
            return result
        }
    }

    private val json = Json { encodeDefaults = true }

    @Test
    fun `serialize non-null payload to hex`()
    {
        val data = TestData("Hi".toByteArray())
        val encoded = json.encodeToString(data)

        // "Hi" in hex is 4869
        assertTrue(encoded.contains("4869"))
    }

    @Test
    fun `deserialize non-null hex string`()
    {
        val jsonString = """{"nonNullPayload":"4869"}"""
        val decoded = json.decodeFromString<TestData>(jsonString)

        assertEquals("Hi", String(decoded.nonNullPayload))
        assertNull(decoded.nullablePayload)
    }

    @Test
    fun `round trip with nullable null`()
    {
        val original = TestData("Hello".toByteArray(), null)
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<TestData>(encoded)

        assertArrayEquals(original.nonNullPayload, decoded.nonNullPayload)
        assertNull(decoded.nullablePayload)
    }

    @Test
    fun `round trip with nullable non-null`()
    {
        val original = TestData("Hello".toByteArray(), "World".toByteArray())
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<TestData>(encoded)

        assertArrayEquals(original.nonNullPayload, decoded.nonNullPayload)
        assertArrayEquals(original.nullablePayload, decoded.nullablePayload)
    }

    @Test
    fun `empty byte array`()
    {
        val original = TestData(ByteArray(0))
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<TestData>(encoded)

        assertArrayEquals(original.nonNullPayload, decoded.nonNullPayload)
    }
}