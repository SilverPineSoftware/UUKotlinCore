package com.silverpine.uu.core.test

import com.silverpine.uu.core.UUTimer
import com.silverpine.uu.core.UUTimerThread
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class UUTimerTests
{
    // A manual worker that queues runnables; tests trigger them explicitly
    private class ManualWorkerThread : UUTimerThread
    {
        private val queue = mutableListOf<Runnable>()

        override fun postDelayed(interval: Long, runnable: Runnable)
        {
            queue.add(runnable)
        }

        override fun remove(runnable: Runnable)
        {
            queue.remove(runnable)
        }

        fun queuedCount(): Int = queue.size

        fun triggerOne(): Boolean
        {
            if (queue.isEmpty()) return false
            val r = queue.removeAt(0)
            r.run()
            return true
        }

        fun triggerAll()
        {
            val copy = queue.toList()
            queue.clear()
            copy.forEach { it.run() }
        }
    }

    private lateinit var manual: ManualWorkerThread
    private var originalWorker: UUTimerThread? = null

    @BeforeEach
    fun setUp()
    {
        originalWorker = UUTimer.workerThread
        manual = ManualWorkerThread()
        UUTimer.workerThread = manual
    }

    @AfterEach
    fun tearDown()
    {
        UUTimer.workerThread = originalWorker ?: UUTimer.workerThread
        // Clean up any active timers if a previous test failed midway
        UUTimer.listActiveTimers().forEach { it.cancel() }
    }

    @Test
    fun start_nonRepeating_schedules_and_fires_once_then_removes_from_active()
    {
        val fired = CountDownLatch(1)
        val timer = UUTimer(
            timerId = "once",
            interval = 100,
            repeat = false,
            userInfo = null
        ) { _, _ -> fired.countDown() }

        timer.start()

        // It should be active and queued once
        assertNotNull(UUTimer.findActiveTimer("once"), "timer should be active after start")
        assertEquals(1, manual.queuedCount(), "expected exactly one scheduled runnable")

        // lastFireTime should be set on start
        assertTrue(timer.lastFireTime > 0, "lastFireTime should be set")

        // Fire it
        assertTrue(manual.triggerOne(), "expected to trigger queued runnable")
        assertTrue(fired.await(1, TimeUnit.SECONDS), "callback did not fire")

        // Non-repeating should cancel itself and be removed
        assertNull(UUTimer.findActiveTimer("once"), "non-repeating timer should remove itself after firing")
        assertEquals(0, manual.queuedCount(), "no runnables should remain queued")
    }

    @Test
    fun cancel_before_fire_prevents_callback_and_clears_active()
    {
        val fired = CountDownLatch(1)
        val timer = UUTimer("cancel-me", 100, false, null) { _, _ -> fired.countDown() }

        timer.start()
        assertNotNull(UUTimer.findActiveTimer("cancel-me"), "timer should be active after start")
        assertEquals(1, manual.queuedCount(), "expected one runnable scheduled")

        timer.cancel()

        // Should not fire even if we try to trigger all
        manual.triggerAll()
        assertFalse(fired.await(200, TimeUnit.MILLISECONDS), "callback should not fire after cancel")
        assertNull(UUTimer.findActiveTimer("cancel-me"), "timer should be removed from active on cancel")
        assertEquals(0, manual.queuedCount(), "queue should be empty after cancel removes runnable")
    }

    @Test
    fun repeating_timer_reschedules_until_cancel()
    {
        val count = AtomicInteger(0)
        val timer = UUTimer("repeat", 50, true, null) { _, _ -> count.incrementAndGet() }

        timer.start()

        // First schedule happens on start
        assertEquals(1, manual.queuedCount(), "first tick should be scheduled")

        // Trigger first tick
        assertTrue(manual.triggerOne(), "expected first tick to trigger")
        assertEquals(1, count.get(), "after first tick count should be 1")

        // Repeats should schedule again
        assertEquals(1, manual.queuedCount(), "repeat should schedule next tick")

        // Trigger second tick
        assertTrue(manual.triggerOne(), "expected second tick to trigger")
        assertEquals(2, count.get(), "after second tick count should be 2")

        // Cancel and ensure no more firings even if we try to trigger
        timer.cancel()
        manual.triggerAll()
        assertEquals(2, count.get(), "no further ticks should occur after cancel")
        assertNull(UUTimer.findActiveTimer("repeat"), "repeating timer should be removed after cancel")
    }

    @Test
    fun startTimer_companion_starts_and_fires_then_clears()
    {
        val fired = CountDownLatch(1)

        UUTimer.startTimer(
            timerId = "startTimerId",
            timeoutMilliseconds = 123,
            userInfo = "ctx"
        ) { t, info ->
            // Validate we get the same user info we passed
            assertEquals("ctx", info, "userInfo mismatch")
            // Validate the timer we receive matches the active one at fire time
            assertEquals("startTimerId", t.timerId, "timerId mismatch")
            fired.countDown()
        }

        // Should exist and be queued once
        assertNotNull(UUTimer.findActiveTimer("startTimerId"), "startTimer should register an active timer")
        assertEquals(1, manual.queuedCount(), "expected one runnable scheduled")

        // Fire
        assertTrue(manual.triggerOne(), "expected to trigger startTimer runnable")
        assertTrue(fired.await(1, TimeUnit.SECONDS), "startTimer callback did not fire")

        // Non-repeating: should be gone
        assertNull(UUTimer.findActiveTimer("startTimerId"), "timer should be cleared after firing once")
        assertEquals(0, manual.queuedCount(), "no runnables should remain queued")
    }

    @Test
    fun cancelActiveTimer_stops_pending_execution()
    {
        val fired = CountDownLatch(1)

        UUTimer.startTimer("to-cancel", 250, null) { _, _ -> fired.countDown() }
        assertNotNull(UUTimer.findActiveTimer("to-cancel"), "timer should be active after start")
        assertEquals(1, manual.queuedCount(), "expected one runnable scheduled")

        UUTimer.cancelActiveTimer("to-cancel")

        // Try to fire anything remaining
        manual.triggerAll()
        assertFalse(fired.await(200, TimeUnit.MILLISECONDS), "callback should not fire after cancelActiveTimer")
        assertNull(UUTimer.findActiveTimer("to-cancel"), "timer should be removed from active after cancelActiveTimer")
        assertEquals(0, manual.queuedCount(), "queue should be empty after cancel")
    }

    @Test
    fun listActiveTimers_reflects_add_and_remove()
    {
        val t1 = UUTimer("a", 100, false, null) { _, _ -> }
        val t2 = UUTimer("b", 100, false, null) { _, _ -> }

        t1.start()
        t2.start()
        assertEquals(2, UUTimer.listActiveTimers().size, "expected two active timers")

        // Fire both; they are non-repeating, so they should clear
        manual.triggerAll()
        assertEquals(0, UUTimer.listActiveTimers().size, "expected zero active timers after both fire once")
    }
}