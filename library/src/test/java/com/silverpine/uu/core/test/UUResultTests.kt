package com.silverpine.uu.core.test

import com.silverpine.uu.core.UUError
import com.silverpine.uu.core.UUResult
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for [UUResult].
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
        val result: UUResult<Int> = UUResult.failure(error)
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

        assertTrue("onSuccess block should be called", called)
    }

    @Test
    fun onFailure_isCalledForFailure()
    {
        val error = UUError(code = 500, domain = "Oops")
        val result: UUResult<String> = UUResult.failure(error)
        var called = false

        result.onFailure {
            called = true
            assertEquals(error, it)
        }

        assertTrue("onFailure block should be called", called)
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
        val result: UUResult<Int> = UUResult.failure<String>(error).map { it.toInt() }
        assertTrue(result is UUResult.Failure)
        assertEquals(error, (result as UUResult.Failure).error)
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
        val r1: UUResult<Int> = UUResult.failure(error)
        val r2 = r1.flatMap { UUResult.success(it * 10) }

        assertTrue(r2 is UUResult.Failure)
        assertEquals(error, (r2 as UUResult.Failure).error)
    }

    @Test
    fun recover_replacesFailureWithValue()
    {
        val error = UUError(code = 888, domain = "nope")
        val r: UUResult<Int> = UUResult.failure(error)

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
            onFailure = { "failure:${it.domain}" }
        )

        assertEquals("success:X", folded)
    }

    @Test
    fun fold_returnsFailureBranch()
    {
        val error = UUError(code = 777, domain = "err")
        val r: UUResult<String> = UUResult.failure(error)
        val folded = r.fold(
            onSuccess = { "success:$it" },
            onFailure = { "failure:${it.domain}-${it.code}" }
        )

        assertEquals("failure:err-777", folded)
    }
}