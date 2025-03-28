package me.matl114.matlib.Utils.Command.Interruption;

import me.matl114.matlib.Utils.Command.CommandGroup.AbstractMainCommand;
import org.bukkit.command.CommandSender;

public abstract class ArgumentException extends RuntimeException{
    public ArgumentException(){
        super();
    }
    public abstract void handleAbort(CommandSender sender, InterruptionHandler command);
}
