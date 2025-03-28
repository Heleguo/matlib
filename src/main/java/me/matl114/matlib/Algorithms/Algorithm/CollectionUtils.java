package me.matl114.matlib.Algorithms.Algorithm;

import java.util.HashSet;
import java.util.Set;

public class CollectionUtils {
    public static <T extends Object> Set<T> intersection(Set<T> set1, Set<T> set2) {
        Set<T> result = new HashSet<>(set1);
        result.retainAll(set2);
        return result;
    }
}
