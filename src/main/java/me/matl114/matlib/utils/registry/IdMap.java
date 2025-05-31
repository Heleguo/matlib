package me.matl114.matlib.utils.registry;

import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

public interface IdMap<T> {
    @Nullable
    T getById(String id);
    @Nonnull
    default Optional<T> getByIdOptional(String id){
        return Optional.ofNullable(getById(id));
    }

    @Nullable
    String getId(T value);

    @Nonnull
    default Optional<String> getOptionalId(T value){
        return Optional.ofNullable(getId(value));
    }

    Collection<String> idSet();

    default boolean containsId(String id){
        return idSet().contains(id);
    }
}
