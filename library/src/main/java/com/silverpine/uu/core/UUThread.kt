package com.silverpine.uu.core

import android.os.Handler
import android.os.Looper
import com.silverpine.uu.logging.UULog

/**
 * Some handy helpers for dealing with threads and loopers
 */
object UUThread
{
    private val workerThread = UUWorkerThread("UUThread")

    /**
     * Checks to see if the currently running thread is the main thread or not
     *
     * @return true if the main thread, false otherwise
     */
    val isMain: Boolean
        get() = (Looper.myLooper() == Looper.getMainLooper())

    /**
     * Safely runs a block of code on the main thread.
     *
     * @param delay the amount of delay before running the block
     * @param block the block to run
     *
     */
    fun main(delay: Long, block: ()->Unit)
    {
        workerThread.postDelayed(delay)
        {
            Handler(Looper.getMainLooper()).post(block)
        }
    }

    /**
     * Safely runs a block of code on the main thread.
     *
     * @param block the block to run
     *
     * Runs immediately
     */
    fun main(block: ()->Unit)
    {
        main(0L, block)
    }

    /**
     * Safely runs a block of code on a background thread.
     *
     * @param block the block to run
     * * @param delay the amount of delay before running the block
     */
    fun background(delay: Long, block: ()->Unit)
    {
        workerThread.postDelayed(delay, block)
    }

    /**
     * Safely runs a block of code on a background thread.
     *
     * @param block the block to run
     *
     * Runs immediately
     */
    fun background(block: ()->Unit)
    {
        background(0L, block)
    }

    fun safeSleep(fromWhere: String, millis: Long)
    {
        try
        {
            UULog.d(javaClass,
                "safeSleep",
                fromWhere + ", currentState: " + Thread.currentThread().state + ", isMainThread: " + isMain
            )

            Thread.sleep(millis)
        }
        catch (ex: Exception)
        {
            UULog.d(javaClass, "safeSleep", "", ex)
        }
    }
}