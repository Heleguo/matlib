package me.matl114.matlib.nmsMirror.nbt;

import me.matl114.matlib.common.lang.annotations.Internal;
import me.matl114.matlib.utils.reflect.classBuild.annotation.IgnoreFailure;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.CastCheck;
import me.matl114.matlib.utils.reflect.descriptor.annotations.ConstructorTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.version.Version;

import java.util.AbstractList;
import java.util.List;

import static me.matl114.matlib.nmsMirror.Import.*;

@MultiDescriptive(targetDefault = "net.minecraft.nbt.Tag")
public interface TagAPI extends TargetDescriptor ,TagHelper{
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

    @ConstructorTarget
    @RedirectClass("net.minecraft.nbt.ListTag")
    public AbstractList<?> listTag(List<?> list, byte type);
    @ConstructorTarget
    @RedirectClass("net.minecraft.nbt.ListTag")
    public AbstractList<?> listTag();

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

    @RedirectClass(SnbtPrinterTagVisitor)
    @ConstructorTarget
    Object createSnbtPrinter(String prefix, int indentationLevel, List<String> pathParts);

    @RedirectClass(SnbtPrinterTagVisitor)
    @MethodTarget
    @RedirectName("visit")
    String visitAsSnbt(Object snbt, @RedirectType(Tag)Object tag);

    @RedirectClass(StringTagVisitor)
    @ConstructorTarget
    Object createNbtStringPrinter();

    @RedirectClass(StringTagVisitor)
    @MethodTarget
    @RedirectName("visit")
    String visitAsNbtString(Object nbt, @RedirectType(Tag)Object tag);

    default String printAsSnbt(Object nbt, String prefix, int indentationLevel, List<String> pathParts){
        return visitAsSnbt(createSnbtPrinter(prefix, indentationLevel, pathParts),nbt);
    }

    //change with version, do it later
    @RedirectClass(TextComponentTagVisitor)
    @ConstructorTarget
    @IgnoreFailure(thresholdInclude = Version.v1_20_R4, below = true)
    default  Object createChatComponentPrinter(String prefix){
        return createChatComponentPrinter(prefix, 0);
    }
    default String printAsNbtString(Object tag){
        return visitAsNbtString(createNbtStringPrinter(), tag);
    }

    @Internal
    @RedirectClass(TextComponentTagVisitor)
    @ConstructorTarget
    @IgnoreFailure(thresholdInclude = Version.v1_20_R4, below = false)
    Object createChatComponentPrinter(String prefix, int why);

    default Iterable<?> printAsChatComponent(Object tag, String prefix){
        return visitAsChatComponent(createChatComponentPrinter(prefix),tag);
    }

    @RedirectClass(TextComponentTagVisitor)
    @MethodTarget
    @RedirectName("visit")
    Iterable<?> visitAsChatComponent(Object chat, @RedirectType(Tag)Object tag);

}
