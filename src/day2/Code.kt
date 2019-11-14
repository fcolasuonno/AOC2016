package day2

import java.io.File

fun main() {
    val name = if (false) "test.txt" else "input.txt"
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val parsed = parse(input)
    println("Part 1 = ${part1(parsed)}")
    println("Part 2 = ${part2(parsed)}")
}

fun parse(input: List<String>) = input.map {
    it.toList()
}.requireNoNulls()

val keypad = mapOf(
        1 to Pair(-1, 1),
        2 to Pair(0, 1),
        3 to Pair(1, 1),
        4 to Pair(-1, 0),
        5 to Pair(0, 0),
        6 to Pair(1, 0),
        7 to Pair(-1, -1),
        8 to Pair(0, -1),
        9 to Pair(1, -1)
)
val reverseKeypad = keypad.map { it.value to it.key }.toMap()

class MultiNullMap<K> : HashMap<K, MultiNullMap.ValueMap<K>>(), MutableMap<K, MultiNullMap.ValueMap<K>> {
    class ValueMap<K> : HashMap<K, K?>(), MutableMap<K, K?> {
        override fun get(key: K) = super.get(key)
    }

    override fun get(key: K) = super.get(key) ?: ValueMap<K>().also { put(key, it) }
}

val crossPad = MultiNullMap<Char>().apply {
    this['5']['R'] = '6'
    this['2']['R'] = '3'
    this['2']['D'] = '6'
    this['6']['R'] = '7'
    this['6']['D'] = 'A'
    this['6']['L'] = '5'
    this['6']['U'] = '2'
    this['A']['R'] = 'B'
    this['A']['U'] = '6'
    this['1']['D'] = '3'
    this['3']['R'] = '4'
    this['3']['D'] = '7'
    this['3']['L'] = '2'
    this['3']['U'] = '1'
    this['7']['R'] = '8'
    this['7']['D'] = 'B'
    this['7']['L'] = '6'
    this['7']['U'] = '3'
    this['B']['R'] = 'C'
    this['B']['D'] = 'D'
    this['B']['L'] = 'A'
    this['B']['U'] = '7'
    this['D']['U'] = 'B'
    this['4']['D'] = '8'
    this['4']['L'] = '3'
    this['8']['R'] = '9'
    this['8']['D'] = 'C'
    this['8']['L'] = '7'
    this['8']['U'] = '4'
    this['C']['U'] = '8'
    this['C']['L'] = 'B'
    this['9']['L'] = '8'
}

fun part1(input: List<List<Char>>): Any? = input.fold(mutableListOf(5)) { combination, next ->
    combination.apply {
        val last = last()
        add(reverseKeypad.getValue(next.fold(keypad.getValue(last)) { pos, dir ->
            when (dir) {
                'L' -> (pos.first - 1).coerceIn(-1, 1) to pos.second
                'R' -> (pos.first + 1).coerceIn(-1, 1) to pos.second
                'U' -> pos.first to (pos.second + 1).coerceIn(-1, 1)
                else -> pos.first to (pos.second - 1).coerceIn(-1, 1)
            }
        }))
    }
}.drop(1).joinToString(separator = "")

fun part2(input: List<List<Char>>): Any? = input.fold(mutableListOf('5')) { combination, next ->
    combination.apply {
        val last = last()
        add(next.fold(last) { pos, dir -> crossPad[pos][dir] ?: pos })
    }
}.drop(1).joinToString(separator = "")
