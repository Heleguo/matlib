package me.matl114.matlib.implement.custom.inventory;

import me.matl114.matlib.algorithms.dataStructures.frames.collection.SimpleLinkList;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.Stack;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ScreenHistoryStack {

    Map<UUID, Stack<Pair<Screen, Integer>>> historyStack;

    public ScreenHistoryStack(){
        this.historyStack = new ConcurrentHashMap<>();
    }

    public void onPlayerLeave(Player player){
        historyStack.remove(player.getUniqueId());
    }
    public Stack<Pair<Screen, Integer>> getPlayerHistory(Player player){
        return this.historyStack.computeIfAbsent(player.getUniqueId(), (i)->new SimpleLinkList<>());
    }

    /**
     * return whether there are histories
     * @param player
     * @return
     */
    public boolean back(InventoryBuilder.InventoryFactory factory, Player player){
        var stack = getPlayerHistory(player);
        var pair = stack.poll();
        if(pair != null){
            pair.getA().openPage(factory, player, pair.getB());
            return true;
        }else {
            return false;
        }
    }

    public void openFrom(Screen screen, Player player, int page){
        var stack = getPlayerHistory(player);
        stack.push(Pair.of(screen, page));
    }

}
