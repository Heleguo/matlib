package me.matl114.matlib.Utils.Inventory.InventoryRecords;

import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

public record OldSlimefunInventoryRecord(Inventory inventory, BlockMenu optionalHolder) implements InventoryRecord {
    @Override
    public Location invLocation() {
        return optionalHolder.getLocation();
    }

    @Override
    public boolean isSlimefunInv() {
        return optionalHolder != null;
    }

    @Override
    public boolean isVanillaInv() {
        return false;
    }

    @Override
    public boolean stillValid() {
        return optionalHolder!=null && BlockStorage.getInventory(optionalHolder.getLocation())==optionalHolder;
    }

    @Override
    public Inventory getInventorySync() {
        return inventory;
    }

    @Override
    public boolean hasData(){
        return BlockStorage.hasBlockInfo(optionalHolder.getLocation());
    }

    public static InventoryRecord getInventoryRecord(Location loc,boolean checkVanilla){
        BlockMenu inv = BlockStorage.getInventory(loc);
        if(inv != null){
            return new OldSlimefunInventoryRecord(inv.toInventory(),inv);
            //also contains vanilla inventory with Slimefun Item
        }
        return checkVanilla? SimpleInventoryRecord.getInventoryRecord(loc):new SimpleInventoryRecord(null,null,loc);

    }
}
