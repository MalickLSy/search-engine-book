package com.sorbonne.daar.indexation

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.sorbonne.daar.models.Book
import com.sorbonne.daar.utils.JsonFormatter
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream


object BookMetadata {

    val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    const val GutendexBaseUrl = "https://gutendex.com/"
    val httpClient = RestTemplate()
    var count = 0

    fun download() {
        val books = HashMap<Int, Book>()
        val folder = File("data")
        println(folder.toString())
        val bookIds = folder.listFiles().map { it.name.replace(".txt", "").toInt() }

        bookIds.forEach { id ->
            var title: String?
            var authors: List<String?>?
            var content: String? = ""
            var image = ""
            try {
                val b = httpClient.getForObject(GutendexBaseUrl + "books/" + id, String::class.java)
                val jo = gson.fromJson(b, JsonObject::class.java)
                title = JsonFormatter.getBookTitle(jo)
                authors = JsonFormatter.getBookAuthors(jo)
                content = JsonFormatter.getBookHtmlContentURL(jo)
                image = JsonFormatter.getBookImageURL(jo)!!
                if (image === "NULL") {
                    println("************$id")
                    count++
                }
                val book = Book(id, title, authors, content, image)
                books[id] = book
            } catch (e: HttpClientErrorException) {
                System.err.println("not found")
            }
        }

        println(count)
        println("saving books.ser file")
        val oos = ObjectOutputStream(FileOutputStream("books.maps"))
        oos.writeObject(books)
        oos.flush()
        oos.close()
    }
}