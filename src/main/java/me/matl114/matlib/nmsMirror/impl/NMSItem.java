package me.matl114.matlib.nmsMirror.impl;

import me.matl114.matlib.core.EnvironmentManager;
import me.matl114.matlib.nmsMirror.inventory.ContainerHelper;
import me.matl114.matlib.nmsMirror.inventory.ContainerMenuHelper;
import me.matl114.matlib.nmsMirror.inventory.ItemStackHelper;
import me.matl114.matlib.nmsMirror.inventory.ItemsAPI;
import me.matl114.matlib.nmsMirror.inventory.v1_20_R4.ItemStackHelper_1_20_R4;
import me.matl114.matlib.utils.reflect.descriptor.DescriptorImplBuilder;
import me.matl114.matlib.utils.version.Version;

public class NMSItem {
    public static final ItemStackHelper ITEMSTACK;
    public static final ItemsAPI ITEMS;
    public static final ContainerHelper CONTAINER;
//    public static final ContainerMenuHelper MENU;
    static {
        Version version = EnvironmentManager.getManager().getVersion();
        if(version.isAtLeast(Version.v1_20_R4)){
            ITEMSTACK = DescriptorImplBuilder.createHelperImpl(ItemStackHelper_1_20_R4.class);
        }else {
            ITEMSTACK = DescriptorImplBuilder.createHelperImpl(ItemStackHelper.class);
        }
        ITEMS = DescriptorImplBuilder.createMultiHelper(ItemsAPI.class);
        CONTAINER = DescriptorImplBuilder.createMultiHelper(ContainerHelper.class);
//        MENU = DescriptorImplBuilder.createMultiHelper(ContainerMenuHelper.class);
    }
}
