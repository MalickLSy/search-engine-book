package com.sorbonne.daar.indexation

import org.apache.commons.io.FileUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger


object Downloader {
    private val executorService: ExecutorService = ThreadPoolExecutor(
        1,
        1,
        1,
        TimeUnit.HOURS, LinkedBlockingQueue()
    )

    @Throws(Exception::class)
    fun download(number: Int) {
        println("Building Book Library: \n")
        println(buildBooksDatabase(number).size)
    }

    @Throws(Exception::class)
    fun buildBooksDatabase(nbBooks: Int): ArrayList<Int> {
        val listofBooksIds = ArrayList<Int>()
        var cpt = 1
        while (listofBooksIds.size < nbBooks) {
            listofBooksIds.addAll(auxBuildBooksDatabase(cpt, nbBooks))
            println("Progress(" + listofBooksIds.size + "/" + nbBooks + ")")
            cpt++
        }
        executorService.shutdown()
        executorService.awaitTermination(1, TimeUnit.DAYS)
        return listofBooksIds
    }

    @Throws(Exception::class)
    private fun auxBuildBooksDatabase(page: Int, nbbooks: Int): ArrayList<Int> {
        val listofBooksIds = ArrayList<Int>()
        val url = URL("https://gutendex.com/books/?page=$page")
        val con: HttpURLConnection = url.openConnection() as HttpURLConnection
        con.requestMethod = "GET"
        con.setRequestProperty("Content-Type", "application/json")
        val `in` = BufferedReader(
            InputStreamReader(con.getInputStream())
        )
        var inputLine: String?
        val content = StringBuffer()
        while (`in`.readLine().also { inputLine = it } != null) {
            content.append(inputLine)
        }
        `in`.close()
        con.disconnect()
        val contentJson = JSONObject(content.toString())

        val jsonArray: JSONArray = contentJson.getJSONArray("results")
        for (i in 0 until jsonArray.length()) {
            val id = AtomicInteger(jsonArray.getJSONObject(i).getInt("id"))
            if (listofBooksIds.size >= nbbooks) {
                return listofBooksIds
            }
            // if (countWordsIdBook(id.toInt())) {
            //countWordsIdBook(id.toInt())
            listofBooksIds.add(id.toInt())
            executorService.submit {
                try {
                    downloadBook("https://www.gutenberg.org/files/$id/$id-0.txt", id.toInt())
                } catch (e: IOException) {
                   // e.printStackTrace()
                }
            }
            // }
        }
        return listofBooksIds
    }

    private fun countWordsIdBook(idbook: Int): Boolean {
        val urlbook = "https://www.gutenberg.org/files/$idbook/$idbook-0.txt"
        return try {
            val url = URL(urlbook)
            val con: HttpURLConnection = url.openConnection() as HttpURLConnection
            con.setRequestMethod("GET")
            con.setRequestProperty("Content-Type", "application/json")
            val `in` = BufferedReader(InputStreamReader(con.getInputStream()))
            var inputLine: String
            var comptemot = 0
            while (`in`.readLine().also { inputLine = it } != null) {
                comptemot += countWords(inputLine)
            }
            `in`.close()
            con.disconnect()
            comptemot >= 10000
        } catch (e: Exception) {
            false
        }
    }

    fun countWords(s: String): Int {
        val line = s.replace("\\p{Punct}".toRegex(), "")
        var wordCount = 0
        var word = false
        val endOfLine = line.length - 1
        for (i in 0 until line.length) {
            if (Character.isLetter(line[i]) && i != endOfLine) {
                word = true
            } else if (!Character.isLetter(line[i]) && word) {
                wordCount++
                word = false
            } else if (Character.isLetter(line[i]) && i == endOfLine) {
                wordCount++
            }
        }
        return wordCount
    }

    @Throws(IOException::class)
    fun downloadBook(urlbooks: String?, id: Int) {
        val theDir = File("data")
        if (!theDir.exists()) {
            theDir.mkdirs()
        }
        val dir: String = "data/" + id + ".txt"
        FileUtils.copyURLToFile(URL(urlbooks), File(dir))
    }


}
