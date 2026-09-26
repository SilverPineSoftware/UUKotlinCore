package com.silverpine.uu.core.test

import android.os.SystemClock
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.silverpine.uu.core.uuDispatch
import com.silverpine.uu.core.uuDispatchMain
import com.silverpine.uu.core.uuIsMainThread
import com.silverpine.uu.core.uuSleep
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

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

        Assert.assertTrue(
            "Main-thread callback did not complete within 10 seconds",
            latch.await(10, TimeUnit.SECONDS)
        )

        Assert.assertTrue("Callback was not invoked", didInvoke)
        Assert.assertTrue("Callback must execute on the main thread", didInvokeMain)
    }

    @Test
    fun test_0001_mainWithDelay()
    {
        val latch = CountDownLatch(1)

        var didInvoke = false
        var didInvokeMain = false
        val start = SystemClock.uptimeMillis()
        var end = 0L
        val delay = 100L

        uuDispatchMain(delay)
        {
            end = SystemClock.uptimeMillis()
            didInvoke = true
            didInvokeMain = uuIsMainThread()
            latch.countDown()
        }

        Assert.assertTrue(
            "Delayed main-thread callback did not complete within 10 seconds",
            latch.await(10, TimeUnit.SECONDS)
        )

        Assert.assertTrue("Callback was not invoked", didInvoke)
        Assert.assertTrue("Callback must execute on the main thread", didInvokeMain)

        val duration = end - start
        Assert.assertTrue(
            "Expected at least ${delay}ms; observed ${duration}ms using uptimeMillis",
            duration >= delay
        )
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

        Assert.assertTrue(
            "Background callback did not complete within 10 seconds",
            latch.await(10, TimeUnit.SECONDS)
        )

        Assert.assertTrue("Callback was not invoked", didInvoke)
        Assert.assertFalse("Callback must execute off the main thread", didInvokeMain)
    }

    @Test
    fun test_0003_backgroundWithDelay()
    {
        val latch = CountDownLatch(1)

        var didInvoke = false
        var didInvokeMain = true
        val start = SystemClock.uptimeMillis()
        var end = 0L
        val delay = 100L

        uuDispatch(delay)
        {
            end = SystemClock.uptimeMillis()
            didInvoke = true
            didInvokeMain = uuIsMainThread()
            latch.countDown()
        }

        Assert.assertTrue(
            "Delayed background callback did not complete within 10 seconds",
            latch.await(10, TimeUnit.SECONDS)
        )

        Assert.assertTrue("Callback was not invoked", didInvoke)
        Assert.assertFalse("Callback must execute off the main thread", didInvokeMain)

        val duration = end - start
        Assert.assertTrue(
            "Expected at least ${delay}ms; observed ${duration}ms using uptimeMillis",
            duration >= delay
        )
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
                //UULog.d(javaClass, "test", "Block $id starting")

                for (loop in 0 until loops)
                {
                    //UULog.d(javaClass, "test", "Block_${id}_loop_$loop sleeping")
                    uuSleep(sleep)
                }

                //UULog.d(javaClass, "test", "Block $id finished")
                latch.countDown()
            }
        }

        Assert.assertTrue(
            "Concurrent callbacks did not complete within 10 seconds",
            latch.await(10, TimeUnit.SECONDS)
        )
    }
}