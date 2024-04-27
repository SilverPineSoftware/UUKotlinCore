package com.silverpine.uu.core

import java.util.Locale

open class UUMetric(val name: String, val group: String = "")
{
    open fun reset()
    {
    }
}

class UUTimedMetric(name: String, group: String = ""): UUMetric(name, group)
{
    var startTime: Long = 0
        private set

    var endTime: Long = 0
        private set

    val duration: Long

        get() = if (startTime != 0L && endTime != 0L)
        {
            endTime - startTime
        }
        else
        {
            0
        }

    fun start(force: Boolean = true)
    {
        if (startTime == 0L || force)
        {
            startTime = System.currentTimeMillis()
        }
    }

    fun end(force: Boolean = true)
    {
        if (endTime == 0L || force)
        {
            endTime = System.currentTimeMillis()
        }
    }

    override fun reset()
    {
        startTime = 0
        endTime = 0
    }

    override fun toString(): String
    {
        return String.format(Locale.US, "name: %s, group: %s, duration: %d", name, group, duration)
    }
}

class UUCountMetric(name: String, group: String = ""): UUMetric(name, group)
{
    var count: Long = 0
        private set

    fun incrementCount(amount: Long = 1L)
    {
        count += amount
    }

    override fun reset()
    {
        count = 0
    }

    override fun toString(): String
    {
        return String.format(Locale.US, "name: %s, group: %s, count: %d", name, group, count)
    }
}


class UUTimedBlockMetric(name: String, group: String = ""): UUMetric(name, group)
{
    private val block: UUTimedMetric = UUTimedMetric("${name}_block_timer", group)
    private val sumMetric: UUCountMetric = UUCountMetric("${name}_sum_metric", group)
    private val countMetric: UUCountMetric = UUCountMetric("${name}_count_metric", group)

    fun startBlock()
    {
        block.reset()
        block.start()
    }

    fun endBlock(increment: Long = 1L)
    {
        block.end()
        sumMetric.incrementCount(block.duration)
        countMetric.incrementCount(increment)
        block.reset()
    }

    val count: Long
        get()
        {
            return countMetric.count
        }

    val duration: Long
        get()
        {
            return sumMetric.count
        }

    val blockAverage: Double

        get()
        {
            var avg = 0.0
            if (count > 0)
            {
                avg = duration.toDouble() / count.toDouble()
            }

            return avg
        }

    override fun reset()
    {
        block.reset()
        sumMetric.reset()
        countMetric.reset()
    }

    override fun toString(): String
    {
        return String.format(Locale.US, "name: %s, group: %s, count: %d, duration: %d, blockAverage: %f", name, group, count, duration, blockAverage)
    }
}