package day7

import java.io.File

fun main() {
    val name = if (false) "test.txt" else "input.txt"
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val parsed = parse(input)
    println("Part 1 = ${part1(parsed)}")
    println("Part 2 = ${part2(parsed)}")
}

fun parse(input: List<String>) = input.requireNoNulls()

private fun String.isABBA() = windowed(4).any {
    it[0] == it[3] && it[1] == it[2] && it[0] != it[1]
}

fun part1(input: List<String>): Any? = input.count {
    val (supernet, hyper) = it.split("""[\[\]]""".toRegex())
            .withIndex().partition { it.index % 2 == 0 }
    supernet.any { it.value.isABBA() } && hyper.none { it.value.isABBA() }
}

fun part2(input: List<String>): Any? = input.count {
    val (supernet, hyper) = it.split("""[\[\]]""".toRegex())
            .withIndex().partition { it.index % 2 == 0 }
    val abas = supernet.flatMap { it.value.windowed(3).filter { it[0] == it[2] && it[0] != it[1] } }
    val babs = hyper.flatMap { it.value.windowed(3).filter { it[0] == it[2] && it[0] != it[1] } }
    abas.any { "${it[1]}${it[0]}${it[1]}" in babs }
}
