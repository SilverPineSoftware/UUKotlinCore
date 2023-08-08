package com.silverpine.uu.core

import android.os.Looper

/**
 * Checks to see if the currently running thread is the main thread or not
 *
 * @return true if the main thread, false otherwise
 */
fun uuIsMainThread(): Boolean
{
    return (Looper.myLooper() == Looper.getMainLooper())
}
fun uuSleep(fromWhere: String, millis: Long)
{
    try
    {
        Thread.sleep(millis)
    }
    catch (_: Exception)
    {
        // Eat it
    }
}