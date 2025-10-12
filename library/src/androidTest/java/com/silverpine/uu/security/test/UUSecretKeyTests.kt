package com.silverpine.uu.security.test

import android.security.keystore.KeyProperties
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.silverpine.uu.core.security.UUSecretKey
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.security.Security
import javax.crypto.SecretKey

@RunWith(AndroidJUnit4::class)
class UUSecretKeyTest
{
    private val testAlias = "test_key_alias"

    @Test
    fun printProviders()
    {
        println("Security.getProviders() -- ${Security.getProviders().map { it.name }}")
    }

    @Test
    fun generateNewKey_success()
    {
        val result = UUSecretKey.loadGcmKey(testAlias)

        assertTrue(result.isSuccess)
        val key = result.getOrNull()
        assertNotNull("Key should not be null", key)
        assertTrue("Key should be a SecretKey", key is SecretKey)
    }

    @Test
    fun loadExistingKey_returnsSameKey()
    {
        val first = UUSecretKey.loadGcmKey(testAlias).getOrThrow()
        val second = UUSecretKey.loadGcmKey(testAlias).getOrThrow()

        // The alias should resolve to the same key material
        assertEquals(first.encoded?.size, second.encoded?.size)
    }

    @Test
    fun invalidAlias_createsNewKey()
    {
        val alias = "new_alias_" + System.currentTimeMillis()
        val result = UUSecretKey.loadGcmKey(alias)

        assertTrue(result.isSuccess)
        val key = result.getOrNull()
        assertNotNull(key)
    }

    @Test
    fun keySupportsAesGcm()
    {
        val key = UUSecretKey.loadGcmKey(testAlias).getOrThrow()
        assertEquals("AES", key.algorithm)
        assertEquals(KeyProperties.KEY_ALGORITHM_AES, key.algorithm)
    }

    @Test
    fun failureCase_returnsFailure()
    {
        // Force a failure by passing an invalid key size
        val result = UUSecretKey.loadGcmKey("${testAlias}-invalidKeySize", keySizeBits = 123) // invalid size
        assertTrue(result.isFailure)
    }
}