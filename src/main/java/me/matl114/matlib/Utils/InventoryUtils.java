package me.matl114.matlib.Utils;

import me.matl114.matlib.Algorithms.DataStructures.Frames.InitializeSafeProvider;
import me.matl114.matlib.Common.Lang.Annotations.ForceOnMainThread;
import me.matl114.matlib.Common.Lang.Annotations.Note;
import me.matl114.matlib.core.EnvironmentManager;
import org.bukkit.block.Container;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Lectern;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class InventoryUtils {
    private static final InventoryType PAPER_DECORATED_POT_TYPE = new InitializeSafeProvider<>(()->InventoryType.valueOf("DECORATED_POT"),null).v();
    @Note(value = "check if Inventory type safe enough to setItem and getItem,some inventory are too weird")
    public static boolean isInventoryTypeCommon(InventoryType inventoryType){
        return inventoryType!=InventoryType.CHISELED_BOOKSHELF && inventoryType!=InventoryType.JUKEBOX && inventoryType != InventoryType.COMPOSTER && inventoryType != PAPER_DECORATED_POT_TYPE;
    }
    @Note(value = "check if Inventory type commonly async safe,when return false,this type of inventory will 100% trigger block update,others will be safe in most time(still cause block update when redstone comparator is near,but inventory changes will keep)")
    public static boolean isInventoryTypeAsyncSafe(InventoryType inventoryType){
        return inventoryType!=InventoryType.LECTERN && isInventoryTypeCommon(inventoryType);
    }
    @ForceOnMainThread
    public static boolean canBlockInventoryOpenToPlayer(Inventory inventory){
        //should run on Primary thread
        InventoryHolder holder = inventory.getHolder();
        return canBlockInventoryOpenToPlayer(holder);
    }

    public static boolean isBlockInventory(InventoryHolder inventoryHolder){
        return inventoryHolder instanceof BlockInventoryHolder || inventoryHolder instanceof DoubleChest;
    }
    public static boolean canBlockInventoryOpenToPlayer(InventoryHolder holder){
        return holder instanceof Container || holder instanceof Lectern || holder instanceof DoubleChest || !(holder instanceof BlockInventoryHolder);
    }
}
