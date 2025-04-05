package me.matl114.matlib.nmsMirror.versionedEnv;

import me.matl114.matlib.nmsMirror.Utils;
import me.matl114.matlib.utils.reflect.ObfManager;
import me.matl114.matlib.utils.reflect.ReflectUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.Executor;
import static me.matl114.matlib.nmsMirror.Utils.*;

public class Env {
    public static final Object SERVER;
    public static final Object REGISTRY_ACCESS;
    public static final Object REGISTRY_FROZEN;
    public static final Executor MAIN_EXECUTOR;

    static {
        Class<?> clazz;
        try{
            clazz = ObfManager.getManager().reobfClass("net.minecraft.server.MinecraftServer");
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
        List<Field> fields = ReflectUtils.getAllFieldsRecursively(clazz)
            .stream()
            .toList();
            ;
        Field field;
        SERVER = Utils.matchName(fields, "SERVER");
        field = ObfManager.getManager().matchFieldOrThrow(fields, "registries");
        REGISTRY_ACCESS = Utils.reflect(field, SERVER);
        field =  ObfManager.getManager().lookupFieldInClass(REGISTRY_ACCESS.getClass(), "composite");
        REGISTRY_FROZEN = Utils.reflect(field, REGISTRY_ACCESS);
        Class<?> clazz1;
        try{
            clazz1 = ObfManager.getManager().reobfClass("io.papermc.paper.util.MCUtil");
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
        MAIN_EXECUTOR = (Executor) deobfStatic(clazz1, "MAIN_EXECUTOR");
    }


}
