package me.matl114.matlib.implement.custom.inventory;

import org.bukkit.entity.Player;

import java.util.function.Supplier;

public interface ScreenHistoryStack {
    public void cleanPlayerHistory(Player player);

    public boolean goBackToLast(InventoryBuilder.InventoryFactory factory, Player player);

    public void pushNew(Screen screen, Player player, int page);

    public void switchTopPage(Screen screen, Player player, int page);

    public void openWithHistoryClear(InventoryBuilder.InventoryFactory screenType, Player player, int page, Screen screen);

    public boolean openLast(InventoryBuilder.InventoryFactory screenType, Player player);

    public void openLastOrCreate(InventoryBuilder.InventoryFactory screenType, Player player, Supplier<Screen> screenSupplier);

    static ScreenHistoryStack of(){
        return new ScreenHistoryStackImpl();
    }
}
