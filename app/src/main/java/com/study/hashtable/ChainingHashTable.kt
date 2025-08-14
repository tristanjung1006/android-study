package com.study.hashtable

import kotlin.math.absoluteValue

class ChainingHashTable<K, V>(
    private var capacity: Int = 16,
    private val loadFactorThreshold: Float = 0.75f
) : HashTable<K, V> {
    
    private var buckets = Array<MutableList<Entry<K, V>>?>(capacity) { null }
    private var size = 0
    
    data class Entry<K, V>(val key: K, var value: V)
    
    override fun put(key: K, value: V) {
        if (size >= capacity * loadFactorThreshold) {
            resize()
        }
        
        val index = getIndex(key)
        
        if (buckets[index] == null) {
            buckets[index] = mutableListOf()
        }
        
        val bucket = buckets[index]!!
        
        for (entry in bucket) {
            if (entry.key == key) {
                entry.value = value
                return
            }
        }
        
        bucket.add(Entry(key, value))
        size++
    }
    
    override fun get(key: K): V? {
        val index = getIndex(key)
        val bucket = buckets[index] ?: return null
        
        for (entry in bucket) {
            if (entry.key == key) {
                return entry.value
            }
        }
        return null
    }
    
    override fun remove(key: K): V? {
        val index = getIndex(key)
        val bucket = buckets[index] ?: return null
        
        val iterator = bucket.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.key == key) {
                iterator.remove()
                size--
                return entry.value
            }
        }
        return null
    }
    
    override fun contains(key: K): Boolean {
        return get(key) != null
    }
    
    override fun size(): Int = size
    
    override fun isEmpty(): Boolean = size == 0
    
    override fun clear() {
        buckets = Array(capacity) { null }
        size = 0
    }
    
    override fun keys(): Set<K> {
        val keys = mutableSetOf<K>()
        for (bucket in buckets) {
            bucket?.forEach { entry ->
                keys.add(entry.key)
            }
        }
        return keys
    }
    
    override fun values(): Collection<V> {
        val values = mutableListOf<V>()
        for (bucket in buckets) {
            bucket?.forEach { entry ->
                values.add(entry.value)
            }
        }
        return values
    }
    
    override fun entries(): Set<Map.Entry<K, V>> {
        val entries = mutableSetOf<Map.Entry<K, V>>()
        for (bucket in buckets) {
            bucket?.forEach { entry ->
                entries.add(object : Map.Entry<K, V> {
                    override val key: K = entry.key
                    override val value: V = entry.value
                })
            }
        }
        return entries
    }
    
    private fun getIndex(key: K): Int {
        return key.hashCode().absoluteValue % capacity
    }
    
    private fun resize() {
        val oldCapacity = capacity
        val oldBuckets = buckets
        
        capacity *= 2
        size = 0
        buckets = Array(capacity) { null }
        
        for (bucket in oldBuckets) {
            bucket?.forEach { entry ->
                put(entry.key, entry.value)
            }
        }
        
        println("ChainingHashTable resized: $oldCapacity -> $capacity")
    }
    
    fun getCollisionCount(): Int {
        var collisions = 0
        for (bucket in buckets) {
            if (bucket != null && bucket.size > 1) {
                collisions += bucket.size - 1
            }
        }
        return collisions
    }
    
    fun getLoadFactor(): Float = size.toFloat() / capacity
    
    fun getBucketSizes(): List<Int> {
        return buckets.mapNotNull { it?.size }
    }
}