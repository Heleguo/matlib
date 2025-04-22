package me.matl114.matlib.nmsMirror.nbt;

import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.CastCheck;
import me.matl114.matlib.utils.reflect.descriptor.annotations.ConstructorTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;

import java.util.AbstractList;

import static me.matl114.matlib.nmsMirror.Import.*;

@MultiDescriptive
public interface TagAPI extends TargetDescriptor {
    @MethodTarget(isStatic = true)
    @RedirectClass("net.minecraft.nbt.StringTag")
    @RedirectName("valueOf")
    public Object stringTag(String value);

    @ConstructorTarget
    @RedirectClass("net.minecraft.nbt.IntArrayTag")
    public AbstractList<?> intArrayTag(int[] array);

    @ConstructorTarget
    @RedirectClass("net.minecraft.nbt.ByteArrayTag")
    public AbstractList<?> byteArrayTag(byte[] array);

    @ConstructorTarget
    @RedirectClass("net.minecraft.nbt.LongArrayTag")
    public AbstractList<?> longArrayTag(long[] array);

    @MethodTarget(isStatic = true)
    @RedirectClass("net.minecraft.nbt.IntTag")
    @RedirectName("valueOf")
    public Object intTag(int value);

    @MethodTarget(isStatic = true)
    @RedirectClass("net.minecraft.nbt.ByteTag")
    @RedirectName("valueOf")
    public Object byteTag(byte value);

    @MethodTarget(isStatic = true)
    @RedirectClass("net.minecraft.nbt.ByteTag")
    @RedirectName("valueOf")
    public Object byteTag(boolean value);

    @MethodTarget(isStatic = true)
    @RedirectClass("net.minecraft.nbt.LongTag")
    @RedirectName("valueOf")
    public Object longTag(long value);

    @MethodTarget
    @RedirectClass("net.minecraft.nbt.CollectionTag")
    Object set(AbstractList<?> list, int index, @RedirectType(Tag)Object tag);

    @MethodTarget
    @RedirectClass("net.minecraft.nbt.CollectionTag")
    void add(AbstractList<?> list, int index, @RedirectType(Tag)Object tag);

    @MethodTarget
    @RedirectClass("net.minecraft.nbt.CollectionTag")
    Object remove(AbstractList<?> list, int index);

    @MethodTarget
    @RedirectClass("net.minecraft.nbt.CollectionTag")
    boolean setTag(AbstractList<?> list, int index, @RedirectType(Tag)Object tag);

    @MethodTarget
    @RedirectClass("net.minecraft.nbt.CollectionTag")
    boolean addTag(AbstractList<?> list, int index, @RedirectType(Tag)Object tag);


    @MethodTarget
    @RedirectClass("net.minecraft.nbt.CollectionTag")
    byte getElementType(AbstractList<?> list);

    @MethodTarget
    @RedirectClass("net.minecraft.nbt.Tag")
    int sizeInBytes(Object tag);

    @CastCheck("net.minecraft.nbt.CompoundTag")
    boolean isCompound(Object tag);
}
