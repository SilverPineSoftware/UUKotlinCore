package com.silverpine.uu.core.test

import android.os.Bundle
import com.silverpine.uu.core.UUError
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.MockedConstruction
import org.mockito.Mockito.mockConstruction
import org.mockito.Mockito.never
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness

/**
 * JUnit4 tests for [com.silverpine.uu.core.UUError] using Mockito.
 *
 * Notes:
 * - These tests assume you include the "mockito-inline" dependency so that
 *   final classes (like android.os.Bundle) and constructor calls can be mocked.
 *
 *   testImplementation("org.mockito:mockito-core:5.x.x")
 *   testImplementation("org.mockito:mockito-inline:5.x.x")
 *
 * - If you run these in JVM unit tests (src/test), android.os stubs must be on the classpath
 *   (they are provided by the Android Gradle Plugin).
 *
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UUErrorMockitoTest
{
    //@get:Rule
    //val mockito: MockitoRule? = MockitoJUnit.rule()//.strictness(Strictness.LENIENT)

    @Mock
    lateinit var mockBundle: Bundle

    @Test
    fun defaultValues_areSet()
    {
        val err = UUError(code = 1234)

        assertEquals(1234, err.code)
        assertEquals("UUErrorDomain", err.domain)
        assertNull(err.exception)
        assertNull(err.userInfo)
        assertNull(err.underlyingError)
        assertNull(err.errorDescription)
        assertNull(err.errorResolution)

        val s = err.toString()
        assertTrue(s.contains("Domain: UUErrorDomain"))
        assertTrue(s.contains("Code: 1234"))
    }

    @Test
    fun errorDescription_setter_putsValueInBundle_withCorrectKey()
    {
        val err = UUError(code = 1, userInfo = mockBundle)

        err.errorDescription = "Something went wrong"

        verify(mockBundle).putString(eq(UUError.Keys.ERROR_DESCRIPTION), eq("Something went wrong"))
        whenever(mockBundle.getString(UUError.Keys.ERROR_DESCRIPTION)).thenReturn("Something went wrong")
        assertEquals("Something went wrong", err.errorDescription)
    }

    @Test
    fun errorResolution_setter_putsValueInBundle_withCorrectKey()
    {
        val err = UUError(code = 2, userInfo = mockBundle)

        err.errorResolution = "Try again later"

        // EXPECTATION: errorResolution writes to ERROR_RESOLUTION
        verify(mockBundle).putString(eq(UUError.Keys.ERROR_RESOLUTION), eq("Try again later"))
        whenever(mockBundle.getString(UUError.Keys.ERROR_RESOLUTION)).thenReturn("Try again later")
        assertEquals("Try again later", err.errorResolution)
        // and should not write to description key
        verify(mockBundle, never()).putString(eq(UUError.Keys.ERROR_DESCRIPTION), anyOrNull())
    }

    @Test
    fun addUserInfo_addsAndOverwritesValues()
    {
        val err = UUError(code = 3, userInfo = mockBundle)

        err.addUserInfo("customKey", "v1")
        verify(mockBundle).putString("customKey", "v1")

        err.addUserInfo("customKey", "v2")
        verify(mockBundle).putString("customKey", "v2")
    }

    @Test
    fun settingProperties_doesNotClearExistingUserInfoEntries()
    {
        val err = UUError(code = 4, userInfo = mockBundle)

        err.errorDescription = "desc"
        err.errorResolution = "res"

        // Ensure both puts occurred and no clears were attempted
        verify(mockBundle).putString(UUError.Keys.ERROR_DESCRIPTION, "desc")
        verify(mockBundle).putString(UUError.Keys.ERROR_RESOLUTION, "res")
    }

    @Test
    fun toString_includesDescriptionAndResolution_whenPresent()
    {
        val err = UUError(code = 5, userInfo = mockBundle)

        // Simulate getters returning values (since toString uses properties)
        whenever(mockBundle.getString(UUError.Keys.ERROR_DESCRIPTION)).thenReturn("Oops")
        whenever(mockBundle.getString(UUError.Keys.ERROR_RESOLUTION)).thenReturn("Retry")

        val s = err.toString()
        assertTrue(s.contains("Description: Oops"))
        assertTrue(s.contains("Resolution: Retry"))
    }

    @Test
    fun canAttachExceptionAndUnderlyingError()
    {
        val root = UUError(code = 100, domain = "Root")
        val ex = IllegalStateException("boom")

        val err = UUError(
            code = 200,
            domain = "Parent",
            exception = ex,
            underlyingError = root
        )

        assertSame(ex, err.exception)
        assertSame(root, err.underlyingError)
    }

    @Test
    fun errorDescription_lazyCreatesBundle_viaConstructorMock()
    {
        // Intercept `Bundle()` construction and provide a mock
        val construction: MockedConstruction<Bundle> = mockConstruction(Bundle::class.java)
        try
        {
            val err = UUError(code = 7, userInfo = null)

            // When setter is called, class should create a new Bundle and call putString on it
            err.errorDescription = "desc"

            // One Bundle was constructed
            assertEquals(1, construction.constructed().size)
            val created = construction.constructed()[0]

            verify(created).putString(UUError.Keys.ERROR_DESCRIPTION, "desc")

            // Also ensure property getter uses the same bundle
            whenever(created.getString(UUError.Keys.ERROR_DESCRIPTION)).thenReturn("desc")
            assertEquals("desc", err.errorDescription)

            // And that userInfo is now non-null
            assertNotNull(err.userInfo)
        }
        finally
        {
            construction.close()
        }
    }

    @Test
    fun errorResolution_lazyCreatesBundle_viaConstructorMock()
    {
        val construction: MockedConstruction<Bundle> = mockConstruction(Bundle::class.java)
        try
        {
            val err = UUError(code = 8, userInfo = null)

            err.errorResolution = "res"

            assertEquals(1, construction.constructed().size)
            val created = construction.constructed()[0]

            // EXPECTATION: resolution uses ERROR_RESOLUTION key
            verify(created).putString(UUError.Keys.ERROR_RESOLUTION, "res")

            whenever(created.getString(UUError.Keys.ERROR_RESOLUTION)).thenReturn("res")
            assertEquals("res", err.errorResolution)
            assertNotNull(err.userInfo)
        }
        finally
        {
            construction.close()
        }
    }
}