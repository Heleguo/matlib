package me.matl114.matlib.nmsMirror.inventory.v1_20_R4;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;

import java.util.Optional;

import static me.matl114.matlib.nmsMirror.Import.*;

@Descriptive(target = "net.minecraft.core.component.PatchedDataComponentMap")
public interface ItemDataComponentMapHelper extends DataComponentMapHelper {
    @FieldTarget
    @RedirectType(DataComponentMap)
    public Iterable<?> prototypeGetter(Object patchedMap);

    @FieldTarget
    @RedirectType("Lit/unimi/dsi/fastutil/objects/Reference2ObjectMap;")
    Reference2ObjectMap<Object, Optional<?>> patchGetter(Object patchedMap);

    @MethodTarget
    public Object get(Object patchedMap, @RedirectType(DataComponentType) Object type);

    @MethodTarget
    public Object set(Object patchedMap, @RedirectType(DataComponentType) Object type, Object value);

    @MethodTarget
    public Object remove(Object patchedMap, @RedirectType(DataComponentType) Object type);

    @MethodTarget
    public void setAll(Object patchedMap, @RedirectType(DataComponentMap) Object comp);

}
