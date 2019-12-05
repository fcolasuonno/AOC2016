package day21

import java.io.File

fun main() {
    val name = if (false) "test.txt" else "input.txt"
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val parsed = parse(input)
    println("Part 1 = ${part1(parsed)}")
    println("Part 2 = ${part2(parsed)}")
}

sealed class Op {
    abstract fun exec(input: List<Char>): List<Char>
    abstract fun reverse(input: List<Char>): List<Char>
    data class Swap(val from: Int, val to: Int) : Op() {
        override fun exec(input: List<Char>) = input.mapIndexed { i, c ->
            when (i) {
                from -> input[to]
                to -> input[from]
                else -> c
            }
        }

        override fun reverse(input: List<Char>) = exec(input)
    }

    data class SwapLetter(val from: Char, val to: Char) : Op() {
        override fun exec(input: List<Char>) = input.map { c ->
            when (c) {
                from -> to
                to -> from
                else -> c
            }
        }

        override fun reverse(input: List<Char>) = exec(input)
    }

    data class Rotate(val lr: String, val amount: Int) : Op() {
        override fun exec(input: List<Char>) = if (lr == "right")
            input.indices.map { input[(it + input.size - amount) % input.size] }
        else
            input.indices.map { input[(it + amount) % input.size] }

        override fun reverse(input: List<Char>) = if (lr != "right")
            input.indices.map { input[(it + input.size - amount) % input.size] }
        else
            input.indices.map { input[(it + amount) % input.size] }
    }

    data class RotateIndex(val letter: Char) : Op() {
        override fun exec(input: List<Char>) = input.indexOf(letter).let {
            if (it >= 4) (it + 2) else (it + 1)
        }.let { amount ->
            input.indices.map { input[(it + input.size - amount % input.size) % input.size] }
        }

        override fun reverse(input: List<Char>) = input.indexOf(letter).let {
            if (it == 0 || it % 2 == 1) {
                1 + it / 2
            } else {
                (input.size - 1) * (input.size - 2 - it) / 2
            }
        }.let { amount ->
            input.indices.map { input[(it + amount) % input.size] }
        }
    }

    data class Reverse(val start: Int, val end: Int) : Op() {
        override fun exec(input: List<Char>) = input.take(start) + input.slice(start..end).reversed() + input.drop(end + 1)
        override fun reverse(input: List<Char>) = exec(input)
    }

    data class Move(val from: Int, val end: Int) : Op() {
        override fun exec(input: List<Char>) = if (end >= from) {
            input.slice((0..end) - from) + input[from] + input.drop(end + 1)
        } else {
            input.slice(0 until end) + input[from] + input.slice((end until input.size) - from)
        }

        override fun reverse(input: List<Char>) = if (from >= end) {
            input.slice((0..from) - end) + input[end] + input.drop(from + 1)
        } else {
            input.slice(0 until from) + input[end] + input.slice((from until input.size) - end)
        }
    }
}

private val swapPosition = """swap position (\d+) with position (\d+)""".toRegex()
private val swapLetter = """swap letter (.) with letter (.)""".toRegex()
private val rotate = """rotate (left|right) (\d+) steps?""".toRegex()
private val rotateIndex = """rotate based on position of letter (.)""".toRegex()
private val reverse = """reverse positions (\d+) through (\d+)""".toRegex()
private val move = """move position (.) to position (.)""".toRegex()

fun parse(input: List<String>) = input.map {
    swapPosition.matchEntire(it)?.destructured?.let {
        val (positionFrom, positionTo) = it.toList()
        Op.Swap(positionFrom.toInt(), positionTo.toInt())
    } ?: swapLetter.matchEntire(it)?.destructured?.let {
        val (letterFrom, letterTo) = it.toList()
        Op.SwapLetter(letterFrom.single(), letterTo.single())
    } ?: rotate.matchEntire(it)?.destructured?.let {
        val (lr, steps) = it.toList()
        Op.Rotate(lr, steps.toInt())
    } ?: rotateIndex.matchEntire(it)?.destructured?.let {
        val (letter) = it.toList()
        Op.RotateIndex(letter.single())
    } ?: reverse.matchEntire(it)?.destructured?.let {
        val (start, end) = it.toList()
        Op.Reverse(start.toInt(), end.toInt())
    } ?: move.matchEntire(it)?.destructured?.let {
        val (from, to) = it.toList()
        Op.Move(from.toInt(), to.toInt())
    }
}.requireNoNulls()

fun part1(input: List<Op>): Any? = input.fold("abcdefgh".toList()) { seq, op ->
    op.exec(seq)
}.joinToString("")

fun part2(input: List<Op>): Any? = input.reversed().fold("fbgdceah".toList()) { seq, op ->
    op.reverse(seq)
}.joinToString("")