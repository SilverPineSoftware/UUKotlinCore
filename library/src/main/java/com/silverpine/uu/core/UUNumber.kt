package com.silverpine.uu.core

import android.content.res.Resources
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

fun Int.uuEpochTimeToSystemTime(): Long
{
    return this * 1000L
}

// Convert px to dp
val Int.uuDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

//Convert dp to px
val Int.uuPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

/**
 * Evaluates two values to determine if one contains bits from the other
 *
 * @since 1.0.0
 * @param mask the mask of bits to check
 * @return true if the bits are found in the value, false otherwise
 */
fun Int.uuIsBitSet(mask: Int): Boolean
{
    return this and mask == mask
}

/**
 * Returns a copy of this [Byte] with the bit at [index] set or cleared.
 *
 * If [index] is outside the valid range (0 until [Byte.SIZE_BITS]), the original value is returned unchanged.
 *
 * @since 1.0.0
 * @param to Pass `true` to set the bit (1), or `false` to clear it (0).
 * @param index The bit position to modify (0 until [Byte.SIZE_BITS]).
 * @return A new [Byte] with the specified bit modified, or the original value if [index] is out of bounds.
 *
 * @sample 0.toByte().uuSetBit(true, 0) // returns 1
 */
fun Byte.uuSetBit(to: Boolean, index: Int): Byte
{
    if (index !in 0 until Byte.SIZE_BITS) return this
    val mask = (1 shl index).toByte()
    return if (to) (this or mask) else (this and mask.inv())
}

/**
 * Returns a copy of this [UByte] with the bit at [index] set or cleared.
 *
 * If [index] is outside the valid range (0 until [UByte.SIZE_BITS]), the original value is returned unchanged.
 *
 * @since 1.0.0
 * @param to Pass `true` to set the bit (1), or `false` to clear it (0).
 * @param index The bit position to modify (0 until [UByte.SIZE_BITS]).
 * @return A new [UByte] with the specified bit modified, or the original value if [index] is out of bounds.
 *
 * @sample 0u.toUByte().uuSetBit(true, 7) // returns 0x80u
 */
fun UByte.uuSetBit(to: Boolean, index: Int): UByte
{
    if (index !in 0 until UByte.SIZE_BITS) return this
    val mask = (1u shl index).toUByte()
    return if (to) (this or mask) else (this and mask.inv())
}

/**
 * Returns a copy of this [Short] with the bit at [index] set or cleared.
 *
 * If [index] is outside the valid range (0 until [Short.SIZE_BITS]), the original value is returned unchanged.
 *
 * @since 1.0.0
 * @param to Pass `true` to set the bit (1), or `false` to clear it (0).
 * @param index The bit position to modify (0 until [Short.SIZE_BITS]).
 * @return A new [Short] with the specified bit modified, or the original value if [index] is out of bounds.
 *
 * @sample 0.toShort().uuSetBit(true, 15) // returns Short.MIN_VALUE
 */
fun Short.uuSetBit(to: Boolean, index: Int): Short
{
    if (index !in 0 until Short.SIZE_BITS) return this
    val mask = (1 shl index).toShort()
    return if (to) (this or mask) else (this and mask.inv())
}

/**
 * Returns a copy of this [UShort] with the bit at [index] set or cleared.
 *
 * If [index] is outside the valid range (0 until [UShort.SIZE_BITS]), the original value is returned unchanged.
 *
 * @since 1.0.0
 * @param to Pass `true` to set the bit (1), or `false` to clear it (0).
 * @param index The bit position to modify (0 until [UShort.SIZE_BITS]).
 * @return A new [UShort] with the specified bit modified, or the original value if [index] is out of bounds.
 *
 * @sample 0u.toUShort().uuSetBit(true, 15) // returns 0x8000u
 */
fun UShort.uuSetBit(to: Boolean, index: Int): UShort
{
    if (index !in 0 until UShort.SIZE_BITS) return this
    val mask = (1u shl index).toUShort()
    return if (to) (this or mask) else (this and mask.inv())
}

/**
 * Returns a copy of this [Long] with the bit at [index] set or cleared.
 *
 * If [index] is outside the valid range (0 until [Long.SIZE_BITS]), the original value is returned unchanged.
 *
 * @since 1.0.0
 * @param to Pass `true` to set the bit (1), or `false` to clear it (0).
 * @param index The bit position to modify (0 until [Long.SIZE_BITS]).
 * @return A new [Long] with the specified bit modified, or the original value if [index] is out of bounds.
 *
 * @sample 0L.uuSetBit(true, 63) // returns Long.MIN_VALUE
 */
fun Long.uuSetBit(to: Boolean, index: Int): Long
{
    if (index !in 0 until Long.SIZE_BITS) return this
    val mask = 1L shl index
    return if (to) (this or mask) else (this and mask.inv())
}

/**
 * Returns a copy of this [ULong] with the bit at [index] set or cleared.
 *
 * If [index] is outside the valid range (0 until [ULong.SIZE_BITS]), the original value is returned unchanged.
 *
 * @since 1.0.0
 * @param to Pass `true` to set the bit (1), or `false` to clear it (0).
 * @param index The bit position to modify (0 until [ULong.SIZE_BITS]).
 * @return A new [ULong] with the specified bit modified, or the original value if [index] is out of bounds.
 *
 * @sample 0uL.uuSetBit(true, 63) // returns 0x8000000000000000uL
 */
fun ULong.uuSetBit(to: Boolean, index: Int): ULong
{
    if (index !in 0 until ULong.SIZE_BITS) return this
    val mask = 1uL shl index
    return if (to) (this or mask) else (this and mask.inv())
}

/**
 * Returns a copy of this integer with the bit at [index] set or cleared.
 *
 * @since 1.0.0
 * @param index The bit position to modify (0 until [Int.SIZE_BITS]).
 * @param to Pass `true` to set the bit (1), or `false` to clear it (0).
 * @return A new integer with that bit modified, or the original value if [index] is out of bounds.
 */
fun Int.uuSetBit(to: Boolean, index: Int): Int
{
    if (index !in 0 until Int.SIZE_BITS)
    {
        return this
    }

    val mask = 1 shl index
    return if (to)
    {
        this or mask   // set bit
    }
    else
    {
        this and mask.inv() // clear bit
    }
}

/**
 * Returns a copy of this unsigned integer with the bit at [index] set or cleared.
 *
 * @since 1.0.0
 * @param index The bit position to modify (0 until [UInt.SIZE_BITS]).
 * @param to Pass `true` to set the bit (1), or `false` to clear it (0).
 * @return A new [UInt] with that bit modified, or the original value if [index] is out of bounds.
 */
fun UInt.uuSetBit(to: Boolean, index: Int): UInt
{
    if (index !in 0 until UInt.SIZE_BITS)
    {
        return this
    }

    val mask = 1u shl index
    return if (to)
    {
        this or mask   // set bit
    }
    else
    {
        this and mask.inv() // clear bit
    }
}

/**
 * Returns a copy of this Byte with the bit at the specified [index] cleared (set to 0).
 *
 * @since 1.0.0
 * @param index The bit position to clear, in 0 until [Byte.SIZE_BITS].
 * @return A new Byte with the specified bit cleared, or the original value if index is out of bounds.
 */
fun Byte.uuClearBit(index: Int): Byte
{
    if (index !in 0 until Byte.SIZE_BITS)
    {
        return this
    }

    val mask = (1 shl index).inv()
    return (this.toInt() and mask).toByte()
}

/**
 * Returns a copy of this UByte with the bit at the specified [index] cleared (set to 0).
 *
 * @since 1.0.0
 * @param index The bit position to clear, in 0 until [UByte.SIZE_BITS].
 * @return A new UByte with the specified bit cleared, or the original value if index is out of bounds.
 */
fun UByte.uuClearBit(index: Int): UByte
{
    if (index !in 0 until UByte.SIZE_BITS)
    {
        return this
    }

    val mask = (1u shl index).toUByte().inv()
    return (this and mask).toUByte()
}

/**
 * Returns a copy of this Short with the bit at the specified [index] cleared (set to 0).
 *
 * @since 1.0.0
 * @param index The bit position to clear, in 0 until [Short.SIZE_BITS].
 * @return A new Short with the specified bit cleared, or the original value if index is out of bounds.
 */
fun Short.uuClearBit(index: Int): Short
{
    if (index !in 0 until Short.SIZE_BITS)
    {
        return this
    }

    val mask = (1 shl index).inv()
    return (this.toInt() and mask).toShort()
}

/**
 * Returns a copy of this UShort with the bit at the specified [index] cleared (set to 0).
 *
 * @since 1.0.0
 * @param index The bit position to clear, in 0 until [UShort.SIZE_BITS].
 * @return A new UShort with the specified bit cleared, or the original value if index is out of bounds.
 */
fun UShort.uuClearBit(index: Int): UShort
{
    if (index !in 0 until UShort.SIZE_BITS)
    {
        return this
    }

    val mask = (1u shl index).toUShort().inv()
    return (this and mask).toUShort()
}

/**
 * Returns a copy of this Int with the bit at the specified [index] cleared (set to 0).
 *
 * @since 1.0.0
 * @param index The bit position to clear, in 0 until [Int.SIZE_BITS].
 * @return A new Int with the specified bit cleared, or the original value if index is out of bounds.
 */
fun Int.uuClearBit(index: Int): Int
{
    if (index !in 0 until Int.SIZE_BITS)
    {
        return this
    }

    val mask = (1 shl index).inv()
    return this and mask
}

/**
 * Returns a copy of this UInt with the bit at the specified [index] cleared (set to 0).
 *
 * @since 1.0.0
 * @param index The bit position to clear, in 0 until [UInt.SIZE_BITS].
 * @return A new UInt with the specified bit cleared, or the original value if index is out of bounds.
 */
fun UInt.uuClearBit(index: Int): UInt
{
    if (index !in 0 until UInt.SIZE_BITS)
    {
        return this
    }

    val mask = (1u shl index).inv()
    return this and mask
}

/**
 * Returns a copy of this Long with the bit at the specified [index] cleared (set to 0).
 *
 * @since 1.0.0
 * @param index The bit position to clear, in 0 until [Long.SIZE_BITS].
 * @return A new Long with the specified bit cleared, or the original value if index is out of bounds.
 */
fun Long.uuClearBit(index: Int): Long
{
    if (index !in 0 until Long.SIZE_BITS)
    {
        return this
    }

    val mask = (1L shl index).inv()
    return this and mask
}

/**
 * Returns a copy of this ULong with the bit at the specified [index] cleared (set to 0).
 *
 * @since 1.0.0
 * @param index The bit position to clear, in 0 until [ULong.SIZE_BITS].
 * @return A new ULong with the specified bit cleared, or the original value if index is out of bounds.
 */
fun ULong.uuClearBit(index: Int): ULong
{
    if (index !in 0 until ULong.SIZE_BITS)
    {
        return this
    }

    val mask = (1uL shl index).inv()
    return this and mask
}

fun Int.uuToBcd8(): Int?
{
    if (this < 0 || this > 99)
    {
        return null
    }

    val highNibble = this / 10
    val lowNibble = this % 10

    return (highNibble shl 4) or lowNibble
}

fun Int.uuFromBcd8(): Int
{
    val n = this.toInt() and 0xFF
    val highNibble = (n and 0xF0) shr 4
    val lowNibble = n and 0x0F
    return highNibble * 10 + lowNibble
}

/**
 * Determines whether the integer value represents a leap year.
 *
 * Leap years occur according to the following rules:
 * 1. Years divisible by 4 are leap years.
 * 2. However, years divisible by 100 are **not** leap years.
 * 3. Exception: years divisible by 400 **are** leap years.
 *
 * Examples:
 * ```
 * 2000.isLeapYear // true  (divisible by 400)
 * 1900.isLeapYear // false (divisible by 100 but not 400)
 * 2024.isLeapYear // true  (divisible by 4, not by 100)
 * 2025.isLeapYear // false (not divisible by 4)
 * ```
 *
 * This property is typically used on year values (e.g., `Calendar.getInstance().get(Calendar.YEAR)`).
 *
 * @since 1.0.0
 */
val Int.uuIsLeapYear: Boolean
    get() = when {
        this % 400 == 0 -> true
        this % 100 == 0 -> false
        else -> this % 4 == 0
    }