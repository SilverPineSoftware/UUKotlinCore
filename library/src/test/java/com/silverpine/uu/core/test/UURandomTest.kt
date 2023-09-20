package com.silverpine.uu.core.test

import com.silverpine.uu.core.UURandom
import com.silverpine.uu.core.uuToHex
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class UURandomTest
{
    private val LOOPS = 100

    @Test
    fun test_bytes()
    {
        for (i in 0 until LOOPS)
        {
            val length = UURandom.uByte().toInt()
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
        for (i in 0 until LOOPS)
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
        for (i in 0 until LOOPS)
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
        for (i in 0 until LOOPS)
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
        for (i in 0 until LOOPS)
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
        for (i in 0 until LOOPS)
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
        for (i in 0 until LOOPS)
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
        for (i in 0 until LOOPS)
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
        for (i in 0 until LOOPS)
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
        for (i in 0 until LOOPS)
        {
            val actual = UURandom.long()
            assertNotNull(actual)
            println("UURandom.long: $actual")
        }
    }

    @Test
    fun test_uLong()
    {
        for (i in 0 until LOOPS)
        {
            val actual = UURandom.uLong()
            assertNotNull(actual)
            println("UURandom.uLong: $actual")
        }
    }

    @Test
    fun test_float()
    {
        for (i in 0 until LOOPS)
        {
            val actual = UURandom.float()
            assertNotNull(actual)
            println("UURandom.float: $actual")
        }
    }

    @Test
    fun test_double()
    {
        for (i in 0 until LOOPS)
        {
            val actual = UURandom.double()
            assertNotNull(actual)
            println("UURandom.double: $actual")
        }
    }

    @Test
    fun test_bool()
    {
        for (i in 0 until LOOPS)
        {
            val actual = UURandom.bool()
            assertNotNull(actual)
            println("UURandom.bool: $actual")
        }
    }

    @Test
    fun test_char()
    {
        for (i in 0 until LOOPS)
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
        for (i in 0 until LOOPS)
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
        for (i in 0 until LOOPS)
        {
            val length = UURandom.uByte().toInt()
            val actual = UURandom.byteObjArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.byteObjArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_uByteObjArray()
    {
        for (i in 0 until LOOPS)
        {
            val length = UURandom.uByte().toInt()
            val actual = UURandom.uByteObjArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.uByteObjArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_shortObjArray()
    {
        for (i in 0 until LOOPS)
        {
            val length = UURandom.uByte().toInt()
            val actual = UURandom.shortObjArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.shortObjArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_uShortObjArray()
    {
        for (i in 0 until LOOPS)
        {
            val length = UURandom.uByte().toInt()
            val actual = UURandom.uShortObjArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.uShortObjArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_intObjArray()
    {
        for (i in 0 until LOOPS)
        {
            val length = UURandom.uByte().toInt()
            val actual = UURandom.intObjArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.intObjArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_uIntObjArray()
    {
        for (i in 0 until LOOPS)
        {
            val length = UURandom.uByte().toInt()
            val actual = UURandom.uIntObjArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.uIntObjArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_longObjArray()
    {
        for (i in 0 until LOOPS)
        {
            val length = UURandom.uByte().toInt()
            val actual = UURandom.longObjArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.longObjArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_uLongObjArray()
    {
        for (i in 0 until LOOPS)
        {
            val length = UURandom.uByte().toInt()
            val actual = UURandom.uLongObjArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.uLongObjArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_floatObjArray()
    {
        for (i in 0 until LOOPS)
        {
            val length = UURandom.uByte().toInt()
            val actual = UURandom.floatObjArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.floatObjArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_doubleObjArray()
    {
        for (i in 0 until LOOPS)
        {
            val length = UURandom.uByte().toInt()
            val actual = UURandom.doubleObjArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.doubleObjArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_boolObjArray()
    {
        for (i in 0 until LOOPS)
        {
            val length = UURandom.uByte().toInt()
            val actual = UURandom.boolObjArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.boolObjArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_charObjArray()
    {
        for (i in 0 until LOOPS)
        {
            val length = UURandom.uByte().toInt()
            val actual = UURandom.charObjArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.charObjArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_shortArray()
    {
        for (i in 0 until LOOPS)
        {
            val length = UURandom.uByte().toInt()
            val actual = UURandom.shortArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.shortArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_intArray()
    {
        for (i in 0 until LOOPS)
        {
            val length = UURandom.uByte().toInt()
            val actual = UURandom.intArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.intArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_longArray()
    {
        for (i in 0 until LOOPS)
        {
            val length = UURandom.uByte().toInt()
            val actual = UURandom.longArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.longArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_floatArray()
    {
        for (i in 0 until LOOPS)
        {
            val length = UURandom.uByte().toInt()
            val actual = UURandom.floatArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.floatArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_doubleArray()
    {
        for (i in 0 until LOOPS)
        {
            val length = UURandom.uByte().toInt()
            val actual = UURandom.doubleArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.doubleArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_boolArray()
    {
        for (i in 0 until LOOPS)
        {
            val length = UURandom.uByte().toInt()
            val actual = UURandom.boolArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.boolArray: ${ actual.map { it } }")
        }
    }

    @Test
    fun test_charArray()
    {
        for (i in 0 until LOOPS)
        {
            val length = UURandom.uByte().toInt()
            val actual = UURandom.charArray(length)
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.charArray: ${ actual.map { it } }")
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

        for (i in 0 until LOOPS)
        {
            val length = UURandom.uByte().toInt()
            val actual = UURandom.objArray(length) { makeRandom() }
            assertNotNull(actual)
            assertTrue(actual.size in 0..length)
            println("UURandom.objArray: ${ actual.map { "${it.a}-${it.b}" } }")
        }
    }


}