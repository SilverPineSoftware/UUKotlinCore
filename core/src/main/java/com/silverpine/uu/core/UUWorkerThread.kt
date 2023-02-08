package com.silverpine.uu.core

import android.os.Handler
import android.os.HandlerThread

class UUWorkerThread(name: String): HandlerThread(name)
{
    private val handler: Handler by lazy { Handler(looper) }

    init
    {
        start()
    }

    fun post(block: ()->Unit)
    {
        try
        {
            handler.post(block)
        }
        catch (ex: Exception)
        {
            // Eat it
        }
    }

    fun postDelayed(delay: Long, block: ()->Unit)
    {
        try
        {
            handler.postDelayed(block, delay)
        }
        catch (ex: Exception)
        {
            // Eat it
        }
    }

    fun remove(block: ()->Unit)
    {
        try
        {
            handler.removeCallbacks(block)
        }
        catch (ex: Exception)
        {
            // Eat it
        }
    }
}
