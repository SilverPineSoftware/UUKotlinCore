package com.silverpine.uu.core

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.IntegerRes
import androidx.annotation.RawRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import com.silverpine.uu.logging.UULog
import com.silverpine.uu.logging.logException
import java.io.ByteArrayOutputStream

private const val LOG_TAG = "UUResources"

object UUResources
{
    private const val INVALID_RESOURCE_ID = 0

    private lateinit var applicationContext: Context
    private lateinit var resources: Resources

    fun init(context: Context)
    {
        applicationContext = context
        resources = applicationContext.resources
    }

    @SuppressLint("DiscouragedApi")
    fun getIdentifier(name: String, type: String, packageName: String = applicationContext.packageName): Int
    {
        requireResources()
        return resources.getIdentifier(name, type, packageName)
    }

    fun getResourceName(resourceId: Int): String
    {
        requireResources()

        var result: String? = null

        try
        {
            if (resourceId != INVALID_RESOURCE_ID)
            {
                result = resources.getResourceName(resourceId)
            }
        }
        catch (ex: Exception)
        {
            UULog.logException(LOG_TAG, "getString($resourceId)", ex)
            result = null
        }

        return result ?: ""
    }

    fun getResourceEntryName(resourceId: Int): String
    {
        requireResources()

        var result: String? = null

        try
        {
            if (resourceId != INVALID_RESOURCE_ID)
            {
                result = resources.getResourceEntryName(resourceId)
            }
        }
        catch (ex: Exception)
        {
            UULog.logException(LOG_TAG, "getResourceEntryName($resourceId)", ex)
            result = null
        }

        return result ?: ""
    }

    fun getResourcePackageName(resourceId: Int): String
    {
        requireResources()

        var result: String? = null

        try
        {
            if (resourceId != INVALID_RESOURCE_ID)
            {
                result = resources.getResourcePackageName(resourceId)
            }
        }
        catch (ex: Exception)
        {
            UULog.logException(LOG_TAG, "getResourcePackageName($resourceId)", ex)
            result = null
        }

        return result ?: ""
    }

    fun getResourceTypeName(resourceId: Int): String
    {
        requireResources()

        var result: String? = null

        try
        {
            if (resourceId != INVALID_RESOURCE_ID)
            {
                result = resources.getResourceTypeName(resourceId)
            }
        }
        catch (ex: Exception)
        {
            UULog.logException(LOG_TAG, "getResourceTypeName($resourceId)", ex)
            result = null
        }

        return result ?: ""
    }

    fun getResourceUri(resourceId: Int, resourceFolder: String): Uri?
    {
        var result: Uri?

        try
        {
            result = Uri.parse("android.resource://${getResourcePackageName(resourceId)}/$resourceFolder/${getResourceEntryName(resourceId)}")
        }
        catch (ex: Exception)
        {
            UULog.logException(LOG_TAG, "getResourceUrl(resourceId: $resourceId, resourceFolder: $resourceFolder)", ex)
            result = null
        }

        return result
    }

    fun getRawResourceUri(resourceId: Int): Uri?
    {
        return getResourceUri(resourceId, "raw")
    }

    fun getString(@StringRes resourceId: Int?): String
    {
        requireResources()
        
        var result: String? = null

        try
        {
            resourceId?.let()
            {
                if (resourceId != INVALID_RESOURCE_ID)
                {
                    result = resources.getString(it)
                }
            }
        }
        catch (ex: Exception)
        {
            UULog.logException(LOG_TAG, "getString($resourceId)", ex)
            result = null
        }

        return result ?: ""
    }

    fun getString(@StringRes resourceId: Int, vararg arg: String): String
    {
        requireResources()

        var result: String? = null

        try
        {
            if (resourceId != INVALID_RESOURCE_ID)
            {
                result = resources.getString(resourceId, *arg)
            }
        }
        catch (ex: Exception)
        {
            UULog.logException(LOG_TAG, "getString($resourceId)", ex)
            result = null
        }

        return result ?: ""
    }

    fun getDrawable(@DrawableRes resourceId: Int, theme: Resources.Theme? = null): Drawable?
    {
        requireResources()

        var result: Drawable? = null

        try
        {
            resources.let()
            { rez ->
                if (resourceId != INVALID_RESOURCE_ID)
                {
                    result = ResourcesCompat.getDrawable(rez, resourceId, theme)
                }
            }
        }
        catch (ex: Exception)
        {
            UULog.logException(LOG_TAG, "getDrawable($resourceId)", ex)
            result = null
        }

        return result
    }

    fun getColor(@ColorRes resourceId: Int, theme: Resources.Theme? = null, defaultValue: Int = 0): Int
    {
        requireResources()

        var result: Int? = null

        try
        {
            if (resourceId != INVALID_RESOURCE_ID)
            {
                result = resources.getColor(resourceId, theme)
            }
        }
        catch (ex: Exception)
        {
            UULog.logException(LOG_TAG, "getColor($resourceId)", ex)
            result = null
        }

        return result ?: defaultValue
    }

    fun getDimension(@DimenRes resourceId: Int, defaultValue: Float = 0.0f): Float
    {
        requireResources()

        var result: Float? = null

        try
        {
            if (resourceId != INVALID_RESOURCE_ID)
            {
                result = resources.getDimension(resourceId)
            }
        }
        catch (ex: Exception)
        {
            UULog.logException(LOG_TAG, "getDimension($resourceId)", ex)
            result = null
        }

        return result ?: defaultValue
    }

    fun getDimensionPixelSize(@DimenRes resourceId: Int, defaultValue: Int = 0): Int
    {
        requireResources()

        var result: Int? = null

        try
        {
            if (resourceId != INVALID_RESOURCE_ID)
            {
                result = resources.getDimensionPixelSize(resourceId)
            }
        }
        catch (ex: Exception)
        {
            UULog.logException(LOG_TAG, "getDimensionPixelSize($resourceId)", ex)
            result = null
        }

        return result ?: defaultValue
    }

    fun getAppVersion(): String
    {
        requireResources()
        return applicationContext.uuAppVersion()
    }

    fun getAppVersionCode(): String
    {
        requireResources()
        return applicationContext.uuAppVersionCode()
    }

    fun getAppBundleId(): String
    {
        requireResources()
        return applicationContext.packageName
    }

    fun getRawText(@RawRes resourceId: Int): String?
    {
        requireResources()

        var result: String? = null

        try
        {
            if (resourceId != INVALID_RESOURCE_ID)
            {
                result = resources.uuGetRawTextFile(resourceId)
            }
        }
        catch (ex: Exception)
        {
            UULog.logException(LOG_TAG, "getRawText($resourceId)", ex)
            result = null
        }

        return result
    }

    fun getRawBytes(@RawRes resourceId: Int): ByteArray?
    {
        requireResources()

        var result: ByteArray? = null

        try
        {
            if (resourceId != INVALID_RESOURCE_ID)
            {
                result = resources.uuGetRawResourceBytes(resourceId)
            }
        }
        catch (ex: Exception)
        {
            UULog.logException(LOG_TAG, "getRawBytes($resourceId)", ex)
            result = null
        }

        return result
    }

    fun getFont(@FontRes resourceId: Int): Typeface?
    {
        requireResources()

        var result: Typeface? = null

        try
        {
            if (resourceId != INVALID_RESOURCE_ID)
            {
                result = resources.getFont(resourceId)
            }
        }
        catch (ex: Exception)
        {
            UULog.logException(LOG_TAG, "getFont($resourceId)", ex)
            result = null
        }

        return result
    }

    fun getInteger(@IntegerRes resourceId: Int, defaultValue: Int = 0): Int
    {
        requireResources()

        var result: Int? = null

        try
        {
            if (resourceId != INVALID_RESOURCE_ID)
            {
                result = resources.getInteger(resourceId)
            }
        }
        catch (ex: Exception)
        {
            UULog.logException(LOG_TAG, "getInteger($resourceId)", ex)
            result = null
        }

        return result ?: defaultValue
    }

    fun getIntArray(@ArrayRes resourceId: Int, defaultValue: IntArray = IntArray(0)): IntArray
    {
        requireResources()

        var result: IntArray? = null

        try
        {
            if (resourceId != INVALID_RESOURCE_ID)
            {
                result = resources.getIntArray(resourceId)
            }
        }
        catch (ex: Exception)
        {
            UULog.logException(LOG_TAG, "getIntArray($resourceId)", ex)
            result = null
        }

        return result ?: defaultValue
    }

    fun getStringArray(@ArrayRes resourceId: Int, defaultValue: Array<String> = arrayOf()): Array<String>
    {
        requireResources()

        var result: Array<String>? = null

        try
        {
            if (resourceId != INVALID_RESOURCE_ID)
            {
                result = resources.getStringArray(resourceId)
            }
        }
        catch (ex: Exception)
        {
            UULog.logException(LOG_TAG, "getStringArray($resourceId)", ex)
            result = null
        }

        return result ?: defaultValue
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getFloat(@DimenRes resourceId: Int, defaultValue: Float = 0.0f): Float
    {
        requireResources()

        var result: Float? = null

        try
        {
            if (resourceId != INVALID_RESOURCE_ID)
            {
                result = resources.getFloat(resourceId)
            }
        }
        catch (ex: Exception)
        {
            UULog.logException(LOG_TAG, "getFloat($resourceId)", ex)
            result = null
        }

        return result ?: defaultValue
    }

    private fun requireResources()
    {
        if (!UUResources::resources.isInitialized)
        {
            throw RuntimeException("Must call UUResource.init(...) prior to first use!")
        }

        if (!UUResources::applicationContext.isInitialized)
        {
            throw RuntimeException("Must call UUResource.init(...) prior to first use!")
        }
    }
}

fun Resources.uuGetRawTextFile(@RawRes id: Int): String? =
    openRawResource(id).bufferedReader().use { it.readText() }

fun Resources.uuGetRawResourceBytes(@RawRes id: Int): ByteArray?
{
    val bos = ByteArrayOutputStream()

    openRawResource(id).use()
    { inputStream ->

        val chunk = ByteArray(10240)
        var bytesRead: Int

        while (true)
        {
            bytesRead = inputStream.read(chunk, 0, chunk.size)
            if (bytesRead == -1)
            {
                break
            }

            bos.write(chunk, 0, bytesRead)
        }
    }

    return bos.toByteArray()
}