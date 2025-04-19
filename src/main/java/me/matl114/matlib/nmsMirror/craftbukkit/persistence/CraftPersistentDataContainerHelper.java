package me.matl114.matlib.nmsMirror.craftbukkit.persistence;

import com.google.common.base.Preconditions;
import lombok.Getter;
import me.matl114.matlib.common.lang.annotations.Internal;
import me.matl114.matlib.common.lang.annotations.NeedTest;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.utils.reflect.classBuild.annotation.IgnoreFailure;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.version.Version;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import javax.annotation.Nonnull;

import static me.matl114.matlib.nmsMirror.impl.CraftBukkit.*;
import static me.matl114.matlib.nmsMirror.impl.NMSCore.*;
import static me.matl114.matlib.nmsMirror.Import.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NeedTest
@MultiDescriptive(targetDefault = "org.bukkit.craftbukkit.persistence.CraftPersistentDataContainer")
public interface CraftPersistentDataContainerHelper extends TargetDescriptor {
//    @MethodTarget
//    Object getTag(PersistentDataContainer pdc, String key);

    @MethodTarget
    Map<String, ?> getRaw(PersistentDataContainer pdc);

//    @FieldTarget
//    @RedirectType("Ljava/util/Map;")
//    Map<String, ?> customDataTagsGetter(PersistentDataContainer pdc);
    @Note("Put a raw CraftPersistentDataContainer, we will related the pdcTagMap to a compound so that you can use compound method to put custom nbtTags in Container, (not-a-NSkey-like key will be ignored in api methods)")
    default Object asCompoundMirror(PersistentDataContainer pdc){
        Preconditions.checkArgument(pdc.getClass() == getTargetClass() ,"PersistentDataContainer type not match(passing a DirtyPersistentDataContainer? We don't support doing that)");
        return NMSCore.COMPOUND_TAG.newComp(getRaw(pdc));
    }

    @CastCheck("org.bukkit.craftbukkit.persistence.CraftPersistentDataContainer")
    boolean isCraftContainer(PersistentDataContainer container);

    @CastCheck("org.bukkit.craftbukkit.persistence.DirtyCraftPersistentDataContainer")
    boolean isDirtyContainer(PersistentDataContainer container);

    @MethodTarget
    @RedirectClass("org.bukkit.craftbukkit.persistence.DirtyCraftPersistentDataContainer")
    public boolean dirty(Object dirtyContainer);

    @MethodTarget
    @RedirectClass("org.bukkit.craftbukkit.persistence.DirtyCraftPersistentDataContainer")
    public void dirty(Object dirtyContainer, final boolean dirty);

    @ConstructorTarget
    public PersistentDataContainer newPersistentDataContainer(Map<String, ?> customTags, @RedirectType("Lorg/bukkit/craftbukkit/persistence/CraftPersistentDataTypeRegistry;")Object registry);

    @ConstructorTarget
    @RedirectClass("org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry")
    public Object createRegistry();

    @ConstructorTarget
    @RedirectClass("org.bukkit.craftbukkit.persistence.CraftPersistentDataAdapterContext")
    public PersistentDataAdapterContext createAdaptorContext(@RedirectType("Lorg/bukkit/craftbukkit/persistence/CraftPersistentDataTypeRegistry;")Object registries);

    @MethodTarget
    @RedirectClass("org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry")
    @IgnoreFailure(thresholdInclude = Version.v1_20_R3, below = true)
    default  <T> Object wrap(Object registries, PersistentDataType<T,?> type, T value){
        return wrap(registries, type.getPrimitiveType(), value);
    }

    @MethodTarget
    @RedirectClass("org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry")
    @IgnoreFailure(thresholdInclude = Version.v1_20_R3, below = false)
    public <T> Object wrap(Object  registries, Class<T> clazz, T value);

    @MethodTarget
    @RedirectClass("org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry")
    @IgnoreFailure(thresholdInclude = Version.v1_20_R3, below = true)
    default boolean isInstanceOf(Object registries, PersistentDataType type, @RedirectType(Tag) Object value){
        return isInstanceOf(registries, type.getPrimitiveType(), value);
    }

    @MethodTarget
    @RedirectClass("org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry")
    @IgnoreFailure(thresholdInclude = Version.v1_20_R3, below = false)
    public boolean isInstanceOf(Object registries, Class<?> type, @RedirectType(Tag) Object value);

    @MethodTarget
    @RedirectClass("org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry")
    @IgnoreFailure(thresholdInclude = Version.v1_20_R3, below = true)
    default  <T> T extract(Object registries, PersistentDataType<T,?> type,@RedirectType(Tag) Object value){
        return extract(registries, type.getPrimitiveType(), value);
    }

    @Internal
    @MethodTarget
    @RedirectClass("org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry")
    @IgnoreFailure(thresholdInclude = Version.v1_20_R3, below = false)
    public <T> T extract(Object registries, Class<T> type, @RedirectType(Tag) Object value);


}
