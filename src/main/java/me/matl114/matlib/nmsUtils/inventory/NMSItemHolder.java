package me.matl114.matlib.nmsUtils.inventory;

import me.matl114.matlib.nmsUtils.ItemUtils;
import org.bukkit.inventory.ItemStack;

import static me.matl114.matlib.nmsMirror.impl.NMSItem.ITEMSTACK;

public interface NMSItemHolder {
    public Object getNMS();
    default boolean hasMeta(){
        return ITEMSTACK.hasExtraData(getNMS());
    }
    default boolean isAir(){
        return ITEMSTACK.isEmpty(getNMS());
    }

    default ItemStack toBukkit(){
        return ItemUtils.asBukkitCopy(getNMS());
    }

    <T extends NMSItemHolder> T copy();


}
