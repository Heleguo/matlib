package me.matl114.matlib.nmsMirror.impl;

import me.matl114.matlib.nmsMirror.chat.ComponentContentAPI;
import me.matl114.matlib.nmsMirror.chat.ComponentHelper;
import me.matl114.matlib.nmsMirror.chat.FormatHelper;
import me.matl114.matlib.nmsMirror.chat.v1_20_R4.ComponentHelper_1_20_R4;
import me.matl114.matlib.utils.reflect.descriptor.DescriptorImplBuilder;
import me.matl114.matlib.utils.version.Version;

public class NMSChat {
    public static final ComponentHelper CHATCOMPONENT ;
    public static final FormatHelper FORMAT = DescriptorImplBuilder.createMultiHelper(FormatHelper.class);
    public static final ComponentContentAPI COMP_CONTENT = DescriptorImplBuilder.createMultiHelper(ComponentContentAPI.class);
    static{
        if(Version.getVersionInstance().isAtLeast(Version.v1_20_R4)){
            CHATCOMPONENT = DescriptorImplBuilder.createMultiHelper(ComponentHelper_1_20_R4.class);
        }else {
            CHATCOMPONENT = DescriptorImplBuilder.createMultiHelper(ComponentHelper.class);
        }
    }
}
