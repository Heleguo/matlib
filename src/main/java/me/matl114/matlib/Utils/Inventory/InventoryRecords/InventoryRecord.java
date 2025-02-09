package me.matl114.matlib.Utils.Inventory.InventoryRecords;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public interface InventoryRecord {
    public Location invLocation();
    public Inventory inventory();
    public InventoryHolder optionalHolder();
    public boolean isSlimefunInv();
    public boolean isVanillaInv();
    public boolean stillValid();
    public boolean hasData();
    public Inventory getInventorySync();
}
