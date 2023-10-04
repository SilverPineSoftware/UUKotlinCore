package com.silverpine.uu.core

import android.os.Handler
import android.os.Looper

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
 * Safely runs a block of code on a background thread.
 *
 * @param block the block to run
 * * @param delay the amount of delay before running the block
 */
fun uuDispatch(delay: Long, block: ()->Unit)
{
    val t = object: Thread()
    {
        override fun run()
        {
            try
            {
                sleep(delay)
                block()
            }
            catch (ex: Exception)
            {
                // Eat it
            }
        }
    }

    t.start()
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
    val t = object: Thread()
    {
        override fun run()
        {
            try
            {
                block()
            }
            catch (ex: Exception)
            {
                // Eat it
            }
        }
    }

    t.start()
}