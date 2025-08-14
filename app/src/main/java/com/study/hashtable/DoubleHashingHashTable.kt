package com.study.hashtable

import kotlin.math.absoluteValue

class DoubleHashingHashTable<K, V>(
    private var capacity: Int = 17,
    private val loadFactorThreshold: Float = 0.75f
) : HashTable<K, V> {
    
    init {
        capacity = findNextPrime(capacity)
    }
    
    private var keys = arrayOfNulls<Any?>(capacity)
    private var values = arrayOfNulls<Any?>(capacity)
    private var size = 0
    
    private val DELETED = Any()
    
    override fun put(key: K, value: V) {
        if (size >= capacity * loadFactorThreshold) {
            resize()
        }
        
        val hash1 = hash1(key)
        val hash2 = hash2(key)
        var index = hash1
        var attempt = 0
        
        while (attempt < capacity) {
            if (keys[index] == null || keys[index] == DELETED) {
                keys[index] = key
                values[index] = value
                size++
                return
            } else if (keys[index] == key) {
                values[index] = value
                return
            }
            
            attempt++
            index = (hash1 + attempt * hash2) % capacity
        }
        
        throw IllegalStateException("Unable to insert key: $key")
    }
    
    override fun get(key: K): V? {
        val hash1 = hash1(key)
        val hash2 = hash2(key)
        var index = hash1
        var attempt = 0
        
        while (attempt < capacity) {
            if (keys[index] == null) {
                return null
            } else if (keys[index] == key) {
                @Suppress("UNCHECKED_CAST")
                return values[index] as V?
            }
            
            attempt++
            index = (hash1 + attempt * hash2) % capacity
        }
        
        return null
    }
    
    override fun remove(key: K): V? {
        val hash1 = hash1(key)
        val hash2 = hash2(key)
        var index = hash1
        var attempt = 0
        
        while (attempt < capacity) {
            if (keys[index] == null) {
                return null
            } else if (keys[index] == key) {
                @Suppress("UNCHECKED_CAST")
                val value = values[index] as V?
                keys[index] = DELETED
                values[index] = null
                size--
                return value
            }
            
            attempt++
            index = (hash1 + attempt * hash2) % capacity
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
    
    private fun hash1(key: K): Int {
        return key.hashCode().absoluteValue % capacity
    }
    
    private fun hash2(key: K): Int {
        val prime = if (capacity > 3) capacity - 2 else 1
        return 1 + (key.hashCode().absoluteValue % prime)
    }
    
    private fun resize() {
        val oldCapacity = capacity
        val oldKeys = keys
        val oldValues = values
        
        capacity = findNextPrime(capacity * 2)
        size = 0
        keys = arrayOfNulls(capacity)
        values = arrayOfNulls(capacity)
        
        for (i in oldKeys.indices) {
            if (oldKeys[i] != null && oldKeys[i] != DELETED) {
                @Suppress("UNCHECKED_CAST")
                put(oldKeys[i] as K, oldValues[i] as V)
            }
        }
        
        println("DoubleHashingHashTable resized: $oldCapacity -> $capacity")
    }
    
    private fun findNextPrime(n: Int): Int {
        var num = n
        while (!isPrime(num)) {
            num++
        }
        return num
    }
    
    private fun isPrime(n: Int): Boolean {
        if (n <= 1) return false
        if (n <= 3) return true
        if (n % 2 == 0 || n % 3 == 0) return false
        
        var i = 5
        while (i * i <= n) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false
            }
            i += 6
        }
        return true
    }
    
    fun getProbeCount(key: K): Int {
        val hash1 = hash1(key)
        val hash2 = hash2(key)
        var index = hash1
        var attempt = 0
        
        while (attempt < capacity) {
            if (keys[index] == key) {
                return attempt
            } else if (keys[index] == null) {
                return -1
            }
            
            attempt++
            index = (hash1 + attempt * hash2) % capacity
        }
        
        return -1
    }
    
    fun getLoadFactor(): Float = size.toFloat() / capacity
    
    fun getAverageProbeCount(): Float {
        if (size == 0) return 0f
        
        var totalProbes = 0
        for (key in keys()) {
            val probeCount = getProbeCount(key)
            if (probeCount >= 0) {
                totalProbes += probeCount
            }
        }
        return totalProbes.toFloat() / size
    }
}