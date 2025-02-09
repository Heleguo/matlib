package me.matl114.matlib.Utils.Inventory.InventoryRecords;

import com.google.common.base.Preconditions;
import me.matl114.matlib.Utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import javax.annotation.Nonnull;

public record SimpleInventoryRecord(Inventory inventory, InventoryHolder optionalHolder, Location invLocation) implements InventoryRecord {
    //this class records a Location 's Inventory Info,whether it is sf or vanilla, and records sth about holder
    @Override
    public boolean isSlimefunInv(){
        return false;
    }

    @Override
    public boolean isVanillaInv(){
        return true;
    }

    @Override
    public boolean stillValid(){
        return inventory != null && (optionalHolder instanceof TileState);
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

    @Nonnull
    static InventoryRecord getInventoryRecord(Location loc) {
        //should force Sync
        Block b = loc.getBlock();
        if(WorldUtils.getBlockStateNoSnapShot(b) instanceof InventoryHolder holder){
            Inventory inventory = holder.getInventory();
            if(WorldUtils.isInventoryTypeAsyncSafe(inventory.getType())){
                return new SimpleInventoryRecord(inventory,holder,loc);
            }else{
                return new SimpleInventoryRecord(null,holder,loc);
            }
        }
        return new SimpleInventoryRecord(null, null, loc);
    }
}
