package me.matl114.matlib.unitTest;

import me.matl114.matlib.utils.Debug;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class TestListeners implements Listener {
    //@EventHandler
    public void checkClose(InventoryCloseEvent paper){
        Debug.logger("close Inventory here",paper.getInventory().getClass());
    }
   // @EventHandler
    public void checkOpen(InventoryOpenEvent paper){
        Debug.logger("open Inventory here",paper.getInventory().getClass());
    }
}
