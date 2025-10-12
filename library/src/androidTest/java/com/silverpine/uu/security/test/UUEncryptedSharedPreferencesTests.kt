package com.silverpine.uu.security.test

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.silverpine.uu.core.security.UUEncryptedSharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UUEncryptedSharedPreferencesTests
{
    private lateinit var context: Context
    private lateinit var prefs: UUEncryptedSharedPreferences

    @Before
    fun setup()
    {
        context = ApplicationProvider.getApplicationContext()
        val delegate = context.getSharedPreferences("secure_prefs_test", Context.MODE_PRIVATE)
        delegate.edit().clear().commit()
        prefs = UUEncryptedSharedPreferences(delegate)
    }

    @Test
    fun putAndGetString_roundTrip()
    {
        val key = "api_key"
        val value = "sk-123456"

        prefs.edit().putString(key, value).commit()
        val retrieved = prefs.getString(key, null)

        assertEquals(value, retrieved)
    }

    @Test
    fun putAndGetInt_roundTrip()
    {
        val key = "int_key"
        val value = 42

        prefs.edit().putInt(key, value).commit()
        val retrieved = prefs.getInt(key, -1)

        assertEquals(value, retrieved)
    }

    @Test
    fun putAndGetBoolean_roundTrip()
    {
        val key = "bool_key"
        val value = true

        prefs.edit().putBoolean(key, value).commit()
        val retrieved = prefs.getBoolean(key, false)

        assertTrue(retrieved)
    }

    @Test
    fun contains_returnsTrueAfterPut()
    {
        val key = "exists"
        prefs.edit().putString(key, "yes").commit()

        assertTrue(prefs.contains(key))
    }

    @Test(expected = UnsupportedOperationException::class)
    fun getAll_throwsUnsupported()
    {
        prefs.all
    }

    @Test
    fun putAndGetLong_roundTrip()
    {
        prefs.edit().putLong("l", 123456789L).commit()
        assertEquals(123456789L, prefs.getLong("l", -1L))
    }

    @Test
    fun putAndGetFloat_roundTrip()
    {
        prefs.edit().putFloat("f", 2.71f).commit()
        assertEquals(2.71f, prefs.getFloat("f", -1f))
    }

    @Test
    fun putAndGetStringSet_roundTrip()
    {
        val key = "roles"
        val value = setOf("admin", "editor", "viewer")

        prefs.edit().putStringSet(key, value.toMutableSet()).commit()
        val retrieved = prefs.getStringSet(key, null)

        assertNotNull(retrieved)
        assertEquals(value.size, retrieved!!.size)
        assertTrue(retrieved.containsAll(value))

        // Ensure raw stored value is not plaintext
        val rawStored = prefs.getString(key, null)
        assertNotNull(rawStored)
        assertNotEquals(value.toString(), rawStored)
    }

    @Test
    fun getStringSet_returnsDefaultIfMissing()
    {
        val def = setOf("guest")
        val retrieved = prefs.getStringSet("missing", def.toMutableSet())
        assertEquals(def, retrieved)
    }

    @Test
    fun putNullStringSet_removesKey()
    {
        val key = "roles"
        prefs.edit().putStringSet(key, setOf("admin").toMutableSet()).commit()
        assertTrue(prefs.contains(key))

        prefs.edit().putStringSet(key, null).commit()
        assertFalse(prefs.contains(key))
    }

    @Test
    fun malformedData_returnsDefault()
    {
        val key = "roles"
        // Write garbage directly into delegate
        prefs.edit().putString(key, "notBase64").commit()

        val def = setOf("fallback")
        val retrieved = prefs.getStringSet(key, def.toMutableSet())
        assertEquals(def, retrieved)
    }
}