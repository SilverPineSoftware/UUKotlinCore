package com.silverpine.uu.core.test.security

import com.silverpine.uu.core.security.UUCrypto
import com.silverpine.uu.core.security.UUEncryptedSharedPreferences
import com.silverpine.uu.core.test.fakes.UUFakeSecretKeyProvider
import com.silverpine.uu.core.test.fakes.UUFakeSharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UUEncryptedSharedPreferencesJvmTest
{
    private lateinit var delegate: UUFakeSharedPreferences
    private lateinit var prefs: UUEncryptedSharedPreferences

    @Before
    fun setup()
    {
        // Use in-memory prefs and JVM AES/GCM crypto via FakeSecretKeyProvider
        delegate = UUFakeSharedPreferences()
        prefs = UUEncryptedSharedPreferences(delegate, "jvm_test_alias")
        UUCrypto.secretKeyProvider = UUFakeSecretKeyProvider()
    }

    @Test
    fun putAndGetString_roundTrip()
    {
        val key = "api_key"
        val value = "sk-abc123"

        prefs.edit().putString(key, value).commit()
        val retrieved = prefs.getString(key, null)

        assertEquals(value, retrieved)
        assertTrue(delegate.contains(key))

        // Stored value in delegate should be base64 ciphertext, not plaintext
        val rawStored = delegate.getString(key, null)
        assertNotEquals(value, rawStored)
        assertNotNull(rawStored)
    }

    @Test
    fun primitives_roundTrip()
    {
        prefs.edit()
            .putInt("int", 42)
            .putLong("long", 9876543210L)
            .putFloat("float", 3.14f)
            .putBoolean("bool", true)
            .commit()

        assertEquals(42, prefs.getInt("int", -1))
        assertEquals(9876543210L, prefs.getLong("long", -1))
        assertEquals(3.14f, prefs.getFloat("float", -1f))
        assertTrue(prefs.getBoolean("bool", false))
    }

    @Test
    fun missingKey_returnsDefault()
    {
        assertEquals("def", prefs.getString("missing", "def"))
        assertEquals(7, prefs.getInt("missing_int", 7))
        assertEquals(9L, prefs.getLong("missing_long", 9L))
        assertEquals(2.71f, prefs.getFloat("missing_float", 2.71f))
        assertTrue(prefs.getBoolean("missing_bool", true))
    }

    @Test
    fun removeAndClear_work()
    {
        prefs.edit().putString("a", "1").putString("b", "2").commit()
        assertTrue(prefs.contains("a"))
        assertTrue(prefs.contains("b"))

        prefs.edit().remove("a").commit()
        assertFalse(prefs.contains("a"))
        assertTrue(prefs.contains("b"))

        prefs.edit().clear().commit()
        assertFalse(prefs.contains("b"))
    }

    @Test(expected = UnsupportedOperationException::class)
    fun getAll_throws()
    {
        prefs.all
    }

    @Test
    fun nullPutString_removesKey()
    {
        prefs.edit().putString("x", "y").commit()
        assertTrue(prefs.contains("x"))
        prefs.edit().putString("x", null).commit()
        assertFalse(prefs.contains("x"))
    }

    @Test
    fun putAndGetStringSet_roundTrip() {
        val key = "roles"
        val value = setOf("admin", "editor", "viewer")

        prefs.edit().putStringSet(key, value.toMutableSet()).commit()
        val retrieved = prefs.getStringSet(key, null)

        assertNotNull(retrieved)
        assertEquals(value.size, retrieved!!.size)
        assertTrue(retrieved.containsAll(value))

        // Ensure raw stored value is not plaintext
        val rawStored = delegate.getString(key, null)
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
        delegate.edit().putString(key, "notBase64").commit()

        val def = setOf("fallback")
        val retrieved = prefs.getStringSet(key, def.toMutableSet())
        assertEquals(def, retrieved)
    }
}