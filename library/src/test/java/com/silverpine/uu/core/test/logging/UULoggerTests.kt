package com.silverpine.uu.core.test.logging

import com.silverpine.uu.core.UUDate
import com.silverpine.uu.core.uuFormatDate
import com.silverpine.uu.logging.UULogLevel
import com.silverpine.uu.logging.UULogWriter
import com.silverpine.uu.logging.UULogger
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.Date

private class MockLogWriter : UULogWriter
{
    var capturedLogLine: String? = null

    override fun writeToLog(level: UULogLevel, tag: String, message: String)
    {
        capturedLogLine = formatLogLine(level, tag, message)
    }

    private fun formatLogLine(level: UULogLevel, tag: String, message: String): String
    {
        val timestamp = Date().uuFormatDate(UUDate.Formats.RFC_3339_WITH_MILLIS, UUDate.TimeZones.LOCAL)
        return "$timestamp ${level.name} $tag $message"
    }
}

private class MockLogger : UULogger
{
    val mockWriter: MockLogWriter = logWriter as MockLogWriter

    constructor() : super(MockLogWriter())

    val capturedLogLine: String?
        get() = mockWriter.capturedLogLine

    fun reset()
    {
        mockWriter.capturedLogLine = null
    }
}

private data class TestInput(
    val level: UULogLevel,
    val tag: String,
    val message: String,
    val expectLogged: Boolean = true
)
{
    fun assertLogged(logger: MockLogger)
    {
        assertNotNull(logger.capturedLogLine, "Log line should not be null")
        assertTrue(logger.capturedLogLine!!.contains(level.name), "Log line should contain level ${level.name}")
        assertTrue(logger.capturedLogLine!!.contains(tag), "Log line should contain tag $tag")
        assertTrue(logger.capturedLogLine!!.contains(message), "Log line should contain message $message")
    }

    fun assertNotLogged(logger: MockLogger)
    {
        assertNull(logger.capturedLogLine, "Log line should be null")
    }

    fun doTest(logger: MockLogger)
    {
        logger.reset()
        logger.writeToLog(level, tag, message)

        if (expectLogged)
        {
            assertLogged(logger)
        }
        else
        {
            assertNotLogged(logger)
        }
    }
}

class UULoggerTests
{
    private val logger = MockLogger()

    @Test
    fun testConsoleLogger_verbose()
    {
        logger.logLevel = UULogLevel.VERBOSE

        val td = listOf(
            TestInput(level = UULogLevel.VERBOSE, tag = "UnitTest", message = "Init Here", expectLogged = true),
            TestInput(level = UULogLevel.DEBUG, tag = "UnitTest", message = "Hello World", expectLogged = true),
            TestInput(level = UULogLevel.INFO, tag = "MyClass", message = "I Live In a Van Down by the River", expectLogged = true),
            TestInput(level = UULogLevel.WARN, tag = "SomeService", message = "Whatever's Clever", expectLogged = true),
            TestInput(level = UULogLevel.ERROR, tag = "MyClass", message = "I Live In a Van Down by the River", expectLogged = true),
            TestInput(level = UULogLevel.FATAL, tag = "YoYo", message = "I like lamp", expectLogged = true),
        )

        for (input in td)
        {
            input.doTest(logger)
        }
    }

    @Test
    fun testConsoleLogger_debug()
    {
        logger.logLevel = UULogLevel.DEBUG

        val td = listOf(
            TestInput(level = UULogLevel.VERBOSE, tag = "UnitTest", message = "Init Here", expectLogged = false),
            TestInput(level = UULogLevel.DEBUG, tag = "UnitTest", message = "Hello World", expectLogged = true),
            TestInput(level = UULogLevel.INFO, tag = "MyClass", message = "I Live In a Van Down by the River", expectLogged = true),
            TestInput(level = UULogLevel.WARN, tag = "SomeService", message = "Whatever's Clever", expectLogged = true),
            TestInput(level = UULogLevel.ERROR, tag = "MyClass", message = "I Live In a Van Down by the River", expectLogged = true),
            TestInput(level = UULogLevel.FATAL, tag = "YoYo", message = "I like lamp", expectLogged = true),
        )

        for (input in td)
        {
            input.doTest(logger)
        }
    }

    @Test
    fun testConsoleLogger_info()
    {
        logger.logLevel = UULogLevel.INFO

        val td = listOf(
            TestInput(level = UULogLevel.VERBOSE, tag = "UnitTest", message = "Init Here", expectLogged = false),
            TestInput(level = UULogLevel.DEBUG, tag = "UnitTest", message = "Hello World", expectLogged = false),
            TestInput(level = UULogLevel.INFO, tag = "MyClass", message = "I Live In a Van Down by the River", expectLogged = true),
            TestInput(level = UULogLevel.WARN, tag = "SomeService", message = "Whatever's Clever", expectLogged = true),
            TestInput(level = UULogLevel.ERROR, tag = "MyClass", message = "I Live In a Van Down by the River", expectLogged = true),
            TestInput(level = UULogLevel.FATAL, tag = "YoYo", message = "I like lamp", expectLogged = true),
        )

        for (input in td)
        {
            input.doTest(logger)
        }
    }

    @Test
    fun testConsoleLogger_warn()
    {
        logger.logLevel = UULogLevel.WARN

        val td = listOf(
            TestInput(level = UULogLevel.VERBOSE, tag = "UnitTest", message = "Init Here", expectLogged = false),
            TestInput(level = UULogLevel.DEBUG, tag = "UnitTest", message = "Hello World", expectLogged = false),
            TestInput(level = UULogLevel.INFO, tag = "MyClass", message = "I Live In a Van Down by the River", expectLogged = false),
            TestInput(level = UULogLevel.WARN, tag = "SomeService", message = "Whatever's Clever", expectLogged = true),
            TestInput(level = UULogLevel.ERROR, tag = "MyClass", message = "I Live In a Van Down by the River", expectLogged = true),
            TestInput(level = UULogLevel.FATAL, tag = "YoYo", message = "I like lamp", expectLogged = true),
        )

        for (input in td)
        {
            input.doTest(logger)
        }
    }

    @Test
    fun testConsoleLogger_error()
    {
        logger.logLevel = UULogLevel.ERROR

        val td = listOf(
            TestInput(level = UULogLevel.VERBOSE, tag = "UnitTest", message = "Init Here", expectLogged = false),
            TestInput(level = UULogLevel.DEBUG, tag = "UnitTest", message = "Hello World", expectLogged = false),
            TestInput(level = UULogLevel.INFO, tag = "MyClass", message = "I Live In a Van Down by the River", expectLogged = false),
            TestInput(level = UULogLevel.WARN, tag = "SomeService", message = "Whatever's Clever", expectLogged = false),
            TestInput(level = UULogLevel.ERROR, tag = "MyClass", message = "I Live In a Van Down by the River", expectLogged = true),
            TestInput(level = UULogLevel.FATAL, tag = "YoYo", message = "I like lamp", expectLogged = true),
        )

        for (input in td)
        {
            input.doTest(logger)
        }
    }

    @Test
    fun testConsoleLogger_fatal()
    {
        logger.logLevel = UULogLevel.FATAL

        val td = listOf(
            TestInput(level = UULogLevel.VERBOSE, tag = "UnitTest", message = "Init Here", expectLogged = false),
            TestInput(level = UULogLevel.DEBUG, tag = "UnitTest", message = "Hello World", expectLogged = false),
            TestInput(level = UULogLevel.INFO, tag = "MyClass", message = "I Live In a Van Down by the River", expectLogged = false),
            TestInput(level = UULogLevel.WARN, tag = "SomeService", message = "Whatever's Clever", expectLogged = false),
            TestInput(level = UULogLevel.ERROR, tag = "MyClass", message = "I Live In a Van Down by the River", expectLogged = false),
            TestInput(level = UULogLevel.FATAL, tag = "YoYo", message = "I like lamp", expectLogged = true),
        )

        for (input in td)
        {
            input.doTest(logger)
        }
    }

    @Test
    fun testConsoleLogger_off()
    {
        logger.logLevel = UULogLevel.OFF

        val td = listOf(
            TestInput(level = UULogLevel.VERBOSE, tag = "UnitTest", message = "Init Here", expectLogged = false),
            TestInput(level = UULogLevel.DEBUG, tag = "UnitTest", message = "Hello World", expectLogged = false),
            TestInput(level = UULogLevel.INFO, tag = "MyClass", message = "I Live In a Van Down by the River", expectLogged = false),
            TestInput(level = UULogLevel.WARN, tag = "SomeService", message = "Whatever's Clever", expectLogged = false),
            TestInput(level = UULogLevel.ERROR, tag = "MyClass", message = "I Live In a Van Down by the River", expectLogged = false),
            TestInput(level = UULogLevel.FATAL, tag = "YoYo", message = "I like lamp", expectLogged = false),
        )

        for (input in td)
        {
            input.doTest(logger)
        }
    }

    @Test
    fun testConsoleLogger_excludeFilter()
    {
        logger.logLevel = UULogLevel.VERBOSE
        logger.excludedTags = mutableListOf("UnitTest", "SomeService")

        val td = listOf(
            TestInput(level = UULogLevel.VERBOSE, tag = "Whatever", message = "Init Here", expectLogged = true),
            TestInput(level = UULogLevel.DEBUG, tag = "UnitTest", message = "Hello World", expectLogged = false),
            TestInput(level = UULogLevel.INFO, tag = "MyClass", message = "I Live In a Van Down by the River", expectLogged = true),
            TestInput(level = UULogLevel.WARN, tag = "SomeService", message = "Whatever's Clever", expectLogged = false),
            TestInput(level = UULogLevel.ERROR, tag = "MyClass", message = "I Live In a Van Down by the River", expectLogged = true),
            TestInput(level = UULogLevel.FATAL, tag = "YoYo", message = "I like lamp", expectLogged = true),
        )

        for (input in td)
        {
            input.doTest(logger)
        }
    }

    @Test
    fun testConsoleLogger_includeFilter()
    {
        logger.logLevel = UULogLevel.VERBOSE
        logger.includedTags = mutableListOf("UnitTest", "SomeService")

        val td = listOf(
            TestInput(level = UULogLevel.VERBOSE, tag = "Whatever", message = "Init Here", expectLogged = false),
            TestInput(level = UULogLevel.DEBUG, tag = "UnitTest", message = "Hello World", expectLogged = true),
            TestInput(level = UULogLevel.INFO, tag = "MyClass", message = "I Live In a Van Down by the River", expectLogged = false),
            TestInput(level = UULogLevel.WARN, tag = "SomeService", message = "Whatever's Clever", expectLogged = true),
            TestInput(level = UULogLevel.ERROR, tag = "MyClass", message = "I Live In a Van Down by the River", expectLogged = false),
            TestInput(level = UULogLevel.FATAL, tag = "YoYo", message = "I like lamp", expectLogged = false),
        )

        for (input in td)
        {
            input.doTest(logger)
        }
    }

    @Test
    fun testConsoleLogger_mixedFilter()
    {
        logger.logLevel = UULogLevel.VERBOSE
        logger.excludedTags = mutableListOf("YoYo")
        logger.includedTags = mutableListOf("UnitTest", "SomeService")

        val td = listOf(
            TestInput(level = UULogLevel.VERBOSE, tag = "Whatever", message = "Init Here", expectLogged = false),
            TestInput(level = UULogLevel.DEBUG, tag = "UnitTest", message = "Hello World", expectLogged = true),
            TestInput(level = UULogLevel.INFO, tag = "MyClass", message = "I Live In a Van Down by the River", expectLogged = false),
            TestInput(level = UULogLevel.WARN, tag = "SomeService", message = "Whatever's Clever", expectLogged = true),
            TestInput(level = UULogLevel.ERROR, tag = "MyClass", message = "I Live In a Van Down by the River", expectLogged = false),
            TestInput(level = UULogLevel.FATAL, tag = "YoYo", message = "I like lamp", expectLogged = false),
        )

        for (input in td)
        {
            input.doTest(logger)
        }
    }

    @Test
    fun testConsoleLogger_includedAndExcluded()
    {
        logger.logLevel = UULogLevel.VERBOSE
        logger.excludedTags = mutableListOf("YoYo")
        logger.includedTags = mutableListOf("YoYo")

        val td = listOf(
            TestInput(level = UULogLevel.VERBOSE, tag = "Whatever", message = "Init Here", expectLogged = false),
            TestInput(level = UULogLevel.DEBUG, tag = "UnitTest", message = "Hello World", expectLogged = false),
            TestInput(level = UULogLevel.INFO, tag = "MyClass", message = "I Live In a Van Down by the River", expectLogged = false),
            TestInput(level = UULogLevel.WARN, tag = "SomeService", message = "Whatever's Clever", expectLogged = false),
            TestInput(level = UULogLevel.ERROR, tag = "MyClass", message = "I Live In a Van Down by the River", expectLogged = false),
            TestInput(level = UULogLevel.FATAL, tag = "YoYo", message = "I like lamp", expectLogged = false),
        )

        for (input in td)
        {
            input.doTest(logger)
        }
    }
}