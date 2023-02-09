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
class UUListTest
{
    @Test
    fun test_0000_uuSlice_negativeStart()
    {
        val input = listOf("1", "2", "3", "4", "5")
        val actual = input.uuSlice(-1, 1)
        Assert.assertNotNull(actual)
        Assert.assertTrue(actual.isEmpty())
    }

    @Test
    fun test_0001_uuSlice_negativeCount()
    {
        val input = listOf("1", "2", "3", "4", "5")
        val actual = input.uuSlice(0, -1)
        Assert.assertNotNull(actual)
        Assert.assertTrue(actual.isEmpty())
    }

    @Test
    fun test_0002_uuSlice_zero_count()
    {
        val input = listOf("1", "2", "3", "4", "5")
        val actual = input.uuSlice(0, 0)
        Assert.assertNotNull(actual)
        Assert.assertTrue(actual.isEmpty())
    }

    @Test
    fun test_0004_uuSlice_exact_start()
    {
        val input = listOf("1", "2", "3", "4", "5")
        val actual = input.uuSlice(0, 2)
        Assert.assertNotNull(actual)
        Assert.assertEquals(2, actual.size)
        Assert.assertEquals("1", actual[0])
        Assert.assertEquals("2", actual[1])
    }

    @Test
    fun test_0005_uuSlice_exact_end()
    {
        val input = listOf("1", "2", "3", "4", "5")
        val actual = input.uuSlice(3, 2)
        Assert.assertNotNull(actual)
        Assert.assertEquals(2, actual.size)
        Assert.assertEquals("4", actual[0])
        Assert.assertEquals("5", actual[1])
    }

    @Test
    fun test_0006_uuSlice_whole_list()
    {
        val input = listOf("1", "2", "3", "4", "5")
        val actual = input.uuSlice(0, input.size)
        Assert.assertNotNull(actual)
        Assert.assertEquals(input.size, actual.size)
        Assert.assertEquals("1", actual[0])
        Assert.assertEquals("2", actual[1])
        Assert.assertEquals("3", actual[2])
        Assert.assertEquals("4", actual[3])
        Assert.assertEquals("5", actual[4])
    }

    @Test
    fun test_0007_uuSlice_truncated_at_end()
    {
        val input = listOf("1", "2", "3", "4", "5")
        val actual = input.uuSlice(2, 4)
        Assert.assertNotNull(actual)
        Assert.assertEquals(3, actual.size)
        Assert.assertEquals("3", actual[0])
        Assert.assertEquals("4", actual[1])
        Assert.assertEquals("5", actual[2])
    }

    @Test
    fun test_0008_getOrNull_emptyArray()
    {
        val input = listOf<String>()
        val actual = input.uuGetOrNull(0)
        Assert.assertNull(actual)
    }

    @Test
    fun test_0009_getOrNull_negativeIndex()
    {
        val input = listOf<String>()
        val actual = input.uuGetOrNull(-1)
        Assert.assertNull(actual)
    }

    @Test
    fun test_0010_getOrNull_min_index()
    {
        val input = listOf("1", "2", "3")
        val actual = input.uuGetOrNull(0)
        Assert.assertNotNull(actual)
        Assert.assertEquals("1", actual)
    }

    @Test
    fun test_0011_getOrNull_max_index()
    {
        val input = listOf("1", "2", "3")
        val actual = input.uuGetOrNull(2)
        Assert.assertNotNull(actual)
        Assert.assertEquals("3", actual)
    }

    @Test
    fun test_0012_getOrNull_good()
    {
        val input = listOf("1", "2", "3")
        val actual = input.uuGetOrNull(1)
        Assert.assertNotNull(actual)
        Assert.assertEquals("2", actual)
    }

    @Test
    fun test_0013_getOrNull_outOfRange()
    {
        val input = listOf("1", "2", "3")
        val actual = input.uuGetOrNull(input.size)
        Assert.assertNull(actual)
    }

    @Test
    fun test_0014_getOrNull_outOfRange2()
    {
        val input = listOf("1", "2", "3")
        val actual = input.uuGetOrNull(57)
        Assert.assertNull(actual)
    }
}