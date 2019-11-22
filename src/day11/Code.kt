package day11

private val test = false
fun main() {
    val parsed = if (test) listOf(
            setOf<Portable>(Microchip('H'), Microchip('L')),
            setOf<Portable>(Generator('H')),
            setOf<Portable>(Generator('L')),
            setOf<Portable>()
    ) else listOf(
            setOf<Portable>(Generator('T'), Microchip('T'), Generator('P'), Generator('S')),
            setOf<Portable>(Microchip('P'), Microchip('S')),
            setOf<Portable>(Generator('p'), Microchip('p'), Generator('R'), Microchip('R')),
            setOf<Portable>()
    )
    println("Part 1 = ${solve(parsed)}")
    println("Part 2 = ${solve(parsed.mapIndexed { index, set ->
        if (index == 0) (set + setOf(Generator('E'), Microchip('E'), Generator('D'), Microchip('D'))) else set
    }
    )}")
}

interface Portable {
    val element: Char
}

private val Set<Portable>.compatible: Boolean
    get() =
        if (size <= 1) true
        else {
            val microchips = filterIsInstance<Microchip>().map { it.element }
            val generators = filterIsInstance<Generator>().map { it.element }
            generators.isEmpty() || microchips.minus(generators).isEmpty()
        }


data class Microchip(override val element: Char) : Portable {
    override fun toString() = element + "M"
}

data class Generator(override val element: Char) : Portable {

    override fun toString() = element + "G"
}

data class Plan(val elevator: Int, val step: Int, val status: List<Set<Portable>>, val priority: Int) {
    override fun toString() = status.toString()

    val hash: String
        get() {
            val elementMap = mutableMapOf<Char, Int>()
            val mapIndexed = status.mapIndexed { index: Int, set: Set<Portable> ->
                index.toString() + set.map { (if (it is Generator) "G" else "M") + elementMap.getOrPut(it.element) { elementMap.size + 1 } }.sorted()
            }.joinToString()
            return elevator.toString() + mapIndexed
        }
}

fun solve(input: List<Set<Portable>>): Int {
    val seen = mutableSetOf<String>()
    val totalPortables = input.sumBy { it.size }
    val toVisit = mutableListOf(Plan(0, 0, input, 0)).toSortedSet(
            compareByDescending<Plan> { it.priority }.thenBy { it.step }.thenBy { it.hash })
    while (toVisit.isNotEmpty()) {
        val currentPlan = toVisit.first()
        toVisit.remove(currentPlan)
        seen.add(currentPlan.hash)
        if (currentPlan.status[3].size == totalPortables) {
            return currentPlan.step
        } else {
            val currentFloor = currentPlan.elevator
            val items = currentPlan.status[currentFloor]
            val up2 = items.flatMap { item -> items.filter { it != item }.map { setOf(it, item) } }
                    .map { carried -> carried to items - carried }
                    .filter { (_, left) -> left.compatible }.mapNotNull { (carried, left) ->
                        currentPlan.status.getOrNull(currentFloor + 1)?.let {
                            (it + carried).takeIf { it.compatible }?.let { new ->
                                Plan(currentFloor + 1, currentPlan.step + 1, currentPlan.status.mapIndexed { index, set ->
                                    when (index) {
                                        currentFloor -> left
                                        currentFloor + 1 -> new
                                        else -> set
                                    }
                                }, 3)
                            }
                        }
                    }.filter { it.hash !in seen }
            val up = items.map { setOf(it) }
                    .map { carried -> carried to items - carried }
                    .filter { (_, left) -> left.compatible }.mapNotNull { (carried, left) ->
                        currentPlan.status.getOrNull(currentFloor + 1)?.let {
                            (it + carried).takeIf { it.compatible }?.let { new ->
                                Plan(currentFloor + 1, currentPlan.step + 1, currentPlan.status.mapIndexed { index, set ->
                                    when (index) {
                                        currentFloor -> left
                                        currentFloor + 1 -> new
                                        else -> set
                                    }
                                }, 2)
                            }
                        }
                    }.filter { it.hash !in seen }
            val down = items.map { setOf(it) }
                    .map { carried -> carried to items - carried }
                    .filter { (_, left) -> left.compatible }.mapNotNull { (carried, left) ->
                        currentPlan.status.getOrNull(currentFloor - 1)?.let {
                            (it + carried).takeIf { it.compatible }?.let { new ->
                                Plan(currentFloor - 1, currentPlan.step + 1, currentPlan.status.mapIndexed { index, set ->
                                    when (index) {
                                        currentFloor -> left
                                        currentFloor - 1 -> new
                                        else -> set
                                    }
                                }, 1)
                            }
                        }
                    }.filter { it.hash !in seen }
            val down2 = items.flatMap { item -> items.filter { it != item }.map { setOf(it, item) } }
                    .map { carried -> carried to items - carried }
                    .filter { (_, left) -> left.compatible }.mapNotNull { (carried, left) ->
                        currentPlan.status.getOrNull(currentFloor - 1)?.let {
                            (it + carried).takeIf { it.compatible }?.let { new ->
                                Plan(currentFloor - 1, currentPlan.step + 1, currentPlan.status.mapIndexed { index, set ->
                                    when (index) {
                                        currentFloor -> left
                                        currentFloor - 1 -> new
                                        else -> set
                                    }
                                }, 0)
                            }
                        }
                    }.filter { it.hash !in seen }

            toVisit.addAll(up)
            toVisit.addAll(up2)
            down.takeIf { (0 until currentFloor).any { currentPlan.status[it].isNotEmpty() } }?.let { toVisit.addAll(it) }
            down2.takeIf { (0 until currentFloor).any { currentPlan.status[it].isNotEmpty() } }?.let { toVisit.addAll(it) }
        }
    }
    return Int.MAX_VALUE
}

