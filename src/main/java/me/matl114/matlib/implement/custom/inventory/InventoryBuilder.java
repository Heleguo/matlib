package me.matl114.matlib.implement.custom.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;


public interface InventoryBuilder<T>{
    /**
     * basic information setup
     * @param builder
     * @param optionalTitle
     * @param pageIndex
     * @param sizePerPage
     * @param currentMaxPage
     */
    void visitPage(ScreenBuilder builder,@Nullable String optionalTitle, int pageIndex, int sizePerPage, int currentMaxPage);

    /**
     * set slot content
     * @param index
     * @param stack
     * @param handler
     */
    void visitSlot(int index,@Nullable ItemStack stack,@Nullable InteractHandler handler);

    /**
     * end of any
     */
    void visitEnd();

    /**
     * set open handler
     * @param handler
     */
    void visitOpen(@Nullable ScreenOpenHandler handler);

    /**
     * set close handler
     * @param handler
     */
    void  visitClose(@Nullable ScreenCloseHandler handler);

    /**
     * must return the instance passed in the "visitPage()"
     * @return
     */
    ScreenBuilder getBuilder();

    /**
     * must return the value passed in the "visitPage()"
     * @return
     */
    int getPage();

    /**
     * must be present after visitEnd
     * @return
     */
    T getResult();
    default void open(Player player){
        player.openInventory(getInventory());
    }

    /**
     * must be present after visitEnd
     * @return
     */
    Inventory getInventory();

    /**
     * factory used for creating this
     * @return
     */
    InventoryFactory<T,? extends InventoryBuilder<T>> getFactory();


    public interface InventoryFactory<T,W extends InventoryBuilder<T>> {
        W visitBuilder(ScreenBuilder builder);
    }

}
