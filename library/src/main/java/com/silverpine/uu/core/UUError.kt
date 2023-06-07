package com.silverpine.uu.core

import android.os.Bundle
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Android equivalent of NSError.  Simply a container for an error code, domain and a dictionary
 * of user information.
 */
@Parcelize
open class UUError(
    val code: Int,
    val domain: String = "UUErrorDomain",
    var exception: Exception? = null,
    var userInfo: Bundle? = null,
    var underlyingError: UUError? = null): Parcelable
{
    companion object Keys
    {
        const val ERROR_DESCRIPTION = "errorDescription"
        const val ERROR_RESOLUTION = "errorResolution"
    }

    var errorDescription: String?
        get() = userInfo?.getString(ERROR_DESCRIPTION)
        set(value)
        {
            addUserInfo(ERROR_DESCRIPTION, value)
        }

    var errorResolution: String?
        get() = userInfo?.getString(ERROR_RESOLUTION)
        set(value)
        {
            addUserInfo(ERROR_DESCRIPTION, value)
        }

    fun addUserInfo(key: String, value: String?)
    {
        createUserInfo()
        userInfo?.putString(key, value)
    }

    private fun createUserInfo()
    {
        if (userInfo == null)
        {
            userInfo = Bundle()
        }
    }

}