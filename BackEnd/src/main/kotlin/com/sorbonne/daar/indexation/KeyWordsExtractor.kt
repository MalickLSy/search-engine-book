package com.sorbonne.daar.indexation

import com.sorbonne.daar.models.KeyWord
import org.apache.lucene.analysis.TokenStream
import org.apache.lucene.analysis.core.LowerCaseFilter
import org.apache.lucene.analysis.core.StopFilter
import org.apache.lucene.analysis.en.EnglishAnalyzer
import org.apache.lucene.analysis.en.PorterStemFilter
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter
import org.apache.lucene.analysis.standard.ClassicFilter
import org.apache.lucene.analysis.standard.ClassicTokenizer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.analysis.util.CharArraySet
import org.apache.lucene.util.Version
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.stream.Collectors


object KeyWordsExtractor {

    fun stem(term: String?): String? {
        var tokenStream: TokenStream? = null
        return try {

            // We tokenize the input
            tokenStream = ClassicTokenizer(Version.LUCENE_40, StringReader(term))
            // stem
            tokenStream = PorterStemFilter(tokenStream)

            // Add each token in a set, so that duplicates are removed
            val stems: MutableSet<String> = HashSet()
            val token: CharTermAttribute = tokenStream.getAttribute(CharTermAttribute::class.java)
            tokenStream.reset()
            while (tokenStream.incrementToken()) {
                stems.add(token.toString())
            }

            // if no stem or 2+ stems have been found, return null
            if (stems.size != 1) {
                return null
            }
            val stem = stems.iterator().next()
            // if the stem has non-alphanumerical chars, return null
            if (!stem.matches(Regex("[a-zA-Z0-9-]+"))) {
                null
            } else stem
        } finally {
            if (tokenStream != null) {
                tokenStream.close()
            }
        }
    }

    /**
     * Construire une liste de mots clés en utilisant des mots vides en anglais
     */
    @Throws(IOException::class)
    fun buildKeyWordsList(input: String, mostUsedKeywords: Long): List<KeyWord> {
        var input = input
        var tokenStream: TokenStream? = null
        return try {
            input = input.replace("-+".toRegex(), "-0")

            input = input.replace("[\\p{Punct}&&[^'-]]+".toRegex(), " ")

            input = input.replace("(?:'(?:[tdsm]|[vr]e|ll))+\\b".toRegex(), "")

            tokenStream = ClassicTokenizer(Version.LUCENE_40, StringReader(input))

            tokenStream = LowerCaseFilter(Version.LUCENE_40, tokenStream)

            tokenStream = ClassicFilter(tokenStream)

            tokenStream = ASCIIFoldingFilter(tokenStream)

            val s = Scanner(File("stopWords.txt"))
            val stopWordsList = ArrayList<String>()
            while (s.hasNextLine()) {
                stopWordsList.add(s.nextLine())
            }
            s.close()
            val cas = CharArraySet(Version.LUCENE_40, stopWordsList, true)
            val defaultSet: CharArraySet = EnglishAnalyzer.getDefaultStopSet()
            cas.addAll(defaultSet)
            tokenStream = StopFilter(Version.LUCENE_40, tokenStream, cas)
            var keywords: MutableList<KeyWord> = LinkedList<KeyWord>()
            val token: CharTermAttribute = tokenStream.getAttribute(CharTermAttribute::class.java)
            tokenStream.reset()
            while (tokenStream.incrementToken()) {
                val term: String = token.toString()
                val stem = stem(term)
                if (stem != null) {
                    val keyword: KeyWord = find(keywords, KeyWord(stem.replace("-0".toRegex(), "-")))
                    keyword.add(term.replace("-0".toRegex(), "-"))
                }
            }

            keywords.sort()
            keywords = keywords.stream().limit(mostUsedKeywords).collect(Collectors.toList<Any>()) as MutableList<KeyWord>
            keywords
        } finally {
            if (tokenStream != null) {
                tokenStream.close()
            }
        }
    }

    fun <T> find(collection: MutableCollection<T>, example: T): T {
        for (element in collection) {
            if (element == example) {
                return element
            }
        }
        collection.add(example)
        return example
    }
    fun makeKeywordsIndex(mostUsedKeywords: Long) {
        val mcm = KeyWordMapIndex()
        val mcMap: HashMap<String, MutableList<Int>> = mcm.motCleMap
        val folder = File("data")
        folder.listFiles().map { it.name.replace(".txt", "").toInt() }.forEach { id ->
            val text: String = JaccardBUilder.readFile("data", id, StandardCharsets.US_ASCII)
            val mcList: List<KeyWord> = buildKeyWordsList(text,mostUsedKeywords)
            for (stem in mcList) {
                var ids = mcMap[stem.racine]
                if (ids != null) {
                    ids.add(id)
                } else {
                    ids = ArrayList()
                    ids.add(id)
                    mcMap[stem.racine] = ids
                }
            }
            if (id % 100 == 0) println("current book : $id")
        }
        println("end, saving the file")
        val oos = ObjectOutputStream(FileOutputStream("keywords.maps"))
        oos.writeObject(mcm)
        oos.flush()
        oos.close()
    }

}