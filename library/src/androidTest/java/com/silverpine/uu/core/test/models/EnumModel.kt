package com.silverpine.uu.core.test.models

import androidx.annotation.Keep
import com.silverpine.uu.core.serialization.uuEnumSerializer
import com.silverpine.uu.core.serialization.uuSafeEnumSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Keep
enum class TestEnumCamelCase
{
    One,
    Two,
    Three
}

@Keep
enum class TestEnumSnakeCase
{
    window_trim,
    dogs,
    tv_ladder_hat
}

object TestEnumCamelCaseSerializer : KSerializer<TestEnumCamelCase> by uuSafeEnumSerializer(TestEnumCamelCase::class.java, defaultDeserializeValue = TestEnumCamelCase.One)
object TestEnumSnakeCaseSerializer : KSerializer<TestEnumSnakeCase> by uuSafeEnumSerializer(TestEnumSnakeCase::class.java, defaultDeserializeValue = TestEnumSnakeCase.tv_ladder_hat)

class NullableTestEnumCamelCaseSerializer: KSerializer<TestEnumCamelCase?> by uuEnumSerializer(TestEnumCamelCase::class.java)
class NullableTestEnumSnakeCaseSerializer: KSerializer<TestEnumSnakeCase?> by uuEnumSerializer(TestEnumSnakeCase::class.java)

@Keep
@Serializable
open class EnumModel
{
    var camelCase: TestEnumCamelCase = TestEnumCamelCase.One
    var snake_case: TestEnumSnakeCase = TestEnumSnakeCase.dogs

    @Serializable(NullableTestEnumCamelCaseSerializer::class)
    var customOne: TestEnumCamelCase? = null

    @Serializable(NullableTestEnumSnakeCaseSerializer::class)
    var customTwo: TestEnumSnakeCase? = null

    @Serializable(TestEnumCamelCaseSerializer::class)
    var customThree: TestEnumCamelCase = TestEnumCamelCase.Three

    @Serializable(TestEnumSnakeCaseSerializer::class)
    var customFour: TestEnumSnakeCase = TestEnumSnakeCase.window_trim

    companion object
    {
        fun default(): EnumModel
        {
            return EnumModel()
        }

        fun random(): EnumModel
        {
            val obj = EnumModel()
            obj.camelCase = TestEnumCamelCase.entries.random()
            obj.snake_case = TestEnumSnakeCase.entries.random()
            obj.customOne = TestEnumCamelCase.entries.random()
            obj.customTwo = TestEnumSnakeCase.entries.random()
            obj.customThree = TestEnumCamelCase.entries.random()
            obj.customFour = TestEnumSnakeCase.entries.random()
            return obj
        }

        fun min(): EnumModel
        {
            val obj = EnumModel()
            obj.camelCase = TestEnumCamelCase.entries.first()
            obj.snake_case = TestEnumSnakeCase.entries.first()
            obj.customOne = TestEnumCamelCase.entries.first()
            obj.customTwo = TestEnumSnakeCase.entries.first()
            obj.customThree = TestEnumCamelCase.entries.first()
            obj.customFour = TestEnumSnakeCase.entries.first()
            return obj
        }

        fun max(): EnumModel
        {
            val obj = EnumModel()
            obj.camelCase = TestEnumCamelCase.entries.last()
            obj.snake_case = TestEnumSnakeCase.entries.last()
            obj.customOne = TestEnumCamelCase.entries.last()
            obj.customTwo = TestEnumSnakeCase.entries.last()
            obj.customThree = TestEnumCamelCase.entries.last()
            obj.customFour = TestEnumSnakeCase.entries.last()
            return obj
        }
    }

    override fun equals(other: Any?): Boolean
    {
        val o = (other as? EnumModel) ?: return false

        return(
                camelCase == o.camelCase &&
                snake_case == o.snake_case &&
                customOne == o.customOne &&
                customTwo == o.customTwo &&
                customThree == o.customThree &&
                customFour == o.customFour
        )
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}