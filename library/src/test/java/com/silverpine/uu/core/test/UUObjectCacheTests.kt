package com.silverpine.uu.core.test

import com.silverpine.uu.core.UUInMemoryObjectCache
import com.silverpine.uu.core.UUObjectCache
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class UUObjectCacheTests
{
    private lateinit var cache: UUInMemoryObjectCache<String>

    @BeforeEach
    fun setUp()
    {
        cache = UUInMemoryObjectCache()
    }

    @Test
    fun testGet_returnsNull_whenEmpty()
    {
        val result = cache["nonexistent"]
        
        assertNull(result, "Should return null for non-existent key")
    }

    @Test
    fun testSetAndGet_basicOperation()
    {
        cache["key1"] = "value1"
        
        val result = cache["key1"]
        
        assertEquals("value1", result, "Should retrieve stored value")
    }

    @Test
    fun testSet_overwritesExistingValue()
    {
        cache["key1"] = "value1"
        cache["key1"] = "value2"
        
        val result = cache["key1"]
        
        assertEquals("value2", result, "Should overwrite existing value")
    }

    @Test
    fun testSet_null_removesEntry()
    {
        cache["key1"] = "value1"
        cache["key1"] = null
        
        val result = cache["key1"]
        
        assertNull(result, "Should return null after setting value to null")
    }

    @Test
    fun testSet_null_whenKeyDoesNotExist()
    {
        cache["nonexistent"] = null
        
        val result = cache["nonexistent"]
        
        assertNull(result, "Should handle null assignment for non-existent key gracefully")
    }

    @Test
    fun testMultipleEntries()
    {
        cache["key1"] = "value1"
        cache["key2"] = "value2"
        cache["key3"] = "value3"
        
        assertEquals("value1", cache["key1"], "Should retrieve first value")
        assertEquals("value2", cache["key2"], "Should retrieve second value")
        assertEquals("value3", cache["key3"], "Should retrieve third value")
    }

    @Test
    fun testRemoveAndReAdd()
    {
        cache["key1"] = "value1"
        cache["key1"] = null
        cache["key1"] = "value2"
        
        val result = cache["key1"]
        
        assertEquals("value2", result, "Should allow re-adding after removal")
    }

    @Test
    fun testDifferentKeyTypes()
    {
        val stringCache: UUInMemoryObjectCache<String> = UUInMemoryObjectCache()
        val intCache: UUInMemoryObjectCache<Int> = UUInMemoryObjectCache()
        
        stringCache["key"] = "string"
        intCache["key"] = 42
        
        assertEquals("string", stringCache["key"], "String cache should work")
        assertEquals(42, intCache["key"], "Int cache should work")
    }

    @Test
    fun testConcurrentAccess_threadSafety()
    {
        val numberOfThreads = 10
        val numberOfOperations = 100
        val executor = Executors.newFixedThreadPool(numberOfThreads)
        val latch = CountDownLatch(numberOfThreads)
        
        try
        {
            // Write operations from multiple threads
            for (i in 0 until numberOfThreads)
            {
                val threadId = i
                executor.submit {
                    try
                    {
                        for (j in 0 until numberOfOperations)
                        {
                            val key = "key-$threadId-$j"
                            cache[key] = "value-$threadId-$j"
                            
                            // Verify we can read it back
                            val value = cache[key]
                            assertNotNull(value, "Value should not be null after write")
                        }
                    }
                    finally
                    {
                        latch.countDown()
                    }
                }
            }
            
            // Wait for all threads to complete
            assertTrue(latch.await(10, TimeUnit.SECONDS), "All threads should complete within timeout")
            
            // Verify all entries exist
            for (i in 0 until numberOfThreads)
            {
                for (j in 0 until numberOfOperations)
                {
                    val key = "key-$i-$j"
                    val expectedValue = "value-$i-$j"
                    assertEquals(expectedValue, cache[key], "All concurrent writes should be preserved")
                }
            }
        }
        catch (e: InterruptedException)
        {
            Thread.currentThread().interrupt()
            fail("Test was interrupted: ${e.message}")
        }
        finally
        {
            executor.shutdown()
            try
            {
                executor.awaitTermination(5, TimeUnit.SECONDS)
            }
            catch (e: InterruptedException)
            {
                Thread.currentThread().interrupt()
            }
        }
    }

    @Test
    fun testConcurrentReadWrite_threadSafety()
    {
        val numberOfThreads = 10
        val numberOfOperations = 50
        val executor = Executors.newFixedThreadPool(numberOfThreads)
        val latch = CountDownLatch(numberOfThreads)
        
        // Pre-populate cache
        for (i in 0 until numberOfThreads)
        {
            cache["pre-key-$i"] = "pre-value-$i"
        }
        
        try
        {
            // Concurrent read and write operations
            for (i in 0 until numberOfThreads)
            {
                val threadId = i
                executor.submit {
                    try
                    {
                        for (j in 0 until numberOfOperations)
                        {
                            // Read existing value
                            val existing = cache["pre-key-$threadId"]
                            assertNotNull(existing, "Pre-existing value should be readable")
                            
                            // Write new value
                            cache["new-key-$threadId-$j"] = "new-value-$threadId-$j"
                            
                            // Read back new value
                            val newValue = cache["new-key-$threadId-$j"]
                            assertNotNull(newValue, "Newly written value should be readable")
                        }
                    }
                    finally
                    {
                        latch.countDown()
                    }
                }
            }
            
            // Wait for all threads to complete
            assertTrue(latch.await(10, TimeUnit.SECONDS), "All threads should complete within timeout")
        }
        catch (e: InterruptedException)
        {
            Thread.currentThread().interrupt()
            fail("Test was interrupted: ${e.message}")
        }
        finally
        {
            executor.shutdown()
            try
            {
                executor.awaitTermination(5, TimeUnit.SECONDS)
            }
            catch (e: InterruptedException)
            {
                Thread.currentThread().interrupt()
            }
        }
    }

    @Test
    fun testEmptyStringKey()
    {
        cache[""] = "emptyKeyValue"
        
        val result = cache[""]
        
        assertEquals("emptyKeyValue", result, "Should handle empty string key")
    }

    @Test
    fun testSpecialCharactersInKey()
    {
        val specialKey = "key-with-special-chars-!@#\$%^&*()_+-=[]{}|;':\",./<>?"
        cache[specialKey] = "specialValue"
        
        val result = cache[specialKey]
        
        assertEquals("specialValue", result, "Should handle special characters in key")
    }

    @Test
    fun testNullValue_handlesGracefully()
    {
        cache["key1"] = "value1"
        
        // Setting to null should remove the entry
        cache["key1"] = null
        val result = cache["key1"]
        
        assertNull(result, "Setting to null should remove entry")
    }

    @Test
    fun testInterface_implementation()
    {
        val cache: UUObjectCache<String> = UUInMemoryObjectCache()
        
        cache["interface-key"] = "interface-value"
        val result = cache["interface-key"]
        
        assertEquals("interface-value", result, "Should work through interface")
    }

    @Test
    fun testMultipleRemovals()
    {
        cache["key1"] = "value1"
        cache["key2"] = "value2"
        cache["key3"] = "value3"
        
        cache["key1"] = null
        cache["key2"] = null
        cache["key3"] = null
        
        assertNull(cache["key1"], "First key should be removed")
        assertNull(cache["key2"], "Second key should be removed")
        assertNull(cache["key3"], "Third key should be removed")
    }

    @Test
    fun testLargeKeySet()
    {
        val numberOfKeys = 1000
        
        for (i in 0 until numberOfKeys)
        {
            cache["key-$i"] = "value-$i"
        }
        
        // Verify all entries
        for (i in 0 until numberOfKeys)
        {
            val expected = "value-$i"
            val actual = cache["key-$i"]
            assertEquals(expected, actual, "Should retrieve value for key-$i")
        }
    }
}