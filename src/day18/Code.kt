package day18

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
    it.map { tile -> tile == '^' }
}.requireNoNulls().single().let { generateSequence(it) { prev ->
    prev.indices.map { index ->
        val (left, center, right) = ((index - 1)..(index+1)).map { i -> prev.getOrNull(i) ?: false }
        (center && ((left && !right) || (!left && right))) ||
                (!center && !right && left) ||
                (!center && right && !left)
    }
} }

fun part1(input: Sequence<List<Boolean>>): Any? = input.take(40).sumBy { line ->
    line.count { !it }
}

fun part2(input: Sequence<List<Boolean>>): Any? = input.take(400000).sumBy { line ->
    line.count { !it }
}
