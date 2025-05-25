package com.silverpine.uu.core.test.models

import androidx.annotation.Keep
import com.silverpine.uu.core.UURandom
import kotlinx.serialization.Serializable

@Keep
@Serializable
open class PrimitiveModel
{
    var aBool: Boolean = false
    var aByte: Byte = 0
    var aShort: Short = 0
    var anInt: Int = 0
    var aLong: Long = 0
    var aFloat: Float = 0.0f
    var aDouble: Double = 0.0
    var aUByte: UByte = 0u
    var aUShort: UShort = 0u
    var aUInt: UInt = 0u
    var aULong: ULong = 0u

    companion object
    {
        fun default(): PrimitiveModel
        {
            return PrimitiveModel()
        }

        fun random(): PrimitiveModel
        {
            val obj = PrimitiveModel()
            obj.aBool = UURandom.bool()
            obj.aByte = UURandom.byte()
            obj.aShort = UURandom.short()
            obj.anInt = UURandom.int()
            obj.aLong = UURandom.long()
            obj.aFloat = UURandom.float()
            obj.aDouble = UURandom.double()
            obj.aUByte = UURandom.uByte()
            obj.aUShort = UURandom.uShort()
            obj.aUInt = UURandom.uInt()
            obj.aULong = UURandom.uLong()
            return obj
        }

        fun min(): PrimitiveModel
        {
            val obj = PrimitiveModel()
            obj.aBool = false
            obj.aByte = Byte.MIN_VALUE
            obj.aShort = Short.MIN_VALUE
            obj.anInt = Int.MIN_VALUE
            obj.aLong = Long.MIN_VALUE
            obj.aFloat = Float.MIN_VALUE
            obj.aDouble = Double.MIN_VALUE
            obj.aUByte = UByte.MIN_VALUE
            obj.aUShort = UShort.MIN_VALUE
            obj.aUInt = UInt.MIN_VALUE
            obj.aULong = ULong.MIN_VALUE
            return obj
        }

        fun max(): PrimitiveModel
        {
            val obj = PrimitiveModel()
            obj.aByte = Byte.MAX_VALUE
            obj.aShort = Short.MAX_VALUE
            obj.anInt = Int.MAX_VALUE
            obj.aLong = Long.MAX_VALUE
            obj.aFloat = Float.MAX_VALUE
            obj.aDouble = Double.MAX_VALUE
            obj.aUByte = UByte.MAX_VALUE
            obj.aUShort = UShort.MAX_VALUE
            obj.aUInt = UInt.MAX_VALUE
            obj.aULong = ULong.MAX_VALUE
            return obj
        }
    }

    override fun equals(other: Any?): Boolean
    {
        val o = (other as? PrimitiveModel) ?: return false

        return(
                aBool == o.aBool &&
                aByte == o.aByte &&
                aShort == o.aShort &&
                anInt == o.anInt &&
                aLong == o.aLong &&
                aFloat == o.aFloat &&
                aDouble == o.aDouble &&
                aUByte == o.aUByte &&
                aUShort == o.aUShort &&
                aUInt == o.aUInt &&
                aULong == o.aULong
        )
    }

    override fun hashCode(): Int {
        var result = aBool.hashCode()
        result = 31 * result + aByte
        result = 31 * result + aShort
        result = 31 * result + anInt
        result = 31 * result + aLong.hashCode()
        result = 31 * result + aFloat.hashCode()
        result = 31 * result + aDouble.hashCode()
        result = 31 * result + aUByte.hashCode()
        result = 31 * result + aUShort.hashCode()
        result = 31 * result + aUInt.hashCode()
        result = 31 * result + aULong.hashCode()
        return result
    }
}