package me.matl114.matlib.Utils.Inventory.InventoryRecords;

import com.google.common.base.Preconditions;
import me.matl114.matlib.Utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.TileState;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public record DoubleStateInventoryRecord(DoubleChestInventory inventory,
                                                                                TileState left,TileState right) implements InventoryRecord {
    public static InventoryRecord ofDoubleChest(DoubleChestInventory inventory){
        Location locationLeft = inventory.getLeftSide().getLocation();
        Location locationRight = inventory.getRightSide().getLocation();
        if(locationLeft==null || locationRight==null) return new DoubleStateInventoryRecord(inventory,null,null);
        BlockState stateLeft = WorldUtils.getBlockStateNoSnapShot(locationLeft.getBlock());
        BlockState stateRight = WorldUtils.getBlockStateNoSnapShot(locationRight.getBlock());
        if(stateLeft instanceof TileState tileLeft && stateRight instanceof TileState tileRight){
            return new DoubleStateInventoryRecord(inventory,tileLeft,tileRight);
        }else{
            return new DoubleStateInventoryRecord(inventory,null,null);
        }
    }

    @Override
    public Location invLocation() {
        return inventory.getLocation();
    }

    @Override
    public InventoryHolder optionalHolder() {
        return inventory.getHolder();
    }

    @Override
    public boolean isSlimefunInv() {
        return false;
    }

    @Override
    public boolean isVanillaInv() {
        return true;
    }

    @Override
    public boolean isMultiBlockInv() {
        return true;
    }

    @Override
    public boolean stillValid() {
        return left != null && WorldUtils.isTileEntityStillValid(left) && right != null && WorldUtils.isTileEntityStillValid(right);
    }

    @Override
    public boolean hasData() {
        return optionalHolder() != null;
    }

    @Override
    public Inventory getInventorySync() {
        if(inventory != null){
            return inventory;
        }else {
            Preconditions.checkArgument(Bukkit.isPrimaryThread());
            return left instanceof InventoryHolder lholder? lholder.getInventory():(right instanceof InventoryHolder rholder?rholder.getInventory():null);
        }
    }
}
