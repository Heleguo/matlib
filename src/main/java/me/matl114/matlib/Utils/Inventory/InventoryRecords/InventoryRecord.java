package me.matl114.matlib.Utils.Inventory.InventoryRecords;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;


public interface InventoryRecord {
    /**
     * get the Location, nonnull
     * @return
     */
    public Location invLocation();

    /**
     * get the optional inventory ,it might be null, which means there is no inventory
     * @return
     */
    public Inventory inventory();

    /**
     * get the optional inventory holder,it might be null, which means there is no data
     * @return
     */
    public InventoryHolder optionalHolder();

    /**
     * check if inventory belongs to a Slimefun Block
     * @return
     */
    public boolean isSlimefunInv();

    /**
     * check if inventory belongs to a BlockState
     * @return
     */
    public boolean isVanillaInv();

    /**
     * check if inventory are made of multi BlockState(DoubleChest)
     * @return
     */
    public boolean isMultiBlockInv();

    /**
     * check if optionalHolder still presents,
     * @return
     */
    public boolean stillValid();

    /**
     * check if data present
     * @return
     */
    public boolean hasData();
    default boolean hasInv(){
        return invLocation() != null;
    }
    /**
     * get Inventory (if needed ,always Sync)
     * @return
     */
    public Inventory getInventorySync();
    //todo I have a big big big idea about this: fast access cache,
    //first we need a ensureDelaySyncRunner class
}
