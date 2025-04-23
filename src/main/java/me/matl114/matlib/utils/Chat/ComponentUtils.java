package me.matl114.matlib.utils.chat;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;

public class ComponentUtils {
    private static final ThreadLocal<ItemMeta> DEFAULT_META = ThreadLocal.withInitial(()->new ItemStack(Material.STONE).getItemMeta());
    public static final Component EMPTY = Component.empty();

    public static final String EMPTY_STRING = "";
    public static Component fromLegacyString(@Nonnull String value){
        if(value.isEmpty()){
            return EMPTY;
        }
        ItemMeta meta = DEFAULT_META.get();
        meta.setDisplayName(value);
        return meta.displayName();
    }


    public static String toLegacyString(@Nonnull Component component){
        ItemMeta meta = DEFAULT_META.get();
        meta.displayName(component);
        String name =  meta.getDisplayName();
        return name == null ?"": name;
    }
}
