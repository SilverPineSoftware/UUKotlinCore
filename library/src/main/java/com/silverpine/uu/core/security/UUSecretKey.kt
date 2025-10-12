package com.silverpine.uu.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * Singleton implementation of [UUSecretKeyProvider] that manages symmetric AES keys
 * using the Android Keystore system.
 *
 * This object encapsulates the logic for securely loading or generating AES keys
 * configured for AES/GCM/NoPadding encryption and decryption. Keys are stored in the
 * device's hardware‑backed keystore (when available) under a caller‑supplied alias,
 * ensuring that sensitive key material never leaves the secure enclave.
 *
 * Typical usage:
 * ```
 * val result = UUSecretKey.loadGcmKey("my_app_key")
 * result.onSuccess { key ->
 *     val cipher = Cipher.getInstance("AES/GCM/NoPadding")
 *     cipher.init(Cipher.ENCRYPT_MODE, key)
 *     // Use cipher to encrypt data
 * }.onFailure { ex ->
 *     // Handle error
 * }
 * ```
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
 */
object UUSecretKey: UUSecretKeyProvider
{
    private const val KEY_STORE_TYPE: String = "AndroidKeyStore"

    /**
     * Loads or generates a symmetric AES key from the underlying keystore.
     *
     * If a key with the given [alias] already exists and is valid, it is returned.
     * Otherwise, a new key is generated with the specified [keySizeBits] and stored
     * under that alias for future use.
     *
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

            val kg = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEY_STORE_TYPE)
            val spec = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setKeySize(keySizeBits)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()

            kg.init(spec)

            val key = kg.generateKey()
            Result.success(key)
        }
        catch (ex: Exception)
        {
            Result.failure(ex)
        }
    }
}