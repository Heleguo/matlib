package me.matl114.matlib.nmsUtils;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.nmsMirror.craftbukkit.persistence.CraftPersistentDataContainerHelper;
import me.matl114.matlib.nmsMirror.impl.CraftBukkit;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.nmsMirror.impl.NMSItem;
import me.matl114.matlib.nmsUtils.nbt.TagCompoundView;
import me.matl114.matlib.utils.CraftUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.VarHandle;
import java.util.Map;

import static me.matl114.matlib.nmsMirror.impl.NMSItem.*;
import static me.matl114.matlib.nmsMirror.impl.NMSCore.*;

public class ItemUtils {

    public static TagCompoundView getPersistentDataContainerView(ItemStack craftItemStack, boolean forceCreate){
        var handle = CraftBukkit.ITEMSTACK.unwrapToNMS(craftItemStack);

        var pdc = ITEMSTACK.getPersistentDataCompound(handle, forceCreate);

        if(pdc != null){
            return new TagCompoundView(pdc);
        }else {
            return null;
        }
    }

    public static void setPersistentDataContainer(@Note("please make sure that this is a CraftItemStack") @NotNull ItemStack craftItemStack, @NotNull PersistentDataContainer container, int copyLevel){
        var handle = CraftBukkit.ITEMSTACK.unwrapToNMS(craftItemStack);
        Map<String, ?> val;
        if(container instanceof TagCompoundView view){
            val = view.customDataTags;
        }else if(CraftBukkit.PERSISTENT_DATACONTAINER.isCraftContainer(container)){
            val = CraftBukkit.PERSISTENT_DATACONTAINER.getRaw(container);
        }else {
            throw new UnsupportedOperationException("Persistent Data Container Class not supported: "+container.getClass());
        }
        Object newComp ;
        switch (copyLevel){
            case 1:
                val = new Object2ObjectOpenHashMap<>(val, 0.8f);
                newComp = COMPOUND_TAG.newComp(val);
                break;
            case 2:
                newComp = COMPOUND_TAG.newComp(val);
                newComp = COMPOUND_TAG.copy(newComp);
                break;
            default:
                newComp = COMPOUND_TAG.newComp(val);
                break;
        }
        ITEMSTACK.setPersistentDataCompound(handle, newComp);
    }

    public static ItemStack newStack(Material material, int amount){
        return CraftBukkit.ITEMSTACK.createCraftItemStack(material, amount);
    }
    public static ItemStack newStack(Material material, int amount, ItemMeta meta){
        return CraftBukkit.ITEMSTACK.createCraftItemStack(material, amount, meta);
    }

    public static ItemStack cleanStack(ItemStack whatever){
        return CraftBukkit.ITEMSTACK.getCraftStack(whatever);
    }
    public static ItemStack copyStack(ItemStack whatever){
        return CraftBukkit.ITEMSTACK.asCraftCopy(whatever);
    }
    public static Object unwrapHandle(ItemStack it){
        return CraftBukkit.ITEMSTACK.unwrapToNMS(it);
    }
    public static Object getHandle(ItemStack cis){
        return CraftBukkit.ITEMSTACK.handleGetter(cis);
    }
    public static boolean matchItemStack(ItemStack item1, ItemStack item2, boolean distinctLore){
        return matchItemStack(item1, item2, distinctLore, true);
    }
    public static boolean matchItemStack(ItemStack item1, ItemStack item2, boolean distinctLore, boolean distinctName){
        var handle1 = CraftBukkit.ITEMSTACK.unwrapToNMS(item1);
        var handle2 = CraftBukkit.ITEMSTACK.unwrapToNMS(item2);
        return ITEMSTACK.matchItem(handle1, handle2, distinctLore, distinctName);
    }
}
