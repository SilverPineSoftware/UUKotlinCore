package com.silverpine.uu.core

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import com.silverpine.uu.logging.UULog
import java.io.ByteArrayOutputStream

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
            UULog.d(javaClass, "getString", "Error getting resource name $resourceId", ex)
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
            UULog.d(javaClass, "getString", "Error getting resource entry name $resourceId", ex)
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
            UULog.d(javaClass, "getString", "Error getting resource package name $resourceId", ex)
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
            UULog.d(javaClass, "getString", "Error getting resource type name $resourceId", ex)
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
            UULog.d(javaClass, "getResourceUri", "Error getting resource uri $resourceId in folder $resourceFolder", ex)
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
            UULog.d(javaClass, "getString", "Error loading string $resourceId", ex)
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
            UULog.d(javaClass, "getString", "Error loading formatted string $resourceId", ex)
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
            UULog.d(javaClass, "getDrawable", "Error loading drawable $resourceId", ex)
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
            UULog.d(javaClass, "getColor", "Error loading color $resourceId", ex)
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
            UULog.d(javaClass, "getDimension", "Error loading dimension $resourceId", ex)
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
            UULog.d(javaClass, "getDimensionPixelSize", "Error loading dimension pixel size $resourceId", ex)
            result = null
        }

        return result ?: defaultValue
    }

    fun getAppVersion(): String
    {
        requireResources()

        try
        {
            val ctx = applicationContext
            val manager = ctx.packageManager
            val info = manager.getPackageInfo(ctx.packageName, 0)
            return info.versionName
        }
        catch (ex: Exception)
        {
            UULog.e(javaClass, "getAppVersion", "", ex)
        }

        return "unknown"
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
            UULog.d(javaClass, "getRawText", "Error loading raw text $resourceId", ex)
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
            UULog.d(javaClass, "getRawBytes", "Error loading raw bytes $resourceId", ex)
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
            UULog.d(javaClass, "getFont", "Error loading font $resourceId", ex)
            result = null
        }

        return result
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