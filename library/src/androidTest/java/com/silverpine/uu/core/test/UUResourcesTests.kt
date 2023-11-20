package com.silverpine.uu.core.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.silverpine.uu.core.UUResources
import com.silverpine.uu.core.uuToHex
import com.silverpine.uu.core.uuUtf8ByteArray
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UUResourcesTests
{
    @Test(expected = RuntimeException::class)
    fun test_0000_notInitialized_getIdentifier()
    {
        UUResources.getIdentifier("foo", "drawable")
    }

    @Test(expected = RuntimeException::class)
    fun test_0001_notInitialized_getResourceName()
    {
        UUResources.getResourceName(1)
    }

    @Test(expected = RuntimeException::class)
    fun test_0002_notInitialized_getResourceEntryName()
    {
        UUResources.getResourceEntryName(1)
    }

    @Test(expected = RuntimeException::class)
    fun test_0003_notInitialized_getResourcePackageName()
    {
        UUResources.getResourcePackageName(1)
    }

    @Test(expected = RuntimeException::class)
    fun test_0004_notInitialized_getResourceTypeName()
    {
        UUResources.getResourceTypeName(1)
    }

    @Test(expected = RuntimeException::class)
    fun test_0005_notInitialized_getString()
    {
        UUResources.getString(1)
    }

    @Test(expected = RuntimeException::class)
    fun test_0006_notInitialized_getStringWithArgs()
    {
        UUResources.getString(1, "foo", "bar")
    }

    @Test(expected = RuntimeException::class)
    fun test_0007_notInitialized_getDrawable()
    {
        UUResources.getDrawable(1)
    }

    @Test(expected = RuntimeException::class)
    fun test_0008_notInitialized_getColor()
    {
        UUResources.getColor(1)
    }

    @Test(expected = RuntimeException::class)
    fun test_0009_notInitialized_getDimension()
    {
        UUResources.getDimension(1)
    }

    @Test(expected = RuntimeException::class)
    fun test_0010_notInitialized_getDimensionPixelSize()
    {
        UUResources.getDimensionPixelSize(1)
    }

    @Test(expected = RuntimeException::class)
    fun test_0011_notInitialized_getAppVersion()
    {
        UUResources.getAppVersion()
    }

    @Test(expected = RuntimeException::class)
    fun test_0012_notInitialized_getAppBundleId()
    {
        UUResources.getAppBundleId()
    }

    @Test(expected = RuntimeException::class)
    fun test_0013_notInitialized_getRawText()
    {
        UUResources.getRawText(1)
    }

    @Test(expected = RuntimeException::class)
    fun test_0014_notInitialized_getRawBytes()
    {
        UUResources.getRawBytes(1)
    }

    @Test(expected = RuntimeException::class)
    fun test_0015_notInitialized_getFont()
    {
        UUResources.getFont(1)
    }

    @Test
    fun test_1000_invalidResourceId_getIdentifier()
    {
        initIfNeeded()
        val result = UUResources.getIdentifier("foo", "drawable")
        Assert.assertEquals(0, result)
    }

    @Test
    fun test_1001_invalidResourceId_getResourceName()
    {
        initIfNeeded()
        Assert.assertEquals("", UUResources.getResourceName(0))
        Assert.assertEquals("", UUResources.getResourceName(-1))
    }

    @Test
    fun test_1002_invalidResourceId_getResourceEntryName()
    {
        initIfNeeded()
        Assert.assertEquals("", UUResources.getResourceEntryName(0))
        Assert.assertEquals("", UUResources.getResourceEntryName(-1))
    }

    @Test
    fun test_1003_invalidResourceId_getResourcePackageName()
    {
        initIfNeeded()
        Assert.assertEquals("", UUResources.getResourcePackageName(0))
        Assert.assertEquals("", UUResources.getResourcePackageName(-1))
    }

    @Test
    fun test_1004_invalidResourceId_getResourceTypeName()
    {
        initIfNeeded()
        Assert.assertEquals("", UUResources.getResourceTypeName(0))
        Assert.assertEquals("", UUResources.getResourceTypeName(-1))
    }

    @Test
    fun test_1005_invalidResourceId_getString()
    {
        initIfNeeded()
        val zero: Int? = 0
        Assert.assertEquals("", UUResources.getString(zero))

        val negOne: Int? = -1
        Assert.assertEquals("", UUResources.getString(negOne))
    }

    @Test
    fun test_1006_invalidResourceId_getStringWithArgs()
    {
        initIfNeeded()
        Assert.assertEquals("", UUResources.getString(0, "foo", "bar"))
        Assert.assertEquals("", UUResources.getString(-1, "foo", "bar"))
    }

    @Test
    fun test_1007_invalidResourceId_getDrawable()
    {
        initIfNeeded()
        Assert.assertNull(UUResources.getDrawable(0))
        Assert.assertNull(UUResources.getDrawable(-1))
    }

    @Test
    fun test_1008_invalidResourceId_getColor()
    {
        initIfNeeded()
        Assert.assertEquals(0, UUResources.getColor(0))
        Assert.assertEquals(0, UUResources.getColor(-1))
    }

    @Test
    fun test_1009_invalidResourceId_getDimension()
    {
        initIfNeeded()
        Assert.assertEquals(0.0f, UUResources.getDimension(0))
        Assert.assertEquals(0.0f, UUResources.getDimension(-1))
    }

    @Test
    fun test_1010_invalidResourceId_getDimensionPixelSize()
    {
        initIfNeeded()
        Assert.assertEquals(0, UUResources.getDimensionPixelSize(0))
        Assert.assertEquals(0, UUResources.getDimensionPixelSize(-1))
    }

    @Test
    fun test_1011_invalidResourceId_getRawText()
    {
        initIfNeeded()
        Assert.assertNull(UUResources.getRawText(0))
        Assert.assertNull(UUResources.getRawText(-1))
    }

    @Test
    fun test_1012_invalidResourceId_getRawBytes()
    {
        initIfNeeded()
        Assert.assertNull(UUResources.getRawBytes(0))
        Assert.assertNull(UUResources.getRawBytes(-1))
    }

    @Test
    fun test_1013_invalidResourceId_getFont()
    {
        initIfNeeded()
        Assert.assertNull(UUResources.getFont(0))
        Assert.assertNull(UUResources.getFont(-1))
    }

    @Test
    fun test_2000_getIdentifier()
    {
        initIfNeeded()
        val result = UUResources.getIdentifier("yellow", "color")
        Assert.assertEquals(R.color.yellow, result)
    }

    @Test
    fun test_2001_getResourceName()
    {
        initIfNeeded()
        val result = UUResources.getResourceName(R.color.dark_gray)
        Assert.assertEquals("com.silverpine.uu.core.test:color/dark_gray", result)
    }

    @Test
    fun test_2002_getResourceEntryName()
    {
        initIfNeeded()
        val result = UUResources.getResourceEntryName(R.color.dark_gray)
        Assert.assertEquals("dark_gray", result)
    }

    @Test
    fun test_2003_getResourcePackageName()
    {
        initIfNeeded()
        val result = UUResources.getResourcePackageName(R.color.dark_gray)
        Assert.assertEquals("com.silverpine.uu.core.test", result)
    }

    @Test
    fun test_2004_getResourceTypeName()
    {
        initIfNeeded()
        val result = UUResources.getResourceTypeName(R.color.dark_gray)
        Assert.assertEquals("color", result)
    }

    @Test
    fun test_2005_getString()
    {
        initIfNeeded()
        val result = UUResources.getString(R.string.text_one)
        Assert.assertEquals("Text One", result)
    }

    @Test
    fun test_2006_getStringWithArgs()
    {
        initIfNeeded()
        val result = UUResources.getString(R.string.text_two, "hello")
        Assert.assertEquals("Text Two: hello", result)
    }

    @Test
    fun test_2007_getDrawable()
    {
        initIfNeeded()
        val result = UUResources.getDrawable(R.drawable.drawable_one)
        Assert.assertNotNull(result)
    }

    @Test
    fun test_2008_getColor()
    {
        initIfNeeded()
        val result = UUResources.getColor(R.color.light_green)
        Assert.assertTrue(result != 0)
    }

    @Test
    fun test_2009_getDimension()
    {
        initIfNeeded()
        val result = UUResources.getDimension(R.dimen.dimen_one)
        Assert.assertTrue(result != 0.0f)
    }

    @Test
    fun test_2010_getDimensionPixelSize()
    {
        initIfNeeded()
        val result = UUResources.getDimensionPixelSize(R.dimen.dimen_one)
        Assert.assertTrue(result != 0)
    }

    @Test
    fun test_2011_getRawText()
    {
        initIfNeeded()
        val result = UUResources.getRawText(R.raw.raw_text)
        Assert.assertNotNull(result)
        Assert.assertEquals("Hello World!", result)
    }

    @Test
    fun test_2012_getRawBytes()
    {
        initIfNeeded()
        val result = UUResources.getRawBytes(R.raw.raw_text)
        Assert.assertNotNull(result)

        val expected = "Hello World!".uuUtf8ByteArray()?.uuToHex()
        Assert.assertNotNull(expected)

        val resultHex = result?.uuToHex()
        Assert.assertNotNull(resultHex)
        Assert.assertEquals(expected, resultHex)
    }

    @Test
    fun test_2013_getFont()
    {
        initIfNeeded()
        val result = UUResources.getFont(R.font.open_dyslexic_mono_regular)
        Assert.assertNotNull(result)
    }












    @Test
    fun test_1000_getString()
    {
        initIfNeeded()
        val result = UUResources.getString(R.string.text_one)
        Assert.assertNotNull(result)
        Assert.assertEquals("Text One", result)
    }


    private var isInitialized = false

    private fun initIfNeeded()
    {
        if (!isInitialized)
        {
            UUResources.init(InstrumentationRegistry.getInstrumentation().context)
            isInitialized = true
        }
    }
}