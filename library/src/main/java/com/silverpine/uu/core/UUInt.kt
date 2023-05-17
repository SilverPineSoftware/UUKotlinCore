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