package com.silverpine.uu.core

import android.os.Handler
import android.os.Looper
import com.silverpine.uu.logging.UULog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Safely runs a block of code on the main thread.
 *
 * @param delay the amount of delay before running the block
 * @param block the block to run
 *
 */
fun uuDispatchMain(delay: Long, block: ()->Unit)
{
    try
    {
        Handler(Looper.getMainLooper()).postDelayed(
        {
            uuSafeInvokeBlock(block)
        }, delay)
    }
    catch (ex: Exception)
    {
        UULog.d(Object::class.java, "uuDispatchMain", "", ex)
    }
}


/**
 * Safely runs a block of code on the main thread.
 *
 * @param block the block to run
 *
 * Runs immediately
 */
fun uuDispatchMain(block: ()->Unit)
{
    try
    {
        Handler(Looper.getMainLooper()).post()
        {
            uuSafeInvokeBlock(block)
        }
    }
    catch (ex: Exception)
    {
        UULog.d(Object::class.java, "uuDispatchMain", "", ex)
    }
}

/**
 * Safely runs a block of code on a background thread using the IO coroutine scope
 *
 * @param delay the amount of delay before running the block
 * @param block the block to run
 *
 */
fun uuDispatch(delay: Long, block: ()->Unit)
{
    try
    {
        CoroutineScope(Dispatchers.IO).launch()
        {
            try
            {
                if (delay > 0)
                {
                    uuSleep(delay)
                }

                block()
            }
            catch (ex: Exception)
            {
                UULog.d(javaClass, "uuDispatch", "", ex)
            }
        }
    }
    catch (ex: Exception)
    {
        UULog.d(Object::class.java, "uuDispatch", "", ex)
    }
}

/**
 * Safely runs a block of code on a background thread.
 *
 * @param block the block to run
 *
 * Runs immediately
 */
fun uuDispatch(block: ()->Unit)
{
    uuDispatch(0, block)
}

private fun uuSafeInvokeBlock(block: ()->Unit)
{
    try
    {
        block()
    }
    catch (ex: Exception)
    {
        UULog.d(Object::class.java, "uuSafeInvokeBlock", "", ex)

    }
}