package com.sorbonne.daar.algorithms.automate

import java.util.*

class DFA(val root: DFAState, val acceptings: Set<DFAState?>) {

    override fun toString(): String {
        return root.toString()
    }

    companion object {
        private var allDFAStates: MutableSet<DFAState> = HashSet()

        /**
         * convert NFA to DFA
         * @param nfa is instance of NFA
         * @return an instance of DFA, removing all epsilon transition
         */
        fun fromNFAtoDFA(nfa: NFA): DFA {
            val inputSymbols = nfa.inputSymbols
            val root = nfa.root
            val accepting = nfa.accepting
            val start = DFAState(root.epsilonClosure())
            val accepts: MutableSet<DFAState?> = HashSet()
            allDFAStates.add(start)
            val queue: Queue<DFAState> = LinkedList()
            queue.offer(start)
            while (!queue.isEmpty()) {
                val current = queue.poll()
                for (input in inputSymbols) {
                    val set = getNextSubStates(current, input)
                    if (set.isNotEmpty()) {
                        val next = DFAState(set)
                        if (!allDFAStates.add(next)) {
                            for (same in allDFAStates) {
                                if (same == next) {
                                    current.addTransition(input, same)
                                    break
                                }
                            }
                            DFAState.counter--
                        } else {
                            if (next.subset?.contains(accepting) == true) {
                                accepts.add(next)
                            }
                            queue.offer(next)
                            current.addTransition(input, next)
                        }
                    }
                }
            }
            val wordlist: Queue<MutableSet<DFAState?>> = LinkedList()
            val union1: MutableSet<DFAState?> = HashSet()
            for (state in allDFAStates) {
                if (!accepts.contains(state)) {
                    union1.add(state)
                }
            }
            wordlist.add(union1)
            wordlist.add(accepts)
            while (!wordlist.isEmpty()) {
                val current = wordlist.poll()
                val union2: MutableSet<DFAState?> = HashSet()
                for (input in inputSymbols) {
                    val inUnion: Set<DFAState?> = HashSet(current)
                    val tmp: MutableSet<DFAState?> = HashSet()
                    for (st in current) {
                        if (!inUnion.contains(st!!.getTransition(input))) {
                            union2.add(st)
                            tmp.add(st)
                        }
                    }
                    current.removeAll(tmp)
                }
                if (union2.size > 1) {
                    wordlist.add(union2)
                }
                if (current.size < 2) {
                    break
                }
                for (state in allDFAStates) {
                    for (input in inputSymbols) {
                        val next = state.transitions[input] ?: continue
                        if (current.contains(next)) {
                            state.transitions.remove(input)
                            val copy: MutableSet<DFAState?> = HashSet(current)
                            copy.remove(next)
                            state.transitions[input] = copy.iterator().next()
                        }
                    }
                }
            }
            return DFA(start, accepts)
        }

        private fun getNextSubStates(state: DFAState, input: Int): Set<NFAState?> {
            val subset: MutableSet<NFAState?> = HashSet()
            val tmp: Set<NFAState> = NFAState.move(input, state.subset)
            for (nfaState in tmp) {
                subset.addAll(nfaState.epsilonClosure())
            }
            return subset
        }
    }
}