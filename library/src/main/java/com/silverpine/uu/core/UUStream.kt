package com.silverpine.uu.core

import android.text.TextUtils
import com.silverpine.uu.logging.UULog
import com.silverpine.uu.logging.logException
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipInputStream

private const val LOG_TAG = "UUStream"

fun InputStream.uuCopyTo(outputStream: OutputStream, bufferSize: Int = 10240)
{
    try
    {
        outputStream.use()
        { os ->

            val buffer = ByteArray(bufferSize)
            var length = 1

            while (length > 0)
            {
                length = read(buffer)

                if (length > 0)
                {
                    os.write(buffer, 0, length)
                }
            }
        }
    }
    catch (ex: Exception)
    {
        UULog.logException(LOG_TAG, "uuCopyTo", ex)
    }
}

fun InputStream.uuUnzip(destinationFolder: Path)
{
    try
    {
        val bos = BufferedInputStream(this)
        ZipInputStream(bos).use()
        { zis ->

            val entries = generateSequence { zis.nextEntry }.filterNot { it.isDirectory }

            for (entry in entries)
            {
                val file = destinationFolder.resolve(entry.name).toFile()
                val pathParts = TextUtils.join("/", entry.name.split("/").dropLast(1))
                Files.createDirectories(destinationFolder.resolve(pathParts))

                val fos = FileOutputStream(file)
                zis.uuCopyTo(fos)
                zis.closeEntry()
            }
        }
    }
    catch (ex: Exception)
    {
        UULog.logException(LOG_TAG, "uuUnzip", ex)
    }
}

fun InputStream.uuReadAll(chunkSize: Int = 10240): ByteArray?
{
    var result: ByteArray? = null

    try
    {
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

            result = os.toByteArray()
        }
    }
    catch (ex: Exception)
    {
        UULog.logException(LOG_TAG, "uuReadAll", ex)
    }

    return result
}