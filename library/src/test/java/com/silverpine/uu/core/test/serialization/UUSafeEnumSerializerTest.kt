package com.silverpine.uu.core.test.serialization

import com.silverpine.uu.core.serialization.UUEnumFormat
import com.silverpine.uu.core.serialization.UUSafeEnumSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class UUSafeEnumSerializerTest
{
    private fun serializer(format: UUEnumFormat, fallback: TestEnum): KSerializer<TestEnum>
    {
        return object : UUSafeEnumSerializer<TestEnum>(TestEnum::class.java, format, fallback) {}
    }

    private fun doSerializeString(format: UUEnumFormat, fallback: TestEnum = TestEnum.SixSeven, input: TestEnum, expected: String)
    {
        val serializer = serializer(format, fallback)
        val result = Json.encodeToString(serializer, input)
        assertEquals("\"$expected\"", result)
    }

    private fun doSerializeInt(fallback: TestEnum = TestEnum.SixSeven, input: TestEnum, expected: Int)
    {
        val serializer = serializer(UUEnumFormat.Ordinal, fallback)
        val result = Json.encodeToString(serializer, input)
        val intResult = result.toInt()
        assertEquals(expected, intResult)
    }

    private fun doDeSerializeString(format: UUEnumFormat, fallback: TestEnum = TestEnum.SixSeven, input: String, expected: TestEnum)
    {
        val serializer = serializer(format, fallback)
        val quotedInput = JsonPrimitive(input).toString()
        val stringInput: String = Json.decodeFromString(quotedInput)
        assertEquals(input, stringInput)

        val result = Json.decodeFromString(serializer, quotedInput)
        assertEquals(expected, result)
    }

    private fun doDeSerializeInt(fallback: TestEnum = TestEnum.SixSeven, input: Int, expected: TestEnum)
    {
        val serializer = serializer(UUEnumFormat.Ordinal, fallback)
        val quotedInput = "$input"

        val result = Json.decodeFromString(serializer, quotedInput)
        assertEquals(expected, result)
    }

    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = false
        isLenient = false
    }

    @Nested
    inner class SerializeTests
    {
        @Test
        fun `serialize Name`()
        {
            doSerializeString(UUEnumFormat.Name, TestEnum.One, TestEnum.Two, "Two")
            doSerializeString(UUEnumFormat.Name, TestEnum.One, TestEnum.SixSeven, "SixSeven")
            doSerializeString(UUEnumFormat.Name, TestEnum.One, TestEnum.Three, "Three")
        }

        @Test
        fun `serialize NameLower`()
        {
            doSerializeString(UUEnumFormat.NameLower, TestEnum.One, TestEnum.Two, "two")
            doSerializeString(UUEnumFormat.NameLower, TestEnum.One, TestEnum.SixSeven, "sixseven")
            doSerializeString(UUEnumFormat.NameLower, TestEnum.One, TestEnum.Three, "three")
        }

        @Test
        fun `serialize NameSnakeCase`()
        {
            doSerializeString(UUEnumFormat.NameSnakeCase, TestEnum.One, TestEnum.Two, "two")
            doSerializeString(UUEnumFormat.NameSnakeCase, TestEnum.One, TestEnum.SixSeven, "six_seven")
            doSerializeString(UUEnumFormat.NameSnakeCase, TestEnum.One, TestEnum.Three, "three")
        }

        @Test
        fun `serialize Ordinal`()
        {
            doSerializeInt(TestEnum.One, TestEnum.Two, 1)
            doSerializeInt(TestEnum.Two, TestEnum.SixSeven, 5)
            doSerializeInt(TestEnum.Three, TestEnum.Three, 2)
        }
    }

    @Nested
    inner class DeserializeTests {
        @Test
        fun `deserialize Name`()
        {
            doDeSerializeString(UUEnumFormat.Name, TestEnum.One, "One", TestEnum.One)
            doDeSerializeString(UUEnumFormat.Name, TestEnum.One, "SixSeven", TestEnum.SixSeven)
            doDeSerializeString(UUEnumFormat.Name, TestEnum.One, "OneTwoThree", TestEnum.OneTwoThree)
        }

        @Test
        fun `deserialize NameLowerCase`()
        {
            doDeSerializeString(UUEnumFormat.NameLower, TestEnum.One, "one", TestEnum.One)
            doDeSerializeString(UUEnumFormat.NameLower, TestEnum.One, "sixseven", TestEnum.SixSeven)
            doDeSerializeString(UUEnumFormat.NameLower, TestEnum.One, "onetwothree", TestEnum.OneTwoThree)
        }

        @Test
        fun `deserialize NameSnakeCase`()
        {
            doDeSerializeString(UUEnumFormat.NameSnakeCase, TestEnum.One, "one", TestEnum.One)
            doDeSerializeString(UUEnumFormat.NameSnakeCase, TestEnum.One, "six_seven", TestEnum.SixSeven)
            doDeSerializeString(UUEnumFormat.NameSnakeCase, TestEnum.One, "one_two_three", TestEnum.OneTwoThree)
        }

        @Test
        fun `deserialize Ordinal`()
        {
            doDeSerializeInt(TestEnum.One, 0, TestEnum.One)
            doDeSerializeInt(TestEnum.One, 5, TestEnum.SixSeven)
            doDeSerializeInt(TestEnum.One, 6, TestEnum.OneTwoThree)
        }
    }

    @Nested
    inner class ToFromTests
    {
        @TestFactory
        fun `test all enum constants across all formats serialize and deserialize correctly`(): List<DynamicTest>
        {
            val formats = UUEnumFormat.entries.toTypedArray()
            val cases = TestEnum.entries.toTypedArray()

            return formats.flatMap { format ->
                cases.map { enumValue ->
                    DynamicTest.dynamicTest("Format=$format, Enum=${enumValue.name}")
                    {
                        val ser = serializer(format, TestEnum.SixSeven)
                        if (ser.descriptor.kind == PrimitiveKind.STRING)
                        {
                            val encoded = json.encodeToString(ser, enumValue)
                            val decoded = json.decodeFromString(ser, encoded)
                            assertEquals(
                                enumValue,
                                decoded,
                                "Round-trip failed for $enumValue with format $format"
                            )
                        }
                        else if (ser.descriptor.kind == PrimitiveKind.INT)
                        {
                            val encoded = json.encodeToString(ser, enumValue)
                            val decoded = json.decodeFromString(ser, encoded)
                            assertEquals(
                                enumValue,
                                decoded,
                                "Round-trip failed for $enumValue with format $format"
                            )
                        }
                    }
                }
            }
        }

        @TestFactory
        fun `test deserialization fallback for unknown values`(): List<DynamicTest>
        {
            val formats = UUEnumFormat.entries.toTypedArray()
            val fallback = TestEnum.One
            val unknownInputs =
                listOf("\"not_valid\"", "\"UNKNOWN\"", "\"does_not_exist\"", "\"123456\"", 99, 110, -55)

            return formats.flatMap { format ->
                unknownInputs.map { input ->
                    DynamicTest.dynamicTest("Format=$format, Input=$input")
                    {
                        val ser = serializer(format, fallback)
                        if (ser.descriptor.kind == PrimitiveKind.STRING && input is String)
                        {
                            val result = json.decodeFromString(ser, input)
                            assertEquals(
                                fallback,
                                result,
                                "Fallback failed for input $input with format $format"
                            )
                        }
                        else if (ser.descriptor.kind == PrimitiveKind.INT && input is Int)
                        {
                            val decoded = json.decodeFromString(ser, "$input")
                            assertEquals(
                                fallback,
                                decoded,
                                "Round-trip failed for $input with format $format"
                            )
                        }
                    }
                }
            }
        }

        @TestFactory
        fun `test nullable deserialization returns default for unknown values`(): List<DynamicTest>
        {
            val formats = UUEnumFormat.entries.toTypedArray()
            val unknownInputs =
                listOf("\"not_valid\"", "\"UNKNOWN\"", "\"does_not_exist\"", "\"123456\"", 99, 110, -55)

            return formats.flatMap { format ->
                unknownInputs.map { input ->
                    DynamicTest.dynamicTest("Format=$format, Input=$input")
                    {
                        val default = TestEnum.OneTwoThree
                        val ser = serializer(format, default)
                        if (ser.descriptor.kind == PrimitiveKind.STRING && input is String)
                        {
                            val result = json.decodeFromString(ser, input)
                            assertEquals(default, result)
                        }
                        else if (ser.descriptor.kind == PrimitiveKind.INT && input is Int)
                        {
                            val result = json.decodeFromString(ser, "$input")
                            assertEquals(default, result)
                        }
                    }
                }
            }
        }
    }
}