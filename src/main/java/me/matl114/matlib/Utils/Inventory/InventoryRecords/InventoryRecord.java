package me.matl114.matlib.Utils.Inventory.InventoryRecords;

import me.matl114.matlib.Common.Lang.Annotations.Experimental;
import me.matl114.matlib.Common.Lang.Annotations.ForceOnMainThread;
import me.matl114.matlib.Common.Lang.Annotations.Note;
import org.bukkit.Location;
import org.bukkit.entity.Player;
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

    @Note("force on main when vanillaInv = true")
    public void setChange();
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
    @Experimental
    public Inventory getInventorySync();
    //todo I have a big big big idea about this: fast access cache,
    //first we need a ensureDelaySyncRunner class
    @ForceOnMainThread
    public boolean canPlayerOpen(Player p);
}
