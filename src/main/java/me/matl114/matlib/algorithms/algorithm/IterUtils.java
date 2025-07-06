package me.matl114.matlib.algorithms.algorithm;

import me.matl114.matlib.algorithms.dataStructures.frames.collection.MappingIterator;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.Zipping2Iterator;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.Zipping3Iterator;
import me.matl114.matlib.algorithms.dataStructures.struct.IndexEntry;
import me.matl114.matlib.algorithms.dataStructures.struct.IndexFastEntry;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.algorithms.dataStructures.struct.Triplet;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

public class IterUtils {
    public static <T> Iterable<IndexEntry<T>> enumerate(Iterable<T > val){
        return new Iterable<IndexEntry<T>>() {
            @NotNull
            @Override
            public Iterator<IndexEntry<T>> iterator() {
                return new MappingIterator<>(val.iterator(), new Function<T, IndexEntry<T>>() {
                    int index;
                    @Override
                    public IndexEntry<T> apply(T t) {
                        return new IndexFastEntry<>(index++, t);
                    }
                });
            }
        };
    }

    public static <T> Iterable<IndexEntry<T>> fastEnumerate(Iterable<T > val){
        return ()-> {
                IndexFastEntry<T> mutableEntry = new IndexFastEntry<>(-1, null);
                return new MappingIterator<>(val.iterator(), (k)->{
                    mutableEntry.addIndex();
                    mutableEntry.setValue(k);
                    return mutableEntry;
                });
            };
    }
    public static <T,W> Iterable<Pair<T,W>> zip(Iterable<T> val1, Iterable<W> val2){
        return ()->  {
                return new Zipping2Iterator<>(val1.iterator(), val2.iterator());
            };
    }
    public static <T,W> Iterable<Pair<T,W>> fastZip(Iterable<T> val1, Iterable<W> val2){
        return ()->  {
            Pair<T,W> pair = Pair.of(null, null);
            return new Zipping2Iterator<>(val1.iterator(), val2.iterator()){
                @Override
                public Pair<T, W> next() {
                    pair.setA(a != null? a.next(): null);
                    pair.setB(b != null? b.next() : null);
                    return pair;
                }
            };
        };
    }

    public static <T,W,R> Iterable<Triplet<T,W,R>> zip(Iterable<T> val1, Iterable<W> val2, Iterable<R> val3){
        return ()->  {
            return new Zipping3Iterator<>(val1.iterator(), val2.iterator(), val3.iterator());
        };
    }
    public static <T,W,R> Iterable<Triplet<T,W,R>> fastZip(Iterable<T> val1, Iterable<W> val2, Iterable<R> val3){
        return ()->  {
            Triplet<T,W,R> pair = Triplet.of(null, null,null);
            return new Zipping3Iterator<>(val1.iterator(), val2.iterator(), val3.iterator()){
                @Override
                public Triplet<T, W, R> next() {
                    pair.setA(a != null? a.next(): null);
                    pair.setB(b != null? b.next() : null);
                    pair.setC(c != null? c.next() : null);
                    return pair;
                }
            };
        };
    }

    public static <T> Stream<T> append(Stream<T> a, Stream<T> b){
        return Stream.concat(a,b);
    }
}
