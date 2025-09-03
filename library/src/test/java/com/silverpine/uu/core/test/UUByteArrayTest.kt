@file:Suppress("SpellCheckingInspection")

package com.silverpine.uu.core.test

import com.silverpine.uu.core.uuAscii
import com.silverpine.uu.core.uuBase64
import com.silverpine.uu.core.uuBcd16
import com.silverpine.uu.core.uuBcd24
import com.silverpine.uu.core.uuBcd32
import com.silverpine.uu.core.uuBcd8
import com.silverpine.uu.core.uuHighNibble
import com.silverpine.uu.core.uuLowNibble
import com.silverpine.uu.core.uuReadInt16
import com.silverpine.uu.core.uuReadInt32
import com.silverpine.uu.core.uuReadInt64
import com.silverpine.uu.core.uuReadInt8
import com.silverpine.uu.core.uuReadUInt16
import com.silverpine.uu.core.uuReadUInt24
import com.silverpine.uu.core.uuReadUInt32
import com.silverpine.uu.core.uuReadUInt64
import com.silverpine.uu.core.uuReadUInt8
import com.silverpine.uu.core.uuString
import com.silverpine.uu.core.uuSubData
import com.silverpine.uu.core.uuToHexData
import com.silverpine.uu.core.uuToHex
import com.silverpine.uu.core.uuUtf8
import com.silverpine.uu.core.uuWriteInt16
import com.silverpine.uu.core.uuWriteInt32
import com.silverpine.uu.core.uuWriteInt64
import com.silverpine.uu.core.uuWriteInt8
import com.silverpine.uu.core.uuWriteUInt16
import com.silverpine.uu.core.uuWriteUInt32
import com.silverpine.uu.core.uuWriteUInt64
import com.silverpine.uu.core.uuWriteUInt8
import org.junit.Assert
import org.junit.Test
import java.nio.ByteOrder
import java.util.Base64
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.junit.JUnitAsserter.assertNotEquals

class UUByteArrayTest
{
    internal class InputPair<T>(
        var order: ByteOrder,
        var source: String,
        var expected: T?,
        var index: Int = 0)
    {
        constructor(src: String, exp: T?, idx: Int = 0): this(ByteOrder.LITTLE_ENDIAN, src, exp, idx)

        fun bytes(): ByteArray
        {
            return source.uuToHexData()!!
        }
    }

    internal class NonNullInputPair<T>(
        var order: ByteOrder,
        var source: String,
        var expected: T,
        var index: Int = 0)
    {
        constructor(src: String, exp: T, idx: Int = 0): this(ByteOrder.LITTLE_ENDIAN, src, exp, idx)

        fun bytes(): ByteArray
        {
            return source.uuToHexData()!!
        }
    }

    @Test
    fun test_uuToHexString_emptyArray()
    {
        val input = ByteArray(4)
        val actual = input.uuToHex()
        val expected = "00000000"
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun test_uuToHexString_max()
    {
        val input = byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte())
        val actual = input.uuToHex()
        val expected = "FFFFFFFF"
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun test_uuToHexString_rando()
    {
        val input = byteArrayOf(0xAB.toByte(), 0xCD.toByte(), 0xEF.toByte(), 0x12.toByte())
        val actual = input.uuToHex()
        val expected = "ABCDEF12"
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun uuString_validUtf8_success()
    {
        val input = "Hello, World!"
        val bytes = input.toByteArray(Charsets.UTF_8)

        val result = bytes.uuString(Charsets.UTF_8)

        assertTrue(result.isSuccess, "Expected success result")
        assertEquals(input, result.getOrNull(), "Decoded string mismatch")
    }

    @Test
    fun uuString_emptyArray_successEmptyString()
    {
        val bytes = ByteArray(0)

        val result = bytes.uuString(Charsets.UTF_8)

        assertTrue(result.isSuccess, "Expected success for empty array")
        assertEquals("", result.getOrNull(), "Expected empty string")
    }

    @Test
    fun uuString_asciiEncoding_success()
    {
        val input = "ASCII TEXT"
        val bytes = input.toByteArray(Charsets.US_ASCII)

        val result = bytes.uuString(Charsets.US_ASCII)

        assertTrue(result.isSuccess, "Expected success result")
        assertEquals(input, result.getOrNull(), "Decoded string mismatch for ASCII")
    }

    @Test
    fun uuUtf8_validInput_success()
    {
        val input = "Hello UTF-8 👋"
        val bytes = input.toByteArray(Charsets.UTF_8)

        val result = bytes.uuUtf8()

        assertTrue(result.isSuccess, "Expected success result")
        assertEquals(input, result.getOrNull(), "Decoded string mismatch for UTF-8")
    }

    @Test
    fun uuUtf8_emptyArray_returnsEmptyString()
    {
        val bytes = ByteArray(0)

        val result = bytes.uuUtf8()

        assertTrue(result.isSuccess, "Expected success result for empty input")
        assertEquals("", result.getOrNull(), "Expected empty string for empty input")
    }

    @Test
    fun uuAscii_validInput_success()
    {
        val input = "Plain ASCII Text 123"
        val bytes = input.toByteArray(Charsets.US_ASCII)

        val result = bytes.uuAscii()

        assertTrue(result.isSuccess, "Expected success result")
        assertEquals(input, result.getOrNull(), "Decoded string mismatch for ASCII")
    }

    @Test
    fun uuAscii_emptyArray_returnsEmptyString()
    {
        val bytes = ByteArray(0)

        val result = bytes.uuAscii()

        assertTrue(result.isSuccess, "Expected success result for empty input")
        assertEquals("", result.getOrNull(), "Expected empty string for empty input")
    }

    @Test
    fun uuBase64_standard_success()
    {
        val input = "Hello".toByteArray()
        val result = input.uuBase64() // default Base64.getEncoder()

        assertTrue(result.isSuccess)
        assertEquals("SGVsbG8=", result.getOrNull())
    }

    @Test
    fun uuBase64_urlSafe_success_differsFromStandard()
    {
        // bytes that produce '+' and '/' in standard Base64
        val input = byteArrayOf(0xFB.toByte(), 0xEF.toByte(), 0xFF.toByte(), 0x00)

        val std = input.uuBase64(Base64.getEncoder())
        val url = input.uuBase64(Base64.getUrlEncoder())

        assertTrue(std.isSuccess); assertTrue(url.isSuccess)

        // Standard uses '+', '/', URL-safe uses '-', '_'
        assertNotEquals("Expect different outputs", std.getOrNull(), url.getOrNull())
    }

    @Test
    fun uuBase64_empty_success()
    {
        val input = ByteArray(0)

        val result = input.uuBase64()

        assertTrue(result.isSuccess, "Empty input should succeed" )
        assertEquals("", result.getOrNull())
    }

    @Test
    fun uuBase64_bad_encoder()
    {
        val input = ByteArray(0)

        val result = input.uuBase64()

        assertTrue(result.isSuccess, "Empty input should succeed")
        assertEquals("", result.getOrNull())
    }

    @Test
    fun test_uuSubData()
    {
        val input = "AABBCCDDEEFF00112233445566778899".uuToHexData()
        Assert.assertNotNull(input)

        var sub = input?.uuSubData(0, 3)
        Assert.assertNotNull(sub)

        Assert.assertArrayEquals(sub, byteArrayOf(0xAA.toByte(), 0xBB.toByte(), 0xCC.toByte()))
        sub = input?.uuSubData(0, 500)
        Assert.assertNotNull(sub)
        Assert.assertArrayEquals(sub, input)
        sub = input?.uuSubData( 13, 7)
        Assert.assertNotNull(sub)
        Assert.assertArrayEquals(sub, byteArrayOf(0x77.toByte(), 0x88.toByte(), 0x99.toByte()))
    }

    @Test
    fun test_uuReadInt8()
    {
        val testInput = ArrayList<InputPair<Byte>>()
        testInput.add(InputPair("FF", 0xFF.toByte()))
        testInput.add(InputPair("00", 0x00.toByte()))
        testInput.add(InputPair("11", 0x11.toByte()))
        testInput.add(InputPair("BB", 0xBB.toByte()))
        testInput.add(InputPair("80", Byte.MIN_VALUE))
        testInput.add(InputPair("7F", Byte.MAX_VALUE))
        testInput.add(InputPair("AABBCCDDEEFF", 0xCC.toByte(), 2))
        testInput.add(InputPair("AABBCCDDEEFF", 0xEE.toByte(), 4))
        testInput.add(InputPair("AABBCCDDEEFF", 0xFF.toByte(), 5))

        // Test some positive cases
        for (ti in testInput)
        {
            val actual = ti.bytes().uuReadInt8(ti.index).getOrNull()
            val expected = ti.expected
            if (expected != null)
            {
                assertNotNull(actual)
                Assert.assertEquals(expected.toLong(), actual.toLong())
            }
            else
            {
                assertNull(actual)
            }
        }

        // Index negative
        assertNull(byteArrayOf().uuReadInt8(-1).getOrNull())

        // Index greater than buffer.length
        assertNull(byteArrayOf().uuReadInt8( 1).getOrNull())
    }

    @Test
    fun test_uuRest_uuReadInt16()
    {
        val testInput = ArrayList<InputPair<Short>>()
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "FFFF", 0xFFFF.toShort()))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "0000", 0x0000.toShort()))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "1122", 0x2211.toShort()))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "BBDD", 0xDDBB.toShort()))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "0080", Short.MIN_VALUE))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "FF7F", Short.MAX_VALUE))
        testInput.add(InputPair(ByteOrder.BIG_ENDIAN, "AABBCCDDEEFF", 0xCCDD.toShort(), 2))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "AABBCCDDEEFF", 0xEEDD.toShort(), 3))

        for (ti in testInput)
        {
            val actual = ti.bytes().uuReadInt16(ti.order, ti.index).getOrNull()
            val expected = ti.expected
            if (expected != null)
            {
                assertNotNull(actual)
                Assert.assertEquals(expected.toLong(), actual.toLong())
            }
            else
            {
                assertNull(actual)
            }
        }

        // Index negative
        assertNull(byteArrayOf().uuReadInt16(ByteOrder.nativeOrder(), -1).getOrNull())

        // Index greater than buffer.length
        assertNull(byteArrayOf().uuReadInt16(ByteOrder.nativeOrder(), 1).getOrNull())
    }

    @Test
    fun test_uuReadInt32()
    {
        val testInput = ArrayList<InputPair<Int>>()
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "FFFFFFFF", -0x1))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "00000000", 0))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "11223344", 0x44332211))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "AABBCCDD", -0x22334456))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "00000080", Int.MIN_VALUE))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "FFFFFF7F", Int.MAX_VALUE))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "1234AABBCCDD9876", -0x22334456, 2))

        for (ti in testInput)
        {
            val actual = ti.bytes().uuReadInt32(ti.order, ti.index).getOrNull()
            val expected = ti.expected
            if (expected != null)
            {
                assertNotNull(actual)
                Assert.assertEquals(expected.toLong(), actual.toLong())
            }
            else
            {
                assertNull(actual)
            }
        }

        // Index negative
        assertNull(byteArrayOf().uuReadInt32(ByteOrder.nativeOrder(), -1).getOrNull())

        // Index greater than buffer.length
        assertNull(byteArrayOf().uuReadInt32(ByteOrder.nativeOrder(), 2).getOrNull())
    }

    @Test
    fun test_uuReadInt64()
    {
        val testInput = ArrayList<InputPair<Long>>()
        testInput.add(InputPair(ByteOrder.BIG_ENDIAN, "FFFFFFFFFFFFFFFF", -0x1L))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "FFFFFFFFFFFFFFFF", -0x1L))
        testInput.add(InputPair(ByteOrder.BIG_ENDIAN, "0000000000000000", 0x0000000000000000L))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "0000000000000000", 0x0000000000000000L))
        testInput.add(InputPair(ByteOrder.BIG_ENDIAN, "1234567890ABCDEF", 0x1234567890ABCDEFL))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "1234567890ABCDEF", -0x1032546f87a9cbeeL))
        testInput.add(InputPair(ByteOrder.BIG_ENDIAN, "1122334455667788", 0x1122334455667788L))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "1122334455667788", -0x778899aabbccddefL))
        testInput.add(InputPair(ByteOrder.BIG_ENDIAN, "8000000000000000", Long.MIN_VALUE))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "0000000000000080", Long.MIN_VALUE))
        testInput.add(InputPair(ByteOrder.BIG_ENDIAN, "7FFFFFFFFFFFFFFF", Long.MAX_VALUE))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "FFFFFFFFFFFFFF7F", Long.MAX_VALUE))
        testInput.add(
            InputPair(
                ByteOrder.LITTLE_ENDIAN,
                "ABCD1122334455667788FFDD",
                -0x778899aabbccddefL,
                2
            )
        )

        for (ti in testInput)
        {
            val actual = ti.bytes().uuReadInt64(ti.order, ti.index).getOrNull()
            val expected = ti.expected
            if (expected != null)
            {
                assertNotNull(actual)
                Assert.assertEquals(expected, actual)
            }
            else
            {
                assertNull(actual)
            }
        }

        // Index negative
        assertNull(byteArrayOf().uuReadInt64(ByteOrder.nativeOrder(), -1).getOrNull())

        // Index greater than buffer.length
        assertNull(byteArrayOf().uuReadInt64(ByteOrder.nativeOrder(), 2).getOrNull())
    }

    @Test
    fun test_uuReadUInt8()
    {
        val testInput = ArrayList<InputPair<UByte>>()
        testInput.add(InputPair("FF", 0xFFu))
        testInput.add(InputPair("00", 0x00u))
        testInput.add(InputPair("11", 0x11u))
        testInput.add(InputPair("BB", 0xBBu))
        testInput.add(InputPair("80", 0x80u))
        testInput.add(InputPair("7F", 0x7Fu))
        testInput.add(InputPair("AABBCCDDEEFF", 0xCCu, 2))
        testInput.add(InputPair("AABBCCDDEEFF", 0xEEu, 4))
        testInput.add(InputPair("AABBCCDDEEFF", 0xFFu, 5))

        // Test some positive cases
        for (ti in testInput)
        {
            val actual = ti.bytes().uuReadUInt8(ti.index).getOrNull()
            val expected = ti.expected
            if (expected != null)
            {
                assertNotNull(actual)
                Assert.assertEquals(expected.toLong(), actual.toLong())
            }
            else
            {
                assertNull(actual)
            }
        }

        // Index negative
        assertNull(byteArrayOf().uuReadUInt8(-1).getOrNull())

        // Index greater than buffer.length
        assertNull(byteArrayOf().uuReadUInt8(1).getOrNull())
    }

    @Test
    fun test_uuReadUInt16()
    {
        val testInput = ArrayList<InputPair<Int>>()
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "FFFF", 0xFFFF))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "0000", 0x0000))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "1122", 0x2211))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "BBDD", 0xDDBB))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "0080", 0x8000))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "FF7F", 0x7FFF))
        testInput.add(InputPair(ByteOrder.BIG_ENDIAN, "AABBCCDDEEFF", 0xCCDD, 2))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "AABBCCDDEEFF", 0xEEDD, 3))

        for (ti in testInput)
        {
            val actual = ti.bytes().uuReadUInt16(ti.order, ti.index).getOrNull()
            val expected = ti.expected
            if (expected != null)
            {
                assertNotNull(actual)
                Assert.assertEquals(expected.toLong(), actual.toLong())
            }
            else
            {
                assertNull(actual)
            }
        }

        // Index negative
        assertNull(byteArrayOf().uuReadUInt16(ByteOrder.nativeOrder(), -1).getOrNull())

        // Index greater than buffer.length
        assertNull(byteArrayOf().uuReadUInt16(ByteOrder.nativeOrder(), 1).getOrNull())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun test_uuReadUInt24()
    {
        val testInput = ArrayList<InputPair<Int>>()
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "FFFFFF", 0xFFFFFF))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "000000", 0))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "112233", 0x332211))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "AABBCC", 0xCCBBAA))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "000080", 0x800000))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "FFFF7F", 0x7FFFFF))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "1234AABBCCDD9876", 0xCCBBAA, 2))
        testInput.add(InputPair(ByteOrder.BIG_ENDIAN, "1234AABBCCDD9876", 0xAABBCC, 2))

        for (ti in testInput)
        {
            println("Trying: ${ti.bytes().toHexString()}, order: ${ti.order}, index: ${ti.index}")
            val actual = ti.bytes().uuReadUInt24(ti.order, ti.index).getOrNull()
            val expected = ti.expected
            if (expected != null)
            {
                assertNotNull(actual)
                Assert.assertEquals(expected.toLong(), actual.toLong())
            }
            else
            {
                assertNull(actual)
            }
        }

        // Index negative
        assertNull(byteArrayOf().uuReadUInt24(ByteOrder.nativeOrder(), -1).getOrNull())

        // Index greater than buffer.length
        assertNull(byteArrayOf().uuReadUInt24(ByteOrder.nativeOrder(), 2).getOrNull())
    }

    @Test
    fun test_uuReadUInt32()
    {
        val testInput = ArrayList<InputPair<Long>>()
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "FFFFFFFF", 0xFFFFFFFFL))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "00000000", 0L))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "11223344", 0x44332211L))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "AABBCCDD", 0xDDCCBBAAL))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "00000080", 0x80000000L))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "FFFFFF7F", 0x7FFFFFFFL))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "1234AABBCCDD9876", 0xDDCCBBAAL, 2))
        testInput.add(InputPair(ByteOrder.BIG_ENDIAN, "1234AABBCCDD9876", 0xAABBCCDDL, 2))

        for (ti in testInput)
        {
            val actual = ti.bytes().uuReadUInt32(ti.order, ti.index).getOrNull()
            val expected = ti.expected
            if (expected != null)
            {
                assertNotNull(actual)
                Assert.assertEquals(expected.toLong(), actual.toLong())
            }
            else
            {
                assertNull(actual)
            }
        }

        // Index negative
        assertNull(byteArrayOf().uuReadUInt32(ByteOrder.nativeOrder(), -1).getOrNull())

        // Index greater than buffer.length
        assertNull(byteArrayOf().uuReadUInt32(ByteOrder.nativeOrder(), 2).getOrNull())
    }

    @Test
    fun test_uuReadUInt64()
    {
        val testInput = ArrayList<InputPair<ULong>>()
        testInput.add(InputPair(ByteOrder.BIG_ENDIAN, "FFFFFFFFFFFFFFFF", 0xFFFFFFFFFFFFFFFFu))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "FFFFFFFFFFFFFFFF", 0xFFFFFFFFFFFFFFFFu))
        testInput.add(InputPair(ByteOrder.BIG_ENDIAN, "0000000000000000", 0x0000000000000000u))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "0000000000000000", 0x0000000000000000u))
        testInput.add(InputPair(ByteOrder.BIG_ENDIAN, "1234567890ABCDEF", 0x1234567890ABCDEFu))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "1234567890ABCDEF", 0xEFCDAB9078563412u))
        testInput.add(InputPair(ByteOrder.BIG_ENDIAN, "1122334455667788", 0x1122334455667788u))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "1122334455667788", 0x8877665544332211u))
        testInput.add(InputPair(ByteOrder.BIG_ENDIAN, "8000000000000000", 0x8000000000000000u))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "8000000000000000", 0x0000000000000080u))
        testInput.add(InputPair(ByteOrder.BIG_ENDIAN, "FFFFFFFFFFFFFFFF", ULong.MAX_VALUE))
        testInput.add(InputPair(ByteOrder.LITTLE_ENDIAN, "FFFFFFFFFFFFFFFF", ULong.MAX_VALUE))
        testInput.add(
            InputPair(
                ByteOrder.LITTLE_ENDIAN,
                "ABCD1122334455667788FFDD",
                0x8877665544332211u,
                2
            )
        )
        for (ti in testInput)
        {
            println("input: ${ti.bytes().uuToHex()}")
            val actual = ti.bytes().uuReadUInt64(ti.order, ti.index).getOrNull()
            val expected = ti.expected
            if (expected != null)
            {
                assertNotNull(actual)
                Assert.assertEquals(expected, actual)
            }
            else
            {
                assertNull(actual)
            }
        }

        // Index negative
        assertNull(byteArrayOf().uuReadUInt64(ByteOrder.nativeOrder(), -1).getOrNull())

        // Index greater than buffer.length
        assertNull(byteArrayOf().uuReadUInt64(ByteOrder.nativeOrder(), 2).getOrNull())
    }

    @Test
    fun test_uuWriteInt8()
    {
        val testInput = ArrayList<NonNullInputPair<Byte>>()
        testInput.add(NonNullInputPair("FF", 0xFF.toByte()))
        testInput.add(NonNullInputPair("00", 0x00.toByte()))
        testInput.add(NonNullInputPair("11", 0x11.toByte()))
        testInput.add(NonNullInputPair("BB", 0xBB.toByte()))
        testInput.add(NonNullInputPair("80", Byte.MIN_VALUE))
        testInput.add(NonNullInputPair("7F", Byte.MAX_VALUE))
        testInput.add(NonNullInputPair("AABBCCDDEEFF", 0xCC.toByte(), 2))
        testInput.add(NonNullInputPair("AABBCCDDEEFF", 0xEE.toByte(), 4))
        testInput.add(NonNullInputPair("AABBCCDDEEFF", 0xFF.toByte(), 5))

        // Test some positive cases
        for (ti in testInput)
        {
            val original: ByteArray = ti.bytes().clone()
            val working: ByteArray = ti.bytes().clone()
            var actual = working.uuReadInt8(ti.index).getOrNull()
            assertNotNull(actual)
            var expected = ti.expected
            Assert.assertEquals(expected, actual)
            val tmp: Byte = 57
            var written: Int = working.uuWriteInt8(ti.index, tmp).getOrDefault(0)
            Assert.assertEquals(java.lang.Byte.BYTES.toLong(), written.toLong())
            actual = working.uuReadInt8(ti.index).getOrNull()
            assertNotNull(actual)
            expected = tmp
            Assert.assertEquals(expected.toLong(), actual.toLong())
            written = working.uuWriteInt8(ti.index, ti.expected).getOrDefault(0)
            Assert.assertEquals(java.lang.Byte.BYTES.toLong(), written.toLong())
            Assert.assertArrayEquals(working, original)
        }

        // Try some negative test cases

        // Index negative
        assertNull(byteArrayOf().uuWriteInt8(-1, 0.toByte()).getOrNull())

        // Index greater than buffer.length
        assertNull(byteArrayOf().uuWriteInt8(1, 0.toByte()).getOrNull())
    }

    @Test
    fun test_uuWriteInt16()
    {
        val testInput = ArrayList<NonNullInputPair<Short>>()
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "FFFF", 0xFFFF.toShort()))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "0000", 0x0000.toShort()))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "1122", 0x2211.toShort()))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "BBDD", 0xDDBB.toShort()))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "0080", Short.MIN_VALUE))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "FF7F", Short.MAX_VALUE))
        testInput.add(NonNullInputPair(ByteOrder.BIG_ENDIAN, "AABBCCDDEEFF", 0xCCDD.toShort(), 2))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "AABBCCDDEEFF", 0xEEDD.toShort(), 3))
        for (ti in testInput)
        {
            val original: ByteArray = ti.bytes().clone()
            val working: ByteArray = ti.bytes().clone()
            var actual = working.uuReadInt16(ti.order, ti.index).getOrNull()
            assertNotNull(actual)
            var expected = ti.expected
            Assert.assertEquals(expected.toLong(), actual.toLong())
            val tmp: Short = 57
            var written: Int = working.uuWriteInt16(ti.order, ti.index, tmp).getOrDefault(0)
            Assert.assertEquals(java.lang.Short.BYTES.toLong(), written.toLong())
            actual = working.uuReadInt16(ti.order, ti.index).getOrNull()
            assertNotNull(actual)
            expected = tmp
            Assert.assertEquals(expected.toLong(), actual.toLong())
            written = working.uuWriteInt16(ti.order, ti.index, ti.expected).getOrDefault(0)
            Assert.assertEquals(java.lang.Short.BYTES.toLong(), written.toLong())
            Assert.assertArrayEquals(working, original)
        }

        // Index negative
        assertNull(byteArrayOf().uuWriteInt16(ByteOrder.nativeOrder(), -1, 0.toShort()).getOrNull())

        // Index greater than buffer.length
        assertNull(byteArrayOf().uuWriteInt16(ByteOrder.nativeOrder(), 1, 0.toShort()).getOrNull())
    }

    @Test
    fun test_uuWriteInt32()
    {
        val testInput = ArrayList<NonNullInputPair<Int>>()
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "FFFFFFFF", -0x1))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "00000000", 0))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "11223344", 0x44332211))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "AABBCCDD", -0x22334456))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "00000080", Int.MIN_VALUE))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "FFFFFF7F", Int.MAX_VALUE))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "1234AABBCCDD9876", -0x22334456, 2))

        for (ti in testInput)
        {
            val original: ByteArray = ti.bytes().clone()
            val working: ByteArray = ti.bytes().clone()
            var actual = working.uuReadInt32(ti.order, ti.index).getOrNull()
            assertNotNull(actual)
            var expected = ti.expected
            Assert.assertEquals(expected.toLong(), actual.toLong())
            val tmp = 57
            var written: Int = working.uuWriteInt32(ti.order, ti.index, tmp).getOrDefault(0)
            Assert.assertEquals(Integer.BYTES.toLong(), written.toLong())
            actual = working.uuReadInt32(ti.order, ti.index).getOrNull()
            assertNotNull(actual)
            expected = tmp
            Assert.assertEquals(expected.toLong(), actual.toLong())
            written = working.uuWriteInt32(ti.order, ti.index, ti.expected).getOrDefault(0)
            Assert.assertEquals(Integer.BYTES.toLong(), written.toLong())
            Assert.assertArrayEquals(working, original)
        }

        // Index negative
        assertNull(byteArrayOf().uuWriteInt32(ByteOrder.nativeOrder(), -1, 0).getOrNull())

        // Index greater than buffer.length
        assertNull(byteArrayOf().uuWriteInt32(ByteOrder.nativeOrder(), 2, 0).getOrNull())
    }

    @Test
    fun test_uuWriteInt64()
    {
        val testInput = ArrayList<NonNullInputPair<Long>>()
        testInput.add(NonNullInputPair(ByteOrder.BIG_ENDIAN, "FFFFFFFFFFFFFFFF", -0x1L))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "FFFFFFFFFFFFFFFF", -0x1L))
        testInput.add(NonNullInputPair(ByteOrder.BIG_ENDIAN, "0000000000000000", 0x0000000000000000L))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "0000000000000000", 0x0000000000000000L))
        testInput.add(NonNullInputPair(ByteOrder.BIG_ENDIAN, "1234567890ABCDEF", 0x1234567890ABCDEFL))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "1234567890ABCDEF", -0x1032546f87a9cbeeL))
        testInput.add(NonNullInputPair(ByteOrder.BIG_ENDIAN, "1122334455667788", 0x1122334455667788L))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "1122334455667788", -0x778899aabbccddefL))
        testInput.add(NonNullInputPair(ByteOrder.BIG_ENDIAN, "8000000000000000", Long.MIN_VALUE))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "0000000000000080", Long.MIN_VALUE))
        testInput.add(NonNullInputPair(ByteOrder.BIG_ENDIAN, "7FFFFFFFFFFFFFFF", Long.MAX_VALUE))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "FFFFFFFFFFFFFF7F", Long.MAX_VALUE))
        testInput.add(
            NonNullInputPair(
                ByteOrder.LITTLE_ENDIAN,
                "ABCD1122334455667788FFDD",
                -0x778899aabbccddefL,
                2
            )
        )
        for (ti in testInput)
        {
            val original: ByteArray = ti.bytes().clone()
            val working: ByteArray = ti.bytes().clone()
            var actual = working.uuReadInt64(ti.order, ti.index).getOrNull()
            assertNotNull(actual)
            var expected = ti.expected
            Assert.assertEquals(expected, actual)
            val tmp: Long = 57
            var written: Int = working.uuWriteInt64(ti.order, ti.index, tmp).getOrDefault(0)
            Assert.assertEquals(java.lang.Long.BYTES.toLong(), written.toLong())
            actual = working.uuReadInt64(ti.order, ti.index).getOrNull()
            assertNotNull(actual)
            expected = tmp
            Assert.assertEquals(expected, actual)
            written = working.uuWriteInt64(ti.order, ti.index, ti.expected).getOrDefault(0)
            Assert.assertEquals(java.lang.Long.BYTES.toLong(), written.toLong())
            Assert.assertArrayEquals(working, original)
        }

        // Index negative
        assertNull(byteArrayOf().uuWriteInt64(ByteOrder.nativeOrder(), -1, 0L).getOrNull())

        // Index greater than buffer.length
        assertNull(byteArrayOf().uuWriteInt64(ByteOrder.nativeOrder(), 2, 0L).getOrNull())
    }

    @Test
    fun test_uuWriteUInt8()
    {
        val testInput = ArrayList<NonNullInputPair<UByte>>()
        testInput.add(NonNullInputPair("FF", 0xFFu))
        testInput.add(NonNullInputPair("00", 0x00u))
        testInput.add(NonNullInputPair("11", 0x11u))
        testInput.add(NonNullInputPair("BB", 0xBBu))
        testInput.add(NonNullInputPair("80", 0x80u))
        testInput.add(NonNullInputPair("7F", 0x7Fu))
        testInput.add(NonNullInputPair("AABBCCDDEEFF", 0xCCu, 2))
        testInput.add(NonNullInputPair("AABBCCDDEEFF", 0xEEu, 4))
        testInput.add(NonNullInputPair("AABBCCDDEEFF", 0xFFu, 5))

        // Test some positive cases
        for (ti in testInput)
        {
            val original: ByteArray = ti.bytes().clone()
            val working: ByteArray = ti.bytes().clone()
            var actual = working.uuReadUInt8(ti.index).getOrNull()
            assertNotNull(actual)
            var expected = ti.expected
            Assert.assertEquals(expected.toLong(), actual.toLong())
            val tmp: UByte = 57u
            var written: Int = working.uuWriteUInt8(ti.index, tmp).getOrDefault(0)
            Assert.assertEquals(java.lang.Byte.BYTES.toLong(), written.toLong())
            actual = working.uuReadUInt8(ti.index).getOrNull()
            assertNotNull(actual)
            expected = tmp
            Assert.assertEquals(expected.toLong(), actual.toLong())
            written = working.uuWriteUInt8(ti.index, ti.expected).getOrDefault(0)
            Assert.assertEquals(java.lang.Byte.BYTES.toLong(), written.toLong())
            Assert.assertArrayEquals(working, original)
        }

        // Try some negative test cases

        // Index negative
        assertNull(byteArrayOf().uuWriteUInt8(-1, 0u).getOrNull())

        // Index greater than buffer.length
        assertNull(byteArrayOf().uuWriteUInt8(1, 0u).getOrNull())
    }

    @Test
    fun test_uuWriteUInt16()
    {
        val testInput = ArrayList<NonNullInputPair<UShort>>()
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "FFFF", 0xFFFFu))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "0000", 0x0000u))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "1122", 0x2211u))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "BBDD", 0xDDBBu))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "0080", 0x8000u))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "FF7F", 0x7FFFu))
        testInput.add(NonNullInputPair(ByteOrder.BIG_ENDIAN, "AABBCCDDEEFF", 0xCCDDu, 2))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "AABBCCDDEEFF", 0xEEDDu, 3))

        for (ti in testInput)
        {
            val original: ByteArray = ti.bytes().clone()
            val working: ByteArray = ti.bytes().clone()
            var actual = working.uuReadUInt16(ti.order, ti.index).getOrNull()
            assertNotNull(actual)
            var expected = ti.expected
            Assert.assertEquals(expected.toLong(), actual.toLong())
            val tmp: UShort = 57u
            var written: Int = working.uuWriteUInt16(ti.order, ti.index, tmp).getOrDefault(0)
            Assert.assertEquals(java.lang.Short.BYTES.toLong(), written.toLong())
            actual = working.uuReadUInt16(ti.order, ti.index).getOrNull()
            assertNotNull(actual)
            expected = tmp
            Assert.assertEquals(expected.toLong(), actual.toLong())
            written = working.uuWriteUInt16(ti.order, ti.index, ti.expected).getOrDefault(0)
            Assert.assertEquals(java.lang.Short.BYTES.toLong(), written.toLong())
            Assert.assertArrayEquals(working, original)
        }

        // Index negative
        assertNull(byteArrayOf().uuWriteUInt16(ByteOrder.nativeOrder(), -1, 0u).getOrNull())

        // Index greater than buffer.length
        assertNull(byteArrayOf().uuWriteUInt16(ByteOrder.nativeOrder(), 1, 0u).getOrNull())
    }

    @Test
    fun test_uuWriteUInt32()
    {
        val testInput = ArrayList<NonNullInputPair<UInt>>()
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "FFFFFFFF", 0xFFFFFFFFu))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "00000000", 0u))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "11223344", 0x44332211u))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "AABBCCDD", 0xDDCCBBAAu))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "00000080", 0x80000000u))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "FFFFFF7F", 0x7FFFFFFFu))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "1234AABBCCDD9876", 0xDDCCBBAAu, 2))
        testInput.add(NonNullInputPair(ByteOrder.BIG_ENDIAN, "1234AABBCCDD9876", 0xAABBCCDDu, 2))

        for (ti in testInput)
        {
            val original: ByteArray = ti.bytes().clone()
            val working: ByteArray = ti.bytes().clone()
            var actual = working.uuReadUInt32(ti.order, ti.index).getOrNull()
            assertNotNull(actual)

            var expected = ti.expected
            Assert.assertEquals(expected, actual)
            val tmp = 57u
            var written: Int = working.uuWriteUInt32(ti.order, ti.index, tmp).getOrDefault(0)
            Assert.assertEquals(Integer.BYTES.toLong(), written.toLong())
            actual = working.uuReadUInt32(ti.order, ti.index).getOrNull()
            assertNotNull(actual)

            expected = tmp
            Assert.assertEquals(expected, actual)
            written = working.uuWriteUInt32(ti.order, ti.index, ti.expected).getOrDefault(0)
            Assert.assertEquals(Integer.BYTES.toLong(), written.toLong())
            Assert.assertArrayEquals(working, original)
        }

        // Index negative
        assertNull(byteArrayOf().uuWriteUInt32(ByteOrder.nativeOrder(), -1, 0u).getOrNull())

        // Index greater than buffer.length
        assertNull(byteArrayOf().uuWriteUInt32(ByteOrder.nativeOrder(), 2, 0u).getOrNull())
    }

    @Test
    fun test_uuWriteUInt64()
    {
        val testInput = ArrayList<NonNullInputPair<ULong>>()
        testInput.add(NonNullInputPair(ByteOrder.BIG_ENDIAN, "FFFFFFFFFFFFFFFF", 0xFFFFFFFFFFFFFFFFu))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "FFFFFFFFFFFFFFFF", 0xFFFFFFFFFFFFFFFFu))
        testInput.add(NonNullInputPair(ByteOrder.BIG_ENDIAN, "0000000000000000", 0x0000000000000000u))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "0000000000000000", 0x0000000000000000u))
        testInput.add(NonNullInputPair(ByteOrder.BIG_ENDIAN, "1234567890ABCDEF", 0x1234567890ABCDEFu))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "1234567890ABCDEF", 0xEFCDAB9078563412u))
        testInput.add(NonNullInputPair(ByteOrder.BIG_ENDIAN, "1122334455667788", 0x1122334455667788u))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "1122334455667788", 0x8877665544332211u))
        testInput.add(NonNullInputPair(ByteOrder.BIG_ENDIAN, "8000000000000000", 0x8000000000000000u))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "8000000000000000", 0x0000000000000080u))
        testInput.add(NonNullInputPair(ByteOrder.BIG_ENDIAN, "FFFFFFFFFFFFFFFF", ULong.MAX_VALUE))
        testInput.add(NonNullInputPair(ByteOrder.LITTLE_ENDIAN, "FFFFFFFFFFFFFFFF", ULong.MAX_VALUE))
        testInput.add(
            NonNullInputPair(
                ByteOrder.LITTLE_ENDIAN,
                "ABCD1122334455667788FFDD",
                0x8877665544332211u,
                2
            )
        )

        for (ti in testInput)
        {
            println("Input: ${ti.bytes().uuToHex()}")

            val original: ByteArray = ti.bytes().clone()
            val working: ByteArray = ti.bytes().clone()
            var actual = working.uuReadUInt64(ti.order, ti.index).getOrNull()
            assertNotNull(actual)
            var expected = ti.expected
            Assert.assertEquals(expected, actual)
            val tmp: ULong = 57u
            var written: Int = working.uuWriteUInt64(ti.order, ti.index, tmp).getOrDefault(0)
            Assert.assertEquals(java.lang.Long.BYTES.toLong(), written.toLong())
            actual = working.uuReadUInt64(ti.order, ti.index).getOrNull()
            assertNotNull(actual)
            expected = tmp
            Assert.assertEquals(expected, actual)
            written = working.uuWriteUInt64(ti.order, ti.index, ti.expected).getOrDefault(0)
            Assert.assertEquals(java.lang.Long.BYTES.toLong(), written.toLong())
            Assert.assertArrayEquals(working, original)
        }

        // Index negative
        assertNull(byteArrayOf().uuWriteUInt64(ByteOrder.nativeOrder(), -1, 0u).getOrNull())

        // Index greater than buffer.length
        assertNull(byteArrayOf().uuWriteUInt64(ByteOrder.nativeOrder(), 2, 0u).getOrNull())
    }

    @Test
    fun test_uuHighNibble()
    {
        val testInput = ArrayList<NonNullInputPair<Byte>>()
        testInput.add(NonNullInputPair("00", 0.toByte()))
        testInput.add(NonNullInputPair("12", 1.toByte()))
        testInput.add(NonNullInputPair("01", 0.toByte()))
        testInput.add(NonNullInputPair("99", 9.toByte()))
        testInput.add(NonNullInputPair("CB", 0xC.toByte()))
        testInput.add(NonNullInputPair("57abcd1234", 5.toByte()))
        testInput.add(NonNullInputPair("FF", 0xF.toByte()))

        for (td in testInput)
        {
            val actual = td.bytes().uuHighNibble(td.index).getOrNull()
            assertNotNull(actual)
            val expected = td.expected
            Assert.assertEquals(expected.toLong(), actual.toLong())
        }
    }

    @Test
    fun test_uuLowNibble()
    {
        val testInput = ArrayList<NonNullInputPair<Byte>>()
        testInput.add(NonNullInputPair("00", 0.toByte()))
        testInput.add(NonNullInputPair("12", 2.toByte()))
        testInput.add(NonNullInputPair("01", 1.toByte()))
        testInput.add(NonNullInputPair("99", 9.toByte()))
        testInput.add(NonNullInputPair("CB", 0xB.toByte()))
        testInput.add(NonNullInputPair("57abcd1234", 7.toByte()))
        testInput.add(NonNullInputPair("FF", 0xF.toByte()))

        for (td in testInput)
        {
            val actual = td.bytes().uuLowNibble(td.index).getOrNull()
            assertNotNull(actual)
            val expected = td.expected
            Assert.assertEquals(expected.toLong(), actual.toLong())
        }
    }

    @Test
    fun test_uuBcd8()
    {
        val testInput = ArrayList<InputPair<Int>>()
        testInput.add(InputPair("00", 0))
        testInput.add(InputPair("12", 12))
        testInput.add(InputPair("01", 1))
        testInput.add(InputPair("99", 99))
        testInput.add(InputPair<Int>("CB", null))
        testInput.add(InputPair("57abcd1234", 57))
        testInput.add(InputPair("FF", null))

        for (td in testInput)
        {
            println("input: ${td.bytes().uuToHex()}")
            val actual = td.bytes().uuBcd8(td.index).getOrNull()
            val expected = td.expected
            if (expected != null)
            {
                assertNotNull(actual)
                Assert.assertEquals(expected.toLong(), actual.toLong())
            }
            else
            {
                assertNull(actual)
            }
        }
    }

    @Test
    fun test_uuBcd16()
    {
        val testInput = ArrayList<InputPair<Int>>()
        testInput.add(InputPair("0000", 0))
        testInput.add(InputPair("1234", 1234))
        testInput.add(InputPair("0101", 101))
        testInput.add(InputPair("9999", 9999))
        testInput.add(InputPair("CBDE", null))
        testInput.add(InputPair("5722abcd1234", 5722))
        testInput.add(InputPair("FFFF", null))

        for (td in testInput)
        {
            val actual = td.bytes().uuBcd16(td.index).getOrNull()
            val expected = td.expected
            if (expected != null)
            {
                assertNotNull(actual)
                Assert.assertEquals(expected.toLong(), actual.toLong())
            }
            else
            {
                assertNull(actual)
            }
        }
    }

    @Test
    fun test_uuBcd24()
    {
        val testInput = ArrayList<InputPair<Int>>()
        testInput.add(InputPair("000000", 0))
        testInput.add(InputPair("123456", 123456))
        testInput.add(InputPair("010101", 10101))
        testInput.add(InputPair("999999", 999999))
        testInput.add(InputPair("CBDEBF", null))
        testInput.add(InputPair("572295abcd1234", 572295))
        testInput.add(InputPair("FFFFFF", null))

        for (td in testInput)
        {
            val actual = td.bytes().uuBcd24(td.index).getOrNull()
            val expected = td.expected
            if (expected != null)
            {
                assertNotNull(actual)
                Assert.assertEquals(expected.toLong(), actual.toLong())
            }
            else
            {
                assertNull(actual)
            }
        }
    }

    @Test
    fun test_uuBcd32()
    {
        val testInput = ArrayList<InputPair<Int>>()
        testInput.add(InputPair("00000000", 0))
        testInput.add(InputPair("12345678", 12345678))
        testInput.add(InputPair("01010101", 1010101))
        testInput.add(InputPair("99999999", 99999999))
        testInput.add(InputPair("CBDEABCD", null))
        testInput.add(InputPair("57229576abcd1234", 57229576))
        testInput.add(InputPair("FFFFFFFF", null))

        for (td in testInput)
        {
            val actual = td.bytes().uuBcd32(td.index).getOrNull()
            val expected = td.expected
            if (expected != null)
            {
                assertNotNull(actual)
                Assert.assertEquals(expected.toLong(), actual.toLong())
            }
            else
            {
                assertNull(actual)
            }
        }
    }
}