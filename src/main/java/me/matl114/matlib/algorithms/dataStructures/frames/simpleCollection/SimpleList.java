package me.matl114.matlib.algorithms.dataStructures.frames.simpleCollection;

import me.matl114.matlib.algorithms.dataStructures.frames.collection.ListMapView;

import java.util.Collection;
import java.util.List;

public interface SimpleList<V> extends SimpleCollection<List<V>,V> , ListMapView<V,V> {
    @Override
    default void flush() {

    }
}
