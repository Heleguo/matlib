package me.matl114.matlib.algorithms.dataStructures.frames.listenedCollection;

import me.matl114.matlib.algorithms.dataStructures.frames.simpleCollection.SimpleCollection;

import java.util.Collection;

public interface ListenedCollection<S extends Collection<T>,T> extends SimpleCollection<S ,T> {
    public void onUpdate(T val, boolean val2);

    public S getHandle();
}
