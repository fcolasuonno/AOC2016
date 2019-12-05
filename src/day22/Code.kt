package day22

import java.io.File

fun main() {
    val name = if (false) "test.txt" else "input.txt"
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val parsed = parse(input)
    println("Part 1 = ${part1(parsed)}")
    println("Part 2 = ${part2(parsed)}")
}

data class Node(val x: Int, val y: Int, val size: Int, val used: Int, val avail: Int, val use: Int)

private val lineStructure = """/dev/grid/node-x(\d+)-y(\d+)\s+(\d+)T\s+(\d+)T\s+(\d+)T\s+(\d+)%""".toRegex()

fun parse(input: List<String>) = input.drop(2).map {
    lineStructure.matchEntire(it)?.destructured?.let {
        val (name, data) = it.toList().map { it.toInt() }.let { it.take(2) to it.drop(2) }
        val (x, y) = name
        val (size, used, avail, use) = data
        Node(x, y, size, used, avail, use)
    }
}.requireNoNulls().toSet()

fun part1(input: Set<Node>): Any? = input.filter { it.used != 0 }.sumBy { node ->
    (input - node).count { node.used <= it.avail }
}

fun part2(input: Set<Node>): Any? = input.size
