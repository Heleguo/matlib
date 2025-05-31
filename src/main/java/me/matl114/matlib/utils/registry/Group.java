package me.matl114.matlib.utils.registry;

import javax.annotation.Nonnull;

public interface Group<T> extends Iterable<Content<T>>, Content<Group<T>>, IdHolder{
    @Nonnull
    default Group<T> value(){
        return this;
    }

    default boolean containContent(Content<T> value){
        return contains(value.value());
    }

    boolean contains(T value);

    Registry<T> type();
}
