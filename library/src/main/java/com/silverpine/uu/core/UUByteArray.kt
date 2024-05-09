package com.silverpine.uu.core

import android.util.Base64
import com.silverpine.uu.logging.UULog
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.util.*

fun ByteArray.uuToHex(): String
{
    val sb = StringBuilder()
    var tmp: String

    forEach()
    { byte ->

        tmp = Integer.toHexString(byte.toInt() and 0xFF)

        if (tmp.length < 2)
        {
            tmp = "0$tmp"
        }

        sb.append(tmp)
    }

    return sb.toString().uppercase(Locale.getDefault())
}

/**
 * Safely creates a string from a byte array
 *
 * @param data the byte array
 * @param encoding the encoding to use
 * @return a string or "" if an exception is caught
 */
fun ByteArray.uuString(encoding: Charset): String
{
    return try
    {
        String(this, encoding)
    }
    catch (ex: Exception)
    {
        UULog.d(javaClass, "uuString", "", ex)
        ""
    }
}

/**
 * Safely creates a UTF8 string
 *
 * @return a string or "" if an exception is caught
 */
fun ByteArray.uuUtf8(): String
{
    return uuString(Charsets.UTF_8)
}

/**
 * Safely creates an ASCII string
 *
 * @return a string or "" if an exception is caught
 */
fun ByteArray.uuAscii(): String
{
    return uuString(Charsets.US_ASCII)
}

/**
 * Safely creates a Base64 string from an array of bytes
 *
 * @param base64Options Base64 flags to encode with (defaults to Base64.NO_WRAP)
 * @return a string or null if an exception is caught
 */
fun ByteArray.uuBase64(base64Options: Int = Base64.NO_WRAP): String
{
    return try
    {
        Base64.encodeToString(this, base64Options)
    }
    catch (ex: Exception)
    {
        ""
    }
}

/**
 * Extracts a set of bytes from a byte array
 *
 * @param index starting index to copy
 * @param count number of bytes to copy
 * @return a byte array, or null if index and count are not in bounds
 */
fun ByteArray.uuSubData(index: Int, count: Int): ByteArray?
{
    if (index < 0)
    {
        return null
    }

    val dataLength = size
    val upperIndex = (index + count).coerceAtMost(dataLength)
    if (index > upperIndex)
    {
        return null
    }

    return copyOfRange(index, upperIndex)
}

private const val UINT8_MASK = 0x000000FF
private const val UINT16_MASK = 0x0000FFFF
private const val UINT24_MASK = 0x00FFFFFFL
private const val UINT32_MASK = 0xFFFFFFFFL
private const val UINT64_MASK = -0x1L

/**
 * Reads a UInt8 from a byte[] array.
 *
 * @param index the index to read from
 *
 * @throws IndexOutOfBoundsException if the index is out of bounds
 * @throws NullPointerException if data is null
 *
 * @return UInt8 value at the index
 */
fun ByteArray.uuReadUInt8(index: Int): Int
{
    val bb = ByteBuffer.wrap(this)
    return bb[index].toInt() and UINT8_MASK
}

/**
 * Reads a UInt16 from a byte[] array.
 *
 * @param order the byte order to use for reading
 * @param index the index to read from
 *
 * @throws IndexOutOfBoundsException if the index is out of bounds
 * @throws NullPointerException if data is null
 *
 * @return UInt16 value at the index
 */
fun ByteArray.uuReadUInt16(order: ByteOrder, index: Int): Int
{
    val bb = ByteBuffer.wrap(this).order(order)
    return bb.getShort(index).toInt() and UINT16_MASK
}

/**
 * Reads a UInt24 from a byte[] array.
 *
 * @param order the byte order to use for reading
 * @param index the index to read from
 *
 * @throws IndexOutOfBoundsException if the index is out of bounds
 * @throws NullPointerException if data is null
 *
 * @return UInt24 value at the index
 */
fun ByteArray.uuReadUInt24(order: ByteOrder, index: Int): Int
{
    return if (order == ByteOrder.LITTLE_ENDIAN)
    {
        uuReadUInt8(index) or
            ((uuReadUInt8(index + 1) shl 8) and 0xFF00) or
            ((uuReadUInt8(index + 2) shl 16) and 0xFF0000)
    }
    else
    {
        uuReadUInt8(index + 2) or
            ((uuReadUInt8(index + 1) shl 8) and 0xFF00) or
            ((uuReadUInt8(index + 0) shl 16) and 0xFF0000)
    }
}

/**
 * Reads a UInt32 from a byte[] array.
 *
 * @param order the byte order to use for reading
 * @param index the index to read from
 *
 * @throws IndexOutOfBoundsException if the index is out of bounds
 * @throws NullPointerException if data is null
 *
 * @return UInt32 value at the index
 */
fun ByteArray.uuReadUInt32(order: ByteOrder, index: Int): Long
{
    val bb = ByteBuffer.wrap(this).order(order)
    return bb.getInt(index).toLong() and UINT32_MASK
}

/**
 * Reads a UInt64 from a byte[] array.
 *
 * @param order ByteOrder order
 * @param index the index to read from
 *
 * @throws IndexOutOfBoundsException if the index is out of bounds
 * @throws NullPointerException if data is null
 *
 * @return UInt64 value at the index
 */
fun ByteArray.uuReadUInt64(order: ByteOrder, index: Int): Long
{
    val bb = ByteBuffer.wrap(this).order(order)
    return bb.getLong(index) and UINT64_MASK
}

/**
 * Reads a UInt8 from a byte[] array.
 *
 * @param index the index to read from
 *
 * @throws IndexOutOfBoundsException if the index is out of bounds
 * @throws NullPointerException if data is null
 *
 * @return UInt8 value at the index
 */
fun ByteArray.uuReadInt8(index: Int): Byte
{
    val bb = ByteBuffer.wrap(this)
    return bb[index]
}

/**
 * Reads a UInt16 from a byte[] array.
 *
 * @param order the byte order to use for reading
 * @param index the index to read from
 *
 * @throws IndexOutOfBoundsException if the index is out of bounds
 * @throws NullPointerException if data is null
 *
 * @return UInt16 value at the index
 */
fun ByteArray.uuReadInt16(order: ByteOrder, index: Int): Short
{
    val bb = ByteBuffer.wrap(this).order(order)
    return bb.getShort(index)
}

/**
 * Reads a Int32 from a byte[] array.
 *
 * @param order the byte order to use for reading
 * @param index the index to read from
 *
 * @throws IndexOutOfBoundsException if the index is out of bounds
 * @throws NullPointerException if data is null
 *
 * @return Int32 value at the index
 */
fun ByteArray.uuReadInt32(order: ByteOrder, index: Int): Int
{
    val bb = ByteBuffer.wrap(this).order(order)
    return bb.getInt(index)
}

/**
 * Reads a Int64 from a byte[] array.
 *
 * @param order the byte order to use for reading
 * @param index the index to read from
 *
 * @throws IndexOutOfBoundsException if the index is out of bounds
 * @throws NullPointerException if data is null
 *
 * @return Int64 value at the index
 */
fun ByteArray.uuReadInt64(order: ByteOrder, index: Int): Long
{
    val bb = ByteBuffer.wrap(this).order(order)
    return bb.getLong(index)
}

/**
 * Writes a UInt8 at the specified index
 *
 * @param index the index to write to
 * @param value the value to write
 *
 * @return the number of bytes written
 *
 * @throws IndexOutOfBoundsException if the index is out of bounds
 * @throws NullPointerException if data is null
 */
fun ByteArray.uuWriteUInt8(index: Int, value: Int): Int
{
    this[index] = (value and 0xFF).toByte()
    return Byte.SIZE_BYTES
}

/**
 * Writes a UInt16 at the specified index
 *
 * @param order the byte order to use for writing
 * @param index the index to write to
 * @param value the value to write
 *
 * @return the number of bytes written
 *
 * @throws IndexOutOfBoundsException if the index is out of bounds
 * @throws NullPointerException if data is null
 */
fun ByteArray.uuWriteUInt16(order: ByteOrder, index: Int, value: Int): Int
{
    val bb = ByteBuffer.wrap(this).order(order)
    bb.putShort(index, (value and 0xFFFF).toShort())
    return Short.SIZE_BYTES
}

/**
 * Writes a UInt32 at the specified index
 *
 * @param order the byte order to use for writing
 * @param index the index to write to
 * @param value the value to write
 *
 * @return the number of bytes written
 *
 * @throws IndexOutOfBoundsException if the index is out of bounds
 * @throws NullPointerException if data is null
 */
fun ByteArray.uuWriteUInt32(order: ByteOrder, index: Int, value: Long): Int
{
    val bb = ByteBuffer.wrap(this).order(order)
    bb.putInt(index, (value and 0xFFFFFFFFL).toInt())
    return Integer.BYTES
}

/**
 * Writes a UInt64 at the specified index
 *
 * @param order the byte order to use for writing
 * @param index the index to write to
 * @param value the value to write
 *
 * @return the number of bytes written
 *
 * @throws IndexOutOfBoundsException if the index is out of bounds
 * @throws NullPointerException if data is null
 */
fun ByteArray.uuWriteUInt64(order: ByteOrder, index: Int, value: Long): Int
{
    val bb = ByteBuffer.wrap(this).order(order)
    bb.putLong(index, (value and UINT64_MASK))
    return Long.SIZE_BYTES
}

/**
 * Writes a Int8 at the specified index
 *
 * @param index the index to write to
 * @param value the value to write
 *
 * @return the number of bytes written
 *
 * @throws IndexOutOfBoundsException if the index is out of bounds
 * @throws NullPointerException if data is null
 */
fun ByteArray.uuWriteInt8(index: Int, value: Byte): Int
{
    this[index] = value
    return Byte.SIZE_BYTES
}

/**
 * Writes a Int16 at the specified index
 *
 * @param order the byte order to use for writing
 * @param index the index to write to
 * @param value the value to write
 *
 * @return the number of bytes written
 *
 * @throws IndexOutOfBoundsException if the index is out of bounds
 * @throws NullPointerException if data is null
 */
fun ByteArray.uuWriteInt16(order: ByteOrder, index: Int, value: Short): Int
{
    val bb = ByteBuffer.wrap(this).order(order)
    bb.putShort(index, value)
    return Short.SIZE_BYTES
}

/**
 * Writes a Int32 at the specified index
 *
 * @param order the byte order to use for writing
 * @param index the index to write to
 * @param value the value to write
 *
 * @return the number of bytes written
 *
 * @throws IndexOutOfBoundsException if the index is out of bounds
 * @throws NullPointerException if data is null
 */
fun ByteArray.uuWriteInt32(order: ByteOrder, index: Int, value: Int): Int
{
    val bb = ByteBuffer.wrap(this).order(order)
    bb.putInt(index, value)
    return Integer.BYTES
}

/**
 * Writes a Int64 at the specified index
 *
 * @param order the byte order to use for writing
 * @param index the index to write to
 * @param value the value to write
 *
 * @return the number of bytes written
 *
 * @throws IndexOutOfBoundsException if the index is out of bounds
 * @throws NullPointerException if data is null
 */
fun ByteArray.uuWriteInt64(order: ByteOrder, index: Int, value: Long): Int
{
    val bb = ByteBuffer.wrap(this).order(order)
    bb.putLong(index, value)
    return Long.SIZE_BYTES
}

/**
 *
 * Writes a string to a buffer
 *
 * @param index the index to write to
 * @param value the value to write
 * @return the number of bytes written
 *
 * @throws IndexOutOfBoundsException if the index is out of bounds
 * @throws NullPointerException if data is null
 */
fun ByteArray.uuWriteData(index: Int, value: ByteArray): Int
{
    System.arraycopy(value, 0, this, index, value.size)
    return value.size
}

/**
 *
 * Writes a string to a buffer
 *
 * @param index the index to write to
 * @param value the value to write
 * @param charset the character encoding to use
 * @return the number of bytes written
 *
 * @throws IndexOutOfBoundsException if the index is out of bounds
 * @throws NullPointerException if data is null
 */
fun ByteArray.uuWriteString(index: Int, value: String, charset: Charset): Int
{
    val buffer = value.toByteArray(charset)
    return uuWriteData(index, buffer)
}

/**
 *
 * Writes a UTF8 string to a buffer
 *
 * @param index the index to write to
 * @param value the value to write
 * @return the number of bytes written
 *
 * @throws IndexOutOfBoundsException if the index is out of bounds
 * @throws NullPointerException if data is null
 */
fun ByteArray.uuWriteUtf8(index: Int, value: String): Int
{
    return uuWriteString(index, value, Charsets.UTF_8)
}

/**
 *
 * @param index the index to read from
 * @return the high order nibble (4 bit value, MSB-LSB) at the specified index
 */
fun ByteArray.uuHighNibble(index: Int): Byte
{
    val value = uuReadUInt8(index)
    return ((value and 0xFF).toShort().toInt() shr 4 and 0x0F).toByte()
}

/**
 *
 * @param index the index to read from
 * @return the low order nibble (4 bit value, MSB-LSB) at the specified index
 */
fun ByteArray.uuLowNibble(index: Int): Byte
{
    val value = uuReadUInt8(index)
    return (value and 0x0F).toByte()
}

/**
 *
 * @param index the index to read from
 * @return a single byte binary coded decimal, or -1 if parsing fails
 */
fun ByteArray.uuBcd8(index: Int): Int
{
    val highNibble = uuHighNibble(index)
    val lowNibble = uuLowNibble(index)
    return if (highNibble <= 9 && lowNibble <= 9)
    {
        highNibble * 10 + lowNibble
    } else -1
}

/**
 *
 * @param index the index to read from
 * @return a two byte binary coded decimal, or -1 if parsing fails
 */
fun ByteArray.uuBcd16(index: Int): Int
{
    val data1 = uuBcd8(index)
    val data2 = uuBcd8(index + 1)
    return if (data1 != -1 && data2 != -1)
    {
        data1 * 100 + data2
    } else -1
}

/**
 *
 * @param index the index to read from
 * @return a three byte binary coded decimal, or -1 if parsing fails
 */
fun ByteArray.uuBcd24(index: Int): Int
{
    val data1 = uuBcd8(index)
    val data2 = uuBcd8(index + 1)
    val data3 = uuBcd8(index + 2)

    return if (data1 != -1 && data2 != -1 && data3 != -1)
    {
        data1 * 10000 + data2 * 100 + data3
    } else -1
}

/**
 *
 * @param index the index to read from
 * @return a four byte binary coded decimal, or -1 if parsing fails
 */
fun ByteArray.uuBcd32(index: Int): Int
{
    val data1 = uuBcd16(index)
    val data2 = uuBcd16(index + 2)
    return if (data1 != -1 && data2 != -1) {
        data1 * 10000 + data2
    } else -1
}
