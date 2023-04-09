package com.sorbonne.daar.indexation

import java.io.Serializable


class KeyWordMapIndex : Serializable {

    /**
     *les clés sont la racine des mots-clés, les valeurs sont l'identifiant des livres
     */
    var motCleMap: HashMap<String, MutableList<Int>> = HashMap()

    override fun toString(): String {
        val sb = StringBuilder()
        for (stem in motCleMap.keys) {
            sb.append(stem + " -> " + motCleMap[stem]!!.size)
            sb.append("\n")
        }
        return sb.toString()
    }

    companion object {
        private const val serialVersionUID = -1667849664615407919L
    }
}
