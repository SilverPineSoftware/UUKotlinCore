package com.silverpine.uu.logging.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.silverpine.uu.core.uuSleep
import com.silverpine.uu.logging.UUConsoleLogWriter
import com.silverpine.uu.logging.UULogLevel
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UUConsoleLogWriterTests
{
    private lateinit var writer: UUConsoleLogWriter

    @Before
    fun setUp()
    {
        writer = UUConsoleLogWriter()
    }

    @After
    fun tearDown()
    {
        // Allow time for async logging to complete
        uuSleep(200)
    }

    @Test
    fun writeToLog_verbose_doesNotThrow()
    {
        // Should not throw when writing verbose log
        writer.writeToLog(UULogLevel.VERBOSE, "TestTag", "Verbose message")
        uuSleep(100)
        // If we get here without exception, test passes
        assertTrue(true)
    }

    @Test
    fun writeToLog_debug_doesNotThrow()
    {
        writer.writeToLog(UULogLevel.DEBUG, "TestTag", "Debug message")
        uuSleep(100)
        assertTrue(true)
    }

    @Test
    fun writeToLog_info_doesNotThrow()
    {
        writer.writeToLog(UULogLevel.INFO, "TestTag", "Info message")
        uuSleep(100)
        assertTrue(true)
    }

    @Test
    fun writeToLog_warn_doesNotThrow()
    {
        writer.writeToLog(UULogLevel.WARN, "TestTag", "Warning message")
        uuSleep(100)
        assertTrue(true)
    }

    @Test
    fun writeToLog_error_doesNotThrow()
    {
        writer.writeToLog(UULogLevel.ERROR, "TestTag", "Error message")
        uuSleep(100)
        assertTrue(true)
    }

    @Test
    fun writeToLog_fatal_doesNotThrow()
    {
        writer.writeToLog(UULogLevel.FATAL, "TestTag", "Fatal message")
        uuSleep(100)
        assertTrue(true)
    }

    @Test
    fun writeToLog_allLevels_doesNotThrow()
    {
        val levels = listOf(
            UULogLevel.VERBOSE,
            UULogLevel.DEBUG,
            UULogLevel.INFO,
            UULogLevel.WARN,
            UULogLevel.ERROR,
            UULogLevel.FATAL
        )

        for (level in levels)
        {
            writer.writeToLog(level, "TestTag", "Message for ${level.name}")
            uuSleep(50)
        }

        uuSleep(100)
        assertTrue(true)
    }

    @Test
    fun writeToLog_emptyMessage_doesNotThrow()
    {
        writer.writeToLog(UULogLevel.INFO, "TestTag", "")
        uuSleep(100)
        assertTrue(true)
    }

    @Test
    fun writeToLog_emptyTag_doesNotThrow()
    {
        writer.writeToLog(UULogLevel.INFO, "", "Test message")
        uuSleep(100)
        assertTrue(true)
    }

    @Test
    fun writeToLog_specialCharacters_doesNotThrow()
    {
        val specialMessage = "Hello! @#\$%^&*()_+-=[]{}|;':\",./<>?"
        writer.writeToLog(UULogLevel.INFO, "TestTag", specialMessage)
        uuSleep(100)
        assertTrue(true)
    }

    @Test
    fun writeToLog_unicodeCharacters_doesNotThrow()
    {
        val unicodeMessage = "Hello 世界 🌍 🚀"
        writer.writeToLog(UULogLevel.INFO, "TestTag", unicodeMessage)
        uuSleep(100)
        assertTrue(true)
    }

    @Test
    fun writeToLog_longMessage_doesNotThrow()
    {
        val longMessage = "A".repeat(5000)
        writer.writeToLog(UULogLevel.INFO, "TestTag", longMessage)
        uuSleep(200)
        assertTrue(true)
    }

    @Test
    fun writeToLog_veryLongMessage_doesNotThrow()
    {
        val veryLongMessage = "B".repeat(20000)
        writer.writeToLog(UULogLevel.INFO, "TestTag", veryLongMessage)
        uuSleep(300)
        assertTrue(true)
    }

    @Test
    fun writeToLog_multipleMessages_doesNotThrow()
    {
        for (i in 1..10)
        {
            writer.writeToLog(UULogLevel.INFO, "TestTag", "Message $i")
        }
        uuSleep(200)
        assertTrue(true)
    }

    @Test
    fun writeToLog_concurrentWrites_doesNotThrow()
    {
        val threads = mutableListOf<Thread>()
        
        for (i in 1..5)
        {
            val thread = Thread {
                for (j in 1..5)
                {
                    writer.writeToLog(UULogLevel.INFO, "Thread$i", "Message $j")
                }
            }
            threads.add(thread)
            thread.start()
        }

        threads.forEach { it.join() }
        uuSleep(200)
        assertTrue(true)
    }

    @Test
    fun writeToLog_newlinesInMessage_doesNotThrow()
    {
        val messageWithNewlines = "Line 1\nLine 2\nLine 3"
        writer.writeToLog(UULogLevel.INFO, "TestTag", messageWithNewlines)
        uuSleep(100)
        assertTrue(true)
    }

    @Test
    fun writeToLog_offLevel_doesNotThrow()
    {
        // OFF level should not cause issues
        writer.writeToLog(UULogLevel.OFF, "TestTag", "Should not log")
        uuSleep(100)
        assertTrue(true)
    }

    @Test
    fun writeToLog_differentTags_doesNotThrow()
    {
        val tags = listOf("Tag1", "Tag2", "Tag3", "MyClass", "SomeService")
        
        for (tag in tags)
        {
            writer.writeToLog(UULogLevel.INFO, tag, "Message for $tag")
            uuSleep(50)
        }
        
        uuSleep(100)
        assertTrue(true)
    }
}

