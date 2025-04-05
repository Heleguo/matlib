package me.matl114.matlib.nmsMirror.server;

import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;

public interface MinecraftServerHelper {
    @MethodTarget(isStatic = true)
    Object getServer();
}
