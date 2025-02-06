package me.matl114.matlib.Utils;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public class PluginUtils {
    private static Plugin plugin;
    public static void init(Plugin pl){
        plugin = pl;
    }
    public static NamespacedKey getNamedKey(String key){
        return new NamespacedKey(plugin, key);
    }
}
