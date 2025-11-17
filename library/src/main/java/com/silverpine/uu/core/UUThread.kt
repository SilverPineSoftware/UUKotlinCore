package com.silverpine.uu.core

import android.os.Looper
import com.silverpine.uu.logging.UULog
import com.silverpine.uu.logging.logException

private const val LOG_TAG = "UUThread"

/**
 * Checks to see if the currently running thread is the main thread or not
 *
 * @since 1.0.0
 * @return true if the main thread, false otherwise
 */
fun uuIsMainThread(): Boolean
{
    return (Looper.myLooper() == Looper.getMainLooper())
}
fun uuSleep(millis: Long)
{
    try
    {
        Thread.sleep(millis)
    }
    catch (ex : Exception)
    {
        UULog.logException(LOG_TAG, "uuSleep", ex)
    }
}