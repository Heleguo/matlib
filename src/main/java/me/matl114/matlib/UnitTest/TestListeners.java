package me.matl114.matlib.UnitTest;

import io.papermc.paper.event.player.PlayerFailMoveEvent;
import me.matl114.matlib.Utils.Debug;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.PlayerInventory;

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
