package com.silverpine.uu.core.test.models

import UUDateJsonAdapter
import androidx.annotation.Keep
import com.silverpine.uu.core.UULongDateSerializer
import com.silverpine.uu.core.UURandom
import kotlinx.serialization.Serializable
import java.util.Date

@Keep
@Serializable
open class CustomSerializersModel
{
    @Serializable(UULongDateSerializer::class)
    var createdAt: Long = 0

    @Serializable(UUDateJsonAdapter::class)
    var updatedAt: Date = Date(0L)

    companion object
    {
        fun default(): CustomSerializersModel
        {
            return CustomSerializersModel()
        }

        fun random(): CustomSerializersModel
        {
            val o = CustomSerializersModel()
            o.createdAt = UURandom.uInt().toLong()
            o.updatedAt = Date(UURandom.uInt().toLong())
            return o
        }

        fun min(): CustomSerializersModel
        {
            // Date parsing barfs on negatives
            val o = CustomSerializersModel()
            o.createdAt = 0L
            o.updatedAt = Date(0L)
            return o
        }

        fun max(): CustomSerializersModel
        {
            val o = CustomSerializersModel()
            o.createdAt = Long.MAX_VALUE
            o.updatedAt = Date(Long.MAX_VALUE)
            return o
        }
    }

    override fun equals(other: Any?): Boolean
    {
        val o = (other as? CustomSerializersModel) ?: return false

        return (
                createdAt == o.createdAt &&
                updatedAt == o.updatedAt
                )
    }

    override fun hashCode(): Int
    {
        return javaClass.hashCode()
    }
}