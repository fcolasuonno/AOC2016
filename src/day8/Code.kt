package day8

import printWith
import java.io.File

const val test = false

fun main() {
    val name = if (test) "test.txt" else "input.txt"
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val parsed = parse(input)
    println("Part 1 = ${part1(parsed)}")
    println("Part 2 = ${part2(parsed)}")
}

interface Instruction {
    fun update(matrix: List<MutableList<Boolean>>)
}

data class Rect(val x: Int, val y: Int) : Instruction {
    override fun update(matrix: List<MutableList<Boolean>>) {
        for (i in 0 until x) {
            for (j in 0 until y) {
                matrix[i][j] = true
            }
        }
    }
}

data class RotateRow(val y: Int, val amount: Int) : Instruction {
    override fun update(matrix: List<MutableList<Boolean>>) {
        val row = matrix.map { it[y] }
        for (i in row.indices) {
            matrix[(i + amount) % row.size][y] = row[i]
        }
    }
}

data class RotateColumn(val x: Int, val amount: Int) : Instruction {
    override fun update(matrix: List<MutableList<Boolean>>) {
        val column = matrix[x].map { it }
        for (i in column.indices) {
            matrix[x][(i + amount) % column.size] = column[i]
        }
    }
}

private val rectStructure = """rect (\d+)x(\d+)""".toRegex()
private val rotateRowStructure = """rotate row y=(\d+) by (\d+)""".toRegex()
private val rotateColumnRowStructure = """rotate column x=(\d+) by (\d+)""".toRegex()

fun parse(input: List<String>) = input.map {
    rectStructure.matchEntire(it)?.destructured?.let {
        val (x, y) = it.toList()
        Rect(x.toInt(), y.toInt())
    } ?: rotateRowStructure.matchEntire(it)?.destructured?.let {
        val (y, amount) = it.toList()
        RotateRow(y.toInt(), amount.toInt())
    } ?: rotateColumnRowStructure.matchEntire(it)?.destructured?.let {
        val (x, amount) = it.toList()
        RotateColumn(x.toInt(), amount.toInt())
    }
}.requireNoNulls().let {
    if (test) List(7) { MutableList(3) { false } } else List(50) { MutableList(6) { false } }.apply {
        it.forEach { it.update(this) }
    }
}

fun part1(matrix: List<MutableList<Boolean>>) = matrix.sumBy { it.count { it } }

fun part2(matrix: List<MutableList<Boolean>>): Any? = matrix.printWith { if (it) "#" else " " }
