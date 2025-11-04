package com.silverpine.uu.core

/**
 * Interface for persistent key-value storage of preferences.
 * 
 * Provides a simple abstraction for storing and retrieving application preferences
 * with support for various data types. This interface is platform-agnostic and can
 * be implemented for different storage backends (e.g., Android SharedPreferences).
 * 
 * All get operations return a default value if the key doesn't exist or if retrieval fails.
 * Setting a value to `null` typically removes the key from storage.
 * 
 * @sample
 * ```kotlin
 * val prefs: UUPrefs = // implementation
 * 
 * // Store values
 * prefs.putString("username", "john_doe")
 * prefs.putInt("user_id", 12345)
 * prefs.putBoolean("is_logged_in", true)
 * 
 * // Retrieve values
 * val username = prefs.getString("username", "guest")
 * val userId = prefs.getInt("user_id", 0)
 * val isLoggedIn = prefs.getBoolean("is_logged_in", false)
 * 
 * // Remove a key
 * prefs.remove("username")
 * 
 * // Clear all preferences
 * prefs.clear()
 * ```
 */
interface UUPrefs
{
    /**
     * Retrieves a string value from preferences.
     * 
     * @param key The key identifying the preference
     * @param defaultValue The value to return if the key doesn't exist or is null. Defaults to `null`
     * @return The string value associated with the key, or `defaultValue` if not found
     */
    fun getString(key: String, defaultValue: String? = null): String?
    
    /**
     * Retrieves a set of strings from preferences.
     * 
     * @param key The key identifying the preference
     * @param defaultValue The value to return if the key doesn't exist or is null. Defaults to `null`
     * @return The set of strings associated with the key, or `defaultValue` if not found
     */
    fun getStringSet(key: String, defaultValue: Set<String>? = null): Set<String>?
    
    /**
     * Retrieves an integer value from preferences.
     * 
     * @param key The key identifying the preference
     * @param defaultValue The value to return if the key doesn't exist. Defaults to `0`
     * @return The integer value associated with the key, or `defaultValue` if not found
     */
    fun getInt(key: String, defaultValue: Int = 0): Int
    
    /**
     * Retrieves a long value from preferences.
     * 
     * @param key The key identifying the preference
     * @param defaultValue The value to return if the key doesn't exist. Defaults to `0`
     * @return The long value associated with the key, or `defaultValue` if not found
     */
    fun getLong(key: String, defaultValue: Long = 0): Long
    
    /**
     * Retrieves a float value from preferences.
     * 
     * @param key The key identifying the preference
     * @param defaultValue The value to return if the key doesn't exist. Defaults to `0.0f`
     * @return The float value associated with the key, or `defaultValue` if not found
     */
    fun getFloat(key: String, defaultValue: Float = 0.0f): Float
    
    /**
     * Retrieves a double value from preferences.
     * 
     * @param key The key identifying the preference
     * @param defaultValue The value to return if the key doesn't exist. Defaults to `0.0`
     * @return The double value associated with the key, or `defaultValue` if not found
     */
    fun getDouble(key: String, defaultValue: Double = 0.0): Double
    
    /**
     * Retrieves a boolean value from preferences.
     * 
     * @param key The key identifying the preference
     * @param defaultValue The value to return if the key doesn't exist. Defaults to `false`
     * @return The boolean value associated with the key, or `defaultValue` if not found
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
    
    /**
     * Retrieves a byte array from preferences.
     * 
     * @param key The key identifying the preference
     * @param defaultValue The value to return if the key doesn't exist or is null. Defaults to `null`
     * @return The byte array associated with the key, or `defaultValue` if not found
     */
    fun getData(key: String, defaultValue: ByteArray? = null): ByteArray?
    
    /**
     * Retrieves an enum value from preferences.
     * 
     * The enum is stored as a string (using the enum's `name` property) and converted back
     * to the enum type when retrieved. If the stored string doesn't match any enum value,
     * the `defaultValue` is returned.
     * 
     * @param T The enum type to retrieve
     * @param key The key identifying the preference
     * @param enumClass The class of the enum type to retrieve
     * @param defaultValue The value to return if the key doesn't exist, is null, or doesn't match any enum value. Defaults to `null`
     * @return The enum value associated with the key, or `defaultValue` if not found or invalid
     * 
     * @sample
     * ```kotlin
     * enum class Theme { LIGHT, DARK, AUTO }
     * 
     * // Store enum
     * prefs.putEnum("theme", Theme.DARK)
     * 
     * // Retrieve enum
     * val theme = prefs.getEnum("theme", Theme::class.java, Theme.LIGHT)
     * // Returns Theme.DARK if stored, or Theme.LIGHT if not found
     * ```
     */
    fun <T: Enum<T>> getEnum(key: String, enumClass: Class<T>, defaultValue: T? = null): T?
    /**
     * Stores a string value in preferences.
     * 
     * If `value` is `null`, the key will typically be removed from storage.
     * 
     * @param key The key identifying the preference
     * @param value The string value to store, or `null` to remove the key
     */
    fun putString(key: String, value: String?)
    
    /**
     * Stores a set of strings in preferences.
     * 
     * If `value` is `null`, the key will typically be removed from storage.
     * 
     * @param key The key identifying the preference
     * @param value The set of strings to store, or `null` to remove the key
     */
    fun putStringSet(key: String, value: Set<String>?)
    
    /**
     * Stores an integer value in preferences.
     * 
     * @param key The key identifying the preference
     * @param value The integer value to store
     */
    fun putInt(key: String, value: Int)
    
    /**
     * Stores a long value in preferences.
     * 
     * @param key The key identifying the preference
     * @param value The long value to store
     */
    fun putLong(key: String, value: Long)
    
    /**
     * Stores a float value in preferences.
     * 
     * @param key The key identifying the preference
     * @param value The float value to store
     */
    fun putFloat(key: String, value: Float)
    
    /**
     * Stores a double value in preferences.
     * 
     * @param key The key identifying the preference
     * @param value The double value to store
     */
    fun putDouble(key: String, value: Double)
    
    /**
     * Stores a boolean value in preferences.
     * 
     * @param key The key identifying the preference
     * @param value The boolean value to store
     */
    fun putBoolean(key: String, value: Boolean)
    
    /**
     * Stores a byte array in preferences.
     * 
     * If `value` is `null`, the key will typically be removed from storage.
     * 
     * @param key The key identifying the preference
     * @param value The byte array to store, or `null` to remove the key
     */
    fun putData(key: String, value: ByteArray?)
    
    /**
     * Stores an enum value in preferences.
     * 
     * The enum is stored as a string using its `name` property. If `value` is `null`,
     * the key will typically be removed from storage.
     * 
     * @param T The enum type to store
     * @param key The key identifying the preference
     * @param value The enum value to store, or `null` to remove the key
     * 
     * @sample
     * ```kotlin
     * enum class Theme { LIGHT, DARK, AUTO }
     * 
     * prefs.putEnum("theme", Theme.DARK)
     * // Stores "DARK" as a string
     * 
     * prefs.putEnum("theme", null)
     * // Removes the "theme" key
     * ```
     */
    fun <T: Enum<T>> putEnum(key: String, value: T?)
    
    /**
     * Removes a preference entry by its key.
     * 
     * If the key doesn't exist, this operation is typically a no-op.
     * 
     * @param key The key identifying the preference to remove
     */
    fun remove(key: String)
    
    /**
     * Removes all preferences from storage.
     * 
     * This operation clears all key-value pairs stored by this preferences instance.
     * Use with caution as this cannot be undone.
     */
    fun clear()
}