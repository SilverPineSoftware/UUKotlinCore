package com.silverpine.uu.core

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Android equivalent of NSError.  Simply a container for an error code, domain and a dictionary
 * of user information.
 */
@Parcelize
class UUError(
    val code: Int,
    val domain: String = "UUErrorDomain",
    val exception: Exception? = null,
    val userInfo: Parcelable? = null,
    val underlyingError: UUError? = null): Parcelable