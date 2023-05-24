package com.silverpine.uu.core

import com.silverpine.uu.logging.UULog
import java.io.Closeable

fun Closeable.uuSafeClose()
{
    try
    {
        close()
    }
    catch (ex: Exception)
    {
        UULog.d(javaClass, "safeClose", "", ex)
    }
}