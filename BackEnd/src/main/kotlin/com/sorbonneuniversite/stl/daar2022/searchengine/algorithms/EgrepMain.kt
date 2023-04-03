import com.sorbonneuniversite.stl.daar2022.searchengine.algorithms.automate.RegexFactory
import com.sorbonneuniversite.stl.daar2022.searchengine.algorithms.automate.RegexMatcher
import com.sorbonneuniversite.stl.daar2022.searchengine.algorithms.kmp.KMPAlgorithm
import java.io.File
import kotlin.system.exitProcess

const val TEXT_IN_RED = "\u001B[31m"
const val TEXT_COLOR_RESET = "\u001b[0m"
const val EXIT_FAILURE = 1
const val ERE_MOTIF = "*|()"


fun isEREMotif(motif : String): Boolean {
   return motif.map {ERE_MOTIF.contains(it) }.contains(true)
}
fun main(args: Array<String>) {

    var beginTime = System.currentTimeMillis()

    if (args.size < 2) {
        println("Invalid argument, can you follow this syntax: \"RegEx/Factor\" \"filename\" ?")
        exitProcess(EXIT_FAILURE)
    }
    val userMotif = args[0]
    val fileName : String = args[1]
    var line : Int = 0
    var resultNumber = 0
    var characters = 0
    val sb = StringBuilder()

    /**
     * For been able to know if I need automate or not
     */
    if(isEREMotif(userMotif)){
        try {
            val regexFactory  = RegexFactory
            val matcher : RegexMatcher = regexFactory.compile(userMotif)
            File(fileName).forEachLine { it.apply {
                line++;
                characters+=this.length
                matcher.match(this).apply {
                    sb.append( if (this) {resultNumber++; "$line - $it\n" } else "");
                }
            }}
            var endTime = System.currentTimeMillis()
            println("===============================REGEX===================================")
            println(sb.toString())
            println("$resultNumber lines matched !")
            println("Elapsed time ： " + (endTime - beginTime) + "ms for $characters characters")
        }catch (e : Exception){
            println(e.localizedMessage)
            exitProcess(EXIT_FAILURE)
        }

    }else{
        val kmp  = KMPAlgorithm()
        File(fileName).forEachLine { it.apply {
            line++;
            characters+=this.length
            var result : Int= kmp.search(userMotif,this)
            sb.append( if (result!= -1) {
                resultNumber ++
                "$line : $result - ${this.substring(0,result)}$TEXT_IN_RED${this.substring(result,result+ userMotif.length)}$TEXT_COLOR_RESET${this.substring(result+ userMotif.length)}\n"
            } else "");
        }}
        println("=====================================KMP====================================")
        println(sb.toString())
        println("$resultNumber lines found !")
        var endTime = System.currentTimeMillis()
        println("Elapsed time is： " + (endTime - beginTime) + "ms for $characters characters")
    }
}