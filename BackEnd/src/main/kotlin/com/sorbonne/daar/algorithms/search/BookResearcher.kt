package com.sorbonne.daar.algorithms.search

import EgrepSearch
import com.sorbonne.daar.SearchEngineApplication
import com.sorbonne.daar.SearchEngineApplication.Companion.booksAuthors
import com.sorbonne.daar.SearchEngineApplication.Companion.books
import com.sorbonne.daar.SearchEngineApplication.Companion.closenessCentralityRank
import com.sorbonne.daar.SearchEngineApplication.Companion.bookkeywords
import com.sorbonne.daar.SearchEngineApplication.Companion.booksTitles
import com.sorbonne.daar.algorithms.automate.RegexFactory
import com.sorbonne.daar.algorithms.automate.RegexMatcher
import com.sorbonne.daar.models.Book
import org.springframework.stereotype.Component
import java.util.*


@Component
class BookResearcher {

    companion object : SearchEngineApplication()

    /**
     * Get all the books with a title containing a specific keyword
     */
    fun findByTitle(keyword: String?): List<Int> {
        val ids: MutableList<Int> = ArrayList()
        for (title in booksTitles.keys) {
            if (title.lowercase(Locale.getDefault()).contains(keyword!!)) {
                booksTitles[title]?.let { ids.add(it) }
            }
        }
        return ids
    }

    /**
     * Get all the books for a specific author
     */
    fun finByAuthor(keyword: String?): List<Int> {
        val ids: MutableList<Int> = ArrayList()
        for (author in booksAuthors.keys) {
            if (author.lowercase(Locale.getDefault()).contains(keyword!!)) {
                booksAuthors[author]?.let { ids.addAll(it) }
            }
        }
        return ids
    }

    /**
     * Get all the books containing a related keyword
     */
    fun findByKeyWords(keyword: String): Set<Int> {
        // We use a hashset to remove duplicates
        val ids: MutableSet<Int> = HashSet()
        for (kwFromDB in bookkeywords.keys) {
            if (kwFromDB.contains(keyword.lowercase(Locale.getDefault()))) {
                bookkeywords[kwFromDB]?.let { ids.addAll(it) }
            }
        }
        return ids
    }

    /**
     * Order the ids of a result Set based on the closeness graph
     */
    fun ranking(ids: List<Int?>?) {
        val orderedIndexes: List<Int> = ArrayList(closenessCentralityRank.keys)
        Collections.sort(ids, Comparator.comparing { id -> orderedIndexes.indexOf(id) })
    }

    /**
     * Get all the books using a specific keywords using Aho-Ullman or KMP
     */
    fun advancedresearch(regex: String): List<Int> {
        val regexFactory = RegexFactory
        var regEx = regex
        val matcher: RegexMatcher = regexFactory.compile(regex)
        val ids: MutableList<Int> = ArrayList()

        if (!EgrepSearch.isEREMotif(regex)) {
            return findByKeyWords(regex).toList()
        }
        //regExTree
        bookkeywords.keys.forEach { keyword ->
            if( matcher.match(keyword)){
                bookkeywords[keyword]?.let { ids.addAll(it) }
            }

        }
        return ids
    }

    /**
     * Get the data of the books in the ids list
     */

    /**
     * Get the data of the books in the ids list
     */
    fun retrieveBookFromIdsList(ids: List<Int?>): List<Book> {
        val result: MutableList<Book> = ArrayList<Book>()
        for (i in ids) {
            books[i!!]?.let { result.add(it) }
        }
        return result
    }
}

