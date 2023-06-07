package com.silverpine.uu.core

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import com.silverpine.uu.logging.UULog
import java.util.Locale

fun uuPlatformString(): String
{
    return Build.MODEL
}

fun uuOSVersionString(): String
{
    return Build.VERSION.RELEASE.toString()
}

fun Context.uuAppName(): String
{
    return packageName
}

fun Context.uuAppVersion(): String
{
    try
    {
        val manager = packageManager
        val info = manager.getPackageInfo(packageName, 0)
        return info.versionName
    }
    catch (ex: Exception)
    {
        UULog.e(javaClass, "uuAppVersion", "", ex)
    }

    return "unknown"
}

fun Context.uuScreenSize(): String
{
    try
    {
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y
        return String.format(Locale.US, "%dx%d", width, height)

    }
    catch (ex: Exception)
    {
        UULog.e(javaClass, "getScreenSize", "", ex)
    }

    return "unknown"
}

@SuppressLint("HardwareIds")
fun Context.uuAndroidId(): String
{
    return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
}