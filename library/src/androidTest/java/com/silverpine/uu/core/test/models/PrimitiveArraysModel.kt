package com.silverpine.uu.core.test.models

import androidx.annotation.Keep
import com.silverpine.uu.core.UURandom
import kotlinx.serialization.Serializable

@Keep
@Serializable
open class PrimitiveArraysModel
{
    var aBool: Array<Boolean> = arrayOf()
    var aByte: Array<Byte> = arrayOf()
    var aShort: Array<Short> = arrayOf()
    var anInt: Array<Int> = arrayOf()
    var aLong: Array<Long> = arrayOf()
    var aFloat: Array<Float> = arrayOf()
    var aDouble: Array<Double> = arrayOf()
    var aUByte: Array<UByte> = arrayOf()
    var aUShort: Array<UShort> = arrayOf()
    var aUInt: Array<UInt> = arrayOf()
    var aULong: Array<ULong> = arrayOf()

    companion object
    {
        fun default(): PrimitiveArraysModel
        {
            return PrimitiveArraysModel()
        }

        fun random(): PrimitiveArraysModel
        {
            val obj = PrimitiveArraysModel()
            obj.aBool = UURandom.boolObjArray(randomCount())
            obj.aByte = UURandom.byteObjArray(randomCount())
            obj.aShort = UURandom.shortObjArray(randomCount())
            obj.anInt = UURandom.intObjArray(randomCount())
            obj.aLong = UURandom.longObjArray(randomCount())
            obj.aFloat = UURandom.floatObjArray(randomCount())
            obj.aDouble = UURandom.doubleObjArray(randomCount())
            obj.aUByte = UURandom.uByteObjArray(randomCount())
            obj.aUShort = UURandom.uShortObjArray(randomCount())
            obj.aUInt = UURandom.uIntObjArray(randomCount())
            obj.aULong = UURandom.uLongObjArray(randomCount())
            return obj
        }

        private fun randomCount(): Int
        {
            return UURandom.int(1, 100)
        }

        fun min(): PrimitiveArraysModel
        {
            return default()
        }

        fun max(): PrimitiveArraysModel
        {
            return random()
        }
    }

    override fun equals(other: Any?): Boolean
    {
        val o = (other as? PrimitiveArraysModel) ?: return false

        return(
                aBool.contentEquals(o.aBool) &&
                aByte.contentEquals(aByte) &&
                aShort.contentEquals(aShort) &&
                anInt.contentEquals(anInt) &&
                aLong.contentEquals(aLong) &&
                aFloat.contentEquals(aFloat) &&
                aDouble.contentEquals(aDouble) &&
                aUByte.contentEquals(aUByte) &&
                aUShort.contentEquals(aUShort) &&
                aUInt.contentEquals(aUInt) &&
                aULong.contentEquals(aULong)
        )
    }
}