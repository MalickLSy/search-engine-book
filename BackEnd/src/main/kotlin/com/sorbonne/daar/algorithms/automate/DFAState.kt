package com.sorbonne.daar.algorithms.automate

import java.util.*

class DFAState(val subset: Set<NFAState?>?) {
    private val id: Int = counter++
    private var isVisited: Boolean = false
    val transitions: MutableMap<Int?, DFAState?>

    init {
        isVisited = false
        transitions = HashMap()
    }

    fun addTransition(input: Int, next: DFAState?) {
        transitions[input] = next
    }

    fun getTransition(input: Int): DFAState? {
        return transitions[input]
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val dfaState = other as DFAState
        return subset == dfaState.subset
    }

    override fun hashCode(): Int {
        return Objects.hash(subset)
    }

    override fun toString(): String {
        return print(HashSet())!!
    }

    private fun printSubset(): String {
        val sb = StringBuilder()
        sb.append(id.toChar()).append(": ").append("{")
        for (state in subset!!) {
            sb.append(state?.id).append(", ")
        }
        sb.delete(sb.length - 2, sb.length)
        sb.append("}")
        return sb.toString()
    }

    private fun print(visited: HashSet<DFAState?>): String? {
        if (!visited.add(this)) return null
        val sb = StringBuilder()
        for ((key, state) in transitions) {
            sb.append(printSubset()).append(" -- ").append(key!!.toInt().toChar()).append(" --> ").append(
                state!!.printSubset()
            )
            sb.append("\n")
            val seq = state.print(visited)
            if (seq != null) {
                sb.append(seq)
            }
        }
        return sb.toString()
    }

    companion object {
        var counter = 65
    }
}