package me.matl114.matlib.implement.custom.inventory;

import lombok.NonNull;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.SimpleLinkList;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.Stack;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class ScreenHistoryStackImpl implements Listener, ScreenHistoryStack {

    Map<UUID, Stack<Pair<Screen, Integer>>> historyStack;

    public ScreenHistoryStackImpl(){
        this.historyStack = new ConcurrentHashMap<>();
    }

    public void cleanPlayerHistory(Player player){
        historyStack.remove(player.getUniqueId());
    }
    @NonNull
    public Stack<Pair<Screen, Integer>> getPlayerHistory(Player player){
        return this.historyStack.computeIfAbsent(player.getUniqueId(), (i)->new SimpleLinkList<>());
    }

    public void cleanHistoryWhenLeave(PlayerQuitEvent event){
        cleanPlayerHistory(event.getPlayer());
    }

    /**
     * return whether there are histories
     * @param player
     * @return
     */
    public boolean goBackToLast(InventoryBuilder.InventoryFactory factory, Player player){
        var stack = getPlayerHistory(player);
        stack.poll();
        //the top should be the history
        var pair = stack.peek();
        if(pair != null){
            pair.getA().openPage(factory, player, pair.getB());
            return true;
        }else {
            return false;
        }
    }

    public void pushNew(Screen screen, Player player, int page){
        var stack = getPlayerHistory(player);
        stack.push(Pair.of(screen, page));
    }

    public void switchTopPage(Screen screen, Player player, int page){
        var stack = getPlayerHistory(player);
        var pair = stack.peek();
        if(pair != null && pair.getA() == screen){
            pair.setB(page);
        }
    }

    public void openWithHistoryClear(InventoryBuilder.InventoryFactory screenType, Player player, int page, Screen screen){
        this.cleanPlayerHistory(player);
        screen.openPageWithHistory(screenType, player, page);
    }

    public void openLastOrCreate(InventoryBuilder.InventoryFactory screenType, Player player, Supplier<Screen> screenSupplier){
        if(!openLast(screenType, player)) {
            Screen screen = screenSupplier.get().relateToHistory(this);
            screen.openPageWithHistory(screenType, player, 1);
        }
    }

    public boolean openLast(InventoryBuilder.InventoryFactory screenType, Player player){
        var stack = getPlayerHistory(player);
        var history = stack.peek();
        if(history != null){
            history.getA().openPage(screenType, player, history.getB());
            return true;
        }else return false;
    }


}
