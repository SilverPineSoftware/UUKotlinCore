package com.silverpine.uu.core.test.models

import androidx.annotation.Keep
import com.silverpine.uu.core.UURandom
import kotlinx.serialization.Serializable

@Keep
@Serializable
open class NullsModel
{
    var aBool: Boolean? = null
    var aByte: Byte? = null
    var aShort: Short? = null
    var anInt: Int? = null
    var aLong: Long? = null
    var aFloat: Float? = null
    var aDouble: Double? = null
    var aUByte: UByte? = null
    var aUShort: UShort? = null
    var aUInt: UInt? = null
    var aULong: ULong? = null

    companion object
    {
        fun default(): NullsModel
        {
            return NullsModel()
        }

        fun random(): NullsModel
        {
            val obj = NullsModel()
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

        fun min(): NullsModel
        {
            val obj = NullsModel()
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

        fun max(): NullsModel
        {
            val obj = NullsModel()
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
        val o = (other as? NullsModel) ?: return false

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
        var result = aBool?.hashCode() ?: 0
        result = 31 * result + (aByte ?: 0)
        result = 31 * result + (aShort ?: 0)
        result = 31 * result + (anInt ?: 0)
        result = 31 * result + (aLong?.hashCode() ?: 0)
        result = 31 * result + (aFloat?.hashCode() ?: 0)
        result = 31 * result + (aDouble?.hashCode() ?: 0)
        result = 31 * result + (aUByte?.hashCode() ?: 0)
        result = 31 * result + (aUShort?.hashCode() ?: 0)
        result = 31 * result + (aUInt?.hashCode() ?: 0)
        result = 31 * result + (aULong?.hashCode() ?: 0)
        return result
    }
}