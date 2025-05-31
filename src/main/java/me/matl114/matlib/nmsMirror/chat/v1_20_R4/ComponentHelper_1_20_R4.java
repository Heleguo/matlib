package me.matl114.matlib.nmsMirror.chat.v1_20_R4;

import com.google.gson.JsonElement;
import me.matl114.matlib.nmsMirror.chat.ComponentHelper;
import me.matl114.matlib.nmsMirror.impl.Env;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;

import static me.matl114.matlib.nmsMirror.Import.ChatComponent;
import static me.matl114.matlib.nmsMirror.Import.ChatComponentSerializer;

@MultiDescriptive(targetDefault = "net.minecraft.network.chat.Component")
public interface ComponentHelper_1_20_R4 extends ComponentHelper {

    default String toJson(@RedirectType(ChatComponent)Iterable<?> comp){
        return toJson(comp, Env.REGISTRY_FROZEN);
    }


    default JsonElement toJsonTree(@RedirectType(ChatComponent)Iterable<?> comp){
        return serialize(comp, Env.REGISTRY_FROZEN);
    }


    default Iterable<?> fromJson(String json){
        return fromJson(json, Env.REGISTRY_FROZEN);
    }


    default Iterable<?> fromJson(JsonElement json){
        return fromJson(json, Env.REGISTRY_FROZEN);
    }
    @MethodTarget(isStatic = true)
    @RedirectClass(ChatComponentSerializer)
    String toJson(@RedirectType(ChatComponent)Iterable<?> comp,@RedirectType("Lnet/minecraft/core/HolderLookup$Provider;") Object registry);

    @MethodTarget(isStatic = true)
    @RedirectClass(ChatComponentSerializer)
    JsonElement serialize(@RedirectType(ChatComponent)Iterable<?> comp, @RedirectType("Lnet/minecraft/core/HolderLookup$Provider;") Object registry);

    @MethodTarget(isStatic = true)
    @RedirectClass(ChatComponentSerializer)
    Iterable<?> fromJson(String json, @RedirectType("Lnet/minecraft/core/HolderLookup$Provider;") Object registry);

    @MethodTarget(isStatic = true)
    @RedirectClass(ChatComponentSerializer)
    Iterable<?> fromJson(JsonElement json, @RedirectType("Lnet/minecraft/core/HolderLookup$Provider;") Object registry);
}
