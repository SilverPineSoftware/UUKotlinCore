package com.silverpine.uu.core.test

import android.database.MatrixCursor
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.silverpine.uu.core.uuGetBlob
import com.silverpine.uu.core.uuGetBoolean
import com.silverpine.uu.core.uuGetDouble
import com.silverpine.uu.core.uuGetFloat
import com.silverpine.uu.core.uuGetInt
import com.silverpine.uu.core.uuGetLong
import com.silverpine.uu.core.uuGetShort
import com.silverpine.uu.core.uuGetString
import com.silverpine.uu.core.uuGetStringList
import com.silverpine.uu.core.uuGetStringSet
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UUCursorTests
{
    @Test
    fun test_columnDoesNotExist()
    {
        val c = MatrixCursor(arrayOf("a", "b", "c"))

        Assert.assertNull(c.uuGetShort("foo"))
        Assert.assertNull(c.uuGetInt("foo"))
        Assert.assertNull(c.uuGetLong("foo"))
        Assert.assertNull(c.uuGetBoolean("foo"))
        Assert.assertNull(c.uuGetFloat("foo"))
        Assert.assertNull(c.uuGetDouble("foo"))
        Assert.assertNull(c.uuGetBlob("foo"))
        Assert.assertNull(c.uuGetString("foo"))
        Assert.assertNull(c.uuGetStringList("foo"))
        Assert.assertNull(c.uuGetStringSet("foo"))

    }
}