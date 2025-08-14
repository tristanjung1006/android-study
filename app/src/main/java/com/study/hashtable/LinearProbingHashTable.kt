package com.study.hashtable

import kotlin.math.absoluteValue

class LinearProbingHashTable<K, V>(
    private var capacity: Int = 16,
    private val loadFactorThreshold: Float = 0.75f
) : HashTable<K, V> {
    
    private var keys = arrayOfNulls<Any?>(capacity)
    private var values = arrayOfNulls<Any?>(capacity)
    private var size = 0
    
    private val DELETED = Any()
    
    override fun put(key: K, value: V) {
        if (size >= capacity * loadFactorThreshold) {
            resize()
        }
        
        var index = getIndex(key)
        var probeCount = 0
        
        while (keys[index] != null && keys[index] != DELETED) {
            if (keys[index] == key) {
                values[index] = value
                return
            }
            index = (index + 1) % capacity
            probeCount++
            
            if (probeCount >= capacity) {
                throw IllegalStateException("Hash table is full")
            }
        }
        
        keys[index] = key
        values[index] = value
        size++
    }
    
    override fun get(key: K): V? {
        var index = getIndex(key)
        var probeCount = 0
        
        while (keys[index] != null) {
            if (keys[index] == key) {
                @Suppress("UNCHECKED_CAST")
                return values[index] as V?
            }
            index = (index + 1) % capacity
            probeCount++
            
            if (probeCount >= capacity) {
                break
            }
        }
        return null
    }
    
    override fun remove(key: K): V? {
        var index = getIndex(key)
        var probeCount = 0
        
        while (keys[index] != null) {
            if (keys[index] == key) {
                @Suppress("UNCHECKED_CAST")
                val value = values[index] as V?
                keys[index] = DELETED
                values[index] = null
                size--
                return value
            }
            index = (index + 1) % capacity
            probeCount++
            
            if (probeCount >= capacity) {
                break
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
        keys = arrayOfNulls(capacity)
        values = arrayOfNulls(capacity)
        size = 0
    }
    
    override fun keys(): Set<K> {
        val keySet = mutableSetOf<K>()
        for (i in keys.indices) {
            if (keys[i] != null && keys[i] != DELETED) {
                @Suppress("UNCHECKED_CAST")
                keySet.add(keys[i] as K)
            }
        }
        return keySet
    }
    
    override fun values(): Collection<V> {
        val valueList = mutableListOf<V>()
        for (i in keys.indices) {
            if (keys[i] != null && keys[i] != DELETED) {
                @Suppress("UNCHECKED_CAST")
                values[i]?.let { valueList.add(it as V) }
            }
        }
        return valueList
    }
    
    override fun entries(): Set<Map.Entry<K, V>> {
        val entries = mutableSetOf<Map.Entry<K, V>>()
        for (i in keys.indices) {
            if (keys[i] != null && keys[i] != DELETED) {
                @Suppress("UNCHECKED_CAST")
                val key = keys[i] as K
                @Suppress("UNCHECKED_CAST")
                val value = values[i] as V
                entries.add(object : Map.Entry<K, V> {
                    override val key: K = key
                    override val value: V = value
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
        val oldKeys = keys
        val oldValues = values
        
        capacity *= 2
        size = 0
        keys = arrayOfNulls(capacity)
        values = arrayOfNulls(capacity)
        
        for (i in oldKeys.indices) {
            if (oldKeys[i] != null && oldKeys[i] != DELETED) {
                @Suppress("UNCHECKED_CAST")
                put(oldKeys[i] as K, oldValues[i] as V)
            }
        }
        
        println("LinearProbingHashTable resized: $oldCapacity -> $capacity")
    }
    
    fun getProbeCount(key: K): Int {
        var index = getIndex(key)
        var probeCount = 0
        
        while (keys[index] != null) {
            if (keys[index] == key) {
                return probeCount
            }
            index = (index + 1) % capacity
            probeCount++
            
            if (probeCount >= capacity) {
                break
            }
        }
        return -1
    }
    
    fun getLoadFactor(): Float = size.toFloat() / capacity
    
    fun getAverageProbeCount(): Float {
        if (size == 0) return 0f
        
        var totalProbes = 0
        for (key in keys()) {
            totalProbes += getProbeCount(key)
        }
        return totalProbes.toFloat() / size
    }
}