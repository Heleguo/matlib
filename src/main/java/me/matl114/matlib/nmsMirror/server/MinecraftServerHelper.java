package me.matl114.matlib.nmsMirror.server;

import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;

import static me.matl114.matlib.nmsMirror.Import.*;

@Descriptive(target = "net.minecraft.server.MinecraftServer")
public interface MinecraftServerHelper extends TargetDescriptor {
    @MethodTarget(isStatic = true)
    Object getServer();

    @MethodTarget
    void sendSystemMessage(Object server, @RedirectType(ChatComponent)Iterable<?> components);

    @MethodTarget
    Object getPlayerList(Object server);
}
