package com.silverpine.uu.core.test

import com.silverpine.uu.core.UURandom
import com.silverpine.uu.core.uuToHex
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID

class UURandomTest
{
    private val loops = 57

    @Test
    fun test_bytes()
    {
        repeat(loops)
        {
            val length = randomCount()
            val actual = UURandom.bytes(length)
            assertNotNull(actual)
            assertEquals(length, actual.size)
            println("UURandom.bytes: ${actual.uuToHex()}")
        }
    }

    @Test
    fun test_bytes_negativeIndex()
    {
        val actual = UURandom.bytes(-1)
        assertNotNull(actual)
        assertEquals(0, actual.size)
    }

    @Test
    fun test_bytes_zeroIndex()
    {
        val actual = UURandom.bytes(0)
        assertNotNull(actual)
        assertEquals(0, actual.size)
    }

    @Test
    fun test_byte()
    {
        repeat(loops)
        {
            val actual = UURandom.byte()
            assertNotNull(actual)
            val rangeCheck = actual.toInt()
            assertTrue(rangeCheck >= Byte.MIN_VALUE.toInt())
            assertTrue(rangeCheck <= Byte.MAX_VALUE.toInt())
            println("UURandom.byte: $actual")
        }
    }

    @Test
    fun test_uByte()
    {
        repeat(loops)
        {
            val actual = UURandom.uByte()
            assertNotNull(actual)
            val rangeCheck = actual.toInt()
            assertTrue(rangeCheck >= UByte.MIN_VALUE.toInt())
            assertTrue(rangeCheck <= UByte.MAX_VALUE.toInt())
            println("UURandom.uByte: $actual")
        }
    }

    @Test
    fun test_short()
    {
        repeat(loops)
        {
            val actual = UURandom.short()
            assertNotNull(actual)
            val rangeCheck = actual.toInt()
            assertTrue(rangeCheck >= Short.MIN_VALUE.toInt())
            assertTrue(rangeCheck <= Short.MAX_VALUE.toInt())
            println("UURandom.short: $actual")
        }
    }

    @Test
    fun test_uShort()
    {
        repeat(loops)
        {
            val actual = UURandom.uShort()
            assertNotNull(actual)
            val rangeCheck = actual.toInt()
            assertTrue(rangeCheck >= UShort.MIN_VALUE.toInt())
            assertTrue(rangeCheck <= UShort.MAX_VALUE.toInt())
            println("UURandom.uShort: $actual")
        }
    }

    @Test
    fun test_int()
    {
        repeat(loops)
        {
            val actual = UURandom.int()
            assertNotNull(actual)
            val rangeCheck = actual.toLong()
            assertTrue(rangeCheck >= Integer.MIN_VALUE.toLong())
            assertTrue(rangeCheck <= Integer.MAX_VALUE.toLong())
            println("UURandom.int: $actual")
        }
    }

    @Test
    fun test_int_min_max_positive()
    {
        val min = 0
        val max = 1000
        repeat(loops)
        {
            val actual = UURandom.int(min, max)
            assertNotNull(actual)
            val rangeCheck = actual.toLong()
            assertTrue(rangeCheck >= min.toLong())
            assertTrue(rangeCheck <= max.toLong())
            println("UURandom.int: $actual")
        }
    }

    @Test
    fun test_int_min_max_negative()
    {
        val min = -1000
        val max = 1000
        repeat(loops)
        {
            val actual = UURandom.int(min, max)
            assertNotNull(actual)
            val rangeCheck = actual.toLong()
            assertTrue(rangeCheck >= min.toLong())
            assertTrue(rangeCheck <= max.toLong())
            println("UURandom.int: $actual")
        }
    }

    @Test
    fun test_uInt()
    {
        repeat(loops)
        {
            val actual = UURandom.uInt()
            assertNotNull(actual)
            val rangeCheck = actual.toLong()
            assertTrue(rangeCheck >= UInt.MIN_VALUE.toLong())
            assertTrue(rangeCheck <= UInt.MAX_VALUE.toLong())
            println("UURandom.uInt: $actual")
        }
    }

    @Test
    fun test_long()
    {
        repeat(loops)
        {
            val actual = UURandom.long()
            assertNotNull(actual)
            println("UURandom.long: $actual")
        }
    }

    @Test
    fun test_uLong()
    {
        repeat(loops)
        {
            val actual = UURandom.uLong()
            assertNotNull(actual)
            println("UURandom.uLong: $actual")
        }
    }

    @Test
    fun test_float()
    {
        repeat(loops)
        {
            val actual = UURandom.float()
            assertNotNull(actual)
            println("UURandom.float: $actual")
        }
    }

    @Test
    fun test_double()
    {
        repeat(loops)
        {
            val actual = UURandom.double()
            assertNotNull(actual)
            println("UURandom.double: $actual")
        }
    }

    @Test
    fun test_bool()
    {
        repeat(loops)
        {
            val actual = UURandom.bool()
            assertNotNull(actual)
            println("UURandom.bool: $actual")
        }
    }

    @Test
    fun test_char()
    {
        repeat(loops)
        {
            val actual = UURandom.char()
            assertNotNull(actual)
            val rangeCheck = actual.code
            assertTrue(rangeCheck >= Char.MIN_VALUE.code)
            assertTrue(rangeCheck <= Char.MAX_VALUE.code)
            println("UURandom.char: $actual")
        }
    }

    @Test
    fun test_uuid()
    {
        repeat(loops)
        {
            val actual = UURandom.uuid()
            assertNotNull(actual)
            val check = UUID.fromString(actual)
            assertNotNull(check)
            println("UURandom.uuid: $actual")
        }
    }

    @Test
    fun test_byteObjArray()
    {
        repeat(loops)
        {
            val length = randomCount()
            val actual = UURandom.byteObjArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.byteObjArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_uByteObjArray()
    {
        repeat(loops)
        {
            val length = randomCount()
            val actual = UURandom.uByteObjArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.uByteObjArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_shortObjArray()
    {
        repeat(loops)
        {
            val length = randomCount()
            val actual = UURandom.shortObjArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.shortObjArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_uShortObjArray()
    {
        repeat(loops)
        {
            val length = randomCount()
            val actual = UURandom.uShortObjArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.uShortObjArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_intObjArray()
    {
        repeat(loops)
        {
            val length = randomCount()
            val actual = UURandom.intObjArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.intObjArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_uIntObjArray()
    {
        repeat(loops)
        {
            val length = randomCount()
            val actual = UURandom.uIntObjArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.uIntObjArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_longObjArray()
    {
        repeat(loops)
        {
            val length = randomCount()
            val actual = UURandom.longObjArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.longObjArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_uLongObjArray()
    {
        repeat(loops)
        {
            val length = randomCount()
            val actual = UURandom.uLongObjArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.uLongObjArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_floatObjArray()
    {
        repeat(loops)
        {
            val length = randomCount()
            val actual = UURandom.floatObjArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.floatObjArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_doubleObjArray()
    {
        repeat(loops)
        {
            val length = randomCount()
            val actual = UURandom.doubleObjArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.doubleObjArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_boolObjArray()
    {
        repeat(loops)
        {
            val length = randomCount()
            val actual = UURandom.boolObjArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.boolObjArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_charObjArray()
    {
        repeat(loops)
        {
            val length = randomCount()
            val actual = UURandom.charObjArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.charObjArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_shortArray()
    {
        repeat(loops)
        {
            val length = randomCount()
            val actual = UURandom.shortArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.shortArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_intArray()
    {
        repeat(loops)
        {
            val length = randomCount()
            val actual = UURandom.intArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.intArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_longArray()
    {
        repeat(loops)
        {
            val length = randomCount()
            val actual = UURandom.longArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.longArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_floatArray()
    {
        repeat(loops)
        {
            val length = randomCount()
            val actual = UURandom.floatArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.floatArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_doubleArray()
    {
        repeat(loops)
        {
            val length = randomCount()
            val actual = UURandom.doubleArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.doubleArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_boolArray()
    {
        repeat(loops)
        {
            val length = randomCount()
            val actual = UURandom.boolArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.boolArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_charArray()
    {
        repeat(loops)
        {
            val length = randomCount()
            val actual = UURandom.charArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.charArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_int_max()
    {
        val max = 1000
        repeat(loops)
        {
            val actual = UURandom.int(max)
            assertNotNull(actual)
            val rangeCheck = actual.toLong()
            assertTrue(rangeCheck >= 0)
            assertTrue(rangeCheck < max)
            println("UURandom.int(max): $actual")
        }
    }

    @Test
    fun test_asciiLetters()
    {
        repeat(loops)
        {
            val length = randomCount()
            val actual = UURandom.asciiLetters(length)
            assertNotNull(actual)
            assertTrue(actual.length in 0..length)
            
            // Check that all characters are ASCII letters
            for (char in actual)
            {
                assertTrue(char in 'A'..'Z' || char in 'a'..'z')
            }
            println("UURandom.asciiLetters: $actual")
        }
    }

    @Test
    fun test_digits()
    {
        repeat(loops)
        {
            val length = randomCount()
            val actual = UURandom.digits(length)
            assertNotNull(actual)
            assertTrue(actual.length in 0..length)
            
            // Check that all characters are digits
            for (char in actual)
            {
                assertTrue(char in '0'..'9')
            }
            println("UURandom.digits: $actual")
        }
    }

    @Test
    fun test_asciiLettersOrNumbers()
    {
        repeat(loops)
        {
            val length = randomCount()
            val actual = UURandom.asciiLettersOrNumbers(length)
            assertNotNull(actual)
            assertTrue(actual.length in 0..length)
            
            // Check that all characters are ASCII letters or numbers
            for (char in actual)
            {
                assertTrue(char in 'A'..'Z' || char in 'a'..'z' || char in '0'..'9')
            }
            println("UURandom.asciiLettersOrNumbers: $actual")
        }
    }

    @Test
    fun test_chars()
    {
        repeat(loops)
        {
            val length = randomCount()
            val ranges = arrayListOf(Pair('A', 'Z'), Pair('a', 'z'))
            val actual = UURandom.chars(length, ranges)
            assertNotNull(actual)
            assertTrue(actual.length in 0..length)
            
            // Check that all characters are in the specified ranges
            for (char in actual)
            {
                assertTrue(char in 'A'..'Z' || char in 'a'..'z')
            }
            println("UURandom.chars: $actual")
        }
    }

    @Test
    fun test_asciiWord()
    {
        repeat(loops)
        {
            val length = randomCount()
            val actual = UURandom.asciiWord(length)
            assertNotNull(actual)
            assertTrue(actual.length in 0..length)
            
            // Check that all characters are ASCII letters
            for (char in actual)
            {
                assertTrue(char in 'A'..'Z' || char in 'a'..'z')
            }
            println("UURandom.asciiWord: $actual")
        }
    }

    @Test
    fun test_asciiWords()
    {
        repeat(loops)
        {
            val maxWords = randomCount()
            val maxWordLength = randomCount()
            val actual = UURandom.asciiWords(maxWords, maxWordLength)
            assertNotNull(actual)
            
            // Split by spaces and check each word
            val words = actual.trim().split(" ")
            assertTrue(words.size <= maxWords)
            
            for (word in words)
            {
                if (word.isNotEmpty())
                {
                    assertTrue(word.length <= maxWordLength)
                    // Check that all characters are ASCII letters
                    for (char in word)
                    {
                        assertTrue(char in 'A'..'Z' || char in 'a'..'z')
                    }
                }
            }
            println("UURandom.asciiWords: $actual")
        }
    }

    @Test
    fun test_objArray()
    {
        data class TestObj(val a: Int, val b: Int)

        fun makeRandom(): TestObj
        {
            return TestObj(UURandom.int(), UURandom.int())
        }

        repeat(loops)
        {
            val length = randomCount()
            val actual = UURandom.objArray(length) { makeRandom() }
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.objArray: ${ actual.map { "${it.a}-${it.b}" } }")
        }
    }

    private fun randomCount(): Int
    {
        return UURandom.int(1, 22)
    }
}