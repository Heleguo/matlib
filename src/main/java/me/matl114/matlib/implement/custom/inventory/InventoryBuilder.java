package me.matl114.matlib.implement.custom.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public interface InventoryBuilder<T>{
    void visitPage(String optionalTitle, int pageIndex, int sizePerPage, int currentMaxPage);
    void visitSlot(int index, ItemStack stack, InteractHandler handler);
    void visitEnd();
    void visitOpen(ScreenOpenHandler handler);
    void  visitClose(ScreenCloseHandler handler);

    T getResult();
    void open(Player player);
    Inventory getInventory();
    public interface InventoryFactory<T,W extends InventoryBuilder<T>> {
        W visitBuilder(ScreenBuilder builder);


    }

}
