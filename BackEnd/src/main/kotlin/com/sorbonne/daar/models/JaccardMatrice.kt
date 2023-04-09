package com.sorbonne.daar.models

import java.io.Serializable

class JaccardMatrice : Serializable {

    companion object {
        private const val NUMBER_OF_BOOKS = 1664
        private const val serialVersionUID = 204749000403344832L
    }

    val jaccardMatrice: Array<Array<Float?>> = Array(NUMBER_OF_BOOKS) {
        arrayOfNulls(
            NUMBER_OF_BOOKS
        )
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (i in jaccardMatrice.indices) {
            sb.append("$i ")
            for (j in jaccardMatrice.indices) {
                if (jaccardMatrice[i][j] != null) {
                    sb.append(jaccardMatrice[i][j].toString() + " ")
                } else {
                    sb.append("?? ")
                }
            }
            sb.append("\n")
        }
        return sb.toString()
    }
}
