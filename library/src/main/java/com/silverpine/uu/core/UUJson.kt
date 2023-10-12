package com.silverpine.uu.core

import kotlinx.serialization.json.Json
import java.io.InputStream

object UUJson
{
    private var provider: UUJsonProvider = UUKotlinXJsonProvider(Json.Default)

    fun init(provider: UUJsonProvider)
    {
        this.provider = provider
    }

    fun <T: Any> toJson(obj: T?, objectClass: Class<T>): String?
    {
        return provider.toJson(obj, objectClass)
    }

    fun <T: Any> fromString(source: String?, objectClass: Class<T>): T?
    {
        return provider.fromString(source, objectClass)
    }

    fun <T: Any> fromStream(source: InputStream?, objectClass: Class<T>): T?
    {
        return provider.fromStream(source, objectClass)
    }

    fun <T: Any> fromBytes(source: ByteArray?, objectClass: Class<T>): T?
    {
        return provider.fromBytes(source, objectClass)
    }
}