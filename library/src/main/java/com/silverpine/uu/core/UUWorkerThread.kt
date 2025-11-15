package com.silverpine.uu.core

import android.os.Handler
import android.os.HandlerThread
import com.silverpine.uu.logging.UULog
import com.silverpine.uu.logging.logException

private const val LOG_TAG = "UUWorkerThread"

class UUWorkerThread(name: String): HandlerThread(name)
{
    private val handler: Handler by lazy { Handler(looper) }

    init
    {
        start()
    }

    fun post(block: ()->Unit)
    {
        try
        {
            handler.post(block)
        }
        catch (ex: Exception)
        {
            UULog.logException(LOG_TAG, "post", ex)
        }
    }

    fun postDelayed(delay: Long, block: ()->Unit)
    {
        try
        {
            handler.postDelayed(block, delay)
        }
        catch (ex: Exception)
        {
            UULog.logException(LOG_TAG, "postDelayed", ex)
        }
    }

    fun postDelayed(delay: Long, runnable: Runnable)
    {
        try
        {
            handler.postDelayed(runnable, delay)
        }
        catch (ex: Exception)
        {
            UULog.logException(LOG_TAG, "postDelayed", ex)
        }
    }

    fun remove(block: ()->Unit)
    {
        try
        {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
//            {
//                val hasCallback = handler.hasCallbacks(block)
//                //UULog.d(javaClass, "remove", "BEFORE remove, hasCallback: $hasCallback")
//            }

            handler.removeCallbacks(block)

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
//            {
//                val hasCallback = handler.hasCallbacks(block)
//                //UULog.d(javaClass, "remove", "AFTER remove, hasCallback: $hasCallback")
//            }
        }
        catch (ex: Exception)
        {
            UULog.logException(LOG_TAG, "remove", ex)
        }
    }

    fun remove(runnable: Runnable)
    {
        try
        {
            handler.removeCallbacks(runnable)
        }
        catch (ex: Exception)
        {
            UULog.logException(LOG_TAG, "remove", ex)
        }
    }
}
