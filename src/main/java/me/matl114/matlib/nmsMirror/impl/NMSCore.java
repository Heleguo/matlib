package me.matl114.matlib.nmsMirror.impl;

import me.matl114.matlib.nmsMirror.core.BlockPosHelper;
import me.matl114.matlib.nmsMirror.core.RegistriesHelper;
import me.matl114.matlib.nmsMirror.nbt.CompoundTagHelper;
import me.matl114.matlib.nmsMirror.resources.ResourceLocationHelper;
import me.matl114.matlib.utils.reflect.descriptor.DescriptorImplBuilder;

public class NMSCore {
    public static final CompoundTagHelper COMPONENT_TAG;

    public static final ResourceLocationHelper NAMESPACE_KEY;
    public static final RegistriesHelper REGISTRIES;
    public static final BlockPosHelper BLOCKPOS ;
    static {
        //Version version = Version.getVersionInstance();
        COMPONENT_TAG = DescriptorImplBuilder.createHelperImpl(CompoundTagHelper.class);

        NAMESPACE_KEY = DescriptorImplBuilder.createHelperImpl(ResourceLocationHelper.class);
        REGISTRIES = DescriptorImplBuilder.createHelperImpl(RegistriesHelper.class);
        BLOCKPOS = DescriptorImplBuilder.createHelperImpl(BlockPosHelper.class);
    }
}
