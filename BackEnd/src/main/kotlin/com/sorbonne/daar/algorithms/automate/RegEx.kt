package com.sorbonne.daar.algorithms.automate

/**
 * this class aLow to represent REGEX using SYNTAX TREE
 */
object RegEx {
    val regEx = null
    fun parse(regEx: String): RegExTree? {
        val result = ArrayList<RegExTree>()
        for (element in regEx) result.add(RegExTree(charToRoot(element), ArrayList()))
        return parse(result)
    }

    private fun charToRoot(c: Char): Int {
        if (c == '.') return RegExTree.DOT
        if (c == '*') return RegExTree.ETOILE
        if (c == '|') return RegExTree.ALTERN
        if (c == '(') return RegExTree.PARENTHESEOUVRANT
        return if (c == ')') RegExTree.PARENTHESEFERMANT else c.code
    }

    @Throws(Exception::class)
    private fun parse(p_result: ArrayList<RegExTree>): RegExTree? {
        var result = p_result
        while (containParenthesis(result)) result = processParenthesis(result)
        while (containEtoile(result)) result = processEtoile(result)
        while (containConcat(result)) result = processConcat(result)
        while (containAlter(result)) result = processAlter(result)
        if (result.size > 1) throw Exception()
        return removeProtection(result[0])
    }

    private fun containParenthesis(trees: ArrayList<RegExTree>): Boolean {
        for (t in trees) if (t.root == RegExTree.PARENTHESEFERMANT || t.root == RegExTree.PARENTHESEOUVRANT) return true
        return false
    }

    @Throws(Exception::class)
    private fun processParenthesis(trees: ArrayList<RegExTree>): ArrayList<RegExTree> {
        val result = ArrayList<RegExTree>()
        var found = false
        for (t in trees) {
            if (!found && t.root == RegExTree.PARENTHESEFERMANT) {
                var done = false
                val content = ArrayList<RegExTree>()
                while (!done && !result.isEmpty()) if (result[result.size - 1].root == RegExTree.PARENTHESEOUVRANT) {
                    done = true
                    result.removeAt(result.size - 1)
                } else content.add(0, result.removeAt(result.size - 1))
                if (!done) throw Exception()
                found = true
                val subTrees = ArrayList<RegExTree?>()
                subTrees.add(parse(content))
                result.add(RegExTree(RegExTree.PROTECTION, subTrees))
            } else {
                result.add(t)
            }
        }
        if (!found) throw Exception()
        return result
    }

    private fun containEtoile(trees: ArrayList<RegExTree>): Boolean {
        for (t in trees) if (t.root == RegExTree.ETOILE && t.subTrees.isEmpty()) return true
        return false
    }

    @Throws(Exception::class)
    private fun processEtoile(trees: ArrayList<RegExTree>): ArrayList<RegExTree> {
        val result = ArrayList<RegExTree>()
        var found = false
        for (t in trees) {
            if (!found && t.root == RegExTree.ETOILE && t.subTrees.isEmpty()) {
                if (result.isEmpty()) throw Exception()
                found = true
                val last = result.removeAt(result.size - 1)
                val subTrees = ArrayList<RegExTree?>()
                subTrees.add(last)
                result.add(RegExTree(RegExTree.ETOILE, subTrees))
            } else {
                result.add(t)
            }
        }
        return result
    }

    private fun containConcat(trees: ArrayList<RegExTree>): Boolean {
        var firstFound = false
        for (t in trees) {
            if (!firstFound && t.root != RegExTree.ALTERN) {
                firstFound = true
                continue
            }
            if (firstFound) firstFound = if (t.root != RegExTree.ALTERN) return true else false
        }
        return false
    }

    @Throws(Exception::class)
    private fun processConcat(trees: ArrayList<RegExTree>): ArrayList<RegExTree> {
        val result = ArrayList<RegExTree>()
        var found = false
        var firstFound = false
        for (t in trees) {
            if (!found && !firstFound && t.root != RegExTree.ALTERN) {
                firstFound = true
                result.add(t)
                continue
            }
            if (!found && firstFound && t.root == RegExTree.ALTERN) {
                firstFound = false
                result.add(t)
                continue
            }
            if (!found && firstFound && t.root != RegExTree.ALTERN) {
                found = true
                val last = result.removeAt(result.size - 1)
                val subTrees = ArrayList<RegExTree?>()
                subTrees.add(last)
                subTrees.add(t)
                result.add(RegExTree(RegExTree.CONCAT, subTrees))
            } else {
                result.add(t)
            }
        }
        return result
    }

    private fun containAlter(trees: ArrayList<RegExTree>): Boolean {
        for (t in trees) if (t.root == RegExTree.ALTERN && t.subTrees.isEmpty()) return true
        return false
    }

    @Throws(Exception::class)
    private fun processAlter(trees: ArrayList<RegExTree>): ArrayList<RegExTree> {
        val result = ArrayList<RegExTree>()
        var found = false
        var gauche: RegExTree? = null
        var done = false
        for (t in trees) {
            if (!found && t.root == RegExTree.ALTERN && t.subTrees.isEmpty()) {
                if (result.isEmpty()) throw Exception()
                found = true
                gauche = result.removeAt(result.size - 1)
                continue
            }
            if (found && !done) {
                if (gauche == null) throw Exception()
                done = true
                val subTrees = ArrayList<RegExTree?>()
                subTrees.add(gauche)
                subTrees.add(t)
                result.add(RegExTree(RegExTree.ALTERN, subTrees))
            } else {
                result.add(t)
            }
        }
        return result
    }

    @Throws(Exception::class)
    private fun removeProtection(tree: RegExTree?): RegExTree? {
        if (tree!!.root == RegExTree.PROTECTION && tree.subTrees.size != 1) throw Exception()
        if (tree.subTrees.isEmpty()) return tree
        if (tree.root == RegExTree.PROTECTION) return removeProtection(tree.subTrees[0])
        val subTrees = ArrayList<RegExTree?>()
        for (t in tree.subTrees) subTrees.add(removeProtection(t))
        return RegExTree(tree.root, subTrees)
    }
}