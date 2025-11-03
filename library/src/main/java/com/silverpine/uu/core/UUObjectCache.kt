package com.silverpine.uu.core

import java.util.concurrent.ConcurrentHashMap

/**
 * Interface for caching objects by string identifiers.
 * 
 * Provides a simple key-value cache abstraction where objects can be stored and retrieved
 * using string-based identifiers. Setting a value to `null` will remove it from the cache.
 * 
 * @param T The type of objects stored in the cache
 * 
 * @sample
 * ```kotlin
 * val cache: UUObjectCache<String> = UUInMemoryObjectCache()
 * cache["key1"] = "value1"
 * val value = cache["key1"] // Returns "value1"
 * cache["key1"] = null // Removes "key1" from cache
 * val removed = cache["key1"] // Returns null
 * ```
 */
interface UUObjectCache<T>
{
    /**
     * Retrieves an object from the cache by its identifier.
     * 
     * @param identifier The string key used to identify the cached object
     * @return The cached object if found, `null` otherwise
     */
    operator fun get(identifier: String): T?
    
    /**
     * Stores an object in the cache with the given identifier.
     * 
     * If `obj` is `null`, the entry for `identifier` will be removed from the cache.
     * 
     * @param identifier The string key to associate with the object
     * @param obj The object to cache, or `null` to remove the entry
     */
    operator fun set(identifier: String, obj: T?)
}

/**
 * Thread-safe in-memory implementation of [UUObjectCache] using [ConcurrentHashMap].
 * 
 * This implementation provides a simple, thread-safe cache that stores objects in memory.
 * Objects are stored using their string identifiers as keys, and can be retrieved or removed
 * via the index operator syntax (`cache["key"]`).
 * 
 * **Note**: Kotlin's standard library does not provide a built-in caching solution. This
 * implementation uses Java's `ConcurrentHashMap` which provides thread-safety but does not
 * include features like:
 * - Size limits or eviction policies (LRU, TTL, etc.)
 * - Persistence across app restarts
 * - Memory size management
 * 
 * If you need more advanced caching features (size limits, TTL, eviction policies), consider
 * using a third-party library like Caffeine, Guava Cache, or Kotlin Multiplatform alternatives.
 * 
 * @param T The type of objects stored in the cache
 * 
 * @sample
 * ```kotlin
 * val cache = UUInMemoryObjectCache<String>()
 * cache["user:123"] = "John Doe"
 * cache["user:456"] = "Jane Smith"
 * 
 * val user = cache["user:123"] // Returns "John Doe"
 * cache["user:123"] = null // Removes "user:123"
 * ```
 */
open class UUInMemoryObjectCache<T>: UUObjectCache<T>
{
    private val cache = ConcurrentHashMap<String, T>()

    /**
     * Retrieves an object from the cache by its identifier.
     * 
     * @param identifier The string key used to identify the cached object
     * @return The cached object if found, `null` otherwise
     */
    override operator fun get(identifier: String): T?
    {
        return cache[identifier]
    }

    /**
     * Stores an object in the cache with the given identifier.
     * 
     * If `obj` is `null`, the entry for `identifier` will be removed from the cache.
     * This allows for convenient cache entry removal using `cache["key"] = null`.
     * 
     * @param identifier The string key to associate with the object
     * @param obj The object to cache, or `null` to remove the entry
     */
    override operator fun set(identifier: String, obj: T?)
    {
        if (obj != null)
        {
            cache[identifier] = obj
        }
        else
        {
            cache.remove(identifier)
        }
    }
}