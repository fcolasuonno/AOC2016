package day17

import java.io.File
import java.security.MessageDigest

fun main() {
    val name = if (false) "test.txt" else "input.txt"
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val parsed = parse(input)
    println("Part 1 = ${part1(parsed)}")
    println("Part 2 = ${part2(parsed)}")
}

fun parse(input: List<String>) = input.single()

data class Position(val hash: String, val location: Pair<Int, Int>)

fun isOpen(hash: ByteArray, direction: Pair<Char, Pair<Int, Int>>) =
        when (direction.first) {
            'U' -> direction.second.second >= 0 && (0xf0 and hash[0].toInt()).ushr(4) > 10
            'D' -> direction.second.second <= 3 && (0x0f and hash[0].toInt()) > 10
            'L' -> direction.second.first >= 0 && (0xf0 and hash[1].toInt()).ushr(4) > 10
            else -> direction.second.first <= 3 && (0x0f and hash[1].toInt()) > 10
        }

fun part1(input: String): Any? {
    val frontier = mutableSetOf<Position>().toSortedSet(compareBy<Position> { it.hash.length }
            .thenBy { 6 - it.location.first - it.location.second }.thenBy { it.hash })
    frontier.add(Position(input, (0 to 0)))
    while (frontier.isNotEmpty()) {
        val position = frontier.first()
        frontier.remove(position)
        val location = position.location

        val hash = MessageDigest.getInstance("MD5").digest(position.hash.toByteArray())
        val elements = setOf(
                'U' to location.copy(second = location.second - 1),
                'D' to location.copy(second = location.second + 1),
                'L' to location.copy(first = location.first - 1),
                'R' to location.copy(first = location.first + 1))
                .filter { isOpen(hash, it) }
                .map { Position(position.hash + it.first, it.second) }
        elements.forEach {
            if (it.location == (3 to 3)) {
                return it.hash.substring(input.length)
            } else {
                frontier.add((it))
            }
        }
    }
    return null
}

fun part2(input: String): Any? {
    val frontier = mutableSetOf<Position>().toSortedSet(compareBy<Position> { it.hash.length }
            .thenBy { 6 - it.location.first - it.location.second }.thenBy { it.hash })
    frontier.add(Position(input, (0 to 0)))
    val solutions = mutableSetOf<String>()
    while (frontier.isNotEmpty()) {
        val position = frontier.first()
        frontier.remove(position)
        val location = position.location

        val hash = MessageDigest.getInstance("MD5").digest(position.hash.toByteArray())
        val elements = setOf(
                'U' to location.copy(second = location.second - 1),
                'D' to location.copy(second = location.second + 1),
                'L' to location.copy(first = location.first - 1),
                'R' to location.copy(first = location.first + 1))
                .filter { isOpen(hash, it) }
                .map { Position(position.hash + it.first, it.second) }
        elements.forEach {
            if (it.location == (3 to 3)) {
                solutions.add(it.hash.substring(input.length))
            } else {
                frontier.add((it))
            }
        }
    }
    return solutions.map { it.length }.max()
}

