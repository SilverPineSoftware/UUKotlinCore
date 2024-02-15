package com.silverpine.uu.core.test

import com.silverpine.uu.core.uuHasLowercase
import com.silverpine.uu.core.uuHasNumber
import com.silverpine.uu.core.uuHasSymbol
import com.silverpine.uu.core.uuHasUppercase
import org.junit.Assert
import org.junit.Test

class UUStringTest
{
    @Test
    fun test_hasNumber()
    {
        val inputs: ArrayList<Pair<String,Boolean>> = arrayListOf(
            Pair("abcd", false),
            Pair("abc1d", true),
            Pair("", false),
            Pair("1234", true),
            Pair("!@#%*%", false)
        )

        for (input in inputs)
        {
            Assert.assertEquals(input.first.uuHasNumber(), input.second)
        }
    }

    @Test
    fun test_hasUpperCase()
    {
        val inputs: ArrayList<Pair<String,Boolean>> = arrayListOf(
            Pair("abcd", false),
            Pair("abc1d", false),
            Pair("", false),
            Pair("1234", false),
            Pair("!@#%*%", false),
            Pair("Abcd", true),
            Pair("abc1D", true),
        )

        for (input in inputs)
        {
            Assert.assertEquals(input.first.uuHasUppercase(), input.second)
        }
    }

    @Test
    fun test_hasLowerCase()
    {
        val inputs: ArrayList<Pair<String,Boolean>> = arrayListOf(
            Pair("abcd", true),
            Pair("abc1d", true),
            Pair("", false),
            Pair("1234", false),
            Pair("!@#%*%", false),
            Pair("Abcd", true),
            Pair("ABC", false),
        )

        for (input in inputs)
        {
            Assert.assertEquals(input.first.uuHasLowercase(), input.second)
        }
    }

    @Test
    fun test_hasSymbol()
    {
        val inputs: ArrayList<Pair<String,Boolean>> = arrayListOf(
            Pair("abcd", false),
            Pair("abc1d", false),
            Pair("", false),
            Pair("1234", false),
            Pair("!@#%*%", true),
            Pair("Abcd", false),
            Pair("ABC", false),
            Pair("Ab+cd", true),
            Pair("-ABC", true),
        )

        for (input in inputs)
        {
            Assert.assertEquals(input.first.uuHasSymbol(), input.second)
        }
    }
}