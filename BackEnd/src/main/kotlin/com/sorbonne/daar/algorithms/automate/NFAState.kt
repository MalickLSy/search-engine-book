package com.sorbonne.daar.algorithms.automate

import java.util.*

class NFAState {
    val id: Int
    private val transitions: MutableMap<Int, NFAState>
    private val epsilonTransitions: MutableSet<NFAState>

    init {
        id = counter++
        transitions = HashMap()
        epsilonTransitions = HashSet()
    }

    /**
     * add a transition(input symbol, next NFAState) to this
     * @param input an input symbol in all the 256 ascii char
     * @param next the next NFAState that `this` will go to via the input
     */
    fun addTransition(input: Int, next: NFAState) {
        transitions[input] = next
    }

    /**
     * add an epsilon transition - no input, only the next state
     * @param next the NFAState which `this` goes to via an epsilon
     */
    fun addTransition(next: NFAState) {
        epsilonTransitions.add(next)
    }

    /**
     * get next state by the input symbol found in transitions
     * @param input an input symbol in all the 256 ascii char
     * @return a set NFAState
     */
    private fun getTransition(input: Int): NFAState? {
        return transitions[input]
    }

    private fun search(input: Int): Set<NFAState> {
        val stateViaInput = getTransition(input) ?: return HashSet()
        val res: MutableSet<NFAState> = HashSet()
        res.add(stateViaInput)
        res.addAll(stateViaInput.search(input))
        return res
    }

    /**
     * a set of NFAStates, which departing from `this` NFAState, does a Kleene Closure of epsilon, can get
     * @return a set of NFAStates
     */
    fun epsilonClosure(): Set<NFAState> {
        val res: MutableSet<NFAState> = HashSet(epsilonTransitions)
        val tmp: MutableSet<NFAState> = HashSet()
        for (next in res) {
            tmp.addAll(next.epsilonClosure())
        }
        res.add(this)
        res.addAll(tmp)
        return res
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val state = other as NFAState
        return id == state.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    override fun toString(): String {
        return print(HashSet())!!
    }

    private fun print(visited: HashSet<NFAState?>): String? {
        if (!visited.add(this)) return null
        val sb = StringBuilder()
        for ((key, state) in transitions) {
            // (char): convert input symbol to ascii char
            sb.append(id).append(" -- ").append(key.toChar()).append(" --> ").append(state.id)
            sb.append("\n")
            val seq = state.print(visited)
            if (seq != null) {
                sb.append(seq)
            }
        }

        // and epsilon transitions
        for (state in epsilonTransitions) {
            sb.append(id).append(" -- ").append("Ɛ").append(" --> ").append(state.id)
            sb.append("\n")
        }
        for (state in epsilonTransitions) {
            val seq = state.print(visited)
            if (seq != null) {
                sb.append(seq)
            }
        }
        return sb.toString()
    }

    /**
     * from a set of NFAStates, move via the input, get a new set of NFAStates
     * the subset makes a new DFAState
     * @param input an input symbol in all the 256 ascii char
     * @return a subset of NFAStates
     */
    companion object {
        var counter = 0
        fun move(input: Int, dfa: Set<NFAState?>?): Set<NFAState> {
            val subset: MutableSet<NFAState> = HashSet()
            for (nfaState in dfa!!) {
                subset.addAll(nfaState!!.search(input))
            }
            return subset
        }
    }
}