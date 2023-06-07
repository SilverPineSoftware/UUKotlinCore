package com.silverpine.uu.core.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.silverpine.uu.core.UUThread
import com.silverpine.uu.core.UUTimer
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UUTimerTest
{
    @Test
    fun test_0000_simple_timer()
    {
        val latch = CountDownLatch(1)

        var didInvoke = false
        var callbackTimer: UUTimer? = null
        var callbackUserInfo: Any? = null

        val start = System.currentTimeMillis()
        var invokeTime = 0L
        val interval = 100L

        val timerId = "unit_test"
        UUTimer.startTimer(timerId, interval, null)
        { timer, userInfo ->

            invokeTime = System.currentTimeMillis()
            callbackTimer = timer
            callbackUserInfo = userInfo
            didInvoke = true
            latch.countDown()
        }

        latch.await()

        Assert.assertNotNull(callbackTimer)
        Assert.assertNull(callbackUserInfo)
        Assert.assertTrue(didInvoke)
        Assert.assertTrue((invokeTime - start) >= interval)

        // A single shot timer gets cleaned up after the timer block is invoked, so we need
        // to wait.  This is a hacky way to test, but works in this simple case
        UUThread.safeSleep("unit_test", 100)

        Assert.assertEquals(0, UUTimer.listActiveTimers().size)
        Assert.assertNull(UUTimer.findActiveTimer(timerId))
    }

    @Test
    fun test_0001_cancel_timer()
    {
        //val latch = CountDownLatch(1)

//        var didInvoke = false
//        var callbackTimer: UUTimer? = null
//        var callbackUserInfo: Any? = null
//
//        val start = System.currentTimeMillis()
//        var invokeTime = 0L
        val interval = 1000L

        val timerId = "unit_test"
        UUTimer.startTimer(timerId, interval, null)
        { timer, userInfo ->

            System.out.println("GOT HERE")
            //Assert.fail("Should never get here")
//            invokeTime = System.currentTimeMillis()
//            callbackTimer = timer
//            callbackUserInfo = userInfo
//            didInvoke = true
            //latch.countDown()
        }

        UUThread.safeSleep("test", 100L)
        Assert.assertEquals(1, UUTimer.listActiveTimers().size)
        val t = UUTimer.findActiveTimer(timerId)
        Assert.assertNotNull(t)
        t?.cancel()

        //latch.await()

//        Assert.assertNotNull(callbackTimer)
//        Assert.assertNull(callbackUserInfo)
//        Assert.assertTrue(didInvoke)
//        Assert.assertTrue((invokeTime - start) >= interval)

        // A single shot timer gets cleaned up after the timer block is invoked, so we need
        // to wait.  This is a hacky way to test, but works in this simple case
        UUThread.safeSleep("unit_test", 100)

        Assert.assertEquals(0, UUTimer.listActiveTimers().size)
        Assert.assertNull(UUTimer.findActiveTimer(timerId))
    }
}