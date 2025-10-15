package com.silverpine.uu.core.test

import com.silverpine.uu.core.uuFromBase64
import com.silverpine.uu.core.uuHasLowercase
import com.silverpine.uu.core.uuHasNumber
import com.silverpine.uu.core.uuHasSymbol
import com.silverpine.uu.core.uuHasUppercase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.Base64
import kotlin.test.assertTrue

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
            assertEquals(input.first.uuHasNumber(), input.second)
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
            assertEquals(input.first.uuHasUppercase(), input.second)
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
            assertEquals(input.first.uuHasLowercase(), input.second)
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
            assertEquals(input.first.uuHasSymbol(), input.second)
        }
    }

    @Test
    fun `uuToBase64Bytes validBase64String_decodesSuccessfully`()
    {
        val original = "Hello, world!"
        val encoded = Base64.getEncoder().encodeToString(original.toByteArray())

        val result = encoded.uuFromBase64()

        assertTrue(result.isSuccess)
        val decoded = result.getOrNull()
        assertNotNull(decoded)
        assertEquals(original, String(decoded!!))
    }

    @Test
    fun `uuToBase64Bytes invalidBase64String_returnsFailure`()
    {
        val invalid = "Not@@Base64!!"

        val result = invalid.uuFromBase64()

        assertTrue(result.isFailure)
        assertThrows(IllegalArgumentException::class.java) {
            result.getOrThrow()
        }
    }

    @Test
    fun `uuToBase64Bytes emptyString_decodesToEmptyByteArray`()
    {
        val encodedEmpty = "" // Base64 decoder treats empty string as empty array

        val result = encodedEmpty.uuFromBase64()

        assertTrue(result.isSuccess)
        val decoded = result.getOrNull()
        assertNotNull(decoded)
        assertEquals(0, decoded!!.size)
    }

    @Test
    fun `uuToBase64Bytes customDecoder_canBeInjected`()
    {
        val original = "Hi"
        val encoded = Base64.getEncoder().encodeToString(original.toByteArray())

        // Use a custom decoder (still the standard one, but proves injection works)
        val result = encoded.uuFromBase64(Base64.getDecoder())

        assertTrue(result.isSuccess)
        assertEquals(original, String(result.getOrThrow()))
    }
}