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
 * @since 1.0.0
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
    /**
     * The active logger instance used to handle log messages.
     *
     * @since 1.0.0
     */
    private var logger: UULogger? = null

    /**
     * Sets the global logger instance used by [UULog].
     *
     * @since 1.0.0
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
     * @since 1.0.0
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
     * @since 1.0.0
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
     * @since 1.0.0
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
     * @since 1.0.0
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
     * @since 1.0.0
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
     * @since 1.0.0
     *
     * @param tag A short identifier for the source of the log message.
     * @param message The message to be logged.
     */
    fun fatal(tag: String, message: String)
    {
        logger?.fatal(tag, message)
    }
}

/**
 * Logs an exception at debug level with contextual information.
 * 
 * This convenience extension function formats and logs exception information
 * at the debug level. The log message includes the location where the exception
 * was caught and the exception's string representation.
 *
 * @since 1.0.0
 * 
 * @param logTag A short identifier for the source of the log message (e.g., class name or component).
 * @param where A description of where the exception was caught (e.g., method name or operation).
 * @param exception The exception that was caught and should be logged.
 * 
 * @sample
 * ```kotlin
 * try {
 *     performRiskyOperation()
 * } catch (e: IOException) {
 *     UULog.logException("FileService", "saveFile", e)
 *     // Logs: "Caught exception in saveFile: java.io.IOException: File not found"
 * }
 * ```
 */
fun UULog.logException(logTag: String, where: String, exception: Exception)
{
    debug(logTag, "Caught exception in $where: $exception")
}