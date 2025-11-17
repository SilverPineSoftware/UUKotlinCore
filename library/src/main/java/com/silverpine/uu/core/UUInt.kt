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
    return (this and mask) == mask
}