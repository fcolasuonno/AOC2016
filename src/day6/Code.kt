package day6

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

fun part1(input: List<List<Char>>): Any? = input.fold(List(input.first().size) { mutableListOf<Char>() }) { list, line ->
    line.forEachIndexed { index, c -> list[index] += c }
    list
}.map { it.groupingBy { it }.eachCount().maxBy { it.value }!!.key }.joinToString(separator = "")

fun part2(input: List<List<Char>>): Any? = input.fold(List(input.first().size) { mutableListOf<Char>() }) { list, line ->
    line.forEachIndexed { index, c -> list[index] += c }
    list
}.map { it.groupingBy { it }.eachCount().minBy { it.value }!!.key }.joinToString(separator = "")
