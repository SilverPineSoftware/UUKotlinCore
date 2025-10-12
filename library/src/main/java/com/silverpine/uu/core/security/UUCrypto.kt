package com.silverpine.uu.core.security

import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

/**
 * Utility object providing AES/GCM/NoPadding encryption and decryption
 * using a pluggable [UUSecretKeyProvider].
 *
 * This class encapsulates the details of working with AES in Galois/Counter Mode (GCM),
 * including IV generation and authenticated decryption. It produces and consumes
 * ciphertexts in a self‑contained binary format:
 *
 * ```
 * [IV length (Int32, little endian)] [IV bytes] [encrypted payload bytes]
 * ```
 *
 * ### Behavior
 * - If the input to [gcmEncrypt] or [gcmDecrypt] is `null`, the result is `null`.
 * - If the input is empty, the result is empty.
 * - On encryption, a fresh IV is generated for each call and prepended to the ciphertext.
 * - On decryption, the IV is parsed from the buffer and used to initialize the cipher.
 *
 * ### Key Management
 * Keys are obtained from [secretKeyProvider], which defaults to [UUSecretKey] (backed by
 * the Android Keystore). For testing, this provider can be swapped with a fake or mock.
 *
 * @property secretKeyProvider the provider used to load or generate AES keys.
 */
object UUCrypto
{
    private const val IV_TAG_SIZE_BITS = 128
    private const val CRYPTO_TRANSFORMATION = "AES/GCM/NoPadding"
    private const val KEY_ALIAS = "com.silverpine.uu.core.UUCrypto"

    var secretKeyProvider: UUSecretKeyProvider = UUSecretKey

    /**
     * Encrypts the given [value] using AES/GCM/NoPadding with a key from [secretKeyProvider].
     *
     * @param value the plaintext bytes to encrypt. `null` returns `null`, empty returns empty.
     * @param keyAlias the alias under which the key is stored in the keystore.
     * @return a [Result] containing the ciphertext buffer on success, or a failure if
     *         encryption fails. The ciphertext format is:
     *         `[IV length (Int32 LE)] [IV bytes] [encrypted bytes]`.
     */
    fun gcmEncrypt(
        value: ByteArray?,
        keyAlias: String = KEY_ALIAS
    ): Result<ByteArray?>
    {
        return try
        {
            // Null in --> Null Out
            if (value == null)
            {
                return Result.success(value)
            }

            // Empty in --> Empty Out
            if (value.isEmpty())
            {
                return Result.success(value)
            }

            val key = secretKeyProvider.loadGcmKey(keyAlias).getOrThrow()


            val cipher = Cipher.getInstance(CRYPTO_TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, key)

            val encryptedBytes = cipher.doFinal(value)
            val iv = cipher.iv

            // Build buffer: [IV length (int32 LE)] [IV bytes] [encrypted bytes]
            val buffer = ByteBuffer.allocate(Int.SIZE_BYTES + iv.size + encryptedBytes.size)
                .order(ByteOrder.LITTLE_ENDIAN)
            buffer.putInt(iv.size)
            buffer.put(iv)
            buffer.put(encryptedBytes)

            Result.success(buffer.array())
        }
        catch (ex: Exception)
        {
            return Result.failure(ex)
        }
    }

    /**
     * Decrypts the given [value] using AES/GCM/NoPadding with a key from [secretKeyProvider].
     *
     * @param value the ciphertext buffer to decrypt. `null` returns `null`, empty returns empty.
     *              The buffer must be in the format produced by [gcmEncrypt].
     * @param keyAlias the alias under which the key is stored in the keystore.
     * @return a [Result] containing the decrypted plaintext bytes on success, or a failure if
     *         decryption fails (e.g., malformed buffer, authentication failure).
     */
    fun gcmDecrypt(
        value: ByteArray?,
        keyAlias: String = KEY_ALIAS): Result<ByteArray?>
    {
        return try
        {
            // Null in --> Null Out
            if (value == null)
            {
                return Result.success(value)
            }

            // Empty in --> Empty Out
            if (value.isEmpty())
            {
                return Result.success(value)
            }

            //val bytes = Base64.decode(value, Base64.NO_WRAP)
            val buffer = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN)

            if (buffer.remaining() < 4)
            {
                return Result.failure(IllegalArgumentException("Buffer too short, unable to read IV size bytes"))
            }

            val ivLength = buffer.int
            if (ivLength <= 0 || buffer.remaining() < ivLength)
            {
                return Result.failure(IllegalArgumentException("Buffer too short, unable to read IV"))
            }

            val iv = ByteArray(ivLength)
            buffer.get(iv)

            val encryptedPart = ByteArray(buffer.remaining())
            buffer.get(encryptedPart)

            val key = secretKeyProvider.loadGcmKey(keyAlias).getOrThrow()

            val cipher = Cipher.getInstance(CRYPTO_TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(IV_TAG_SIZE_BITS, iv))
            val decryptedBytes = cipher.doFinal(encryptedPart)

            Result.success(decryptedBytes)
        }
        catch (ex: Exception)
        {
            return Result.failure(ex)
        }
    }
}