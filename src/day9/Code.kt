package day9

import java.io.File

fun main() {
    val name = if (false) "test.txt" else "input.txt"
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val parsed = parse(input)
    println("Part 1 = ${part1(parsed)}")
    println("Part 2 = ${part2(parsed)}")
}

private val lineStructure = """\((\d+)x(\d+)\)""".toRegex()

fun parse(input: List<String>) = input.requireNoNulls()

fun part1(input: List<String>): Any? = input.map { string ->
    generateSequence(0 to string) { (start, s) ->
        lineStructure.takeIf { start < s.length }?.find(s, start)?.let {
            val (rangeLength, repetitions) = it.destructured
            val pattern = s.substring(it.range.last + 1, (it.range.last + 1 + rangeLength.toInt()))
            val repeated = pattern.repeat(repetitions.toInt())
            val replaceRange = s.replaceRange(it.range.first..(it.range.last + rangeLength.toInt()), repeated)
            (it.range.first + repeated.length) to replaceRange
        }
    }.last().second.length
}

interface Expandable {
    fun expandedCount(): Long
}

data class Node(val string: String) : Expandable {
    override fun expandedCount() = string.length.toLong()
}

data class Repetition(val repetition: Long, val string: String) : Expandable {
    override fun expandedCount() = repetition * string.toNodes().map { it.expandedCount() }.sum()
}

fun String.toNodes(): List<Expandable> = lineStructure.find(this)?.let {
    val (rangeLength, repetitions) = it.destructured
    val repetition = repetitions.toLong()
    val repeatEnd = it.range.last + 1 + rangeLength.toInt()
    listOf(Node(substring(0, it.range.first)), Repetition(repetition, substring(it.range.last + 1, repeatEnd))) + substring(repeatEnd).toNodes()
} ?: listOf(Node(this))

fun part2(input: List<String>): Any? = input.map { string ->
    val nodes = string.toNodes()
    nodes.map { it.expandedCount() }.sum()
}