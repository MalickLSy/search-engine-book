package com.sorbonneuniversite.stl.daar2022.searchengine.algorithms.automate

class RegexMatcher(private val root:DFAState, private val acceptings : Set<DFAState?>){
    fun match (text : String): Boolean { return match(root,text,0 ) }

    private fun match(state: DFAState?, line: String, position: Int): Boolean {
        if (acceptings.contains(state)) return true
        if (position >= line.length) return false
        val input = line[position].code
        val next = state!!.getTransition(input) ?: return match(root, line, position + 1)
        return if (!match(next, line, position + 1)) match(root, line, position + 1) else true
    }
}