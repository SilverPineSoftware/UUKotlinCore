package com.silverpine.uu.core

import android.os.Handler
import android.os.Looper
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
    Handler(Looper.getMainLooper()).postDelayed(block, delay)
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
    Handler(Looper.getMainLooper()).post(block)
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
    CoroutineScope(Dispatchers.IO).launch()
    {
        try
        {
            if (delay > 0)
            {
                uuSleep("uuDispatch", delay)
            }

            block()
        }
        catch (ex: Exception)
        {
            // Eat it
        }
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