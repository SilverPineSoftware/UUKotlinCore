package com.silverpine.uu.core

import android.content.res.Resources

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
 * @param mask the mask of bits to check
 * @return true if the bits are found in the value, false otherwise
 */
fun Int.uuIsBitSet(mask: Int): Boolean
{
    return this and mask == mask
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
 */
val Int.uuIsLeapYear: Boolean
    get() = when {
        this % 400 == 0 -> true
        this % 100 == 0 -> false
        else -> this % 4 == 0
    }