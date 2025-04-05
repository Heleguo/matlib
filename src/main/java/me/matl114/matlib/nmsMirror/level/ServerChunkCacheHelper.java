package me.matl114.matlib.nmsMirror.level;

import me.matl114.matlib.common.lang.annotations.NotRecommended;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;

import java.util.concurrent.CompletableFuture;

import static me.matl114.matlib.nmsMirror.Import.*;

@Descriptive(target = "net.minecraft.server.level.ServerChunkCache")
public interface ServerChunkCacheHelper extends TargetDescriptor {
    @MethodTarget
    @NotRecommended
    @Note("Return a chunkAccess ,may not present")
    Object getChunkAtImmediately(Object cache,  int x, int z);

    @MethodTarget
    @Note("return a LevelChunk")
    Object getChunkAtIfLoadedImmediately(Object cache, int x, int z);

    @MethodTarget
    @NotRecommended
    Object getChunkNow(Object cache, int x, int z);

    @MethodTarget
    @Nullable
    @Note("when createIfNoExist, will force loading target Chunk if chunk is not loaded")
    @Contract("_,_,FULL,_ -> LevelChunk")
    Object getChunk(Object obj, int x, int z, @RedirectType(ChunkStatus)Object leastStatus, boolean createIfNoExist);

    @MethodTarget
    CompletableFuture<?> getChunkFuture(Object cache, int chunkX, int chunkZ, @RedirectType(ChunkStatus)Object leastStatus, boolean createIfNoExist);

    @MethodTarget
    Object getLevel(Object cache);


}
