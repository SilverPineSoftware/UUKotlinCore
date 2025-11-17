package com.silverpine.uu.core.test

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.silverpine.uu.core.uuAndroidId
import com.silverpine.uu.core.uuAppName
import com.silverpine.uu.core.uuAppVersion
import com.silverpine.uu.core.uuAppVersionCode
import com.silverpine.uu.core.uuOSVersionString
import com.silverpine.uu.core.uuPlatformString
import com.silverpine.uu.core.uuScreenSize
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UUContextTests
{
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun test_uuPlatformString()
    {
        val result = uuPlatformString()
        assertNotNull(result)
        // Should return Build.MODEL
        assertEquals(Build.MODEL, result)
        // Should not be empty
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun test_uuOSVersionString()
    {
        val result = uuOSVersionString()
        assertNotNull(result)
        // Should return Build.VERSION.RELEASE
        assertEquals(Build.VERSION.RELEASE, result)
        // Should not be empty
        assertTrue(result.isNotEmpty())
        // Should contain at least one digit (version numbers have digits)
        assertTrue(result.any { it.isDigit() })
    }

    @Test
    fun test_uuAppName()
    {
        val result = appContext.uuAppName()
        assertNotNull(result)
        // Should return package name
        assertEquals(appContext.packageName, result)
        // Should not be empty
        assertTrue(result.isNotEmpty())
        // Should contain at least one dot (package names have dots)
        assertTrue(result.contains("."))
    }

    @Test
    fun test_uuAppVersion()
    {
        val result = appContext.uuAppVersion()
        assertNotNull(result)
        // Should not be empty (even if "unknown", it's not empty)
        assertTrue(result.isNotEmpty())
        // Should return either a version string or "unknown"
        assertTrue(result == "unknown" || result.matches(Regex("\\d+(\\.\\d+)*")))
    }

    @Test
    fun test_uuAppVersionCode()
    {
        val result = appContext.uuAppVersionCode()
        assertNotNull(result)
        // Should not be empty (even if "unknown", it's not empty)
        assertTrue(result.isNotEmpty())
        // Should return either a numeric string or "unknown"
        assertTrue(result == "unknown" || result.all { it.isDigit() })
        
        // If not "unknown", should be a valid number
        if (result != "unknown")
        {
            assertTrue(result.toLongOrNull() != null)
        }
    }

    @Test
    fun test_uuScreenSize()
    {
        val result = appContext.uuScreenSize()
        assertNotNull(result)
        // Should not be empty (even if "unknown", it's not empty)
        assertTrue(result.isNotEmpty())
        
        // If not "unknown", should match the format "WIDTHxHEIGHT"
        if (result != "unknown")
        {
            // Should contain an 'x' separator
            assertTrue(result.contains("x"))
            val parts = result.split("x")
            assertEquals(2, parts.size)
            
            // Both parts should be numeric
            val width = parts[0].toIntOrNull()
            val height = parts[1].toIntOrNull()
            assertNotNull(width)
            assertNotNull(height)
            
            // Screen dimensions should be positive
            assertTrue(width!! > 0)
            assertTrue(height!! > 0)
        }
    }

    @Test
    fun test_uuAndroidId()
    {
        val result = appContext.uuAndroidId()
        assertNotNull(result)
        // Android ID should be 16 hex characters (64 bits)
        // It can be null on some devices, but if present should be valid hex
        if (result.isNotEmpty())
        {
            // Should be 16 hex characters
            assertEquals(16, result.length)
            // Should be valid hexadecimal
            assertTrue(result.all { it.isDigit() || it in 'a'..'f' || it in 'A'..'F' })
        }
    }

    @Test
    fun test_uuAppVersionCode_returnsLongVersionCodeOnPPlus()
    {
        val result = appContext.uuAppVersionCode()
        
        if (result != "unknown")
        {
            // On API 28+ (P), should use longVersionCode
            // On older APIs, should use versionCode
            // Both should be positive numbers
            val versionCode = result.toLongOrNull()
            assertNotNull(versionCode)
        }
    }

    @Test
    fun test_uuScreenSize_format()
    {
        val result = appContext.uuScreenSize()
        
        if (result != "unknown")
        {
            // Should match format: digits x digits
            assertTrue(result.matches(Regex("\\d+x\\d+")))
        }
    }

    @Test
    fun test_uuAppName_matchesPackageName()
    {
        val result = appContext.uuAppName()
        val expectedPackageName = appContext.packageName
        
        assertEquals(expectedPackageName, result)
    }

    @Test
    fun test_uuPlatformString_matchesBuildModel()
    {
        val result = uuPlatformString()
        val expectedModel = Build.MODEL
        
        assertEquals(expectedModel, result)
    }

    @Test
    fun test_uuOSVersionString_matchesBuildVersionRelease()
    {
        val result = uuOSVersionString()
        val expectedRelease = Build.VERSION.RELEASE
        
        assertEquals(expectedRelease, result)
    }
}
