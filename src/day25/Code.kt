package day25

import java.io.File

fun main() {
    val name = if (false) "test.txt" else "input.txt"
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val parsed = parse(input)
    println("Part 1 = ${part1(parsed.toMutableList())}")
}

val output = mutableListOf<Long>()

val opcodes = mapOf<String, (String, String) -> ((MutableMap<String, Long>, MutableList<Triple<String, String, String>>) -> Unit)>(
        "inc" to { a: String, _ ->
            { regs: MutableMap<String, Long>, _: MutableList<Triple<String, String, String>> ->
                regs[a] = regs.getValue(a) + 1
                regs["ip"] = regs.getValue("ip") + 1
            }
        },
        "dec" to { a: String, _ ->
            { regs: MutableMap<String, Long>, _: MutableList<Triple<String, String, String>> ->
                regs[a] = regs.getValue(a) - 1
                regs["ip"] = regs.getValue("ip") + 1
            }
        },
        "tgl" to { a: String, _ ->
            { regs: MutableMap<String, Long>, input: MutableList<Triple<String, String, String>> ->
                val position = regs.getValue("ip").toInt() + regs.getValue(a).toInt()
                val instruction = input.getOrNull(position)
                if (instruction != null) {
                    input[position] = instruction.copy(first = when (instruction.first) {
                        "inc" -> "dec"
                        "dec" -> "inc"
                        "tgl" -> "inc"
                        "jnz" -> "cpy"
                        "cpy" -> "jnz"
                        else -> ""
                    })
                }
                regs["ip"] = regs.getValue("ip") + 1
            }
        },
        "cpy" to { a: String, b: String ->
            { regs: MutableMap<String, Long>, _: MutableList<Triple<String, String, String>> ->
                regs[b] = (a.toLongOrNull() ?: regs.getValue(a))
                regs["ip"] = regs.getValue("ip") + 1
            }
        },
        "jnz" to { a: String, b: String ->
            { regs: MutableMap<String, Long>, _: MutableList<Triple<String, String, String>> ->
                if ((a.toLongOrNull() ?: regs.getValue(a)) != 0L) {
                    regs["ip"] = regs.getValue("ip") + (b.toLongOrNull() ?: regs.getValue(b))
                } else {
                    regs["ip"] = regs.getValue("ip") + 1
                }
            }
        },
        "out" to { a: String, _ ->
            { regs: MutableMap<String, Long>, _: MutableList<Triple<String, String, String>> ->
                output.add(a.toLongOrNull() ?: regs.getValue(a))
                regs["ip"] = regs.getValue("ip") + 1
            }
        }
)

private val lineStructure = """(\w+) (-?\w+) ?(-?\w+)?""".toRegex()

fun parse(input: List<String>) = input.map {
    lineStructure.matchEntire(it)?.destructured?.let {
        val (op, op1, op2) = it.toList()
        Triple(op, op1, op2)
    }
}.requireNoNulls()

fun part1(input: MutableList<Triple<String, String, String>>): Any? = generateSequence(1) { it + 1 }.first {
    output.clear()
    mutableMapOf<String, Long>("a" to it.toLong()).withDefault { 0L }.let { regs ->
        generateSequence {
            input.getOrNull(regs.getValue("ip").toInt())
        }.map { (op, op1, op2) ->
            opcodes.getValue(op)(op1, op2)(regs, input)
        }.takeWhile { output.size < 10 }.last()
        output.zipWithNext().all { it.first != it.second }
    }
}
