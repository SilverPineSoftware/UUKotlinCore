package com.silverpine.uu.core

import android.os.Handler
import android.os.Looper

private val workerThread = UUWorkerThread("UUDispatch")

/**
 * Safely runs a block of code on the main thread.
 *
 * @param delay the amount of delay before running the block
 * @param block the block to run
 *
 */
fun uuDispatchMain(delay: Long, block: ()->Unit)
{
    workerThread.postDelayed(delay)
    {
        Handler(Looper.getMainLooper()).post(block)
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
    uuDispatchMain(0L, block)
}

/**
 * Safely runs a block of code on a background thread.
 *
 * @param block the block to run
 * * @param delay the amount of delay before running the block
 */
fun uuDispatch(delay: Long, block: ()->Unit)
{
    workerThread.postDelayed(delay, block)
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
    workerThread.post(block)
}