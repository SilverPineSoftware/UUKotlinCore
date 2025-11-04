package com.silverpine.uu.core.security

import android.content.Context
import android.content.SharedPreferences
import com.silverpine.uu.core.uuToHex
import com.silverpine.uu.core.uuToHexData

object UUSecurePrefs
{
    private lateinit var reader: SharedPreferences
    private lateinit var writer: SharedPreferences.Editor

    fun init(context: Context, name: String = "${context.packageName}-UUSecurePrefs")
    {
        reader = UUEncryptedSharedPreferences(context.getSharedPreferences(name, Context.MODE_PRIVATE))
        writer = reader.edit()
    }

    fun getString(key: String, defaultValue: String? = null): String?
    {
        requireReader()
        return reader.getString(key, defaultValue)
    }

    fun getStringSet(key: String, defaultValue: Set<String>? = null): Set<String>?
    {
        requireReader()
        return reader.getStringSet(key, defaultValue)
    }

    fun getInt(key: String, defaultValue: Int = 0): Int
    {
        requireReader()
        return reader.getInt(key, defaultValue)
    }

    fun getLong(key: String, defaultValue: Long = 0): Long
    {
        requireReader()
        return reader.getLong(key, defaultValue)
    }

    fun getFloat(key: String, defaultValue: Float = 0.0f): Float
    {
        requireReader()
        return reader.getFloat(key, defaultValue)
    }

    fun getDouble(key: String, defaultValue: Double = 0.0): Double
    {
        requireReader()
        return Double.fromBits(getLong(key, defaultValue.toBits()))
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
    {
        requireReader()
        return reader.getBoolean(key, defaultValue)
    }

    fun getData(key: String, defaultValue: ByteArray? = null): ByteArray?
    {
        return getString(key)?.uuToHexData() ?: defaultValue
    }

    fun putString(key: String, value: String?)
    {
        requireWriter()
        return writer.putString(key, value).apply()
    }

    fun putStringSet(key: String, value: Set<String>?)
    {
        requireWriter()
        return writer.putStringSet(key, value).apply()
    }

    fun putInt(key: String, value: Int)
    {
        requireWriter()
        return writer.putInt(key, value).apply()
    }

    fun putLong(key: String, value: Long)
    {
        requireWriter()
        return writer.putLong(key, value).apply()
    }

    fun putFloat(key: String, value: Float)
    {
        requireWriter()
        return writer.putFloat(key, value).apply()
    }

    fun putDouble(key: String, value: Double)
    {
        requireWriter()
        putLong(key, value.toBits())
    }

    fun putBoolean(key: String, value: Boolean)
    {
        requireWriter()
        return writer.putBoolean(key, value).apply()
    }

    fun putData(key: String, value: ByteArray?)
    {
        return putString(key, value?.uuToHex())
    }

    fun remove(key: String)
    {
        requireWriter()
        return writer.remove(key).apply()
    }

    fun clear()
    {
        requireWriter()
        return writer.clear().apply()
    }

    private fun requireReader()
    {
        if (!UUSecurePrefs::reader.isInitialized)
        {
            throw RuntimeException("Must call UUSecurePrefs.init(...) prior to first use!")
        }
    }

    private fun requireWriter()
    {
        if (!UUSecurePrefs::writer.isInitialized)
        {
            throw RuntimeException("Must call UUSecurePrefs.init(...) prior to first use!")
        }
    }
}