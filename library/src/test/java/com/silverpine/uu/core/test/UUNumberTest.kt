package com.silverpine.uu.core.test

import com.silverpine.uu.core.uuFromBcd8
import com.silverpine.uu.core.uuToBcd8
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UUNumberTest
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
}