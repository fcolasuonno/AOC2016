package day19

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
    1..it.toInt()
}.requireNoNulls().single()

fun part1(input: IntRange): Any? {
    val copy = input.toMutableSet()
    var iter = copy.iterator()
    while (copy.size > 1) {
        if (!iter.hasNext()) {
            iter = copy.iterator()
        }
        iter.next()
        if (!iter.hasNext()) {
            iter = copy.iterator()
        }
        iter.next()
        iter.remove()

    }
    return copy.single()
}

fun part2(input: IntRange): Any? {
    val copy = input.toMutableSet()
    var iter = copy.iterator()
    repeat(copy.size / 2 + 1) {
        iter.next()
    }
    while (copy.size > 1) {
        iter.remove()
        if (!iter.hasNext()) {
            iter = copy.iterator()
        }
        iter.next()
        if (copy.size % 2 == 0) {
            if (!iter.hasNext()) {
                iter = copy.iterator()
            }
            iter.next()
        }
    }
    return copy.single()
}
