package me.matl114.matlib.nmsUtils.inventory;

import me.matl114.matlib.nmsUtils.ItemUtils;
import org.bukkit.inventory.ItemStack;

public class ItemStackKey extends ItemStack implements ItemHashCache {
    ItemStack handled;
    Integer hashCode;
    Integer hashCodeNoLore;
    protected ItemStackKey(ItemStack cis){
        super(cis);
        this.handled = cis;
    }
    public static ItemStackKey of(ItemStack item){
        if(item == null)return null;
        ItemStack cis = ItemUtils.cleanStack(item);
        return new ItemStackKey(cis);
    }
    public int getHashCode(){
        if(hashCode == null){
            hashCode =  ItemUtils.itemStackHashCode(handled);
        }
        return hashCode;
    }
    public int getHashCodeNoLore(){
        if(hashCodeNoLore == null){
            hashCodeNoLore = ItemUtils.itemStackHashCodeWithoutLore(handled);
        }
        return hashCodeNoLore;
    }

    @Override
    public ItemStack getCraftStack() {
        return this.handled;
    }
}
