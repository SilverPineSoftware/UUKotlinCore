package com.silverpine.uu.core

import android.util.Base64
import com.silverpine.uu.logging.UULog
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

