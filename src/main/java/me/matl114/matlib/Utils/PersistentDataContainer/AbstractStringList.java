package me.matl114.matlib.Utils.PersistentDataContainer;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class AbstractStringList implements PersistentDataType<PersistentDataContainer, List<String>> {
    String namespace;
    public AbstractStringList(String namespace) {
        this.namespace = namespace;
    }

    @Nonnull
    public Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }
    private final Class clazz=(new ArrayList<String>()).getClass();
    @Nonnull
    public Class<List<String>> getComplexType() {

        return (Class<List<String>>)clazz;
    }

    @Nonnull
    public PersistentDataContainer toPrimitive(@Nonnull List<String> complex, @Nonnull PersistentDataAdapterContext context) {
        PersistentDataContainer container = context.newPersistentDataContainer();

        for(int i = 0; i < complex.size(); ++i) {
            NamespacedKey key = new NamespacedKey(namespace, "i_"+i);
            container.set(key, STRING, (String)complex.get(i));
        }

        return container;
    }

    @Nonnull
    public List<String> fromPrimitive(@Nonnull PersistentDataContainer primitive, @Nonnull PersistentDataAdapterContext context) {
        List<String> strings = new ArrayList<>();
        for(int i=0;true;++i){
            NamespacedKey key = new NamespacedKey(namespace, "i_"+i);
            if(primitive.has(key, STRING)){
                strings.add(primitive.get(key, STRING));
            }else {
                break;
            }
        }
        return strings;
    }

}