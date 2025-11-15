package com.silverpine.uu.logging

/**
 * Defines a logging interface for structured log output.
 *
 * Implementations of [UULogWriter] are responsible for writing log messages
 * with varying severity levels, associated tags, and formatted text output.
 *
 * This abstraction allows decoupling of the logging destination (e.g. console,
 * file, network, or remote analytics) from the rest of the application code.
 *
 * @since 1.0.0
 */
interface UULogWriter
{
    /**
     * Writes a log entry with the given severity level, tag, and message.
     *
     * @since 1.0.0
     *
     * @param level The severity of the log message, represented by [UULogLevel].
     *              Common levels include [UULogLevel.Verbose], [UULogLevel.Debug],
     *              [UULogLevel.Info], [UULogLevel.Warn], [UULogLevel.Error],
     *              and [UULogLevel.Fatal].
     *
     * @param tag A short string identifier (such as a component or module name)
     *            used to categorize and filter log output.
     *
     * @param message The log message content to be recorded.
     */
    fun writeToLog(level: UULogLevel, tag: String, message: String)
}