package me.matl114.matlib.nmsMirror.server;

import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;

import static me.matl114.matlib.nmsMirror.Import.*;

@Descriptive(target = "net.minecraft.server.players.PlayerList")
public interface PlayerListHelper extends TargetDescriptor {
//    @MethodTarget
//    void broadcastMessage(Object list, @RedirectType("["+ChatComponent)Iterable<?>... chatcomps);

    @MethodTarget
    void broadcastSystemMessage(Object list, @RedirectType(ChatComponent)Iterable<?> comp, boolean overlay);
}
