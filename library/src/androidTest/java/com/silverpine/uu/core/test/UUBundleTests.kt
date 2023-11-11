package com.silverpine.uu.core.test

import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.silverpine.uu.core.test.models.PrimitiveModel
import com.silverpine.uu.core.uuGetJsonObject
import com.silverpine.uu.core.uuPutJson
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UUBundleTests
{
    @Test
    fun testPutJsonIntoBundle()
    {
        val model = PrimitiveModel.random()
        val bundle = Bundle()
        bundle.uuPutJson("foobar", model, PrimitiveModel::class.java)

        val check = bundle.uuGetJsonObject("foobar", PrimitiveModel::class.java)
        Assert.assertEquals(model, check)
    }
}