package com.silverpine.uu.core

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import com.silverpine.uu.logging.UULog

object UUResources
{
    private lateinit var applicationContext: Context
    private lateinit var resources: Resources

    fun init(context: Context)
    {
        applicationContext = context
        resources = applicationContext.resources
    }

    fun getString(@StringRes resourceId: Int?): String
    {
        requireResources()
        
        var result: String? = null

        try
        {
            resourceId?.let()
            {
                if (resourceId != -1)
                {
                    result = resources.getString(resourceId)
                }
            }
        }
        catch (ex: Exception)
        {
            result = null
        }

        return result ?: ""
    }

    fun getString(@StringRes resourceId: Int, vararg arg: String): String
    {
        requireResources()

        var result: String?

        try
        {
            result = resources.getString(resourceId, *arg)
        }
        catch (ex: Exception)
        {
            result = null
        }

        return result ?: ""
    }

    fun getDrawable(@DrawableRes resourceId: Int, theme: Resources.Theme? = null): Drawable?
    {
        requireResources()

        var result: Drawable?

        try
        {
            resources.let()
            {
                result = ResourcesCompat.getDrawable(it, resourceId, theme)
            }
        }
        catch (ex: Exception)
        {
            result = null
        }

        return result
    }

    fun getColor(@ColorRes resourceId: Int, theme: Resources.Theme? = null, defaultValue: Int = 0): Int
    {
        requireResources()

        var result: Int?

        try
        {
            resources.let()
            {
                result = it.getColor(resourceId, theme)
            }
        }
        catch (ex: Exception)
        {
            result = null
        }

        return result ?: defaultValue
    }

    fun getDimension(@DimenRes resourceId: Int, defaultValue: Float = 0.0f): Float
    {
        requireResources()

        var result: Float?

        try
        {
            resources.let()
            {
                result = it.getDimension(resourceId)
            }
        }
        catch (ex: Exception)
        {
            result = null
        }

        return result ?: defaultValue
    }

    fun getDimensionPixelSize(@DimenRes resourceId: Int, defaultValue: Int = 0): Int
    {
        requireResources()

        var result: Int?

        try
        {
            resources.let()
            {
                result = it.getDimensionPixelSize(resourceId)
            }
        }
        catch (ex: Exception)
        {
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
        catch (ex: java.lang.Exception)
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

    fun getRawText(@RawRes id: Int): String
    {
        requireResources()
        return resources.uuGetRawTextFile(id) ?: ""
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

fun Resources.uuGetRawTextFile(@RawRes id: Int) =
    openRawResource(id).bufferedReader().use { it.readText() }