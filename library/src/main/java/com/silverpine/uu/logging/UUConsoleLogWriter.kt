package com.silverpine.uu.logging

import android.util.Log
import com.silverpine.uu.core.UUDate
import com.silverpine.uu.core.UUWorkerThread
import com.silverpine.uu.core.uuFormatDate
import java.util.Date

/**
 * A logger implementation that writes structured log messages to the console using Android Log
 *
 * This class implements [UULogWriter] and sends formatted log lines to `println()`.
 * The default output format is:
 *
 * ```
 * [DateTime] [Level] [Tag] [Message]
 * ```
 *
 * **Example output:**
 * ```
 * 2025-10-23T20:50:48.466-0700 DEBUG UnitTest Hello World
 * ```
 *
 * @since 1.0.0
 */
class UUConsoleLogWriter : UULogWriter
{
    companion object
    {
        private const val LINE_LENGTH = 4000
    }

    private val workerThread = UUWorkerThread("UUConsoleLogWriter")

    /**
     * Writes a log entry to the console.
     *
     * @since 1.0.0
     *
     * @param level The severity level of the log message (e.g., [UULogLevel.DEBUG], [UULogLevel.INFO]).
     * @param tag A short identifier or category for the source of the message.
     * @param message The log message content.
     */
    override fun writeToLog(level: UULogLevel, tag: String, message: String)
    {
        workerThread.post()
        {
            threadDoLogWrite(level, tag, message)
        }
    }

    /**
     * Formats a log message line.
     *
     * Output format:
     * ```
     * [timestamp] [level] [tag] [message]
     * ```
     *
     * **Example:**
     * ```
     * 2025-10-23T20:50:48.466-0700 DEBUG UnitTest Hello World
     * ```
     *
     * @since 1.0.0
     *
     * @param level The log level (e.g., debug, info, error).
     * @param tag The tag or component name associated with the log entry.
     * @param message The message text.
     * @return The formatted log line as a [String].
     */
    private fun formatLogLine(level: UULogLevel, tag: String, message: String): String
    {
        val timestamp = Date().uuFormatDate(UUDate.Formats.RFC_3339_WITH_MILLIS, UUDate.TimeZones.LOCAL)
        return "$timestamp ${level.name} $tag $message"
    }

    private val UULogLevel.priority: Int?
        get()
        {
            return when (this)
            {
                UULogLevel.OFF -> null
                UULogLevel.FATAL -> Log.ASSERT
                UULogLevel.ERROR -> Log.ERROR
                UULogLevel.WARN -> Log.WARN
                UULogLevel.INFO -> Log.INFO
                UULogLevel.DEBUG -> Log.DEBUG
                UULogLevel.VERBOSE -> Log.VERBOSE
            }
        }

    private fun threadDoLogWrite(level: UULogLevel, tag: String, message: String)
    {
        try
        {
            val priority = level.priority ?: return

            val logLine = formatLogLine(level, tag, message)
            val expectedToWrite = logLine.length
            var totalWritten = 0

            while (totalWritten < expectedToWrite)
            {
                val chunk = logLine.substring(
                    totalWritten,
                    (totalWritten + LINE_LENGTH).coerceAtMost(logLine.length)
                )

                val bytesWritten = Log.println(priority, tag, chunk)

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
}