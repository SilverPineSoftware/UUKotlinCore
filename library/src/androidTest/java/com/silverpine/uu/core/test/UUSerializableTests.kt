package com.silverpine.uu.core.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.silverpine.uu.core.uuDeserialize
import com.silverpine.uu.core.uuSerialize
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.io.Serializable

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UUSerializableTests
{
    data class SerializeTestModel(var anInt: Int = 0, var aString: String = ""): Serializable

    @Test
    fun test_0000_serializeAndDeserialize()
    {
        val b = SerializeTestModel()
        b.anInt = 22
        b.aString = "Unit Testing"

        val serialized = b.uuSerialize()
        Assert.assertNotNull("Expect non null output for serialize")

        val deserialized = serialized?.uuDeserialize(SerializeTestModel::class.java)
        Assert.assertNotNull(deserialized)

        Assert.assertEquals(b.anInt, deserialized!!.anInt)
        Assert.assertEquals(b.aString, deserialized.aString)
    }

}