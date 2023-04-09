package com.sorbonne.daar.indexation

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.sorbonne.daar.utils.JsonFormatter
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream

object TitleIndexator {
    fun makeTitleIndex() {
        val titles = HashMap<String, Int>()
        val authors = HashMap<String, ArrayList<Int>>()
        val gson = GsonBuilder().setPrettyPrinting().create()
        val Gutenberg = "https://gutendex.com/"
        val rt = RestTemplate()
        val folder = File("data")
        println(folder.toString())
        var cpt = 1
        for (indexBook in folder.listFiles()) {
            if (cpt % 5 == 0) println("In Progress : " + cpt + "/" + folder.listFiles().size)
            cpt++
            var title = ""
            try {
                val id = indexBook.name.replace(".txt", "").toInt()
                // First we get the json content for the book with the id i
                val book = rt.getForObject(Gutenberg + "books/" + id, String::class.java)
                val jo = gson.fromJson(book, JsonObject::class.java)

                // We get the title of the book with the id i
                title = JsonFormatter.getBookTitle(jo).toString()
                titles[title] = id
                println("**" + titles[title].toString())
                val author = JsonFormatter.getBookAuthors(jo)
                for (a in author) {
                    val l = authors[a]
                    if (l == null) {
                        val x = ArrayList<Int>()
                        x.add(id)
                        authors[a] = x
                    } else {
                        l.add(id)
                    }
                    println("--" + authors[a].toString())
                }
            } catch (e: HttpClientErrorException) {
                System.err.println("not found")
            }
        }
        println("saving titles.ser file")
        var oos = ObjectOutputStream(FileOutputStream("titles.maps"))
        oos.writeObject(titles)
        oos.flush()
        oos.close()
        println("saving authors.ser file")
        oos = ObjectOutputStream(FileOutputStream("authors.maps"))
        oos.writeObject(authors)
        oos.flush()
        oos.close()
    }
}

