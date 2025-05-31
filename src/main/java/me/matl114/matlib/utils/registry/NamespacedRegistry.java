package me.matl114.matlib.utils.registry;

import net.kyori.adventure.key.Namespaced;

import java.util.Set;

public interface NamespacedRegistry<T, W extends Namespaced> extends Registry<T>{
    public <R extends T> Registry<R> createSubRegistry(W holder);

    public boolean removeSubRegistry(W holder);

    public <R extends T> Registry<R> getSubRegistry(String namespace);

    public <R extends T> Registry<R> getSubRegistry(W holder);

    public Set<String> getNamespaces();

    public <R extends T> Registry<R> asRegistryView();
}
