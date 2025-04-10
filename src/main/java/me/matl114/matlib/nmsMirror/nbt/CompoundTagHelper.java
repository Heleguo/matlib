package me.matl114.matlib.nmsMirror.nbt;

import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static me.matl114.matlib.nmsMirror.Import.*;

@Descriptive(target = "net.minecraft.nbt.CompoundTag")
public interface CompoundTagHelper extends TargetDescriptor, TagHelper {
    @ConstructorTarget
    Object newComp();

    @ConstructorTarget
    Object newComp(Map<String,?> entries);

    @MethodTarget
    Set<String> getAllKeys(Object nbt);

    @MethodTarget
    int size(Object nbt);

    @MethodTarget
    Object put(Object nbt, String key,@RedirectType(Tag) Object tagElement);

    @MethodTarget
    void putByte(Object nbt, String key, byte value);

    @MethodTarget
    void putShort(Object nbt, String key, short value);
    @MethodTarget
    void putInt(Object nbt, String key, int value);
    @MethodTarget
    void putLong(Object nbt, String key, long value);
    @MethodTarget
    void putUUID(Object nbt, String key, UUID value);
    @MethodTarget
    UUID getUUID (Object nbt, String key);
    @MethodTarget
    boolean hasUUID(Object nbt, String key);

    @MethodTarget
    void putFloat(Object nbt, String key, float value);

    @MethodTarget
    void putDouble(Object nbt, String key, double value);

    @MethodTarget
    void putString(Object nbt, String key, String value);

    @MethodTarget
    void putByteArray(Object nbt, String key, byte[] value);

    @MethodTarget
    void putByteArray(Object nbt, String key, List<Byte> value);

    @MethodTarget
    void putIntArray(Object nbt, String key, int[] value);

    @MethodTarget
    void putIntArray(Object nbt, String key, List<Integer> value);

    @MethodTarget
    void putLongArray(Object nbt, String key, long[] value);

    @MethodTarget
    void putLongArray(Object nbt, String key, List<Long> value);

    @MethodTarget
    void putBoolean(Object nbt, String key, boolean value);

    @MethodTarget
    @Nullable
    @RedirectType(Tag)
    Object get(Object nbt, String key);

    @MethodTarget
    boolean contains(Object nbt, String key);

    @MethodTarget
    boolean contains(Object nbt, String key, int type);

    @MethodTarget
    byte getTagType(Object nbt, String key);

    @MethodTarget
    public byte getByte(Object nbt, String key);

    @MethodTarget
    public short getShort(Object nbt, String key);

    @MethodTarget
    public int getInt(Object nbt, String key);

    @MethodTarget
    public long getLong(Object nbt, String key);

    @MethodTarget
    public float getFloat(Object nbt, String key);

    @MethodTarget
    public double getDouble(Object nbt, String key);

    @MethodTarget
    public String getString(Object nbt, String key);

    @MethodTarget
    @Note("may throw crash report")
    public byte[] getByteArray(Object nbt, String key);

    @MethodTarget
    @Note("may throw crash report")
    public int[] getIntArray(Object nbt, String key);

    @MethodTarget
    @Note("may throw crash report")
    public long[] getLongArray(Object nbt, String key);

    @MethodTarget
    @Note("may throw crash report")
    @RedirectType(CompoundTag)
    public Object getCompound(Object nbt, String key);

    @MethodTarget
    @Note("may throw crash report")
    @RedirectType(ListTag)
    public Object getList(Object nbt, String key, int type);

    @MethodTarget
    public boolean getBoolean(Object nbt, String key);

    @MethodTarget
    public void remove(Object nbt, String key);

    @MethodTarget
    public boolean isEmpty(Object nbt);

    @MethodTarget
    @Contract("_,_ -> param1")
    public Object merge(Object nbt,@RedirectType(CompoundTag) Object otherNbt);


    @FieldTarget
    @RedirectType("Ljava/util/Map;")
    Map<String,?> tagsGetter(Object nbt);

//    @MethodTarget
//    Map<String,?> entries(Object nbt);

    default void clear(Object nbt){
        tagsGetter(nbt).clear();
    }

    default Object getOrCreateCompound(Object nbt, String key){
        Object obj = getCompound(nbt, key);
        if(obj != null){
            return obj;
        }else {
            obj = newComp();
            put(nbt, key, obj);
            return obj;
        }
    }
    //removed
//    @MethodTarget
//    @RedirectName("entries")
//    Map<String, ?> toUnmodified(Object nbt);
}
