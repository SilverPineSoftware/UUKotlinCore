package com.silverpine.uu.core

import android.util.Base64
import com.silverpine.uu.logging.UULog
import java.nio.charset.Charset
import java.util.*

fun String?.uuSafeString(): String
{
    if (this == null)
    {
        return ""
    }

    return this
}

fun String?.uuIsEmpty(): Boolean
{
    return this?.isEmpty() ?: true
}

fun String?.uuIsNotEmpty(): Boolean
{
    return this?.isNotEmpty() ?: false
}

/**
 * Converts a hex string into a byte array
 *
 * @return a byte array or null if the string has invalid hex characters
 */
fun String?.uuToHexData(): ByteArray?
{
    val data = this ?: return null

    var buffer: ByteArray? = null

    try
    {
        var workingData: String = data
        if (data.length % 2 != 0)
        {
            workingData = "0$data"
        }

        val bufferLength = workingData.length / 2
        buffer = ByteArray(bufferLength)

        for (i in 0 until bufferLength)
        {
            buffer[i] = workingData.substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
    }
    catch (ex: NumberFormatException)
    {
        buffer = null
    }

    return buffer
}

/**
 * Safely creates a ByteArray from a string
 *
 * @param encoding the character encoding to use
 * @return a ByteArray or null if an error occurs
 */
fun String.uuToByteArray(encoding: Charset): ByteArray?
{
    return try
    {
        toByteArray(encoding)
    }
    catch (ex: Exception)
    {
        UULog.d(javaClass, "uuToByteArray", "", ex)
        null
    }
}

/**
 * Safely creates a UTF8 byte array
 *
 * @return a byte[] or null if an error occurs
 */
fun String.uuUtf8ByteArray(): ByteArray?
{
    return uuToByteArray(Charsets.UTF_8)
}

/**
 * Safely creates a ASCII byte array
 *
 * @return a byte[] or null if an error occurs
 */
fun String.uuAsciiByteArray(): ByteArray?
{
    return uuToByteArray(Charsets.US_ASCII)
}



/**
 * Safelye decodes a base 64 into its byte[] representation
 *
 * @param base64Options Base64 flags to encode with (defaults to Base64.NO_WRAP)
 * @return a byte[] or null if an exception is caught
 */
fun String.uuToBase64Bytes(base64Options: Int = Base64.NO_WRAP): ByteArray?
{
    return try
    {
        Base64.decode(this, base64Options)
    }
    catch (ex: Exception)
    {
        null
    }
}

fun String.uuDecodeFromBase64ToUtf8String(base64options: Int = Base64.NO_WRAP): String
{
    return uuFromBase64ToString(base64options, Charsets.UTF_8)
}

fun String.uuFromBase64ToString(base64options: Int, encoding: Charset): String
{
    val base64 = uuToBase64Bytes(base64options) ?: return ""
    return base64.uuString(encoding)
}

/**
 * Capitalizes the first letter of a string
 *
 * @return A string with the first letter capitalized, or the string itself if the length is
 * less than zero or null.
 */
fun String.uuFirstCapital(): String
{
    return if (uuIsNotEmpty() && length > 1)
    {
        substring(0, 1).uppercase(Locale.getDefault()) + substring(1)
    }
    else
    {
        this
    }
}

/**
 * Converts a string to snake_case
 *
 * @param string a string
 *
 * @return another string
 */
fun String.uuToSnakeCase(string: String): String
{
    val regex = "(\\p{Ll})(\\p{Lu})"
    val replacement = "$1_$2"
    return string.replace(regex.toRegex(), replacement).lowercase(Locale.getDefault())
}

/**
 * Safely truncates a string to a given length
 *
 * @return another string
 */
fun String.uuTruncate(length: Int): String
{
    return if (this.length > length)
    {
        this.substring(0, length)
    }
    else
    {
        this
    }
}

/**
 * Checks to see if all characters in a string are a numerical digit
 *
 * @return true or false
 */
fun String.uuIsDigits(): Boolean
{
    for (c in toCharArray())
    {
        if (!Character.isDigit(c))
        {
            return false
        }
    }

    return true
}

/**
 * Checks to see if all characters in a string are alphabetic
 *
 * @return true or false
 */
val String.uuIsAlphabeticOnly: Boolean
    get() = matches(Regex("^[a-zA-Z]*\$"))


/**
 * Checks to see if all characters in a string are alphabetic or numeric
 *
 * @return true or false
 */
val String.uuIsAlphanumericOnly: Boolean
    get() = matches(Regex("^[a-zA-Z\\d]*\$"))


/**
 * Applies a simple transform to each element in an array
 *
 * @param input the input array
 * @param transformMethod the transform method
 * @return the output array
 */
fun Array<String>.uuTransform(transform: (String)->String): Array<String>
{
    var result: ArrayList<String> = arrayListOf()

    forEach()
    {
        result.add(transform.invoke(it))
    }

    return result.toTypedArray()
}

val String.uuWordCount: Int
    get() = this.trim().split("\\s+".toRegex()).size

val String.uuFileExtension: String
    get()
    {
        return split(".").lastOrNull()?.lowercase() ?: ""
    }