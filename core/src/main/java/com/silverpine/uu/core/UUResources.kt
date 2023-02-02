package com.silverpine.uu.core

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import com.silverpine.uu.logging.UULog

object UUResources
{
    private var applicationContext: Context? = null
    private var resources: Resources? = null

    fun init(context: Context)
    {
        applicationContext = context
        resources = applicationContext?.resources
    }

    fun getString(@StringRes resourceId: Int?): String
    {
        var result: String? = null

        try
        {
            resourceId?.let()
            {
                if (resourceId != -1)
                {
                    result = resources?.getString(resourceId)
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
        var result: String?

        try
        {
            result = resources?.getString(resourceId, *arg)
        }
        catch (ex: Exception)
        {
            result = null
        }

        return result ?: ""
    }

    fun getDrawable(@DrawableRes resourceId: Int, theme: Resources.Theme? = null): Drawable?
    {
        var result: Drawable? = null

        try
        {
            resources?.let()
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

    fun getColor(@ColorRes resourceId: Int, theme: Resources.Theme? = null): Int?
    {
        var result: Int? = null

        try
        {
            resources?.let()
            {
                result = ResourcesCompat.getColor(it, resourceId, theme)
            }
        }
        catch (ex: Exception)
        {
            result = null
        }

        return result
    }

    fun getAppVersion(): String
    {
        val ctx = applicationContext ?: return "unknown"

        try
        {
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
        val ctx = applicationContext ?: return "unknown"
        return ctx.packageName
    }
}