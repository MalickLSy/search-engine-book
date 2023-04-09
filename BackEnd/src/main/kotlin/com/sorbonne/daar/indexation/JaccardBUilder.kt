package com.sorbonne.daar.indexation

import com.google.common.collect.Sets
import com.sorbonne.daar.models.JaccardMatrice
import com.sorbonne.daar.utils.MapUtils
import java.io.*
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*


object JaccardBUilder {

    /**
     * Important : la fonction renvoie (1 - similarité) pour
     * montrer la distance entre les textes
     * -> Deux textes indentiques renvoient 0
     */
    fun distanceJaccard(str1: String?, str2: String?): Float {
        if (str1!!.trim { it <= ' ' }.isEmpty() || str1 == null || str2!!.trim { it <= ' ' }
                .isEmpty() || str2 == null) {
            return 1f
        }
        val s1: Set<String?> = HashSet(Arrays.asList(*str1.split(" +".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()))
        val s2: Set<String?> = HashSet(Arrays.asList(*str2.split(" +".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()))
        var intersection = 0
        // Better performances if we put the smaller set first
        intersection = if (s1.size < s2.size) {
            Sets.intersection(s1, s2).size
        } else {
            Sets.intersection(s2, s1).size
        }

        // Intersection / Union
        val similarity = intersection / (s1.size + s2.size - intersection).toFloat()
        // Distance is 1 - similarity
        return 1 - similarity
    }


    fun buildJaccardMatrice(constanteJaccard: Float) {

        val jm = JaccardMatrice()
        val jaccardArray = jm.jaccardMatrice

        val folder = File("data")
        val bookIds = folder.listFiles().map { it.name.replace(".txt", "").toInt() }

        for (i in bookIds.indices) {
            val book1 = readFile("data", bookIds[i], StandardCharsets.US_ASCII)
            for (j in bookIds.indices) {
                if (j < i) {
                    jaccardArray[i][j] = jaccardArray[j][i]
                } else if (i == j) {
                    jaccardArray[i][j] = 0f
                } else {
                    val book2 = readFile("data", bookIds[j], StandardCharsets.US_ASCII)
                    val distance = distanceJaccard(book1, book2)
                    if (distance < constanteJaccard) {
                        jaccardArray[i][j] = distance
                    }
                    if (j % 200 == 0) println("column : $j")
                }
            }

            println("current book : $bookIds[$i]")

            if (i % 50 == 0) {
                println("saving file")
                val oos = ObjectOutputStream(FileOutputStream("jaccard.maps"))
                oos.writeObject(jm)
                oos.flush()
                oos.close()
            }
        }
        println("test jArray origin size : " + jaccardArray.size)
        println("end, saving the file")
        val oos = ObjectOutputStream(FileOutputStream("jaccard.maps"))
        oos.writeObject(jm)
        oos.flush()
        oos.close()
    }

    /**
     * Lire le contenu d'un fichier
     */
    @Throws(IOException::class)
    fun readFile(path: String, i: Int, encoding: Charset?): String {
        val encoded = Files.readAllBytes(Paths.get(path + File.separator + i + ".txt"))
        return String(encoded, encoding!!)
    }

    /**
     * Construire un graphe géometrique , un arret existe si la distance entre 2 nœuds est <= regulator 0,5
     */
    fun buildClosenessCentrality() {
        val ois = ObjectInputStream(FileInputStream("jaccard.maps"))
        val jmIN = ois.readObject() as JaccardMatrice
        ois.close()
        val jArray = jmIN.jaccardMatrice
        var closeness = HashMap<Int?, Float?>()
        val folder = File("data")
        val bookIds = folder.listFiles().map { it.name.replace(".txt", "").toInt() }
        for (i in bookIds.indices) {
            var sum = 0f
            for (j in bookIds.indices) {
                val x = jArray[i][j]
                if (x != null) {
                    sum += x
                }
            }
            closeness[bookIds[i]] = 1 / sum
        }
        closeness = MapUtils.sortByValue(closeness) as HashMap<Int?, Float?>
        val oos = ObjectOutputStream(FileOutputStream("closeness.maps"))
        oos.writeObject(closeness)
        oos.flush()
        oos.close()
    }
}

