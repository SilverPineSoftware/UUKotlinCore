package com.silverpine.uu.logging

/**
 * Defines a logging interface for writing diagnostic or informational messages.
 *
 * This interface is intended to be implemented by classes that provide
 * logging functionality (e.g., writing to console, files, or remote systems).
 */
interface UULogger
{
    /**
     * Writes a log entry with the specified details.
     *
     * @param level         The severity level of the log message (e.g., debug, info, warning, error).
     *                      The exact mapping of integers to levels should be defined by the implementation.
     *
     * @param callingClass  The class where the log entry originated. This provides context
     *                      about the source of the message.
     *
     * @param method        The name of the method in which the log entry was generated.
     *                      Useful for tracing execution flow.
     *
     * @param message       The actual log message to record. This may include status information,
     *                      debugging output, or error descriptions.
     *
     * @param exception     An optional [Throwable] associated with the log entry.
     *                      If provided, the logger should include stack trace or exception details
     *                      in the output. Defaults to `null`.
     */
    fun writeToLog(
        level: Int,
        callingClass: Class<*>,
        method: String,
        message: String,
        exception: Throwable? = null
    )
}
