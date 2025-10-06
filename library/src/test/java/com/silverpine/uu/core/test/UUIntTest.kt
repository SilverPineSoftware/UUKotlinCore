package com.silverpine.uu.core.test

import com.silverpine.uu.core.uuFromBcd8
import com.silverpine.uu.core.uuIsLeapYear
import com.silverpine.uu.core.uuToBcd8
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UUIntTest
{
    @Test
    fun test_uuToBcd8()
    {
        // Each test datum: input, expected
        val inputs = arrayOf<Pair<Int,Int?>>(
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
        val inputs = arrayOf<Pair<Int,Int>>(
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
                expected, actual.toInt(),
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
}