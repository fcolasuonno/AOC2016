package day22

import java.io.File
import java.util.*
import kotlin.math.abs

fun main() {
    val name = if (false) "test.txt" else "input.txt"
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val parsed = parse(input)
    println("Part 1 = ${part1(parsed)}")
    println("Part 2 = ${part2(parsed)}")
}

private val xRange = 0..34
private val yRange = 0..28

data class Node(val x: Int, val y: Int, val size: Int, val used: Int, val avail: Int, val usePercent: Int)
data class SimpleNode(val pos: Pair<Int, Int>, val size: Int, val used: Int)
data class NodeMap(val empty: SimpleNode, val goal: SimpleNode, val steps: Int = 0) {
    var grid: SortedSet<SimpleNode> = sortedSetOf(compareBy<SimpleNode> { it.pos.first }.thenBy { it.pos.second })
    fun candidates() = setOf(empty.pos.copy(first = empty.pos.first - 1),
            empty.pos.copy(first = empty.pos.first + 1),
            empty.pos.copy(second = empty.pos.second - 1),
            empty.pos.copy(second = empty.pos.second + 1))
            .filter {
                it.first in xRange && it.second in yRange
            }.let { newEmptyPositions ->
                grid.filter { it.pos in newEmptyPositions }
            }.mapNotNull { newNode ->
                if (empty.size >= newNode.used) {
                    val newEmptyNode = newNode.copy(used = 0)
                    val simpleNode = empty.copy(used = newNode.used)
                    this.copy(empty = newEmptyNode, goal = if (newEmptyNode.pos == goal.pos) simpleNode else goal, steps = steps + 1).also {
                        it.grid.addAll(grid.filter { it.pos != newNode.pos && it.pos != empty.pos } + setOf(newEmptyNode, simpleNode))
                    }
                } else null
            }

    fun emptyToGoal() = abs(empty.pos.first - xRange.last - 1) + empty.pos.second
    fun goalToZero() = goal.pos.first + goal.pos.second
}

private val lineStructure = """/dev/grid/node-x(\d+)-y(\d+)\s+(\d+)T\s+(\d+)T\s+(\d+)T\s+(\d+)%""".toRegex()

fun parse(input: List<String>) = input.drop(2).map {
    lineStructure.matchEntire(it)?.destructured?.let {
        val (name, data) = it.toList().map { it.toInt() }.let { it.take(2) to it.drop(2) }
        val (x, y) = name
        val (size, used, avail, use) = data
        Node(x, y, size, used, avail, use)
    }
}.requireNoNulls().toSet()

fun part1(input: Set<Node>): Any? = input.filter { it.used != 0 }.sumBy { node ->
    (input - node).count { node.used <= it.avail }
}

fun part2(input: Set<Node>): Int {
    val newInput = input.map { SimpleNode(it.x to it.y, it.size, it.used) }
    var solution: NodeMap? = null
    val candidates = NodeMap(
            newInput.single { it.used == 0 },
            newInput.single { it.pos.first == xRange.last && it.pos.second == 0 }
    ).also { it.grid.addAll(newInput) }.candidates()
            .toSortedSet(compareBy<NodeMap> { it.steps + it.emptyToGoal() }
                    .thenBy { it.empty.pos.first }.thenBy { it.empty.pos.second })
    val seen = mutableSetOf<Pair<Int, Int>>()
    while (candidates.isNotEmpty()) {
        val first = candidates.first()
        seen.add(first.empty.pos)
        candidates.remove(first)
        if (first.emptyToGoal() == 1) {
            solution = first
        } else {
            candidates.addAll(first.candidates().filter { it.empty.pos !in seen })
        }
    }

    val newCandidates = solution!!.candidates().toSortedSet(compareBy<NodeMap> { it.goal.pos.first }
            .thenBy { it.goal.pos.second }
            .thenBy { it.steps }
            .thenBy { it.empty.pos.first }.thenBy { it.empty.pos.second })
    while (newCandidates.isNotEmpty()) {
        val first = newCandidates.first()
        newCandidates.remove(first)
        if (first.goalToZero() == 0) {
            return first.steps
        } else {
            newCandidates.addAll(first.candidates())
        }
    }
    return 0
}
