package com.silverpine.uu.core.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.silverpine.uu.core.UUThread
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UUThreadTest
{
    @Test
    fun test_0000_main()
    {
        val latch = CountDownLatch(1)

        var didInvoke = false
        var didInvokeMain = false

        UUThread.main()
        {
            didInvoke = true
            didInvokeMain = UUThread.isMain
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

        UUThread.main(delay)
        {
            end = System.currentTimeMillis()
            didInvoke = true
            didInvokeMain = UUThread.isMain
            latch.countDown()
        }

        latch.await()

        Assert.assertTrue(didInvoke)
        Assert.assertTrue(didInvokeMain)

        val duration = end - start
        Assert.assertTrue(duration >= delay)
    }

    @Test
    fun test_0002_background()
    {
        val latch = CountDownLatch(1)

        var didInvoke = false
        var didInvokeMain = true

        UUThread.background()
        {
            didInvoke = true
            didInvokeMain = UUThread.isMain
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

        UUThread.background(delay)
        {
            end = System.currentTimeMillis()
            didInvoke = true
            didInvokeMain = UUThread.isMain
            latch.countDown()
        }

        latch.await()

        Assert.assertTrue(didInvoke)
        Assert.assertFalse(didInvokeMain)

        val duration = end - start
        Assert.assertTrue(duration >= delay)
    }
}