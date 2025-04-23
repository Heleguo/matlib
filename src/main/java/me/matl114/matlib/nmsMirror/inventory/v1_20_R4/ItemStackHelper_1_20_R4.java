package me.matl114.matlib.nmsMirror.inventory.v1_20_R4;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import me.matl114.matlib.common.lang.annotations.Protected;
import me.matl114.matlib.nmsMirror.versionedEnv.Env;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.nmsMirror.inventory.ItemStackHelper;
import me.matl114.matlib.nmsMirror.versionedEnv.Env1_20_R4;
import me.matl114.matlib.common.lang.annotations.Internal;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.version.Version;
import me.matl114.matlib.utils.version.VersionAtLeast;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static me.matl114.matlib.nmsMirror.Import.*;

@VersionAtLeast(Version.v1_20_R4)
@Descriptive(target = "net.minecraft.world.item.ItemStack")
@Note("After 1.20.5, ItemStack use DataComponents, only customTag remain nbt, so")
public interface ItemStackHelper_1_20_R4 extends ItemStackHelper {

    @MethodTarget(isStatic = true)
    @RedirectName("isSameItemSameComponents")
    public boolean isSameItemSameTags(@RedirectType(ItemStack) @Nonnull Object stack, @RedirectType(ItemStack) @Nonnull Object otherStack);

    @MethodTarget
    @RedirectName("set")
    Object setDataComponentValue(Object item, @RedirectType(DataComponentType) Object type, Object value);

    @MethodTarget
    @RedirectName("remove")
    Object removeDataComponentValue(Object stack, @RedirectType(DataComponentType) Object type);

    @MethodTarget
    @RedirectName("get")
    Object getDataComponentValue(Object stack, @RedirectType(DataComponentType) Object type);

    @MethodTarget
    Object getEnchantments(Object stack);

    @MethodTarget
    @RedirectName("save")
    @Internal
    Object saveV1_20_R4(Object stack,@RedirectType("Lnet/minecraft/core/HolderLookup$Provider;")Object registries,@RedirectType(Tag) Object tag );

    @MethodTarget(isStatic = true)
    @RedirectName("parseOptional")
    Object parseV1_20_R4(@RedirectType("Lnet/minecraft/core/HolderLookup$Provider;")Object registries, @RedirectType(CompoundTag) Object nbt);

    @MethodTarget
    @Internal
    Object getComponents(Object stack);

    default Object ofNbt(@RedirectType(CompoundTag) Object nbt){
        return parseV1_20_R4(Env.REGISTRY_FROZEN, nbt);
    }

    @Override
    default Object save(Object stack,@RedirectType(CompoundTag) Object tag){
        return saveV1_20_R4(stack, Env.REGISTRY_FROZEN, tag);
    }
    @Override
    default boolean hasCustomTag(Object stack){
        return getCustomTag(stack) != null;
    }
    @Override
    default Object getCustomTag(Object stack){
        return Env1_20_R4.ICUSTOMDATA.tagOrNull(getDataComponentValue(stack, DataComponentEnum.CUSTOM_DATA));
    }
    @Override
    default Object getOrCreateCustomTag(Object stack){
        Object customData = getDataComponentValue(stack, DataComponentEnum.CUSTOM_DATA);
        if(customData != null){
            return Env1_20_R4.ICUSTOMDATA.tagOrNull(customData);
        }else {
            Object newComp = NMSCore.COMPOUND_TAG.newComp();
            Object newCustomData = Env1_20_R4.ICUSTOMDATA.ofNoCopy(newComp);
            setDataComponentValue(stack, DataComponentEnum.CUSTOM_DATA, newCustomData);
            return newComp;
        }
    }

    @Override
    default void setTag(Object stack, @RedirectType(CompoundTag)@Nullable Object nbt){
        if(NMSCore.COMPOUND_TAG.isEmpty(nbt)){
            removeDataComponentValue(stack, DataComponentEnum.CUSTOM_DATA);
        }else {
            setDataComponentValue(stack, DataComponentEnum.CUSTOM_DATA, Env1_20_R4.ICUSTOMDATA.of(nbt));
        }
    }
    @Override
    default boolean matchItem(Object item1, Object item2, @Note("distinct assumed that they both have lore/name, and we don't care about them, BUT if one of then don't have, then it is regarded as not match") boolean distinctLore, boolean distinctName){
        if(item1 == item2){
            return true;
        }
        if(item1 == null || item2 == null){
            return false;
        }
        if(distinctLore && distinctName){
            return isSameItemSameTags(item1, item2);
        }
        if(getItem(item1) != getItem(item2)){
            return false;
        }
        Object comp1 = getComponents(item1);
        Object comp2 = getComponents(item2);
        if(comp1 == comp2){
            return true;
        }
        if(comp1 == null || comp2 == null){
            return false;
        }
        //should match name , or match lore here
        return matchComp(comp1, comp2, distinctLore, distinctName);
    }
    @Override
    default boolean matchNbt(Object item1, Object item2, boolean distinctLore, boolean distinctName){
        Object comp1 = getComponents(item1);
        Object comp2 = getComponents(item2);
        if(comp1 == comp2){
            return true;
        }
        if(comp1 == null || comp2 == null){
            return false;
        }
        //should match name , or match lore here
        return matchComp(comp1, comp2, distinctLore, distinctName);
    }
    //need optimize
    @Internal
    default boolean matchComp(@Nonnull Object comp1, @Nonnull Object comp2, boolean matchLore, boolean matchName){
        //prototype is from Item.component, it should be the reference to same object! if itemType is same
//        if(Env1_20_R4.ICOMPONENT.prototypeGetter(comp1) != Env1_20_R4.ICOMPONENT.prototypeGetter(comp2)){
//            return false;
//        }
        Reference2ObjectMap<Object, Optional<?>> patch1 = Env1_20_R4.ICOMPONENT.patchGetter(comp1);
        Reference2ObjectMap<Object, Optional<?>> patch2 = Env1_20_R4.ICOMPONENT.patchGetter(comp2);

        if(patch1 == patch2){
            return true;
        }
        if(patch1.size() != patch2.size())return false;
        ObjectSet<Reference2ObjectMap.Entry<Object, Optional<?>>> entryset1 = patch1.reference2ObjectEntrySet();
        ObjectIterator<Reference2ObjectMap.Entry<Object,Optional<?>>> iter =
            entryset1 instanceof Reference2ObjectMap.FastEntrySet fast?fast.fastIterator():
            entryset1.iterator();

        while (iter.hasNext()){
            var entry = iter.next();
            var type = entry.getKey();
            if(!matchName && type == DataComponentEnum.CUSTOM_NAME){
            } else if(!matchLore && type == DataComponentEnum.LORE){
            } else {
                if(!Objects.equals(entry.getValue(), patch2.get(type))){
                    return false;
                }
            }
        }
        //ensure that no more type that need check in patch2
        return true;
    }
    @MethodTarget(isStatic = true)
    @RedirectName("hashItemAndComponents")
    public int customHashcode(@Nonnull @RedirectType(ItemStack) Object item);

    @Override
    default int customHashWithoutDisplay(Object item){
        int a = 79* getItem(item).hashCode() ;
        Object comp = getComponents(item);
        if(comp == null){
            return a;
        }
        Reference2ObjectMap<Object, Optional<?>> patch = Env1_20_R4.ICOMPONENT.patchGetter(comp);
        if(patch.containsKey(DataComponentEnum.LORE)){
            Map<Object, Optional<?>> patchShallowCopy = new Reference2ObjectOpenHashMap<>(patch);
            patchShallowCopy.remove(DataComponentEnum.LORE);
            return a + patchShallowCopy.hashCode();
        }
        return a + patch.hashCode();
//        ObjectSet<Reference2ObjectMap.Entry<Object, Optional<?>>> entryset = patch.reference2ObjectEntrySet();
//        Object key,value;
//        for (var entry: entryset){
//            key = entry.getKey();
//            if(key != DataComponentEnum.LORE && key != null && (value = entry.getValue()) != null){
//                a += key.hashCode()^ value.hashCode();
//            }
//        }
//        return a;
    }

    default boolean presentAt(Reference2ObjectMap<?, Optional<?>> map, Object type){
        var re = map.get(type);
        if(re == null || re.isEmpty()){
            return false;
        }
        return true;
    }
}
