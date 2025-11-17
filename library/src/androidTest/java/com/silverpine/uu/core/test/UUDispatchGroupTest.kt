package com.silverpine.uu.core.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.silverpine.uu.core.UUDispatchGroup
import com.silverpine.uu.core.uuDispatch
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
class UUDispatchGroupTest
{
    @Test
    fun test_0000_testGroups()
    {
        val count = 10
        val loops = 50
        val sleep = 10L

        val checks = Array(count)
        {
            false
        }

        val latch = CountDownLatch(1)
        val group = UUDispatchGroup()
        {
            latch.countDown()
        }

        for (id in 0 until count)
        {
            group.enter()
            uuDispatch()
            {
                //UULog.d(javaClass, "test", "Block $id starting")

                for (loop in 0 until loops)
                {
                    //UULog.d(javaClass, "test", "Block_${id}_loop_$loop sleeping")
                    uuSleep(sleep)
                }

                //UULog.d(javaClass, "test", "Block $id finished")
                checks[id] = true
                group.leave()
            }
        }

        latch.await()

        checks.forEachIndexed()
        { index, flag ->
            Assert.assertTrue("ID $index was false", flag)
        }
    }
}