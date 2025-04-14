package me.matl114.matlib.nmsMirror.impl;

import me.matl114.matlib.nmsMirror.core.BlockPosHelper;
import me.matl114.matlib.nmsMirror.core.RegistriesHelper;
import me.matl114.matlib.nmsMirror.nbt.CompoundTagHelper;
import me.matl114.matlib.nmsMirror.nbt.ListTagHelper;
import me.matl114.matlib.nmsMirror.nbt.TagAPI;
import me.matl114.matlib.nmsMirror.resources.ResourceLocationHelper;
import me.matl114.matlib.utils.reflect.descriptor.DescriptorImplBuilder;

public class NMSCore {
    public static final CompoundTagHelper COMPOUND_TAG;
    public static final ListTagHelper LIST_TAG ;
    public static final TagAPI TAGS;

    public static final ResourceLocationHelper NAMESPACE_KEY;
    public static final RegistriesHelper REGISTRIES;
    public static final BlockPosHelper BLOCKPOS ;
    static {
        //Version version = Version.getVersionInstance();
        COMPOUND_TAG = DescriptorImplBuilder.createHelperImpl(CompoundTagHelper.class);
        LIST_TAG = DescriptorImplBuilder.createHelperImpl(ListTagHelper.class);
        TAGS = DescriptorImplBuilder.createMultiHelper(TagAPI.class);

        NAMESPACE_KEY = DescriptorImplBuilder.createHelperImpl(ResourceLocationHelper.class);
        REGISTRIES = DescriptorImplBuilder.createHelperImpl(RegistriesHelper.class);
        BLOCKPOS = DescriptorImplBuilder.createHelperImpl(BlockPosHelper.class);
    }
}
