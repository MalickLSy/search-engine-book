package com.sorbonne.daar.models

import java.io.Serializable
import java.util.*


class KeyWord(
    /** Racine du mot  */
    val racine: String
) : Comparable<KeyWord>, Serializable {

    /** Tous les mots ayant cette racine  */
    private val mots: MutableSet<String> = HashSet()

    /** Nombre de fois où le mot apparait  */
    var frequence = 0
        private set

    /**
     * Ajoute un mot à la liste
     * @param term
     */
    fun add(term: String) {
        mots.add(term)
        frequence++
    }

    /**
     * On classe les mots du set selon leur fréquence dans le texte
     */
    override fun compareTo(other: KeyWord): Int {
        return Integer.valueOf(other.frequence).compareTo(frequence)
    }

    /**
     * deux mots sont considérés égaux s'ils ont la même racine
     */
    override fun equals(obj: Any?): Boolean {
        return if (this === obj) {
            true
        } else if (obj !is KeyWord) {
            false
        } else {
            racine == obj.racine
        }
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(arrayOf<Any>(racine))
    }

    fun getMots(): Set<String> {
        return mots
    }

    override fun toString(): String {
        return "MotCle [racine = $racine, frequence = $frequence]"
    }

    companion object {
        private const val serialVersionUID = -212487834992453643L
    }
}
