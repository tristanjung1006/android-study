package com.study.hashtable

import kotlin.random.Random
import kotlin.system.measureTimeMillis

class HashTableTest {
    
    data class TestResult(
        val name: String,
        val insertTime: Long,
        val searchTime: Long,
        val deleteTime: Long,
        val memoryUsage: Long,
        val collisions: Int = 0,
        val averageProbeCount: Float = 0f
    )
    
    fun runPerformanceTest() {
        println("=== HashTable Performance Test ===\n")
        
        val testSizes = listOf(100, 1000, 10000)
        
        for (size in testSizes) {
            println("Test Data Size: $size")
            println("-".repeat(50))
            
            val testData = generateTestData(size)
            val results = mutableListOf<TestResult>()
            
            results.add(testChainingHashTable(testData))
            results.add(testLinearProbingHashTable(testData))
            results.add(testQuadraticProbingHashTable(testData))
            results.add(testDoubleHashingHashTable(testData))
            results.add(testBuiltInHashMap(testData))
            
            printResults(results)
            println()
        }
        
        runCollisionTest()
        runResizeTest()
    }
    
    private fun generateTestData(size: Int): List<Pair<String, Int>> {
        return List(size) { i ->
            "key_${Random.nextInt(1000000)}" to Random.nextInt(1000)
        }
    }
    
    private fun testChainingHashTable(testData: List<Pair<String, Int>>): TestResult {
        val table = ChainingHashTable<String, Int>()
        val memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        
        val insertTime = measureTimeMillis {
            testData.forEach { (key, value) ->
                table.put(key, value)
            }
        }
        
        val searchTime = measureTimeMillis {
            testData.forEach { (key, _) ->
                table.get(key)
            }
        }
        
        val deleteTime = measureTimeMillis {
            testData.take(testData.size / 2).forEach { (key, _) ->
                table.remove(key)
            }
        }
        
        val memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        
        return TestResult(
            name = "Chaining",
            insertTime = insertTime,
            searchTime = searchTime,
            deleteTime = deleteTime,
            memoryUsage = memoryAfter - memoryBefore,
            collisions = table.getCollisionCount()
        )
    }
    
    private fun testLinearProbingHashTable(testData: List<Pair<String, Int>>): TestResult {
        val table = LinearProbingHashTable<String, Int>()
        val memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        
        val insertTime = measureTimeMillis {
            testData.forEach { (key, value) ->
                table.put(key, value)
            }
        }
        
        val searchTime = measureTimeMillis {
            testData.forEach { (key, _) ->
                table.get(key)
            }
        }
        
        val deleteTime = measureTimeMillis {
            testData.take(testData.size / 2).forEach { (key, _) ->
                table.remove(key)
            }
        }
        
        val memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        
        return TestResult(
            name = "Linear Probing",
            insertTime = insertTime,
            searchTime = searchTime,
            deleteTime = deleteTime,
            memoryUsage = memoryAfter - memoryBefore,
            averageProbeCount = table.getAverageProbeCount()
        )
    }
    
    private fun testQuadraticProbingHashTable(testData: List<Pair<String, Int>>): TestResult {
        val table = QuadraticProbingHashTable<String, Int>()
        val memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        
        val insertTime = measureTimeMillis {
            testData.forEach { (key, value) ->
                table.put(key, value)
            }
        }
        
        val searchTime = measureTimeMillis {
            testData.forEach { (key, _) ->
                table.get(key)
            }
        }
        
        val deleteTime = measureTimeMillis {
            testData.take(testData.size / 2).forEach { (key, _) ->
                table.remove(key)
            }
        }
        
        val memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        
        return TestResult(
            name = "Quadratic Probing",
            insertTime = insertTime,
            searchTime = searchTime,
            deleteTime = deleteTime,
            memoryUsage = memoryAfter - memoryBefore,
            averageProbeCount = table.getAverageProbeCount()
        )
    }
    
    private fun testDoubleHashingHashTable(testData: List<Pair<String, Int>>): TestResult {
        val table = DoubleHashingHashTable<String, Int>()
        val memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        
        val insertTime = measureTimeMillis {
            testData.forEach { (key, value) ->
                table.put(key, value)
            }
        }
        
        val searchTime = measureTimeMillis {
            testData.forEach { (key, _) ->
                table.get(key)
            }
        }
        
        val deleteTime = measureTimeMillis {
            testData.take(testData.size / 2).forEach { (key, _) ->
                table.remove(key)
            }
        }
        
        val memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        
        return TestResult(
            name = "Double Hashing",
            insertTime = insertTime,
            searchTime = searchTime,
            deleteTime = deleteTime,
            memoryUsage = memoryAfter - memoryBefore,
            averageProbeCount = table.getAverageProbeCount()
        )
    }
    
    private fun testBuiltInHashMap(testData: List<Pair<String, Int>>): TestResult {
        val table = HashMap<String, Int>()
        val memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        
        val insertTime = measureTimeMillis {
            testData.forEach { (key, value) ->
                table[key] = value
            }
        }
        
        val searchTime = measureTimeMillis {
            testData.forEach { (key, _) ->
                table[key]
            }
        }
        
        val deleteTime = measureTimeMillis {
            testData.take(testData.size / 2).forEach { (key, _) ->
                table.remove(key)
            }
        }
        
        val memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        
        return TestResult(
            name = "Built-in HashMap",
            insertTime = insertTime,
            searchTime = searchTime,
            deleteTime = deleteTime,
            memoryUsage = memoryAfter - memoryBefore
        )
    }
    
    private fun printResults(results: List<TestResult>) {
        println(String.format("%-20s %10s %10s %10s %12s %10s %10s",
            "Implementation", "Insert(ms)", "Search(ms)", "Delete(ms)", "Memory(KB)", "Collisions", "Avg Probe"))
        println("-".repeat(95))
        
        results.forEach { result ->
            println(String.format("%-20s %10d %10d %10d %12d %10d %10.2f",
                result.name,
                result.insertTime,
                result.searchTime,
                result.deleteTime,
                result.memoryUsage / 1024,
                result.collisions,
                result.averageProbeCount
            ))
        }
    }
    
    private fun runCollisionTest() {
        println("\n=== Collision Test ===")
        println("Testing with data having same hash value")
        println("-".repeat(50))
        
        val collisionData = List(100) { i ->
            CollisionKey(i) to i
        }
        
        val chainingTable = ChainingHashTable<CollisionKey, Int>()
        collisionData.forEach { (key, value) ->
            chainingTable.put(key, value)
        }
        println("Chaining - Collision count: ${chainingTable.getCollisionCount()}")
        println("Chaining - Bucket size distribution: ${chainingTable.getBucketSizes()}")
        
        val linearTable = LinearProbingHashTable<CollisionKey, Int>()
        collisionData.forEach { (key, value) ->
            linearTable.put(key, value)
        }
        println("Linear Probing - Average probe count: ${linearTable.getAverageProbeCount()}")
    }
    
    private fun runResizeTest() {
        println("\n=== Dynamic Resize Test ===")
        println("Checking when resizing occurs")
        println("-".repeat(50))
        
        val table = ChainingHashTable<Int, String>(capacity = 4, loadFactorThreshold = 0.75f)
        
        for (i in 1..20) {
            table.put(i, "value_$i")
            if (i % 3 == 0) {
                println("Inserted $i items - Load Factor: ${table.getLoadFactor()}")
            }
        }
    }
    
    data class CollisionKey(val value: Int) {
        override fun hashCode(): Int = 42
    }
}

class HashTableMain {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val test = HashTableTest()
            test.runPerformanceTest()
        }
    }
}