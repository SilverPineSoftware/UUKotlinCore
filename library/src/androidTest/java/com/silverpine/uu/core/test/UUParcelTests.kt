package com.silverpine.uu.core.test

import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.silverpine.uu.core.uuDeserializeParcel
import com.silverpine.uu.core.uuSerializeParcel
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UUParcelTests
{
    @Test
    fun test_0000_serializeAndDeserializeParcel()
    {
        val b = Bundle()
        b.putString("Foo", "is to Bar")
        b.putString("Bar", "is to Baz")
        b.putString("Baz", "is to Foo")
        b.putInt("Int", 99)

        val serialized = b.uuSerializeParcel()
        Assert.assertNotNull("Expect non null output for serialize")

        val deserialized = serialized?.uuDeserializeParcel(Bundle.CREATOR)
        Assert.assertNotNull(deserialized)

        Assert.assertArrayEquals(b.keySet().toTypedArray(), deserialized!!.keySet().toTypedArray())

        for (key in b.keySet())
        {
            when (key)
            {
                "Int" ->
                {
                    val expected = b.getInt(key)
                    val actual = deserialized.getInt(key)
                    Assert.assertEquals("Expect same value for key: $key", expected, actual)
                }

                else ->
                {
                    val expected = b.getString(key)
                    val actual = deserialized.getString(key)
                    Assert.assertEquals("Expect same value for key: $key", expected, actual)
                }
            }
        }
    }
}