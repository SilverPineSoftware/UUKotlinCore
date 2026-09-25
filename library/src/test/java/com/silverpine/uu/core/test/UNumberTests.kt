package com.silverpine.uu.core.test

import com.silverpine.uu.core.uuClearBit
import com.silverpine.uu.core.uuFromBcd8
import com.silverpine.uu.core.uuIsBitSet
import com.silverpine.uu.core.uuIsLeapYear
import com.silverpine.uu.core.uuSetBit
import com.silverpine.uu.core.uuToBcd8
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class UNumberTests
{
    @Test
    fun test_uuToBcd8()
    {
        // Each test datum: input, expected
        val inputs = arrayOf(
            Pair(12, 0x12),
            Pair(99, 0x99),
            Pair(0, 0x00),
            Pair(100, null),
            Pair(-1, null),
            Pair(78, 0x78)
        )

        for (td in inputs) {
            val input = td.first
            val expected = td.second

            val actual = input.uuToBcd8()

            if (expected == null)
            {
                assertNull(actual, "Expected null for input $input")
            }
            else
            {
                assertEquals(expected, actual, "BCD conversion failed for input $input")
            }
        }
    }

    @Test
    fun test_uuFromBcd8()
    {
        // Each test datum: original number, its BCD
        val inputs = arrayOf(
            Pair(12, 0x12),
            Pair(99, 0x99),
            Pair(0, 0x00),
            Pair(78, 0x78)
        )

        for (td in inputs)
        {
            val expected = td.first
            val bcd = td.second

            val actual = bcd.uuFromBcd8()
            assertEquals(
                expected, actual,
                "Failed to decode BCD " + Integer.toHexString(bcd)
            )
        }
    }

    @Test
    fun yearsDivisibleBy400_areLeapYears()
    {
        assertTrue(1600.uuIsLeapYear)
        assertTrue(2000.uuIsLeapYear)
        assertTrue(2400.uuIsLeapYear)
    }

    @Test
    fun yearsDivisibleBy100_butNot400_areNotLeapYears()
    {
        assertFalse(1700.uuIsLeapYear)
        assertFalse(1800.uuIsLeapYear)
        assertFalse(1900.uuIsLeapYear)
    }

    @Test
    fun yearsDivisibleBy4_butNot100_areLeapYears()
    {
        assertTrue(1996.uuIsLeapYear)
        assertTrue(2004.uuIsLeapYear)
        assertTrue(2024.uuIsLeapYear)
    }

    @Test
    fun yearsNotDivisibleBy4_areNotLeapYears()
    {
        assertFalse(1999.uuIsLeapYear)
        assertFalse(2001.uuIsLeapYear)
        assertFalse(2023.uuIsLeapYear)
    }

    @Test
    fun zeroAndNegativeYears_followSameRules()
    {
        // Mathematically, 0 is divisible by 400 so it's considered a leap year.
        assertTrue(0.uuIsLeapYear)
        assertTrue((-4).uuIsLeapYear)
        assertFalse((-1).uuIsLeapYear)
    }

    @Nested
    inner class IsBitSetTests
    {
        // Each test supplies a call to its concrete overload. ULong holds the test
        // bit patterns without losing the highest bit of any integer size.
        private fun assertBitMasks(size: Int, isBitSet: (ULong, ULong) -> Boolean)
        {
            val allBits = ULong.MAX_VALUE shr (ULong.SIZE_BITS - size)
            assertTrue(isBitSet(0uL, 0uL), "Zero contains the empty mask")
            assertTrue(isBitSet(allBits, 0uL), "All bits contain the empty mask")
            assertTrue(isBitSet(allBits, allBits), "All bits match themselves")
            assertFalse(isBitSet(0uL, allBits), "Zero contains no set bits")
            assertFalse(isBitSet(1uL, allBits), "One bit does not satisfy an all-bits mask")

            for (index in 0 until size)
            {
                val bit = 1uL shl index
                assertTrue(isBitSet(allBits, bit), "Bit $index of $size is set")
                assertFalse(isBitSet(0uL, bit), "Bit $index of $size is absent")
                assertFalse(isBitSet(allBits xor bit, bit), "Bit $index of $size is cleared")

                for (maskIndex in 0 until size)
                {
                    assertEquals(
                        index == maskIndex,
                        isBitSet(bit, 1uL shl maskIndex),
                        "Value bit $index and mask bit $maskIndex of $size"
                    )
                }
            }

            assertTrue(isBitSet(0b1011uL, 0b0011uL), "All requested bits are present")
            assertFalse(isBitSet(0b1011uL, 0b0110uL), "Partial overlap is insufficient")
            assertFalse(isBitSet(0b1011uL, 0b0100uL), "Disjoint mask is absent")

            val highBit = 1uL shl (size - 1)
            val highAndLow = highBit or 1uL
            assertTrue(isBitSet(highAndLow, highAndLow), "Highest and lowest bits match")
            assertTrue(isBitSet(allBits, highAndLow), "Extra bits are allowed")
            assertFalse(isBitSet(highBit, highAndLow), "Lowest requested bit is missing")
            assertFalse(isBitSet(1uL, highAndLow), "Highest requested bit is missing")
        }

        @Test
        fun `Byte - checks every bit and composite masks`()
        {
            assertBitMasks(Byte.SIZE_BITS) { value, mask ->
                value.toByte().uuIsBitSet(mask.toByte())
            }
        }

        @Test
        fun `Short - checks every bit and composite masks`()
        {
            assertBitMasks(Short.SIZE_BITS) { value, mask ->
                value.toShort().uuIsBitSet(mask.toShort())
            }
        }

        @Test
        fun `Int - checks every bit and composite masks`()
        {
            assertBitMasks(Int.SIZE_BITS) { value, mask ->
                value.toInt().uuIsBitSet(mask.toInt())
            }
        }

        @Test
        fun `Long - checks every bit and composite masks`()
        {
            assertBitMasks(Long.SIZE_BITS) { value, mask ->
                value.toLong().uuIsBitSet(mask.toLong())
            }
        }

        @Test
        fun `UByte - checks every bit and composite masks`()
        {
            assertBitMasks(UByte.SIZE_BITS) { value, mask ->
                value.toUByte().uuIsBitSet(mask.toUByte())
            }
        }

        @Test
        fun `UShort - checks every bit and composite masks`()
        {
            assertBitMasks(UShort.SIZE_BITS) { value, mask ->
                value.toUShort().uuIsBitSet(mask.toUShort())
            }
        }

        @Test
        fun `UInt - checks every bit and composite masks`()
        {
            assertBitMasks(UInt.SIZE_BITS) { value, mask ->
                value.toUInt().uuIsBitSet(mask.toUInt())
            }
        }

        @Test
        fun `ULong - checks every bit and composite masks`()
        {
            assertBitMasks(ULong.SIZE_BITS) { value, mask ->
                value.uuIsBitSet(mask)
            }
        }
    }

    @Nested
    inner class SetBitTests
    {
        @Test
        fun `Int - set bit at position 0`()
        {
            val value = 0
            val result = value.uuSetBit(true, 0)
            assertEquals(1, result) // 0001
        }

        @Test
        fun `Int - clear bit at position 0`()
        {
            val value = 1 // 0001
            val result = value.uuSetBit(false, 0)
            assertEquals(0, result)
        }

        @Test
        fun `Int - set bit at higher position`()
        {
            val value = 0
            val result = value.uuSetBit(true, 5)
            assertEquals(32, result) // 1 << 5
        }

        @Test
        fun `Int - clear bit at higher position`()
        {
            val value = 0b111111 // 63
            val result = value.uuSetBit(false, 5)
            assertEquals(31, result) // clears bit 5
        }

        @Test
        fun `Int - out of range index returns unchanged`()
        {
            val value = 42
            assertEquals(value, value.uuSetBit(true, -1))
            assertEquals(value, value.uuSetBit(true, Int.SIZE_BITS)) // too high
        }

        @Test
        fun `Int - set and clear round trip`()
        {
            val value = 0
            val set = value.uuSetBit(true, 3)
            val cleared = set.uuSetBit(false, 3)
            assertEquals(value, cleared)
        }

        @Test
        fun `Int - set highest valid bit`()
        {
            val index = Int.SIZE_BITS - 1
            val value = 0
            val result = value.uuSetBit(true, index)
            assertEquals(Int.MIN_VALUE, result) // highest bit in Int is the sign bit
        }

        @Test
        fun `UInt - set bit at position 0`()
        {
            val value = 0u
            val result = value.uuSetBit(true, 0)
            assertEquals(1u, result)
        }

        @Test
        fun `UInt - clear bit at position 0`()
        {
            val value = 1u
            val result = value.uuSetBit(false, 0)
            assertEquals(0u, result)
        }

        @Test
        fun `UInt - set bit at higher position`()
        {
            val value = 0u
            val result = value.uuSetBit(true, 5)
            assertEquals(32u, result) // 1u shl 5
        }

        @Test
        fun `UInt - clear bit at higher position`()
        {
            val value = 63u // 0b111111
            val result = value.uuSetBit(false, 5)
            assertEquals(31u, result) // clears bit 5
        }

        @Test
        fun `UInt - out of range index returns unchanged`()
        {
            val value = 42u
            assertEquals(value, value.uuSetBit(true, -1))
            assertEquals(value, value.uuSetBit(true, UInt.SIZE_BITS)) // too high
        }

        @Test
        fun `UInt - set and clear round trip`()
        {
            val value = 0u
            val set = value.uuSetBit(true, 7)
            val cleared = set.uuSetBit(false, 7)
            assertEquals(value, cleared)
        }

        @Test
        fun `UInt - set highest valid bit`()
        {
            val index = UInt.SIZE_BITS - 1 // 31
            val value = 0u
            val result = value.uuSetBit(true, index)
            assertEquals(0x80000000u, result) // highest bit in UInt
        }

        @Test
        fun `Byte set and clear`()
        {
            val value: Byte = 0
            assertEquals(1, value.uuSetBit(true, 0))
            assertEquals(0, 1.toByte().uuSetBit(false, 0))
        }

        @Test
        fun `Byte highest bit`()
        {
            val result = 0.toByte().uuSetBit(true, Byte.SIZE_BITS - 1)
            assertEquals(Byte.MIN_VALUE, result)
        }

        @Test
        fun `UByte set and clear`()
        {
            val value: UByte = 0u
            assertEquals(1u.toUByte(), value.uuSetBit(true, 0))
            assertEquals(0u.toUByte(), 1u.toUByte().uuSetBit(false, 0))
        }

        @Test
        fun `UByte highest bit`()
        {
            val result = 0u.toUByte().uuSetBit(true, UByte.SIZE_BITS - 1)
            assertEquals(0x80u.toUByte(), result)
        }

        @Test
        fun `Short set and clear`()
        {
            val value: Short = 0
            assertEquals(1, value.uuSetBit(true, 0))
            assertEquals(0, 1.toShort().uuSetBit(false, 0))
        }

        @Test
        fun `Short highest bit`()
        {
            val result = 0.toShort().uuSetBit(true, Short.SIZE_BITS - 1)
            assertEquals(Short.MIN_VALUE, result)
        }

        @Test
        fun `UShort set and clear`()
        {
            val value: UShort = 0u
            assertEquals(1u.toUShort(), value.uuSetBit(true, 0))
            assertEquals(0u.toUShort(), 1u.toUShort().uuSetBit(false, 0))
        }

        @Test
        fun `UShort highest bit`()
        {
            val result = 0u.toUShort().uuSetBit(true, UShort.SIZE_BITS - 1)
            assertEquals(0x8000u.toUShort(), result)
        }

        @Test
        fun `Long set and clear`()
        {
            val value: Long = 0
            assertEquals(1, value.uuSetBit(true, 0))
            assertEquals(0, 1L.uuSetBit(false, 0))
        }

        @Test
        fun `Long highest bit`()
        {
            val result = 0L.uuSetBit(true, Long.SIZE_BITS - 1)
            assertEquals(Long.MIN_VALUE, result)
        }

        @Test
        fun `ULong set and clear`()
        {
            val value: ULong = 0u
            assertEquals(1uL, value.uuSetBit(true, 0))
            assertEquals(0uL, 1uL.uuSetBit(false, 0))
        }

        @Test
        fun `ULong highest bit`()
        {
            val result = 0uL.uuSetBit(true, ULong.SIZE_BITS - 1)
            assertEquals(0x8000000000000000uL, result)
        }

        @Test
        fun `out of range indices return unchanged`()
        {
            assertEquals(5.toByte(), 5.toByte().uuSetBit(true, -1))
            assertEquals(5.toByte(), 5.toByte().uuSetBit(true, Byte.SIZE_BITS))

            assertEquals(5u.toUByte(), 5u.toUByte().uuSetBit(true, -1))
            assertEquals(5u.toUByte(), 5u.toUByte().uuSetBit(true, UByte.SIZE_BITS))

            assertEquals(5.toShort(), 5.toShort().uuSetBit(true, -1))
            assertEquals(5.toShort(), 5.toShort().uuSetBit(true, Short.SIZE_BITS))

            assertEquals(5u.toUShort(), 5u.toUShort().uuSetBit(true, -1))
            assertEquals(5u.toUShort(), 5u.toUShort().uuSetBit(true, UShort.SIZE_BITS))

            assertEquals(5L, 5L.uuSetBit(true, -1))
            assertEquals(5L, 5L.uuSetBit(true, Long.SIZE_BITS))

            assertEquals(5uL, 5uL.uuSetBit(true, -1))
            assertEquals(5uL, 5uL.uuSetBit(true, ULong.SIZE_BITS))
        }

        @ParameterizedTest(name = "Byte {0}.uuSetBit(to={2}, index={1}) = {3}")
        @CsvSource(
            "0, 0, true, 1",
            "1, 0, false, 0",
            "0, 6, true, 64",
            "127, 6, false, 63",
            "0, 7, true, -128",   // highest bit (sign bit)
            "-128, 7, false, 0"
        )
        fun testByte(value: Byte, index: Int, to: Boolean, expected: Byte)
        {
            assertEquals(expected, value.uuSetBit(to, index))
        }

        @ParameterizedTest(name = "UByte {0}.uuSetBit(to={2}, index={1}) = {3}")
        @CsvSource(
            "0, 0, true, 1",
            "1, 0, false, 0",
            "0, 6, true, 64",
            "255, 6, false, 191",
            "0, 7, true, 128",
            "128, 7, false, 0"
        )
        fun testUByte(valueStr: String, index: Int, to: Boolean, expectedStr: String)
        {
            val value = valueStr.toUByte()
            val expected = expectedStr.toUByte()
            assertEquals(expected, value.uuSetBit(to, index))
        }

        @ParameterizedTest(name = "Short {0}.uuSetBit(to={2}, index={1}) = {3}")
        @CsvSource(
            "0, 0, true, 1",
            "1, 0, false, 0",
            "0, 10, true, 1024",
            "1023, 10, false, 1023",
            "0, 15, true, -32768",
            "-32768, 15, false, 0"
        )
        fun testShort(valueStr: String, index: Int, to: Boolean, expectedStr: String)
        {
            val value = valueStr.toShort()
            val expected = expectedStr.toShort()
            assertEquals(expected, value.uuSetBit(to, index))
        }

        @ParameterizedTest(name = "UShort {0}.uuSetBit(to={2}, index={1}) = {3}")
        @CsvSource(
            "0, 0, true, 1",
            "1, 0, false, 0",
            "0, 10, true, 1024",
            "1023, 10, false, 1023",
            "0, 15, true, 32768",
            "32768, 15, false, 0"
        )
        fun testUShort(valueStr: String, index: Int, to: Boolean, expectedStr: String)
        {
            val value = valueStr.toUShort()
            val expected = expectedStr.toUShort()
            assertEquals(expected, value.uuSetBit(to, index))
        }

        @ParameterizedTest(name = "uuSetBit({0}, to={2}, index={1}) = {3}")
        @CsvSource(
            // value, index, to, expected
            "0, 0, true, 1",           // set LSB
            "1, 0, false, 0",          // clear LSB
            "0, 5, true, 32",          // set bit 5
            "63, 5, false, 31",        // clear bit 5
            "0, 7, true, 128",         // set bit 7
            "128, 7, false, 0",        // clear bit 7
            "0, 30, true, 1073741824", // set bit 30
            "1073741824, 30, false, 0",// clear bit 30
            "0, 31, true, -2147483648",// set highest bit (sign bit)
            "-2147483648, 31, false, 0"// clear highest bit
        )
        fun `Int - test uuSetBit with multiple scenarios`(
            value: Int,
            index: Int,
            to: Boolean,
            expected: Int
        )
        {
            val result = value.uuSetBit(to, index)
            assertEquals(expected, result)
        }

        @ParameterizedTest(name = "uuSetBit({0}, to={2}, index={1}) = {3}")
        @CsvSource(
            // value, index, to, expected
            "0, 0, true, 1",
            "1, 0, false, 0",
            "0, 5, true, 32",
            "63, 5, false, 31",
            "0, 7, true, 128",
            "128, 7, false, 0",
            "0, 31, true, 2147483648",
            "2147483648, 31, false, 0"
        )
        fun `UInt - test uuSetBit with multiple scenarios`(
            valueStr: String,
            index: Int,
            to: Boolean,
            expectedStr: String
        )
        {
            val value = valueStr.toUInt()
            val expected = expectedStr.toUInt()
            val result = value.uuSetBit(to, index)
            assertEquals(expected, result)
        }

        @ParameterizedTest(name = "Long {0}.uuSetBit(to={2}, index={1}) = {3}")
        @CsvSource(
            "0, 0, true, 1",
            "1, 0, false, 0",
            "0, 40, true, 1099511627776",
            "1099511627776, 40, false, 0",
            "0, 63, true, -9223372036854775808", // highest bit (sign bit)
            "-9223372036854775808, 63, false, 0"
        )
        fun testLong(value: Long, index: Int, to: Boolean, expected: Long)
        {
            assertEquals(expected, value.uuSetBit(to, index))
        }

        @ParameterizedTest(name = "ULong {0}.uuSetBit(to={2}, index={1}) = {3}")
        @CsvSource(
            "0, 0, true, 1",
            "1, 0, false, 0",
            "0, 40, true, 1099511627776",
            "1099511627776, 40, false, 0",
            "0, 63, true, 9223372036854775808",
            "9223372036854775808, 63, false, 0"
        )
        fun testULong(valueStr: String, index: Int, to: Boolean, expectedStr: String)
        {
            val value = valueStr.toULong()
            val expected = expectedStr.toULong()
            val result = value.uuSetBit(to, index)
            assertEquals(expected, result)
        }
    }

    @Nested
    inner class ClearBitTests
    {
        @Test
        fun byte_clearBit_validIndex()
        {
            val original: Byte = 0b00001111
            val result = original.uuClearBit(3)
            assertEquals(0b00000111.toByte(), result)
        }

        @Test
        fun byte_clearBit_outOfBounds()
        {
            val original: Byte = 0b00001111
            assertEquals(original, original.uuClearBit(-1))
            assertEquals(original, original.uuClearBit(8))
        }

        @Test
        fun uByte_clearBit_validIndex()
        {
            val original: UByte = 0b11111111u
            val result = original.uuClearBit(7)
            assertEquals(0b01111111u.toUByte(), result)
        }

        @Test
        fun uShort_clearBit_validIndex()
        {
            val original: UShort = 0b1000000000000000u
            val result = original.uuClearBit(15)
            assertEquals(0u.toUShort(), result)
        }

        @Test
        fun int_clearBit_validIndex()
        {
            val original = 0b1010_1010
            val result = original.uuClearBit(1)
            assertEquals(0b1010_1000, result)
        }

        @Test
        fun uint_clearBit_validIndex()
        {
            val original = 0b1111u
            val result = original.uuClearBit(2)
            assertEquals(0b1011u, result)
        }

        @Test
        fun long_clearBit_validIndex()
        {
            val original = 1L shl 63
            val result = original.uuClearBit(63)
            assertEquals(0L, result)
        }

        @Test
        fun uLong_clearBit_validIndex()
        {
            val original = 1uL shl 63
            val result = original.uuClearBit(63)
            assertEquals(0uL, result)
        }

        @Test
        fun short_clearBit_outOfBounds()
        {
            val original: Short = 0b00001111
            assertEquals(original, original.uuClearBit(-1))
            assertEquals(original, original.uuClearBit(16))
        }

        @Test
        fun long_clearBit_outOfBounds()
        {
            val original = 123456789L
            assertEquals(original, original.uuClearBit(-1))
            assertEquals(original, original.uuClearBit(64))
        }
    }
}