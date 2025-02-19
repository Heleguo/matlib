package me.matl114.matlib.SlimefunUtils.BlockInventory.Records;

import me.matl114.matlib.Utils.Inventory.InventoryRecords.InventoryRecord;
import me.matl114.matlib.Utils.Inventory.InventoryRecords.SimpleInventoryRecord;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;

public record OldSlimefunInventoryRecord(@Nonnull Inventory inventory,@Nonnull BlockMenu optionalHolder) implements InventoryRecord {
    @Override
    public Location invLocation() {
        return optionalHolder.getLocation();
    }

    @Override
    public boolean isSlimefunInv() {
        return true;
    }

    @Override
    public boolean isVanillaInv() {
        return false;
    }

    @Override
    public boolean isMultiBlockInv(){
        return false;
    }

    @Override
    public boolean stillValid() {
        return BlockStorage.getInventory(optionalHolder.getLocation())==optionalHolder;
    }

    @Override
    public Inventory getInventorySync() {
        return inventory;
    }

    @Override
    public boolean hasData(){
        return BlockStorage.hasBlockInfo(optionalHolder.getLocation());
    }

    public boolean canPlayerOpen(Player p){
        return optionalHolder!=null && optionalHolder.canOpen(optionalHolder.getBlock(),p);
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
