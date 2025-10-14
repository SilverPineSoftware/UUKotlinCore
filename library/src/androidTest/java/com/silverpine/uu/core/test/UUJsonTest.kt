package com.silverpine.uu.core.test

import androidx.annotation.Keep
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.silverpine.uu.core.UUJson
import com.silverpine.uu.core.UUKotlinXJsonProvider
import com.silverpine.uu.core.test.models.CustomSerializersModel
import com.silverpine.uu.core.test.models.EnumModel
import com.silverpine.uu.core.test.models.GenericsConcreteModel
import com.silverpine.uu.core.test.models.NullsModel
import com.silverpine.uu.core.test.models.PrimitiveArraysModel
import com.silverpine.uu.core.test.models.PrimitiveListModel
import com.silverpine.uu.core.test.models.PrimitiveModel
import com.silverpine.uu.core.test.models.TestEnumCamelCase
import com.silverpine.uu.core.test.models.TestEnumSnakeCase
import com.silverpine.uu.core.uuToByteArray
import com.silverpine.uu.core.uuUtf8ByteArray
import com.silverpine.uu.logging.UULog
import com.silverpine.uu.test.UUAssert
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import kotlin.test.assertNotNull

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UUJsonTest
{
    companion object
    {
        const val LOOPS = 100

        @OptIn(ExperimentalSerializationApi::class)
        @JvmStatic
        @BeforeClass
        fun beforeClass()
        {
            UUJson.init(UUKotlinXJsonProvider(Json()
            {
                ignoreUnknownKeys = true
                explicitNulls = false
                encodeDefaults = true
                namingStrategy = JsonNamingStrategy.SnakeCase
                coerceInputValues = true
            }))
        }

        @JvmStatic
        @AfterClass
        fun afterClass()
        {
            UUJson.init(UUKotlinXJsonProvider(Json.Default))
        }
    }

    @Test
    fun test_0000_primitiveModel()
    {
        doToFromJsonTest(PrimitiveModel::default)
        doToFromJsonTest(PrimitiveModel::random, LOOPS)
        doToFromJsonTest(PrimitiveModel::min)
        doToFromJsonTest(PrimitiveModel::max)
    }

    @Test
    fun test_0001_customerSerializersModel()
    {
        doToFromJsonTest(CustomSerializersModel::default)
        doToFromJsonTest(CustomSerializersModel::random, LOOPS)
        doToFromJsonTest(CustomSerializersModel::min)
        doToFromJsonTest(CustomSerializersModel::max)
    }

    @Test
    fun test_0002_nullsModel()
    {
        doToFromJsonTest(NullsModel::default)
        doToFromJsonTest(NullsModel::random, LOOPS)
        doToFromJsonTest(NullsModel::min)
        doToFromJsonTest(NullsModel::max)
    }

    @Test
    fun test_0003_primitiveArraysModel()
    {
        doToFromJsonTest(PrimitiveArraysModel::default)
        doToFromJsonTest(PrimitiveArraysModel::random, LOOPS)
        doToFromJsonTest(PrimitiveArraysModel::min)
        doToFromJsonTest(PrimitiveArraysModel::max)
    }

    @Test
    fun test_0004_genericsModel()
    {
        doToFromJsonTest(GenericsConcreteModel::default)
        doToFromJsonTest(GenericsConcreteModel::random, LOOPS)
        doToFromJsonTest(GenericsConcreteModel::min)
        doToFromJsonTest(GenericsConcreteModel::max)
    }

    @Test
    fun test_0005_enumModels()
    {
        doToFromJsonTest(EnumModel::default)
        doToFromJsonTest(EnumModel::random, LOOPS)
        doToFromJsonTest(EnumModel::min)
        doToFromJsonTest(EnumModel::max)
    }

    /**
     * Tests an object with a nullable enum field being deserialized when the JSON string has
     * garbage in it.
     */
    @Test
    fun test_0006_nullable_lenient_enums()
    {
        val input = "{\"camel_case\":\"Three\",\"snake_case\":\"window_trim\",\"custom_one\":\"Garbage\",\"custom_two\":\"window_trim\",\"custom_three\":\"one\",\"custom_four\":\"tv_ladder_hat\"}"

        val check = UUJson.fromString(input, EnumModel::class.java).getOrNull()
        Assert.assertNotNull(check)
        Assert.assertEquals(TestEnumCamelCase.Three, check!!.camelCase)
        Assert.assertEquals(TestEnumSnakeCase.window_trim, check.snake_case)
        Assert.assertEquals(null, check.customOne)
        Assert.assertEquals(TestEnumSnakeCase.window_trim, check.customTwo)
        Assert.assertEquals(TestEnumCamelCase.One, check.customThree)
        Assert.assertEquals(TestEnumSnakeCase.tv_ladder_hat, check.customFour)
    }

    /**
     * Tests an object with a nullable enum field being deserialized when the JSON string has
     * null in it.
     */
    @Test
    fun test_0007_nullable_lenient_enum_null()
    {
        val input = "{\"camel_case\":\"Three\",\"snake_case\":\"window_trim\",\"custom_one\":null,\"custom_two\":\"window_trim\",\"custom_three\":\"one\",\"custom_four\":\"tv_ladder_hat\"}"

        val check = UUJson.fromString(input, EnumModel::class.java).getOrNull()
        Assert.assertNotNull(check)
        Assert.assertEquals(TestEnumCamelCase.Three, check!!.camelCase)
        Assert.assertEquals(TestEnumSnakeCase.window_trim, check.snake_case)
        Assert.assertEquals(null, check.customOne)
        Assert.assertEquals(TestEnumSnakeCase.window_trim, check.customTwo)
        Assert.assertEquals(TestEnumCamelCase.One, check.customThree)
        Assert.assertEquals(TestEnumSnakeCase.tv_ladder_hat, check.customFour)
    }

    /**
     * Tests an object with a non-nullable enum field being deserialized when the JSON string has
     * garbage in it.
     */
    @Test
    fun test_0008_non_nullable_lenient_enums()
    {
        val input = "{\"camel_case\":\"Three\",\"snake_case\":\"window_trim\",\"custom_one\":\"two\",\"custom_two\":\"window_trim\",\"custom_three\":\"FooBar\",\"custom_four\":\"tv_ladder_hat\"}"

        val check = UUJson.fromString(input, EnumModel::class.java).getOrNull()
        Assert.assertNotNull(check)
        Assert.assertEquals(TestEnumCamelCase.Three, check!!.camelCase)
        Assert.assertEquals(TestEnumSnakeCase.window_trim, check.snake_case)
        Assert.assertEquals(TestEnumCamelCase.Two, check.customOne)
        Assert.assertEquals(TestEnumSnakeCase.window_trim, check.customTwo)
        Assert.assertEquals(TestEnumCamelCase.One, check.customThree)
        Assert.assertEquals(TestEnumSnakeCase.tv_ladder_hat, check.customFour)
    }

    /**
     * Tests an object with a non-nullable enum field being deserialized when the JSON string has
     * null in it.
     */
    @Test
    fun test_0009_non_nullable_lenient_enum_null()
    {
        val input = "{\"camel_case\":\"Three\",\"snake_case\":\"window_trim\",\"custom_one\":\"Two\",\"custom_two\":\"window_trim\",\"custom_three\":null,\"custom_four\":\"tv_ladder_hat\"}"

        val check = UUJson.fromString(input, EnumModel::class.java).getOrNull()
        Assert.assertNotNull(check)
        Assert.assertEquals(TestEnumCamelCase.Three, check!!.camelCase)
        Assert.assertEquals(TestEnumSnakeCase.window_trim, check.snake_case)
        Assert.assertEquals(TestEnumCamelCase.Two, check.customOne)
        Assert.assertEquals(TestEnumSnakeCase.window_trim, check.customTwo)
        Assert.assertEquals(TestEnumCamelCase.Three, check!!.customThree)
        Assert.assertEquals(TestEnumSnakeCase.tv_ladder_hat, check.customFour)
    }

    @Test
    fun test_0010_primitiveListsModel()
    {
        doToFromJsonTest(PrimitiveListModel::default)
        doToFromJsonTest(PrimitiveListModel::random, LOOPS)
        doToFromJsonTest(PrimitiveListModel::min)
        doToFromJsonTest(PrimitiveListModel::max)
    }

    private inline fun <reified T: Any> doToFromJsonTest(createObject: ()->T, loops: Int = 1)
    {
        for (i in 0 until loops)
        {
            val source = createObject()

            val json = UUAssert.unwrap(UUJson.toJson(source, source.javaClass).getOrNull())
            UULog.d(javaClass, "doToFromJsonTest-$i", "JSON Object: $json")

            val check = TestObject(source)
            val checkJson = check.encodeToJson()
            Assert.assertEquals(json, checkJson)

            val fromJson: T? = UUJson.fromString(json, source.javaClass).getOrNull()
            Assert.assertNotNull(fromJson)
            Assert.assertEquals(source, fromJson)

            val fromObj = FromObject()
            val fromCheck: T? = fromObj.decodeFromJson(json)
            Assert.assertNotNull(fromJson)
            Assert.assertEquals(source, fromCheck)

            val jsonBytes = UUAssert.unwrap(json.uuUtf8ByteArray())
            val fromJsonBytes: T? = UUJson.fromBytes(jsonBytes, source.javaClass).getOrNull()
            assertNotNull(fromJsonBytes)
        }
    }




    class TestObject<T: Any>(
        private val jsonObject: T)
    {
        fun encodeToJson(): String?
        {
            return UUJson.toJson(jsonObject, jsonObject.javaClass).getOrNull()
        }
    }

    class FromObject
    {
        inline fun <reified T: Any> decodeFromJson(string: String): T?
        {
            return UUJson.fromString(string, T::class.java).getOrNull()
        }
    }


    @Keep
    @Serializable
    class SerializableHashMap: HashMap<String, String>()


    @Test
    fun test_1000_hashMapTest()
    {
        val input = """
            {
              "06FD" : "ESS Embedded System Solutions Inc.",
              "03BA" : "Maxscend Microelectronics Company Limited"
            }
        """.trimIndent()

        //val obj: HashMap<String, String> = hashMapOf()

        @Suppress("UNCHECKED_CAST")
        val vendorMap = UUJson.fromString(input, SerializableHashMap::class.java).getOrNull() as? Map<String, String>
        Assert.assertNotNull(vendorMap)

        //Assert.assertEquals("ESS Embedded System Solutions Inc.", vendorMap["06FD"])
        //Assert.assertEquals("Maxscend Microelectronics Company Limited", vendorMap["03BA"])
    }

    @Test
    fun test_1001_hashMapTest()
    {
        val input = """
            {
              "06FD" : "ESS Embedded System Solutions Inc.",
              "03BA" : "Maxscend Microelectronics Company Limited"
            }
        """.trimIndent()

        // 2) create a Json instance; you can customize settings here if needed
        val json = Json {
            ignoreUnknownKeys = true    // if there might be extra fields
        }

        // 3) decode directly to a Map<String, String>
        val vendorMap: Map<String, String> = json.decodeFromString(input)

        // 4) use it!
        vendorMap.forEach { (id, name) ->
            println("Vendor 0x$id = $name")
        }



        //val strategy = serializer(objectClass) as DeserializationStrategy<T>
    }

    @Test
    fun test_1002_hashMapTest()
    {
        // 1. A strategy for Map<String,String>:
        val mapStrategy: DeserializationStrategy<Map<String, String>> =
            MapSerializer(String.serializer(), String.serializer())

        // 2. Then you can feed that into your UUJson (or Json) call:
                val input = """
            {
              "06FD" : "ESS Embedded System Solutions Inc.",
              "03BA" : "Maxscend Microelectronics Company Limited"
            }
        """.trimIndent()

        // 2) create a Json instance; you can customize settings here if needed
        val json = Json {
            ignoreUnknownKeys = true    // if there might be extra fields
        }
        @Suppress("UNCHECKED_CAST")
        val vendorMap: Map<String, String> = json.decodeFromString(mapStrategy, input)

        vendorMap.forEach { (id, name) ->
            println("Vendor 0x$id = $name")
        }

// or
    }
}

