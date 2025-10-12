package com.silverpine.uu.security.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.silverpine.uu.core.security.UUCrypto
import com.silverpine.uu.core.security.UUSecretKey
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UUCryptoInstrumentedTest
{
    @Before
    fun setup()
    {
        // Use the real provider backed by Android Keystore
        UUCrypto.secretKeyProvider = UUSecretKey
    }

    @Test
    fun encryptAndDecrypt_roundTrip_success()
    {
        val plaintext = "Secret message".toByteArray()
        val encrypted = UUCrypto.gcmEncrypt(plaintext).getOrThrow()
        val decrypted = UUCrypto.gcmDecrypt(encrypted).getOrThrow()

        assertArrayEquals(plaintext, decrypted)
    }

    @Test
    fun nullAndEmptyInputs_handledGracefully()
    {
        assertNull(UUCrypto.gcmEncrypt(null).getOrThrow())
        assertNull(UUCrypto.gcmDecrypt(null).getOrThrow())

        assertArrayEquals(ByteArray(0), UUCrypto.gcmEncrypt(ByteArray(0)).getOrThrow())
        assertArrayEquals(ByteArray(0), UUCrypto.gcmDecrypt(ByteArray(0)).getOrThrow())
    }

    @Test
    fun malformedCiphertext_returnsFailure()
    {
        val badData = byteArrayOf(0x00, 0x00, 0x00, 0x02, 0x01) // invalid IV length
        val result = UUCrypto.gcmDecrypt(badData)
        assertTrue(result.isFailure)
    }
}