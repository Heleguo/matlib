package me.matl114.matlib.utils.command.interruption;

import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;

public interface InterruptionHandler {
    public void handleTypeError(CommandSender sender, String argument, TypeError.BaseArgumentType type, String input);
    public void handleValueAbsent(CommandSender sender,@Nonnull String argument);
}
