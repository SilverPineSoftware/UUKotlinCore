package com.silverpine.uu.core.security

import android.content.Context
import android.content.SharedPreferences
import com.silverpine.uu.core.UUPrefs
import com.silverpine.uu.core.security.UUSecurePrefs.init
import com.silverpine.uu.core.uuToHex
import com.silverpine.uu.core.uuToHexData

/**
 * Singleton implementation of [UUPrefs] that provides encrypted storage for preferences.
 * 
 * This object uses [UUEncryptedSharedPreferences] to securely store preference data with encryption.
 * All data stored through this interface is encrypted at rest, providing an additional layer
 * of security for sensitive application preferences.
 * 
 * **Important**: You must call [init] before using any of the get or put methods, otherwise
 * a [RuntimeException] will be thrown.
 * 
 * The default storage name is based on the application package name, but can be customized
 * via the [init] method.
 * 
 * @since 1.0.0
 * @sample
 * ```kotlin
 * // Initialize with default name
 * UUSecurePrefs.init(context)
 * 
 * // Initialize with custom name
 * UUSecurePrefs.init(context, "my-custom-prefs-name")
 * 
 * // Use encrypted preferences
 * UUSecurePrefs.putString("api_key", "secret-key")
 * val apiKey = UUSecurePrefs.getString("api_key", null)
 * 
 * // Store sensitive data
 * UUSecurePrefs.putData("encrypted_data", byteArray)
 * val data = UUSecurePrefs.getData("encrypted_data")
 * ```
 */
object UUSecurePrefs: UUPrefs
{
    private lateinit var reader: SharedPreferences
    private lateinit var writer: SharedPreferences.Editor

    /**
     * Initializes the secure preferences with the given context and optional name.
     * 
     * This method must be called before any get or put operations. If not initialized,
     * all operations will throw a [RuntimeException].
     * 
     * @since 1.0.0
     * @param context The Android application context
     * @param name The name of the SharedPreferences file. Defaults to `"{packageName}-UUSecurePrefs"`
     */
    fun init(context: Context, name: String = "${context.packageName}-UUSecurePrefs")
    {
        reader = UUEncryptedSharedPreferences(context.getSharedPreferences(name, Context.MODE_PRIVATE))
        writer = reader.edit()
    }

    /**
     * Retrieves an encrypted string value from secure preferences.
     * 
     * The value is automatically decrypted when retrieved.
     * 
     * @since 1.0.0
     * @param key The key identifying the preference
     * @param defaultValue The value to return if the key doesn't exist or is null
     * @return The decrypted string value, or `defaultValue` if not found
     * @throws RuntimeException if [init] has not been called
     */
    override fun getString(key: String, defaultValue: String?): String?
    {
        requireReader()
        return reader.getString(key, defaultValue)
    }

    /**
     * Retrieves an encrypted set of strings from secure preferences.
     * 
     * The values are automatically decrypted when retrieved.
     * 
     * @since 1.0.0
     * @param key The key identifying the preference
     * @param defaultValue The value to return if the key doesn't exist or is null
     * @return The decrypted set of strings, or `defaultValue` if not found
     * @throws RuntimeException if [init] has not been called
     */
    override fun getStringSet(key: String, defaultValue: Set<String>?): Set<String>?
    {
        requireReader()
        return reader.getStringSet(key, defaultValue)
    }

    /**
     * Retrieves an encrypted integer value from secure preferences.
     * 
     * The value is automatically decrypted when retrieved.
     * 
     * @since 1.0.0
     * @param key The key identifying the preference
     * @param defaultValue The value to return if the key doesn't exist. Defaults to `0`
     * @return The decrypted integer value, or `defaultValue` if not found
     * @throws RuntimeException if [init] has not been called
     */
    override fun getInt(key: String, defaultValue: Int): Int
    {
        requireReader()
        return reader.getInt(key, defaultValue)
    }

    /**
     * Retrieves an encrypted long value from secure preferences.
     * 
     * The value is automatically decrypted when retrieved.
     * 
     * @since 1.0.0
     * @param key The key identifying the preference
     * @param defaultValue The value to return if the key doesn't exist. Defaults to `0`
     * @return The decrypted long value, or `defaultValue` if not found
     * @throws RuntimeException if [init] has not been called
     */
    override fun getLong(key: String, defaultValue: Long): Long
    {
        requireReader()
        return reader.getLong(key, defaultValue)
    }

    /**
     * Retrieves an encrypted float value from secure preferences.
     * 
     * The value is automatically decrypted when retrieved.
     * 
     * @since 1.0.0
     * @param key The key identifying the preference
     * @param defaultValue The value to return if the key doesn't exist. Defaults to `0.0f`
     * @return The decrypted float value, or `defaultValue` if not found
     * @throws RuntimeException if [init] has not been called
     */
    override fun getFloat(key: String, defaultValue: Float): Float
    {
        requireReader()
        return reader.getFloat(key, defaultValue)
    }

    /**
     * Retrieves an encrypted double value from secure preferences.
     * 
     * The value is stored as a long (using [Double.toBits]) and automatically
     * decrypted when retrieved.
     * 
     * @since 1.0.0
     * @param key The key identifying the preference
     * @param defaultValue The value to return if the key doesn't exist. Defaults to `0.0`
     * @return The decrypted double value, or `defaultValue` if not found
     * @throws RuntimeException if [init] has not been called
     */
    override fun getDouble(key: String, defaultValue: Double): Double
    {
        requireReader()
        return Double.fromBits(getLong(key, defaultValue.toBits()))
    }

    /**
     * Retrieves an encrypted boolean value from secure preferences.
     * 
     * The value is automatically decrypted when retrieved.
     * 
     * @since 1.0.0
     * @param key The key identifying the preference
     * @param defaultValue The value to return if the key doesn't exist. Defaults to `false`
     * @return The decrypted boolean value, or `defaultValue` if not found
     * @throws RuntimeException if [init] has not been called
     */
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean
    {
        requireReader()
        return reader.getBoolean(key, defaultValue)
    }

    /**
     * Retrieves encrypted byte array data from secure preferences.
     * 
     * The data is stored as a hex-encoded string and automatically decrypted
     * and decoded when retrieved.
     * 
     * @since 1.0.0
     * @param key The key identifying the preference
     * @param defaultValue The value to return if the key doesn't exist or is null
     * @return The decrypted byte array, or `defaultValue` if not found
     * @throws RuntimeException if [init] has not been called
     */
    override fun getData(key: String, defaultValue: ByteArray?): ByteArray?
    {
        return getString(key)?.uuToHexData() ?: defaultValue
    }

    /**
     * Retrieves an encrypted enum value from secure preferences.
     * 
     * The enum is stored as a string (using the enum's `name` property) and automatically
     * decrypted when retrieved. The string is then converted back to the enum type using
     * the provided `enumClass`. If the stored string doesn't match any enum value,
     * the `defaultValue` is returned.
     * 
     * @since 1.0.0
     * @param T The enum type to retrieve
     * @param key The key identifying the preference
     * @param enumClass The class of the enum type to retrieve
     * @param defaultValue The value to return if the key doesn't exist, is null, or doesn't match any enum value
     * @return The decrypted enum value, or `defaultValue` if not found or invalid
     * @throws RuntimeException if [init] has not been called
     */
    override fun <T: Enum<T>> getEnum(key: String, enumClass: Class<T>, defaultValue: T?): T?
    {
        requireReader()
        val storedString = getString(key, null) ?: return defaultValue
        
        return runCatching()
        {
            enumClass.enumConstants?.firstOrNull { it.name == storedString }
        }.getOrDefault(defaultValue)
    }

    /**
     * Stores a string value in secure preferences with encryption.
     * 
     * The value is automatically encrypted before being stored.
     * If `value` is `null`, the key will be removed from storage.
     * 
     * @since 1.0.0
     * @param key The key identifying the preference
     * @param value The string value to encrypt and store, or `null` to remove the key
     * @throws RuntimeException if [init] has not been called
     */
    override fun putString(key: String, value: String?)
    {
        requireWriter()
        return writer.putString(key, value).apply()
    }

    /**
     * Stores a set of strings in secure preferences with encryption.
     * 
     * The values are automatically encrypted before being stored.
     * If `value` is `null`, the key will be removed from storage.
     * 
     * @since 1.0.0
     * @param key The key identifying the preference
     * @param value The set of strings to encrypt and store, or `null` to remove the key
     * @throws RuntimeException if [init] has not been called
     */
    override fun putStringSet(key: String, value: Set<String>?)
    {
        requireWriter()
        return writer.putStringSet(key, value).apply()
    }

    /**
     * Stores an integer value in secure preferences with encryption.
     * 
     * The value is automatically encrypted before being stored.
     * 
     * @since 1.0.0
     * @param key The key identifying the preference
     * @param value The integer value to encrypt and store
     * @throws RuntimeException if [init] has not been called
     */
    override fun putInt(key: String, value: Int)
    {
        requireWriter()
        return writer.putInt(key, value).apply()
    }

    /**
     * Stores a long value in secure preferences with encryption.
     * 
     * The value is automatically encrypted before being stored.
     * 
     * @since 1.0.0
     * @param key The key identifying the preference
     * @param value The long value to encrypt and store
     * @throws RuntimeException if [init] has not been called
     */
    override fun putLong(key: String, value: Long)
    {
        requireWriter()
        return writer.putLong(key, value).apply()
    }

    /**
     * Stores a float value in secure preferences with encryption.
     * 
     * The value is automatically encrypted before being stored.
     * 
     * @since 1.0.0
     * @param key The key identifying the preference
     * @param value The float value to encrypt and store
     * @throws RuntimeException if [init] has not been called
     */
    override fun putFloat(key: String, value: Float)
    {
        requireWriter()
        return writer.putFloat(key, value).apply()
    }

    /**
     * Stores a double value in secure preferences with encryption.
     * 
     * The value is stored as a long (using [Double.toBits]) and automatically
     * encrypted before being stored.
     * 
     * @since 1.0.0
     * @param key The key identifying the preference
     * @param value The double value to encrypt and store
     * @throws RuntimeException if [init] has not been called
     */
    override fun putDouble(key: String, value: Double)
    {
        requireWriter()
        putLong(key, value.toBits())
    }

    /**
     * Stores a boolean value in secure preferences with encryption.
     * 
     * The value is automatically encrypted before being stored.
     * 
     * @since 1.0.0
     * @param key The key identifying the preference
     * @param value The boolean value to encrypt and store
     * @throws RuntimeException if [init] has not been called
     */
    override fun putBoolean(key: String, value: Boolean)
    {
        requireWriter()
        return writer.putBoolean(key, value).apply()
    }

    /**
     * Stores byte array data in secure preferences with encryption.
     * 
     * The data is hex-encoded and then encrypted before being stored.
     * If `value` is `null`, the key will be removed from storage.
     * 
     * @since 1.0.0
     * @param key The key identifying the preference
     * @param value The byte array to encrypt and store, or `null` to remove the key
     * @throws RuntimeException if [init] has not been called
     */
    override fun putData(key: String, value: ByteArray?)
    {
        return putString(key, value?.uuToHex())
    }

    /**
     * Stores an enum value in secure preferences with encryption.
     * 
     * The enum is stored as a string using its `name` property and automatically
     * encrypted before being stored. If `value` is `null`, the key will be removed
     * from storage.
     * 
     * @since 1.0.0
     * @param T The enum type to store
     * @param key The key identifying the preference
     * @param value The enum value to encrypt and store, or `null` to remove the key
     * @throws RuntimeException if [init] has not been called
     */
    override fun <T: Enum<T>> putEnum(key: String, value: T?)
    {
        return putString(key, value?.name)
    }

    /**
     * Removes an encrypted preference entry by its key.
     * 
     * If the key doesn't exist, this operation is typically a no-op.
     * 
     * @since 1.0.0
     * @param key The key identifying the preference to remove
     * @throws RuntimeException if [init] has not been called
     */
    override fun remove(key: String)
    {
        requireWriter()
        return writer.remove(key).apply()
    }

    /**
     * Removes all encrypted preferences from storage.
     * 
     * This operation clears all key-value pairs stored by this secure preferences instance.
     * Use with caution as this cannot be undone.
     * 
     * @since 1.0.0
     * @throws RuntimeException if [init] has not been called
     */
    override fun clear()
    {
        requireWriter()
        return writer.clear().apply()
    }

    /**
     * Ensures that the reader has been initialized.
     * 
     * @since 1.0.0
     * @throws RuntimeException if [init] has not been called
     */
    private fun requireReader()
    {
        if (!UUSecurePrefs::reader.isInitialized)
        {
            throw RuntimeException("Must call UUSecurePrefs.init(...) prior to first use!")
        }
    }

    /**
     * Ensures that the writer has been initialized.
     * 
     * @since 1.0.0
     * @throws RuntimeException if [init] has not been called
     */
    private fun requireWriter()
    {
        if (!UUSecurePrefs::writer.isInitialized)
        {
            throw RuntimeException("Must call UUSecurePrefs.init(...) prior to first use!")
        }
    }
}