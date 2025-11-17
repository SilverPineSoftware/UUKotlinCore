package com.silverpine.uu.core.test.fakes

import android.content.SharedPreferences

class UUFakeSharedPreferences : SharedPreferences
{
    private val data = mutableMapOf<String, String?>()
    private val listeners = mutableSetOf<SharedPreferences.OnSharedPreferenceChangeListener>()

    override fun getAll(): MutableMap<String, *>
    {
        // Return raw stored values; wrapper under test should not call this
        return data.toMutableMap()
    }

    override fun getString(key: String?, defValue: String?): String?
    {
        if (key == null) return defValue
        return data[key] ?: defValue
    }

    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>?
    {
        throw UnsupportedOperationException("StringSet not supported")
    }

    override fun getInt(key: String?, defValue: Int): Int
    {
        val v = getString(key, null) ?: return defValue
        return v.toIntOrNull() ?: defValue
    }

    override fun getLong(key: String?, defValue: Long): Long
    {
        val v = getString(key, null) ?: return defValue
        return v.toLongOrNull() ?: defValue
    }

    override fun getFloat(key: String?, defValue: Float): Float
    {
        val v = getString(key, null) ?: return defValue
        return v.toFloatOrNull() ?: defValue
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean
    {
        val v = getString(key, null) ?: return defValue
        return v.toBooleanStrictOrNull() ?: defValue
    }

    override fun contains(key: String?): Boolean
    {
        if (key == null) return false
        return data.containsKey(key)
    }

    override fun edit(): SharedPreferences.Editor
    {
        return EditorImpl()
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?)
    {
        if (listener != null) listeners.add(listener)
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?)
    {
        if (listener != null) listeners.remove(listener)
    }

    private inner class EditorImpl : SharedPreferences.Editor
    {
        private val pending = mutableMapOf<String, String?>()
        private var clearCalled = false
        private val removed = mutableSetOf<String>()

        override fun putString(key: String?, value: String?): SharedPreferences.Editor
        {
            if (key != null)
            {
                pending[key] = value
                removed.remove(key)
            }

            return this
        }

        override fun putStringSet(key: String?, values: MutableSet<String>?): SharedPreferences.Editor
        {
            throw UnsupportedOperationException("StringSet not supported")
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
            if (key != null)
            {
                removed.add(key)
                pending.remove(key)
            }
            return this
        }

        override fun clear(): SharedPreferences.Editor
        {
            clearCalled = true
            pending.clear()
            removed.clear()
            return this
        }

        override fun commit(): Boolean
        {
            apply() // behave the same for tests
            return true
        }

        override fun apply()
        {
            if (clearCalled)
            {
                val keys = data.keys.toList()
                keys.forEach { k ->
                    data.remove(k)
                    notifyChanged(k)
                }
                clearCalled = false
            }
            removed.forEach { k ->
                if (data.remove(k) != null) notifyChanged(k)
            }
            pending.forEach { (k, v) ->
                data[k] = v
                notifyChanged(k)
            }
            pending.clear()
            removed.clear()
        }

        private fun notifyChanged(key: String)
        {
            listeners.forEach { it.onSharedPreferenceChanged(this@UUFakeSharedPreferences, key) }
        }
    }
}