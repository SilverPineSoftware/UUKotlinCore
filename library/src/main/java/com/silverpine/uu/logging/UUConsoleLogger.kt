package com.silverpine.uu.logging

import android.util.Log
import com.silverpine.uu.core.UUWorkerThread
import java.io.PrintWriter
import java.io.StringWriter


/**
 * Basic Logger implementation that simply writes log entries using android.util.Log
 */
class UUConsoleLogger: UULogger
{
    companion object
    {
        private const val LINE_LENGTH = 4000
    }

    private val workerThread = UUWorkerThread("UULog")

    override fun writeToLog(level: Int, callingClass: Class<*>, method: String, message: String, exception: Throwable?)
    {
        workerThread.post()
        {
            threadDoLogWrite(level, callingClass, method, message, exception)
        }
    }

    private fun formatLogLine(method: String, message: String, exception: Throwable? = null): String
    {
        var logLine = "$method: $message"

        exception?.let()
        { ex ->
            logLine += ", Exception: " + stackTraceToString(ex)
        }

        return logLine
    }

    private fun threadDoLogWrite(level: Int, callingClass: Class<*>, method: String, message: String, exception: Throwable?)
    {
        try
        {
            val logLine = formatLogLine(method, message, exception)
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
            Log.e(javaClass.name, "Error writing to log", ex)
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
            Log.e(javaClass.name, "stackTraceToString", t)
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
            Log.e(javaClass.name, "stackTraceToString", t)
        }

        return ""
    }
}
