package com.silverpine.uu.logging

import com.silverpine.uu.logging.UULog.setLogger

/**
 * A static logging utility that provides convenience methods for logging
 * messages at various severity levels.
 *
 * This class acts as a global façade for logging, delegating all output
 * to a configured [UULogger] instance. If no logger is configured,
 * all log calls are silently ignored.
 *
 * ### Usage Example
 * ```
 * // Create a console logger instance.
 * val consoleLogger = UULogger(UUConsoleLogWriter())
 *
 * // Initialize the global logger.
 * UULog.setLogger(consoleLogger)
 *
 * // Log messages at different severity levels.
 * UULog.verbose("Startup", "Application is starting up.")
 * UULog.debug("Networking", "Fetching data from API endpoint.")
 * UULog.info("Database", "User data loaded successfully.")
 * UULog.warn("Memory", "Memory usage is nearing the limit.")
 * UULog.error("FileIO", "Failed to save file to disk.")
 * UULog.fatal("Crash", "Unrecoverable error occurred. Shutting down.")
 * ```
 *
 * ### Notes
 * - You can replace the active logger by calling [setLogger] with a custom [UULogger].
 * - If no logger is set, all logging operations are ignored.
 */
object UULog
{
    /** The active logger instance used to handle log messages. */
    private var logger: UULogger? = null

    /**
     * Sets the global logger instance used by [UULog].
     *
     * @param logger The [UULogger] instance to use for handling log messages.
     *               If `null`, all log calls will be ignored.
     */
    fun setLogger(logger: UULogger?)
    {
        this.logger = logger
    }

    /**
     * Logs a verbose message.
     *
     * @param tag A short identifier for the source of the log message.
     * @param message The message to be logged.
     */
    fun verbose(tag: String, message: String)
    {
        logger?.verbose(tag, message)
    }

    /**
     * Logs a debug message.
     *
     * @param tag A short identifier for the source of the log message.
     * @param message The message to be logged.
     */
    fun debug(tag: String, message: String)
    {
        logger?.debug(tag, message)
    }

    /**
     * Logs an informational message.
     *
     * @param tag A short identifier for the source of the log message.
     * @param message The message to be logged.
     */
    fun info(tag: String, message: String)
    {
        logger?.info(tag, message)
    }

    /**
     * Logs a warning message.
     *
     * @param tag A short identifier for the source of the log message.
     * @param message The message to be logged.
     */
    fun warn(tag: String, message: String)
    {
        logger?.warn(tag, message)
    }

    /**
     * Logs an error message.
     *
     * @param tag A short identifier for the source of the log message.
     * @param message The message to be logged.
     */
    fun error(tag: String, message: String)
    {
        logger?.error(tag, message)
    }

    /**
     * Logs a fatal (critical) message.
     *
     * Use this for unrecoverable conditions that may lead to application termination.
     *
     * @param tag A short identifier for the source of the log message.
     * @param message The message to be logged.
     */
    fun fatal(tag: String, message: String)
    {
        logger?.fatal(tag, message)
    }
}