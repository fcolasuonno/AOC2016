package day24

import java.io.File

fun main() {
    val name = if (false) "test.txt" else "input.txt"
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val (parsed, points) = parse(input)
    println("Part 1 = ${part1(parsed, points)}")
    println("Part 2 = ${part2(parsed, points)}")
}

fun parse(input: List<String>) = input.withIndex().flatMap { (j, s) ->
    s.withIndex().mapNotNull { (i, c) -> if (c == '#') Pair(i, j) else null }
}.toSet() to input.withIndex().flatMap { (j, s) ->
    s.withIndex().mapNotNull { (i, c) -> if (c.isDigit()) Pair(i, j) to c else null }
}.toMap()

fun part1(input: Set<Pair<Int, Int>>, nodesToExplores: Map<Pair<Int, Int>, Char>): Any? {
    val frontier = nodesToExplores.filterValues { it == '0' }.keys.map { Triple(it, setOf('0'), 0) }.toSortedSet(compareBy<Triple<Pair<Int, Int>, Set<Char>, Int>> { it.third }
            .thenByDescending { it.second.size }.thenBy { it.second.sorted().joinToString("") }.thenBy { it.first.first }.thenBy { it.first.second })
    val seen = mutableMapOf<Set<Char>, MutableSet<Pair<Int, Int>>>()
    while (frontier.isNotEmpty()) {
        val current = frontier.first()
        frontier.remove(current)
        if (current.second.size == nodesToExplores.size) {
            return current.third
        }
        seen.getOrPut(current.second) { mutableSetOf<Pair<Int, Int>>() }.add(current.first)
        val next = current.first.neighbours().filter { it !in input && it !in seen.getValue(current.second) }.map {
            Triple(it, current.second + (nodesToExplores[it] ?: '0'), current.third + 1)
        }
        frontier.addAll(next)
    }
    return 0
}

fun part2(input: Set<Pair<Int, Int>>, nodesToExplores: Map<Pair<Int, Int>, Char>): Any? {
    val frontier = nodesToExplores.filterValues { it == '0' }.keys.map { Triple(it, setOf('0'), 0) }.toSortedSet(compareBy<Triple<Pair<Int, Int>, Set<Char>, Int>> { it.third }
            .thenByDescending { it.second.size }.thenBy { it.second.sorted().joinToString("") }.thenBy { it.first.first }.thenBy { it.first.second })
    val seen = mutableMapOf<Set<Char>, MutableSet<Pair<Int, Int>>>()
    while (frontier.isNotEmpty()) {
        val current = frontier.first()
        frontier.remove(current)
        if (current.second.size == nodesToExplores.size && nodesToExplores[current.first] == '0') {
            return current.third
        }
        seen.getOrPut(current.second) { mutableSetOf<Pair<Int, Int>>() }.add(current.first)
        val next = current.first.neighbours().filter { it !in input && it !in seen.getValue(current.second) }.map {
            Triple(it, current.second + (nodesToExplores[it] ?: '0'), current.third + 1)
        }
        frontier.addAll(next)
    }
    return 0
}

private fun Pair<Int, Int>.neighbours() = listOf(
        copy(first = first - 1),
        copy(second = second - 1),
        copy(first = first + 1),
        copy(second = second + 1)
)