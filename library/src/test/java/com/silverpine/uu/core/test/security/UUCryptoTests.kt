package com.silverpine.uu.core.test.security

import com.silverpine.uu.core.security.UUCrypto
import com.silverpine.uu.core.security.UUSecretKeyProvider
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class FakeSecretKeyProvider : UUSecretKeyProvider
{
    private val keys = mutableMapOf<String, SecretKey>()

    override fun loadGcmKey(alias: String, keySizeBits: Int): Result<SecretKey>
    {
        val key = keys.getOrPut(alias)
        {
            val kg = KeyGenerator.getInstance("AES")
            kg.init(keySizeBits)
            kg.generateKey()
        }

        return Result.success(key)
    }
}

class UUCryptoJvmTest
{
    @BeforeEach
    fun setup() {
        UUCrypto.secretKeyProvider = FakeSecretKeyProvider()
    }

    @Test
    fun encryptAndDecrypt_roundTrip_success()
    {
        val plaintext = "Hello, world!".toByteArray()
        val encrypted = UUCrypto.gcmEncrypt(plaintext).getOrThrow()
        val decrypted = UUCrypto.gcmDecrypt(encrypted).getOrThrow()

        assertNotNull(encrypted)
        assertArrayEquals(plaintext, decrypted)
    }

    @Test
    fun nullInput_returnsNull()
    {
        assertNull(UUCrypto.gcmEncrypt(null).getOrThrow())
        assertNull(UUCrypto.gcmDecrypt(null).getOrThrow())
    }

    @Test
    fun emptyInput_returnsEmpty()
    {
        assertArrayEquals(ByteArray(0), UUCrypto.gcmEncrypt(ByteArray(0)).getOrThrow())
        assertArrayEquals(ByteArray(0), UUCrypto.gcmDecrypt(ByteArray(0)).getOrThrow())
    }

    @Test
    fun malformedCiphertext_returnsFailure()
    {
        val badData = byteArrayOf(0x01, 0x02, 0x03)
        val result = UUCrypto.gcmDecrypt(badData)
        assertTrue(result.isFailure)
    }
}