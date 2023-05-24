package com.silverpine.uu.core.test

import com.silverpine.uu.core.*
import org.junit.Assert
import org.junit.Test
import java.nio.ByteOrder

class UUByteArrayTest
{
    internal class InputPair<T>(
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
    fun test_uuToHex()
    {
        val input = ByteArray(4)
        val actual = input.uuToHex()
        val expected = "00000000"
        Assert.assertEquals(expected, actual)
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
            val actual = ti.bytes().uuReadInt8(ti.index)
            val expected = ti.expected
            Assert.assertEquals(expected.toLong(), actual.toLong())
        }

        // Try some negative test cases
        try
        {
            // Index negative
            try
            {
                byteArrayOf().uuReadInt8(-1)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }

            // Index greater than buffer.length
            try
            {
                byteArrayOf().uuReadInt8( 1)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }
        }
        catch (error: Exception)
        {
            Assert.fail("An exception escaped the negative test area: $error")
        }
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
            val actual: Short = ti.bytes().uuReadInt16(ti.order, ti.index)
            val expected = ti.expected
            Assert.assertEquals(expected.toLong(), actual.toLong())
        }

        try
        {
            // Index negative
            try
            {
                byteArrayOf().uuReadInt16(ByteOrder.nativeOrder(), -1)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }

            // Index greater than buffer.length
            try
            {
                byteArrayOf().uuReadInt16(ByteOrder.nativeOrder(), 1)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }
        }
        catch (error: Exception)
        {
            Assert.fail("An exception escaped the negative test area: $error")
        }
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
            val actual: Int = ti.bytes().uuReadInt32(ti.order, ti.index)
            val expected = ti.expected
            Assert.assertEquals(expected.toLong(), actual.toLong())
        }

        try
        {
            // Index negative
            try
            {
                byteArrayOf().uuReadInt32(ByteOrder.nativeOrder(), -1)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }

            // Index greater than buffer.length
            try
            {
                byteArrayOf().uuReadInt32(ByteOrder.nativeOrder(), 2)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }
        }
        catch (error: Exception)
        {
            Assert.fail("An exception escaped the negative test area: $error")
        }
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
            val actual: Long = ti.bytes().uuReadInt64(ti.order, ti.index)
            val expected = ti.expected
            Assert.assertEquals(expected, actual)
        }

        try
        {
            // Index negative
            try
            {
                byteArrayOf().uuReadInt64(ByteOrder.nativeOrder(), -1)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }

            // Index greater than buffer.length
            try
            {
                byteArrayOf().uuReadInt64(ByteOrder.nativeOrder(), 2)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }
        }
        catch (error: Exception)
        {
            Assert.fail("An exception escaped the negative test area: $error")
        }
    }

    @Test
    fun test_uuReadUInt8()
    {
        val testInput = ArrayList<InputPair<Int>>()
        testInput.add(InputPair("FF", 0xFF))
        testInput.add(InputPair("00", 0x00))
        testInput.add(InputPair("11", 0x11))
        testInput.add(InputPair("BB", 0xBB))
        testInput.add(InputPair("80", 0x80))
        testInput.add(InputPair("7F", 0x7F))
        testInput.add(InputPair("AABBCCDDEEFF", 0xCC, 2))
        testInput.add(InputPair("AABBCCDDEEFF", 0xEE, 4))
        testInput.add(InputPair("AABBCCDDEEFF", 0xFF, 5))

        // Test some positive cases
        for (ti in testInput)
        {
            val actual: Int = ti.bytes().uuReadUInt8(ti.index)
            val expected = ti.expected
            Assert.assertEquals(expected.toLong(), actual.toLong())
        }

        // Try some negative test cases
        try
        {
            // Index negative
            try
            {
                byteArrayOf().uuReadUInt8(-1)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }

            // Index greater than buffer.length
            try
            {
                byteArrayOf().uuReadUInt8(1)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }
        }
        catch (error: Exception)
        {
            Assert.fail("An exception escaped the negative test area: $error")
        }
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
            val actual: Int = ti.bytes().uuReadUInt16(ti.order, ti.index)
            val expected = ti.expected
            Assert.assertEquals(expected.toLong(), actual.toLong())
        }

        try
        {
            // Index negative
            try
            {
                byteArrayOf().uuReadUInt16(ByteOrder.nativeOrder(), -1)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }

            // Index greater than buffer.length
            try
            {
                byteArrayOf().uuReadUInt16(ByteOrder.nativeOrder(), 1)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }
        }
        catch (error: Exception)
        {
            Assert.fail("An exception escaped the negative test area: $error")
        }
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
            val actual: Long = ti.bytes().uuReadUInt32(ti.order, ti.index)
            val expected = ti.expected
            Assert.assertEquals(expected, actual)
        }

        try
        {
            // Index negative
            try
            {
                byteArrayOf().uuReadUInt32(ByteOrder.nativeOrder(), -1)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }

            // Index greater than buffer.length
            try
            {
                byteArrayOf().uuReadUInt32(ByteOrder.nativeOrder(), 2)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }
        }
        catch (error: Exception)
        {
            Assert.fail("An exception escaped the negative test area: $error")
        }
    }

    @Test
    fun test_uuReadUInt64()
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
            val actual: Long = ti.bytes().uuReadUInt64(ti.order, ti.index)
            val expected = ti.expected
            Assert.assertEquals(expected, actual)
        }

        try
        {
            // Index negative
            try
            {
                byteArrayOf().uuReadUInt64(ByteOrder.nativeOrder(), -1)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }

            // Index greater than buffer.length
            try
            {
                byteArrayOf().uuReadUInt64(ByteOrder.nativeOrder(), 2)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }
        }
        catch (error: Exception)
        {
            Assert.fail("An exception escaped the negative test area: $error")
        }
    }

    @Test
    fun test_uuWriteInt8()
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
            val original: ByteArray = ti.bytes().clone()
            val working: ByteArray = ti.bytes().clone()
            var actual: Byte = working.uuReadInt8(ti.index)
            var expected = ti.expected
            Assert.assertEquals(expected, actual)
            val tmp: Byte = 57
            var written: Int = working.uuWriteInt8(ti.index, tmp)
            Assert.assertEquals(java.lang.Byte.BYTES.toLong(), written.toLong())
            actual = working.uuReadInt8(ti.index)
            expected = tmp
            Assert.assertEquals(expected.toLong(), actual.toLong())
            written = working.uuWriteInt8(ti.index, ti.expected)
            Assert.assertEquals(java.lang.Byte.BYTES.toLong(), written.toLong())
            Assert.assertArrayEquals(working, original)
        }

        // Try some negative test cases
        try
        {
            // Index negative
            try
            {
                byteArrayOf().uuWriteInt8(-1, 0.toByte())
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }

            // Index greater than buffer.length
            try
            {
                byteArrayOf().uuWriteInt8(1, 0.toByte())
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }
        }
        catch (error: Exception)
        {
            Assert.fail("An exception escaped the negative test area: $error")
        }
    }

    @Test
    fun test_uuWriteInt16()
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
            val original: ByteArray = ti.bytes().clone()
            val working: ByteArray = ti.bytes().clone()
            var actual: Short = working.uuReadInt16(ti.order, ti.index)
            var expected = ti.expected
            Assert.assertEquals(expected.toLong(), actual.toLong())
            val tmp: Short = 57
            var written: Int = working.uuWriteInt16(ti.order, ti.index, tmp)
            Assert.assertEquals(java.lang.Short.BYTES.toLong(), written.toLong())
            actual = working.uuReadInt16(ti.order, ti.index)
            expected = tmp
            Assert.assertEquals(expected.toLong(), actual.toLong())
            written = working.uuWriteInt16(ti.order, ti.index, ti.expected)
            Assert.assertEquals(java.lang.Short.BYTES.toLong(), written.toLong())
            Assert.assertArrayEquals(working, original)
        }

        try
        {
            // Index negative
            try
            {
                byteArrayOf().uuWriteInt16(ByteOrder.nativeOrder(), -1, 0.toShort())
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }

            // Index greater than buffer.length
            try
            {
                byteArrayOf().uuWriteInt16(ByteOrder.nativeOrder(), 1, 0.toShort())
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }
        }
        catch (error: Exception)
        {
            Assert.fail("An exception escaped the negative test area: $error")
        }
    }

    @Test
    fun test_uuWriteInt32()
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
            val original: ByteArray = ti.bytes().clone()
            val working: ByteArray = ti.bytes().clone()
            var actual: Int = working.uuReadInt32(ti.order, ti.index)
            var expected = ti.expected
            Assert.assertEquals(expected.toLong(), actual.toLong())
            val tmp = 57
            var written: Int = working.uuWriteInt32(ti.order, ti.index, tmp)
            Assert.assertEquals(Integer.BYTES.toLong(), written.toLong())
            actual = working.uuReadInt32(ti.order, ti.index)
            expected = tmp
            Assert.assertEquals(expected.toLong(), actual.toLong())
            written = working.uuWriteInt32(ti.order, ti.index, ti.expected)
            Assert.assertEquals(Integer.BYTES.toLong(), written.toLong())
            Assert.assertArrayEquals(working, original)
        }

        try
        {
            // Index negative
            try
            {
                byteArrayOf().uuWriteInt32(ByteOrder.nativeOrder(), -1, 0)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }

            // Index greater than buffer.length
            try
            {
                byteArrayOf().uuWriteInt32(ByteOrder.nativeOrder(), 2, 0)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }
        }
        catch (error: Exception)
        {
            Assert.fail("An exception escaped the negative test area: $error")
        }
    }

    @Test
    fun test_uuWriteInt64()
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
            val original: ByteArray = ti.bytes().clone()
            val working: ByteArray = ti.bytes().clone()
            var actual: Long = working.uuReadInt64(ti.order, ti.index)
            var expected = ti.expected
            Assert.assertEquals(expected, actual)
            val tmp: Long = 57
            var written: Int = working.uuWriteInt64(ti.order, ti.index, tmp)
            Assert.assertEquals(java.lang.Long.BYTES.toLong(), written.toLong())
            actual = working.uuReadInt64(ti.order, ti.index)
            expected = tmp
            Assert.assertEquals(expected, actual)
            written = working.uuWriteInt64(ti.order, ti.index, ti.expected)
            Assert.assertEquals(java.lang.Long.BYTES.toLong(), written.toLong())
            Assert.assertArrayEquals(working, original)
        }

        try
        {
            // Index negative
            try
            {
                byteArrayOf().uuWriteInt64(ByteOrder.nativeOrder(), -1, 0L)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }

            // Index greater than buffer.length
            try
            {
                byteArrayOf().uuWriteInt64(ByteOrder.nativeOrder(), 2, 0L)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }
        }
        catch (error: Exception)
        {
            Assert.fail("An exception escaped the negative test area: $error")
        }
    }

    @Test
    fun test_uuWriteUInt8()
    {
        val testInput = ArrayList<InputPair<Int>>()
        testInput.add(InputPair("FF", 0xFF))
        testInput.add(InputPair("00", 0x00))
        testInput.add(InputPair("11", 0x11))
        testInput.add(InputPair("BB", 0xBB))
        testInput.add(InputPair("80", 0x80))
        testInput.add(InputPair("7F", 0x7F))
        testInput.add(InputPair("AABBCCDDEEFF", 0xCC, 2))
        testInput.add(InputPair("AABBCCDDEEFF", 0xEE, 4))
        testInput.add(InputPair("AABBCCDDEEFF", 0xFF, 5))

        // Test some positive cases
        for (ti in testInput)
        {
            val original: ByteArray = ti.bytes().clone()
            val working: ByteArray = ti.bytes().clone()
            var actual: Int = working.uuReadUInt8(ti.index)
            var expected = ti.expected
            Assert.assertEquals(expected.toLong(), actual.toLong())
            val tmp = 57
            var written: Int = working.uuWriteUInt8(ti.index, tmp)
            Assert.assertEquals(java.lang.Byte.BYTES.toLong(), written.toLong())
            actual = working.uuReadUInt8(ti.index)
            expected = tmp
            Assert.assertEquals(expected.toLong(), actual.toLong())
            written = working.uuWriteUInt8(ti.index, ti.expected)
            Assert.assertEquals(java.lang.Byte.BYTES.toLong(), written.toLong())
            Assert.assertArrayEquals(working, original)
        }

        // Try some negative test cases
        try
        {
            // Index negative
            try
            {
                byteArrayOf().uuWriteUInt8(-1, 0)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }

            // Index greater than buffer.length
            try
            {
                byteArrayOf().uuWriteUInt8(1, 0)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }
        }
        catch (error: Exception)
        {
            Assert.fail("An exception escaped the negative test area: $error")
        }
    }

    @Test
    fun test_uuWriteUInt16()
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
            val original: ByteArray = ti.bytes().clone()
            val working: ByteArray = ti.bytes().clone()
            var actual: Int = working.uuReadUInt16(ti.order, ti.index)
            var expected = ti.expected
            Assert.assertEquals(expected.toLong(), actual.toLong())
            val tmp = 57
            var written: Int = working.uuWriteUInt16(ti.order, ti.index, tmp)
            Assert.assertEquals(java.lang.Short.BYTES.toLong(), written.toLong())
            actual = working.uuReadUInt16(ti.order, ti.index)
            expected = tmp
            Assert.assertEquals(expected.toLong(), actual.toLong())
            written = working.uuWriteUInt16(ti.order, ti.index, ti.expected)
            Assert.assertEquals(java.lang.Short.BYTES.toLong(), written.toLong())
            Assert.assertArrayEquals(working, original)
        }

        try
        {
            // Index negative
            try
            {
                byteArrayOf().uuWriteUInt16(ByteOrder.nativeOrder(), -1, 0)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }

            // Index greater than buffer.length
            try
            {
                byteArrayOf().uuWriteUInt16(ByteOrder.nativeOrder(), 1, 0)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }
        }
        catch (error: Exception)
        {
            Assert.fail("An exception escaped the negative test area: $error")
        }
    }

    @Test
    fun test_uuWriteUInt32()
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
            val original: ByteArray = ti.bytes().clone()
            val working: ByteArray = ti.bytes().clone()
            var actual: Long = working.uuReadUInt32(ti.order, ti.index)
            var expected = ti.expected
            Assert.assertEquals(expected, actual)
            val tmp: Long = 57
            var written: Int = working.uuWriteUInt32(ti.order, ti.index, tmp)
            Assert.assertEquals(Integer.BYTES.toLong(), written.toLong())
            actual = working.uuReadUInt32(ti.order, ti.index)
            expected = tmp
            Assert.assertEquals(expected, actual)
            written = working.uuWriteUInt32(ti.order, ti.index, ti.expected)
            Assert.assertEquals(Integer.BYTES.toLong(), written.toLong())
            Assert.assertArrayEquals(working, original)
        }

        try
        {
            // Index negative
            try
            {
                byteArrayOf().uuWriteUInt32(ByteOrder.nativeOrder(), -1, 0)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }

            // Index greater than buffer.length
            try
            {
                byteArrayOf().uuWriteUInt32(ByteOrder.nativeOrder(), 2, 0)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }
        }
        catch (error: Exception)
        {
            Assert.fail("An exception escaped the negative test area: $error")
        }
    }

    @Test
    fun test_uuWriteUInt64()
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
            val original: ByteArray = ti.bytes().clone()
            val working: ByteArray = ti.bytes().clone()
            var actual: Long = working.uuReadUInt64(ti.order, ti.index)
            var expected = ti.expected
            Assert.assertEquals(expected, actual)
            val tmp: Long = 57
            var written: Int = working.uuWriteUInt64(ti.order, ti.index, tmp)
            Assert.assertEquals(java.lang.Long.BYTES.toLong(), written.toLong())
            actual = working.uuReadUInt64(ti.order, ti.index)
            expected = tmp
            Assert.assertEquals(expected, actual)
            written = working.uuWriteUInt64(ti.order, ti.index, ti.expected)
            Assert.assertEquals(java.lang.Long.BYTES.toLong(), written.toLong())
            Assert.assertArrayEquals(working, original)
        }

        try
        {
            // Index negative
            try
            {
                byteArrayOf().uuWriteUInt64(ByteOrder.nativeOrder(), -1, 0)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }

            // Index greater than buffer.length
            try
            {
                byteArrayOf().uuWriteUInt64(ByteOrder.nativeOrder(), 2, 0)
                Assert.fail("Should raise a ArrayIndexOutOfBoundsException")
            }
            catch (ignored: IndexOutOfBoundsException)
            {
            }
        }
        catch (error: Exception)
        {
            Assert.fail("An exception escaped the negative test area: $error")
        }
    }

    @Test
    fun test_uuHighNibble()
    {
        val testInput = ArrayList<InputPair<Byte>>()
        testInput.add(InputPair("00", 0.toByte()))
        testInput.add(InputPair("12", 1.toByte()))
        testInput.add(InputPair("01", 0.toByte()))
        testInput.add(InputPair("99", 9.toByte()))
        testInput.add(InputPair("CB", 0xC.toByte()))
        testInput.add(InputPair("57abcd1234", 5.toByte()))
        testInput.add(InputPair("FF", 0xF.toByte()))

        for (td in testInput)
        {
            val actual: Byte = td.bytes().uuHighNibble(td.index)
            val expected = td.expected
            Assert.assertEquals(expected.toLong(), actual.toLong())
        }
    }

    @Test
    fun test_uuLowNibble()
    {
        val testInput = ArrayList<InputPair<Byte>>()
        testInput.add(InputPair("00", 0.toByte()))
        testInput.add(InputPair("12", 2.toByte()))
        testInput.add(InputPair("01", 1.toByte()))
        testInput.add(InputPair("99", 9.toByte()))
        testInput.add(InputPair("CB", 0xB.toByte()))
        testInput.add(InputPair("57abcd1234", 7.toByte()))
        testInput.add(InputPair("FF", 0xF.toByte()))

        for (td in testInput)
        {
            val actual: Byte = td.bytes().uuLowNibble(td.index)
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
        testInput.add(InputPair("CB", -1))
        testInput.add(InputPair("57abcd1234", 57))
        testInput.add(InputPair("FF", -1))

        for (td in testInput)
        {
            val actual: Int = td.bytes().uuBcd8(td.index)
            val expected = td.expected
            Assert.assertEquals(expected.toLong(), actual.toLong())
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
        testInput.add(InputPair("CBDE", -1))
        testInput.add(InputPair("5722abcd1234", 5722))
        testInput.add(InputPair("FFFF", -1))

        for (td in testInput)
        {
            val actual: Int = td.bytes().uuBcd16(td.index)
            val expected = td.expected
            Assert.assertEquals(expected.toLong(), actual.toLong())
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
        testInput.add(InputPair("CBDEBF", -1))
        testInput.add(InputPair("572295abcd1234", 572295))
        testInput.add(InputPair("FFFFFF", -1))

        for (td in testInput)
        {
            val actual: Int = td.bytes().uuBcd24(td.index)
            val expected = td.expected
            Assert.assertEquals(expected.toLong(), actual.toLong())
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
        testInput.add(InputPair("CBDEABCD", -1))
        testInput.add(InputPair("57229576abcd1234", 57229576))
        testInput.add(InputPair("FFFFFFFF", -1))

        for (td in testInput)
        {
            val actual: Int = td.bytes().uuBcd32(td.index)
            val expected = td.expected
            Assert.assertEquals(expected.toLong(), actual.toLong())
        }
    }
}