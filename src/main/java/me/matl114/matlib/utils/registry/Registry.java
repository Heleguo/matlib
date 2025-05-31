package me.matl114.matlib.utils.registry;

import me.matl114.matlib.common.lang.annotations.Note;
import net.kyori.adventure.key.Namespaced;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Registry<T> extends IdHolder, Namespaced ,IdMap<T>,  Iterable<T> {
    // may have parent Registry
    @Nullable
    Registry<? super T> owner();

    @Nullable
    NamespacedKey getKey(T value);

    @Nullable
    Content<T> getContent(T value);

    @Nullable
    T getByKey(NamespacedKey key);

    default Optional<T> getByKeyOptional(NamespacedKey key){
        return Optional.ofNullable(getByKey(key));
    }

    Collection<NamespacedKey> keySet();

    Collection<Map.Entry<NamespacedKey, Content<T>>> entrySet();

    Collection<T> contents();

    @NotNull
    Iterator<T> iterator();

    Stream<T> getByGroup(Group<T> group);

    Iterable<Content<T>> asContentSet();

    @Note("using this namespace as namespace")
    Content<T> registerThis(String rawName, T value);

    //left for group, where should group registry be

    Content<T> register(NamespacedKey namespacedKey, T value);


    default boolean unregisterThis(String id){
        return getByIdOptional(id).map((Function<T, Boolean>)this::unregister).orElse(false);
    }


    boolean unregister(T value);

    default boolean unregister(NamespacedKey namespacedKey){
        return getByKeyOptional(namespacedKey).map((Function<T, Boolean>)this::unregister).orElse(false);
    }

    Registry<T> freezeView();

    void unfreeze();

    boolean containsKey(NamespacedKey key);

    boolean containsValue(Content<T> value);

    Content<T> createRegistryContent(String value, T newValue);

    default <R, W extends Registry<R>> W cast(){
        return (W) this;
    }

}
