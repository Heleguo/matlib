package me.matl114.matlib.nmsMirror.nbt;

import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;

@Descriptive(target = "net.minecraft.nbt.Tag")
public interface TagHelper extends TargetDescriptor {
    @MethodTarget
    byte getId(Object tag);

    @MethodTarget
    Object copy(Object tag);

    @MethodTarget
    String getAsString(Object tag);

}
