package com.silverpine.uu.core.test

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.silverpine.uu.core.UUJson
import com.silverpine.uu.core.UURandom
import com.squareup.moshi.Moshi
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.util.*

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UUJsonTest
{
    init
    {
        UUJson.init(Moshi.Builder().build())
    }

    @Test
    fun test_0000_toJsonString()
    {
        val model = TestModel()
        model.id = UURandom.uuid()
        model.name = UURandom.charArray(10).toString()
        model.level = UURandom.uByte().toInt()
        model.xp = UURandom.uShort().toInt()

        val json = UUJson.toJson(model, TestModel::class.java)
        Log.d("TAG", "$json")

    }
}

