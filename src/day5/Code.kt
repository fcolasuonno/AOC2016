package day5

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

fun parse(input: List<String>) = input.single().let { door ->
    generateSequence(0) {
        it + 1
    }.map {
        MessageDigest.getInstance("MD5").digest((door + it).toByteArray())
    }.filter { b ->
        b[0] == zero && b[1] == zero && b[2].toInt().and(0xf0) == 0
    }
}

val DIGITS_LOWER = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

fun ByteArray.encodeHex() = CharArray(size * 2) {
    if (it % 2 == 0) {
        DIGITS_LOWER[(0xf0 and this[it / 2].toInt()).ushr(4)]
    } else {
        DIGITS_LOWER[0x0f and this[it / 2].toInt()]
    }
}

val zero: Byte = 0

fun part1(input: Sequence<ByteArray>): Any? = input.take(8).map { DIGITS_LOWER[0x0f and it[2].toInt()] }.joinToString(separator = "")

fun part2(input: Sequence<ByteArray>): Any? = Array<Char?>(8) { null }.let { password ->
    val list = input.map { 0x0f and it[2].toInt() to DIGITS_LOWER[(0xf0 and it[3].toInt()).ushr(4)] }
            .filter { it.first in 0..7 }.onEach {
                if (password[it.first] == null) {
                    password[it.first] = it.second
                }
            }.takeWhile { password.any { it == null } }.toList()
    password.joinToString(separator = "")
}
