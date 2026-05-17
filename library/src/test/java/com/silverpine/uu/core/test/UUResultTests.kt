package com.silverpine.uu.core.test

import com.silverpine.uu.core.UUError
import com.silverpine.uu.core.UUResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Unit tests for [UUResult].
 *
 * @since 1.0.0
 */
class UUResultTest
{
    @Test
    fun success_getOrNull_returnsValue()
    {
        val result = UUResult.success(42)
        assertEquals(42, result.getOrNull())
        assertNull(result.errorOrNull())
    }

    @Test
    fun failure_errorOrNull_returnsError()
    {
        val error = UUError(code = 100, domain = "Boom")
        val result: UUResult<Int, UUError> = UUResult.failure(error)
        assertNull(result.getOrNull())
        assertEquals(error, result.errorOrNull())
    }

    @Test
    fun onSuccess_isCalledForSuccess()
    {
        val result = UUResult.success("hello")
        var called = false

        result.onSuccess {
            called = true
            assertEquals("hello", it)
        }

        assertTrue(called, "onSuccess block should be called")
    }

    @Test
    fun onFailure_isCalledForFailure()
    {
        val error = UUError(code = 500, domain = "Oops")
        val result: UUResult<String, UUError> = UUResult.failure(error)
        var called = false

        result.onFailure {
            called = true
            assertEquals(error, it)
        }

        assertTrue(called, "onFailure block should be called")
    }

    @Test
    fun map_transformsSuccessValue()
    {
        val result = UUResult.success(5).map { it * 2 }
        assertTrue(result is UUResult.Success)
        assertEquals(10, (result as UUResult.Success).value)
    }

    @Test
    fun map_propagatesFailure()
    {
        val error = UUError(code = 123, domain = "fail")
        val result: UUResult<Int, UUError> = UUResult.failure<Int, UUError>(error).map { it * 2 }
        assertTrue(result is UUResult.Failure)
        assertEquals(error, (result as UUResult.Failure<UUError>).error)
    }

    @Test
    fun flatMap_chainsSuccesses()
    {
        val r1 = UUResult.success(2)
        val r2 = r1.flatMap { UUResult.success(it * 10) }

        assertTrue(r2 is UUResult.Success)
        assertEquals(20, (r2 as UUResult.Success).value)
    }

    @Test
    fun flatMap_propagatesFailure()
    {
        val error = UUError(code = 999, domain = "broken")
        val r1: UUResult<Int, UUError> = UUResult.failure(error)
        val r2 = r1.flatMap { UUResult.success(it * 10) }

        assertTrue(r2 is UUResult.Failure)
        assertEquals(error, (r2 as UUResult.Failure<UUError>).error)
    }

    @Test
    fun recover_replacesFailureWithValue()
    {
        val error = UUError(code = 888, domain = "nope")
        val r: UUResult<Int, UUError> = UUResult.failure(error)

        val recovered = r.recover { 99 }
        assertTrue(recovered is UUResult.Success)
        assertEquals(99, (recovered as UUResult.Success).value)
    }

    @Test
    fun recover_keepsSuccessValue()
    {
        val r = UUResult.success(5)
        val recovered = r.recover { 99 }

        assertTrue(recovered is UUResult.Success)
        assertEquals(5, (recovered as UUResult.Success).value)
    }

    @Test
    fun fold_returnsSuccessBranch()
    {
        val r = UUResult.success("X")
        val folded = r.fold(
            onSuccess = { "success:$it" },
            onFailure = { "failure" },
        )

        assertEquals("success:X", folded)
    }

    @Test
    fun fold_returnsFailureBranch()
    {
        val error = UUError(code = 777, domain = "err")
        val r: UUResult<String, UUError> = UUResult.failure(error)
        val folded = r.fold(
            onSuccess = { "success:$it" },
            onFailure = { "failure:${it.domain}-${it.code}" },
        )

        assertEquals("failure:err-777", folded)
    }

    @Test
    fun getOrElse_returnsSuccessValue()
    {
        val result = UUResult.success("ok")
        assertEquals("ok", result.getOrElse { "fallback" })
    }

    @Test
    fun getOrElse_returnsFallbackOnFailure()
    {
        val error = UUError(code = 1, domain = "d")
        val result: UUResult<String, UUError> = UUResult.failure(error)
        assertEquals("fallback", result.getOrElse { "fallback" })
    }

    @Test
    fun stringErrorType_isPreservedOnFailure()
    {
        val result: UUResult<Int, String> = UUResult.failure("network-down")
        assertNull(result.getOrNull())
        assertEquals("network-down", result.errorOrNull())
    }

    @Test
    fun stringErrorType_onFailure_receivesMessage()
    {
        val result: UUResult<Unit, String> = UUResult.failure("timeout")
        var message: String? = null

        result.onFailure { message = it }

        assertEquals("timeout", message)
    }

    @Test
    fun stringErrorType_map_propagatesStringError()
    {
        val result: UUResult<Int, String> =
            UUResult.failure<Int, String>("bad-gateway").map { it * 2 }

        assertTrue(result is UUResult.Failure)
        assertEquals("bad-gateway", (result as UUResult.Failure<String>).error)
    }

    @Test
    fun stringErrorType_recover_usesErrorMessage()
    {
        val result: UUResult<Int, String> = UUResult.failure("offline")
        val recovered = result.recover { it.length }

        assertTrue(recovered is UUResult.Success)
        assertEquals(7, (recovered as UUResult.Success).value)
    }

    @Test
    fun enumErrorType_fold_formatsCode()
    {
        val result: UUResult<Long, TestError> = UUResult.failure(TestError.NotFound)

        val folded = result.fold(
            onSuccess = { "value:$it" },
            onFailure = { "error:${it.name}" },
        )

        assertEquals("error:NotFound", folded)
    }

    @Test
    fun enumErrorType_flatMap_chainsWithSameErrorType()
    {
        val r1: UUResult<Int, TestError> = UUResult.success(3)
        val r2 = r1.flatMap { value ->
            if (value > 0) UUResult.success(value * 2)
            else UUResult.failure(TestError.InvalidInput)
        }

        assertTrue(r2 is UUResult.Success)
        assertEquals(6, (r2 as UUResult.Success).value)
    }

    @Test
    fun enumErrorType_flatMap_propagatesEnumError()
    {
        val r1: UUResult<Int, TestError> = UUResult.failure(TestError.Unauthorized)
        val r2 = r1.flatMap { UUResult.success(it * 2) }

        assertTrue(r2 is UUResult.Failure)
        assertEquals(TestError.Unauthorized, (r2 as UUResult.Failure<TestError>).error)
    }

    @Test
    fun success_isAssignableToResultWithSpecificErrorType()
    {
        val result: UUResult<Int, UUError> = UUResult.success(1)
        assertEquals(1, result.getOrNull())
    }

    private enum class TestError
    {
        NotFound,
        Unauthorized,
        InvalidInput,
    }
}
