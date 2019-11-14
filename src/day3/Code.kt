package day3

import java.io.File

fun main() {
    val name = if (false) "test.txt" else "input.txt"
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val parsed = parse(input)
    println("Part 1 = ${part1(parsed)}")
    println("Part 2 = ${part2(parsed)}")
}

private val Triple<Int, Int, Int>.isTriangle: Boolean
    get() = (first + second) > third && (first + third) > second && (second + third) > first

private val lineStructure = """ +(\d+) +(\d+) +(\d+)""".toRegex()

fun parse(input: List<String>) = input.map {
    lineStructure.matchEntire(it)?.destructured?.let {
        val (a, b, c) = it.toList()
        Triple(a.toInt(), b.toInt(), c.toInt())
    }
}.requireNoNulls()

fun part1(input: List<Triple<Int, Int, Int>>): Any? = input.count {
    it.isTriangle
}

fun part2(input: List<Triple<Int, Int, Int>>): Any? = input.chunked(3) {
    listOf(
            Triple(it[0].first, it[1].first, it[2].first),
            Triple(it[0].second, it[1].second, it[2].second),
            Triple(it[0].third, it[1].third, it[2].third))
}.flatten().count {
    it.isTriangle
}