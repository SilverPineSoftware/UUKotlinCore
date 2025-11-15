package com.silverpine.uu.core

import com.silverpine.uu.logging.UULog
import com.silverpine.uu.logging.logException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.util.Base64
import java.util.Locale

private const val LOG_TAG = "UUByteArray"

/**
 * Converts this [ByteArray] into a hexadecimal string representation.
 *
 * Each byte is formatted as a two-digit hexadecimal value (zero-padded if necessary)
 * and concatenated in order. The result is returned in uppercase using the
 * current default [Locale].
 *
 * Example:
 * ```
 * val bytes = byteArrayOf(0x0F, 0xA0.toByte(), 0x3C)
 * val hex = bytes.uuToHexString()
 * println(hex) // "0FA03C"
 * ```
 *
 * @receiver the [ByteArray] to convert.
 *
 * @since 1.0.0
 * @return a [String] containing the uppercase hexadecimal representation of the bytes.
 */
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
 * Attempts to decode this [ByteArray] into a [String] using the given [Charset].
 *
 * This function wraps the decoding process in a [Result], so that callers can
 * safely handle both success and failure cases without throwing exceptions.
 * If decoding succeeds, the resulting [String] is returned as a [Result.success].
 * If an exception is thrown (for example, due to an unsupported charset or
 * invalid byte sequence), the error is logged and a [Result.failure] is returned.
 *
 * Example:
 * ```
 * val bytes = "Hello".toByteArray(Charsets.UTF_8)
 * val result = bytes.uuString(Charsets.UTF_8)
 *
 * result.onSuccess { str ->
 *     println("Decoded string: $str") // "Hello"
 * }.onFailure { err ->
 *     println("Decoding failed: ${err.message}")
 * }
 * ```
 *
 * @receiver the [ByteArray] to decode.
 *
 * @since 1.0.0
 * @param encoding the [Charset] to use when decoding.
 * @return a [Result] containing the decoded [String] on success,
 *         or the thrown [Exception] on failure.
 */
fun ByteArray.uuString(encoding: Charset): Result<String>
{
    return try
    {
        Result.success(String(this, encoding))
    }
    catch (ex: Exception)
    {
        UULog.logException(LOG_TAG, "uuString" ,ex)
        Result.failure(ex)
    }
}

/**
 * Attempts to decode this [ByteArray] into a UTF-8 [String].
 *
 * This is a convenience wrapper around [uuString] that always uses
 * [Charsets.UTF_8] as the decoding charset. The result is wrapped in a [Result],
 * so callers can handle both success and failure cases explicitly.
 *
 * Example:
 * ```
 * val bytes = byteArrayOf(72, 101, 108, 108, 111) // "Hello"
 * val result = bytes.uuUtf8()
 *
 * result.onSuccess { str ->
 *     println(str) // "Hello"
 * }.onFailure { err ->
 *     println("Decoding failed: ${err.message}")
 * }
 * ```
 *
 * @receiver the [ByteArray] to decode as UTF-8.
 *
 * @since 1.0.0
 * @return a [Result] containing the decoded [String] on success,
 *         or the thrown [Exception] on failure.
 */
fun ByteArray.uuUtf8(): Result<String>
{
    return uuString(Charsets.UTF_8)
}

/**
 * Attempts to decode this [ByteArray] into a US-ASCII [String].
 *
 * This is a convenience wrapper around [uuString] that always uses
 * [Charsets.US_ASCII] as the decoding charset. The result is wrapped
 * in a [Result], allowing callers to handle both success and failure
 * without exceptions being thrown.
 *
 * Example:
 * ```
 * val bytes = byteArrayOf(72, 105) // "Hi"
 * val result = bytes.uuAscii()
 *
 * result.onSuccess { str ->
 *     println(str) // "Hi"
 * }.onFailure { err ->
 *     println("Decoding failed: ${err.message}")
 * }
 * ```
 *
 * @receiver the [ByteArray] to decode as US-ASCII.
 *
 * @since 1.0.0
 * @return a [Result] containing the decoded [String] on success,
 *         or the thrown [Exception] on failure.
 */
fun ByteArray.uuAscii(): Result<String>
{
    return uuString(Charsets.US_ASCII)
}

/**
 * Encodes this [ByteArray] into a Base64-encoded [String] using the
 * provided [Base64.Encoder].
 *
 * By default, this method uses the standard Java [Base64.getEncoder],
 * which produces a padded, non-wrapped Base64 string. Callers may supply
 * alternative encoders such as [Base64.getUrlEncoder] or
 * [Base64.getMimeEncoder] to adjust behavior (e.g., URL-safe encoding or
 * MIME-style line wrapping).
 *
 * The operation is wrapped in a [Result], allowing callers to safely
 * handle both success and failure cases. On success, the encoded string
 * is returned inside [Result.success]. If an exception occurs during
 * encoding, it is logged and returned in [Result.failure].
 *
 * Example:
 * ```
 * val data = "Hello".toByteArray()
 *
 * // Standard Base64
 * val result = data.uuBase64()
 * println(result.getOrNull()) // "SGVsbG8="
 *
 * // URL-safe Base64
 * val urlSafeResult = data.uuBase64(Base64.getUrlEncoder())
 * println(urlSafeResult.getOrNull()) // "SGVsbG8="
 * ```
 *
 * @receiver the [ByteArray] to encode as Base64.
 *
 * @since 1.0.0
 * @param encoder the [Base64.Encoder] to use for encoding. Defaults to
 *                [Base64.getEncoder].
 * @return a [Result] containing the Base64-encoded [String] on success,
 *         or the thrown [Exception] on failure.
 */
fun ByteArray.uuBase64(encoder: Base64.Encoder = Base64.getEncoder()): Result<String>
{
    return try
    {
        return Result.success(encoder.encodeToString(this))
    }
    catch (ex: Exception)
    {
        UULog.logException(LOG_TAG, "uuBase64" ,ex)
        Result.failure(ex)
    }
}

/**
 * Extracts a set of bytes from a byte array
 *
 * @since 1.0.0
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

/**
 * Reads an unsigned 8-bit integer ([UByte]) from this [ByteArray] at the specified [index].
 *
 * Internally, this wraps the [ByteArray] in a [ByteBuffer] and retrieves the
 * byte at the given position, masking it with [UINT8_MASK] to ensure the value
 * is treated as unsigned before converting to [UByte].
 *
 * The result is wrapped in a [Result], so callers can safely handle both
 * success and failure cases:
 * - On success, [Result.success] contains the decoded [UByte].
 * - On failure (e.g. if [index] is out of bounds), [Result.failure] contains
 *   the thrown [Exception].
 *
 * Example:
 * ```
 * val data = byteArrayOf(0x01, 0xFF.toByte())
 *
 * val value1 = data.uuReadUInt8(0)
 * println(value1.getOrNull()) // 1u
 *
 * val value2 = data.uuReadUInt8(1)
 * println(value2.getOrNull()) // 255u
 *
 * val outOfBounds = data.uuReadUInt8(5)
 * println(outOfBounds.isFailure) // true
 * ```
 *
 * @receiver the [ByteArray] to read from.
 *
 * @since 1.0.0
 * @param index the position of the byte to read.
 * @return a [Result] containing the [UByte] if successful, or an [Exception] if an error occurs.
 */
fun ByteArray.uuReadUInt8(index: Int): Result<UByte>
{
    return try
    {
        val bb = ByteBuffer.wrap(this)
        Result.success((bb[index].toInt() and UINT8_MASK).toUByte())
    }
    catch (ex: Exception)
    {
        Result.failure(ex)
    }
}

/**
 * Reads an unsigned 16-bit integer from this [ByteArray] at the specified [index],
 * using the given byte [order].
 *
 * Internally, the [ByteArray] is wrapped in a [ByteBuffer] with its order set to [order].
 * The method then reads 2 bytes starting at [index] via [ByteBuffer.getShort] and
 * converts the result into a [UShort], ensuring it is always treated as unsigned.
 *
 * The result is wrapped in a [Result], so callers can safely handle both success
 * and failure:
 * - On success, [Result.success] contains the decoded unsigned 16-bit value as a [UShort].
 * - On failure (e.g., if [index] is out of bounds or fewer than 2 bytes remain),
 *   [Result.failure] contains the thrown [Exception].
 *
 * Example:
 * ```
 * val data = byteArrayOf(0x01, 0x02)
 *
 * val be = data.uuReadUInt16(ByteOrder.BIG_ENDIAN, 0)
 * println(be.getOrNull()) // 0x0102u = 258u
 *
 * val le = data.uuReadUInt16(ByteOrder.LITTLE_ENDIAN, 0)
 * println(le.getOrNull()) // 0x0201u = 513u
 * ```
 *
 * @receiver the [ByteArray] to read from.
 *
 * @since 1.0.0
 * @param order the [ByteOrder] to use when interpreting the 2 bytes.
 * @param index the starting position in the [ByteArray].
 * @return a [Result] containing the unsigned 16-bit integer as a [UShort] on success,
 *         or an [Exception] if reading fails.
 */
fun ByteArray.uuReadUInt16(order: ByteOrder, index: Int): Result<UShort>
{
    return try
    {
        val bb = ByteBuffer.wrap(this).order(order)
        Result.success(bb.getShort(index).toUShort())
    }
    catch (ex: Exception)
    {
        Result.failure(ex)
    }
}

/**
 * Reads an unsigned 24-bit integer from this [ByteArray] at the specified [index],
 * using the provided byte [order].
 *
 * This method composes the result from three consecutive bytes starting at [index].
 * Each byte is read using [uuReadUInt8], and failures (e.g., out-of-bounds access)
 * are immediately returned as [Result.failure].
 *
 * - When [order] is [ByteOrder.LITTLE_ENDIAN], the first byte is the least significant
 *   and the third byte is the most significant.
 * - When [order] is [ByteOrder.BIG_ENDIAN], the third byte is treated as the least
 *   significant and the first byte as the most significant.
 *
 * The combined result is returned as an [Int], since Kotlin does not have a native
 * unsigned 24-bit type. The value will always be in the range `0..0xFFFFFF`.
 *
 * Example:
 * ```
 * val data = byteArrayOf(0x01, 0x02, 0x03)
 *
 * val little = data.uuReadUInt24(ByteOrder.LITTLE_ENDIAN, 0)
 * println(little.getOrNull()) // 0x030201 = 197121
 *
 * val big = data.uuReadUInt24(ByteOrder.BIG_ENDIAN, 0)
 * println(big.getOrNull())    // 0x010203 = 66051
 *
 * val invalid = data.uuReadUInt24(ByteOrder.BIG_ENDIAN, 5)
 * println(invalid.isFailure)  // true
 * ```
 *
 * @receiver the [ByteArray] to read from.
 *
 * @since 1.0.0
 * @param order the [ByteOrder] (little or big endian).
 * @param index the starting position of the three-byte sequence.
 * @return a [Result] containing the unsigned 24-bit integer as an [Int] on success,
 *         or an [Exception] if reading fails.
 */
fun ByteArray.uuReadUInt24(order: ByteOrder, index: Int): Result<UInt>
{
    val byteOne = uuReadUInt8(index).getOrElse { ex ->
        return Result.failure(ex)
    }.toUInt()

    val byteTwo = uuReadUInt8(index + 1).getOrElse { ex ->
        return Result.failure(ex)
    }.toUInt()

    val byteThree = uuReadUInt8(index + 2).getOrElse { ex ->
        return Result.failure(ex)
    }.toUInt()

    return if (order == ByteOrder.LITTLE_ENDIAN)
    {
        Result.success(byteOne or
                ((byteTwo shl 8) and 0xFF00u) or
                ((byteThree shl 16) and 0xFF0000u))
    }
    else
    {
        Result.success(byteThree or
                ((byteTwo shl 8) and 0xFF00u) or
                ((byteOne shl 16) and 0xFF0000u))
    }
}

/**
 * Reads an unsigned 32-bit integer from this [ByteArray] at the specified [index],
 * using the given byte [order].
 *
 * Internally, the [ByteArray] is wrapped in a [ByteBuffer] with its order set to [order].
 * The method then reads 4 bytes starting at [index] via [ByteBuffer.getInt] and converts
 * the result into a [UInt], ensuring it is always treated as unsigned.
 *
 * The result is wrapped in a [Result], allowing safe handling of both success and failure:
 * - On success, [Result.success] contains the decoded unsigned 32-bit value as a [UInt].
 * - On failure (e.g., if [index] is out of bounds or fewer than 4 bytes remain),
 *   [Result.failure] contains the thrown [Exception].
 *
 * Example:
 * ```
 * val data = byteArrayOf(0x01, 0x02, 0x03, 0x04)
 *
 * val be = data.uuReadUInt32(ByteOrder.BIG_ENDIAN, 0)
 * println(be.getOrNull()) // 0x01020304u = 16909060u
 *
 * val le = data.uuReadUInt32(ByteOrder.LITTLE_ENDIAN, 0)
 * println(le.getOrNull()) // 0x04030201u = 67305985u
 * ```
 *
 * @receiver the [ByteArray] to read from.
 *
 * @since 1.0.0
 * @param order the [ByteOrder] to use when interpreting the 4 bytes.
 * @param index the starting position in the [ByteArray].
 * @return a [Result] containing the unsigned 32-bit integer as a [UInt] on success,
 *         or an [Exception] if reading fails.
 */
fun ByteArray.uuReadUInt32(order: ByteOrder, index: Int): Result<UInt>
{
    return try
    {
        val bb = ByteBuffer.wrap(this).order(order)
        Result.success(bb.getInt(index).toUInt())
    }
    catch (ex: Exception)
    {
        Result.failure(ex)
    }
}

/**
 * Reads an unsigned 64-bit integer from this [ByteArray] at the specified [index],
 * using the given byte [order].
 *
 * Internally, the [ByteArray] is wrapped in a [ByteBuffer] with its order set to [order].
 * The method reads 8 bytes starting at [index] via [ByteBuffer.getLong] and converts
 * the result into a [ULong], ensuring the value is always treated as unsigned.
 *
 * The result is wrapped in a [Result], so callers can safely handle both success
 * and failure:
 * - On success, [Result.success] contains the decoded unsigned 64-bit value as a [ULong].
 * - On failure (e.g., if [index] is out of bounds or fewer than 8 bytes remain),
 *   [Result.failure] contains the thrown [Exception].
 *
 * Example:
 * ```
 * val data = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08)
 *
 * // Big-endian: 0x0102030405060708
 * val be = data.uuReadUInt64(ByteOrder.BIG_ENDIAN, 0)
 * println(be.getOrNull()) // 72623859790382856u
 *
 * // Little-endian: 0x0807060504030201
 * val le = data.uuReadUInt64(ByteOrder.LITTLE_ENDIAN, 0)
 * println(le.getOrNull()) // 578437695752307201u
 *
 * // Out of bounds
 * val invalid = data.uuReadUInt64(ByteOrder.BIG_ENDIAN, 2)
 * println(invalid.isFailure) // true
 * ```
 *
 * @receiver the [ByteArray] to read from.
 *
 * @since 1.0.0
 * @param order the [ByteOrder] to use when interpreting the bytes.
 * @param index the starting position in the [ByteArray].
 * @return a [Result] containing the unsigned 64-bit value as a [ULong] on success,
 *         or an [Exception] if reading fails.
 */
fun ByteArray.uuReadUInt64(order: ByteOrder, index: Int): Result<ULong>
{
    return try
    {
        val bb = ByteBuffer.wrap(this).order(order)
        Result.success(bb.getLong(index).toULong())
    }
    catch (ex: Exception)
    {
        Result.failure(ex)
    }
}

/**
 * Reads a signed 8-bit integer from this [ByteArray] at the specified [index].
 *
 * Internally, the [ByteArray] is wrapped in a [ByteBuffer] and the single byte at
 * [index] is accessed directly. The result is returned as a signed [Byte].
 *
 * The value is wrapped in a [Result] to safely capture both success and failure:
 * - On success, [Result.success] contains the decoded [Byte].
 * - On failure (e.g., if [index] is out of bounds), [Result.failure] contains
 *   the thrown [Exception].
 *
 * Example:
 * ```
 * val data = byteArrayOf(0x7F, 0x80.toByte())
 *
 * val first = data.uuReadInt8(0)
 * println(first.getOrNull()) // 127
 *
 * val second = data.uuReadInt8(1)
 * println(second.getOrNull()) // -128
 *
 * val invalid = data.uuReadInt8(5)
 * println(invalid.isFailure) // true
 * ```
 *
 * @receiver the [ByteArray] to read from.
 *
 * @since 1.0.0
 * @param index the position in the [ByteArray] to read.
 * @return a [Result] containing the signed 8-bit integer as a [Byte] on success,
 *         or an [Exception] if reading fails.
 */
fun ByteArray.uuReadInt8(index: Int): Result<Byte>
{
    return try
    {
        val bb = ByteBuffer.wrap(this)
        Result.success(bb[index])
    }
    catch (ex: Exception)
    {
        Result.failure(ex)
    }
}

/**
 * Reads a signed 16-bit integer ([Short]) from this [ByteArray] at the specified [index],
 * using the provided byte [order].
 *
 * Internally, the [ByteArray] is wrapped in a [ByteBuffer] with its byte order set to [order],
 * and [ByteBuffer.getShort] is used to extract two bytes starting at [index].
 *
 * The result is wrapped in a [Result], so callers can safely handle both success and failure:
 * - On success, [Result.success] contains the decoded [Short].
 * - On failure (e.g., if [index] is out of bounds or insufficient bytes remain),
 *   [Result.failure] contains the thrown [Exception].
 *
 * Example:
 * ```
 * val data = byteArrayOf(0x01, 0x02)
 *
 * // Read as big endian
 * val be = data.uuReadInt16(ByteOrder.BIG_ENDIAN, 0)
 * println(be.getOrNull()) // 0x0102 = 258
 *
 * // Read as little endian
 * val le = data.uuReadInt16(ByteOrder.LITTLE_ENDIAN, 0)
 * println(le.getOrNull()) // 0x0201 = 513
 *
 * // Invalid index
 * val invalid = data.uuReadInt16(ByteOrder.BIG_ENDIAN, 5)
 * println(invalid.isFailure) // true
 * ```
 *
 * @receiver the [ByteArray] to read from.
 *
 * @since 1.0.0
 * @param order the [ByteOrder] to use when interpreting the two bytes.
 * @param index the starting position in the [ByteArray].
 * @return a [Result] containing the [Short] if successful, or an [Exception] if an error occurs.
 */
fun ByteArray.uuReadInt16(order: ByteOrder, index: Int): Result<Short>
{
    return try
    {
        val bb = ByteBuffer.wrap(this).order(order)
        Result.success(bb.getShort(index))
    }
    catch (ex: Exception)
    {
        Result.failure(ex)
    }
}

/**
 * Reads a signed 32-bit integer from this [ByteArray] at the specified [index],
 * using the provided byte [order].
 *
 * Internally, the [ByteArray] is wrapped in a [ByteBuffer] with its order set to [order].
 * The method then reads 4 bytes starting at [index] using [ByteBuffer.getInt],
 * interpreting them as a signed [Int].
 *
 * The result is wrapped in a [Result] to provide safe error handling:
 * - On success, [Result.success] contains the decoded signed 32-bit value.
 * - On failure (e.g., if [index] is out of bounds or fewer than 4 bytes remain),
 *   [Result.failure] contains the thrown [Exception].
 *
 * Example:
 * ```
 * val data = byteArrayOf(0x01, 0x02, 0x03, 0x04)
 *
 * val be = data.uuReadInt32(ByteOrder.BIG_ENDIAN, 0)
 * println(be.getOrNull()) // 16909060 (0x01020304)
 *
 * val le = data.uuReadInt32(ByteOrder.LITTLE_ENDIAN, 0)
 * println(le.getOrNull()) // 67305985 (0x04030201)
 *
 * val invalid = data.uuReadInt32(ByteOrder.BIG_ENDIAN, 2)
 * println(invalid.isFailure) // true
 * ```
 *
 * @receiver the [ByteArray] to read from.
 *
 * @since 1.0.0
 * @param order the [ByteOrder] to use when interpreting the 4 bytes.
 * @param index the starting position in the [ByteArray].
 * @return a [Result] containing the signed 32-bit integer as an [Int] on success,
 *         or an [Exception] if reading fails.
 */
fun ByteArray.uuReadInt32(order: ByteOrder, index: Int): Result<Int>
{
    return try
    {
        val bb = ByteBuffer.wrap(this).order(order)
        Result.success(bb.getInt(index))
    }
    catch (ex: Exception)
    {
        Result.failure(ex)
    }
}

/**
 * Reads a signed 64-bit integer from this [ByteArray] at the specified [index],
 * using the provided byte [order].
 *
 * Internally, the [ByteArray] is wrapped in a [ByteBuffer] with its order set to [order].
 * The method then reads 8 bytes starting at [index] using [ByteBuffer.getLong],
 * interpreting them as a signed [Long].
 *
 * The result is wrapped in a [Result] to safely capture both success and failure:
 * - On success, [Result.success] contains the decoded signed 64-bit value.
 * - On failure (e.g., if [index] is out of bounds or fewer than 8 bytes remain),
 *   [Result.failure] contains the thrown [Exception].
 *
 * Example:
 * ```
 * val data = byteArrayOf(
 *     0x01, 0x02, 0x03, 0x04,
 *     0x05, 0x06, 0x07, 0x08
 * )
 *
 * val be = data.uuReadInt64(ByteOrder.BIG_ENDIAN, 0)
 * println(be.getOrNull()) // 72623859790382856 (0x0102030405060708)
 *
 * val le = data.uuReadInt64(ByteOrder.LITTLE_ENDIAN, 0)
 * println(le.getOrNull()) // 578437695752307201 (0x0807060504030201)
 *
 * val invalid = data.uuReadInt64(ByteOrder.BIG_ENDIAN, 2)
 * println(invalid.isFailure) // true
 * ```
 *
 * @receiver the [ByteArray] to read from.
 *
 * @since 1.0.0
 * @param order the [ByteOrder] to use when interpreting the 8 bytes.
 * @param index the starting position in the [ByteArray].
 * @return a [Result] containing the signed 64-bit integer as a [Long] on success,
 *         or an [Exception] if reading fails.
 */
fun ByteArray.uuReadInt64(order: ByteOrder, index: Int): Result<Long>
{
    return try
    {
        val bb = ByteBuffer.wrap(this).order(order)
        Result.success(bb.getLong(index))
    }
    catch (ex: Exception)
    {
        Result.failure(ex)
    }
}

/**
 * Writes an unsigned 8-bit integer ([UByte]) into this [ByteArray] at the specified [index].
 *
 * The method stores [value] at [index] after converting it to a signed [Byte].
 * On success, the method returns the number of bytes written (always [UByte.SIZE_BYTES]).
 *
 * The operation is wrapped in a [Result] to safely handle errors:
 * - On success, [Result.success] contains the number of bytes written (1).
 * - On failure (e.g., if [index] is out of bounds), [Result.failure] contains
 *   the thrown [Exception].
 *
 * Example:
 * ```
 * val data = ByteArray(2)
 *
 * val result = data.uuWriteUInt8(0, 0x7Fu)
 * println(result.getOrNull()) // 1
 * println(data.joinToString()) // "127, 0"
 *
 * val invalid = data.uuWriteUInt8(5, 0xFFu)
 * println(invalid.isFailure) // true
 * ```
 *
 * @receiver the [ByteArray] to write into.
 *
 * @since 1.0.0
 * @param index the position in the [ByteArray] where the value should be written.
 * @param value the [UByte] value to store.
 * @return a [Result] containing the number of bytes written (1) on success,
 *         or an [Exception] if writing fails.
 */
fun ByteArray.uuWriteUInt8(index: Int, value: UByte): Result<Int>
{
    return try
    {
        this[index] = value.toByte()
        Result.success(UByte.SIZE_BYTES)
    }
    catch (ex: Exception)
    {
        Result.failure(ex)
    }
}

/**
 * Writes an unsigned 16-bit integer ([UShort]) into this [ByteArray] at the specified [index],
 * using the given byte [order].
 *
 * Internally, the [ByteArray] is wrapped in a [ByteBuffer] with its byte order set to [order].
 * The [value] is converted to a signed [Short] (since [ByteBuffer] does not support unsigned
 * types directly) and written starting at [index].
 *
 * The result is wrapped in a [Result] to provide safe error handling:
 * - On success, [Result.success] contains the number of bytes written (always [UShort.SIZE_BYTES]).
 * - On failure (e.g., if [index] is out of bounds or fewer than 2 bytes remain),
 *   [Result.failure] contains the thrown [Exception].
 *
 * Example:
 * ```
 * val data = ByteArray(4)
 *
 * // Write in big-endian order
 * val beResult = data.uuWriteUInt16(ByteOrder.BIG_ENDIAN, 0, 0x1234u)
 * println(beResult.getOrNull()) // 2
 * println(data.joinToString { String.format("%02X", it) }) // "12, 34, 00, 00"
 *
 * // Write in little-endian order
 * val leResult = data.uuWriteUInt16(ByteOrder.LITTLE_ENDIAN, 2, 0xABCDu)
 * println(leResult.getOrNull()) // 2
 * println(data.joinToString { String.format("%02X", it) }) // "12, 34, CD, AB"
 *
 * // Out-of-bounds write
 * val invalid = data.uuWriteUInt16(ByteOrder.BIG_ENDIAN, 5, 0xFFFFu)
 * println(invalid.isFailure) // true
 * ```
 *
 * @receiver the [ByteArray] to write into.
 *
 * @since 1.0.0
 * @param order the [ByteOrder] (big-endian or little-endian) used to write the 2 bytes.
 * @param index the starting position in the [ByteArray].
 * @param value the [UShort] value to write.
 * @return a [Result] containing the number of bytes written (2) on success,
 *         or an [Exception] if writing fails.
 */
fun ByteArray.uuWriteUInt16(order: ByteOrder, index: Int, value: UShort): Result<Int>
{
    return try
    {
        val bb = ByteBuffer.wrap(this).order(order)
        bb.putShort(index, value.toShort())
        Result.success(UShort.SIZE_BYTES)
    }
    catch (ex: Exception)
    {
        Result.failure(ex)
    }
}

/**
 * Writes an unsigned 32-bit integer ([UInt]) into this [ByteArray] at the specified [index],
 * using the given byte [order].
 *
 * Internally, the [ByteArray] is wrapped in a [ByteBuffer] with its byte order set to [order].
 * Since [ByteBuffer] does not support unsigned types directly, the [value] is converted
 * to a signed [Int] and written starting at [index].
 *
 * The result is wrapped in a [Result] to provide safe error handling:
 * - On success, [Result.success] contains the number of bytes written (always [Integer.BYTES], i.e. 4).
 * - On failure (e.g., if [index] is out of bounds or fewer than 4 bytes remain),
 *   [Result.failure] contains the thrown [Exception].
 *
 * Example:
 * ```
 * val data = ByteArray(8)
 *
 * // Write in big-endian order
 * val beResult = data.uuWriteUInt32(ByteOrder.BIG_ENDIAN, 0, 0x12345678u)
 * println(beResult.getOrNull()) // 4
 * println(data.joinToString { String.format("%02X", it) })
 * // "12, 34, 56, 78, 00, 00, 00, 00"
 *
 * // Write in little-endian order
 * val leResult = data.uuWriteUInt32(ByteOrder.LITTLE_ENDIAN, 4, 0x90ABCDu)
 * println(leResult.getOrNull()) // 4
 * println(data.joinToString { String.format("%02X", it) })
 * // "12, 34, 56, 78, CD, AB, 90, 00"
 *
 * // Attempting out-of-bounds write
 * val invalid = data.uuWriteUInt32(ByteOrder.BIG_ENDIAN, 6, 0xFFFFFFFFu)
 * println(invalid.isFailure) // true
 * ```
 *
 * @receiver the [ByteArray] to write into.
 *
 * @since 1.0.0
 * @param order the [ByteOrder] (big-endian or little-endian) used to write the 4 bytes.
 * @param index the starting position in the [ByteArray].
 * @param value the [UInt] value to write.
 * @return a [Result] containing the number of bytes written (4) on success,
 *         or an [Exception] if writing fails.
 */
fun ByteArray.uuWriteUInt32(order: ByteOrder, index: Int, value: UInt): Result<Int>
{
    return try
    {
        val bb = ByteBuffer.wrap(this).order(order)
        bb.putInt(index, value.toInt())
        Result.success(UInt.SIZE_BYTES)
    }
    catch (ex: Exception)
    {
        Result.failure(ex)
    }
}

/**
 * Writes an unsigned 64-bit integer ([ULong]) into this [ByteArray] at the specified [index],
 * using the given byte [order].
 *
 * Internally, the [ByteArray] is wrapped in a [ByteBuffer] with its byte order set to [order].
 * Since [ByteBuffer] does not support unsigned types directly, the [value] is converted
 * to a signed [Long] and written starting at [index].
 *
 * The result is wrapped in a [Result] to safely handle errors:
 * - On success, [Result.success] contains the number of bytes written
 *   (always [Long.SIZE_BYTES], i.e., 8).
 * - On failure (e.g., if [index] is out of bounds or fewer than 8 bytes remain),
 *   [Result.failure] contains the thrown [Exception].
 *
 * Example:
 * ```
 * val data = ByteArray(16)
 *
 * // Write in big-endian order
 * val beResult = data.uuWriteUInt64(ByteOrder.BIG_ENDIAN, 0, 0x1122334455667788uL)
 * println(beResult.getOrNull()) // 8
 * println(data.joinToString { String.format("%02X", it) })
 * // "11, 22, 33, 44, 55, 66, 77, 88, 00, 00, 00, 00, 00, 00, 00, 00"
 *
 * // Write in little-endian order
 * val leResult = data.uuWriteUInt64(ByteOrder.LITTLE_ENDIAN, 8, 0xAABBCCDDEEFF0011uL)
 * println(leResult.getOrNull()) // 8
 * println(data.joinToString { String.format("%02X", it) })
 * // "11, 22, 33, 44, 55, 66, 77, 88, 11, 00, FF, EE, DD, CC, BB, AA"
 *
 * // Attempting out-of-bounds write
 * val invalid = data.uuWriteUInt64(ByteOrder.BIG_ENDIAN, 12, 0xFFFFFFFFFFFFFFFFuL)
 * println(invalid.isFailure) // true
 * ```
 *
 * @receiver the [ByteArray] to write into.
 *
 * @since 1.0.0
 * @param order the [ByteOrder] (big-endian or little-endian) used to write the 8 bytes.
 * @param index the starting position in the [ByteArray].
 * @param value the [ULong] value to write.
 * @return a [Result] containing the number of bytes written (8) on success,
 *         or an [Exception] if writing fails.
 */
fun ByteArray.uuWriteUInt64(order: ByteOrder, index: Int, value: ULong): Result<Int>
{
    return try
    {
        val bb = ByteBuffer.wrap(this).order(order)
        bb.putLong(index, value.toLong())
        Result.success(ULong.SIZE_BYTES)
    }
    catch (ex: Exception)
    {
        Result.failure(ex)
    }
}

/**
 * Writes a signed 8-bit integer ([Byte]) into this [ByteArray] at the specified [index].
 *
 * The [value] is written directly into the [ByteArray] at the given position.
 *
 * The result is wrapped in a [Result] to provide safe error handling:
 * - On success, [Result.success] contains the number of bytes written
 *   (always [Byte.SIZE_BYTES], i.e., 1).
 * - On failure (e.g., if [index] is out of bounds), [Result.failure] contains
 *   the thrown [Exception].
 *
 * Example:
 * ```
 * val data = ByteArray(2)
 *
 * // Successful write
 * val result = data.uuWriteInt8(0, 42)
 * println(result.getOrNull()) // 1
 * println(data.joinToString()) // "42, 0"
 *
 * // Out-of-bounds write
 * val invalid = data.uuWriteInt8(5, -10)
 * println(invalid.isFailure) // true
 * ```
 *
 * @receiver the [ByteArray] to write into.
 *
 * @since 1.0.0
 * @param index the position in the [ByteArray] where the value should be written.
 * @param value the signed 8-bit integer ([Byte]) to write.
 * @return a [Result] containing the number of bytes written (1) on success,
 *         or an [Exception] if writing fails.
 */
fun ByteArray.uuWriteInt8(index: Int, value: Byte): Result<Int>
{
    return try
    {
        this[index] = value
        Result.success(Byte.SIZE_BYTES)
    }
    catch (ex: Exception)
    {
        Result.failure(ex)
    }
}

/**
 * Writes a signed 16-bit integer ([Short]) into this [ByteArray] at the specified [index],
 * using the given byte [order].
 *
 * Internally, this method wraps the [ByteArray] in a [ByteBuffer] and sets its byte order
 * to [order] (either [ByteOrder.BIG_ENDIAN] or [ByteOrder.LITTLE_ENDIAN]).
 * The [value] is then written starting at the given [index].
 *
 * The result is wrapped in a [Result] to provide safe error handling:
 * - On success, [Result.success] contains the number of bytes written
 *   (always [Short.SIZE_BYTES], i.e., 2).
 * - On failure (e.g., if [index] is out of bounds or the buffer does not have
 *   enough remaining space), [Result.failure] contains the thrown [Exception].
 *
 * Example:
 * ```
 * val data = ByteArray(4)
 *
 * // Write in big-endian order
 * val beResult = data.uuWriteInt16(ByteOrder.BIG_ENDIAN, 0, 0x1234.toShort())
 * println(beResult.getOrNull()) // 2
 * println(data.joinToString { String.format("%02X", it) })
 * // "12, 34, 00, 00"
 *
 * // Write in little-endian order
 * val leResult = data.uuWriteInt16(ByteOrder.LITTLE_ENDIAN, 2, 0x5678.toShort())
 * println(leResult.getOrNull()) // 2
 * println(data.joinToString { String.format("%02X", it) })
 * // "12, 34, 78, 56"
 *
 * // Attempting out-of-bounds write
 * val invalid = data.uuWriteInt16(ByteOrder.BIG_ENDIAN, 3, 0x9999.toShort())
 * println(invalid.isFailure) // true
 * ```
 *
 * @receiver the [ByteArray] to write into.
 *
 * @since 1.0.0
 * @param order the [ByteOrder] (big-endian or little-endian) used to write the 2 bytes.
 * @param index the starting position in the [ByteArray].
 * @param value the signed 16-bit integer ([Short]) to write.
 * @return a [Result] containing the number of bytes written (2) on success,
 *         or an [Exception] if writing fails.
 */
fun ByteArray.uuWriteInt16(order: ByteOrder, index: Int, value: Short): Result<Int>
{
    return try
    {
        val bb = ByteBuffer.wrap(this).order(order)
        bb.putShort(index, value)
        Result.success(Short.SIZE_BYTES)
    }
    catch (ex: Exception)
    {
        Result.failure(ex)
    }
}

/**
 * Writes a signed 32-bit integer ([Int]) into this [ByteArray] at the specified [index],
 * using the given byte [order].
 *
 * Internally, this method wraps the [ByteArray] in a [ByteBuffer] and sets its byte order
 * to [order] (either [ByteOrder.BIG_ENDIAN] or [ByteOrder.LITTLE_ENDIAN]).
 * The [value] is then written starting at the given [index].
 *
 * The result is wrapped in a [Result] to provide safe error handling:
 * - On success, [Result.success] contains the number of bytes written
 *   (always [Integer.BYTES], i.e., 4).
 * - On failure (e.g., if [index] is out of bounds or the buffer does not have
 *   enough remaining space), [Result.failure] contains the thrown [Exception].
 *
 * Example:
 * ```
 * val data = ByteArray(8)
 *
 * // Write in big-endian order
 * val beResult = data.uuWriteInt32(ByteOrder.BIG_ENDIAN, 0, 0x12345678)
 * println(beResult.getOrNull()) // 4
 * println(data.joinToString { String.format("%02X", it) })
 * // "12, 34, 56, 78, 00, 00, 00, 00"
 *
 * // Write in little-endian order
 * val leResult = data.uuWriteInt32(ByteOrder.LITTLE_ENDIAN, 4, 0x90ABCDEF.toInt())
 * println(leResult.getOrNull()) // 4
 * println(data.joinToString { String.format("%02X", it) })
 * // "12, 34, 56, 78, EF, CD, AB, 90"
 *
 * // Attempting out-of-bounds write
 * val invalid = data.uuWriteInt32(ByteOrder.BIG_ENDIAN, 6, 42)
 * println(invalid.isFailure) // true
 * ```
 *
 * @receiver the [ByteArray] to write into.
 *
 * @since 1.0.0
 * @param order the [ByteOrder] (big-endian or little-endian) used to write the 4 bytes.
 * @param index the starting position in the [ByteArray].
 * @param value the signed 32-bit integer ([Int]) to write.
 * @return a [Result] containing the number of bytes written (4) on success,
 *         or an [Exception] if writing fails.
 */
fun ByteArray.uuWriteInt32(order: ByteOrder, index: Int, value: Int): Result<Int>
{
    return try
    {
        val bb = ByteBuffer.wrap(this).order(order)
        bb.putInt(index, value)
        Result.success(Integer.BYTES)
    }
    catch (ex: Exception)
    {
        Result.failure(ex)
    }
}

/**
 * Writes a signed 64-bit integer ([Long]) into this [ByteArray] at the specified [index],
 * using the given byte [order].
 *
 * Internally, this method wraps the [ByteArray] in a [ByteBuffer] and sets its byte order
 * to [order] (either [ByteOrder.BIG_ENDIAN] or [ByteOrder.LITTLE_ENDIAN]).
 * The [value] is then written starting at the given [index].
 *
 * The result is wrapped in a [Result] to provide safe error handling:
 * - On success, [Result.success] contains the number of bytes written
 *   (always [Long.SIZE_BYTES], i.e., 8).
 * - On failure (e.g., if [index] is out of bounds or the buffer does not have
 *   enough remaining space), [Result.failure] contains the thrown [Exception].
 *
 * Example:
 * ```
 * val data = ByteArray(16)
 *
 * // Write in big-endian order
 * val beResult = data.uuWriteInt64(ByteOrder.BIG_ENDIAN, 0, 0x1122334455667788L)
 * println(beResult.getOrNull()) // 8
 * println(data.joinToString { String.format("%02X", it) })
 * // "11, 22, 33, 44, 55, 66, 77, 88, 00, 00, 00, 00, 00, 00, 00, 00"
 *
 * // Write in little-endian order
 * val leResult = data.uuWriteInt64(ByteOrder.LITTLE_ENDIAN, 8, 0xAABBCCDDEEFF0011L)
 * println(leResult.getOrNull()) // 8
 * println(data.joinToString { String.format("%02X", it) })
 * // "11, 22, 33, 44, 55, 66, 77, 88, 11, 00, FF, EE, DD, CC, BB, AA"
 *
 * // Attempting out-of-bounds write
 * val invalid = data.uuWriteInt64(ByteOrder.BIG_ENDIAN, 12, Long.MAX_VALUE)
 * println(invalid.isFailure) // true
 * ```
 *
 * @receiver the [ByteArray] to write into.
 *
 * @since 1.0.0
 * @param order the [ByteOrder] (big-endian or little-endian) used to write the 8 bytes.
 * @param index the starting position in the [ByteArray].
 * @param value the signed 64-bit integer ([Long]) to write.
 * @return a [Result] containing the number of bytes written (8) on success,
 *         or an [Exception] if writing fails.
 */
fun ByteArray.uuWriteInt64(order: ByteOrder, index: Int, value: Long): Result<Int>
{
    return try
    {
        val bb = ByteBuffer.wrap(this).order(order)
        bb.putLong(index, value)
        Result.success(Long.SIZE_BYTES)
    }
    catch (ex: Exception)
    {
        Result.failure(ex)
    }
}

/**
 * Writes the contents of the given [value] [ByteArray] into this [ByteArray]
 * starting at the specified [index].
 *
 * Internally, this method delegates to [System.arraycopy] to copy all elements
 * from [value] into the target array. The number of bytes copied equals
 * `value.size`.
 *
 * The result is wrapped in a [Result] to provide safe error handling:
 * - On success, [Result.success] contains the number of bytes written
 *   (the size of [value]).
 * - On failure (e.g., if [index] is negative, out of bounds, or there is not
 *   enough space in the target array), [Result.failure] contains the thrown [Exception].
 *
 * Example:
 * ```
 * val buffer = ByteArray(10)
 * val data = byteArrayOf(0x01, 0x02, 0x03)
 *
 * val result = buffer.uuWriteData(4, data)
 * println(result.getOrNull()) // 3
 * println(buffer.joinToString { String.format("%02X", it) })
 * // "00, 00, 00, 00, 01, 02, 03, 00, 00, 00"
 *
 * // Attempting out-of-bounds write
 * val invalid = buffer.uuWriteData(9, data)
 * println(invalid.isFailure) // true
 * ```
 *
 * @receiver the [ByteArray] into which [value] will be written.
 *
 * @since 1.0.0
 * @param index the starting position in this [ByteArray].
 * @param value the [ByteArray] whose contents should be written.
 * @return a [Result] containing the number of bytes written on success,
 *         or an [Exception] if the copy fails.
 */
fun ByteArray.uuWriteData(index: Int, value: ByteArray): Result<Int>
{
    return try
    {
        System.arraycopy(value, 0, this, index, value.size)
        Result.success(value.size)
    }
    catch (ex: Exception)
    {
        Result.failure(ex)
    }
}

/**
 * Writes the given [value] string into this [ByteArray] starting at the specified [index],
 * encoding it using the provided [charset].
 *
 * The string is first converted into a [ByteArray] using [String.toByteArray] with the
 * given [charset], and then written into this [ByteArray] by delegating to [uuWriteData].
 *
 * The result is wrapped in a [Result] to provide safe error handling:
 * - On success, [Result.success] contains the number of bytes written (the size of the encoded string).
 * - On failure (e.g., if [index] is out of bounds, or if the array is too small to hold the
 *   encoded string), [Result.failure] contains the thrown [Exception].
 *
 * Example:
 * ```
 * val buffer = ByteArray(20)
 *
 * val result = buffer.uuWriteString(5, "Hello", Charsets.UTF_8)
 * println(result.getOrNull()) // 5
 * println(buffer.copyOfRange(5, 10).toString(Charsets.UTF_8)) // "Hello"
 *
 * // Attempting out-of-bounds write
 * val invalid = buffer.uuWriteString(18, "World", Charsets.UTF_8)
 * println(invalid.isFailure) // true
 * ```
 *
 * @receiver the [ByteArray] into which the encoded string will be written.
 *
 * @since 1.0.0
 * @param index the starting position in this [ByteArray].
 * @param value the [String] to encode and write.
 * @param charset the [Charset] used to encode the string.
 * @return a [Result] containing the number of bytes written on success,
 *         or an [Exception] if the write fails.
 */
fun ByteArray.uuWriteString(index: Int, value: String, charset: Charset): Result<Int>
{
    val buffer = value.toByteArray(charset)
    return uuWriteData(index, buffer)
}

/**
 * Writes the given [value] string into this [ByteArray] starting at the specified [index],
 * encoding it as UTF-8.
 *
 * This is a convenience method that delegates to [uuWriteString] with [Charsets.UTF_8].
 *
 * The result is wrapped in a [Result] to provide safe error handling:
 * - On success, [Result.success] contains the number of bytes written (the length of the
 *   UTF-8 encoded string).
 * - On failure (e.g., if [index] is out of bounds or the target array is too small),
 *   [Result.failure] contains the thrown [Exception].
 *
 * Example:
 * ```
 * val buffer = ByteArray(20)
 * val result = buffer.uuWriteUtf8(2, "Hello")
 * println(result.getOrNull()) // 5
 * println(buffer.copyOfRange(2, 7).toString(Charsets.UTF_8)) // "Hello"
 *
 * // Out-of-bounds write attempt
 * val invalid = buffer.uuWriteUtf8(18, "World")
 * println(invalid.isFailure) // true
 * ```
 *
 * @receiver the [ByteArray] into which the UTF-8 encoded string will be written.
 *
 * @since 1.0.0
 * @param index the starting position in this [ByteArray].
 * @param value the [String] to encode and write in UTF-8.
 * @return a [Result] containing the number of bytes written on success,
 *         or an [Exception] if the write fails.
 */
fun ByteArray.uuWriteUtf8(index: Int, value: String): Result<Int>
{
    return uuWriteString(index, value, Charsets.UTF_8)
}

/**
 * Extracts the high nibble (the upper 4 bits) from the byte at the specified [index]
 * within this [ByteArray].
 *
 * This method first reads the unsigned 8-bit value at the given [index] using [uuReadUInt8].
 * If the read succeeds, the method isolates the high nibble by shifting the byte right by
 * 4 bits and masking with `0x0F`, then returns it as a [Byte].
 *
 * The result is wrapped in a [Result] to provide safe error handling:
 * - On success, [Result.success] contains the high nibble value (0–15).
 * - On failure (e.g., if [index] is out of bounds), [Result.failure] contains the thrown [Exception].
 *
 * Example:
 * ```
 * val data = byteArrayOf(0xAB.toByte()) // binary: 10101011
 * val result = data.uuHighNibble(0)
 * println(result.getOrNull()) // 0x0A (decimal 10)
 *
 * // Out-of-bounds example
 * val invalid = data.uuHighNibble(5)
 * println(invalid.isFailure) // true
 * ```
 *
 * @receiver the [ByteArray] to read from.
 *
 * @since 1.0.0
 * @param index the position of the byte whose high nibble is to be extracted.
 * @return a [Result] containing the high nibble (0–15) on success,
 *         or an [Exception] if the read fails.
 */
fun ByteArray.uuHighNibble(index: Int): Result<Byte>
{
    val value = uuReadUInt8(index).getOrElse { ex ->
        return Result.failure(ex)
    }.toInt()

    return Result.success(((value and 0xFF).toShort().toInt() shr 4 and 0x0F).toByte())
}

/**
 * Extracts the low nibble (the lower 4 bits) from the byte at the specified [index]
 * within this [ByteArray].
 *
 * This method first reads the unsigned 8-bit value at the given [index] using [uuReadUInt8].
 * If the read succeeds, it masks the lower 4 bits with `0x0F` and returns the result as a [Byte].
 *
 * The result is wrapped in a [Result] to provide safe error handling:
 * - On success, [Result.success] contains the low nibble value (0–15).
 * - On failure (e.g., if [index] is out of bounds), [Result.failure] contains the thrown [Exception].
 *
 * Example:
 * ```
 * val data = byteArrayOf(0xAB.toByte()) // binary: 10101011
 * val result = data.uuLowNibble(0)
 * println(result.getOrNull()) // 0x0B (decimal 11)
 *
 * // Out-of-bounds example
 * val invalid = data.uuLowNibble(5)
 * println(invalid.isFailure) // true
 * ```
 *
 * @receiver the [ByteArray] to read from.
 *
 * @since 1.0.0
 * @param index the position of the byte whose low nibble is to be extracted.
 * @return a [Result] containing the low nibble (0–15) on success,
 *         or an [Exception] if the read fails.
 */
fun ByteArray.uuLowNibble(index: Int): Result<Byte>
{
    val value = uuReadUInt8(index).getOrElse { ex ->
        return Result.failure(ex)
    }.toInt()

    return Result.success((value and 0x0F).toByte())
}

/**
 * Interprets the byte at the given [index] in this [ByteArray] as a
 * single **binary-coded decimal (BCD)** value and converts it into a decimal [Int].
 *
 * The byte is split into its high nibble (upper 4 bits) and low nibble (lower 4 bits):
 * - The high nibble represents the **tens** digit.
 * - The low nibble represents the **ones** digit.
 *
 * If both nibbles are valid decimal digits (0–9), the method returns their combined
 * decimal value (0–99). If either nibble is greater than 9, the method returns
 * a failed [Result] with an [IllegalArgumentException].
 *
 * Any out-of-bounds access when reading the byte also results in a failed [Result].
 *
 * Example:
 * ```
 * val data = byteArrayOf(0x45) // high=4, low=5
 * val result = data.uuReadBcd8(0)
 * println(result.getOrNull()) // 45
 *
 * val invalid = byteArrayOf(0xAB) // high=10, low=11 (not valid BCD)
 * println(invalid.uuReadBcd8(0).isFailure) // true
 * ```
 *
 * @receiver the [ByteArray] containing the BCD-encoded byte.
 *
 * @since 1.0.0
 * @param index the position of the byte to decode.
 * @return a [Result] containing the decimal value (0–99) on success,
 *         or an [Exception] if the nibble values are invalid or if the index is out of bounds.
 */
fun ByteArray.uuBcd8(index: Int): Result<Int>
{
    val highNibble = uuHighNibble(index).getOrElse { ex ->
        return Result.failure(ex)
    }.toInt()

    val lowNibble = uuLowNibble(index).getOrElse { ex ->
        return Result.failure(ex)
    }.toInt()

    return if (highNibble <= 9 && lowNibble <= 9)
    {
        Result.success(highNibble * 10 + lowNibble)
    }
    else
    {
        Result.failure(IllegalArgumentException("value must be between 0 and 99"))
    }
}

/**
 * Decodes a 16-bit **binary-coded decimal (BCD)** value from this [ByteArray],
 * starting at the given [index], and returns its decimal representation.
 *
 * This method leverages [uuReadBcd8] to decode **two consecutive bytes**:
 * - At `index` → the **high two digits** (thousands and hundreds).
 * - At `index + 1` → the **low two digits** (tens and ones).
 *
 * The result is formed by combining the two decoded bytes into a 4-digit integer
 * in the range **0..9999**.
 * If either byte contains invalid BCD digits (`> 9`) or if the access is out of bounds,
 * the method returns a failed [Result].
 *
 * Example:
 * ```
 * val data = byteArrayOf(0x12, 0x34) // "12" and "34"
 * val value = data.uuBcd16(0)
 * println(value.getOrNull()) // 1234
 *
 * val invalid = byteArrayOf(0x1A, 0x34) // "1A" invalid
 * println(invalid.uuBcd16(0).isFailure) // true
 * ```
 *
 * @receiver The byte array containing the BCD-encoded value.
 *
 * @since 1.0.0
 * @param index The starting position of the 2-byte BCD value.
 * @return A [Result] containing the decoded integer (0..9999) on success,
 *         or an [Exception] if decoding fails or invalid BCD digits are encountered.
 */
fun ByteArray.uuBcd16(index: Int): Result<Int>
{
    val data1 = uuBcd8(index).getOrElse { ex ->
        return Result.failure(ex)
    }

    val data2 = uuBcd8(index + 1).getOrElse { ex ->
        return Result.failure(ex)
    }

    return Result.success(data1 * 100 + data2)
}

/**
 * Decodes a 24-bit **binary-coded decimal (BCD)** value from this [ByteArray],
 * starting at the given [index], and returns its decimal representation.
 *
 * This method reads **three consecutive bytes**, each parsed with [uuBcd8]:
 * - `index` → high two digits (hundred-thousands and ten-thousands)
 * - `index + 1` → middle two digits (thousands and hundreds)
 * - `index + 2` → low two digits (tens and ones)
 *
 * The three decoded values are combined to form a 6-digit integer in the range **0..999999**.
 * If any byte contains invalid BCD digits (`> 9`) or if the access is out of bounds,
 * the method returns a failed [Result].
 *
 * Example:
 * ```
 * val data = byteArrayOf(0x12, 0x34, 0x56) // "12" "34" "56"
 * val result = data.uuBcd24(0)
 * println(result.getOrNull()) // 123456
 *
 * val invalid = byteArrayOf(0x12, 0x3A, 0x56) // "3A" invalid BCD
 * println(invalid.uuBcd24(0).isFailure) // true
 * ```
 *
 * @receiver the [ByteArray] containing the BCD-encoded value
 *
 * @since 1.0.0
 * @param index the starting position of the 3-byte BCD sequence
 * @return a [Result] containing the decoded integer (0..999999) on success,
 *         or an [Exception] if decoding fails or the digits are invalid
 */
fun ByteArray.uuBcd24(index: Int): Result<Int>
{
    val data1 = uuBcd8(index).getOrElse { ex ->
        return Result.failure(ex)
    }

    val data2 = uuBcd8(index + 1).getOrElse { ex ->
        return Result.failure(ex)
    }

    val data3 = uuBcd8(index + 2).getOrElse { ex ->
        return Result.failure(ex)
    }

    return Result.success(data1 * 10000 + data2 * 100 + data3)
}

/**
 * Decodes a 32-bit **binary-coded decimal (BCD)** value from this [ByteArray],
 * starting at the given [index], and returns its decimal representation.
 *
 * This method reads **four consecutive bytes**, grouped as two 16-bit BCD values
 * decoded with [uuBcd16]:
 * - Bytes at `index` and `index + 1` → high four digits (ten-thousands through millions place).
 * - Bytes at `index + 2` and `index + 3` → low four digits (thousands through ones place).
 *
 * The two decoded values are combined into an 8-digit integer in the range **0..99,999,999**.
 *
 * If any byte contains invalid BCD digits (`> 9`) or if the access is out of bounds,
 * the method returns a failed [Result].
 *
 * Example:
 * ```
 * val data = byteArrayOf(0x12, 0x34, 0x56, 0x78) // "1234" "5678"
 * val result = data.uuBcd32(0)
 * println(result.getOrNull()) // 12345678
 *
 * val invalid = byteArrayOf(0x12, 0x3A, 0x56, 0x78) // "3A" invalid BCD
 * println(invalid.uuBcd32(0).isFailure) // true
 * ```
 *
 * @receiver the [ByteArray] containing the BCD-encoded value
 *
 * @since 1.0.0
 * @param index the starting position of the 4-byte BCD sequence
 * @return a [Result] containing the decoded integer (0..99,999,999) on success,
 *         or an [Exception] if decoding fails or the digits are invalid
 */
fun ByteArray.uuBcd32(index: Int): Result<Int>
{
    val data1 = uuBcd16(index).getOrElse { ex ->
        return Result.failure(ex)
    }

    val data2 = uuBcd16(index + 2).getOrElse { ex ->
        return Result.failure(ex)
    }

    return Result.success(data1 * 10000 + data2)
}

/**
 * Securely resets the contents of this [ByteArray] by overwriting
 * every element with zero (`0`).
 *
 * Use this to clear sensitive material (e.g., cryptographic keys or
 * passwords) from memory once it is no longer needed. This helps
 * reduce the chance of secrets leaking if memory is later reused or
 * inspected (e.g., via heap dumps).
 *
 * ⚠️ Note: On the JVM/Android, memory management is handled by the
 * garbage collector and the JIT compiler may make optimizations.
 * While `uuReset()` will overwrite the array in place, it cannot
 * guarantee that:
 *  - the JVM has not already copied the array elsewhere, or
 *  - the compiler/runtime won’t optimize away the overwrite.
 *
 * For most applications this is still considered a best-effort
 * practice and is commonly used in security-sensitive code.
 *
 * Example:
 * ```
 * val key = ByteArray(32) { it.toByte() }
 * // ... use key for encryption ...
 * key.uuReset() // key now contains only zeros
 * ```
 *
 * @since 1.0.0
 */
fun ByteArray.uuReset()
{
    uuSetAll(0)
}

/**
 * Overwrites every element of this [ByteArray] with the given [value].
 *
 * Useful for clearing sensitive material (e.g., cryptographic keys or passwords)
 * once it is no longer needed.
 *
 * Example:
 * ```
 * val key = ByteArray(32) { it.toByte() }
 * // ... use key ...
 * key.uuSetAll(0xFF.toByte()) // all bytes set to 0xFF
 * ```
 *
 * @since 1.0.0
 */
fun ByteArray.uuSetAll(value: Byte)
{
    fill(value)
}

/**
 * Returns a new [ByteArray] whose length is padded with `0x00` bytes
 * up to the next multiple of [blockSize].
 *
 * If this array's length is already a multiple of [blockSize],
 * the original contents are copied and returned unchanged.
 *
 * Example:
 * ```
 * val data = byteArrayOf(1, 2, 3)
 * val padded = data.uuPadded(toBlockSize = 4)
 * // padded.size == 4, padded == [1, 2, 3, 0]
 * ```
 *
 * @since 1.0.0
 * @param blockSize The block size to pad to (must be > 0).
 * @return A new [ByteArray] whose length is a multiple of [blockSize].
 */
fun ByteArray.uuPadded(blockSize: Int): ByteArray
{
    if (blockSize <= 0)
    {
        return copyOf()
    }

    val remainder = size % blockSize
    if (remainder == 0)
    {
        return copyOf()
    }

    val padCount = blockSize - remainder
    return copyOf(size + padCount) // pads with zeros
}

/**
 * Returns a new [ByteArray] where each byte is the XOR of the
 * corresponding bytes in this array and [other].
 *
 * Both arrays must have the same length. If lengths differ,
 * the original array is copied and returned unchanged.
 *
 * Example:
 * ```
 * val a = byteArrayOf(0x0F, 0xF0.toByte())
 * val b = byteArrayOf(0xFF.toByte(), 0x0F)
 * val result = a.uuXor(b)
 * // result == [0xF0.toByte(), 0xFF.toByte()]
 * ```
 *
 * @since 1.0.0
 * @param other The [ByteArray] to XOR against.
 * @return A new [ByteArray] containing the XOR result, or a copy of
 *         this array if lengths differ.
 */
fun ByteArray.uuXor(other: ByteArray): ByteArray
{
    if (size != other.size)
    {
        return copyOf()
    }

    val out = ByteArray(size)
    for (i in indices)
    {
        out[i] = (this[i].toInt() xor other[i].toInt()).toByte()
    }

    return out
}

/**
 * Splits this [ByteArray] into a list of chunks, each up to [chunkSize] bytes.
 *
 * @since 1.0.0
 * @param chunkSize the maximum size of each chunk (must be > 0).
 * @return a list of [ByteArray] chunks. The last chunk may be shorter if not divisible.
 */
fun ByteArray.uuSplitIntoChunks(chunkSize: Int): List<ByteArray>
{
    if (chunkSize <= 0)
    {
        return listOf()
    }

    val chunks = mutableListOf<ByteArray>()
    var index = 0

    while (index < this.size)
    {
        val end = minOf(index + chunkSize, this.size)
        val chunk = this.copyOfRange(index, end)
        chunks.add(chunk)
        index += chunkSize
    }

    return chunks
}