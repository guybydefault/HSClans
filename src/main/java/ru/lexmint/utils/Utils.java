package ru.lexmint.utils;

import ru.lexmint.domain.CPLayer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Class with some useful features.
 */
public class Utils {



    /**
     * Converts Set of given CPLayers' names to single String where elements of the set are separated
     * by comma.
     * @param set Set of CPLayers whose names are needed to be converted.
     * @return Single string with comma separated elements of the set.
     */
    public static String convertToString(Set<CPLayer> set) {
        StringBuilder result = new StringBuilder();
        Iterator<CPLayer> it = set.iterator();

        while(it.hasNext()) {
            result.append(it.next().getName());
            if (it.hasNext()) {
                result.append(',');
            }
        }

        return result.toString();
    }
}
