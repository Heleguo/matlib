package me.matl114.matlib.nmsUtils.inventory;

import lombok.Getter;
import lombok.Setter;
import me.matl114.matlib.nmsMirror.impl.EmptyEnum;
import me.matl114.matlib.nmsUtils.ItemUtils;
import org.bukkit.inventory.ItemStack;

import static me.matl114.matlib.nmsMirror.impl.NMSItem.*;

public class ItemStackCounter implements NMSItemHolder {
    public static final ItemStackCounter EMPTY = new ItemStackCounter(EmptyEnum.EMPTY_ITEMSTACK, 0);
    @Getter
    @Setter
    int amount;
    final Object itemStack;
    public boolean isAir(){
        return ITEMSTACK.isEmpty(this.itemStack);
    }

    @Override
    public <T extends NMSItemHolder> T copy() {
        return (T)new ItemStackCounter(ITEMSTACK.copy(this.itemStack, true), this.amount);
    }

    public static ItemStackCounter of(ItemStack val){
        return (val == null)? EMPTY: new ItemStackCounter(ItemUtils.unwrapHandle(val), val.getAmount());
    }

    public ItemStackCounter(Object val, int amount){
        this.itemStack = val;
        this.amount = amount;
    }

    public ItemStackWrapper newWrapper(){
        return ItemStackWrapper.ofNMS(this.itemStack);
    }

    @Override
    public Object getNMS() {
        return this.itemStack;
    }


}
