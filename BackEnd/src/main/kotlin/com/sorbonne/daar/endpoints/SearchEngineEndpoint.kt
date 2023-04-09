package com.sorbonne.daar.endpoints

import com.sorbonne.daar.algorithms.search.BookResearcher
import com.sorbonne.daar.indexation.KeyWordsExtractor
import com.sorbonne.daar.indexation.NeighborIndex
import com.sorbonne.daar.models.SearchResponse
import mu.KLogging
import org.apache.camel.Exchange
import org.apache.camel.LoggingLevel.INFO
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.rest.RestBindingMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*


@Component
class SearchEngineEndpoint : RouteBuilder() {

    companion object : KLogging()

    @Value("\${netty.server.port}")
    private val serverPort: String? = null

    @Autowired
    private val bookResearcher = BookResearcher()


    override fun configure() {

        restConfiguration()
            .component("netty-http")
            .host("0.0.0.0")
            .port(serverPort)
            .bindingMode(RestBindingMode.auto)

        rest("/searchbywords")
            .get("?words={words}")
            .outType(SearchResponse::class.java)
            .to("vm:searchbywords")

        rest("/searchbyregex")
            .get("?regex={regex}")
            .outType(SearchResponse::class.java)
            .to("vm:searchbyregex")

        rest("/searchbytitle")
            .get("?title={title}")
            .outType(SearchResponse::class.java)
            .to("vm:searchbytitle")

        rest("/searchbyauthor")
            .get("?author={author}")
            .outType(SearchResponse::class.java)
            .to("vm:searchbyauthor")


        from("direct:error")
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
            .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
            .setBody(constant("Bad search request"))

        from("vm:searchbywords")
            .choice()
            .`when`(header("words").isNotNull)
            .process { e ->
                val content = e.`in`.getHeader("words") as String
                logger.info("Searching  by words  : $content")
                val keywords: List<String> = content.split("\\s+")
                val ids: MutableSet<Int> = LinkedHashSet()
                keywords.forEach { word ->
                    KeyWordsExtractor.stem(word)
                        ?.let { it1 -> ids.addAll(bookResearcher.findByKeyWords(it1.lowercase())) }
                }
                val idsAsList = ArrayList(ids)
                bookResearcher.ranking(idsAsList)
                val books = bookResearcher.retrieveBookFromIdsList(idsAsList)
                val suggestions =
                    bookResearcher.retrieveBookFromIdsList(NeighborIndex.recommendations(idsAsList.toSet()).toList())
                e.`in`.body = SearchResponse(books, suggestions)
            }
            .otherwise()
            .to("direct:error")

        from("vm:searchbyregex")
            .log(INFO, log, "Received search by regex(\"regex\")")
            .choice()
            .`when`(header("regex").isNotNull)
            .log(INFO, log, "Received search by regex(\"regex\")")
            .process { e ->
                val regex = e.`in`.getHeader("regex") as String
                logger.info("Searching  by regex  : $regex")
                val keywords: List<String> = regex.split("\\s+")
                val ids: MutableSet<Int> = LinkedHashSet()
                keywords.forEach { userMotif ->
                    ids.addAll(bookResearcher.advancedresearch(userMotif.lowercase()))
                }
                val idsAsList = ArrayList(ids)
                bookResearcher.ranking(idsAsList)
                val books = bookResearcher.retrieveBookFromIdsList(idsAsList)
                val suggestions =
                    bookResearcher.retrieveBookFromIdsList(NeighborIndex.recommendations(idsAsList.toSet()).toList())
                e.`in`.body = SearchResponse(books, suggestions)
            }
            .otherwise()
            .to("direct:error")

        from("vm:searchbytitle")
            .choice()
            .`when`(header("title").isNotNull)
            .process { e ->
                val title = e.`in`.getHeader("title") as String
                logger.info("Searching for title : $title")
                val keywords: List<String> = title.split("\\s+")
                val ids: MutableSet<Int> = HashSet()
                keywords.forEach { k -> ids.addAll(bookResearcher.findByTitle(k.lowercase())) }
                val idsAsList: List<Int> = ArrayList(ids)
                bookResearcher.ranking(idsAsList)
                val books = bookResearcher.retrieveBookFromIdsList(idsAsList)
                val suggestions =
                    bookResearcher.retrieveBookFromIdsList(NeighborIndex.recommendations(idsAsList.toSet()).toList())
                e.`in`.body = SearchResponse(books, suggestions)
            }
            .otherwise()
            .to("direct:error")


        from("vm:searchbyauthor")
            .choice()
            .`when`(header("author").isNotNull)
            .process { e ->
                val author = e.`in`.getHeader("author") as String
                logger.info("Searching for author : $author")
                val keywords = author.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val ids: MutableSet<Int> = HashSet()
                keywords.forEach { k -> ids.addAll(bookResearcher.finByAuthor(k.lowercase())) }
                val idsAsList: List<Int> = ArrayList(ids)
                bookResearcher.ranking(idsAsList)
                val books = bookResearcher.retrieveBookFromIdsList(idsAsList)
                val suggestions =
                    bookResearcher.retrieveBookFromIdsList(NeighborIndex.recommendations(idsAsList.toSet()).toList())
                e.`in`.body = SearchResponse(books, suggestions)
            }
            .otherwise()
            .to("direct:error")

    }


}