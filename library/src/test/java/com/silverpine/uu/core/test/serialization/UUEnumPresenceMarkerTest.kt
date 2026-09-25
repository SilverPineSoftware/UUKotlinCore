package com.silverpine.uu.core.test.serialization

import com.silverpine.uu.core.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.io.*

@OptIn(ExperimentalSerializationApi::class)
class UUEnumPresenceMarkerTest
{
    private class BinaryEncoder(val bytes: ByteArrayOutputStream = ByteArrayOutputStream()) : AbstractEncoder()
    {
        override val serializersModule = EmptySerializersModule()
        private val output = DataOutputStream(bytes)
        override fun encodeNotNullMark() = output.writeBoolean(true)
        override fun encodeNull() = output.writeBoolean(false)
        override fun encodeString(value: String) = output.writeUTF(value)
        override fun encodeInt(value: Int) = output.writeInt(value)
        override fun encodeLong(value: Long) = output.writeLong(value)
    }

    private class BinaryDecoder(bytes: ByteArray) : AbstractDecoder()
    {
        override val serializersModule = EmptySerializersModule()
        val input = DataInputStream(ByteArrayInputStream(bytes))
        override fun decodeNotNullMark(): Boolean = input.readBoolean()
        override fun decodeNull(): Nothing? = null
        override fun decodeString(): String = input.readUTF()
        override fun decodeInt(): Int = input.readInt()
        override fun decodeLong(): Long = input.readLong()
        override fun decodeElementIndex(descriptor: SerialDescriptor): Int = error("Primitives only")
    }

    @TestFactory
    fun `nullable enum sequence consumes exactly one marker per value`(): List<DynamicTest> =
        UUEnumFormat.entries.flatMap { format ->
            listOf<TestEnum?>(null, TestEnum.Two).map { fallback ->
                DynamicTest.dynamicTest("Nullable $format fallback=$fallback") {
                    val serializer = uuEnumSerializer(TestEnum::class.java, format, fallback)
                    val encoder = BinaryEncoder()
                    val values = listOf(TestEnum.SixSeven, null, TestEnum.One)
                    values.forEach { serializer.serialize(encoder, it) }
                    assertEquals(1, encoder.bytes.toByteArray().first().toInt())
                    val decoder = BinaryDecoder(encoder.bytes.toByteArray())
                    values.forEach { assertEquals(it ?: fallback, serializer.deserialize(decoder)) }
                    assertEquals(0, decoder.input.available())
                }
            }
        }

    @TestFactory
    fun `safe enum sequence contains only primitives`(): List<DynamicTest> =
        UUEnumFormat.entries.map { format ->
            DynamicTest.dynamicTest("Safe $format") {
                val serializer = uuSafeEnumSerializer(TestEnum::class.java, format, TestEnum.Two)
                val encoder = BinaryEncoder()
                serializer.serialize(encoder, TestEnum.SixSeven)
                serializer.serialize(encoder, TestEnum.One)
                val expected = BinaryEncoder()
                when (format)
                {
                    UUEnumFormat.Name -> { expected.encodeString("SixSeven"); expected.encodeString("One") }
                    UUEnumFormat.NameLower -> { expected.encodeString("sixseven"); expected.encodeString("one") }
                    UUEnumFormat.NameSnakeCase -> { expected.encodeString("six_seven"); expected.encodeString("one") }
                    UUEnumFormat.Ordinal -> { expected.encodeInt(5); expected.encodeInt(0) }
                }
                assertArrayEquals(expected.bytes.toByteArray(), encoder.bytes.toByteArray())
                val decoder = BinaryDecoder(encoder.bytes.toByteArray())
                assertEquals(TestEnum.SixSeven, serializer.deserialize(decoder))
                assertEquals(TestEnum.One, serializer.deserialize(decoder))
                assertEquals(0, decoder.input.available())

                val unknown = BinaryEncoder()
                if (format == UUEnumFormat.Ordinal) unknown.encodeInt(99) else unknown.encodeString("unknown")
                val unknownDecoder = BinaryDecoder(unknown.bytes.toByteArray())
                assertEquals(TestEnum.Two, serializer.deserialize(unknownDecoder))
                assertEquals(0, unknownDecoder.input.available())
            }
        }

    @TestFactory
    fun `number backed sequence preserves markers and 64 bit values`(): List<DynamicTest> =
        listOf<LongBacked?>(null, LongBacked.TEN).map { fallback ->
            DynamicTest.dynamicTest("Number fallback=$fallback") {
                val serializer = uuValueBackedEnumSerializer<Long, LongBacked>(Long.serializer(),
                    { raw -> LongBacked.entries.firstOrNull { it.value == raw } }, fallback
                )
                val values = listOf(LongBacked.MIN, null, LongBacked.HIGH, LongBacked.MAX)
                val encoder = BinaryEncoder()
                values.forEach { serializer.serialize(encoder, it) }
                assertEquals(28, encoder.bytes.size()) // Three marked Longs and one null marker.
                val decoder = BinaryDecoder(encoder.bytes.toByteArray())
                values.forEach { assertEquals(it ?: fallback, serializer.deserialize(decoder)) }
                assertEquals(0, decoder.input.available())
            }
        }
}
