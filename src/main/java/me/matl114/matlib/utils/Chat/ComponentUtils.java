package me.matl114.matlib.utils.chat;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ComponentUtils {
    private static final ThreadLocal<ItemMeta> DEFAULT_META = ThreadLocal.withInitial(()->new ItemStack(Material.STONE).getItemMeta());


    public static Component fromLegacyString(String value){
        ItemMeta meta = DEFAULT_META.get();
        meta.setDisplayName(value);
        return meta.displayName();
    }

    public static String toLegacyString(Component component){
        ItemMeta meta = DEFAULT_META.get();
        meta.displayName(component);
        return meta.getDisplayName();
    }
}
