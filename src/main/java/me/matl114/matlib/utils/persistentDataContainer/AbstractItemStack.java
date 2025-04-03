package me.matl114.matlib.utils.persistentDataContainer;

import com.jeff_media.morepersistentdatatypes.DataType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;

public class AbstractItemStack implements PersistentDataType<PersistentDataContainer, ItemStack> {
    NamespacedKey key;
    public AbstractItemStack(NamespacedKey key) {
        this.key = key;
    }
    public Class<PersistentDataContainer> getPrimitiveType(){
        return PersistentDataContainer.class;
    }
    public Class<ItemStack> getComplexType(){
        return ItemStack.class;
    }
    @Override
    @Nonnull
    public PersistentDataContainer toPrimitive(@Nonnull ItemStack complex, @Nonnull PersistentDataAdapterContext context) {
        final PersistentDataContainer container = context.newPersistentDataContainer();
        if(complex.getAmount()==1){
            container.set(key, DataType.ITEM_STACK, complex);
        }
        else{
            complex=complex.clone();
            complex.setAmount(1);
            container.set(key, DataType.ITEM_STACK, complex);

        }
        return container;
    }

    @Override
    @Nonnull
    public ItemStack fromPrimitive(@Nonnull PersistentDataContainer primitive, @Nonnull PersistentDataAdapterContext context) {
        final ItemStack item = primitive.get(key, DataType.ITEM_STACK);
        return item;
    }

}
