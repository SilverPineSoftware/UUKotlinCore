package com.silverpine.uu.core.test.serialization

import com.silverpine.uu.core.UUNumberBackedEnum
import com.silverpine.uu.core.serialization.UUNumberBackedEnumSerializer
import com.silverpine.uu.core.serialization.uuNumberBackedEnumSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal enum class ByteBacked(override val value: Byte) : UUNumberBackedEnum<Byte>
{
    ONE(1), TEN(10), NEGATIVE(-7), MIN(Byte.MIN_VALUE), MAX(Byte.MAX_VALUE)
}

internal enum class ShortBacked(override val value: Short) : UUNumberBackedEnum<Short>
{
    ONE(1), TEN(10), NEGATIVE(-7), MIN(Short.MIN_VALUE), MAX(Short.MAX_VALUE)
}

internal enum class IntBacked(override val value: Int) : UUNumberBackedEnum<Int>
{
    ONE(1), TEN(10), NEGATIVE(-7), MIN(Int.MIN_VALUE), MAX(Int.MAX_VALUE)
}

internal enum class LongBacked(override val value: Long) : UUNumberBackedEnum<Long>
{
    ONE(1L), TEN(10L), NEGATIVE(-7L), HIGH(4294967297L), MIN(Long.MIN_VALUE), MAX(Long.MAX_VALUE)
}

class UUNumberBackedEnumSerializerTest
{
    private fun <N: Number, T> cases(entries: List<T>): List<DynamicTest>
        where T: Enum<T>, T: UUNumberBackedEnum<N>
    {
        val converter: (Long) -> T? = { raw -> entries.firstOrNull { it.value.toLong() == raw } }
        return listOf(false, true).flatMap { useFactory ->
            listOf<T?>(null, entries.first()).flatMap { fallback ->
                val serializer: KSerializer<T?> = if (useFactory)
                {
                    uuNumberBackedEnumSerializer<N, T>(converter, fallback)
                }
                else
                {
                    object : UUNumberBackedEnumSerializer<N, T>(converter, fallback) {}
                }
                val label = "${entries.first().javaClass.simpleName}, factory=$useFactory, fallback=$fallback"
                entries.map { value ->
                    DynamicTest.dynamicTest("$label, value=$value") {
                        val expected = value.value.toLong().toString()
                        assertEquals(expected, Json.encodeToString(serializer, value))
                        assertEquals(value, Json.decodeFromString(serializer, expected))
                    }
                } + listOf(
                    DynamicTest.dynamicTest("$label, null") {
                        assertEquals("null", Json.encodeToString(serializer, null))
                        assertEquals(fallback, Json.decodeFromString(serializer, "null"))
                    },
                    DynamicTest.dynamicTest("$label, unknown") {
                        // Zero would select ONE if decoding accidentally used the ordinal.
                        assertEquals(fallback, Json.decodeFromString(serializer, "0"))
                        assertEquals(fallback, Json.decodeFromString(serializer, "99"))
                    }
                )
            }
        }
    }

    @TestFactory
    fun `Byte backing values`() = cases<Byte, ByteBacked>(ByteBacked.entries)

    @TestFactory
    fun `Short backing values`() = cases<Short, ShortBacked>(ShortBacked.entries)

    @TestFactory
    fun `Int backing values`() = cases<Int, IntBacked>(IntBacked.entries)

    @TestFactory
    fun `Long backing values`() = cases<Long, LongBacked>(LongBacked.entries)

    @Test
    fun `numeric input reaches converter without narrowing and null skips converter`()
    {
        val received = mutableListOf<Long>()
        val serializer = uuNumberBackedEnumSerializer<Long, LongBacked>(
            converter = { raw ->
                received.add(raw)
                LongBacked.entries.firstOrNull { it.value == raw }
            },
            defaultDeserializeValue = LongBacked.NEGATIVE
        )
        assertEquals(LongBacked.NEGATIVE, Json.decodeFromString(serializer, "null"))
        assertTrue(received.isEmpty())
        for (value in listOf(LongBacked.HIGH, LongBacked.MIN, LongBacked.MAX))
        {
            assertEquals(value, Json.decodeFromString(serializer, value.value.toString()))
        }
        assertEquals(listOf(4294967297L, Long.MIN_VALUE, Long.MAX_VALUE), received)
    }

    @Test
    fun `out of range Int backing input does not wrap to ONE`()
    {
        val serializer = uuNumberBackedEnumSerializer<Int, IntBacked>(
            converter = { raw -> IntBacked.entries.firstOrNull { it.value.toLong() == raw } }
        )
        assertNull(Json.decodeFromString(serializer, "4294967297"))
    }

    @TestFactory
    fun `malformed numeric input throws instead of using fallback`(): List<DynamicTest> =
        listOf("1.5", "true", "{}", "[]", "\"invalid\"", "9223372036854775808").map { input ->
            DynamicTest.dynamicTest("Malformed input=$input") {
                val serializer = uuNumberBackedEnumSerializer<Long, LongBacked>(
                    converter = { fail("Converter must not run for malformed input") },
                    defaultDeserializeValue = LongBacked.ONE
                )
                assertThrows(SerializationException::class.java) {
                    Json.decodeFromString(serializer, input)
                }
            }
        }

    @Test
    fun `converter exceptions propagate instead of using fallback`()
    {
        val failure = IllegalStateException("Converter failed")
        val serializer = uuNumberBackedEnumSerializer<Long, LongBacked>(
            converter = { throw failure },
            defaultDeserializeValue = LongBacked.ONE
        )
        assertSame(failure, assertThrows(IllegalStateException::class.java) {
            Json.decodeFromString(serializer, "10")
        })
    }
}
