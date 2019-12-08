package day23

import java.io.File

fun main() {
    val name = if (false) "test.txt" else "input.txt"
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val parsed = parse(input)
    println("Part 1 = ${part1(parsed.toMutableList())}")
    println("Part 2 = ${part2(parsed.toMutableList())}")
}

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
        }
)

private val lineStructure = """(\w+) (-?\w+) ?(-?\w+)?""".toRegex()

fun parse(input: List<String>) = input.map {
    lineStructure.matchEntire(it)?.destructured?.let {
        val (op, op1, op2) = it.toList()
        Triple(op, op1, op2)
    }
}.requireNoNulls()

fun part1(input: MutableList<Triple<String, String, String>>): Any? = mutableMapOf<String, Long>("a" to 7).withDefault { 0L }.let { regs ->
    generateSequence {
        input.getOrNull(regs.getValue("ip").toInt())
    }.map { (op, op1, op2) ->
        opcodes.getValue(op)(op1, op2)(regs, input)
    }.last()
    regs["a"]
}

fun part2(input: MutableList<Triple<String, String, String>>): Any? = mutableMapOf<String, Long>("a" to 12).withDefault { 0L }.let { regs ->
    generateSequence {
        val ip = regs.getValue("ip").toInt()
        input.getOrNull(ip)?.let {
            ip to it
        }
    }.map { (index, ops) ->
        val (op, op1, op2) = ops
        if (op == "jnz" && op2 == "-2") {
            if (input.slice((index - 2) until index).map { it.first } == listOf("inc", "dec")) {
                val a = (input[index - 2].second)
                val b = (input[index - 1].second)
                regs[a] = regs[a]!! + regs[b]!!
                regs[b] = 0
            }
        }
        if (op == "jnz" && op2 == "-5") {
            if (input.slice((index - 5) until index).map { it.first } == listOf("cpy", "inc", "dec", "jnz", "dec")) {
                val a = (input[index - 4].second)
                val b = (input[index - 5].second)
                val c = (input[index - 2].second)
                val d = (input[index - 1].second)
                regs[a] = regs[a]!! + (b.toLongOrNull() ?: regs[b]!!) * regs[d]!!
                regs[c] = 0
                regs[d] = 0
            }
        }
        opcodes.getValue(op)(op1, op2)(regs, input)

    }.last()
    regs["a"]
}
