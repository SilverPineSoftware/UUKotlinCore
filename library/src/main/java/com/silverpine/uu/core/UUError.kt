package com.silverpine.uu.core

import android.os.Bundle
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Represents a structured, parcelable error object for passing rich error information
 * across Android components and between layers of an application.
 *
 * `UUError` includes:
 * - A numeric [code] representing the specific error.
 * - A [domain] string identifying the error domain (e.g. subsystem or feature area).
 * - An optional [exception] providing stack trace or root cause information.
 * - A [userInfo] bundle for arbitrary key–value metadata such as descriptions and resolutions.
 * - An [underlyingError] for nested or chained error reporting.
 *
 * This class is marked with [Parcelize] to support easy serialization
 * via Android's [Parcelable] mechanism.
 *
 * @since 1.0.0
 * @property code A numeric error code that uniquely identifies the error.
 * @property domain A string identifying the error domain (default is `"UUErrorDomain"`).
 * @property exception An optional [Exception] associated with this error.
 * @property userInfo An optional bundle of additional metadata, typically including
 * human-readable descriptions or debugging information.
 * @property underlyingError An optional nested [UUError] providing more context on the root cause.
 */
@Parcelize
open class UUError(
    val code: Int,
    val domain: String = "UUErrorDomain",
    var exception: Exception? = null,
    var userInfo: Bundle? = null,
    var underlyingError: UUError? = null
) : Parcelable {

    /**
     * Common keys for storing standard metadata inside [userInfo].
     *
     * @since 1.0.0
     */
    companion object Keys
    {
        /**
         * Key for human-readable error description.

         * @since 1.0.0
         * */
        const val ERROR_DESCRIPTION = "errorDescription"

        /**
         * Key for recommended user or system resolution text.
         *
         * @since 1.0.0
         * */
        const val ERROR_RESOLUTION = "errorResolution"
    }

    /**
     * Human-readable description of the error, stored in [userInfo].
     *
     * Setting this property will automatically create and update the [userInfo] bundle if needed.
     *
     * @since 1.0.0
     */
    var errorDescription: String?
        get() = userInfo?.getString(ERROR_DESCRIPTION)
        set(value)
        {
            addUserInfo(ERROR_DESCRIPTION, value)
        }

    /**
     * Suggested resolution or recovery instructions for the error, stored in [userInfo].
     *
     * Setting this property will automatically create and update the [userInfo] bundle if needed.
     *
     * @since 1.0.0
     */
    var errorResolution: String?
        get() = userInfo?.getString(ERROR_RESOLUTION)
        set(value)
        {
            addUserInfo(ERROR_RESOLUTION, value)
        }

    /**
     * Adds a string value to the [userInfo] bundle under the given [key].
     * If [userInfo] does not yet exist, it is created automatically.
     *
     * @param key The key under which to store the value.
     * @param value The string value to store. A null value will overwrite with null.
     *
     * @since 1.0.0
     */
    fun addUserInfo(key: String, value: String?)
    {
        createUserInfo()
        userInfo?.putString(key, value)
    }

    /**
     * Lazily initializes the [userInfo] bundle if it does not already exist.
     *
     * @since 1.0.0
     */
    private fun createUserInfo()
    {
        if (userInfo == null)
        {
            userInfo = Bundle()
        }
    }

    /**
     * Returns a string representation of the error including domain, code,
     * description, and resolution text if present.
     *
     * @since 1.0.0
     */
    override fun toString(): String
    {
        return "Domain: $domain, Code: $code, Description: $errorDescription, Resolution: $errorResolution"
    }
}