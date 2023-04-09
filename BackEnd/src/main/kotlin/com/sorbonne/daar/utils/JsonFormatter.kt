package com.sorbonne.daar.utils

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.IOException
import java.util.function.Consumer

object JsonFormatter {

    /** Get the html text url of jo
     * @throws IOException
     */
    @Throws(IOException::class)
    fun getBookHtmlContentURL(jo: JsonObject): String? {
        var contentURL: JsonElement? = JsonObject()
        var el = jo.getAsJsonObject("formats")["text/html"]
        if (el != null) {
            contentURL = el
        } else if (jo.getAsJsonObject("formats")["text/html; charset=us-ascii"].also { el = it } != null) {
            contentURL = el
        } else if (jo.getAsJsonObject("formats")["text/html; charset=utf-8"].also { el = it } != null) {
            contentURL = el
        } else if (jo.getAsJsonObject("formats")["text/html; charset=iso-8859-1"].also { el = it } != null) {
            contentURL = el
        } else if (jo.getAsJsonObject("formats")["text/plain"].also { el = it } != null) {
            contentURL = el
        } else if (jo.getAsJsonObject("formats")["text/plain; charset=us-ascii"].also { el = it } != null) {
            contentURL = el
        } else if (jo.getAsJsonObject("formats")["text/plain; charset=utf-8"].also { el = it } != null) {
            contentURL = el
        } else {
            return "NULL"
        }
        //book/632 (has nothing ..)
        return contentURL!!.asString
    }

    /** Get the book title
     * @throws IOException
     */
    @Throws(IOException::class)
    fun getBookTitle(jo: JsonObject): String? {
        val el: JsonElement = jo.getAsJsonPrimitive("title")
        return el.asString
    }

    /** Get the book authors
     * @throws IOException
     */
    fun getBookAuthors(jo: JsonObject): ArrayList<String> {
        val bookAuthors = ArrayList<String>()
        val ar = jo.getAsJsonArray("authors")
        ar.forEach(Consumer { a: JsonElement ->
            bookAuthors.add(
                a.asJsonObject["name"].asString
            )
        })
        return bookAuthors
    }

    /** Get the book title
     * @throws IOException
     */
    fun getBookImageURL(jo: JsonObject): String? {
        var imageURL: JsonElement = JsonObject()
        val el = jo.getAsJsonObject("formats")["image/jpeg"]
        imageURL = (el ?: return "NULL")
        return imageURL.asString
    }


}