package me.matl114.matlib.implement.custom.inventory;

import me.matl114.matlib.common.lang.annotations.Internal;
import me.matl114.matlib.common.lang.annotations.Note;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;
import java.util.function.Predicate;

public interface InteractHandler {
    @Internal
    public boolean onClick(Inventory inventory, Player player, int slotIndex, InventoryAction action, ClickType clickType);
    @Note("Caller will call this method")

    default boolean onClick(Inventory inventory, Player player, InventoryClickEvent clickEvent){
        return onClick(inventory, player, clickEvent.getSlot(), clickEvent.getAction(), clickEvent.getClick());
    }

    public static final InteractHandler EMPTY = (SimpleInteractHandler)((inventory, player, event) -> false);

    public static InteractHandler task(Predicate<Player> p){
        return (SimpleInteractHandler)((inventory, player, event) -> p.test(player));
    }
}
