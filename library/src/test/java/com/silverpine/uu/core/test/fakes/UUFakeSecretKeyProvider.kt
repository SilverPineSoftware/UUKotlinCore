package com.silverpine.uu.core.test.fakes

import com.silverpine.uu.core.security.UUSecretKeyProvider
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class UUFakeSecretKeyProvider : UUSecretKeyProvider
{
    private val keys = mutableMapOf<String, SecretKey>()

    override fun loadGcmKey(alias: String, keySizeBits: Int): Result<SecretKey>
    {
        val key = keys.getOrPut(alias) {
            val kg = KeyGenerator.getInstance("AES")
            kg.init(keySizeBits)
            kg.generateKey()
        }
        return Result.success(key)
    }
}