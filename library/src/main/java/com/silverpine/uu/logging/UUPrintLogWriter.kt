package com.silverpine.uu.logging

import com.silverpine.uu.core.UUDate
import com.silverpine.uu.core.uuFormatDate
import java.util.Date

/**
 * A logger implementation that writes structured log messages using Kotlin println.
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
 */
class UUPrintLogWriter : UULogWriter
{
    /**
     * Writes a log entry using Kotlin println.
     *
     * @param level The severity level of the log message (e.g., [UULogLevel.DEBUG], [UULogLevel.INFO]).
     * @param tag A short identifier or category for the source of the message.
     * @param message The log message content.
     */
    override fun writeToLog(level: UULogLevel, tag: String, message: String)
    {
        println(formatLogLine(level, tag, message))
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
}