package com.silverpine.uu.logging

/**
 * Interface to a simple log writer.
 */
interface UULogger
{
    fun writeToLog(level: Int, callingClass: Class<*>, method: String, message: String, exception: Throwable? = null)
}
