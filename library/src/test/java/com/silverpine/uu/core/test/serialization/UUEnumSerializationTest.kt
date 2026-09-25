package com.silverpine.uu.core.test.serialization

import com.silverpine.uu.core.serialization.UUEnumFormat
import com.silverpine.uu.core.serialization.UUEnumSerialization
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

@OptIn(ExperimentalSerializationApi::class)
class UUEnumSerializationTest
{
    @TestFactory
    fun `enum helper consumes null and applies its own fallback`(): List<DynamicTest> =
        UUEnumFormat.entries.flatMap { format ->
            listOf<TestEnum?>(null, TestEnum.SixSeven).map { fallback ->
                DynamicTest.dynamicTest("Null format=$format, fallback=$fallback") {
                    val decoder = mock<Decoder>()
                    whenever(decoder.decodeNotNullMark()).thenReturn(false)
                    assertEquals(fallback, UUEnumSerialization.deserialize(
                        decoder, format, TestEnum::class.java, fallback
                    ))
                    verify(decoder).decodeNotNullMark()
                    verify(decoder).decodeNull()
                    verifyNoMoreInteractions(decoder)
                }
            }
        }

    @TestFactory
    fun `enum helper applies fallback without wrapper assistance`(): List<DynamicTest> =
        UUEnumFormat.entries.flatMap { format ->
            listOf<TestEnum?>(null, TestEnum.SixSeven).flatMap { fallback ->
                listOf(false, true).map { known ->
                    DynamicTest.dynamicTest("Format=$format, fallback=$fallback, known=$known") {
                        val decoder = mock<Decoder>()
                        whenever(decoder.decodeNotNullMark()).thenReturn(true)
                        when (format)
                        {
                            UUEnumFormat.Name -> whenever(decoder.decodeString()).thenReturn(if (known) "Two" else "unknown")
                            UUEnumFormat.NameLower -> whenever(decoder.decodeString()).thenReturn(if (known) "two" else "unknown")
                            UUEnumFormat.NameSnakeCase -> whenever(decoder.decodeString()).thenReturn(if (known) "two" else "unknown")
                            UUEnumFormat.Ordinal -> whenever(decoder.decodeInt()).thenReturn(if (known) 1 else 99)
                        }
                        assertEquals(if (known) TestEnum.Two else fallback, UUEnumSerialization.deserialize(
                            decoder, format, TestEnum::class.java, fallback
                        ))
                        verify(decoder).decodeNotNullMark()
                        if (format == UUEnumFormat.Ordinal) verify(decoder).decodeInt()
                        else verify(decoder).decodeString()
                        verifyNoMoreInteractions(decoder)
                    }
                }
            }
        }

    @TestFactory
    fun `number backed helper consumes null and skips converter`(): List<DynamicTest> =
        listOf<LongBacked?>(null, LongBacked.TEN).map { fallback ->
            DynamicTest.dynamicTest("Number null, fallback=$fallback") {
                val decoder = mock<Decoder>()
                whenever(decoder.decodeNotNullMark()).thenReturn(false)
                assertEquals(fallback, UUEnumSerialization.deserializeValueBacked<Long, LongBacked>(
                    decoder, Long.serializer(), { fail("Null must not invoke the converter") }, fallback
                ))
                verify(decoder).decodeNotNullMark()
                verify(decoder).decodeNull()
                verifyNoMoreInteractions(decoder)
            }
        }

    @TestFactory
    fun `number backed helper matches or applies its own fallback`(): List<DynamicTest> =
        listOf<LongBacked?>(null, LongBacked.TEN).flatMap { fallback ->
            listOf(99L, Long.MIN_VALUE, Long.MAX_VALUE, LongBacked.HIGH.value).map { raw ->
                DynamicTest.dynamicTest("Number=$raw, fallback=$fallback") {
                    val decoder = mock<Decoder>()
                    whenever(decoder.decodeNotNullMark()).thenReturn(true)
                    whenever(decoder.decodeLong()).thenReturn(raw)
                    val received = mutableListOf<Long>()
                    val expected = LongBacked.entries.firstOrNull { it.value == raw } ?: fallback
                    assertEquals(expected, UUEnumSerialization.deserializeValueBacked<Long, LongBacked>(
                        decoder, Long.serializer(),
                        { input ->
                            received.add(input)
                            LongBacked.entries.firstOrNull { it.value == input }
                        },
                        fallback
                    ))
                    assertEquals(listOf(raw), received)
                    verify(decoder).decodeNotNullMark()
                    verify(decoder).decodeLong()
                    verifyNoMoreInteractions(decoder)
                }
            }
        }

    @TestFactory
    fun `enum helper encodes null for every format`(): List<DynamicTest> =
        UUEnumFormat.entries.map { format ->
            DynamicTest.dynamicTest("Encode null format=$format") {
                val encoder = mock<Encoder>()
                UUEnumSerialization.serialize<TestEnum>(encoder, format, null)
                verify(encoder).encodeNull()
                verifyNoMoreInteractions(encoder)
            }
        }

    @Test
    fun `number backed helper encodes null`()
    {
        val encoder = mock<Encoder>()
        UUEnumSerialization.serializeValueBacked<Long, LongBacked>(encoder, Long.serializer(), null)
        verify(encoder).encodeNull()
        verifyNoMoreInteractions(encoder)
    }

    @TestFactory
    fun `number backed helper encodes backing value rather than ordinal`(): List<DynamicTest> =
        LongBacked.entries.map { value ->
            DynamicTest.dynamicTest("Encode $value") {
                val encoder = mock<Encoder>()
                UUEnumSerialization.serializeValueBacked<Long, LongBacked>(encoder, Long.serializer(), value)
                inOrder(encoder) {
                    verify(encoder).encodeNotNullMark()
                    verify(encoder).encodeLong(value.value)
                }
                verifyNoMoreInteractions(encoder)
            }
        }
}
