package me.matl114.matlib.Utils.Inventory.InventoryRecords;

import com.google.common.base.Preconditions;
import me.matl114.matlib.Utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import javax.annotation.Nonnull;

public record SimpleInventoryRecord<T extends TileState & InventoryHolder>(Inventory inventory, T optionalHolder, Location invLocation) implements InventoryRecord {
    //this class records a Location 's Inventory Info,whether it is sf or vanilla, and records sth about holder
    @Override
    public boolean isSlimefunInv(){
        return false;
    }

    @Override
    public boolean isVanillaInv(){
        return optionalHolder != null;
    }

    @Override
    public boolean isMultiBlockInv() {
        return false;
    }

    @Override
    public boolean stillValid(){
        return inventory != null && optionalHolder!=null &&  WorldUtils.isTileEntityStillValid(optionalHolder);
//        if( inventory == null ) return false;
//        if( ){
//            return WorldUtils.isTileEntityStillValid(tile);
//        }else {
//            //this is vanilla ,this is not a valid inventory
//            return false;
//        }
    }

    @Override
    public Inventory getInventorySync(){
        if(inventory != null){
            return inventory;
        }else {
            Preconditions.checkArgument(Bukkit.isPrimaryThread());
            return optionalHolder.getInventory();
        }
    }

    @Override
    public boolean hasData() {
        return optionalHolder != null;
    }

    public boolean canPlayerOpen(Player p){
        return optionalHolder!=null && WorldUtils.canBlockInventoryOpenToPlayer(optionalHolder);
    }
    //todo need check of double chest
    @Nonnull
    public static InventoryRecord getInventoryRecord(Location loc) {
        return getInventoryRecord(loc,false);
    }
    @Nonnull
    public static InventoryRecord getInventoryRecord(Location loc,boolean forceOnMain) {
        //should force Sync
        Block b = loc.getBlock();
        if(WorldUtils.getBlockStateNoSnapShot(b) instanceof InventoryHolder holder && holder instanceof TileState state){
            Inventory inventory = holder.getInventory();
            if((forceOnMain)||WorldUtils.isInventoryTypeAsyncSafe(inventory.getType())){
                return inventory instanceof DoubleChestInventory chestchest? DoubleStateInventoryRecord.ofDoubleChest(chestchest): new SimpleInventoryRecord(inventory,state,loc);
            }else{
                return new SimpleInventoryRecord(null,state,loc);
            }
        }
        return new SimpleInventoryRecord(null, null, loc);
    }
}
