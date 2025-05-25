package com.silverpine.uu.core.test.models

import androidx.annotation.Keep
import com.silverpine.uu.core.UURandom
import kotlinx.serialization.Serializable
import java.util.Objects

@Keep
@Serializable
open class PrimitiveListModel
{
    var aBool: List<Boolean> = listOf()
    var aByte: List<Byte> = listOf()
    var aShort: List<Short> = listOf()
    var anInt: List<Int> = listOf()
    var aLong: List<Long> = listOf()
    var aFloat: List<Float> = listOf()
    var aDouble: List<Double> = listOf()
    var aUByte: List<UByte> = listOf()
    var aUShort: List<UShort> = listOf()
    var aUInt: List<UInt> = listOf()
    var aULong: List<ULong> = listOf()

    companion object
    {
        fun default(): PrimitiveListModel
        {
            return PrimitiveListModel()
        }

        fun random(): PrimitiveListModel
        {
            val obj = PrimitiveListModel()
            obj.aBool = UURandom.boolObjArray(randomCount()).toList()
            obj.aByte = UURandom.byteObjArray(randomCount()).toList()
            obj.aShort = UURandom.shortObjArray(randomCount()).toList()
            obj.anInt = UURandom.intObjArray(randomCount()).toList()
            obj.aLong = UURandom.longObjArray(randomCount()).toList()
            obj.aFloat = UURandom.floatObjArray(randomCount()).toList()
            obj.aDouble = UURandom.doubleObjArray(randomCount()).toList()
            obj.aUByte = UURandom.uByteObjArray(randomCount()).toList()
            obj.aUShort = UURandom.uShortObjArray(randomCount()).toList()
            obj.aUInt = UURandom.uIntObjArray(randomCount()).toList()
            obj.aULong = UURandom.uLongObjArray(randomCount()).toList()
            return obj
        }

        private fun randomCount(): Int
        {
            return UURandom.int(1, 100)
        }

        fun min(): PrimitiveListModel
        {
            return default()
        }

        fun max(): PrimitiveListModel
        {
            return random()
        }
    }

    override fun equals(other: Any?): Boolean
    {
        val o = (other as? PrimitiveListModel) ?: return false

        return(
                Objects.equals(aBool, o.aBool) &&
                Objects.equals(aBool, o.aBool) &&
                Objects.equals(aByte, o.aByte) &&
                Objects.equals(aShort, o.aShort) &&
                Objects.equals(anInt, o.anInt) &&
                Objects.equals(aLong, o.aLong) &&
                Objects.equals(aFloat, o.aFloat) &&
                Objects.equals(aDouble, o.aDouble) &&
                Objects.equals(aUByte, o.aUByte) &&
                Objects.equals(aUShort, o.aUShort) &&
                Objects.equals(aUInt, o.aUInt) &&
                Objects.equals(aULong, o.aULong)
        )
    }

    override fun hashCode(): Int {
        var result = aBool.hashCode()
        result = 31 * result + aByte.hashCode()
        result = 31 * result + aShort.hashCode()
        result = 31 * result + anInt.hashCode()
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
