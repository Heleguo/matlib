package me.matl114.matlib.nmsMirror.impl;

import me.matl114.matlib.core.EnvironmentManager;
import me.matl114.matlib.nmsMirror.core.BlockPosHelper;
import me.matl114.matlib.nmsMirror.core.RegistriesHelper;
import me.matl114.matlib.nmsMirror.inventory.ItemStackHelper;
import me.matl114.matlib.nmsMirror.inventory.v1_20_R4.ItemStackHelper_1_20_R4;
import me.matl114.matlib.nmsMirror.nbt.ComponentTagHelper;
import me.matl114.matlib.nmsMirror.resources.ResourceLocationHelper;
import me.matl114.matlib.utils.reflect.descriptor.DescriptorImplBuilder;
import me.matl114.matlib.utils.version.Version;

public class NMSCore {
    public static final ComponentTagHelper COMPONENT_TAG;

    public static final ResourceLocationHelper NAMESPACE_KEY;
    public static final RegistriesHelper REGISTRIES;
    public static final BlockPosHelper BLOCKPOS ;
    static {
        Version version = EnvironmentManager.getManager().getVersion();
        COMPONENT_TAG = DescriptorImplBuilder.createHelperImpl(ComponentTagHelper.class);

        NAMESPACE_KEY = DescriptorImplBuilder.createHelperImpl(ResourceLocationHelper.class);
        REGISTRIES = DescriptorImplBuilder.createHelperImpl(RegistriesHelper.class);
        BLOCKPOS = DescriptorImplBuilder.createHelperImpl(BlockPosHelper.class);
    }
}
