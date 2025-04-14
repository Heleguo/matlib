package me.matl114.matlib.nmsMirror.inventory;

import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.common.lang.annotations.Protected;
import me.matl114.matlib.nmsMirror.impl.EmptyEnum;
import me.matl114.matlib.nmsMirror.interfaces.PdcCompoundHolder;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static me.matl114.matlib.nmsMirror.Import.*;

@Descriptive(target = "net.minecraft.world.item.ItemStack")
public interface ItemStackHelper extends TargetDescriptor , PdcCompoundHolder {

    @ConstructorTarget
    public Object newItemStack(@RedirectType(ItemLike) Object itemLike, int count);

    @MethodTarget(isStatic = true)
    @RedirectName("of")
    public Object ofNbt(@RedirectType(CompoundTag) Object nbt);

    @MethodTarget
    public Object copyAndClear(Object itemStack);

    @MethodTarget
    public Object getItem(Object itemStack);

    @MethodTarget
    public int getMaxStackSize(Object itemStack);

    @MethodTarget
    boolean isEmpty(Object itemStack);

    @MethodTarget
    @Note("this is known as \"Duriability\" not attack damage")
    int getDamageValue(Object stack);

    @MethodTarget
    void setDamageValue(Object stack, int value);

    @MethodTarget
    int getMaxDamage(Object stack);

    @MethodTarget
    public void setItem(Object stack, @RedirectType(Item)Object item);

    @Note("Do not use it, its returnType varies with version, only used for comp")
    @MethodTarget
    @RedirectName("getEnchantmentTags")
    Object getEnchantments(Object stack);

    @MethodTarget
    public Object save(Object itemStack, @RedirectType(CompoundTag) Object nbt);

    @MethodTarget
    public Object copy(Object itemSTack, boolean originItem);

    @MethodTarget
    public Object copyWithCount(Object itemSTack, int count);

    @MethodTarget(isStatic = true)
    public boolean isSameItemSameTags(@RedirectType(ItemStack) @Nonnull Object stack, @RedirectType(ItemStack) @Nonnull Object otherStack);

    @MethodTarget
    @RedirectName("hasTag")
    @Note("hasCustomTag value may vary when version up than 1_20_R4")
    public boolean hasCustomTag(Object stack);

    @Nullable
    @MethodTarget
    @RedirectName("getTag")
    @Note("This returns the custom nbtTag of item, no copy")
    public Object getCustomTag(Object stack);

    @Nullable
    @MethodTarget
    @RedirectName("getOrCreateTag")
    @Note("This returns the custom nbtTag of item, no copy")
    public Object getOrCreateCustomTag(Object stack);

    @MethodTarget
    public void setTag(Object stack, @RedirectType(CompoundTag)@Nullable Object nbt);


    @MethodTarget
    public int getCount(Object stack);

    @MethodTarget
    public void setCount(Object stack, int count);


    @MethodTarget
    public ItemStack getBukkitStack(Object stack);
    default boolean equalsEmpty(Object stack){
        return stack == EmptyEnum.EMPTY_ITEMSTACK;
    }
    @MethodTarget
    public ItemStack asBukkitCopy(Object stack);


    default Object getPersistentDataCompound(Object val, boolean create){

        if(create){
            Object custom = getOrCreateCustomTag(val);
            return NMSCore.COMPOUND_TAG.getOrCreateCompound(custom, "PublicBukkitValues");
        }else {
            Object custom = getCustomTag(val);
            return custom == null ? null: NMSCore.COMPOUND_TAG.get(custom, "PublicBukkitValues");
        }
    }

    static String DISPLAY = "display";
    static String NAME = "Name";
    static String LORE = "Lore";
    default boolean matchItem(Object item1, Object item2, boolean matchLore, boolean matchName){
        if(item1 == item2){
            return true;
        }
        if(item1 == null || item2 == null){
            return false;
        }
        if(getItem(item1) != getItem(item2)){
            return false;
        }
        Object nbt1 = getCustomTag(item1);
        Object nbt2 = getCustomTag(item2);
        if(nbt1 == null || nbt2 == null){
            return nbt1 == nbt2;
        }
        return matchTag(nbt1, nbt2, matchLore, matchName);
    }
    @Protected
    default boolean matchTag(@Nonnull Object nbt1,@Nonnull Object nbt2, boolean matchLore, boolean matchName){
        if(matchLore && matchName){
            return Objects.equals(nbt1, nbt2);
        }
        Map<String, ?> map1 = NMSCore.COMPOUND_TAG.tagsGetter(nbt1);
        Map<String, ?> map2 = NMSCore.COMPOUND_TAG.tagsGetter(nbt2);
        int size1 = map1.size();
        int size2 = map2.size();
        //get the DISPLAY tag here
        Object obj1 = map1.get(DISPLAY);
        Object obj2 = map2.get(DISPLAY);
        if(obj1 != null){
            size1 -= 1;
        }
        if(obj2 != null){
            size2 -= 1;
        }
        if(size1 != size2){
            return false;
        }
        Set<? extends Map.Entry<String, ?>> key1 = map1.entrySet();
        for (var val: key1){
            Object value = val.getValue();
            //escape certain tag value, which its key is DISPLAY, using Reference == instead of String.equals
            //in this situation, values are different from each other
            if(value == obj1){
                continue;
                //not equal, otherside null(key absent) or sth(value not match)
            }else if(!Objects.equals(value, map2.get(val.getKey()))){
                return false;
            }
        }
        //key-value都匹配
        //考虑Display
        //有一方没有display
        if(obj1 == null || obj2 == null){
            //如果不匹配任何display, 或者均没有,返回true
            return obj1 == obj2 || (!matchLore && !matchName);
        }

       // Class<?> nbtCompClass = NMSCore.COMPOUND_TAG.getTargetClass();
        if(isCompoundTag(obj1) && isCompoundTag(obj2)){
            if(matchName && !Objects.equals(NMSCore.COMPOUND_TAG.get(obj1, NAME), NMSCore.COMPOUND_TAG.get(obj2, NAME))){
                return false;
            }
            if(matchLore && !Objects.equals(NMSCore.COMPOUND_TAG.get(obj1, LORE), NMSCore.COMPOUND_TAG.get(obj2, LORE))){
                return false;
            }
            return true;
        }else {
            return obj1.getClass() == obj2.getClass();
        }
    }
    @CastCheck(NbtCompoundClass)
    public boolean isCompoundTag(Object unknown);
}

