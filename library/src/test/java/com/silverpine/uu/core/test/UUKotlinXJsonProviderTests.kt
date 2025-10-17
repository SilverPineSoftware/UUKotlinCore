package com.silverpine.uu.core.test

import com.silverpine.uu.core.UUJsonProvider
import com.silverpine.uu.core.UUKotlinXJsonProvider
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.InputStream

class UUKotlinXJsonProviderTest
{
    private lateinit var provider: UUJsonProvider

    @BeforeEach
    fun setUp()
    {
        provider = UUKotlinXJsonProvider(Json)
    }

    @Serializable
    data class Person(val name: String, val age: Int)

    private val person = Person("Alice", 30)
    private val jsonString = """{"name":"Alice","age":30}"""

    @Test
    fun `toJson should serialize object to JSON string`()
    {
        val result = provider.toJson(person, Person::class.java)
        assertTrue(result.isSuccess)
        assertEquals(jsonString, result.getOrNull())
    }

    @Test
    fun `fromString should deserialize JSON string to object`()
    {
        val result = provider.fromString(jsonString, Person::class.java)
        assertTrue(result.isSuccess)
        assertEquals(person, result.getOrNull())
    }

    @Test
    fun `fromStream should deserialize JSON from InputStream`()
    {
        val stream: InputStream = ByteArrayInputStream(jsonString.toByteArray())
        val result = provider.fromStream(stream, Person::class.java)
        assertTrue(result.isSuccess)
        assertEquals(person, result.getOrNull())
    }

    @Test
    fun `fromBytes should deserialize JSON from ByteArray`()
    {
        val bytes = jsonString.toByteArray()
        val result = provider.fromBytes(bytes, Person::class.java)
        assertTrue(result.isSuccess)
        assertEquals(person, result.getOrNull())
    }

    @Test
    fun `fromString should fail on invalid JSON`()
    {
        val invalidJson = """{"name":"Alice","age":"notAnInt"}"""
        val result = provider.fromString(invalidJson, Person::class.java)
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun `toJson should fail on non-serializable object`()
    {
        class NonSerializable(val data: String)
        val result = provider.toJson(NonSerializable("oops"), NonSerializable::class.java)
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }
}