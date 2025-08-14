package com.study.hashtable

import kotlin.math.absoluteValue

class QuadraticProbingHashTable<K, V>(
    capacity: Int = 16,
    private val loadFactorThreshold: Float = 0.5f
) : HashTable<K, V> {
    
    private var capacity: Int
    private var keys: Array<Any?>
    private var values: Array<Any?>
    
    init {
        this.capacity = findNextPrime(if (capacity < 16) 16 else capacity)
        this.keys = arrayOfNulls(this.capacity)
        this.values = arrayOfNulls(this.capacity)
    }
    private var size = 0
    
    private val DELETED = Any()
    
    override fun put(key: K, value: V) {
        if (size >= capacity * loadFactorThreshold) {
            resize()
        }
        
        val baseIndex = getIndex(key)
        var attempt = 0
        val maxAttempts = capacity / 2
        
        while (attempt <= maxAttempts) {
            val index = (baseIndex + attempt * attempt) % capacity
            
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
        }
        
        resize()
        put(key, value)
    }
    
    override fun get(key: K): V? {
        val baseIndex = getIndex(key)
        var attempt = 0
        
        while (attempt < capacity) {
            val index = (baseIndex + attempt * attempt) % capacity
            
            if (keys[index] == null) {
                return null
            } else if (keys[index] == key) {
                @Suppress("UNCHECKED_CAST")
                return values[index] as V?
            }
            
            attempt++
        }
        
        return null
    }
    
    override fun remove(key: K): V? {
        val baseIndex = getIndex(key)
        var attempt = 0
        
        while (attempt < capacity) {
            val index = (baseIndex + attempt * attempt) % capacity
            
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
    
    private fun getIndex(key: K): Int {
        return key.hashCode().absoluteValue % capacity
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
        
        println("QuadraticProbingHashTable resized: $oldCapacity -> $capacity")
    }
    
    fun getProbeCount(key: K): Int {
        val baseIndex = getIndex(key)
        var attempt = 0
        
        while (attempt < capacity) {
            val index = (baseIndex + attempt * attempt) % capacity
            
            if (keys[index] == key) {
                return attempt
            } else if (keys[index] == null) {
                return -1
            }
            
            attempt++
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