package com.sorbonne.daar.indexation

import com.sorbonne.daar.SearchEngineApplication.Companion.neighbors
import com.sorbonne.daar.models.Graph
import com.sorbonne.daar.models.JaccardMatrice
import java.io.*


object NeighborIndex {


    fun recommendations(bookIds: Set<Int>): Set<Int> {

        val suggestions: MutableSet<Int> = HashSet()
        for (book in bookIds) {
            val closeNeighbors = neighbors[book]!!
            for (sug in closeNeighbors) {
                if (!bookIds.contains(sug)) suggestions.add(sug)
            }
        }
        return suggestions
    }

    fun createVertexForAllIndexBooks(constantJacquard: Float) {
        val ois = ObjectInputStream(FileInputStream("jaccard.maps"))
        val jArray = (ois.readObject() as JaccardMatrice).jaccardMatrice
        ois.close()
        val graph = Graph()
        val folder = File("data")
        val bookIds = folder.listFiles().map { it.name.replace(".txt", "").toInt() }

        for (i in bookIds.indices) {
            val nb = arrayListOf<Int>()
            for (j in bookIds.indices) {
                val distance = jArray[i][j]
                if (distance != null) {
                    if (distance < constantJacquard) {
                        nb.add(bookIds[j])
                    }
                }
            }
            graph.neighbours[i]?.addAll(nb)
        }
        println(graph)
        val oos = ObjectOutputStream(FileOutputStream("neighbors.maps"))
        oos.writeObject(graph)
        oos.flush()
        oos.close()
    }
}