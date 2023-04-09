package com.sorbonne.daar.utils;

import java.util.*;
import java.util.Map.Entry;

/**
 * Utilisé pour ordonner la map de proximité en valeur décroissante. Les livres intéressants
 * ont une proximité plus élevée, donc leur rang doit être plus élevé.
 */
public class MapUtils {
    public static Map<Integer, Float> sortByValue(Map<Integer, Float> map) {
        List<Entry<Integer, Float>> list = new ArrayList<>(map.entrySet());
        // Order by value, lower index first
        list.sort(Entry.comparingByValue());
        // We reverse the order so the higher closeness books are first
        Collections.reverse(list);
        Map<Integer, Float> result = new LinkedHashMap<>();
        for (Entry<Integer, Float> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}