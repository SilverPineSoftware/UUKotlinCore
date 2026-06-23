package com.silverpine.uu.core.security

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.StrongBoxUnavailableException
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * Singleton implementation of [UUSecretKeyProvider] that manages symmetric AES keys
 * using the Android Keystore system.
 *
 * Keys are generated for AES/GCM/NoPadding and stored in hardware-backed secure storage.
 * When the device supports StrongBox, key generation prefers that isolated secure element;
 * otherwise keys fall back to the standard TEE-backed Android Keystore.
 *
 * ### Behavior
 * - If a valid [SecretKey] already exists under the given alias, it is returned.
 * - If the alias exists but is not a [KeyStore.SecretKeyEntry], the entry is deleted
 *   and a new key is generated.
 * - If no key exists, a new one is generated with the specified size and stored under
 *   the alias for future use.
 *
 * @constructor This is an `object` singleton and cannot be instantiated directly.
 *
 * @see UUSecretKeyProvider for the abstraction this class implements.
 *
 * @since 1.0.0
 */
object UUSecretKey: UUSecretKeyProvider
{
    private const val KEY_STORE_TYPE: String = "AndroidKeyStore"

    /**
     * Loads or generates a symmetric AES key from the Android Keystore.
     *
     * If a key with the given [alias] already exists and is valid, it is returned.
     * Otherwise, a new key is generated with the specified [keySizeBits] and stored
     * under that alias for future use.
     *
     * @since 1.0.0
     * @param alias the unique identifier under which the key is stored.
     * @param keySizeBits the size of the AES key in bits (default is 256).
     *
     * @return a [Result] containing the loaded or newly generated [SecretKey] on success,
     * or a failure wrapping the thrown [Exception] if the keystore could not be accessed
     * or the key could not be created.
     */
    override fun loadGcmKey(alias: String, keySizeBits: Int): Result<SecretKey>
    {
        return try
        {
            val ks = KeyStore.getInstance(KEY_STORE_TYPE)
            ks.load(null)

            if (ks.containsAlias(alias))
            {
                val entry = ks.getEntry(alias, null)

                if (entry is KeyStore.SecretKeyEntry)
                {
                    return Result.success(entry.secretKey)
                }
                else
                {
                    ks.deleteEntry(alias)
                }
            }

            val key = generateHardwareBackedKey(alias, keySizeBits)
            Result.success(key)
        }
        catch (ex: Exception)
        {
            Result.failure(ex)
        }
    }

    private fun generateHardwareBackedKey(alias: String, keySizeBits: Int): SecretKey
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        {
            try
            {
                return generateKey(alias, keySizeBits, strongBoxBacked = true)
            }
            catch (_: StrongBoxUnavailableException)
            {
                // Fall back to the standard hardware-backed keystore.
            }
        }

        return generateKey(alias, keySizeBits, strongBoxBacked = false)
    }

    private fun generateKey(
        alias: String,
        keySizeBits: Int,
        strongBoxBacked: Boolean): SecretKey
    {
        val kg = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEY_STORE_TYPE)
        val builder = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setKeySize(keySizeBits)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setRandomizedEncryptionRequired(true)

        if (strongBoxBacked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        {
            builder.setIsStrongBoxBacked(true)
        }

        kg.init(builder.build())
        return kg.generateKey()
    }
}
