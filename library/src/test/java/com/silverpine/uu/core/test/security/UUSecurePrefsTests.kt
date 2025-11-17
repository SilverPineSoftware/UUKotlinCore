package com.silverpine.uu.core.test.security

import android.content.Context
import com.silverpine.uu.core.security.UUCrypto
import com.silverpine.uu.core.security.UUEncryptedSharedPreferences
import com.silverpine.uu.core.security.UUSecurePrefs
import com.silverpine.uu.core.test.fakes.UUFakeSecretKeyProvider
import com.silverpine.uu.core.test.fakes.UUFakeSharedPreferences
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.argThat
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UUSecurePrefsTests
{
    @Mock
    private lateinit var mockContext: Context
    
    private lateinit var delegate: UUFakeSharedPreferences
    private lateinit var encryptedPrefs: UUEncryptedSharedPreferences

    @BeforeEach
    fun setup()
    {
        // Use in-memory prefs and JVM AES/GCM crypto via FakeSecretKeyProvider
        delegate = UUFakeSharedPreferences()
        encryptedPrefs = UUEncryptedSharedPreferences(delegate, "jvm_test_alias")
        UUCrypto.secretKeyProvider = UUFakeSecretKeyProvider()
        
        // Mock Context to return our encrypted SharedPreferences
        whenever(mockContext.getSharedPreferences(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt()))
            .thenReturn(encryptedPrefs)
        whenever(mockContext.packageName).thenReturn("com.test.package")
        
        // Initialize UUSecurePrefs with mocked context
        UUSecurePrefs.init(mockContext, "test_prefs")
    }

    @Test
    fun putAndGetString_roundTrip()
    {
        val key = "api_key"
        val value = "sk-abc123"

        UUSecurePrefs.putString(key, value)
        val retrieved = UUSecurePrefs.getString(key, null)

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
        UUSecurePrefs.putInt("int", 42)
        UUSecurePrefs.putLong("long", 9876543210L)
        UUSecurePrefs.putFloat("float", 3.14f)
        UUSecurePrefs.putBoolean("bool", true)

        assertEquals(42, UUSecurePrefs.getInt("int", -1))
        assertEquals(9876543210L, UUSecurePrefs.getLong("long", -1))
        assertEquals(3.14f, UUSecurePrefs.getFloat("float", -1f))
        assertTrue(UUSecurePrefs.getBoolean("bool", false))
    }

    @Test
    fun missingKey_returnsDefault()
    {
        assertEquals("def", UUSecurePrefs.getString("missing", "def"))
        assertEquals(7, UUSecurePrefs.getInt("missing_int", 7))
        assertEquals(9L, UUSecurePrefs.getLong("missing_long", 9L))
        assertEquals(2.71f, UUSecurePrefs.getFloat("missing_float", 2.71f))
        assertTrue(UUSecurePrefs.getBoolean("missing_bool", true))
    }

    @Test
    fun removeAndClear_work()
    {
        UUSecurePrefs.putString("a", "1")
        UUSecurePrefs.putString("b", "2")
        assertNotNull(UUSecurePrefs.getString("a", null))
        assertNotNull(UUSecurePrefs.getString("b", null))

        UUSecurePrefs.remove("a")
        assertNull(UUSecurePrefs.getString("a", null))
        assertNotNull(UUSecurePrefs.getString("b", null))

        UUSecurePrefs.clear()
        assertNull(UUSecurePrefs.getString("b", null))
    }

    @Test
    fun nullPutString_removesKey()
    {
        UUSecurePrefs.putString("x", "y")
        assertNotNull(UUSecurePrefs.getString("x", null))
        UUSecurePrefs.putString("x", null)
        assertNull(UUSecurePrefs.getString("x", null))
    }

    @Test
    fun putAndGetStringSet_roundTrip()
    {
        val key = "roles"
        val value = setOf("admin", "editor", "viewer")

        UUSecurePrefs.putStringSet(key, value)
        val retrieved = UUSecurePrefs.getStringSet(key, null)

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
        val retrieved = UUSecurePrefs.getStringSet("missing", def)
        assertEquals(def, retrieved)
    }

    @Test
    fun putNullStringSet_removesKey()
    {
        val key = "roles"
        UUSecurePrefs.putStringSet(key, setOf("admin"))
        assertNotNull(UUSecurePrefs.getStringSet(key, null))

        UUSecurePrefs.putStringSet(key, null)
        assertNull(UUSecurePrefs.getStringSet(key, null))
    }

    @Test
    fun putAndGetDouble_roundTrip()
    {
        val key = "double_key"
        val value = 3.141592653589793

        UUSecurePrefs.putDouble(key, value)
        val retrieved = UUSecurePrefs.getDouble(key, 0.0)

        assertEquals(value, retrieved, 0.0001)
    }

    @Test
    fun getDouble_returnsDefaultIfMissing()
    {
        val defaultValue = 2.718281828
        val retrieved = UUSecurePrefs.getDouble("missing_double", defaultValue)
        assertEquals(defaultValue, retrieved, 0.0001)
    }

    @Test
    fun putAndGetData_roundTrip()
    {
        val key = "data_key"
        val value = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05)

        UUSecurePrefs.putData(key, value)
        val retrieved = UUSecurePrefs.getData(key, null)

        assertNotNull(retrieved)
        assertArrayEquals(value, retrieved)
    }

    @Test
    fun getData_returnsDefaultIfMissing()
    {
        val defaultValue = byteArrayOf(0xFF.toByte(), 0xFE.toByte())
        val retrieved = UUSecurePrefs.getData("missing_data", defaultValue)
        assertArrayEquals(defaultValue, retrieved)
    }

    @Test
    fun putNullData_removesKey()
    {
        val key = "data_key"
        UUSecurePrefs.putData(key, byteArrayOf(0x01, 0x02))
        assertNotNull(UUSecurePrefs.getData(key, null))

        UUSecurePrefs.putData(key, null)
        assertNull(UUSecurePrefs.getData(key, null))
    }

    enum class TestEnum { VALUE1, VALUE2, VALUE3 }

    @Test
    fun putAndGetEnum_roundTrip()
    {
        val key = "enum_key"
        val value = TestEnum.VALUE2

        UUSecurePrefs.putEnum(key, value)
        val retrieved = UUSecurePrefs.getEnum(key, TestEnum::class.java, null)

        assertNotNull(retrieved)
        assertEquals(value, retrieved)
    }

    @Test
    fun getEnum_returnsDefaultIfMissing()
    {
        val defaultValue = TestEnum.VALUE1
        val retrieved = UUSecurePrefs.getEnum("missing_enum", TestEnum::class.java, defaultValue)
        
        assertEquals(defaultValue, retrieved)
    }

    @Test
    fun getEnum_returnsDefaultIfInvalid()
    {
        // Store an invalid enum value directly in delegate
        delegate.edit().putString("invalid_enum", "INVALID_VALUE").commit()
        
        val defaultValue = TestEnum.VALUE1
        val retrieved = UUSecurePrefs.getEnum("invalid_enum", TestEnum::class.java, defaultValue)
        
        assertEquals(defaultValue, retrieved)
    }

    @Test
    fun putNullEnum_removesKey()
    {
        val key = "enum_key"
        UUSecurePrefs.putEnum(key, TestEnum.VALUE1)
        assertNotNull(UUSecurePrefs.getEnum(key, TestEnum::class.java, null))

        UUSecurePrefs.putEnum(key, null)
        assertNull(UUSecurePrefs.getEnum(key, TestEnum::class.java, null))
    }

    @Test
    fun multipleOperations_roundTrip()
    {
        UUSecurePrefs.putString("string", "test")
        UUSecurePrefs.putInt("int", 123)
        UUSecurePrefs.putLong("long", 456L)
        UUSecurePrefs.putFloat("float", 1.5f)
        UUSecurePrefs.putDouble("double", 2.5)
        UUSecurePrefs.putBoolean("bool", true)
        UUSecurePrefs.putStringSet("set", setOf("a", "b", "c"))
        UUSecurePrefs.putData("data", byteArrayOf(0x01, 0x02))

        assertEquals("test", UUSecurePrefs.getString("string", null))
        assertEquals(123, UUSecurePrefs.getInt("int", 0))
        assertEquals(456L, UUSecurePrefs.getLong("long", 0L))
        assertEquals(1.5f, UUSecurePrefs.getFloat("float", 0.0f))
        assertEquals(2.5, UUSecurePrefs.getDouble("double", 0.0), 0.0001)
        assertEquals(true, UUSecurePrefs.getBoolean("bool", false))
        assertEquals(setOf("a", "b", "c"), UUSecurePrefs.getStringSet("set", null))
        assertArrayEquals(byteArrayOf(0x01, 0x02), UUSecurePrefs.getData("data", null))
    }

    @Test
    fun overwriteExistingValue()
    {
        val key = "overwrite_key"
        UUSecurePrefs.putString(key, "value1")
        UUSecurePrefs.putString(key, "value2")

        val retrieved = UUSecurePrefs.getString(key, null)
        assertEquals("value2", retrieved)
    }

    @Test
    fun emptyString()
    {
        val key = "empty_string_key"
        UUSecurePrefs.putString(key, "")
        val retrieved = UUSecurePrefs.getString(key, "default")

        assertEquals("", retrieved)
    }

    @Test
    fun emptyByteArray()
    {
        val key = "empty_data_key"
        UUSecurePrefs.putData(key, ByteArray(0))
        val retrieved = UUSecurePrefs.getData(key, null)

        assertNotNull(retrieved)
        assertEquals(0, retrieved!!.size)
    }

    @Test
    fun largeByteArray()
    {
        val key = "large_data_key"
        val largeData = ByteArray(1024) { it.toByte() }

        UUSecurePrefs.putData(key, largeData)
        val retrieved = UUSecurePrefs.getData(key, null)

        assertNotNull(retrieved)
        assertArrayEquals(largeData, retrieved)
    }

    @Test
    fun specialCharactersInString()
    {
        val key = "special_chars_key"
        val value = "Hello! @#\$%^&*()_+-=[]{}|;':\",./<>?"
        UUSecurePrefs.putString(key, value)
        val retrieved = UUSecurePrefs.getString(key, null)

        assertEquals(value, retrieved)
    }

    @Test
    fun init_withDefaultName()
    {
        // Reset and re-init with default name
        val newDelegate = UUFakeSharedPreferences()
        val newEncryptedPrefs = UUEncryptedSharedPreferences(newDelegate, "jvm_test_alias")
        whenever(mockContext.getSharedPreferences(argThat { contains("UUSecurePrefs") }, ArgumentMatchers.anyInt()))
            .thenReturn(newEncryptedPrefs)
        
        UUSecurePrefs.init(mockContext)
        
        // Should work without throwing
        assertDoesNotThrow {
            UUSecurePrefs.getString("test_key", "default")
        }
    }

    @Test
    fun init_withCustomName()
    {
        val customName = "custom_prefs_name"
        val newDelegate = UUFakeSharedPreferences()
        val newEncryptedPrefs = UUEncryptedSharedPreferences(newDelegate, "jvm_test_alias")
        whenever(mockContext.getSharedPreferences(eq(customName), ArgumentMatchers.anyInt()))
            .thenReturn(newEncryptedPrefs)
        
        UUSecurePrefs.init(mockContext, customName)
        
        // Should work without throwing
        assertDoesNotThrow {
            UUSecurePrefs.getString("test_key", "default")
        }
    }
}

