package com.silverpine.uu.core.test.serialization

import com.silverpine.uu.core.UUValueBackedEnum
import com.silverpine.uu.core.serialization.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal object SafeUnsignedSerializer : UUSafeValueBackedEnumSerializer<ULong, ULongBacked>(
    ULong.serializer(), { raw -> ULongBacked.entries.firstOrNull { it.value == raw } }, ULongBacked.TEN
)

@Serializable
internal data class SafeUnsignedModel(
    @Serializable(with = SafeUnsignedSerializer::class) val status: ULongBacked
)

class UUSafeValueBackedEnumSerializerTest
{
    private fun <V: Any, T> cases(values: List<T>, backing: KSerializer<V>, unknown: String): List<DynamicTest>
        where T: Enum<T>, T: UUValueBackedEnum<V> =
        listOf(false, true).flatMap { factory ->
            val lookup: (V) -> T? = { raw -> values.firstOrNull { it.value == raw } }
            val fallback = values.last()
            val serializer: KSerializer<T> = if (factory)
                uuSafeValueBackedEnumSerializer(backing, lookup, fallback)
            else object : UUSafeValueBackedEnumSerializer<V, T>(backing, lookup, fallback) {}
            val label = "${values.first().javaClass.simpleName} factory=$factory"
            values.map { value ->
                DynamicTest.dynamicTest("$label value=$value") {
                    val expected = if (value.value is String) "\"${value.value}\"" else value.value.toString()
                    assertEquals(expected, Json.encodeToString(serializer, value))
                    assertEquals(value, Json.decodeFromString(serializer, expected))
                }
            } + listOf(DynamicTest.dynamicTest("$label fallback and descriptor") {
                assertEquals(fallback, Json.decodeFromString(serializer, unknown))
                assertEquals(fallback, Json.decodeFromString(serializer, "null"))
                assertFalse(serializer.descriptor.isNullable)
                assertSame(backing.descriptor, serializer.descriptor)
            })
        }

    @TestFactory fun `Byte values`() = cases(ByteBacked.entries, Byte.serializer(), "99")
    @TestFactory fun `Short values`() = cases(ShortBacked.entries, Short.serializer(), "99")
    @TestFactory fun `Int values`() = cases(IntBacked.entries, Int.serializer(), "99")
    @TestFactory fun `Long values`() = cases(LongBacked.entries, Long.serializer(), "99")
    @TestFactory fun `UByte values`() = cases(UByteBacked.entries, UByte.serializer(), "99")
    @TestFactory fun `UShort values`() = cases(UShortBacked.entries, UShort.serializer(), "99")
    @TestFactory fun `UInt values`() = cases(UIntBacked.entries, UInt.serializer(), "99")
    @TestFactory fun `ULong values`() = cases(ULongBacked.entries, ULong.serializer(), "99")
    @TestFactory fun `String values`() = cases(StringBacked.entries, String.serializer(), "\"unknown\"")

    @Test
    fun `null skips converter and converter failures propagate`()
    {
        val failure = IllegalStateException("lookup failed")
        val serializer = uuSafeValueBackedEnumSerializer(Long.serializer(), { _: Long -> throw failure }, LongBacked.TEN)
        assertEquals(LongBacked.TEN, Json.decodeFromString(serializer, "null"))
        assertSame(failure, assertThrows(IllegalStateException::class.java) {
            Json.decodeFromString(serializer, "1")
        })
    }

    @TestFactory
    fun `malformed inputs do not use fallback`() = listOf("true", "{}", "1.5", "9223372036854775808").map { input ->
        DynamicTest.dynamicTest(input) {
            val serializer = uuSafeValueBackedEnumSerializer<Long, LongBacked>(
                Long.serializer(), { fail("Malformed input must not reach lookup") }, LongBacked.TEN
            )
            assertThrows(SerializationException::class.java) { Json.decodeFromString(serializer, input) }
        }
    }

    @Test
    fun `non nullable annotated unsigned property handles null unknown and full range`()
    {
        for (input in listOf("null", "99"))
            assertEquals(SafeUnsignedModel(ULongBacked.TEN), Json.decodeFromString<SafeUnsignedModel>("{\"status\":$input}"))
        val model = SafeUnsignedModel(ULongBacked.MAX)
        val encoded = Json.encodeToString(SafeUnsignedModel.serializer(), model)
        assertEquals("""{"status":18446744073709551615}""", encoded)
        assertEquals(model, Json.decodeFromString<SafeUnsignedModel>(encoded))
    }

    @Test
    fun `structured values retain backing representation`()
    {
        val serializer = uuSafeValueBackedEnumSerializer(BackingRecord.serializer(),
            { raw -> RecordBacked.entries.firstOrNull { it.value == raw } }, RecordBacked.FIRST)
        val json = """{"code":10,"label":"first"}"""
        assertEquals(json, Json.encodeToString(serializer, RecordBacked.FIRST))
        assertEquals(RecordBacked.FIRST, Json.decodeFromString(serializer, json))
        assertEquals(RecordBacked.FIRST, Json.decodeFromString(serializer, "null"))
    }
}
