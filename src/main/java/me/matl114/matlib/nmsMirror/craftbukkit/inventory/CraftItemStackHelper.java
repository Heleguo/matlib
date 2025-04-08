package me.matl114.matlib.nmsMirror.craftbukkit.inventory;

import me.matl114.matlib.nmsMirror.Import;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.nmsMirror.impl.EmptyEnum;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.version.Version;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

@Descriptive(target = "org.bukkit.craftbukkit.inventory.CraftItemStack")
public interface CraftItemStackHelper extends TargetDescriptor {
    @ConstructorTarget
    ItemStack createCraftItemStack(Material type, int amount, short durability, ItemMeta meta);
    default ItemStack createCraftItemStack(Material type, int amount, ItemMeta meta){
        return createCraftItemStack(type, amount, (short) 0, meta);
    }
    default ItemStack createCraftItemStack(Material type, int amount){
        return createCraftItemStack(type, amount, null);
    }
    @FieldTarget
    @RedirectType(Import.ItemStack)
    Object handleGetter(ItemStack cis);

    @MethodTarget(isStatic = true)
    @RedirectName("unwrap")
    @Note("copy only when bukkit is not a craftItemStack")
    @IgnoreFailure(thresholdInclude = Version.v1_20_R2, below = true)
    default Object unwrapToNMS(ItemStack bukkit){
        Debug.logger("Default Impl called");
        if(getTargetClass().isInstance(bukkit)){
            Object nms = handleGetter(bukkit);
            return nms == null? EmptyEnum.EMPTY_ITEMSTACK: nms;
        }else {
            return asNMSCopy(bukkit);
        }
    }

    @MethodTarget(isStatic = true)
    @Note("create a nmsItem copy")
    Object asNMSCopy(ItemStack origin);

    @MethodTarget(isStatic = true)
    @Note("copy nmsItem with amount")
    Object copyNMSStack(@RedirectType(Import.ItemStack) Object original, int amount);

    @MethodTarget(isStatic = true)
    @Note("create a strictly-Bukkit stack(or same as craftMirror upper 1_20_R4)")
    ItemStack asBukkitCopy(@RedirectType(Import.ItemStack) Object original);

    @MethodTarget(isStatic = true)
    @Note("create a CraftItemStack mirroring nmsItemStack")
    ItemStack asCraftMirror(@RedirectType(Import.ItemStack) Object original);

    @MethodTarget(isStatic = true)
    @Note("create a CraftItemStack copying original")
    ItemStack asCraftCopy(ItemStack original);

    @MethodTarget(isStatic = true)
    @Note("new CraftItemStack using Item Registry")
    ItemStack asNewCraftStack(@RedirectType(Import.Item)Object item, int amount);

    @MethodTarget(isStatic = true)
    @Note("translate Enchantment nbt to bukkit, ImmutableMap")
    Map<Enchantment, Integer> getEnchantments(@RedirectType(Import.ItemStack) Object item);

    @MethodTarget(isStatic = true)
    boolean hasItemMeta(@RedirectType(Import.ItemStack)Object nmsItem);

    @MethodTarget(isStatic = true)
    @Note("create empty CompoundTag if absent, useless when upper 1_20_R4")
    boolean makeTag(@RedirectType(Import.ItemStack) Object nms);
}
