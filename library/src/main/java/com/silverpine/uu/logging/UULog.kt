package com.silverpine.uu.logging

import android.util.Log


/**
 * Singleton interface to logging in UU.  Simply assign a logger on application startup to use it.
 */
object UULog
{
    private var logger: UULogger? = null

    /**
     * Configures the logger to be used by UULog
     *
     * @param logger a logger implementation
     */
    fun init(logger: UULogger)
    {
        this.logger = logger
    }
    
    fun e(callingClass: Class<*>, method: String, message: String, exception: Throwable? = null)
    {
        logger?.writeToLog(Log.ERROR, callingClass, method, message, exception)
    }

    fun w(callingClass: Class<*>, method: String, message: String, exception: Throwable? = null)
    {
        logger?.writeToLog(Log.WARN, callingClass, method, message, exception)
    }

    fun i(callingClass: Class<*>, method: String, message: String, exception: Throwable? = null)
    {
        logger?.writeToLog(Log.INFO, callingClass, method, message, exception)
    }

    fun d(callingClass: Class<*>, method: String, message: String, exception: Throwable? = null)
    {
        logger?.writeToLog(Log.DEBUG, callingClass, method, message, exception)
    }

    fun v(callingClass: Class<*>, method: String, message: String, exception: Throwable? = null)
    {
        logger?.writeToLog(Log.VERBOSE, callingClass, method, message, exception)
    }
}
