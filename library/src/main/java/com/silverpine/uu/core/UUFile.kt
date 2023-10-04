package com.silverpine.uu.core

import com.silverpine.uu.logging.UULog
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isDirectory
fun Path.uuListAllFiles(): ArrayList<Path>
{
    val list = ArrayList<Path>()

    try
    {
        Files.walk(this)
            .filter { !it.isDirectory() }
            .forEach { list.add(it) }
    }
    catch (ex: Exception)
    {
        UULog.d(javaClass, "uuListAllFiles", "", ex)
    }

    return list
}

fun Path.uuDelete()
{
    uuDeleteContents()
    uuSafeDelete()
}

private fun Path.uuSafeDelete()
{
    try
    {
        Files.deleteIfExists(this)
    }
    catch (ex: Exception)
    {
        UULog.d(javaClass, "uuSafeDelete", "", ex)
    }
}

private fun Path.uuDeleteContents()
{
    try
    {
        Files.walk(this)
            .sorted(Comparator.reverseOrder())
            .forEach { it.uuSafeDelete() }
    }
    catch (ex: Exception)
    {
        UULog.d(javaClass, "uuDeleteContents", "", ex)
    }
}