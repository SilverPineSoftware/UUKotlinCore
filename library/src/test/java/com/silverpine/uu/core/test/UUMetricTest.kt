package com.silverpine.uu.core.test

import com.silverpine.uu.core.UUCountMetric
import com.silverpine.uu.core.UUTimedBlockMetric
import com.silverpine.uu.core.UUTimedMetric
import com.silverpine.uu.core.uuSleep
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UUMetricTest
{
    @Test
    fun testTimedMetric()
    {
        val sleepTime = 1234L

        val m = UUTimedMetric("timed_metric", "unit_test")
        assertEquals("timed_metric", m.name)
        assertEquals("unit_test", m.group)

        m.start()

        uuSleep(sleepTime)

        m.end()

        assertTrue(m.duration >= sleepTime)
        assertTrue(m.endTime > m.startTime)
        println(m.toString())
    }

    @Test
    fun testCountMetric()
    {
        val iterations = 57L

        val m = UUCountMetric("count_metric", "unit_test")
        assertEquals("count_metric", m.name)
        assertEquals("unit_test", m.group)

        for (i in 0..<iterations)
        {
            m.incrementCount()
        }

        assertEquals(iterations, m.count)
        println(m.toString())
    }

    @Test
    fun testSumMetric()
    {
        val iterations = 5L
        val amount = 3L

        val m = UUCountMetric("sum_metric", "unit_test")
        assertEquals("sum_metric", m.name)
        assertEquals("unit_test", m.group)

        for (i in 0..<iterations)
        {
            m.incrementCount(amount)
        }

        assertEquals(iterations * amount, m.count)
        println(m.toString())
    }

    @Test
    fun testTimedBlockMetric()
    {
        val sleepTime = 123L
        val iterations = 7L

        val m = UUTimedBlockMetric("block_count_metric", "unit_test")
        assertEquals("block_count_metric", m.name)
        assertEquals("unit_test", m.group)

        for (i in 0..<iterations)
        {
            uuSleep(sleepTime)
            uuSleep(sleepTime)
            m.startBlock()
            uuSleep(sleepTime)
            m.endBlock()

            uuSleep(sleepTime)
        }

        assertTrue(m.duration >= (sleepTime * iterations))
        assertEquals(iterations, m.count)
        assertTrue(m.blockAverage >= sleepTime.toFloat())
        println(m.toString())
    }
}