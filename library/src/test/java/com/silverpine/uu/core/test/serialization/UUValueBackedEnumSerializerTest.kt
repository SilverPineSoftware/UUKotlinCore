package com.silverpine.uu.core.test.serialization

import com.silverpine.uu.core.UUValueBackedEnum
import com.silverpine.uu.core.serialization.UUValueBackedEnumSerializer
import com.silverpine.uu.core.serialization.uuValueBackedEnumSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal enum class ByteBacked(override val value: Byte) : UUValueBackedEnum<Byte>
{
    ONE(1), TEN(10), NEGATIVE(-7), MIN(Byte.MIN_VALUE), MAX(Byte.MAX_VALUE)
}

internal enum class ShortBacked(override val value: Short) : UUValueBackedEnum<Short>
{
    ONE(1), TEN(10), NEGATIVE(-7), MIN(Short.MIN_VALUE), MAX(Short.MAX_VALUE)
}

internal enum class IntBacked(override val value: Int) : UUValueBackedEnum<Int>
{
    ONE(1), TEN(10), NEGATIVE(-7), MIN(Int.MIN_VALUE), MAX(Int.MAX_VALUE)
}

internal enum class LongBacked(override val value: Long) : UUValueBackedEnum<Long>
{
    ONE(1L), TEN(10L), NEGATIVE(-7L), HIGH(4294967297L), MIN(Long.MIN_VALUE), MAX(Long.MAX_VALUE)
}

internal enum class UByteBacked(override val value: UByte) : UUValueBackedEnum<UByte>
{
    ONE(1u), TEN(10u), MAX(UByte.MAX_VALUE)
}

internal enum class UShortBacked(override val value: UShort) : UUValueBackedEnum<UShort>
{
    ONE(1u), TEN(10u), MAX(UShort.MAX_VALUE)
}

internal enum class UIntBacked(override val value: UInt) : UUValueBackedEnum<UInt>
{
    ONE(1u), TEN(10u), MAX(UInt.MAX_VALUE)
}

internal enum class ULongBacked(override val value: ULong) : UUValueBackedEnum<ULong>
{
    ONE(1u), TEN(10u), MAX(ULong.MAX_VALUE)
}

internal enum class StringBacked(override val value: String) : UUValueBackedEnum<String>
{
    ONE("first_value"), TEN("tenth_value")
}

@Serializable
internal data class BackingRecord(val code: Int, val label: String)

internal enum class RecordBacked(override val value: BackingRecord) : UUValueBackedEnum<BackingRecord>
{
    FIRST(BackingRecord(10, "first"))
}

class UUValueBackedEnumSerializerTest
{
    @Test
    fun `structured backing values use their serializer and descriptor`()
    {
        val serializer = uuValueBackedEnumSerializer(
            BackingRecord.serializer(),
            { raw -> RecordBacked.entries.firstOrNull { it.value == raw } }
        )
        val expected = """{"code":10,"label":"first"}"""
        assertEquals(expected, Json.encodeToString(serializer, RecordBacked.FIRST))
        assertEquals(RecordBacked.FIRST, Json.decodeFromString(serializer, expected))
        assertNull(Json.decodeFromString(serializer, "null"))
        assertTrue(serializer.descriptor.isNullable)
        assertEquals(BackingRecord.serializer().descriptor.kind, serializer.descriptor.kind)
    }

    private fun <N: Any, T> cases(entries: List<T>, valueSerializer: KSerializer<N>, unknown: String = "99"): List<DynamicTest>
        where T: Enum<T>, T: UUValueBackedEnum<N>
    {
        val converter: (N) -> T? = { raw -> entries.firstOrNull { it.value == raw } }
        return listOf(false, true).flatMap { useFactory ->
            listOf(null, entries.first()).flatMap { fallback ->
                val serializer: KSerializer<T?> = if (useFactory)
                {
                    uuValueBackedEnumSerializer(valueSerializer, converter, fallback)
                }
                else
                {
                    object : UUValueBackedEnumSerializer<N, T>(valueSerializer, converter, fallback) {}
                }
                val label = "${entries.first().javaClass.simpleName}, factory=$useFactory, fallback=$fallback"
                entries.map { value ->
                    DynamicTest.dynamicTest("$label, value=$value") {
                        val expected = if (value.value is String) "\"${value.value}\"" else value.value.toString()
                        assertEquals(expected, Json.encodeToString(serializer, value))
                        assertEquals(value, Json.decodeFromString(serializer, expected))
                    }
                } + listOf(
                    DynamicTest.dynamicTest("$label, null") {
                        assertEquals("null", Json.encodeToString(serializer, null))
                        assertEquals(fallback, Json.decodeFromString(serializer, "null"))
                    },
                    DynamicTest.dynamicTest("$label, unknown") {
                        // Values absent from the backing-value map use the configured fallback.
                        assertEquals(fallback, Json.decodeFromString(serializer, unknown))
                        assertEquals(fallback, Json.decodeFromString(serializer, unknown))
                    }
                )
            }
        }
    }

    @TestFactory
    fun `UByte backing values`() = cases(UByteBacked.entries, UByte.serializer())

    @TestFactory
    fun `UShort backing values`() = cases(UShortBacked.entries, UShort.serializer())

    @TestFactory
    fun `UInt backing values`() = cases(UIntBacked.entries, UInt.serializer())

    @TestFactory
    fun `ULong backing values`() = cases(ULongBacked.entries, ULong.serializer())

    @TestFactory
    fun `String backing values`() = cases(StringBacked.entries, String.serializer(), "\"unknown\"")

    @TestFactory
    fun `Byte backing values`() = cases(ByteBacked.entries, Byte.serializer())

    @TestFactory
    fun `Short backing values`() = cases(ShortBacked.entries, Short.serializer())

    @TestFactory
    fun `Int backing values`() = cases(IntBacked.entries, Int.serializer())

    @TestFactory
    fun `Long backing values`() = cases(LongBacked.entries, Long.serializer())

    @Test
    fun `numeric input reaches converter without narrowing and null skips converter`()
    {
        val received = mutableListOf<Long>()
        val serializer = uuValueBackedEnumSerializer(Long.serializer(),
            fromValue = { raw ->
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
        val serializer = uuValueBackedEnumSerializer(Int.serializer(),
            fromValue = { raw -> IntBacked.entries.firstOrNull { it.value == raw } }
        )
        assertThrows(SerializationException::class.java) {
            Json.decodeFromString(serializer, "4294967297")
        }
    }

    @TestFactory
    fun `malformed numeric input throws instead of using fallback`(): List<DynamicTest> =
        listOf("1.5", "true", "{}", "[]", "\"invalid\"", "9223372036854775808").map { input ->
            DynamicTest.dynamicTest("Malformed input=$input") {
                val serializer = uuValueBackedEnumSerializer(Long.serializer(),
                    fromValue = { fail("Converter must not run for malformed input") },
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
        val serializer = uuValueBackedEnumSerializer(Long.serializer(),
            fromValue = { throw failure },
            defaultDeserializeValue = LongBacked.ONE
        )
        assertSame(failure, assertThrows(IllegalStateException::class.java) {
            Json.decodeFromString(serializer, "10")
        })
    }
}
