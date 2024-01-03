package com.silverpine.uu.core

import android.os.Looper
import com.silverpine.uu.logging.UULog

/**
 * Checks to see if the currently running thread is the main thread or not
 *
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
        UULog.d(Thread::class.java, "uuSleep", "", ex)
    }
}