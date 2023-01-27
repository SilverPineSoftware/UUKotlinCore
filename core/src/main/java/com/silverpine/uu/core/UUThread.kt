package com.silverpine.uu.core

import android.os.Handler
import android.os.Looper
import com.silverpine.uu.logging.UULog

/**
 * Some handy helpers for dealing with threads and loopers
 */
object UUThread
{
    /**
     * Checks to see if the currently running thread is the main thread or not
     *
     * @return true if the main thread, false otherwise
     */
    val isMainThread: Boolean
        get() = (Looper.myLooper() == Looper.getMainLooper())

    /**
     * Safely runs a block of code on the main thread.
     *
     * @param block the block to run
     */
    fun runOnMainThread(block: ()->Unit)
    {
        try
        {
            if (isMainThread)
            {
                try
                {
                    block.invoke()
                }
                catch (ex: Exception)
                {
                    UULog.d(javaClass, "runOnMainThread.invoke", "", ex)
                }
            }
            else
            {
                Handler(Looper.getMainLooper()).post { runOnMainThread(block) }
            }
        }
        catch (ex: Exception)
        {
            UULog.d(javaClass, "runOnMainThread", "", ex)
        }
    }

    /**
     * Safely runs a block of code on a background thread.
     *
     * @param block the block to run
     */
    fun runOnBackgroundThread(block: ()->Unit)
    {
        try
        {
            val t: Thread = object : Thread()
            {
                override fun run()
                {
                    try
                    {
                        block.invoke()
                    }
                    catch (ex: Exception)
                    {
                        UULog.d(javaClass, "runOnBackgroundThread.invoke", "", ex)
                    }
                }
            }

            t.start()
        }
        catch (ex: Exception)
        {
            UULog.d(javaClass, "runOnBackgroundThread", "", ex)
        }
    }

    fun safeSleep(fromWhere: String, millis: Long)
    {
        try
        {
            UULog.d(javaClass,
                "safeSleep",
                fromWhere + ", currentState: " + Thread.currentThread().state + ", isMainThread: " + isMainThread
            )

            Thread.sleep(millis)
        }
        catch (ex: Exception)
        {
            UULog.d(javaClass, "safeSleep", "", ex)
        }
    }
}