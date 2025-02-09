package me.matl114.matlib.Utils.Inventory;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import me.matl114.matlib.Implements.Managers.BlockDataCache;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

public record SlimefunInventoryRecord(Inventory inventory, BlockMenu optionalHolder, SlimefunBlockData data) implements InventoryRecord {
    @Override
    public Location invLocation() {
        return data.getLocation();
    }

    @Override
    public boolean isSlimefunInv() {
        return inventory != null;
    }

    @Override
    public boolean isVanillaInv() {
        return false;
    }

    @Override
    public boolean stillValid() {
        return inventory!=null && !data.isPendingRemove();
    }

    @Override
    public Inventory getInventorySync() {
        return inventory;
    }

    @Override
    public boolean hasData(){
        return data != null;
    }

    public static InventoryRecord getInventoryRecord(Location loc,boolean checkVanilla){
        SlimefunBlockData data = BlockDataCache.getManager().safeGetBlockDataFromCache(loc);
        if(data != null){
            BlockMenu inv = data.getBlockMenu();
            if(inv != null){
                return new SlimefunInventoryRecord(inv.toInventory(),inv,data);
            }
            //also contains vanilla inventory with Slimefun Item
        }
        return checkVanilla?SimpleInventoryRecord.getInventoryRecord(loc):new SimpleInventoryRecord(null,null,loc);

    }
}
