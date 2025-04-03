package me.matl114.matlib.utils.command.interruption;

import org.bukkit.command.CommandSender;

public abstract class ArgumentException extends RuntimeException{
    public ArgumentException(){
        super();
    }
    public abstract void handleAbort(CommandSender sender, InterruptionHandler command);
}
