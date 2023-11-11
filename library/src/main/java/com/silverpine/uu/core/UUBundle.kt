package com.silverpine.uu.core

import android.os.Bundle

fun <T: Any> Bundle.uuPutJson(key: String, obj: T?, objectClass: Class<T>)
{
    val json = UUJson.toJson(obj, objectClass)
    putString(key, json)
}

fun <T: Any> Bundle.uuGetJsonObject(key: String, objectClass: Class<T>): T?
{
    val json = getString(key)
    return UUJson.fromString(json, objectClass)
}