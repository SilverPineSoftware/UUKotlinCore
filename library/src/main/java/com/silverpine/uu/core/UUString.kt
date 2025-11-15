package com.silverpine.uu.core

import com.silverpine.uu.logging.UULog
import com.silverpine.uu.logging.logException
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.charset.Charset
import java.util.Base64
import java.util.Locale
import java.util.regex.Pattern

private const val LOG_TAG = "UUString"

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

    var buffer: ByteArray?

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
        UULog.logException(LOG_TAG, "uuToByteArray", ex)
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
 * Decodes this [String] from Base64 into a [ByteArray].
 *
 * This is a convenience extension that wraps [Base64.Decoder.decode] and returns
 * the result inside a [Result] type to simplify error handling.
 *
 * ### Behavior
 * - On success, returns a [Result.success] containing the decoded bytes.
 * - On failure (e.g. if the string is not valid Base64), returns a [Result.failure]
 *   wrapping the thrown [Exception].
 *
 * @receiver the Base64‑encoded string to decode.
 * @param decoder the [Base64.Decoder] to use for decoding. Defaults to
 * [Base64.getDecoder].
 *
 * @return a [Result] containing the decoded [ByteArray] on success, or a failure
 * if decoding fails.
 *
 * ### Example
 * ```
 * val encoded = "SGVsbG8gd29ybGQ=" // "Hello world"
 * val decoded = encoded.uuToBase64Bytes().getOrThrow()
 * println(String(decoded)) // prints "Hello world"
 * ```
 */
fun String.uuFromBase64(decoder: Base64.Decoder = Base64.getDecoder()): Result<ByteArray>
{
    return try
    {
        Result.success(decoder.decode(this))
    }
    catch (ex: Exception)
    {
        Result.failure(ex)
    }
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
fun String.uuToSnakeCase(): String
{
    val regex = "(\\p{Ll})(\\p{Lu})"
    val replacement = "$1_$2"
    return replace(regex.toRegex(), replacement).lowercase(Locale.getDefault())
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


private const val UU_HAS_NUMBER_REGEX = "^.*[0-9].*"
private const val UU_HAS_UPPER_CASE_REGEX = "^.*[A-Z].*"
private const val UU_HAS_LOWER_CASE_REGEX = "^.*[a-z].*"
private const val UU_PASSWORD_VALID_SYMBOLS_REGEX = "^.*[!@#\$%^&*()_+=[{]};:<>|./?,-].*"

fun String.uuMatchesRegex(regexPattern: String): Boolean
{
    return try
    {
        Pattern.matches(regexPattern, this)
    }
    catch (ex: Exception)
    {
        UULog.logException(LOG_TAG, "uuMatchesRegex", ex)
        false
    }
}

fun String.uuHasUppercase(): Boolean
{
    return uuMatchesRegex(UU_HAS_UPPER_CASE_REGEX)
}

fun String.uuHasLowercase(): Boolean
{
    return uuMatchesRegex(UU_HAS_LOWER_CASE_REGEX)
}

fun String.uuHasNumber(): Boolean
{
    return uuMatchesRegex(UU_HAS_NUMBER_REGEX)
}

fun String.uuHasSymbol(): Boolean
{
    return uuMatchesRegex(UU_PASSWORD_VALID_SYMBOLS_REGEX)
}

fun Throwable.uuStackTrace(): String
{
    return runCatching()
    {
        val sw = StringWriter()
        printStackTrace(PrintWriter(sw))
        return sw.toString()

    }.getOrElse()
    {
        uuSafeToString()
    }
}