package me.matl114.matlib.utils.registry;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public interface IdHolder extends Keyed {
    String namespace();
    String keyStr();

    @Override
   @NotNull NamespacedKey getKey();
    String getId();
}
