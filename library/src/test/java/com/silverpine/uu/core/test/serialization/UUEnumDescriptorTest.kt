package com.silverpine.uu.core.test.serialization

import com.silverpine.uu.core.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal object NullableNameSerializer : UUEnumSerializer<TestEnum>(TestEnum::class.java)
internal object DefaultNameSerializer : UUEnumSerializer<TestEnum>(
    TestEnum::class.java, defaultDeserializeValue = TestEnum.Two
)
internal object NullableNumberSerializer : UUValueBackedEnumSerializer<Long, LongBacked>(Long.serializer(),
    { raw -> LongBacked.entries.firstOrNull { it.value == raw } }
)
internal object DefaultNumberSerializer : UUValueBackedEnumSerializer<Long, LongBacked>(Long.serializer(),
    { raw -> LongBacked.entries.firstOrNull { it.value == raw } }, LongBacked.TEN
)

@Serializable
internal data class EnumDescriptorModel(
    @Serializable(with = NullableNameSerializer::class)
    val name: TestEnum?,
    @Serializable(with = DefaultNameSerializer::class)
    val defaultName: TestEnum?,
    @Serializable(with = NullableNumberSerializer::class)
    val number: LongBacked?,
    @Serializable(with = DefaultNumberSerializer::class)
    val defaultNumber: LongBacked?
)

@OptIn(ExperimentalSerializationApi::class)
class UUEnumDescriptorTest
{
    @TestFactory
    fun `nullable enum descriptors retain their primitive kind across formats`(): List<DynamicTest> =
        UUEnumFormat.entries.flatMap { format ->
            listOf<TestEnum?>(null, TestEnum.Two).map { fallback ->
                DynamicTest.dynamicTest("Format=$format, fallback=$fallback") {
                    val serializers = listOf(
                        object : UUEnumSerializer<TestEnum>(TestEnum::class.java, format, fallback) {},
                        uuEnumSerializer(TestEnum::class.java, format, fallback)
                    )
                    for (serializer in serializers)
                    {
                        assertTrue(serializer.descriptor.isNullable)
                        assertEquals(format.primitiveKind, serializer.descriptor.kind)
                    }
                }
            }
        }

    @Test
    fun `number backed descriptors are nullable with or without fallback`()
    {
        val serializers = listOf(
            NullableNumberSerializer,
            DefaultNumberSerializer,
            uuValueBackedEnumSerializer<Long, LongBacked>(Long.serializer(), { null }),
            uuValueBackedEnumSerializer<Long, LongBacked>(Long.serializer(), { null }, LongBacked.TEN)
        )
        for (serializer in serializers)
        {
            assertTrue(serializer.descriptor.isNullable)
            assertEquals(PrimitiveKind.LONG, serializer.descriptor.kind)
        }
    }

    @TestFactory
    fun `safe enum descriptors remain non nullable`(): List<DynamicTest> =
        UUEnumFormat.entries.map { format ->
            DynamicTest.dynamicTest("Safe format=$format") {
                val serializers = listOf(
                    object : UUSafeEnumSerializer<TestEnum>(TestEnum::class.java, format, TestEnum.Two) {},
                    uuSafeEnumSerializer(TestEnum::class.java, format, TestEnum.Two)
                )
                for (serializer in serializers)
                {
                    assertFalse(serializer.descriptor.isNullable)
                    assertEquals(format.primitiveKind, serializer.descriptor.kind)
                }
            }
        }

    @Test
    fun `annotated properties expose nullable descriptors`()
    {
        val descriptor = EnumDescriptorModel.serializer().descriptor
        for (index in 0 until descriptor.elementsCount)
        {
            assertTrue(descriptor.getElementDescriptor(index).isNullable, descriptor.getElementName(index))
        }
    }

    @Test
    fun `annotated null properties preserve configured fallback behavior`()
    {
        val input = """{"name":null,"defaultName":null,"number":null,"defaultNumber":null}"""
        assertEquals(input, Json.encodeToString(EnumDescriptorModel.serializer(), EnumDescriptorModel(null, null, null, null)))
        assertEquals(
            EnumDescriptorModel(null, TestEnum.Two, null, LongBacked.TEN),
            Json.decodeFromString(EnumDescriptorModel.serializer(), input)
        )
    }

    @Test
    fun `annotated non null properties round trip as primitives`()
    {
        val model = EnumDescriptorModel(TestEnum.One, TestEnum.Three, LongBacked.HIGH, LongBacked.NEGATIVE)
        val expected = """{"name":"One","defaultName":"Three","number":4294967297,"defaultNumber":-7}"""
        assertEquals(expected, Json.encodeToString(EnumDescriptorModel.serializer(), model))
        assertEquals(model, Json.decodeFromString(EnumDescriptorModel.serializer(), expected))
    }
}
