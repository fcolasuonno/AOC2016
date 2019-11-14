package day1

import java.io.File
import kotlin.math.abs

fun main() {
    val name = if (false) "test.txt" else "input.txt"
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val parsed = parse(input)
    println("Part 1 = ${part1(parsed)}")
    println("Part 2 = ${part2(parsed)}")
}

fun parse(input: List<String>) = input.map {
    it.split(", ")
}.requireNoNulls()

fun rotate(dir: Char, lr: Char) =
        when (dir) {
            'N' -> if (lr == 'R') 'E' else 'W'
            'S' -> if (lr == 'R') 'W' else 'E'
            'W' -> if (lr == 'R') 'N' else 'S'
            'E' -> if (lr == 'R') 'S' else 'N'
            else -> throw IllegalArgumentException()
        }

fun part1(input: List<List<String>>): Any? = input.map {
    it.fold(mutableListOf("NO")) { list, next ->
        list.apply {
            add(rotate(list.last().first(), next.first()) + next.drop(1))
        }
    }.drop(1).groupBy { it.first() }.mapValues { it.value.sumBy { it.drop(1).toInt() } }.let {
        abs(it.getOrDefault('E', 0) - it.getOrDefault('W', 0)) +
                abs(it.getOrDefault('N', 0) - it.getOrDefault('S', 0))
    }
}

fun part2(input: List<List<String>>): Any? = input.map {
    val visited = mutableSetOf<Pair<Int, Int>>()
    it.fold(mutableListOf("NO")) { list, next ->
        list.apply {
            add(rotate(list.last().first(), next.first()) + next.drop(1))
        }
    }.drop(1).fold(mutableListOf(0 to 0)) { currentPos, steps ->
        currentPos.apply {
            val last = last()
            currentPos += (1..steps.drop(1).toInt()).map { movement ->
                last.copy(
                        first = last.first + when (steps.first()) {
                            'E' -> movement
                            'W' -> -movement
                            else -> 0
                        },
                        second = last.second + when (steps.first()) {
                            'N' -> movement
                            'S' -> -movement
                            else -> 0
                        })
            }
        }
    }.first {
        !visited.add(it)
    }.let {
        abs(it.first) + abs(it.second)
    }
}