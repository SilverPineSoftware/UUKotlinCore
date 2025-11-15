package com.silverpine.uu.logging

/**
 * A logger that routes structured log messages to a [UULogWriter], with support for:
 * - A minimum [logLevel] threshold
 * - Inclusive and exclusive tag filtering
 *
 * @since 1.0.0
 */
open class UULogger(

    /**
     * The log writer that handles the actual output (file/console/remote/etc.).
     *
     * @since 1.0.0
     */
    var logWriter: UULogWriter
) {

    /**
     * The minimum severity that will be logged.
     *
     * Only messages whose [UULogLevel.value] is **less than or equal to** this [logLevel]'s value
     * will be emitted. For example, if `logLevel == INFO`, then `INFO`, `WARN`, `ERROR`, and
     * `FATAL` are written; `DEBUG` and `VERBOSE` are ignored.
     *
     * @since 1.0.0
     */
    var logLevel: UULogLevel = UULogLevel.OFF

    /**
     * Tags that are explicitly allowed. If non-empty, only messages whose tag is present here
     * will be logged (subject to [excludedTags] and [logLevel]).
     *
     * @since 1.0.0
     */
    var includedTags: MutableList<String> = mutableListOf()

    /**
     * Tags that are explicitly suppressed. If a tag appears here, it will not be logged
     * even if it is present in [includedTags].
     *
     * @since 1.0.0
     */
    var excludedTags: MutableList<String> = mutableListOf()

    // ---------------------------------------------------------------------------------------------
    // Filtering helpers
    // ---------------------------------------------------------------------------------------------

    /**
     * Returns true if [level] meets or exceeds the configured [logLevel] threshold.
     * Lower numeric values indicate higher severity (e.g., FATAL < ERROR < WARN ...).
     *
     * @since 1.0.0
     */
    fun shouldLog(level: UULogLevel): Boolean
    {
        return level.value <= this.logLevel.value
    }

    /**
     * Returns true if [tag] is allowed by the include/exclude filters.
     *
     * - If [excludedTags] contains [tag], returns false.
     * - If [includedTags] is non-empty, returns true only if [tag] is included.
     * - Otherwise returns true.
     *
     * @since 1.0.0
     */
    fun shouldLog(tag: String): Boolean
    {
        if (excludedTags.contains(tag))
        {
            return false
        }

        if (includedTags.isNotEmpty())
        {
            return includedTags.contains(tag)
        }

        return true
    }

    // ---------------------------------------------------------------------------------------------
    // Core logging
    // ---------------------------------------------------------------------------------------------

    /**
     * Writes a log entry if it passes level and tag filters.
     *
     * @since 1.0.0
     *
     * @param level   Severity of the message.
     * @param tag     Short categorization label (component/module/etc.).
     * @param message The message text to log.
     */
    fun writeToLog(level: UULogLevel, tag: String, message: String)
    {
        if (!shouldLog(level) || !shouldLog(tag))
        {
            return
        }

        logWriter.writeToLog(level, tag, message)
    }

    // ---------------------------------------------------------------------------------------------
    // Convenience methods by level
    // ---------------------------------------------------------------------------------------------

    /**
     * Logs a verbose-level message.
     *
     * Use this for highly detailed diagnostic output that is typically only
     * useful during active debugging or development. Verbose messages are
     * usually the lowest priority and may be filtered out in production builds.
     *
     * @since 1.0.0
     *
     * @param tag A short identifier that categorizes the log message, such as
     *            a component or module name.
     * @param message The message text to record.
     */
    fun verbose(tag: String, message: String)
    {
        writeToLog(UULogLevel.VERBOSE, tag, message)
    }

    /**
     * Logs a debug-level message.
     *
     * Use this for general debugging information that helps trace normal
     * program flow or internal state changes.
     *
     * @since 1.0.0
     *
     * @param tag A short identifier that categorizes the log message.
     * @param message The message text to record.
     */
    fun debug(tag: String, message: String)
    {
        writeToLog(UULogLevel.DEBUG, tag, message)
    }

    /**
     * Logs an informational message.
     *
     * Use this for normal, expected events or significant operational
     * milestones. These messages confirm that the application is working as
     * intended.
     *
     * @since 1.0.0
     *
     * @param tag A short identifier that categorizes the log message.
     * @param message The message text to record.
     */
    fun info(tag: String, message: String)
    {
        writeToLog(UULogLevel.INFO, tag, message)
    }

    /**
     * Logs a warning message.
     *
     * Use this to report potential issues or unexpected conditions that do not
     * prevent the application from continuing, but may require attention or
     * investigation.
     *
     * @since 1.0.0
     *
     * @param tag A short identifier that categorizes the log message.
     * @param message The message text to record.
     */
    fun warn(tag: String, message: String)
    {
        writeToLog(UULogLevel.WARN, tag, message)
    }

    /**
     * Logs an error message.
     *
     * Use this to record errors or problems that occurred during execution but
     * from which the application can recover.
     *
     * @since 1.0.0
     *
     * @param tag A short identifier that categorizes the log message.
     * @param message The message text to record.
     */
    fun error(tag: String, message: String)
    {
        writeToLog(UULogLevel.ERROR, tag, message)
    }

    /**
     * Logs a fatal or critical message.
     *
     * Use this for severe conditions that cause the application or component
     * to stop functioning correctly. Fatal logs should indicate unrecoverable
     * failures that may require immediate attention.
     *
     * @since 1.0.0
     *
     * @param tag A short identifier that categorizes the log message.
     * @param message The message text to record.
     */
    fun fatal(tag: String, message: String)
    {
        writeToLog(UULogLevel.FATAL, tag, message)
    }
}

