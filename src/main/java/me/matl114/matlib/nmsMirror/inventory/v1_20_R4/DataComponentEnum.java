package me.matl114.matlib.nmsMirror.inventory.v1_20_R4;

import me.matl114.matlib.nmsMirror.Utils;
import me.matl114.matlib.utils.reflect.internel.ObfManager;
import me.matl114.matlib.utils.version.Version;
import me.matl114.matlib.utils.version.VersionAtLeast;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

@VersionAtLeast(Version.v1_20_R4)
public class DataComponentEnum {
    private static final String mojangName1 = "net.minecraft.core.component.DataComponents";
    private static final Class<?> targetClass;
    public static final Object CUSTOM_DATA;
    public static final Object CUSTOM_NAME;
    public static final Object LORE;
    public static final Object ENCHANTMENTS;

    public static final Object COMMON_ITEM_COMPONENTS;
    private static final String mojangName2 = "net.minecraft.core.component.DataComponentMap";
    public static final Object COMPONENT_MAP_EMPTY ;
    static{
        Class<?> a= null;
        try{
            a = ObfManager.getManager().reobfClass(mojangName1);
        }catch (Throwable e){
        }
        List<Field> fields = Arrays.stream(a.getFields())
            .filter(f -> Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers()))
            .toList();
        targetClass = a;
        CUSTOM_DATA = Utils.matchName(fields, "CUSTOM_DATA");
        CUSTOM_NAME = Utils.matchName(fields, "CUSTOM_NAME");
        LORE = Utils.matchName(fields, "LORE");
        ENCHANTMENTS = Utils.matchName(fields, "ENCHANTMENTS");
        COMMON_ITEM_COMPONENTS = Utils.matchName(fields, "COMMON_ITEM_COMPONENTS");
        try{
            a = ObfManager.getManager().reobfClass(mojangName2);
        }catch (Throwable e){
        }
        fields = Arrays.stream(a.getFields())
            .filter(f -> Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers()))
            .toList();
        COMPONENT_MAP_EMPTY = Utils.matchName(fields, "EMPTY");
    }
}
