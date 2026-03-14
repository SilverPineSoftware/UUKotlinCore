package com.silverpine.uu.core.test.security

import android.content.SharedPreferences
import com.silverpine.uu.core.security.UUCrypto
import com.silverpine.uu.core.security.UUEncryptedSharedPreferences
import com.silverpine.uu.core.test.fakes.UUFakeSecretKeyProvider
import com.silverpine.uu.core.test.fakes.UUFakeSharedPreferences
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private fun <T : Enum<T>> SharedPreferences.getEnumSet(key: String, enumClass: Class<T>, defaultValue: Set<T>?): Set<T>?
{
    val storedSet = getStringSet(key, null) ?: return defaultValue
    val constants = enumClass.enumConstants ?: return defaultValue
    val nameToEnum = constants.associateBy { it.name }
    return storedSet.mapNotNull { nameToEnum[it] }.toSet()
}

private fun <T : Enum<T>> SharedPreferences.putEnumSet(key: String, value: Set<T>?)
{
    val names = value?.map { it.name }?.toSet()?.toMutableSet()
    edit().putStringSet(key, names).commit()
}

class UUEncryptedSharedPreferencesJvmTest
{
    private lateinit var delegate: UUFakeSharedPreferences
    private lateinit var prefs: UUEncryptedSharedPreferences

    @BeforeEach
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

    @Test
    fun getAll_throws()
    {
        assertThrows(UnsupportedOperationException::class.java)
        {
            prefs.all
        }
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

    @Test
    fun putAndGetStringSet_emptySet_roundTrip()
    {
        val key = "empty_set_key"
        val value = emptySet<String>()

        prefs.edit().putStringSet(key, value.toMutableSet()).commit()
        val retrieved = prefs.getStringSet(key, null)

        assertNotNull(retrieved)
        assertTrue(retrieved!!.isEmpty())
    }

    @Test
    fun putAndGetStringSet_singleElement_roundTrip()
    {
        val key = "single_key"
        val value = setOf("VALUE1")

        prefs.edit().putStringSet(key, value.toMutableSet()).commit()
        val retrieved = prefs.getStringSet(key, null)

        assertNotNull(retrieved)
        assertEquals(1, retrieved!!.size)
        assertTrue(retrieved.contains("VALUE1"))
    }

    @Test
    fun putAndGetStringSet_enumLikeNames_roundTrip()
    {
        val key = "enum_set_key"
        val value = setOf("VALUE1", "VALUE2", "VALUE3")

        prefs.edit().putStringSet(key, value.toMutableSet()).commit()
        val retrieved = prefs.getStringSet(key, null)

        assertNotNull(retrieved)
        assertEquals(value.size, retrieved!!.size)
        assertTrue(retrieved.containsAll(value))
        assertTrue(value.containsAll(retrieved))

        val rawStored = delegate.getString(key, null)
        assertNotNull(rawStored)
        assertNotEquals(value.toString(), rawStored)
    }

    @Test
    fun getStringSet_returnsNullWhenMissingAndDefaultNull()
    {
        val retrieved = prefs.getStringSet("missing_set", null)
        assertNull(retrieved)
    }

    @Test
    fun putEmptyStringSet_roundTrip()
    {
        val key = "empty_roles"
        prefs.edit().putStringSet(key, mutableSetOf()).commit()
        assertTrue(prefs.contains(key))

        val retrieved = prefs.getStringSet(key, setOf("default").toMutableSet())
        assertNotNull(retrieved)
        assertTrue(retrieved!!.isEmpty())
    }

    private enum class TestEnum { VALUE1, VALUE2, VALUE3 }

    @Test
    fun putAndGetEnumSet_roundTrip()
    {
        val key = "enum_set_key"
        val value = setOf(TestEnum.VALUE1, TestEnum.VALUE3)

        prefs.putEnumSet(key, value)
        val retrieved = prefs.getEnumSet(key, TestEnum::class.java, null)

        assertNotNull(retrieved)
        assertEquals(value, retrieved)
        val rawStored = delegate.getString(key, null)
        assertNotNull(rawStored)
        assertNotEquals(value.toString(), rawStored)
    }

    @Test
    fun getEnumSet_returnsDefaultIfMissing()
    {
        val defaultValue = setOf(TestEnum.VALUE1)
        val retrieved = prefs.getEnumSet("missing_enum_set", TestEnum::class.java, defaultValue)

        assertEquals(defaultValue, retrieved)
    }

    @Test
    fun getEnumSet_returnsNullWhenMissingAndDefaultNull()
    {
        val retrieved = prefs.getEnumSet("missing_enum_set", TestEnum::class.java, null)

        assertNull(retrieved)
    }

    @Test
    fun getEnumSet_skipsInvalidNames()
    {
        val key = "enum_set_key"
        prefs.edit().putStringSet(key, setOf("VALUE1", "INVALID_VALUE", "VALUE3").toMutableSet()).commit()

        val retrieved = prefs.getEnumSet(key, TestEnum::class.java, null)

        assertNotNull(retrieved)
        assertEquals(setOf(TestEnum.VALUE1, TestEnum.VALUE3), retrieved)
    }

    @Test
    fun getEnumSet_returnsEmptySetWhenAllNamesInvalid()
    {
        val key = "enum_set_key"
        prefs.edit().putStringSet(key, setOf("INVALID1", "INVALID2").toMutableSet()).commit()

        val retrieved = prefs.getEnumSet(key, TestEnum::class.java, null)

        assertNotNull(retrieved)
        assertTrue(retrieved!!.isEmpty())
    }

    @Test
    fun putNullEnumSet_removesKey()
    {
        val key = "enum_set_key"
        prefs.putEnumSet(key, setOf(TestEnum.VALUE1, TestEnum.VALUE2))
        assertTrue(prefs.contains(key))

        prefs.putEnumSet(key, null)
        assertFalse(prefs.contains(key))
        assertNull(prefs.getEnumSet(key, TestEnum::class.java, null))
    }

    @Test
    fun putEmptyEnumSet_roundTrip()
    {
        val key = "enum_set_key"
        prefs.putEnumSet(key, emptySet())
        val retrieved = prefs.getEnumSet(key, TestEnum::class.java, null)

        assertNotNull(retrieved)
        assertTrue(retrieved!!.isEmpty())
    }
}