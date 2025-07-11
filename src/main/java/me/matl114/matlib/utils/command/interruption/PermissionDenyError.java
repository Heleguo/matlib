package me.matl114.matlib.utils.command.interruption;

import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;

import java.util.Optional;

@AllArgsConstructor
public class PermissionDenyError extends ArgumentException {
    String permission;
    Optional<String> commandName;

    @Override
    public void handleAbort(CommandSender sender, InterruptionHandler command) {
        command.handlePermissionDenied(sender, permission, commandName.orElse(null));
    }
}
