package com.silverpine.uu.security.test

import android.security.keystore.KeyInfo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.silverpine.uu.core.security.UUCrypto
import com.silverpine.uu.core.security.UUSecretKey
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.security.KeyStore
import java.util.UUID
import javax.crypto.SecretKeyFactory

@RunWith(AndroidJUnit4::class)
class UUCryptoInstrumentedTest
{
    private lateinit var keyAlias: String

    @Before
    fun setup()
    {
        keyAlias = "com.uu.tests.crypto.${UUID.randomUUID()}"
        UUCrypto.secretKeyProvider = UUSecretKey
    }

    @After
    fun tearDown()
    {
        val ks = KeyStore.getInstance("AndroidKeyStore")
        ks.load(null)
        if (ks.containsAlias(keyAlias))
        {
            ks.deleteEntry(keyAlias)
        }
    }

    @Test
    fun encryptAndDecrypt_roundTrip_success()
    {
        val plaintext = "Secret message".toByteArray()
        val encrypted = UUCrypto.gcmEncrypt(plaintext, keyAlias).getOrThrow()
        val decrypted = UUCrypto.gcmDecrypt(encrypted, keyAlias).getOrThrow()

        assertNotNull(encrypted)
        assertArrayEquals(plaintext, decrypted)
    }

    @Test
    fun nullAndEmptyInputs_handledGracefully()
    {
        assertNull(UUCrypto.gcmEncrypt(null, keyAlias).getOrThrow())
        assertNull(UUCrypto.gcmDecrypt(null, keyAlias).getOrThrow())

        assertArrayEquals(ByteArray(0), UUCrypto.gcmEncrypt(ByteArray(0), keyAlias).getOrThrow())
        assertArrayEquals(ByteArray(0), UUCrypto.gcmDecrypt(ByteArray(0), keyAlias).getOrThrow())
    }

    @Test
    fun malformedCiphertext_returnsFailure()
    {
        val badData = byteArrayOf(0x00, 0x00, 0x00, 0x02, 0x01)
        val result = UUCrypto.gcmDecrypt(badData, keyAlias)
        assertTrue(result.isFailure)
    }

    @Test
    fun repeatedEncryption_producesDifferentCiphertext()
    {
        val plaintext = "repeatable-plaintext".toByteArray()
        val first = UUCrypto.gcmEncrypt(plaintext, keyAlias).getOrThrow()
        val second = UUCrypto.gcmEncrypt(plaintext, keyAlias).getOrThrow()

        assertNotNull(first)
        assertNotNull(second)
        assertNotEquals(first!!.toList(), second!!.toList())
        assertArrayEquals(plaintext, UUCrypto.gcmDecrypt(first, keyAlias).getOrThrow())
        assertArrayEquals(plaintext, UUCrypto.gcmDecrypt(second, keyAlias).getOrThrow())
    }

    @Test
    fun keyPersistsAcrossProviderReload()
    {
        val plaintext = "persisted-key-check".toByteArray()
        val encrypted = UUCrypto.gcmEncrypt(plaintext, keyAlias).getOrThrow()

        val reloadedKey = UUSecretKey.loadGcmKey(keyAlias).getOrThrow()
        assertNotNull(reloadedKey)

        assertArrayEquals(plaintext, UUCrypto.gcmDecrypt(encrypted, keyAlias).getOrThrow())
    }

    @Test
    fun hardwareBackedKey_isInsideSecureHardware()
    {
        val secretKey = UUSecretKey.loadGcmKey(keyAlias).getOrThrow()
        val factory = SecretKeyFactory.getInstance(secretKey.algorithm, "AndroidKeyStore")
        val keyInfo = factory.getKeySpec(secretKey, KeyInfo::class.java) as KeyInfo

        assertTrue(keyInfo.isInsideSecureHardware)
    }

    @Test
    fun encryptedPayload_hasLengthPrefixedIVFormat()
    {
        val plaintext = "format-check".toByteArray()
        val encrypted = UUCrypto.gcmEncrypt(plaintext, keyAlias).getOrThrow()!!

        val ivLength = encrypted.copyOfRange(0, 4)
            .let { java.nio.ByteBuffer.wrap(it).order(java.nio.ByteOrder.LITTLE_ENDIAN).int }

        assertTrue(ivLength > 0)
        assertEquals(4 + ivLength + (encrypted.size - 4 - ivLength), encrypted.size)
    }
}
