package com.silverpine.uu.core.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.silverpine.uu.core.UUWorkerThread
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UUWorkerThreadTest
{
    @Test
    fun test_0000_post()
    {
        val t = UUWorkerThread("unit_test")

        val latch = CountDownLatch(1)

        var didInvoke = false

        t.post()
        {
            didInvoke = true
            latch.countDown()
        }

        latch.await()

        Assert.assertTrue(didInvoke)
    }

    @Test
    fun test_0001_post()
    {
        val t = UUWorkerThread("unit_test")

        val latch = CountDownLatch(1)

        var didInvoke = false
        val start = System.currentTimeMillis()
        var end = 0L
        val delay = 100L

        t.postDelayed(delay)
        {
            end = System.currentTimeMillis()
            didInvoke = true
            latch.countDown()
        }

        latch.await()

        Assert.assertTrue(didInvoke)

        val duration = end - start
        Assert.assertTrue(duration >= delay)
    }
}