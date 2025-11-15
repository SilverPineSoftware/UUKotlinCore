package com.silverpine.uu.logging

/**
 * Represents different levels of logging severity.
 *
 * `UULogLevel` defines various logging levels, ranging from detailed debugging
 * information to critical errors. Each log level is associated with an integer
 * value to allow easy comparisons and filtering of log messages.
 */
enum class UULogLevel(val value: Int)
{
    /**
     * Off logging level (-1).
     *
     * Disables all logging for this logger.
     */
    OFF(-1),

    /**
     * Fatal logging level (0).
     *
     * Represents critical issues that lead to the immediate termination of
     * the application. Indicates a severe problem that must be addressed immediately.
     */
    FATAL(0),

    /**
     * Error logging level (1).
     *
     * Indicates a significant problem that occurred, but the application can
     * still continue running.
     */
    ERROR(1),

    /**
     * Warning logging level (2).
     *
     * Indicates potential issues or unexpected situations that do not prevent
     * the application from functioning but may need attention.
     */
    WARN(2),

    /**
     * Info logging level (3).
     *
     * Used to report general operational messages and key events.
     * Indicates that things are working as expected.
     */
    INFO(3),

    /**
     * Debug logging level (4).
     *
     * Used to provide diagnostic information useful during development.
     * Less detailed than verbose but still useful for debugging.
     */
    DEBUG(4),

    /**
     * Verbose logging level (5).
     *
     * Provides the most detailed and fine-grained information.
     * Typically used for detailed tracing and debugging purposes.
     */
    VERBOSE(5);

    companion object
    {
        /**
         * Returns the [UULogLevel] corresponding to the given integer [value],
         * or [OFF] if no match is found.
         */
        fun fromValue(value: Int): UULogLevel =
            entries.firstOrNull { it.value == value } ?: OFF
    }
}