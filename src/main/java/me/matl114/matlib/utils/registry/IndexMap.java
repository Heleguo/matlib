package me.matl114.matlib.utils.registry;

import me.matl114.matlib.common.lang.annotations.Internal;

@Internal
public interface IndexMap<T> {
    Content<T> getByIndex(int index);

    int getIndex(Content<T> value);
}
