package com.study.hashtable

interface HashTable<K, V> {
    fun put(key: K, value: V)
    fun get(key: K): V?
    fun remove(key: K): V?
    fun contains(key: K): Boolean
    fun size(): Int
    fun isEmpty(): Boolean
    fun clear()
    fun keys(): Set<K>
    fun values(): Collection<V>
    fun entries(): Set<Map.Entry<K, V>>
}