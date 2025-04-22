package me.matl114.matlib.nmsUtils;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.matl114.matlib.algorithms.algorithm.MathUtils;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.nmsMirror.core.BuiltInRegistryEnum;
import me.matl114.matlib.nmsMirror.craftbukkit.persistence.CraftPersistentDataContainerHelper;
import me.matl114.matlib.nmsMirror.impl.CraftBukkit;
import me.matl114.matlib.nmsMirror.impl.EmptyEnum;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.nmsMirror.impl.NMSItem;
import me.matl114.matlib.nmsUtils.nbt.TagCompoundView;
import me.matl114.matlib.utils.CraftUtils;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.VarHandle;
import java.util.List;
import java.util.Map;

import static me.matl114.matlib.nmsMirror.impl.NMSItem.*;
import static me.matl114.matlib.nmsMirror.impl.NMSCore.*;

public class ItemUtils {

    public static TagCompoundView getPersistentDataContainerView(@Nonnull ItemStack craftItemStack, boolean forceCreate){
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

    public static ItemStack cleanStack(@Nullable ItemStack whatever){
        return whatever == null ? null : CraftBukkit.ITEMSTACK.getCraftStack(whatever);
    }
    public static ItemStack copyStack(@javax.annotation.Nullable ItemStack whatever){
        return whatever == null ? null : CraftBukkit.ITEMSTACK.asCraftCopy(whatever);
    }
    public static Object unwrapHandle(@Nonnull ItemStack it){
        return CraftBukkit.ITEMSTACK.unwrapToNMS(it);
    }
    public static Object getHandle(@Nonnull ItemStack cis){
        return CraftBukkit.ITEMSTACK.handleGetter(cis);
    }
    public static boolean matchItemStack(@Nullable ItemStack item1,@Nullable ItemStack item2, boolean distinctLore){
        return matchItemStack(item1, item2, distinctLore, true);
    }
    public static boolean matchItemStack(@Nullable ItemStack item1,@Nullable ItemStack item2, boolean distinctLore, boolean distinctName){
        if(item1 == null || item2 == null){
            return item1 == item2;
        }
        var handle1 = CraftBukkit.ITEMSTACK.unwrapToNMS(item1);
        var handle2 = CraftBukkit.ITEMSTACK.unwrapToNMS(item2);
        return ITEMSTACK.matchItem(handle1, handle2, distinctLore, distinctName);
    }

    public static ItemStack pushItem(@Note("make sure this is made by craftbukkit, not self-implemented") Inventory craftInventory, ItemStack item, int... slots){
        Preconditions.checkArgument(CraftBukkit.INVENTORYS.isCraftInventory(craftInventory),"Should pass a craft inventory");
        if(item == null || item.getType() == Material.AIR){
            return null;
        }
        var content = CraftBukkit.INVENTORYS.getInventory(craftInventory);
        List<?> lst = CONTAINER.getContents(content);
        var itemNMS = CraftBukkit.ITEMSTACK.unwrapToNMS(item);
        if(itemNMS == EmptyEnum.EMPTY_ITEMSTACK){
            return null;
        }
        int amount = item.getAmount();
        int maxSize = item.getMaxStackSize();
        for (int slot: slots){
            if(amount <= 0){
                break;
            }
            var nmsItem = lst.get(slot);
            //is null or air
            if(ITEMSTACK.isEmpty(nmsItem)){
                int received = Math.min(amount, maxSize);
                var re = ITEMSTACK.split(itemNMS, received);

                   // CraftBukkit.ITEMSTACK.asNMSCopy(item);
                amount -= received;
                CONTAINER.setItem(content, slot, re);
            }else{
                //nmsItem is not EMPTY
                int count = ITEMSTACK.getCount(nmsItem);
                if(count >= maxSize){
                    continue;
                }
                if(!ITEMSTACK.matchItem(nmsItem, itemNMS, false, true)){
                    continue;
                }
                int received = MathUtils.clamp(maxSize - count, 0, amount);
                amount -= received;
                ITEMSTACK.setCount(nmsItem, count + received);
            }
        }
        item.setAmount(amount);
        return amount>0 ? item: null;
    }

    public static void pushItem(@Note("make sure this is made by craftbukkit, not self-implemented") Inventory craftInventory, int[] slots, ItemStack... items){
        Preconditions.checkArgument(CraftBukkit.INVENTORYS.isCraftInventory(craftInventory),"Should pass a craft inventory");
        var content = CraftBukkit.INVENTORYS.getInventory(craftInventory);
        List<?> lst = CONTAINER.getContents(content);
        for(var item: items){
            if(item == null || item.getType() == Material.AIR){
                continue;
            }
            var itemNMS = CraftBukkit.ITEMSTACK.unwrapToNMS(item);
            if(itemNMS == null || itemNMS == EmptyEnum.EMPTY_ITEMSTACK){
                continue;
            }
            int amount = item.getAmount();
            int maxSize = item.getMaxStackSize();
            for (int slot: slots){
                if(amount <= 0){
                    break;
                }
                var nmsItem = lst.get(slot);
                //is null or air
                if(ITEMSTACK.isEmpty(nmsItem)){
                    int received = Math.min(amount, maxSize);
                    var re = ITEMSTACK.split(itemNMS, received);

                    // CraftBukkit.ITEMSTACK.asNMSCopy(item);
                    amount -= received;
                    CONTAINER.setItem(content, slot, re);
                }else{
                    //nmsItem is not EMPTY
                    int count = ITEMSTACK.getCount(nmsItem);
                    if(count >= maxSize){
                        continue;
                    }
                    if(!ITEMSTACK.matchItem(nmsItem, itemNMS, false, true)){
                        continue;
                    }
                    int received = MathUtils.clamp(maxSize - count, 0, amount);
                    amount -= received;
                    ITEMSTACK.setCount(nmsItem, count + received);
                }
            }
            item.setAmount(amount);
        }
    }

    public static ItemStack pushItemWithoutMatch(@Note("make sure this is made by craftbukkit, not self-implemented inv") Inventory craftInventory, ItemStack item, int... slots){
        Preconditions.checkArgument(CraftBukkit.INVENTORYS.isCraftInventory(craftInventory),"Should pass a craft inventory");
        if(item == null || item.getType() == Material.AIR){
            return null;
        }
        var content = CraftBukkit.INVENTORYS.getInventory(craftInventory);
        List<?> lst = CONTAINER.getContents(content);
        var itemNMS = CraftBukkit.ITEMSTACK.unwrapToNMS(item);
        if(itemNMS == EmptyEnum.EMPTY_ITEMSTACK){
            return null;
        }
        int amount = item.getAmount();
        int maxSize = item.getMaxStackSize();
        for (int slot: slots){
            if(amount <= 0){
                break;
            }
            var nmsItem = lst.get(slot);
            //is null or air
            if(ITEMSTACK.isEmpty(nmsItem)){
                int received = Math.min(amount, maxSize);
                var re = ITEMSTACK.split(itemNMS, received);
                // CraftBukkit.ITEMSTACK.asNMSCopy(item);
                amount -= received;
                CONTAINER.setItem(content, slot, re);
            }else{
                //nmsItem is not EMPTY
                int count = ITEMSTACK.getCount(nmsItem);
                if(count >= maxSize){
                    continue;
                }
                int received = MathUtils.clamp(maxSize - count, 0, amount);
                amount -= received;
                ITEMSTACK.setCount(nmsItem, count + received);
            }
        }
        item.setAmount(amount);
        return amount>0 ? item: null;
    }

    public static ItemStack grabItem(@Note("make sure this is made by craftbukkit, not self-implemented inv")Inventory craftInventory, ItemStack item, int requestedAmount, int... slots){
        Preconditions.checkArgument(CraftBukkit.INVENTORYS.isCraftInventory(craftInventory),"Should pass a craft inventory");
        if(item == null || item.getType() == Material.AIR){
            return null;
        }
        var content = CraftBukkit.INVENTORYS.getInventory(craftInventory);
        List<?> lst = CONTAINER.getContents(content);
        Object stackToReturnNMS = null;
        int collected = 0;
        for (int slot: slots){
            if(collected >= requestedAmount){
                break;
            }
            var nms = lst.get(slot);
            if(nms==null || ITEMSTACK.isEmpty(nms)){
                continue;
            }else {
                if(stackToReturnNMS == null){
                    stackToReturnNMS = CraftBukkit.ITEMSTACK.asNMSCopy(item);
                }
                if(ITEMSTACK.matchItem(stackToReturnNMS, nms, false, true)){
                    int count = ITEMSTACK.getCount(nms);
                    int withDraw = Math.min(count, requestedAmount - collected);
                    ITEMSTACK.setCount(nms, count - withDraw);
                    collected += withDraw;
                }
            }
        }
        if(stackToReturnNMS == null) {
            return null;
        }
        ITEMSTACK.setCount(stackToReturnNMS, collected);
        return CraftBukkit.ITEMSTACK.asCraftMirror(stackToReturnNMS);
    }

    public static int itemStackHashCode(@Note("should pass a CraftItemStack for the best") ItemStack craftItemStack){
        var handle = CraftBukkit.ITEMSTACK.unwrapToNMS(craftItemStack);
        return ITEMSTACK.customHashcode(handle);
    }

    public static int itemStackHashCodeWithoutLore(@Note("should pass a CraftItemStack for the best") ItemStack craftItemStack){
        var handle = CraftBukkit.ITEMSTACK.unwrapToNMS(craftItemStack);
        return ITEMSTACK.customHashWithoutDisplay(handle);
    }
}
