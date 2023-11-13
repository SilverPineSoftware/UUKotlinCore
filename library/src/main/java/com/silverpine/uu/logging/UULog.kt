package com.silverpine.uu.logging

import android.util.Log
import com.silverpine.uu.core.UUWorkerThread
import java.io.PrintWriter
import java.io.StringWriter


/**
 * Simple logging wrapper.  Set LOGGING_ENABLED to false to turn off all Logging.  This is useful
 * when you want to disable all Log output for release builds in one common location.
 */
object UULog
{
    private const val LINE_LENGTH = 4000
    private val workerThread = UUWorkerThread("UULog")

    fun e(callingClass: Class<*>, method: String, message: String, exception: Throwable? = null)
    {
        writeToLog(Log.ERROR, callingClass, method, message, exception)
    }

    fun w(callingClass: Class<*>, method: String, message: String, exception: Throwable? = null)
    {
        writeToLog(Log.WARN, callingClass, method, message, exception)
    }

    fun i(callingClass: Class<*>, method: String, message: String, exception: Throwable? = null)
    {
        writeToLog(Log.INFO, callingClass, method, message, exception)
    }

    fun d(callingClass: Class<*>, method: String, message: String, exception: Throwable? = null)
    {
        writeToLog(Log.DEBUG, callingClass, method, message, exception)
    }

    fun v(callingClass: Class<*>, method: String, message: String, exception: Throwable? = null)
    {
        writeToLog(Log.VERBOSE, callingClass, method, message, exception)
    }

    private fun writeToLog(level: Int, callingClass: Class<*>, method: String, message: String, exception: Throwable? = null)
    {
        workerThread.post()
        {
            threadDoLogWrite(level, callingClass, method, message, exception)
        }
    }

    private fun threadDoLogWrite(level: Int, callingClass: Class<*>, method: String, message: String, exception: Throwable?)
    {
        var logLine = "$method: $message"

        try
        {
            if (exception != null)
            {
                logLine += ", Exception: " + stackTraceToString(exception)
            }

            val expectedToWrite = logLine.length
            var totalWritten = 0

            while (totalWritten < expectedToWrite)
            {
                val chunk = logLine.substring(
                    totalWritten,
                    (totalWritten + LINE_LENGTH).coerceAtMost(logLine.length)
                )

                val bytesWritten = Log.println(level, callingClass.name, chunk)

                // On some devices, logging seems to fail and return zero.  In this case, we have to just
                // abort and let the app keep running.
                if (bytesWritten <= 0)
                {
                    break
                }

                totalWritten += chunk.length
            }
        }
        catch (ex: Exception)
        {
            Log.e("UULog", "Error writing to log", ex)
        }
    }

    private fun stackTraceToString(throwable: Throwable?): String
    {
        try
        {
            if (throwable != null)
            {
                val sw = StringWriter()
                throwable.printStackTrace(PrintWriter(sw))
                return sw.toString()
            }
        }
        catch (t: Throwable)
        {
            Log.e("UULog", "stackTraceToString", t)
        }

        try
        {
            if (throwable != null)
            {
                return throwable.toString()
            }
        }
        catch (t: Throwable)
        {
            Log.e("UULog", "stackTraceToString", t)
        }

        return ""
    }
}
