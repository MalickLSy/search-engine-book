package com.sorbonneuniversite.stl.daar2022.searchengine.algorithms.kmp

import java.util.*

/**
 * Author Alphonse Daudet CHOKOSSA
 */
class KMPAlgorithm {
    fun search(pattern: String, text: String) : Int {
        val textLen = text.length
        val factorLen : Int = pattern.length
        val carryOver = computeCarryOverArray(pattern)
        var i = 0
        var j = 0;
        while(i<textLen){
            if(text[i]==pattern[j]){
                i++
                j++
                if(j==factorLen){
                    return (i-factorLen);
                }
            }else{
                i = (i - carryOver[j]);
                j = 0
            }
        }
        return -1;
    }
     fun computeCarryOverArray(pattern: String) : IntArray {
        val factorLen = pattern.length
        var carryOver = IntArray(factorLen + 1)
        carryOver[0] = -1
        carryOver[factorLen] = 0

        for (i in 1..(factorLen - 1)) {
            /* step 1 */
            carryOver[i] = longestPrefixSuffix(pattern.substring(0,i))

            /* step 2 */
            if (pattern[carryOver[i]] == pattern[i] && carryOver[carryOver[i]] == -1) {
                carryOver[i] = -1
            }
            /* step 3 */
            if(carryOver[i] < 0){
                continue;
            }
            if (pattern[carryOver[i]] == pattern[i] && carryOver[carryOver[i]] != -1) {
                carryOver[i] = carryOver[carryOver[i]]
            }
        }
         return carryOver;
    }

    fun longestPrefixSuffix(s: String): Int {
        val n = s.length
        if (n < 2) {
            return 0
        }
        var len = 0
        var i = 1
        while (i < n) {
            if (s[i] == s[len]) {
                ++len
                ++i
            } else {
                i = i - len + 1
                len = 0
            }
        }
        return len
    }
}
