package com.silverpine.uu.core

import android.text.TextUtils
import com.silverpine.uu.logging.UULog
import com.silverpine.uu.logging.logException
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.FilterInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipInputStream

private const val LOG_TAG = "UUStream"

/**
 * Copies the remaining bytes from this stream to [outputStream].
 *
 * Leaves both supplied streams open on success or failure, including invalid input.
 * Does not flush [outputStream]; the caller is responsible for flushing and closing it. A failure can leave partially written
 * output; bytes already consumed from this stream are not restored.
 *
 * @param outputStream The caller-owned destination stream.
 * @param bufferSize The copy buffer size in bytes. Must be greater than zero; defaults to 10 KiB.
 * @return Success with [Unit] after copying, or failure containing
 * the caught exception. A nonpositive [bufferSize] produces an [IllegalArgumentException]
 * failure. Read and write exceptions are also logged and returned as failures.
 */
fun InputStream.uuCopyTo(outputStream: OutputStream, bufferSize: Int = 10240): Result<Unit>
{
    try
    {
        require(bufferSize > 0) { "bufferSize must be greater than zero" }
        val buffer = ByteArray(bufferSize)
        var length = read(buffer)
        while (length > 0)
        {
            outputStream.write(buffer, 0, length)
            length = read(buffer)
        }

        return Result.success(Unit)
    }
    catch (ex: Exception)
    {
        UULog.logException(LOG_TAG, "uuCopyTo", ex)
        return Result.failure(ex)
    }
}

/**
 * Extracts ZIP file entries from this stream into [destinationFolder].
 *
 * Creates parent directories as needed and overwrites existing destination files.
 * Directory entries are skipped, so empty directories are not preserved. Leaves this
 * caller-owned input stream open on success or failure, while closing the ZIP wrapper
 * and all file output streams created internally. Buffering may consume bytes beyond
 * the last ZIP entry; the position of the input after extraction is not guaranteed.
 *
 * Rejects file entries whose normalized absolute paths fall outside [destinationFolder]
 * with a [java.util.zip.ZipException] failure. This is a lexical path check; it does not
 * resolve symbolic links in the destination, so use a destination with trusted contents.
 * Extraction stops on failure without removing files already created or partially written.
 *
 * An input with no recognized ZIP entries may succeed without creating any files;
 * success does not guarantee that the input was a valid ZIP archive.
 *
 * @param destinationFolder The root directory for extracted files.
 * @return Success with [Unit] after extraction and closure of internally owned streams, or failure containing
 * the caught exception from path validation, directory creation, ZIP reading, file writing,
 * or stream closure. Caught exceptions are logged.
 */
fun InputStream.uuUnzip(destinationFolder: Path): Result<Unit>
{
    try
    {
        val nonClosingInput = object : FilterInputStream(this)
        {
            override fun close() = Unit
        }
        val bos = BufferedInputStream(nonClosingInput)
        ZipInputStream(bos).use()
        { zis ->

            val entries = generateSequence { zis.nextEntry }.filterNot { it.isDirectory }

            for (entry in entries)
            {
                // Normalize paths to prevent Zip Slip vulnerability
                val destDir = destinationFolder.toAbsolutePath().normalize()
                val resolvedPath = destinationFolder.resolve(entry.name).toAbsolutePath().normalize()

                if (!resolvedPath.startsWith(destDir))
                {
                    throw java.util.zip.ZipException("ZIP entry escapes destination folder: ${entry.name}")
                }

                val pathParts = TextUtils.join("/", entry.name.split("/").dropLast(1))
                Files.createDirectories(destinationFolder.resolve(pathParts))

                FileOutputStream(resolvedPath.toFile()).use { output ->
                    zis.uuCopyTo(output).getOrThrow()
                }
                zis.closeEntry()
            }
        }

        return Result.success(Unit)
    }
    catch (ex: Exception)
    {
        UULog.logException(LOG_TAG, "uuUnzip", ex)
        return Result.failure(ex)
    }
}

/**
 * Reads the remaining bytes from this stream into a non-null byte array.
 *
 * Returns an empty array when the stream is already at EOF. Leaves this input stream
 * open on success or failure. On failure, no partial byte array is returned and bytes
 * already consumed from the stream are not restored.
 *
 * Buffers the entire result in memory; [chunkSize] controls the read buffer size,
 * not the maximum result size. Prefer [uuCopyTo] for data that should not be held in memory.
 *
 * @param chunkSize The read buffer size in bytes. Must be greater than zero; defaults to 10 KiB.
 * @return Success containing the bytes read, or failure containing the caught exception.
 * A nonpositive [chunkSize] produces an [IllegalArgumentException] failure. Caught
 * exceptions are logged. JVM errors such as [OutOfMemoryError] are not caught.
 */
fun InputStream.uuReadAll(chunkSize: Int = 10240): Result<ByteArray>
{
    try
    {
        require(chunkSize > 0) { "chunkSize must be greater than zero" }
        val outputStream = ByteArrayOutputStream()
        outputStream.use()
        { os ->

            val buffer = ByteArray(chunkSize)
            var length = 1

            while (length > 0)
            {
                length = read(buffer)

                if (length > 0)
                {
                    os.write(buffer, 0, length)
                }
            }

            return Result.success(os.toByteArray())
        }
    }
    catch (ex: Exception)
    {
        UULog.logException(LOG_TAG, "uuReadAll", ex)
        return Result.failure(ex)
    }
}
