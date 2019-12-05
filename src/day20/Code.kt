package day20

import java.io.File
import java.util.*

fun main() {
    val name = if (false) "test.txt" else "input.txt"
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val parsed = parse(input)
    println("Part 1 = ${part1(parsed)}")
    println("Part 2 = ${part2(parsed)}")
}

private val lineStructure = """(\d+)-(\d+)""".toRegex()

fun parse(input: List<String>) = input.map {
    lineStructure.matchEntire(it)?.destructured?.let {
        val (start, end) = it.toList()
        (start.toLong())..(end.toLong())
    }
}.requireNoNulls().toSortedSet(compareBy<LongRange> { it.first }.thenByDescending { it.last })

fun part1(input: SortedSet<LongRange>): Any? {
    var first = input.first()
    var overlapping = input.filter { it.last in first || (first.last + 1) in it }
    while (overlapping.size > 1) {
        input.removeAll(overlapping)
        first = first.first..(overlapping.map { it.last }.max()!!)
        input.add(first)
        overlapping = input.filter { it.last in first || (first.last + 1) in it }
    }
    return first.last + 1
}

fun part2(input: SortedSet<LongRange>) = 4294967296L - generateSequence<List<LongRange>>(input.toList()) { reduced ->
    val seen = mutableSetOf<LongRange>()
    reduced.mapNotNull { range ->
        if (seen.none { range.last in it }) {
            (range.first..reduced.filter { it.last in range || (range.last + 1) in it }.map { it.last }.max()!!).also {
                seen.add(it)
            }
        } else null
    }.takeIf { reduced != it }
}.last().map { it.last - it.first + 1 }.sum()


