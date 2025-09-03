package com.silverpine.uu.core.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.silverpine.uu.core.UURandom
import com.silverpine.uu.core.uuDelete
import com.silverpine.uu.core.uuToHex
import com.silverpine.uu.core.uuUnzip
import com.silverpine.uu.core.uuUtf8ByteArray
import com.silverpine.uu.test.uuPrintln
import com.silverpine.uu.test.uuRandomBytes
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.absolutePathString

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UUZipTests
{
    @Test
    fun test_0000_unzipInvalidStream()
    {
        val bytes = UURandom.bytes(1024)
        val bos = ByteArrayInputStream(bytes)

        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        val outputFolder = Paths.get("${applicationContext.noBackupFilesDir}/uu_core")
        outputFolder.uuDelete()

        // This is basically a no-op, but the output folder will not be created
        bos.uuUnzip(outputFolder)

        outputFolder.uuPrint()

        Assert.assertFalse(Files.exists(outputFolder))
    }

    @Test
    fun test_0001_unzip_1()
    {
        val zipPath = makeZip(1)

        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        val outputFolder = Paths.get("${applicationContext.noBackupFilesDir}/uu_core")
        outputFolder.uuDelete()

        val fis = FileInputStream(zipPath.toFile())
        fis.uuUnzip(outputFolder)

        outputFolder.uuPrint()

        Assert.assertTrue(Files.exists(outputFolder))
    }

    @Test
    fun test_0002_unzip_2()
    {
        val zipPath = makeZip(2)

        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        val outputFolder = Paths.get("${applicationContext.noBackupFilesDir}/uu_core")
        outputFolder.uuDelete()

        val fis = FileInputStream(zipPath.toFile())
        fis.uuUnzip(outputFolder)

        outputFolder.uuPrint()

        Assert.assertTrue(Files.exists(outputFolder))
    }

    @Test
    fun test_0003_unzip_100()
    {
        val zipPath = makeZip(100)

        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        val outputFolder = Paths.get("${applicationContext.noBackupFilesDir}/uu_core")
        outputFolder.uuDelete()

        val fis = FileInputStream(zipPath.toFile())
        fis.uuUnzip(outputFolder)

        outputFolder.uuPrint()

        Assert.assertTrue(Files.exists(outputFolder))
    }

    private fun makeZip(fileCount: Int): Path
    {
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        val outputFolder = Paths.get("${applicationContext.noBackupFilesDir}/unit_test_files")
        Files.createDirectories(outputFolder)
        val outputPath = outputFolder.resolve("random_zip_$fileCount.zip")
        val outputFile = outputPath.toFile()
        ZipOutputStream(FileOutputStream(outputFile)).use()
        {
            for (i in 0 until fileCount)
            {
                val fileData = uuRandomBytes(1024).uuToHex().uuUtf8ByteArray() // uuRandomWords(1024,30).uuUtf8ByteArray()
                val name = "random_file_$i.txt"
                it.putNextEntry(ZipEntry(name))
                it.write(fileData)
            }
        }

        return outputPath
    }
}

fun Path.uuPrint()
{
    if (Files.exists(this))
    {
        Files.walk(this)
            .forEach()
            {
                uuPrintln("uuPrint", it.absolutePathString())
            }
    }
    else
    {
        uuPrintln("uuPrint", "No folder exists at: ${absolutePathString()}")
    }
}