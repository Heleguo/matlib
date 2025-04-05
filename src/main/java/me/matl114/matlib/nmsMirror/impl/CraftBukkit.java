package me.matl114.matlib.nmsMirror.impl;

import me.matl114.matlib.nmsMirror.craftbukkit.inventory.CraftItemStackHelper;
import me.matl114.matlib.nmsMirror.craftbukkit.persistence.CraftPersistentDataContainerHelper;
import me.matl114.matlib.nmsMirror.craftbukkit.utils.CraftMagicNumbersHelper;
import me.matl114.matlib.utils.CraftUtils;
import me.matl114.matlib.utils.reflect.descriptor.DescriptorImplBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

public class CraftBukkit {

    public static final CraftMagicNumbersHelper MAGIC_NUMBERS ;
    public static final CraftItemStackHelper CRAFT_ITEMSTACK;
    //should have a persistentDataContainerHelper
    public static final CraftPersistentDataContainerHelper PERSISTENT_DATACONTAINER;
    static {
        MAGIC_NUMBERS = DescriptorImplBuilder.createHelperImplAt(Bukkit.getUnsafe().getClass(), CraftMagicNumbersHelper.class);
        CRAFT_ITEMSTACK = DescriptorImplBuilder.createHelperImplAt(CraftUtils.getCraftItemStackClass(), CraftItemStackHelper.class);
        ItemStack item = new ItemStack(Material.STONE);
        PersistentDataContainer persistentDataContainer = item.getItemMeta().getPersistentDataContainer();
        PERSISTENT_DATACONTAINER = DescriptorImplBuilder.createHelperImplAt(persistentDataContainer.getClass(), CraftPersistentDataContainerHelper.class);
    }
}
