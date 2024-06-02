package com.silverpine.uu.core

import com.silverpine.uu.logging.UULog
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

/**
 * Compresses the Byte Array using GZIP
 */
fun ByteArray.uuGzip(): ByteArray?
{
    var bos: ByteArrayOutputStream? = null
    var zos: GZIPOutputStream? = null
    var compressed: ByteArray? = null

    try
    {
        bos = ByteArrayOutputStream()
        zos = GZIPOutputStream(BufferedOutputStream(bos))
        zos.write(this)
        zos.close()
        compressed = bos.toByteArray()
    }
    catch (ex: Exception)
    {
        UULog.d(javaClass, "uuGzip", "", ex)
    }
    finally
    {
        zos?.close()
        bos?.close()
    }

    return compressed
}