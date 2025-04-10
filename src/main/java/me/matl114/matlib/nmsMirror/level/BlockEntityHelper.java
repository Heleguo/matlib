package me.matl114.matlib.nmsMirror.level;

import me.matl114.matlib.common.lang.annotations.ForceOnMainThread;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.nmsMirror.impl.CraftBukkit;
import me.matl114.matlib.nmsMirror.interfaces.PdcCompoundHolder;
import me.matl114.matlib.utils.reflect.classBuild.annotation.IgnoreFailure;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.version.Version;
import org.bukkit.persistence.PersistentDataContainer;

import static me.matl114.matlib.nmsMirror.Import.*;

@Descriptive(target = "net.minecraft.world.level.block.entity.BlockEntity")
public interface BlockEntityHelper extends TargetDescriptor , PdcCompoundHolder {
    @FieldTarget
//    @RedirectType(CraftPersistentDataContainer)
    PersistentDataContainer persistentDataContainerGetter(Object be);

    default Object getPersistentDataCompound(Object val, boolean create){
        PersistentDataContainer container = persistentDataContainerGetter(val);
        return CraftBukkit.PERSISTENT_DATACONTAINER.asCompoundMirror(container);
    }

    @MethodTarget
    boolean isRemoved(Object be);

    @MethodTarget
    void setRemoved(Object be);

    @MethodTarget
    void clearRemoved(Object be);

    @MethodTarget
    @ForceOnMainThread
    void setChanged(Object be);

    @MethodTarget
    Object getBlockPos(Object be);

    @MethodTarget
    Object getBlockState(Object be);

    @MethodTarget
    Object getType(Object be);

    @Note("full nbt without pos information")
    @MethodTarget
    @IgnoreFailure(thresholdInclude = Version.v1_20_R4)
    Object saveWithId(Object be);

    @Note("full nbt")
    @MethodTarget
    @IgnoreFailure(thresholdInclude = Version.v1_20_R4)
    Object saveWithFullMetadata(Object be);

    @MethodTarget(isStatic = true)
    Object loadStatic(@RedirectType(BlockPos)Object pos, @RedirectType(BlockState)Object state, @RedirectType(CompoundTag) Object nbt);

    @MethodTarget
    void load(Object entity, @RedirectType(CompoundTag)Object rewritingNBT);
}
