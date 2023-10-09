package com.silverpine.uu.core.test.models

import androidx.annotation.Keep
import com.silverpine.uu.core.UURandom
import kotlinx.serialization.Serializable

@Keep
@Serializable
open class GenericsBaseModel<T: Any>
{
    var id: String = ""
    var payload: T? = null

    override fun equals(other: Any?): Boolean
    {
        val o = (other as? GenericsBaseModel<*>) ?: return false

        return(
                id == o.id &&
                payload == o.payload
        )
    }
}

@Keep
@Serializable
open class GenericsConcreteModel: GenericsBaseModel<String>()
{
    var count: Int = 0

    companion object
    {
        fun default(): GenericsConcreteModel
        {
            return GenericsConcreteModel()
        }

        fun random(): GenericsConcreteModel
        {
            val obj = GenericsConcreteModel()
            obj.id = UURandom.uuid()
            obj.payload = UURandom.uuid()
            obj.count = UURandom.int()
            return obj
        }

        fun min(): GenericsConcreteModel
        {
            val obj = GenericsConcreteModel()
            obj.id = ""
            obj.payload = ""
            obj.count = Int.MIN_VALUE
            return obj
        }

        fun max(): GenericsConcreteModel
        {
            val obj = GenericsConcreteModel()
            obj.id = "Max"
            obj.payload = "Max"
            obj.count = Int.MAX_VALUE
            return obj
        }
    }

    override fun equals(other: Any?): Boolean
    {
        val o = (other as? GenericsConcreteModel) ?: return false

        return (super.equals(other) &&
                count == o.count)
    }
}