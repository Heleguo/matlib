package me.matl114.matlib.nmsMirror.core;

import me.matl114.matlib.nmsMirror.Utils;
import me.matl114.matlib.utils.reflect.ObfManager;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class BuiltInRegistryEnum {
    private static final String mojangName = "net.minecraft.core.registries.BuiltInRegistries";
    private static final Class<?> RegistriesClass ;
    public static final Object ITEM;
    public static final Object BLOCK;

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
    }
}
