package me.matl114.matlib.implement.custom.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface InventoryFactory<T> {
    InventoryBuilder<T> visitBuilder(ScreenBuilder builder);

    public interface InventoryBuilder<T>{
        void visitPage(int pageIndex, int sizePerPage, int currentMaxPage);
        void visitSlot(int index, ItemStack stack, InteractHandler handler);
        void visitEnd();
        T getResult();
        void open(Player player);
        Inventory getInventory();
    }
}
