package day14

import day5.encodeHex
import java.io.File
import java.security.MessageDigest

fun main() {
    val name = if (true) "test.txt" else "input.txt"
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val parsed = parse(input)
    println("Part 1 = ${part1(parsed)}")
    println("Part 2 = ${part2(parsed)}")
}

fun parse(input: List<String>) = input.single()

fun part1(input: String): Any? = input.let { salt ->
    val instance = MessageDigest.getInstance("MD5")
    val generateSequence = generateSequence(0) {
        it + 1
    }
    generateSequence.map {
        val byteArray = (salt + it).toByteArray()
        instance.update(byteArray)
        val hash = instance.digest().encodeHex().asSequence()
        Triple(hash, hash.windowed(3).firstOrNull {
            it[0] == it[1] && it[1] == it[2]
        }?.first(), hash.windowed(5).filter {
            it[0] == it[1] && it[1] == it[2] && it[2] == it[3] && it[3] == it[4]
        }.map { it.first() }.toSet())
    }
}.windowed(1000).withIndex().filter {
    val md5 = it.value
    val repeat = md5.first().second
    repeat != null &&
            md5.drop(1).any {
                it.third.contains(repeat)
            }
}.map { it.index }.take(64).last()

fun part2(input: String): Any? = input.let { salt ->
    val instance = MessageDigest.getInstance("MD5")
    val generateSequence = generateSequence(0) {
        it + 1
    }
    generateSequence.map {
        var byteArray = (salt + it).toCharArray()
        repeat(2017) {
            byteArray = instance.digest(byteArray.map { it.toByte() }.toByteArray()).encodeHex()
        }
        val hash = byteArray.asSequence()
        Triple(hash, hash.windowed(3).firstOrNull {
            it[0] == it[1] && it[1] == it[2]
        }?.first(), hash.windowed(5).filter {
            it[0] == it[1] && it[1] == it[2] && it[2] == it[3] && it[3] == it[4]
        }.map { it.first() }.toSet())
    }
}.windowed(1000).withIndex().filter {
    val md5 = it.value
    val repeat = md5.first().second
    repeat != null &&
            md5.drop(1).any {
                it.third.contains(repeat)
            }
}.map { it.index }.take(64).last()
