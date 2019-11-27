package day16

import java.io.File

fun main() {
    val name = if (false) "test.txt" else "input.txt"
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val parsed = parse(input)
    println("Part 1 = ${part1(parsed)}")
    println("Part 2 = ${part2(parsed)}")
}

fun parse(input: List<String>) = input.single().split(' ').let { it[0].toInt() to it[1].toList().map { it != '0' } }

fun part1(input: Pair<Int, List<Boolean>>) = generateSequence(
        generateSequence(input.second) { prev -> prev + false + prev.asReversed().map { !it } }.first { it.size >= input.first }.take(input.first)
) {
    it.chunked(2) { it[0] == it[1] }
}.drop(1).first { it.size % 2 != 0 }.map { if (it) '1' else '0' }.joinToString("")

fun part2(input: Pair<Int, List<Boolean>>): Any? = part1(35651584 to input.second)
