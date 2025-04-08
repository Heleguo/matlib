package me.matl114.matlib.nmsMirror.core;

import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.IgnoreFailure;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.version.Version;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static me.matl114.matlib.nmsMirror.Import.*;


@Descriptive(target = "net.minecraft.core.Registry")
public interface RegistriesHelper extends TargetDescriptor {
    @MethodTarget
    int getId(Object registry, Object val);

    @MethodTarget
    public Object byId(Object registry, int index);

    @MethodTarget
    Object getKey(Object registry, Object value);

    @MethodTarget
    Object get(Object registry, @RedirectType(ResourceLocation)Object namespacedKey);

    @MethodTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R2, below = true)
    Object getValue(Object registry, @RedirectType(ResourceLocation)Object namespacedKey);
    static AtomicBoolean FAIL_TYPE_MATCH = new AtomicBoolean(true);
    default Object getRegistryByKey(Object registry, @RedirectType(ResourceLocation)Object namespacedKey){
        if(FAIL_TYPE_MATCH.get()){
            Object val = get(registry, namespacedKey);
            if(val instanceof Optional<?> ){
                FAIL_TYPE_MATCH.set(false);
            }else {
                return val;
            }
        }
        return getValue(registry, namespacedKey);
    }
    default Object getRegistryByKey(Object registry, String key){
        return getRegistryByKey(registry, NMSCore.NAMESPACE_KEY.newNSKey(key));
    }


    @Note("Suggested")
    @MethodTarget
    Optional getOptional(Object registry, @RedirectType(ResourceLocation)Object id);

    @MethodTarget
    boolean containsKey(Object registry, @RedirectType(ResourceLocation)Object id);


    default boolean containsKey(Object registry, String id){
        return containsKey(registry, NMSCore.NAMESPACE_KEY.newNSKey(id));
    }

    @MethodTarget
    Stream stream(Object registry);

    @MethodTarget
    Set keySet(Object registry);

    default Iterable<?> toIterable(Object registries){
        return (Iterable<?>) registries;
    }
}
