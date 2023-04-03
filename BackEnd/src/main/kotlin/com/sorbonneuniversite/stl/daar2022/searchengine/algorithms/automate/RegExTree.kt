package com.sorbonneuniversite.stl.daar2022.searchengine.algorithms.automate

import java.util.*
import kotlin.collections.ArrayList

/**
 * FROM TREE TO PARENTHESIS
 */
class RegExTree(var root: Int, var subTrees: ArrayList<RegExTree?>) {
    override fun toString(): String {
        if (subTrees.isEmpty()) return rootToString()
        var result = rootToString() + "(" + subTrees[0].toString()
        for (i in 1 until subTrees.size) result += "," + subTrees[i].toString()
        return "$result)"
    }

    private fun rootToString(): String {
        if (root == RegExTree.CONCAT) return "."
        if (root == RegExTree.ETOILE) return "*"
        if (root == RegExTree.ALTERN) return "|"
        return if (root == RegExTree.DOT) "." else root.toChar().toString()
    }

    companion object {
        const val CONCAT = 0xC04CA7
        const val ETOILE = 0xE7011E
        const val ALTERN = 0xA17E54
        const val PROTECTION = 0xBADDAD
        const val PARENTHESEOUVRANT = 0x16641664
        const val PARENTHESEFERMANT = 0x51515151
        const val DOT = 0xD07
    }

}