package com.silverpine.uu.core

import java.nio.ByteBuffer
import java.security.SecureRandom
import java.util.UUID

/**
 * UURandom
 * Useful Utilities - UURandom contains helper methods for generating random data
 *
 * License:
 * You are free to use this code for whatever purposes you desire.
 * The only requirement is that you smile everytime you use it. :-)
 *
 * @since 1.0.0
 */
object UURandom
{
    private val secureRandom = SecureRandom.getInstanceStrong()

    private val UPPER_CASE = Pair('A','Z')
    private val LOWER_CASE = Pair('a','z')
    private val NUMBERS = Pair('0','9')

    /**
     * Generates a random byte array using java.security.SecureRandom
     *
     * @since 1.0.0
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
     * Generates a random byte array or null using java.security.SecureRandom
     *
     * @since 1.0.0
     * @param length length of byte array to generate
     * @return byte array filled with data or null
     * @see java.security.SecureRandom
     */
    fun bytesOrNull(length: Int): ByteArray?
    {
        return if (bool()) bytes(length) else null
    }

    /**
     * Generates a random integer with an upper and lower bound
     *
     * @since 1.0.0
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
     * Generates a random integer with an upper and lower bound or null
     *
     * @since 1.0.0
     * @param min minimum value
     * @param max maximum value
     * @return a random integer or null
     */
    fun intOrNull(min: Int, max: Int): Int?
    {
        return if (bool()) int(min, max) else null
    }

    /**
     * Generates a random integer with an upper bound
     *
     * @since 1.0.0
     * @param max maximum value
     * @return a random integer
     */
    fun int(max: Int): Int
    {
        return int(0, max)
    }

    /**
     * Generates a random integer with an upper bound or null
     *
     * @since 1.0.0
     * @param max maximum value
     * @return a random integer or null
     */
    fun intOrNull(max: Int): Int?
    {
        return if (bool()) int(max) else null
    }

    /**
     * Generates a random integer
     *
     * @since 1.0.0
     * @return a random integer
     */
    fun int(): Int
    {
        return secureRandom.nextInt()
    }

    /**
     * Generates a random integer or null
     *
     * @since 1.0.0
     * @return a random integer or null
     */
    fun intOrNull(): Int?
    {
        return if (bool()) int() else null
    }

    /**
     * Generates a random unsigned integer
     *
     * @since 1.0.0
     * @return a random unsigned integer
     */
    fun uInt(): UInt
    {
        return int().toUInt()
    }

    /**
     * Generates a random unsigned integer or null
     *
     * @since 1.0.0
     * @return a random unsigned integer or null
     */
    fun uIntOrNull(): UInt?
    {
        return if (bool()) uInt() else null
    }

    /**
     * Generates a random long
     *
     * @since 1.0.0
     * @return a random long
     */
    fun long(): Long
    {
        return secureRandom.nextLong()
    }

    /**
     * Generates a random long or null
     *
     * @since 1.0.0
     * @return a random long or null
     */
    fun longOrNull(): Long?
    {
        return if (bool()) long() else null
    }

    /**
     * Generates a random unsigned long
     *
     * @since 1.0.0
     * @return a random unsigned long
     */
    fun uLong(): ULong
    {
        return long().toULong()
    }

    /**
     * Generates a random unsigned long or null
     *
     * @since 1.0.0
     * @return a random unsigned long or null
     */
    fun uLongOrNull(): ULong?
    {
        return if (bool()) uLong() else null
    }

    /**
     * Generates a random double
     *
     * @since 1.0.0
     * @return a random double
     */
    fun double(): Double
    {
        return secureRandom.nextDouble()
    }

    /**
     * Generates a random double or null
     *
     * @since 1.0.0
     * @return a random double or null
     */
    fun doubleOrNull(): Double?
    {
        return if (bool()) double() else null
    }

    /**
     * Generates a random float
     *
     * @since 1.0.0
     * @return a random float
     */
    fun float(): Float
    {
        return secureRandom.nextFloat()
    }

    /**
     * Generates a random float or null
     *
     * @since 1.0.0
     * @return a random float or null
     */
    fun floatOrNull(): Float?
    {
        return if (bool()) float() else null
    }

    /**
     * Generates a random boolean
     *
     * @since 1.0.0
     * @return a random boolean
     */
    fun bool(): Boolean
    {
        return secureRandom.nextBoolean()
    }

    /**
     * Generates a random boolean or null
     *
     * @since 1.0.0
     * @return a random boolean or null
     */
    fun boolOrNull(): Boolean?
    {
        return if (bool()) bool() else null
    }

    /**
     * Generates a random byte
     *
     * @since 1.0.0
     * @return a random byte
     */
    fun byte(): Byte
    {
        val result = bytes(1)
        return result[0]
    }

    /**
     * Generates a random byte or null
     *
     * @since 1.0.0
     * @return a random byte or null
     */
    fun byteOrNull(): Byte?
    {
        return if (bool()) byte() else null
    }

    /**
     * Generates a random unsigned byte
     *
     * @since 1.0.0
     * @return a random unsigned byte
     */
    fun uByte(): UByte
    {
        val result = bytes(1)
        return result[0].toUByte()
    }

    /**
     * Generates a random unsigned byte or null
     *
     * @since 1.0.0
     * @return a random unsigned byte or null
     */
    fun uByteOrNull(): UByte?
    {
        return if (bool()) uByte() else null
    }

    /**
     * Generates a random short
     *
     * @since 1.0.0
     * @return a random short
     */
    fun short(): Short
    {
        val result = bytes(2)
        val bb = ByteBuffer.wrap(result)
        return bb.short
    }

    /**
     * Generates a random short or null
     *
     * @since 1.0.0
     * @return a random short or null
     */
    fun shortOrNull(): Short?
    {
        return if (bool()) short() else null
    }

    /**
     * Generates a random unsigned short
     *
     * @since 1.0.0
     * @return a random unsigned short
     */
    fun uShort(): UShort
    {
        return short().toUShort()
    }

    /**
     * Generates a random unsigned short or null
     *
     * @since 1.0.0
     * @return a random unsigned short or null
     */
    fun uShortOrNull(): UShort?
    {
        return if (bool()) uShort() else null
    }

    /**
     * Generates a random byte
     *
     * @since 1.0.0
     * @return a random byte
     */
    fun char(): Char
    {
        return Char(uShort())
    }

    /**
     * Generates a random char or null
     *
     * @since 1.0.0
     * @return a random char or null
     */
    fun charOrNull(): Char?
    {
        return if (bool()) char() else null
    }

    /**
     * Generates a random UUID and returns the string form
     *
     * @since 1.0.0
     * @return a random string
     */
    fun uuid(): String
    {
        val uid = UUID.randomUUID()
        return uid.toString()
    }

    /**
     * Generates a random UUID and returns the string form or null
     *
     * @since 1.0.0
     * @return a random string or null
     */
    fun uuidOrNull(): String?
    {
        return if (bool()) uuid() else null
    }
    
    /**
     * Generates a random array of objects using a provided random generator function
     *
     * @since 1.0.0
     * @param T the type of objects to generate
     * @param maxLength maximum length of the array to generate
     * @param random function that generates a random object of type T
     * @return array of randomly generated objects
     */
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

    /**
     * Generates a random array of objects using a provided random generator function or null
     *
     * @since 1.0.0
     * @param T the type of objects to generate
     * @param maxLength maximum length of the array to generate
     * @param random function that generates a random object of type T
     * @return array of randomly generated objects or null
     */
    inline fun <reified T> objArrayOrNull(maxLength: Int, random: ()->T): Array<T>?
    {
        return if (bool()) objArray(maxLength, random) else null
    }

    /**
     * Generates a random array of Byte objects
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return array of randomly generated Byte objects
     */
    fun byteObjArray(maxLength: Int): Array<Byte>
    {
        return objArray(maxLength, this::byte)
    }

    /**
     * Generates a random array of Byte objects or null
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return array of randomly generated Byte objects or null
     */
    fun byteObjArrayOrNull(maxLength: Int): Array<Byte>?
    {
        return if (bool()) byteObjArray(maxLength) else null
    }

    /**
     * Generates a random array of UByte objects
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return array of randomly generated UByte objects
     */
    fun uByteObjArray(maxLength: Int): Array<UByte>
    {
        return objArray(maxLength, this::uByte)
    }

    /**
     * Generates a random array of UByte objects or null
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return array of randomly generated UByte objects or null
     */
    fun uByteObjArrayOrNull(maxLength: Int): Array<UByte>?
    {
        return if (bool()) uByteObjArray(maxLength) else null
    }

    /**
     * Generates a random array of Short objects
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return array of randomly generated Short objects
     */
    fun shortObjArray(maxLength: Int): Array<Short>
    {
        return objArray(maxLength, this::short)
    }

    /**
     * Generates a random array of Short objects or null
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return array of randomly generated Short objects or null
     */
    fun shortObjArrayOrNull(maxLength: Int): Array<Short>?
    {
        return if (bool()) shortObjArray(maxLength) else null
    }

    /**
     * Generates a random array of UShort objects
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return array of randomly generated UShort objects
     */
    fun uShortObjArray(maxLength: Int): Array<UShort>
    {
        return objArray(maxLength, this::uShort)
    }

    /**
     * Generates a random array of UShort objects or null
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return array of randomly generated UShort objects or null
     */
    fun uShortObjArrayOrNull(maxLength: Int): Array<UShort>?
    {
        return if (bool()) uShortObjArray(maxLength) else null
    }

    /**
     * Generates a random array of Int objects
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return array of randomly generated Int objects
     */
    fun intObjArray(maxLength: Int): Array<Int>
    {
        return objArray(maxLength, this::int)
    }

    /**
     * Generates a random array of Int objects or null
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return array of randomly generated Int objects or null
     */
    fun intObjArrayOrNull(maxLength: Int): Array<Int>?
    {
        return if (bool()) intObjArray(maxLength) else null
    }

    /**
     * Generates a random array of UInt objects
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return array of randomly generated UInt objects
     */
    fun uIntObjArray(maxLength: Int): Array<UInt>
    {
        return objArray(maxLength, this::uInt)
    }

    /**
     * Generates a random array of UInt objects or null
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return array of randomly generated UInt objects or null
     */
    fun uIntObjArrayOrNull(maxLength: Int): Array<UInt>?
    {
        return if (bool()) uIntObjArray(maxLength) else null
    }

    /**
     * Generates a random array of Long objects
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return array of randomly generated Long objects
     */
    fun longObjArray(maxLength: Int): Array<Long>
    {
        return objArray(maxLength, this::long)
    }

    /**
     * Generates a random array of Long objects or null
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return array of randomly generated Long objects or null
     */
    fun longObjArrayOrNull(maxLength: Int): Array<Long>?
    {
        return if (bool()) longObjArray(maxLength) else null
    }

    /**
     * Generates a random array of ULong objects
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return array of randomly generated ULong objects
     */
    fun uLongObjArray(maxLength: Int): Array<ULong>
    {
        return objArray(maxLength, this::uLong)
    }

    /**
     * Generates a random array of ULong objects or null
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return array of randomly generated ULong objects or null
     */
    fun uLongObjArrayOrNull(maxLength: Int): Array<ULong>?
    {
        return if (bool()) uLongObjArray(maxLength) else null
    }

    /**
     * Generates a random array of Float objects
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return array of randomly generated Float objects
     */
    fun floatObjArray(maxLength: Int): Array<Float>
    {
        return objArray(maxLength, this::float)
    }

    /**
     * Generates a random array of Float objects or null
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return array of randomly generated Float objects or null
     */
    fun floatObjArrayOrNull(maxLength: Int): Array<Float>?
    {
        return if (bool()) floatObjArray(maxLength) else null
    }

    /**
     * Generates a random array of Double objects
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return array of randomly generated Double objects
     */
    fun doubleObjArray(maxLength: Int): Array<Double>
    {
        return objArray(maxLength, this::double)
    }

    /**
     * Generates a random array of Double objects or null
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return array of randomly generated Double objects or null
     */
    fun doubleObjArrayOrNull(maxLength: Int): Array<Double>?
    {
        return if (bool()) doubleObjArray(maxLength) else null
    }

    /**
     * Generates a random array of Boolean objects
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return array of randomly generated Boolean objects
     */
    fun boolObjArray(maxLength: Int): Array<Boolean>
    {
        return objArray(maxLength, this::bool)
    }

    /**
     * Generates a random array of Boolean objects or null
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return array of randomly generated Boolean objects or null
     */
    fun boolObjArrayOrNull(maxLength: Int): Array<Boolean>?
    {
        return if (bool()) boolObjArray(maxLength) else null
    }

    /**
     * Generates a random array of Char objects
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return array of randomly generated Char objects
     */
    fun charObjArray(maxLength: Int): Array<Char>
    {
        return objArray(maxLength, this::char)
    }

    /**
     * Generates a random array of Char objects or null
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return array of randomly generated Char objects or null
     */
    fun charObjArrayOrNull(maxLength: Int): Array<Char>?
    {
        return if (bool()) charObjArray(maxLength) else null
    }

    /**
     * Generates a random ShortArray
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return ShortArray filled with random values
     */
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

    /**
     * Generates a random ShortArray or null
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return ShortArray filled with random values or null
     */
    fun shortArrayOrNull(maxLength: Int): ShortArray?
    {
        return if (bool()) shortArray(maxLength) else null
    }

    /**
     * Generates a random IntArray
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return IntArray filled with random values
     */
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

    /**
     * Generates a random IntArray or null
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return IntArray filled with random values or null
     */
    fun intArrayOrNull(maxLength: Int): IntArray?
    {
        return if (bool()) intArray(maxLength) else null
    }

    /**
     * Generates a random LongArray
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return LongArray filled with random values
     */
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

    /**
     * Generates a random LongArray or null
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return LongArray filled with random values or null
     */
    fun longArrayOrNull(maxLength: Int): LongArray?
    {
        return if (bool()) longArray(maxLength) else null
    }

    /**
     * Generates a random FloatArray
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return FloatArray filled with random values
     */
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

    /**
     * Generates a random FloatArray or null
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return FloatArray filled with random values or null
     */
    fun floatArrayOrNull(maxLength: Int): FloatArray?
    {
        return if (bool()) floatArray(maxLength) else null
    }

    /**
     * Generates a random DoubleArray
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return DoubleArray filled with random values
     */
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

    /**
     * Generates a random DoubleArray or null
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return DoubleArray filled with random values or null
     */
    fun doubleArrayOrNull(maxLength: Int): DoubleArray?
    {
        return if (bool()) doubleArray(maxLength) else null
    }

    /**
     * Generates a random BooleanArray
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return BooleanArray filled with random values
     */
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

    /**
     * Generates a random BooleanArray or null
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return BooleanArray filled with random values or null
     */
    fun boolArrayOrNull(maxLength: Int): BooleanArray?
    {
        return if (bool()) boolArray(maxLength) else null
    }

    /**
     * Generates a random CharArray
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return CharArray filled with random values
     */
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

    /**
     * Generates a random CharArray or null
     *
     * @since 1.0.0
     * @param maxLength maximum length of the array to generate
     * @return CharArray filled with random values or null
     */
    fun charArrayOrNull(maxLength: Int): CharArray?
    {
        return if (bool()) charArray(maxLength) else null
    }

    /**
     * Generates a random string containing only ASCII letters (A-Z, a-z)
     *
     * @since 1.0.0
     * @param maxLength maximum length of the string to generate
     * @return random string containing only ASCII letters
     */
    fun asciiLetters(maxLength: Int): String
    {
        return chars(maxLength, arrayListOf(UPPER_CASE, LOWER_CASE))
    }

    /**
     * Generates a random string containing only ASCII letters or null (A-Z, a-z)
     *
     * @since 1.0.0
     * @param maxLength maximum length of the string to generate
     * @return random string containing only ASCII letters or null
     */
    fun asciiLettersOrNull(maxLength: Int): String?
    {
        return if (bool()) asciiLetters(maxLength) else null
    }

    /**
     * Generates a random string containing only digits (0-9)
     *
     * @since 1.0.0
     * @param maxLength maximum length of the string to generate
     * @return random string containing only digits
     */
    fun digits(maxLength: Int): String
    {
        return chars(maxLength, arrayListOf(NUMBERS))
    }

    /**
     * Generates a random string containing only digits or null (0-9)
     *
     * @since 1.0.0
     * @param maxLength maximum length of the string to generate
     * @return random string containing only digits or null
     */
    fun digitsOrNull(maxLength: Int): String?
    {
        return if (bool()) digits(maxLength) else null
    }

    /**
     * Generates a random string containing ASCII letters and numbers (A-Z, a-z, 0-9)
     *
     * @since 1.0.0
     * @param maxLength maximum length of the string to generate
     * @return random string containing ASCII letters and numbers
     */
    fun asciiLettersOrNumbers(maxLength: Int): String
    {
        return chars(maxLength, arrayListOf(UPPER_CASE, LOWER_CASE, NUMBERS))
    }

    /**
     * Generates a random string containing ASCII letters and numbers or null (A-Z, a-z, 0-9)
     *
     * @since 1.0.0
     * @param maxLength maximum length of the string to generate
     * @return random string containing ASCII letters and numbers or null
     */
    fun asciiLettersOrNumbersOrNull(maxLength: Int): String?
    {
        return if (bool()) asciiLettersOrNumbers(maxLength) else null
    }

    /**
     * Generates a random string containing characters from specified ranges
     *
     * @since 1.0.0
     * @param maxLength maximum length of the string to generate
     * @param ranges list of character ranges to choose from (defaults to all characters)
     * @return random string containing characters from the specified ranges
     */
    fun chars(
        maxLength: Int,
        ranges: ArrayList<Pair<Char, Char>> = arrayListOf(Pair(Char.MIN_VALUE, Char.MAX_VALUE))): String
    {
        val length = int(maxLength)

        val sb = StringBuilder()

        while (sb.length < length)
        {
            val c = char()

            for (i in 0 until ranges.size)
            {
                if (c in ranges[i].first .. ranges[i].second)
                {
                    sb.append(c)
                    break
                }
            }
        }

        return sb.toString()
    }

    /**
     * Generates a random string containing characters from specified ranges or null
     *
     * @since 1.0.0
     * @param maxLength maximum length of the string to generate
     * @param ranges list of character ranges to choose from (defaults to all characters)
     * @return random string containing characters from the specified ranges or null
     */
    fun charsOrNull(
        maxLength: Int,
        ranges: ArrayList<Pair<Char, Char>> = arrayListOf(Pair(Char.MIN_VALUE, Char.MAX_VALUE))): String?
    {
        return if (bool()) chars(maxLength, ranges) else null
    }

    /**
     * Generates a random ASCII word (letters only)
     *
     * @since 1.0.0
     * @param maxLength maximum length of the word to generate
     * @return random ASCII word containing only letters
     */
    fun asciiWord(maxLength: Int): String
    {
        return asciiLetters(maxLength)
    }

    /**
     * Generates a random ASCII word or null (letters only)
     *
     * @since 1.0.0
     * @param maxLength maximum length of the word to generate
     * @return random ASCII word containing only letters or null
     */
    fun asciiWordOrNull(maxLength: Int): String?
    {
        return if (bool()) asciiWord(maxLength) else null
    }

    /**
     * Generates a random string containing multiple ASCII words separated by spaces
     *
     * @since 1.0.0
     * @param maxNumberOfWords maximum number of words to generate
     * @param maxWordLength maximum length of each word
     * @return random string containing ASCII words separated by spaces
     */
    fun asciiWords(maxNumberOfWords: Int, maxWordLength: Int): String
    {
        val sb = StringBuilder()
        val words = int(maxNumberOfWords)

        for (i in 0 until words)
        {
            sb.append(asciiWord(maxWordLength))
            sb.append(" ")
        }

        return sb.toString()
    }

    /**
     * Generates a random string containing multiple ASCII words separated by spaces or null
     *
     * @since 1.0.0
     * @param maxNumberOfWords maximum number of words to generate
     * @param maxWordLength maximum length of each word
     * @return random string containing ASCII words separated by spaces or null
     */
    fun asciiWordsOrNull(maxNumberOfWords: Int, maxWordLength: Int): String?
    {
        return if (bool()) asciiWords(maxNumberOfWords, maxWordLength) else null
    }
}