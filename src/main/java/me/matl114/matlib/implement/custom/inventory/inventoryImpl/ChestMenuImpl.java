package me.matl114.matlib.implement.custom.inventory.inventoryImpl;

import com.google.common.base.Preconditions;
import me.matl114.matlib.implement.custom.inventory.*;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChestMenuImpl implements InventoryBuilder<ChestMenu> {
    ChestMenu menu;
    ScreenBuilder builder;
    int page;


    @Override
    public void visitPage(ScreenBuilder builder, String optionalTitle, int pageIndex, int sizePerPage, int currentMaxPage) {
        this.builder = builder;
        this.menu = new ChestMenu(optionalTitle, sizePerPage);
        this.page = pageIndex;
    }

    @Override
    public void visitSlot(int index, ItemStack stack, InteractHandler handler) {
        Preconditions.checkNotNull(menu, "You should visitPage before visitSlot");
        menu.replaceExistingItem(index, stack);
        menu.addMenuClickHandler(index, wrap( handler));
    }

    public static ChestMenu.MenuClickHandler wrap(InteractHandler handler){
        return new ChestMenu.AdvancedMenuClickHandler(){

            @Override
            public boolean onClick(Player player, int i, ItemStack itemStack, ClickAction clickAction) {
                throw new UnsupportedOperationException("Do not call");
            }

            @Override
            public boolean onClick(InventoryClickEvent inventoryClickEvent, Player player, int i, ItemStack itemStack, ClickAction clickAction) {
                return handler != null && handler.onClick(inventoryClickEvent.getClickedInventory(), player, inventoryClickEvent);
            }
        };
    }




    @Override
    public void visitEnd() {
        Preconditions.checkNotNull(menu, "You should visitPage before visitEnd");
    }

    @Override
    public void visitOpen(ScreenOpenHandler handler) {
        Preconditions.checkNotNull(menu, "You should visitPage before visitOpen");
        if(handler != null)
            menu.addMenuOpeningHandler((p)->handler.handleOpen(p, menu.getInventory()));
    }

    @Override
    public void visitClose(ScreenCloseHandler handler) {
        Preconditions.checkNotNull(menu, "You should visitPage before visitClose");
        if(handler != null)
            menu.addMenuCloseHandler((p)->handler.handleClose(p, menu.getInventory()));
    }

    @Override
    public ScreenBuilder getBuilder() {
        return this.builder;
    }

    @Override
    public int getPage() {
        return this.page;
    }

    @Override
    public ChestMenu getResult() {
        return menu;
    }

    @Override
    public void openInternal(Player player) {
        menu.open(player);
    }


    @Override
    public Inventory getInventory() {
        return menu.getInventory();
    }

    @Override
    public InventoryFactory<ChestMenu, ? extends InventoryBuilder<ChestMenu>> getFactory() {
        return FACTORY;
    }

    public static InventoryFactory<ChestMenu, ChestMenuImpl> FACTORY = new Factory();

    private static class Factory implements InventoryFactory<ChestMenu, ChestMenuImpl>{

        @Override
        public ChestMenuImpl visitBuilder(ScreenBuilder builder) {
            return new ChestMenuImpl();
        }
    }
}
