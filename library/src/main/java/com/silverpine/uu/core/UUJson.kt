package com.silverpine.uu.core

import com.silverpine.uu.logging.UULog
import com.squareup.moshi.Moshi


object UUJson
{
    private var moshi: Moshi? = null

    fun init(moshi: Moshi)
    {
        this.moshi = moshi
    }

    private fun requireMoshi(): Moshi
    {
        if (moshi == null)
        {
            throw RuntimeException("Moshi is null. Must call init(Moshi) before use.")
        }

        return moshi!!
    }

    fun <T> toJson(obj: T?, objectClass: Class<T>): String?
    {
        try
        {
            val moshi = requireMoshi()
            val jsonAdapter = moshi.adapter(objectClass)
            return jsonAdapter.toJson(obj)
        }
        catch (ex: Exception)
        {
            UULog.d(javaClass, "toJson", "", ex)
            return null
        }
    }

    fun <T> fromJson(source: String?, objectClass: Class<T>): T?
    {
        try
        {
            val moshi = requireMoshi()
            val src = source ?: return null

            val jsonAdapter = moshi.adapter(objectClass)
            return jsonAdapter.fromJson(src)
        }
        catch (ex: Exception)
        {
            UULog.d(javaClass, "fromJson", "", ex)
            return null
        }
    }
}

/*
fun Any.uuToJson(): String?
{
    return UUJson.toJson(this, javaClass)
}

fun <T> Class<T>.uuFromJson(source: String?): T?
{
    return UUJson.fromJson(source, this)
}*/


/*
@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class SerializeNulls
{
    companion object
    {
        var JSON_ADAPTER_FACTORY: JsonAdapter.Factory = object : JsonAdapter.Factory
        {
            @Nullable
            override fun create(
                type: Type?,
                annotations: Set<Annotation?>?,
                moshi: Moshi
            ): JsonAdapter<*>?
            {
                val nextAnnotations: Set<Annotation?> = Types.nextAnnotations(
                    annotations,
                    SerializeNulls::class.java
                ) ?: return null

                return moshi.nextAdapter(this, type, nextAnnotations).serializeNulls()
            }
        }
    }
}*/
