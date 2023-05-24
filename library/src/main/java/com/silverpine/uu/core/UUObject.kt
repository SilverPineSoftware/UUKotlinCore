package com.silverpine.uu.core

object UUObject
{
    fun <T> safeCast(type: Class<T>?, obj: Any?): T?
    {
        var castedObj: T? = null

        try
        {
            if (obj != null && type != null && obj.javaClass.isAssignableFrom(type))
            {
                castedObj = type.cast(obj)
            }
        }
        catch (ex: Exception)
        {
            castedObj = null
        }

        return castedObj
    }
}