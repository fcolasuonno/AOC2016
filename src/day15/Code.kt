package day15

import java.io.File

fun main() {
    val name = if (false) "test.txt" else "input.txt"
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val parsed = parse(input)
    println("Part 1 = ${part1(parsed)}")
    println("Part 2 = ${part2(parsed)}")
}

data class Disc(val delay: Int, val positions: Int, var currentPos: Int) {
    fun positionAt(time: Long) = ((time + delay + currentPos) % positions).toInt()
}

private val lineStructure = """Disc #(\d+) has (\d+) positions; at time=0, it is at position (\d+).""".toRegex()

fun parse(input: List<String>) = input.map {
    lineStructure.matchEntire(it)?.destructured?.let {
        val (disc, positions, currentPos) = it.toList()
        Disc(disc.toInt(), positions.toInt(), currentPos.toInt())
    }
}.requireNoNulls()

fun part1(input: List<Disc>): Any? = generateSequence(0L) { it + 1 }.first { time -> input.all { it.positionAt(time) == 0 } }

fun part2(input: List<Disc>) = (input + Disc(input.size + 1, 11, 0)).let { newInput -> generateSequence(0L) { it + 1 }.first { time -> newInput.all { it.positionAt(time) == 0 } } }
