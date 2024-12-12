package me.matl114.matlib.Utils.ItemCache;

import org.bukkit.inventory.ItemStack;

public interface AbstractItemStack {
    //under this interface should itemstack clone to get an instance before they do sth
    public <T extends ItemStack> T copy();
}
