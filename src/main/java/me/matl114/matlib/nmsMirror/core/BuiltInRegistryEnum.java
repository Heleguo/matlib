package me.matl114.matlib.nmsMirror.core;

import me.matl114.matlib.nmsMirror.Utils;
import me.matl114.matlib.utils.reflect.internel.ObfManager;
import me.matl114.matlib.utils.version.DependsOnVersion;
import me.matl114.matlib.utils.version.Version;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class BuiltInRegistryEnum {
    private static final String mojangName = "net.minecraft.core.registries.BuiltInRegistries";
    private static final Class<?> RegistriesClass ;
    public static final Iterable<?> ITEM;
    public static final Iterable<?> BLOCK;
    public static final Iterable<?> ATTRIBUTE;

    @DependsOnVersion(near = Version.v1_20_R4)
    public static final Iterable<?> DATA_COMPONENT_TYPE;
    @DependsOnVersion(near = Version.v1_21_R1)
    public static final Iterable<?> ENCHANTMENT;
    static{
        Class<?> a= null;
        try{
            a = ObfManager.getManager().reobfClass(mojangName);
        }catch (Throwable e){
        }
        RegistriesClass = a;
        List<Field> fields = Arrays.stream(a.getFields())
            .filter(f -> Modifier.isStatic(f.getModifiers()))
            .toList();
        ITEM = Utils.matchName(fields, "ITEM");
        BLOCK = Utils.matchName(fields , "BLOCK");
        ATTRIBUTE = Utils.matchName(fields, "ATTRIBUTE");

        ENCHANTMENT = Utils.matchNull(fields, "ENCHANTMENT");
        DATA_COMPONENT_TYPE = Utils.matchNull(fields, "DATA_COMPONENT_TYPE");
    }
}
