package com.silverpine.uu.core.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.silverpine.uu.core.uuGetOrNull
import com.silverpine.uu.core.uuSlice
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UUArrayTest
{
    @Test
    fun test_0000_getOrNull_emptyArray()
    {
        val input = arrayOf<String>()
        val actual = input.uuGetOrNull(0)
        Assert.assertNull(actual)
    }

    @Test
    fun test_0001_getOrNull_negativeIndex()
    {
        val input = arrayOf<String>()
        val actual = input.uuGetOrNull(-1)
        Assert.assertNull(actual)
    }

    @Test
    fun test_0002_getOrNull_min_index()
    {
        val input = arrayOf("1", "2", "3")
        val actual = input.uuGetOrNull(0)
        Assert.assertNotNull(actual)
        Assert.assertEquals("1", actual)
    }

    @Test
    fun test_0004_getOrNull_max_index()
    {
        val input = arrayOf("1", "2", "3")
        val actual = input.uuGetOrNull(2)
        Assert.assertNotNull(actual)
        Assert.assertEquals("3", actual)
    }

    @Test
    fun test_0005_getOrNull_good()
    {
        val input = arrayOf("1", "2", "3")
        val actual = input.uuGetOrNull(1)
        Assert.assertNotNull(actual)
        Assert.assertEquals("2", actual)
    }

    @Test
    fun test_0006_getOrNull_outOfRange()
    {
        val input = arrayOf("1", "2", "3")
        val actual = input.uuGetOrNull(input.size)
        Assert.assertNull(actual)
    }

    @Test
    fun test_0006_getOrNull_outOfRange2()
    {
        val input = arrayOf("1", "2", "3")
        val actual = input.uuGetOrNull(57)
        Assert.assertNull(actual)
    }
}