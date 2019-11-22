package day12

import java.io.File

fun main() {
    val name = if (false) "test.txt" else "input.txt"
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val parsed = parse(input)
    println("Part 1 = ${part1(parsed)}")
    println("Part 2 = ${part2(parsed)}")
}

val opcodes = mapOf<String, (String, String) -> ((MutableMap<String, Long>) -> Unit)>(
        "cpy" to { a: String, b: String ->
            { regs: MutableMap<String, Long> ->
                regs[b] = (a.toLongOrNull() ?: regs.getValue(a))
                regs["ip"] = regs.getValue("ip") + 1
            }
        },
        "inc" to { a: String, _ ->
            { regs: MutableMap<String, Long> ->
                regs[a] = regs.getValue(a) + 1
                regs["ip"] = regs.getValue("ip") + 1
            }
        },
        "dec" to { a: String, _ ->
            { regs: MutableMap<String, Long> ->
                regs[a] = regs.getValue(a) - 1
                regs["ip"] = regs.getValue("ip") + 1
            }
        },
        "jnz" to { a: String, b: String ->
            { regs: MutableMap<String, Long> ->
                if ((a.toLongOrNull() ?: regs.getValue(a)) != 0L) {
                    regs["ip"] = regs.getValue("ip") + b.toInt()
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
        opcodes.getValue(op).invoke(op1, op2)
    }
}.requireNoNulls()

fun part1(input: List<(MutableMap<String, Long>) -> Unit>): Any? = mutableMapOf<String, Long>().withDefault { 0L }.let { regs ->
    generateSequence {
        input.getOrNull(regs.getValue("ip").toInt())
    }.map { it(regs) }.last()
    regs["a"]
}

fun part2(input: List<(MutableMap<String, Long>) -> Unit>): Any? = mutableMapOf<String, Long>("c" to 1).withDefault { 0L }.let { regs ->
    generateSequence {
        input.getOrNull(regs.getValue("ip").toInt())
    }.map { it(regs) }.last()
    regs["a"]
}
