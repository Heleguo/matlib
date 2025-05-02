package me.matl114.matlib.algorithms.dataStructures.frames.lazyCollection;

import me.matl114.matlib.common.lang.annotations.NotCompleted;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

@NotCompleted
public class LazySet<T> extends AbstractSet<T> implements Set<T> {

    @Override
    public Iterator<T> iterator() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }
}
