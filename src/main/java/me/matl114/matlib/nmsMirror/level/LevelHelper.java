package me.matl114.matlib.nmsMirror.level;

import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.FieldTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.checkerframework.checker.units.qual.N;

import java.util.List;

import static me.matl114.matlib.nmsMirror.Import.*;

@Descriptive(target = "net.minecraft.world.level.Level")
public interface LevelHelper extends TargetDescriptor {
    @MethodTarget
    World getWorld(Object level);

    @MethodTarget
    @Note("Return a levelChunk if it is fully loaded, else return null")
    Object getChunkIfLoaded(Object level, int chunkX, int chunkZ);

    @MethodTarget
    @Note("return a ServerChunkCache")
    Object getChunkSource(Object level);

    @MethodTarget
    @Note("same as getChunkSource.getChunkIfLoadedImmediately")
    Object getChunkIfLoadedImmediately(Object level, int chunkx, int chunkz);

    @MethodTarget
    @Note("same as getChunkSource.getChunk(x,z,FULL,true)")
    Object getChunk(Object level, int chunkX, int chunkZ);

    @MethodTarget
    BlockState getBlockState(Object level,@RedirectType(BlockPos) Object pos);


    @FieldTarget
    @RedirectType("Ljava/util/List;")
    List<?> blockEntityTickersGetter(Object chunk);

    @MethodTarget
    void moonrise$midTickTasks(Object chunk);
    //addEntity
}
