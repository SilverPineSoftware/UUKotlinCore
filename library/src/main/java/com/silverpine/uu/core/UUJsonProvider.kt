package com.silverpine.uu.core

import java.io.InputStream

interface UUJsonProvider
{
    fun <T: Any> toJson(obj: T?, objectClass: Class<T>): String?
    fun <T: Any> fromString(source: String?, objectClass: Class<T>): T?
    fun <T: Any> fromStream(source: InputStream?, objectClass: Class<T>): T?
    fun <T: Any> fromBytes(source: ByteArray?, objectClass: Class<T>): T?
}