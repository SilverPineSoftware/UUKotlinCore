package com.silverpine.uu.core.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.silverpine.uu.core.test.models.CustomSerializersModel
import com.silverpine.uu.core.test.models.GenericsConcreteModel
import com.silverpine.uu.core.test.models.NullsModel
import com.silverpine.uu.core.test.models.PrimitiveArraysModel
import com.silverpine.uu.core.test.models.PrimitiveModel
import com.silverpine.uu.core.uuFromJson
import com.silverpine.uu.core.uuToJson
import com.silverpine.uu.logging.UULog
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.util.*

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UUJsonTest
{
    companion object
    {
        const val LOOPS = 100
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

    private inline fun <reified T: Any> doToFromJsonTest(createObject: ()->T, loops: Int = 1)
    {
        for (i in 0 until loops)
        {
            val source = createObject()

            val json = source.uuToJson()
            Assert.assertNotNull(json)
            UULog.d(javaClass, "doToFromJsonTest-$i", "JSON Object: $json")

            val fromJson: T? = json?.uuFromJson()
            Assert.assertNotNull(fromJson)
            Assert.assertEquals(source, fromJson)
        }
    }
}

