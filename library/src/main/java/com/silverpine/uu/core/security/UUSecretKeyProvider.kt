package com.silverpine.uu.core.security

import javax.crypto.SecretKey

/**
 * Abstraction for providing symmetric AES keys stored in the Android Keystore.
 *
 * Implementations of this interface are responsible for loading or generating
 * a secret key suitable for AES/GCM/NoPadding encryption and decryption.
 * This allows production code to use a secure, hardware‑backed keystore
 * implementation, while tests can supply a fake or in‑memory provider.
 */
interface UUSecretKeyProvider {

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
    fun loadGcmKey(alias: String, keySizeBits: Int = 256): Result<SecretKey>
}