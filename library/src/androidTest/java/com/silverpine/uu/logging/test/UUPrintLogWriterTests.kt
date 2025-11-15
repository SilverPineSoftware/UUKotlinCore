package com.silverpine.uu.logging.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.silverpine.uu.logging.UULogLevel
import com.silverpine.uu.logging.UUPrintLogWriter
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.io.PrintStream

@RunWith(AndroidJUnit4::class)
class UUPrintLogWriterTests
{
    private lateinit var writer: UUPrintLogWriter
    private var originalOut: PrintStream? = null
    private var capturedOut: ByteArrayOutputStream? = null

    @Before
    fun setUp()
    {
        writer = UUPrintLogWriter()
        // Capture stdout for verification
        originalOut = System.out
        capturedOut = ByteArrayOutputStream()
        System.setOut(PrintStream(capturedOut))
    }

    @After
    fun tearDown()
    {
        // Restore original stdout
        originalOut?.let { System.setOut(it) }
    }

    @Test
    fun writeToLog_verbose_outputsCorrectly()
    {
        writer.writeToLog(UULogLevel.VERBOSE, "TestTag", "Verbose message")
        
        val output = capturedOut.toString()
        assertTrue("Output should contain VERBOSE", output.contains("VERBOSE"))
        assertTrue("Output should contain tag", output.contains("TestTag"))
        assertTrue("Output should contain message", output.contains("Verbose message"))
    }

    @Test
    fun writeToLog_debug_outputsCorrectly()
    {
        writer.writeToLog(UULogLevel.DEBUG, "TestTag", "Debug message")
        
        val output = capturedOut.toString()
        assertTrue("Output should contain DEBUG", output.contains("DEBUG"))
        assertTrue("Output should contain tag", output.contains("TestTag"))
        assertTrue("Output should contain message", output.contains("Debug message"))
    }

    @Test
    fun writeToLog_info_outputsCorrectly()
    {
        writer.writeToLog(UULogLevel.INFO, "TestTag", "Info message")
        
        val output = capturedOut.toString()
        assertTrue("Output should contain INFO", output.contains("INFO"))
        assertTrue("Output should contain tag", output.contains("TestTag"))
        assertTrue("Output should contain message", output.contains("Info message"))
    }

    @Test
    fun writeToLog_warn_outputsCorrectly()
    {
        writer.writeToLog(UULogLevel.WARN, "TestTag", "Warning message")
        
        val output = capturedOut.toString()
        assertTrue("Output should contain WARN", output.contains("WARN"))
        assertTrue("Output should contain tag", output.contains("TestTag"))
        assertTrue("Output should contain message", output.contains("Warning message"))
    }

    @Test
    fun writeToLog_error_outputsCorrectly()
    {
        writer.writeToLog(UULogLevel.ERROR, "TestTag", "Error message")
        
        val output = capturedOut.toString()
        assertTrue("Output should contain ERROR", output.contains("ERROR"))
        assertTrue("Output should contain tag", output.contains("TestTag"))
        assertTrue("Output should contain message", output.contains("Error message"))
    }

    @Test
    fun writeToLog_fatal_outputsCorrectly()
    {
        writer.writeToLog(UULogLevel.FATAL, "TestTag", "Fatal message")
        
        val output = capturedOut.toString()
        assertTrue("Output should contain FATAL", output.contains("FATAL"))
        assertTrue("Output should contain tag", output.contains("TestTag"))
        assertTrue("Output should contain message", output.contains("Fatal message"))
    }

    @Test
    fun writeToLog_allLevels_outputsCorrectly()
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
            capturedOut?.reset()
            writer.writeToLog(level, "TestTag", "Message for ${level.name}")
            
            val output = capturedOut.toString()
            assertTrue("Output should contain ${level.name}", output.contains(level.name))
            assertTrue("Output should contain tag", output.contains("TestTag"))
            assertTrue("Output should contain message", output.contains("Message for ${level.name}"))
        }
    }

    @Test
    fun writeToLog_emptyMessage_outputsCorrectly()
    {
        writer.writeToLog(UULogLevel.INFO, "TestTag", "")
        
        val output = capturedOut.toString()
        assertTrue("Output should contain INFO", output.contains("INFO"))
        assertTrue("Output should contain tag", output.contains("TestTag"))
        // Empty message should still produce output
        assertNotNull("Output should not be null", output)
    }

    @Test
    fun writeToLog_emptyTag_outputsCorrectly()
    {
        writer.writeToLog(UULogLevel.INFO, "", "Test message")
        
        val output = capturedOut.toString()
        assertTrue("Output should contain INFO", output.contains("INFO"))
        assertTrue("Output should contain message", output.contains("Test message"))
    }

    @Test
    fun writeToLog_specialCharacters_outputsCorrectly()
    {
        val specialMessage = "Hello! @#\$%^&*()_+-=[]{}|;':\",./<>?"
        writer.writeToLog(UULogLevel.INFO, "TestTag", specialMessage)
        
        val output = capturedOut.toString()
        assertTrue("Output should contain special characters", output.contains(specialMessage))
    }

    @Test
    fun writeToLog_unicodeCharacters_outputsCorrectly()
    {
        val unicodeMessage = "Hello 世界 🌍 🚀"
        writer.writeToLog(UULogLevel.INFO, "TestTag", unicodeMessage)
        
        val output = capturedOut.toString()
        assertTrue("Output should contain unicode characters", output.contains(unicodeMessage))
    }

    @Test
    fun writeToLog_longMessage_outputsCorrectly()
    {
        val longMessage = "A".repeat(5000)
        writer.writeToLog(UULogLevel.INFO, "TestTag", longMessage)
        
        val output = capturedOut.toString()
        assertTrue("Output should contain long message", output.contains(longMessage))
    }

    @Test
    fun writeToLog_veryLongMessage_outputsCorrectly()
    {
        val veryLongMessage = "B".repeat(20000)
        writer.writeToLog(UULogLevel.INFO, "TestTag", veryLongMessage)
        
        val output = capturedOut.toString()
        assertTrue("Output should contain very long message", output.contains(veryLongMessage))
    }

    @Test
    fun writeToLog_multipleMessages_outputsAll()
    {
        for (i in 1..5)
        {
            writer.writeToLog(UULogLevel.INFO, "TestTag", "Message $i")
        }
        
        val output = capturedOut.toString()
        for (i in 1..5)
        {
            assertTrue("Output should contain Message $i", output.contains("Message $i"))
        }
    }

    @Test
    fun writeToLog_newlinesInMessage_outputsCorrectly()
    {
        val messageWithNewlines = "Line 1\nLine 2\nLine 3"
        writer.writeToLog(UULogLevel.INFO, "TestTag", messageWithNewlines)
        
        val output = capturedOut.toString()
        assertTrue("Output should contain newlines", output.contains(messageWithNewlines))
    }

    @Test
    fun writeToLog_offLevel_outputsCorrectly()
    {
        writer.writeToLog(UULogLevel.OFF, "TestTag", "Should log")
        
        val output = capturedOut.toString()
        assertTrue("Output should contain OFF", output.contains("OFF"))
        assertTrue("Output should contain message", output.contains("Should log"))
    }

    @Test
    fun writeToLog_differentTags_outputsCorrectly()
    {
        val tags = listOf("Tag1", "Tag2", "Tag3", "MyClass", "SomeService")
        
        for (tag in tags)
        {
            capturedOut?.reset()
            writer.writeToLog(UULogLevel.INFO, tag, "Message for $tag")
            
            val output = capturedOut.toString()
            assertTrue("Output should contain tag $tag", output.contains(tag))
        }
    }

    @Test
    fun writeToLog_formatting_containsTimestamp()
    {
        writer.writeToLog(UULogLevel.INFO, "TestTag", "Test message")
        
        val output = capturedOut.toString()
        // Should contain timestamp format (RFC 3339 with millis)
        // Format: YYYY-MM-DDTHH:mm:ss.SSS with optional timezone
        // Example: 2025-10-23T20:50:48.466-0700 or 2025-10-23T20:50:48.466Z
        // Check for the basic timestamp pattern: YYYY-MM-DDTHH:mm:ss.SSS
        val timestampPattern = Regex(".*\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}.*", RegexOption.DOT_MATCHES_ALL)
        assertTrue("Output should contain timestamp. Actual output: '$output'", timestampPattern.matches(output))
    }

    @Test
    fun writeToLog_formatting_hasCorrectStructure()
    {
        writer.writeToLog(UULogLevel.INFO, "TestTag", "Test message")
        
        val output = capturedOut.toString().trim()
        // Should have structure: timestamp LEVEL tag message
        val parts = output.split(" ")
        assertTrue("Output should have at least 4 parts", parts.size >= 4)
        assertTrue("Should contain level", output.contains("INFO"))
        assertTrue("Should contain tag", output.contains("TestTag"))
        assertTrue("Should contain message", output.contains("Test message"))
    }
}

