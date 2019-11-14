package day4

import java.io.File

fun main() {
    val name = if (false) "test.txt" else "input.txt"
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val parsed = parse(input)
    println("Part 1 = ${part1(parsed)}")
    println("Part 2 = ${part2(parsed)}")
}

data class Room(val obfuscated: String, val sector: Int, val checksum: String) {

    val decoded
        get() = sector.let {
            it % ('z' - 'a' + 1)
        }.let { key ->
            obfuscated.map { c ->
                if (c == '-') ' ' else 'a' + ((c - 'a' + key) % ('z' - 'a' + 1))
            }.joinToString(separator = "")
        }
    val isReal: Boolean = obfuscated.filterNot { it == '-' }.groupingBy {
        it
    }.eachCount().toList()
            .sortedWith(compareByDescending<Pair<Char, Int>> { it.second }.thenBy { it.first })
            .take(checksum.length).map {
                it.first
            }.joinToString(separator = "") == checksum
}

private val lineStructure = """([a-z-]+)-(\d+)\[(\w+)]""".toRegex()

fun parse(input: List<String>) = input.map {
    lineStructure.matchEntire(it)?.destructured?.let {
        val (obfuscated, sector, checksum) = it.toList()
        Room(obfuscated, sector.toInt(), checksum)
    }
}.requireNoNulls()

fun part1(input: List<Room>): Any? = input.filter { it.isReal }.sumBy { it.sector }

fun part2(input: List<Room>): Any? = input.filter { it.isReal && it.decoded.contains("north", ignoreCase = true) }.single().sector
