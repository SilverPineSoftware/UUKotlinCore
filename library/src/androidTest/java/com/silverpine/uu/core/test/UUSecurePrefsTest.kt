package com.silverpine.uu.core.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.silverpine.uu.core.security.UUSecurePrefs
import com.silverpine.uu.core.uuToHexData
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UUSecurePrefsTest
{
    @Test()
    fun test_0000_uninitialized_getString()
    {
        assertThrows(RuntimeException::class.java)
        {
            UUSecurePrefs.getString("crash")
        }
    }

    @Test()
    fun test_0001_uninitialized_getStringSet()
    {
        assertThrows(RuntimeException::class.java)
        {
            UUSecurePrefs.getStringSet("crash")
        }
    }

    @Test()
    fun test_0002_uninitialized_getInt()
    {
        assertThrows(RuntimeException::class.java)
        {
            UUSecurePrefs.getInt("crash")
        }
    }

    @Test()
    fun test_0003_uninitialized_getLong()
    {
        assertThrows(RuntimeException::class.java)
        {
            UUSecurePrefs.getLong("crash")
        }
    }

    @Test()
    fun test_0004_uninitialized_getFloat()
    {
        assertThrows(RuntimeException::class.java)
        {
            UUSecurePrefs.getFloat("crash")
        }
    }

    @Test()
    fun test_0005_uninitialized_getDouble()
    {
        assertThrows(RuntimeException::class.java)
        {
            UUSecurePrefs.getDouble("crash")
        }
    }

    @Test()
    fun test_0006_uninitialized_getBoolean()
    {
        assertThrows(RuntimeException::class.java)
        {
            UUSecurePrefs.getBoolean("crash")
        }
    }

    @Test()
    fun test_0007_uninitialized_getData()
    {
        assertThrows(RuntimeException::class.java)
        {
            UUSecurePrefs.getData("crash")
        }
    }

    @Test()
    fun test_0008_uninitialized_setString()
    {
        assertThrows(RuntimeException::class.java)
        {
            UUSecurePrefs.setString("crash", "crash")
        }
    }

    @Test()
    fun test_0009_uninitialized_setStringSet()
    {
        assertThrows(RuntimeException::class.java)
        {
            UUSecurePrefs.setStringSet("crash", null)
        }
    }

    @Test()
    fun test_0010_uninitialized_setInt()
    {
        assertThrows(RuntimeException::class.java)
        {
            UUSecurePrefs.setInt("crash", 123)
        }
    }

    @Test()
    fun test_0011_uninitialized_setLong()
    {
        assertThrows(RuntimeException::class.java)
        {
            UUSecurePrefs.setLong("crash", 456L)
        }
    }

    @Test()
    fun test_0012_uninitialized_setFloat()
    {
        assertThrows(RuntimeException::class.java)
        {
            UUSecurePrefs.setFloat("crash", 92.3f)
        }
    }

    @Test()
    fun test_0013_uninitialized_setDouble()
    {
        assertThrows(RuntimeException::class.java)
        {
            UUSecurePrefs.setFloat("crash", 92.3f)
        }
    }

    @Test()
    fun test_0014_uninitialized_setBoolean()
    {
        assertThrows(RuntimeException::class.java)
        {
            UUSecurePrefs.setBoolean("crash", false)
        }
    }

    @Test()
    fun test_0015_uninitialized_setData()
    {
        assertThrows(RuntimeException::class.java)
        {
            UUSecurePrefs.setData("crash", "ABCD".uuToHexData())
        }
    }

    @Test()
    fun test_0100_initialize()
    {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        UUSecurePrefs.init(appContext, "UUSecurePrefsTest")
    }

    @Test()
    fun test_0101_getSetString()
    {
        val key = "string_key"
        var value = UUSecurePrefs.getString(key)
        assertNull(value)

        val toWrite = "barbecue sauce"
        UUSecurePrefs.setString(key, toWrite)

        value = UUSecurePrefs.getString(key)
        assertEquals(toWrite, value)

        UUSecurePrefs.remove(key)

        value = UUSecurePrefs.getString(key)
        assertNull(value)
    }

    @Test()
    fun test_0102_getSetInt()
    {
        val key = "int_key"
        var value = UUSecurePrefs.getInt(key)
        assertEquals(0, value)

        val toWrite = 300
        UUSecurePrefs.setInt(key, toWrite)

        value = UUSecurePrefs.getInt(key)
        assertEquals(toWrite, value)

        UUSecurePrefs.remove(key)

        value = UUSecurePrefs.getInt(key, 99)
        assertEquals(99, value)
    }

    @Test()
    fun test_0103_getSetLong()
    {
        val key = "long_key"
        var value = UUSecurePrefs.getLong(key)
        assertEquals(0L, value)

        val toWrite = 8675309L
        UUSecurePrefs.setLong(key, toWrite)

        value = UUSecurePrefs.getLong(key)
        assertEquals(toWrite, value)

        UUSecurePrefs.remove(key)

        value = UUSecurePrefs.getLong(key, 987L)
        assertEquals(987L, value)
    }

    @Test()
    fun test_0104_getSetFloat()
    {
        val key = "float_key"
        var value = UUSecurePrefs.getFloat(key)
        assertEquals(0.0f, value)

        val toWrite = 101.6f
        UUSecurePrefs.setFloat(key, toWrite)

        value = UUSecurePrefs.getFloat(key)
        assertEquals(toWrite, value)

        UUSecurePrefs.remove(key)

        value = UUSecurePrefs.getFloat(key, -57.22f)
        assertEquals(-57.22f, value)
    }

    @Test()
    fun test_0105_getSetDouble()
    {
        val key = "double_key"
        var value = UUSecurePrefs.getDouble(key)
        assertEquals(0.0, value, 0.0)

        val toWrite = 9509.7611
        UUSecurePrefs.setDouble(key, toWrite)

        value = UUSecurePrefs.getDouble(key)
        assertEquals(toWrite, value, 0.0)

        UUSecurePrefs.remove(key)

        value = UUSecurePrefs.getDouble(key, 2257.5722)
        assertEquals(2257.5722, value, 0.0)
    }

    @Test()
    fun test_0105_getSetBoolean()
    {
        val key = "boolean_key"
        var value = UUSecurePrefs.getBoolean(key)
        assertEquals(false, value)

        val toWrite = true
        UUSecurePrefs.setBoolean(key, toWrite)

        value = UUSecurePrefs.getBoolean(key)
        assertEquals(toWrite, value)

        UUSecurePrefs.remove(key)

        value = UUSecurePrefs.getBoolean(key, true)
        assertEquals(true, value)
    }

    @Test()
    fun test_0106_getSetData()
    {
        val key = "data_key"
        var value = UUSecurePrefs.getData(key)
        assertNull(value)

        val toWrite = "ABCDEF1234".uuToHexData()
        UUSecurePrefs.setData(key, toWrite)

        value = UUSecurePrefs.getData(key)
        assertArrayEquals(toWrite, value)

        UUSecurePrefs.remove(key)

        value = UUSecurePrefs.getData(key, "1234".uuToHexData())
        assertArrayEquals("1234".uuToHexData(), value)
    }

    @Test()
    fun test_0107_getSetStringSet()
    {
        val key = "string_set_key"
        var value = UUSecurePrefs.getStringSet(key)
        assertNull(value)

        val toWrite = setOf("one", "two")
        UUSecurePrefs.setStringSet(key, toWrite)

        value = UUSecurePrefs.getStringSet(key)
        assertNotNull(value)
        assertTrue(value?.containsAll(toWrite) == true)
        assertTrue(toWrite.containsAll(value!!))

        UUSecurePrefs.remove(key)

        val def = setOf("four", "five")
        value = UUSecurePrefs.getStringSet(key, def)
        assertNotNull(value)
        assertTrue(value?.containsAll(def) == true)
        assertTrue(def.containsAll(value!!))
    }


}