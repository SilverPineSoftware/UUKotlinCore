package com.silverpine.uu.core

/**
 * Converts a number into an 8-bit BCD value (0x00..0x99).
 * Returns null if the number is outside 0–99.
 */
fun Number.uuToBcd8(): Number?
{
    val n = this.toInt()

    if (n < 0 || n > 99)
    {
        return null
    }

    val highNibble = n / 10
    val lowNibble = n % 10

    return (highNibble shl 4) or lowNibble
}

/**
 * Converts an 8-bit BCD value (stored in this Number) into a normal integer.
 * Assumes the Number is 0..255.
 */
fun Number.uuFromBcd8(): Number
{
    val n = this.toInt() and 0xFF
    val highNibble = (n and 0xF0) shr 4
    val lowNibble = n and 0x0F
    return highNibble * 10 + lowNibble
}