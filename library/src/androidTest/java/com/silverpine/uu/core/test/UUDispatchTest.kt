package com.silverpine.uu.core.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.silverpine.uu.core.uuDispatch
import com.silverpine.uu.core.uuDispatchMain
import com.silverpine.uu.core.uuIsMainThread
import com.silverpine.uu.core.uuSleep
import com.silverpine.uu.logging.UULog
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UUDispatchTest
{
    @Test
    fun test_0000_main()
    {
        val latch = CountDownLatch(1)

        var didInvoke = false
        var didInvokeMain = false

        uuDispatchMain()
        {
            didInvoke = true
            didInvokeMain = uuIsMainThread()
            latch.countDown()
        }

        latch.await()

        Assert.assertTrue(didInvoke)
        Assert.assertTrue(didInvokeMain)
    }

    @Test
    fun test_0001_mainWithDelay()
    {
        val latch = CountDownLatch(1)

        var didInvoke = false
        var didInvokeMain = false
        val start = System.currentTimeMillis()
        var end = 0L
        val delay = 100L

        uuDispatchMain(delay)
        {
            end = System.currentTimeMillis()
            didInvoke = true
            didInvokeMain = uuIsMainThread()
            latch.countDown()
        }

        latch.await()

        Assert.assertTrue(didInvoke)
        Assert.assertTrue(didInvokeMain)

        val duration = end - start
        println("delay: $delay, duration: $duration")
        Assert.assertTrue(duration >= delay)
    }

    @Test
    fun test_0002_background()
    {
        val latch = CountDownLatch(1)

        var didInvoke = false
        var didInvokeMain = true

        uuDispatch()
        {
            didInvoke = true
            didInvokeMain = uuIsMainThread()
            latch.countDown()
        }

        latch.await()

        Assert.assertTrue(didInvoke)
        Assert.assertFalse(didInvokeMain)
    }

    @Test
    fun test_0003_mainWithDelay()
    {
        val latch = CountDownLatch(1)

        var didInvoke = false
        var didInvokeMain = true
        val start = System.currentTimeMillis()
        var end = 0L
        val delay = 100L

        uuDispatch(delay)
        {
            end = System.currentTimeMillis()
            didInvoke = true
            didInvokeMain = uuIsMainThread()
            latch.countDown()
        }

        latch.await()

        Assert.assertTrue(didInvoke)
        Assert.assertFalse(didInvokeMain)

        val duration = end - start
        Assert.assertTrue(duration >= delay)
    }

    @Test
    fun test_0004_multipleConcurrent()
    {
        val count = 10
        val loops = 50
        val sleep = 10L

        val latch = CountDownLatch(count)

        for (id in 0 until count)
        {
            uuDispatch()
            {
                UULog.d(javaClass, "test", "Block $id starting")

                for (loop in 0 until loops)
                {
                    UULog.d(javaClass, "test", "Block_${id}_loop_$loop sleeping")
                    uuSleep(sleep)
                }

                UULog.d(javaClass, "test", "Block $id finished")
                latch.countDown()
            }
        }

        latch.await()
    }
}