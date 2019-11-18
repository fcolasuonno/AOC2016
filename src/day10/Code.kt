package day10

import java.io.File

fun main() {
    val name = if (false) "test.txt" else "input.txt"
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val parsed = parse(input)
    println("Part 1 = ${part1(parsed)}")
    println("Part 2 = ${part2()}")
}

private val assignStructure = """value (\d+) goes to bot (\d+)""".toRegex()
private val robotStructure = """bot (\d+) gives low to (output|bot) (\d+) and high to (output|bot) (\d+)""".toRegex()

interface Operable {
    fun addValue(value: Int)
}

data class Assign(val value: Int, val bot: String)
data class Output(val output: String) : Operable {
    val values = mutableListOf<Int>()
    override fun addValue(value: Int) {
        values += value
    }
}

data class Robot(val name: String, val lowOutput: Boolean, val lowNumber: String, val highOutput: Boolean, val highNumber: String) : Operable {
    val values = mutableListOf<Int>()
    override fun addValue(value: Int) {
        values += value
        if (values.size == 2) {
            low.addValue(values.min()!!)
            high.addValue(values.max()!!)
        }
    }

    lateinit var low: Operable
    lateinit var high: Operable
}

val outputs = mutableMapOf<String, Output>()

fun parse(input: List<String>) = input.mapNotNull {
    robotStructure.matchEntire(it)?.destructured?.let {
        val (bot, lowOutput, lowNumber, highOutput, highNumber) = it.toList()
        Robot(bot, lowOutput == "output", lowNumber, highOutput == "output", highNumber)
    }
}.associateBy { it.name }.apply {
    forEach { (_, bot) ->
        bot.low = if (bot.lowOutput) outputs.getOrPut(bot.lowNumber) { Output(bot.lowNumber) } else getValue(bot.lowNumber)
        bot.high = if (bot.highOutput) outputs.getOrPut(bot.highNumber) { Output(bot.highNumber) } else getValue(bot.highNumber)
    }
    input.mapNotNull {
        assignStructure.matchEntire(it)?.destructured?.let {
            val (value, bot) = it.toList()
            Assign(value.toInt(), bot)
        }
    }.forEach {
        getValue(it.bot).addValue(it.value)
    }
}

fun part1(input: Map<String, Robot>) = input.values.single { it.values.toSet() == setOf(61, 17) }.name
fun part2() = listOf("0", "1", "2").map { outputs.getValue(it).values.single() }.reduce { acc, i -> acc * i }
