package com.silverpine.uu.core

import java.nio.ByteBuffer
import java.security.SecureRandom
import java.util.*

/**
 * UURandom
 * Useful Utilities - UURandom contains helper methods for generating random data
 *
 * License:
 * You are free to use this code for whatever purposes you desire.
 * The only requirement is that you smile everytime you use it. :-)
 */
object UURandom
{
    private val secureRandom = SecureRandom.getInstanceStrong()

    /**
     * Generates a random byte array using java.security.SecureRandom
     *
     * @param length length of byte array to generate
     * @return byte array filled with data.
     * @see java.security.SecureRandom
     */
    fun bytes(length: Int): ByteArray
    {
        if (length < 0)
        {
            return ByteArray(0)
        }

        val output = ByteArray(length)
        secureRandom.nextBytes(output)
        return output
    }

    /**
     * Generates a random integer with an upper and lower bound
     *
     * @param min minimum value
     * @param max maximum value
     * @return a random integer
     */
    fun int(min: Int, max: Int): Int
    {
        val range = max - min
        return secureRandom.nextInt(range) + min
    }

    /**
     * Generates a random integer with an upper bound
     *
     * @param max maximum value
     * @return a random integer
     */
    fun int(max: Int): Int
    {
        return int(0, max)
    }

    /**
     * Generates a random integer
     *
     * @return a random integer
     */
    fun int(): Int
    {
        return secureRandom.nextInt()
    }

    /**
     * Generates a random unsigned integer
     *
     * @return a random unsigned integer
     */
    fun uInt(): UInt
    {
        return int().toUInt()
    }

    /**
     * Generates a random long
     *
     * @return a random long
     */
    fun long(): Long
    {
        return secureRandom.nextLong()
    }

    /**
     * Generates a random unsigned long
     *
     * @return a random unsigned long
     */
    fun uLong(): ULong
    {
        return long().toULong()
    }

    /**
     * Generates a random double
     *
     * @return a random double
     */
    fun double(): Double
    {
        return secureRandom.nextDouble()
    }

    /**
     * Generates a random float
     *
     * @return a random float
     */
    fun float(): Float
    {
        return secureRandom.nextFloat()
    }

    /**
     * Generates a random boolean
     *
     * @return a random boolean
     */
    fun bool(): Boolean
    {
        return secureRandom.nextBoolean()
    }

    /**
     * Generates a random byte
     *
     * @return a random byte
     */
    fun byte(): Byte
    {
        val result = bytes(1)
        return result[0]
    }

    /**
     * Generates a random unsigned byte
     *
     * @return a random unsigned byte
     */
    fun uByte(): UByte
    {
        val result = bytes(1)
        return result[0].toUByte()
    }

    /**
     * Generates a random short
     *
     * @return a random short
     */
    fun short(): Short
    {
        val result = bytes(2)
        val bb = ByteBuffer.wrap(result)
        return bb.short
    }

    /**
     * Generates a random unsigned short
     *
     * @return a random unsigned short
     */
    fun uShort(): UShort
    {
        return short().toUShort()
    }

    /**
     * Generates a random byte
     *
     * @return a random byte
     */
    fun char(): Char
    {
        return Char(uShort())
    }

    /**
     * Generates a random UUID and returns the string form
     *
     * @return a random string
     */
    fun uuid(): String
    {
        val uid = UUID.randomUUID()
        return uid.toString()
    }

    inline fun <reified T> objArray(maxLength: Int, random: ()->T): Array<T>
    {
        val length = int(maxLength)

        val working = arrayListOf<T>()

        for (i in 0 until length)
        {
            working.add(random())
        }

        return working.toTypedArray()
    }

    fun byteObjArray(maxLength: Int): Array<Byte>
    {
        return objArray(maxLength, this::byte)
    }

    fun uByteObjArray(maxLength: Int): Array<UByte>
    {
        return objArray(maxLength, this::uByte)
    }

    fun shortObjArray(maxLength: Int): Array<Short>
    {
        return objArray(maxLength, this::short)
    }

    fun uShortObjArray(maxLength: Int): Array<UShort>
    {
        return objArray(maxLength, this::uShort)
    }

    fun intObjArray(maxLength: Int): Array<Int>
    {
        return objArray(maxLength, this::int)
    }

    fun uIntObjArray(maxLength: Int): Array<UInt>
    {
        return objArray(maxLength, this::uInt)
    }

    fun longObjArray(maxLength: Int): Array<Long>
    {
        return objArray(maxLength, this::long)
    }

    fun uLongObjArray(maxLength: Int): Array<ULong>
    {
        return objArray(maxLength, this::uLong)
    }

    fun floatObjArray(maxLength: Int): Array<Float>
    {
        return objArray(maxLength, this::float)
    }

    fun doubleObjArray(maxLength: Int): Array<Double>
    {
        return objArray(maxLength, this::double)
    }

    fun boolObjArray(maxLength: Int): Array<Boolean>
    {
        return objArray(maxLength, this::bool)
    }

    fun charObjArray(maxLength: Int): Array<Char>
    {
        return objArray(maxLength, this::char)
    }

    fun shortArray(maxLength: Int): ShortArray
    {
        val length = int(maxLength)

        val a = ShortArray(length)

        for (i in 0 until length)
        {
            a[i] = short()
        }

        return a
    }

    fun intArray(maxLength: Int): IntArray
    {
        val length = int(maxLength)

        val a = IntArray(length)

        for (i in 0 until length)
        {
            a[i] = int()
        }
        return a
    }

    fun longArray(maxLength: Int): LongArray
    {
        val length = int(maxLength)

        val a = LongArray(length)

        for (i in 0 until length)
        {
            a[i] = long()
        }

        return a
    }

    fun floatArray(maxLength: Int): FloatArray
    {
        val length = int(maxLength)

        val a = FloatArray(length)

        for (i in 0 until length)
        {
            a[i] = float()
        }
        return a
    }

    fun doubleArray(maxLength: Int): DoubleArray
    {
        val length = int(maxLength)

        val a = DoubleArray(length)

        for (i in 0 until length)
        {
            a[i] = double()
        }

        return a
    }

    fun boolArray(maxLength: Int): BooleanArray
    {
        val length = int(maxLength)

        val a = BooleanArray(length)

        for (i in 0 until length)
        {
            a[i] = bool()
        }

        return a
    }

    fun charArray(maxLength: Int): CharArray
    {
        val length = int(maxLength)

        val a = CharArray(length)

        for (i in 0 until length)
        {
            a[i] = char()
        }
        return a
    }
}