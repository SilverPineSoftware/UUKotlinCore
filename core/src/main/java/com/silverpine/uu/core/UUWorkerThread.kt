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
        handler.post(block)
    }

    fun postDelayed(delay: Long, block: ()->Unit)
    {
        handler.postDelayed(block, delay)
    }

    fun remove(block: ()->Unit)
    {
        handler.removeCallbacks(block)
    }
}
