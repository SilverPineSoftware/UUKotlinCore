package com.silverpine.uu.core

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.silverpine.uu.logging.UULog
import com.silverpine.uu.logging.logException

private const val LOG_TAG = "UUContentResolver"

object UUContentResolver
{
    private lateinit var applicationContext: Context
    private lateinit var contentResolver: ContentResolver

    fun init(context: Context)
    {
        applicationContext = context
        contentResolver = applicationContext.contentResolver
    }

    fun getFileName(uri: Uri): String
    {
        try
        {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use()
            { c ->
                if (c.moveToFirst())
                {
                    return c.uuSafeGetString(OpenableColumns.DISPLAY_NAME)
                }
            }
        }
        catch (ex: Exception)
        {
            UULog.logException(LOG_TAG, "getFileName" ,ex)
        }

        return ""
    }

    fun getFileExtension(uri: Uri): String
    {
        return getFileName(uri).uuFileExtension
    }
}