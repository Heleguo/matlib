package me.matl114.matlib.nmsMirror.craftbukkit.persistence;

import com.google.common.base.Preconditions;
import me.matl114.matlib.common.lang.annotations.NeedTest;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
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
    public PersistentDataContainer newPersistentDataContainer(Map<String, ?> customTags, @RedirectType("Lorg/bukkit/craftbukkit/persistence/CraftPersistentDataTypeRegistry")Object registry);

    @ConstructorTarget
    @RedirectClass("org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry")
    public Object createRegistry();

    @ConstructorTarget
    @RedirectClass("org.bukkit.craftbukkit.persistence.CraftPersistentDataAdapterContext")
    public PersistentDataAdapterContext createAdaptorContext(@RedirectType("Lorg/bukkit/craftbukkit/persistence/CraftPersistentDataTypeRegistry")Object registries);

    @MethodTarget
    @RedirectClass("org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry")
    public Object wrap(Object registries, PersistentDataType type, Object value);

    @MethodTarget
    @RedirectClass("org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry")
    public boolean isInstanceOf(Object registries, PersistentDataType type,@RedirectType(Tag) Object value);

    @MethodTarget
    @RedirectClass("org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry")
    public <T> T extract(Object registries, PersistentDataType<T,?> type,@RedirectType(Tag) Object value);

    @Note("create a view from a raw Compound Tag or a map, No new Map will be created")
    static class DataContainerView implements PersistentDataContainer{
        static Object registry;
        static PersistentDataAdapterContext adapterContext;
        @Nonnull
        private static Object getRegistry(){
            if(registry == null){
                registry = PERSISTENT_DATACONTAINER.createRegistry();
                adapterContext = PERSISTENT_DATACONTAINER.createAdaptorContext(registry);
            }
            return registry;
        }

        public static PersistentDataAdapterContext getAdaptorContext(){
            getRegistry();
            return adapterContext;
        }

        private final Map<String, Object> customDataTags ;
        public DataContainerView(Object compoundTags){
            customDataTags = (Map<String, Object>) COMPOUND_TAG.tagsGetter(compoundTags);
        }

        public DataContainerView(Map<String, ?> rawMap){
            customDataTags = (Map<String, Object>) rawMap;
        }

        @Override
        public <P, C> void set(@NotNull NamespacedKey key, @NotNull PersistentDataType<P, C> type, @NotNull C value) {
            this.customDataTags.put(key.toString(), PERSISTENT_DATACONTAINER.wrap(getRegistry(), type, type.toPrimitive(value, this.adapterContext)));
        }

        @Override
        public void remove(@NotNull NamespacedKey namespacedKey) {
            this.customDataTags.remove(namespacedKey.toString());
        }

        @Override
        public void readFromBytes(byte @NotNull [] bytes, boolean b) throws IOException {

            getRegistry();
            PersistentDataContainer container = adapterContext.newPersistentDataContainer();
            container.readFromBytes(bytes);
            if(b){
                this.customDataTags.clear();
            }
            this.customDataTags.putAll(PERSISTENT_DATACONTAINER.getRaw(container));


        }

        @Override
        public <P, C> boolean has(NamespacedKey namespacedKey, PersistentDataType<P, C> persistentDataType) {
            Object raw = customDataTags.get(namespacedKey.toString());
            if(raw == null)
                return false;
            return PERSISTENT_DATACONTAINER.isInstanceOf(getRegistry(), persistentDataType, raw);
        }

        @Override
        public boolean has(NamespacedKey namespacedKey) {
            return this.customDataTags.get(namespacedKey.toString()) != null;
        }

        @Override
        public <P, C> @Nullable C get(NamespacedKey namespacedKey, PersistentDataType<P, C> persistentDataType) {
            Object raw = customDataTags.get(namespacedKey.toString());
            if(raw == null)
                return null;
            return persistentDataType.fromPrimitive( PERSISTENT_DATACONTAINER.extract(getRegistry(), persistentDataType, raw), adapterContext);
        }

        @Override
        public <P, C> C getOrDefault(NamespacedKey namespacedKey, PersistentDataType<P, C> persistentDataType, C c) {
            C value = get(namespacedKey, persistentDataType);
            return value == null ? c : value;
        }

        @Override
        public Set<NamespacedKey> getKeys() {
            Set<NamespacedKey> keys = new HashSet<>();

            this.customDataTags.keySet().forEach(key -> {
                String[] keyData = key.split(":", 2);
                if (keyData.length == 2) {
                    keys.add(new NamespacedKey(keyData[0], keyData[1]));
                }
            });

            return keys;
        }

        @Override
        public boolean isEmpty() {
            return this.customDataTags.isEmpty();
        }

        @Override
        public void copyTo(PersistentDataContainer persistentDataContainer, boolean b) {
            if(persistentDataContainer instanceof DataContainerView view){
                if(b){
                    view.customDataTags.putAll(this.customDataTags);
                }else {
                    this.customDataTags.forEach((k,v)->view.customDataTags.putIfAbsent(k,v));
                }
            }else if(PERSISTENT_DATACONTAINER.isCraftContainer(persistentDataContainer)){
                Map<String, ?> tags = PERSISTENT_DATACONTAINER.getRaw(persistentDataContainer);
                if(b){
                    tags.putAll((Map) this.customDataTags);
                }else {
                    this.customDataTags.forEach((k,v)-> ((Map)tags).putIfAbsent(k,v));
                }
            }else {
                throw new UnsupportedOperationException("Persistent Data Container Class not supported: "+persistentDataContainer.getClass());
            }
        }

        @Override
        public PersistentDataAdapterContext getAdapterContext() {
            getRegistry();
            return adapterContext;
        }

        @Override
        public byte[] serializeToBytes() throws IOException {
            PersistentDataContainer craftPersistentDataContainer = PERSISTENT_DATACONTAINER.newPersistentDataContainer(this.customDataTags, getRegistry());
            return craftPersistentDataContainer.serializeToBytes();
        }
    }

}
