package com.sorbonneuniversite.stl.daar2022.searchengine.algorithms.automate

/**
 * this class represent NFA automate
 */
class NFA(
    val root: NFAState, val accepting: NFAState, val inputSymbols: Set<Int>
) {

    override fun toString(): String {
        return root.toString()
    }

    companion object {
        // 256 ASCII chars
        private const val COL = 256
        fun fromRegExTreeToNFA(ret: RegExTree?): NFA {
            if (ret!!.subTrees.isEmpty()) {
                val startState = NFAState()
                val finalState = NFAState()
                val inputSymbols: MutableSet<Int> = HashSet()
                if (ret.root != RegExTree.DOT) {
                    // only 1 transition
                    startState.addTransition(ret.root, finalState)
                    inputSymbols.add(ret.root)
                } else {
                    for (i in 0 until COL) {
                        startState.addTransition(i, finalState)
                        inputSymbols.add(i)
                    }
                }
                return NFA(startState, finalState, inputSymbols)
            }
            if (ret.root == RegExTree.CONCAT) {
                val left = fromRegExTreeToNFA(ret.subTrees[0])
                val right = fromRegExTreeToNFA(ret.subTrees[1])
                left.accepting.addTransition(right.root)
                val inputSymbols: MutableSet<Int> = HashSet()
                inputSymbols.addAll(left.inputSymbols)
                inputSymbols.addAll(right.inputSymbols)
                return NFA(left.root, right.accepting, inputSymbols)
            }
            if (ret.root == RegExTree.ALTERN) {
                val startState = NFAState()
                val left = fromRegExTreeToNFA(ret.subTrees[0])
                val right = fromRegExTreeToNFA(ret.subTrees[1])
                val inputSymbols: MutableSet<Int> = HashSet()
                inputSymbols.addAll(left.inputSymbols)
                inputSymbols.addAll(right.inputSymbols)
                val endState = NFAState()
                startState.addTransition(left.root)
                startState.addTransition(right.root)
                left.accepting.addTransition(endState)
                right.accepting.addTransition(endState)
                return NFA(startState, endState, inputSymbols)
            }
            if (ret.root == RegExTree.ETOILE) {
                val startState = NFAState()
                val left = fromRegExTreeToNFA(ret.subTrees[0])
                val endState = NFAState()
                startState.addTransition(left.root)
                startState.addTransition(endState)
                left.accepting.addTransition(left.root)
                left.accepting.addTransition(endState)
                val inputSymbols: Set<Int> = HashSet(left.inputSymbols)
                return NFA(startState, endState, inputSymbols)
            }
            return NFA(NFAState(), NFAState(), HashSet())
        }
    }
}