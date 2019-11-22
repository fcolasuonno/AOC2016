package day13

import java.io.File
import kotlin.math.abs


private fun Triple<Int, Int, Int>.movements() = listOf(
        Triple(first - 1, second, third + 1),
        Triple(first + 1, second, third + 1),
        Triple(first, second - 1, third + 1),
        Triple(first, second + 1, third + 1))

private val test = false

fun main() {
    val name = if (test) "test.txt" else "input.txt"
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val parsed = parse(input)
    println("Part 1 = ${part1(parsed, if (test) Pair(7, 4) else Pair(31, 39))}")
    println("Part 2 = ${part2(parsed)}")
}

@UseExperimental(ExperimentalStdlibApi::class)
fun parse(input: List<String>) = input.single().toInt().let { magic -> { x: Int, y: Int -> x >= 0 && y >= 0 && (x * x + 3 * x + 2 * x * y + y + y * y + magic).countOneBits() % 2 == 0 } }

fun part1(isEmpty: (Int, Int) -> Boolean, dest: Pair<Int, Int>): Int {
    val seen = mutableSetOf<Pair<Int, Int>>()
    val explore = sortedSetOf(compareBy<Triple<Int, Int, Int>> { abs(dest.first - it.first) + abs(dest.second - it.second) }
            .thenBy { it.third }.thenBy { it.first }.thenBy { it.second }
            , Triple(1, 1, 0))
    while (explore.isNotEmpty()) {
        val newPos = explore.first()
        if (newPos.first == dest.first && newPos.second == dest.second) {
            return newPos.third
        }
        explore.remove(newPos)
        seen.add(newPos.first to newPos.second)
        explore.addAll(newPos.movements().filter { isEmpty(it.first, it.second) && Pair(it.first, it.second) !in seen })
    }
    return 0
}

fun part2(isEmpty: (Int, Int) -> Boolean): Int {
    val seen = mutableSetOf<Pair<Int, Int>>()
    val explore = sortedSetOf(compareBy<Triple<Int, Int, Int>> { it.third }.thenBy { it.first }.thenBy { it.second }
            , Triple(1, 1, 0))
    while (explore.isNotEmpty()) {
        val newPos = explore.first()
        explore.remove(newPos)
        seen.add(newPos.first to newPos.second)
        explore.addAll(newPos.movements().filter { isEmpty(it.first, it.second) && Pair(it.first, it.second) !in seen && it.third <= 50 })
    }
    return seen.size
}
