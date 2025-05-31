package me.matl114.matlib.algorithms.algorithm;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;

public class CollectionUtils {
    public static <T extends Object> Set<T> intersection(Set<T> set1, Set<T> set2) {
        Set<T> result = new HashSet<>(set1);
        result.retainAll(set2);
        return result;
    }

    public static <K,V> Map<K,V> mapToNullsFromSet(Set<K> value){
        Map<K,V> map0 = new HashMap<>();
        for (K val: value){
            map0.put(val, null);
        }
        return map0;
    }

    public static <T,W> void mapAndSet(List<T> originList, Function<T,W> mapper, @Nonnull List<W> target){
        if(originList == null || originList.isEmpty()){
            target.clear();
        }else {
            int size= target.size();
            int len = originList.size();
            if(size > len){
                for (int i=0; i<len; ++i){
                    target.set(i, mapper.apply(originList.get(i)));
                }
                target.subList(len, size).clear();
            }else {
                for (int i=0; i<size; ++i){
                    target.set(i, mapper.apply(originList.get(i)));
                }
                for (int i= size; i<len;++i){
                    target.add(mapper.apply(originList.get(i)));
                }
            }
        }

    }
    public static void mergeMapNoCopy(Map<?,?> to, Map<?,?> from){
       for (var entry: from.entrySet()){
           Object val = to.get(entry.getKey());
           if(val instanceof Map<?,?> map0){
               if(entry.getValue() instanceof Map<?,?> map1){
                   mergeMapNoCopy(map1, map0);
               }
               else {
                   ((Map.Entry)entry).setValue(map0);
               }
           }else {
               ((Map.Entry)entry).setValue(val);
           }
       }
    }

}
