package me.matl114.matlib.utils.registry;

import me.matl114.matlib.utils.registry.impl.ContentImpl;
import org.bukkit.NamespacedKey;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

public interface Content<T> extends IdHolder {
    @Nonnull
    T value();

    Registry<? super T> owner();

    default String getNamespace(){
        return owner().namespace();
    }

    boolean isIn(Registry<?> registry);

    boolean is(NamespacedKey key);

    boolean is(String key);

    default boolean is(Content<?> key){
        return value() == key.value();
    }

    default boolean isIn(Group<T> group){
        return group.containContent(this);
    }

    Stream<Group<T>> groups();

    static <W> Content<W> common(W va, String namespace, String id){
        return new ContentImpl<>(va, namespace, id);
    }



}
