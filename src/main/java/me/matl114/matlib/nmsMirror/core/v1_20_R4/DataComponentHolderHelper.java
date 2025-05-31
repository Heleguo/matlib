package me.matl114.matlib.nmsMirror.core.v1_20_R4;

import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;

import static me.matl114.matlib.nmsMirror.Import.DataComponentType;

//This interface should be appended to other helper interface
public interface DataComponentHolderHelper {

    @MethodTarget
    Object getOrDefault(Object stack, @RedirectType(DataComponentType)Object dataComponentType, Object defaultValue);

    @MethodTarget
    Object get(Object stack, @RedirectType(DataComponentType)Object dataComponentType);

    @MethodTarget
    Object getComponents(Object stack);

    @MethodTarget
    boolean has(Object stack, @RedirectType(DataComponentType)Object type);
}
