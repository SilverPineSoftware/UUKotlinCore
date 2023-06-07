package com.silverpine.uu.core

import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import com.silverpine.uu.logging.UULog

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
            // Eat it
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
            // Eat it
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
            // Eat it
        }
    }

    fun remove(block: ()->Unit)
    {
        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                val hasCallback = handler.hasCallbacks(block)
                UULog.d(javaClass, "remove", "BEFORE remove, hasCallback: $hasCallback")
            }

            handler.removeCallbacks(block)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                val hasCallback = handler.hasCallbacks(block)
                UULog.d(javaClass, "remove", "AFTER remove, hasCallback: $hasCallback")
            }
        }
        catch (ex: Exception)
        {
            // Eat it
        }
    }

    fun remove(runnable: Runnable)
    {
        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                val hasCallback = handler.hasCallbacks(runnable)
                UULog.d(javaClass, "remove", "BEFORE remove, hasRunnable: $hasCallback")
            }

            handler.removeCallbacks(runnable)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                val hasCallback = handler.hasCallbacks(runnable)
                UULog.d(javaClass, "remove", "AFTER remove, hasRunnable: $hasCallback")
            }
        }
        catch (ex: Exception)
        {
            // Eat it
        }
    }
}
