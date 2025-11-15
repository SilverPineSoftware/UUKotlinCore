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
    private val delegate: SharedPreferences,
    private val keyAlias: String = "com.silverpine.uu.core.security.UUEncryptedSharedPreferences"
) : SharedPreferences
{
    override fun getAll(): MutableMap<String, *>
    {
        // Not safe to expose decrypted values in bulk
        throw UnsupportedOperationException("getAll() not supported for encrypted prefs")
    }

    override fun getString(key: String?, defValue: String?): String?
    {
        val encryptedString = delegate.getString(key, null) ?: return defValue
        val encryptedBytes = encryptedString.uuFromBase64().getOrNull() ?: return defValue
        val decrypted = UUCrypto.gcmDecrypt(encryptedBytes, keyAlias)
        return decrypted.getOrNull()?.toString(StandardCharsets.UTF_8) ?: defValue
    }

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

    override fun getInt(key: String?, defValue: Int): Int
    {
        return getString(key, null)?.toIntOrNull() ?: defValue
    }

    override fun getLong(key: String?, defValue: Long): Long
    {
        return getString(key, null)?.toLongOrNull() ?: defValue
    }

    override fun getFloat(key: String?, defValue: Float): Float
    {
        return getString(key, null)?.toFloatOrNull() ?: defValue
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean
    {
        return getString(key, null)?.toBooleanStrictOrNull() ?: defValue
    }

    override fun contains(key: String?): Boolean
    {
        return delegate.contains(key)
    }

    override fun edit(): SharedPreferences.Editor
    {
        return Editor(delegate.edit(), keyAlias)
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?)
    {
        delegate.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?)
    {
        delegate.unregisterOnSharedPreferenceChangeListener(listener)
    }

    class Editor(
        private val delegate: SharedPreferences.Editor,
        private val keyAlias: String
    ) : SharedPreferences.Editor
    {
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

        override fun putInt(key: String?, value: Int): SharedPreferences.Editor
        {
            return putString(key, value.toString())
        }

        override fun putLong(key: String?, value: Long): SharedPreferences.Editor
        {
            return putString(key, value.toString())
        }

        override fun putFloat(key: String?, value: Float): SharedPreferences.Editor
        {
            return putString(key, value.toString())
        }

        override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor
        {
            return putString(key, value.toString())
        }

        override fun remove(key: String?): SharedPreferences.Editor
        {
            if (key != null) delegate.remove(key)
            return this
        }

        override fun clear(): SharedPreferences.Editor
        {
            delegate.clear()
            return this
        }

        override fun commit(): Boolean = delegate.commit()
        override fun apply() = delegate.apply()
    }
}
