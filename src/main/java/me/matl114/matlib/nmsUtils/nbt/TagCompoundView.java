package me.matl114.matlib.nmsUtils.nbt;

import lombok.Getter;
import me.matl114.matlib.common.lang.annotations.Note;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static me.matl114.matlib.nmsMirror.impl.CraftBukkit.PERSISTENT_DATACONTAINER;
import static me.matl114.matlib.nmsMirror.impl.NMSCore.COMPOUND_TAG;
import static me.matl114.matlib.nmsUtils.CraftBukkitUtils.*;

@Note("create a view from a raw Compound Tag or a map, No new Map will be created")
public class TagCompoundView implements PersistentDataContainer {
    @Getter
    public final Map<String, Object> customDataTags ;
    public TagCompoundView(Object compoundTags){
        customDataTags = (Map<String, Object>) COMPOUND_TAG.tagsGetter(compoundTags);
    }

    public TagCompoundView(Map<String, ?> rawMap){
        customDataTags = (Map<String, Object>) rawMap;
    }

    @Override
    @Note("writing to it should be careful, data may not apply to the original ItemStack")
    public <P, C> void set(@NotNull NamespacedKey key, @NotNull PersistentDataType<P, C> type, @NotNull C value) {
        this.customDataTags.put(key.toString(), PERSISTENT_DATACONTAINER.wrap(getPdcDataTypeRegistry(), type, type.toPrimitive(value, getPdcAdaptorContext())));
    }

    @Override
    public void remove(@NotNull NamespacedKey namespacedKey) {
        this.customDataTags.remove(namespacedKey.toString());
    }

    @Override
    public void readFromBytes(byte @NotNull [] bytes, boolean b) throws IOException {

        getPdcDataTypeRegistry();
        PersistentDataContainer container = getPdcAdaptorContext().newPersistentDataContainer();
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
        return PERSISTENT_DATACONTAINER.isInstanceOf(getPdcDataTypeRegistry(), persistentDataType, raw);
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
        return persistentDataType.fromPrimitive( PERSISTENT_DATACONTAINER.extract(getPdcDataTypeRegistry(), persistentDataType, raw), getPdcAdaptorContext());
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
        if(persistentDataContainer instanceof TagCompoundView view){
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
        return getPdcAdaptorContext();
    }

    public void copyFrom(PersistentDataContainer craftPersistentDataContainer, boolean b){
        if(craftPersistentDataContainer instanceof TagCompoundView view){
            if(b){
                this.customDataTags.putAll(view.customDataTags);
            }else {
                view.customDataTags.forEach((k,v)->this.customDataTags.putIfAbsent(k,v));
            }
        }else if(PERSISTENT_DATACONTAINER.isCraftContainer(craftPersistentDataContainer)){
            Map<String, ?> tags = PERSISTENT_DATACONTAINER.getRaw(craftPersistentDataContainer);
            if(b){
                this.customDataTags.putAll((Map) tags);
            }else {
                tags.forEach((k,v)-> ((Map)this.customDataTags).putIfAbsent(k,v));
            }
        }else {
            throw new UnsupportedOperationException("Persistent Data Container Class not supported: "+craftPersistentDataContainer.getClass());
        }
    }

    public PersistentDataContainer toCraftContainer(){
        return PERSISTENT_DATACONTAINER.newPersistentDataContainer(this.customDataTags, getPdcDataTypeRegistry());
    }


    @Override
    public byte[] serializeToBytes() throws IOException {
        PersistentDataContainer craftPersistentDataContainer = PERSISTENT_DATACONTAINER.newPersistentDataContainer(this.customDataTags, getPdcDataTypeRegistry());
        return craftPersistentDataContainer.serializeToBytes();
    }
}
