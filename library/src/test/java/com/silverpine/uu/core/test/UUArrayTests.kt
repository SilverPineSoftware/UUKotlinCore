package com.silverpine.uu.core.test

import com.silverpine.uu.core.uuSplitIntoChunks
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertArrayEquals

class UUArrayTests
{
    @Test
    fun `array split into equal chunks`()
    {
        val array = arrayOf("a", "b", "c", "d")
        val chunks = array.uuSplitIntoChunks(2)

        assertEquals(2, chunks.size)
        assertArrayEquals(arrayOf("a", "b"), chunks[0])
        assertArrayEquals(arrayOf("c", "d"), chunks[1])
    }

    @Test
    fun `array split with remainder`()
    {
        val array = arrayOf("a", "b", "c", "d", "e")
        val chunks = array.uuSplitIntoChunks(2)

        assertEquals(3, chunks.size)
        assertArrayEquals(arrayOf("a", "b"), chunks[0])
        assertArrayEquals(arrayOf("c", "d"), chunks[1])
        assertArrayEquals(arrayOf("e"), chunks[2])
    }

    @Test
    fun `array split with chunk size larger than array`()
    {
        val array = arrayOf("x", "y", "z")
        val chunks = array.uuSplitIntoChunks(10)

        assertEquals(1, chunks.size)
        assertArrayEquals(arrayOf("x", "y", "z"), chunks[0])
    }

    @Test
    fun `array split with zero or negative chunk size`()
    {
        val array = arrayOf(1, 2, 3)
        assertTrue(array.uuSplitIntoChunks(0).isEmpty())
        assertTrue(array.uuSplitIntoChunks(-1).isEmpty())
    }

    @Test
    fun `empty array returns empty chunks`()
    {
        val array = emptyArray<String>()
        assertTrue(array.uuSplitIntoChunks(3).isEmpty())
    }
}