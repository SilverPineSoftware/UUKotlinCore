package com.silverpine.uu.core.test

import com.silverpine.uu.core.uuToHex
import org.junit.Assert
import org.junit.Test

class UUByteArrayTest
{
    @Test
    fun test_0001_uuToHex()
    {
        val input = ByteArray(4)
        val actual = input.uuToHex()
        val expected = "00000000"
        Assert.assertEquals(expected, actual)

    }
}