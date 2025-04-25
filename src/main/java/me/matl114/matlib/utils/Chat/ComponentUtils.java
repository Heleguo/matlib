package me.matl114.matlib.utils.chat;

import me.matl114.matlib.algorithms.dataStructures.struct.Holder;
import me.matl114.matlib.common.functions.FuncUtils;
import me.matl114.matlib.utils.CraftUtils;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import me.matl114.matlib.utils.reflect.wrapper.FieldAccess;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    private static final VarHandle loreHandle =
        Holder.of(DEFAULT_META.get())
            .thenApply(Object::getClass)
            .thenApply(ReflectUtils::getVarHandlePrivate,"lore")
            .thenApply(Objects::requireNonNull)
            .get();

    public static <T> void addToLore(ItemMeta meta, Component... adds){
        if(adds.length == 0){
            return;
        }
        ItemMeta meta1 = DEFAULT_META.get();
        List<T> lore1 = (List<T>) loreHandle.get(meta);
        meta1.lore(Arrays.asList(adds));
        List<T> lore2 = (List<T>) loreHandle.get(meta1);
        if(lore1 == null){
            lore1 = new ArrayList<>(lore2);
            loreHandle.set(meta, lore1);
        }else {
            lore1.addAll(lore2);
        }
        meta1.lore(null);
    }
    public static void removeLoreLast(ItemMeta meta, int removalSize){
        if(removalSize <= 0){
            return;
        }
        List<?> lore1 = (List<?>) loreHandle.get(meta);
        if(lore1.size() <= removalSize){
            //remove lore nbt
            loreHandle.set(meta, (List<?>) null);
        }else {
            for (int i=0 ; i < removalSize; ++i){
                lore1.removeLast();
            }
        }
    }
}
