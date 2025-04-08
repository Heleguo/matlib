package me.matl114.matlib.nmsMirror.craftbukkit.persistence;

import com.google.common.base.Preconditions;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Map;

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
        return NMSCore.COMPONENT_TAG.newComp(getRaw(pdc));
    }
    @MethodTarget
    @RedirectClass("org.bukkit.craftbukkit.persistence.DirtyCraftPersistentDataContainer")
    public boolean dirty(Object dirtyContainer);

    @MethodTarget
    @RedirectClass("org.bukkit.craftbukkit.persistence.DirtyCraftPersistentDataContainer")
    public void dirty(Object dirtyContainer, final boolean dirty);

}
