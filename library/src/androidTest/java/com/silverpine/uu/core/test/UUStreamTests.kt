package com.silverpine.uu.core.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.silverpine.uu.core.uuCopyTo
import com.silverpine.uu.core.uuReadAll
import com.silverpine.uu.core.uuUnzip
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipOutputStream

@RunWith(AndroidJUnit4::class)
class UUStreamTests
{
    private class TrackedInput(bytes: ByteArray, val failRead: Boolean = false) : java.io.ByteArrayInputStream(bytes)
    {
        var closed = false
        override fun close() { closed = true; super.close() }
        override fun read(buffer: ByteArray, offset: Int, length: Int): Int
        {
            if (failRead) throw java.io.IOException("Read failed")
            return super.read(buffer, offset, length)
        }
    }

    private class TrackedOutput(val failWrite: Boolean = false) : ByteArrayOutputStream()
    {
        var closed = false
        var flushed = false
        override fun close() { closed = true; super.close() }
        override fun flush() { flushed = true; super.flush() }
        override fun write(buffer: ByteArray, offset: Int, length: Int)
        {
            if (failWrite) throw java.io.IOException("Write failed")
            super.write(buffer, offset, length)
        }
    }

    @Test
    fun copyLeavesSuppliedStreamsOpen()
    {
        for (scenario in listOf("success", "read failure", "write failure", "invalid buffer"))
        {
            val input = TrackedInput(byteArrayOf(1, 2), scenario == "read failure")
            val output = TrackedOutput(scenario == "write failure")
            val result = input.uuCopyTo(output, if (scenario == "invalid buffer") 0 else 1)
            assertEquals(scenario, scenario == "success", result.isSuccess)
            assertFalse(scenario, input.closed)
            assertFalse(scenario, output.closed)
            assertFalse(scenario, output.flushed)
            if (result.isSuccess)
            {
                byteArrayOf(3).inputStream().uuCopyTo(output).getOrThrow()
                assertArrayEquals(byteArrayOf(1, 2, 3), output.toByteArray())
            }
            input.close()
            output.close()
            assertTrue(input.closed)
            assertTrue(output.closed)
        }
    }

    @Test
    fun readLeavesSuppliedStreamOpen()
    {
        for (scenario in listOf("success", "read failure", "invalid buffer"))
        {
            val input = TrackedInput(byteArrayOf(1, 2), scenario == "read failure")
            val result = input.uuReadAll(if (scenario == "invalid buffer") 0 else 1)
            assertEquals(scenario, scenario == "success", result.isSuccess)
            assertFalse(scenario, input.closed)
            input.close()
            assertTrue(input.closed)
        }
    }

    @Test
    fun unzipLeavesSuppliedStreamOpen()
    {
        val cache = InstrumentationRegistry.getInstrumentation().targetContext.cacheDir.toPath()
        for (scenario in listOf("success", "read failure", "rejected path", "write failure"))
        {
            val root = Files.createTempDirectory(cache, "zip-ownership-")
            try
            {
                val archive = ByteArrayOutputStream()
                ZipOutputStream(archive).use { zip ->
                    zip.putNextEntry(ZipEntry(if (scenario == "rejected path") "../outside" else "file.txt"))
                    zip.write(byteArrayOf(1, 2, 3))
                    zip.closeEntry()
                }
                if (scenario == "write failure") Files.createDirectory(root.resolve("file.txt"))
                val input = TrackedInput(archive.toByteArray(), scenario == "read failure")
                val result = input.uuUnzip(root)
                assertEquals(scenario, scenario == "success", result.isSuccess)
                assertFalse(scenario, input.closed)
                if (result.isSuccess) assertArrayEquals(byteArrayOf(1, 2, 3), Files.readAllBytes(root.resolve("file.txt")))
                input.close()
                assertTrue(input.closed)
            }
            finally { root.toFile().deleteRecursively() }
        }
    }

    @Test
    fun readEmptyStreamSucceedsWithEmptyArray()
    {
        val result: Result<ByteArray> = byteArrayOf().inputStream().uuReadAll()
        assertArrayEquals(byteArrayOf(), result.getOrThrow())
    }

    @Test
    fun readFailurePreservesException()
    {
        val failure = java.io.IOException("Read failed")
        val input = object : java.io.InputStream()
        {
            override fun read(): Int = throw failure
        }
        assertSame(failure, input.uuReadAll().exceptionOrNull())
    }

    @Test
    fun copyRejectsNonPositiveBufferSizes()
    {
        for (size in listOf(0, -1))
        {
            val input = byteArrayOf(1, 2, 3).inputStream()
            val output = ByteArrayOutputStream()
            val result = input.uuCopyTo(output, size)
            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
            assertEquals(3, input.available())
            assertEquals(0, output.size())
        }
    }

    @Test
    fun readRejectsNonPositiveChunkSizes()
    {
        for (size in listOf(0, -1))
        {
            val input = byteArrayOf(1, 2, 3).inputStream()
            val result = input.uuReadAll(size)
            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
            assertEquals(3, input.available())
        }
    }

    @Test
    fun singleByteBuffersReadAndCopyAllBytes()
    {
        val bytes = byteArrayOf(1, 2, 3)
        assertArrayEquals(bytes, bytes.inputStream().uuReadAll(1).getOrThrow())
        val output = ByteArrayOutputStream()
        bytes.inputStream().uuCopyTo(output, 1).getOrThrow()
        assertArrayEquals(bytes, output.toByteArray())
    }

    @Test
    fun unzipRejectsTraversalPaths() = checkRejectedEntry { "../outside.txt" }

    @Test
    fun unzipRejectsAbsolutePaths() = checkRejectedEntry { it.resolve("outside.txt").toString() }

    private fun checkRejectedEntry(entryName: (java.nio.file.Path) -> String)
    {
        val cache = InstrumentationRegistry.getInstrumentation().targetContext.cacheDir.toPath()
        val root = Files.createTempDirectory(cache, "zip-rejection-")
        try
        {
            val destination = root.resolve("destination")
            val archive = ByteArrayOutputStream()
            ZipOutputStream(archive).use { zip ->
                zip.putNextEntry(ZipEntry(entryName(root)))
                zip.write(byteArrayOf(1, 2, 3))
                zip.closeEntry()
                zip.putNextEntry(ZipEntry("later.txt"))
                zip.write(4)
                zip.closeEntry()
            }
            val result = archive.toByteArray().inputStream().uuUnzip(destination)
            assertTrue(result.exceptionOrNull() is ZipException)
            assertFalse(Files.exists(root.resolve("outside.txt")))
            assertFalse(Files.exists(destination.resolve("later.txt")))
        }
        finally
        {
            root.toFile().deleteRecursively()
        }
    }
}
