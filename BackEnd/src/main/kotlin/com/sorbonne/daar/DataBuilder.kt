package com.sorbonne.daar

import com.sorbonne.daar.indexation.*
import java.io.FileInputStream
import java.io.ObjectInputStream


fun main(args: Array<String>) {
    try {

        Downloader.download(20)

        BookMetadata.download()

        TitleIndexator.makeTitleIndex()

        KeyWordsExtractor.makeKeywordsIndex(50);

        val ois = ObjectInputStream(FileInputStream("keywords.maps"))
        val mcm = ois.readObject() as KeyWordMapIndex
        ois.close()
        val map: HashMap<String, MutableList<Int>> = mcm.motCleMap
        map.entries.removeIf { (key): Map.Entry<String, List<Int>> ->
            map[key]!!.size > 30
        }
        println(mcm.toString())

        JaccardBUilder.buildJaccardMatrice(60.0F)

        JaccardBUilder.buildClosenessCentrality();
        val ois2 = ObjectInputStream(FileInputStream("closeness.maps"))
        val mcm2 = ois2.readObject() as java.util.HashMap<Int, Float>
        ois2.close()

        for (i in mcm2.keys) {
            println(i.toString() + " -> " + mcm2[i])
        }

        NeighborIndex.createVertexForAllIndexBooks(60.0F)

    } catch (e: Exception) {
        e.printStackTrace()
    }
}