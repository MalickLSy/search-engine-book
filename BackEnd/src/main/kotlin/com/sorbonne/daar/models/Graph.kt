package com.sorbonne.daar.models

import java.io.Serializable


class Graph() : Serializable {
    val neighbours: Map<Int, ArrayList<Int>> = HashMap()
    override fun toString(): String {
        return "Graph(neighbours=${neighbours.values.toString()})"
    }


}

