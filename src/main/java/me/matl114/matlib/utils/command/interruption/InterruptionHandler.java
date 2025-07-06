package me.matl114.matlib.utils.command.interruption;

import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface InterruptionHandler {
    public void handleTypeError(CommandSender sender,@Nullable String argument, TypeError.BaseArgumentType type, String input);
    public void handleValueAbsent(CommandSender sender,@Nonnull String argument);
    public void handleValueOutOfRange(CommandSender sender, @Nullable String argument, TypeError.BaseArgumentType type, String from, String to, @Nonnull String input);
    public void handleExecutorInvalid(CommandSender sender, boolean shouldConsole);
    public void handleLogicalError(CommandSender sender, String fullMessage);
}
