package com.silverpine.uu.core.test

import com.silverpine.uu.core.uuSplitIntoChunks
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test

class UUListTests
{
    @Test
    fun `list split into equal chunks`()
    {
        val list = listOf(1, 2, 3, 4)
        val chunks = list.uuSplitIntoChunks(2)

        assertEquals(2, chunks.size)
        assertEquals(listOf(1, 2), chunks[0])
        assertEquals(listOf(3, 4), chunks[1])
    }

    @Test
    fun `list split with remainder`()
    {
        val list = listOf(1, 2, 3, 4, 5)
        val chunks = list.uuSplitIntoChunks(2)

        assertEquals(3, chunks.size)
        assertEquals(listOf(1, 2), chunks[0])
        assertEquals(listOf(3, 4), chunks[1])
        assertEquals(listOf(5), chunks[2])
    }

    @Test
    fun `list split with chunk size larger than list`()
    {
        val list = listOf(1, 2, 3)
        val chunks = list.uuSplitIntoChunks(10)

        assertEquals(1, chunks.size)
        assertEquals(listOf(1, 2, 3), chunks[0])
    }

    @Test
    fun `list split with zero or negative chunk size`()
    {
        val list = listOf(1, 2, 3)
        assertTrue(list.uuSplitIntoChunks(0).isEmpty())
        assertTrue(list.uuSplitIntoChunks(-5).isEmpty())
    }

    @Test
    fun `empty list returns empty chunks`()
    {
        val list = emptyList<Int>()
        assertTrue(list.uuSplitIntoChunks(3).isEmpty())
    }
}