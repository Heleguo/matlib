package me.matl114.matlib.Utils.Chat;

import me.matl114.matlib.Common.Lang.Annotations.Note;
import me.matl114.matlib.Utils.Command.CommandGroup.AbstractMainCommand;
import me.matl114.matlibAdaptor.Implements.Bukkit.InputManager;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * when it is marked as "Async" it is not pure async, in a word, it calls command in every thread
 */
@Note("the \"async\" means that it can be called either on main or off main")
public class AsyncCommandTerminal extends ChatTerminal{
    public AbstractMainCommand handle;

    public AsyncCommandTerminal(Player owner, AbstractMainCommand handle) {
        super(owner);
        this.handle = handle;
    }

    @Override
    public boolean onExecution(Player player, String label, Set<Player> recipients) {
        String[] commands = label.strip().split(" ");
        player.sendMessage("§fTerminal>> "+String.join(" ", commands));
        if(commands.length > 0){
            this.handle.onCommandAsync(player, null, "terminal", commands);
        }
        return true;
    }
    public void onExit(){
        super.onExit();
        owner.sendMessage("§fTerminal>> Exiting terminal ");

    }
    public void onQuit(){
        super.onQuit();
        owner.sendMessage("§fTerminal>> Quiting terminal ");

    }

    @Override
    public void onEnable(InputManager inputManager) {
        super.onEnable(inputManager);
        owner.sendMessage("§fTerminal>> Entering command terminal ");
    }
}
