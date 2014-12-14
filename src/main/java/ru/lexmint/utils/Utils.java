package ru.lexmint.utils;

import java.util.Iterator;
import java.util.Set;

/**
 * Class with some useful features.
 */
public class Utils {
    /**
     * Converts Set of given CPLayers' names to single String where elements of the set are separated
     * by comma.
     *
     * @param set       Set of CPLayers whose names are needed to be converted.
     * @param spaces if it's true, after, there will be spaces.
     * @return Single string with comma separated elements of the set.
     */
    public static String convertToString(Set<String> set, boolean spaces) {
        StringBuilder result = new StringBuilder();
        Iterator<String> it = set.iterator();

        while (it.hasNext()) {
            result.append(it.next());
            if (it.hasNext()) {
                result.append(',');
                if (spaces) {
                    result.append(' ');
                }
            }
        }

        return result.toString();
    }
}
