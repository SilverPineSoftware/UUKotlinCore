package com.silverpine.uu.core.security

import android.content.SharedPreferences
import com.silverpine.uu.core.uuBase64
import com.silverpine.uu.core.uuFromBase64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.charset.StandardCharsets

/**
 * A [SharedPreferences] implementation that transparently encrypts and decrypts
 * values using [UUCrypto].
 *
 * - Keys (the preference names) are stored in plaintext.
 * - Values are encrypted with AES/GCM/NoPadding before being persisted.
 * - Decryption is applied automatically when reading values.
 *
 * This allows you to use the familiar [SharedPreferences] API while ensuring
 * sensitive values are protected at rest.
 *
 * @since 1.0.0
 */
class UUEncryptedSharedPreferences(
    /**
     * The underlying [SharedPreferences] instance that stores the encrypted values.
     *
     * @since 1.0.0
     */
    private val delegate: SharedPreferences,
    /**
     * The key alias used for encryption/decryption operations with [UUCrypto].
     * Defaults to a package-specific alias if not provided.
     *
     * @since 1.0.0
     */
    private val keyAlias: String = "com.silverpine.uu.core.security.UUEncryptedSharedPreferences"
) : SharedPreferences
{
    /**
     * Not supported for encrypted preferences.
     *
     * This method is intentionally unsupported because exposing all decrypted values
     * in bulk would be a security risk. Use individual getter methods instead.
     *
     * @throws UnsupportedOperationException Always thrown when called.
     *
     * @since 1.0.0
     */
    override fun getAll(): MutableMap<String, *>
    {
        // Not safe to expose decrypted values in bulk
        throw UnsupportedOperationException("getAll() not supported for encrypted prefs")
    }

    /**
     * Retrieves a string value, automatically decrypting it if found.
     *
     * @param key The preference key to retrieve.
     * @param defValue The default value to return if the key is not found or decryption fails.
     * @return The decrypted string value, or [defValue] if not found or decryption fails.
     *
     * @since 1.0.0
     */
    override fun getString(key: String?, defValue: String?): String?
    {
        val encryptedString = delegate.getString(key, null) ?: return defValue
        val encryptedBytes = encryptedString.uuFromBase64().getOrNull() ?: return defValue
        val decrypted = UUCrypto.gcmDecrypt(encryptedBytes, keyAlias)
        return decrypted.getOrNull()?.toString(StandardCharsets.UTF_8) ?: defValue
    }

    /**
     * Retrieves a set of string values, automatically decrypting and deserializing them if found.
     *
     * The set is serialized using Java object serialization before encryption, so it must
     * be deserialized when reading. If decryption or deserialization fails, the default value
     * is returned.
     *
     * @param key The preference key to retrieve.
     * @param defValues The default value to return if the key is not found or decryption fails.
     * @return The decrypted and deserialized set of strings, or [defValues] if not found or
     *         decryption/deserialization fails.
     *
     * @since 1.0.0
     */
    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>?
    {
        return try
        {
            val encryptedString = delegate.getString(key, null) ?: return defValues
            val encryptedBytes = encryptedString.uuFromBase64().getOrNull() ?: return defValues
            val decrypted =
                UUCrypto.gcmDecrypt(encryptedBytes, keyAlias).getOrNull() ?: return defValues

            val bis = ByteArrayInputStream(decrypted)
            val d = ObjectInputStream(bis)

            @Suppress("UNCHECKED_CAST")
            val obj = d.readObject() as Set<String>

            obj.toMutableSet()
        }
        catch (_: Exception)
        {
            defValues
        }
    }

    /**
     * Retrieves an integer value, automatically decrypting and parsing it if found.
     *
     * @param key The preference key to retrieve.
     * @param defValue The default value to return if the key is not found or parsing fails.
     * @return The decrypted integer value, or [defValue] if not found or parsing fails.
     *
     * @since 1.0.0
     */
    override fun getInt(key: String?, defValue: Int): Int
    {
        return getString(key, null)?.toIntOrNull() ?: defValue
    }

    /**
     * Retrieves a long value, automatically decrypting and parsing it if found.
     *
     * @param key The preference key to retrieve.
     * @param defValue The default value to return if the key is not found or parsing fails.
     * @return The decrypted long value, or [defValue] if not found or parsing fails.
     *
     * @since 1.0.0
     */
    override fun getLong(key: String?, defValue: Long): Long
    {
        return getString(key, null)?.toLongOrNull() ?: defValue
    }

    /**
     * Retrieves a float value, automatically decrypting and parsing it if found.
     *
     * @param key The preference key to retrieve.
     * @param defValue The default value to return if the key is not found or parsing fails.
     * @return The decrypted float value, or [defValue] if not found or parsing fails.
     *
     * @since 1.0.0
     */
    override fun getFloat(key: String?, defValue: Float): Float
    {
        return getString(key, null)?.toFloatOrNull() ?: defValue
    }

    /**
     * Retrieves a boolean value, automatically decrypting and parsing it if found.
     *
     * @param key The preference key to retrieve.
     * @param defValue The default value to return if the key is not found or parsing fails.
     * @return The decrypted boolean value, or [defValue] if not found or parsing fails.
     *
     * @since 1.0.0
     */
    override fun getBoolean(key: String?, defValue: Boolean): Boolean
    {
        return getString(key, null)?.toBooleanStrictOrNull() ?: defValue
    }

    /**
     * Checks whether a preference key exists in the underlying storage.
     *
     * Note: This only checks for the presence of the key, not whether the value
     * can be successfully decrypted.
     *
     * @param key The preference key to check.
     * @return `true` if the key exists, `false` otherwise.
     *
     * @since 1.0.0
     */
    override fun contains(key: String?): Boolean
    {
        return delegate.contains(key)
    }

    /**
     * Returns an [EncryptedEditor] instance for modifying encrypted preferences.
     *
     * Changes made through the editor are automatically encrypted before being
     * persisted to the underlying [SharedPreferences].
     *
     * @return A new [EncryptedEditor] instance.
     *
     * @since 1.0.0
     */
    override fun edit(): SharedPreferences.Editor
    {
        return EncryptedEditor(delegate.edit(), keyAlias)
    }

    /**
     * Registers a listener to be notified when preferences change.
     *
     * The listener is registered with the underlying [SharedPreferences] delegate.
     *
     * @param listener The listener to register, or `null` to ignore.
     *
     * @since 1.0.0
     */
    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?)
    {
        delegate.registerOnSharedPreferenceChangeListener(listener)
    }

    /**
     * Unregisters a previously registered preference change listener.
     *
     * @param listener The listener to unregister, or `null` to ignore.
     *
     * @since 1.0.0
     */
    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?)
    {
        delegate.unregisterOnSharedPreferenceChangeListener(listener)
    }

    /**
     * An editor for modifying encrypted preferences.
     *
     * All values written through this editor are automatically encrypted using
     * [UUCrypto] before being persisted. The encryption uses the same [keyAlias]
     * as the parent [UUEncryptedSharedPreferences] instance.
     *
     * @since 1.0.0
     */
    class EncryptedEditor(
        /**
         * The underlying [SharedPreferences.Editor] that performs the actual storage operations.
         *
         * @since 1.0.0
         */
        private val delegate: SharedPreferences.Editor,
        /**
         * The key alias used for encryption operations with [UUCrypto].
         *
         * @since 1.0.0
         */
        private val keyAlias: String
    ) : SharedPreferences.Editor
    {
        /**
         * Encrypts and stores a string value.
         *
         * If [value] is `null`, the key is removed from preferences.
         * If [key] is `null`, this operation is ignored.
         *
         * @param key The preference key to store the value under.
         * @param value The string value to encrypt and store, or `null` to remove the key.
         * @return This editor instance for method chaining.
         *
         * @since 1.0.0
         */
        override fun putString(key: String?, value: String?): SharedPreferences.Editor
        {
            if (key == null) return this
            if (value == null)
            {
                delegate.remove(key)
                return this
            }

            val encrypted = UUCrypto.gcmEncrypt(value.toByteArray(StandardCharsets.UTF_8), keyAlias)
                .getOrNull()
                ?.uuBase64()?.getOrNull()
            delegate.putString(key, encrypted)
            return this
        }

        /**
         * Encrypts and stores a set of string values.
         *
         * The set is serialized using Java object serialization before encryption.
         * If [values] is `null`, the key is removed from preferences.
         * If [key] is `null`, this operation is ignored.
         * If serialization or encryption fails, the operation is silently ignored.
         *
         * @param key The preference key to store the value under.
         * @param values The set of strings to serialize, encrypt, and store, or `null` to remove the key.
         * @return This editor instance for method chaining.
         *
         * @since 1.0.0
         */
        override fun putStringSet(key: String?, values: MutableSet<String>?): SharedPreferences.Editor
        {
            try
            {
                if (key == null) return this
                if (values == null) {
                    delegate.remove(key)
                    return this
                }

                val bos = ByteArrayOutputStream()
                ObjectOutputStream(bos).use { it.writeObject(values) }
                val bytes = bos.toByteArray()
                val encrypted = UUCrypto.gcmEncrypt(bytes, keyAlias)
                    .getOrNull()
                    ?.uuBase64()?.getOrNull()
                delegate.putString(key, encrypted)
            }
            catch (_: Exception)
            {

            }

            return this
        }

        /**
         * Encrypts and stores an integer value as a string.
         *
         * The integer is converted to a string representation before encryption.
         *
         * @param key The preference key to store the value under.
         * @param value The integer value to encrypt and store.
         * @return This editor instance for method chaining.
         *
         * @since 1.0.0
         */
        override fun putInt(key: String?, value: Int): SharedPreferences.Editor
        {
            return putString(key, value.toString())
        }

        /**
         * Encrypts and stores a long value as a string.
         *
         * The long is converted to a string representation before encryption.
         *
         * @param key The preference key to store the value under.
         * @param value The long value to encrypt and store.
         * @return This editor instance for method chaining.
         *
         * @since 1.0.0
         */
        override fun putLong(key: String?, value: Long): SharedPreferences.Editor
        {
            return putString(key, value.toString())
        }

        /**
         * Encrypts and stores a float value as a string.
         *
         * The float is converted to a string representation before encryption.
         *
         * @param key The preference key to store the value under.
         * @param value The float value to encrypt and store.
         * @return This editor instance for method chaining.
         *
         * @since 1.0.0
         */
        override fun putFloat(key: String?, value: Float): SharedPreferences.Editor
        {
            return putString(key, value.toString())
        }

        /**
         * Encrypts and stores a boolean value as a string.
         *
         * The boolean is converted to a string representation before encryption.
         *
         * @param key The preference key to store the value under.
         * @param value The boolean value to encrypt and store.
         * @return This editor instance for method chaining.
         *
         * @since 1.0.0
         */
        override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor
        {
            return putString(key, value.toString())
        }

        /**
         * Removes a preference key from storage.
         *
         * If [key] is `null`, this operation is ignored.
         *
         * @param key The preference key to remove, or `null` to ignore.
         * @return This editor instance for method chaining.
         *
         * @since 1.0.0
         */
        override fun remove(key: String?): SharedPreferences.Editor
        {
            if (key != null) delegate.remove(key)
            return this
        }

        /**
         * Removes all preferences from storage.
         *
         * @return This editor instance for method chaining.
         *
         * @since 1.0.0
         */
        override fun clear(): SharedPreferences.Editor
        {
            delegate.clear()
            return this
        }

        /**
         * Commits the changes synchronously.
         *
         * @return `true` if the commit was successful, `false` otherwise.
         *
         * @since 1.0.0
         */
        override fun commit(): Boolean = delegate.commit()

        /**
         * Applies the changes asynchronously.
         *
         * Changes are written to persistent storage asynchronously. This method
         * returns immediately without waiting for the write to complete.
         *
         * @since 1.0.0
         */
        override fun apply() = delegate.apply()
    }
}
