package com.sorbonneuniversite.stl.daar2022.searchengine.algorithms.automate


object RegexFactory {
    private fun parse(regex: String): RegExTree? {
       return if (regex.isEmpty()) {
            throw IllegalArgumentException("this regex is empty.")
        } else {
            try {
                 RegEx.parse(regex)
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid syntax for regEx")
            }
        }
    }
    fun compile(regex: String): RegexMatcher {
        try {
            val ret : RegExTree? = parse(regex)
            val nfa: NFA = NFA.fromRegExTreeToNFA(ret)
            val dfa: DFA = DFA.fromNFAtoDFA(nfa)
            return RegexMatcher(dfa.root,dfa.acceptings)
        }catch (e: Exception){
            println(e.stackTrace)
            throw Exception(e.cause)
        }
    }

}



