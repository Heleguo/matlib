package me.matl114.matlib.nmsMirror.level;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;

import javax.annotation.Nullable;
import java.util.List;

import static me.matl114.matlib.nmsMirror.Import.*;

@Descriptive(target = "net.minecraft.world.level.block.Block" )
public interface BlockHelper extends TargetDescriptor {
    @MethodTarget
    boolean isDestroyable(Object block);

    @MethodTarget(isStatic = true)
    @Note("not block -> BLOCK_AIR")
    Object byItem(@RedirectType(Item)Object item);

    @MethodTarget
    void destroy(Object block, @RedirectType(LevelAccessor)Object world, @RedirectType(BlockPos)Object pos, @RedirectType(BlockState)Object state);

    @MethodTarget(isStatic = true)
    List<?> getDrops(@RedirectType(BlockState) Object state, @RedirectType(ServerLevel)Object world, @RedirectType(BlockPos) Object pos, @RedirectType(BlockEntity)Object blockEntity);

    @MethodTarget(isStatic = true)
    List<?> getDrops(@RedirectType(BlockState) Object state, @RedirectType(ServerLevel)Object world, @RedirectType(BlockPos) Object pos, @RedirectType(BlockEntity)Object blockEntity, @Nullable @RedirectType(Entity) Object entity, @RedirectType(ItemStack)Object stack);

    @MethodTarget(isStatic = true)
    @Note("break naturally")
    void dropResources(@RedirectType(BlockState)Object state, @RedirectType(Level)Object level, @RedirectType(BlockPos)Object pos, @RedirectType(BlockEntity)Object optionalEntity, @RedirectType(Entity)Object executor, @RedirectType(ItemStack)Object tool, boolean doDropExp);

    @MethodTarget
    float getExplosionResistance(Object block);
    //cost Food exhaustion while destroy
//    @MethodTarget
//    void playerDestroy(@RedirectType(BlockState)Object state, @RedirectType(Level)Object level, @RedirectType(BlockPos)Object pos, @RedirectType(BlockEntity)Object optionalEntity, @RedirectType(Entity)Object executor, @RedirectType(ItemStack)Object tool, boolean includeDrop , boolean doDropExp);
//    @MethodTarget
//    boolean dropFromExplosion(Object block);

    @MethodTarget
    Object defaultBlockState(Object block);

    @MethodTarget
    Object getStateDefinition(Object block);

    @MethodTarget
    Object asItem(Object block);

    @MethodTarget
    @Note("copy all intersected properties from sample")
    Object withPropertiesOf(Object block, @RedirectType(BlockState)Object sampleState);

    default ImmutableList<?> getAllStates(Object block){
        Object def = getStateDefinition(block);
        return EnvInternel.STATE_DEFINITION.getPossibleStates(def);
    }

    default ImmutableSortedMap<String, ?> getAllPropertiesWithName(Object block){
        Object def = getStateDefinition(block);
        return EnvInternel.STATE_DEFINITION.propertiesByNameGetter(def);
    }
}
