package com.sorbonne.daar

import com.sorbonne.daar.indexation.KeyWordMapIndex
import com.sorbonne.daar.models.Book
import com.sorbonne.daar.models.Graph
import mu.KLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.FileInputStream
import java.io.ObjectInputStream

@SpringBootApplication
class SearchEngineApplication {
    companion object : KLogging() {
        lateinit var books: HashMap<Int, Book>
        lateinit var bookkeywords: HashMap<String, MutableList<Int>>
        lateinit var booksAuthors: HashMap<String, List<Int>>
        lateinit var booksTitles: HashMap<String, Int>
        lateinit var closenessCentralityRank: HashMap<Int, Float>
        lateinit var neighbors:  Map<Int, ArrayList<Int>>


        fun setup() {

            var ois = ObjectInputStream(FileInputStream("keywords.maps"))
            val mcm = ois.readObject() as KeyWordMapIndex
            ois.close()

            ois = ObjectInputStream(FileInputStream("books.maps"))
            books = ois.readObject() as HashMap<Int, Book>
            println("${books.size} Books ")
            ois.close()

            bookkeywords = mcm.motCleMap
            logger.info(" ${bookkeywords.size} books Keywords")
            ois = ObjectInputStream(FileInputStream("titles.maps"))
            booksTitles = ois.readObject() as HashMap<String, Int>
            ois.close()
            logger.info("${booksTitles.size} Titles")

            ois = ObjectInputStream(FileInputStream("authors.maps"))
            booksAuthors = ois.readObject() as HashMap<String, List<Int>>
            ois.close()
            logger.info("${booksAuthors.size} Authors")

            ois = ObjectInputStream(FileInputStream("closeness.maps"))
            closenessCentralityRank = ois.readObject() as HashMap<Int, Float>
            ois.close()
            logger.info("Closeness graph is calculated ")

            ois = ObjectInputStream(FileInputStream("neighbors.maps"))
            neighbors = (ois.readObject() as Graph).neighbours
            ois.close()
            logger.info("${neighbors.size} Neighbors")

        }
    }

}


fun main(args: Array<String>) {
    SearchEngineApplication.setup()
    runApplication<SearchEngineApplication>(*args)
}
