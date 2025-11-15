package com.silverpine.uu.core

import com.silverpine.uu.logging.UULog
import com.silverpine.uu.logging.logException
import java.io.Closeable

private const val LOG_TAG = "UUCloseable"

fun Closeable.uuSafeClose()
{
    try
    {
        close()
    }
    catch (ex: Exception)
    {
        UULog.logException(LOG_TAG, "uuSafeClose" ,ex)
    }
}