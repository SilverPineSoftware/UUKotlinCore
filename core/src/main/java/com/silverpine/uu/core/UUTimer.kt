package com.silverpine.uu.core

import com.silverpine.uu.logging.UULog
import java.util.*

/**
 * A simple named timer that wraps Timer and TimerTask
 */
class UUTimer(
    val timerId: String,
    val interval: Long,
    val repeat: Boolean,
    val userInfo: Any?,
    private val block: (UUTimer, Any?)->Unit)
{
    private var invokeBlock: (()->Unit)? = { timerTick() }

    /**
     * Gets the last time this timer was fired
     *
     * @return a long
     */
    var lastFireTime: Long = 0
        private set

    /**
     * Starts the timer
     */
    fun start()
    {
        addTimer(this)
        safeStartTimer()
    }

    /**
     * Cancels the timer
     */
    fun cancel()
    {
        safeCancelTimer()
        invokeBlock = null
        removeTimer(this)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private fun safeStartTimer()
    {
        try
        {
            lastFireTime = System.currentTimeMillis()

            invokeBlock?.let()
            {
                workerThread.postDelayed(interval, it)
            }
        }
        catch (ex: Exception)
        {
            UULog.d(javaClass, "safeStartTimer", "", ex)
        }
    }

    private fun safeCancelTimer()
    {
        try
        {
            invokeBlock?.let()
            {
                workerThread.remove(it)
            }
        }
        catch (ex: Exception)
        {
            UULog.d(javaClass, "safeCancelTimer", "", ex)
        }
    }

    private fun handlerTimerFired()
    {
        try
        {
            block.invoke(this, userInfo)
        }
        catch (ex: Exception)
        {
            UULog.d(javaClass, "handlerTimerFired", "", ex)
        }
    }

    private fun timerTick()
    {
        try
        {
            handlerTimerFired()

            if (repeat)
            {
                safeStartTimer()
            }
            else
            {
                cancel()
            }
        }
        catch (ex: Exception)
        {
            UULog.d(javaClass, "safeInvokeRun", "", ex)
        }
    }

    companion object
    {
        private val theActiveTimers = HashMap<String, UUTimer>()
        private val workerThread = UUWorkerThread("UUTimer")

        ////////////////////////////////////////////////////////////////////////////////////////////////
        // Private Class Methods
        ////////////////////////////////////////////////////////////////////////////////////////////////
        private fun addTimer(timer: UUTimer)
        {
            try
            {
                synchronized(theActiveTimers)
                {
                    theActiveTimers.put(timer.timerId, timer)
                }
            }
            catch (ex: Exception)
            {
                UULog.e(UUTimer::class.java, "addTimer", "", ex)
            }
        }

        private fun removeTimer(timer: UUTimer)
        {
            try
            {
                synchronized(theActiveTimers)
                {
                    theActiveTimers.remove(timer.timerId)
                }
            }
            catch (ex: Exception)
            {
                UULog.e(UUTimer::class.java, "removeTimer", "", ex)
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////
        // Public Class Methods
        ////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * Finds an active timer by ID
         *
         * @param timerId the timer ID to lookup
         *
         * @return a timer or null if not found
         */
        fun findActiveTimer(timerId: String): UUTimer?
        {
            synchronized(theActiveTimers)
            {
                return theActiveTimers[timerId]
            }
        }

        /**
         * Lists all active timers
         *
         * @return a list of UUTimer's
         */
        fun listActiveTimers(): ArrayList<UUTimer>
        {
            synchronized(theActiveTimers)
            {
                val copy: ArrayList<UUTimer> = ArrayList()

                for (t: UUTimer in theActiveTimers.values)
                {
                    copy.add(t)
                }

                return copy
            }
        }

        /**
         * Fires a named timer
         *
         * @param timerId timer ID
         * @param timeoutMilliseconds timout in milliseconds
         * @param userInfo optional user context
         * @param delegate timer callback
         */
        fun startTimer(
            timerId: String,
            timeoutMilliseconds: Long,
            userInfo: Any?,
            block: (UUTimer, Any?)->Unit)
        {
            cancelActiveTimer(timerId)

            if (timeoutMilliseconds > 0)
            {
                val t = UUTimer(timerId, timeoutMilliseconds, false, userInfo, block)
                t.start()
            }
        }

        /**
         * Cancels a named timer
         *
         * @param timerId timer ID
         */
        fun cancelActiveTimer(timerId: String)
        {
            val timer = findActiveTimer(timerId)
            timer?.cancel()
        }
    }
}
