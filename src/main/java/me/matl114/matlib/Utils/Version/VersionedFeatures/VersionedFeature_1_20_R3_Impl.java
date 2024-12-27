package me.matl114.matlib.Utils.Version.VersionedFeatures;

import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.Version.Version;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;



public class VersionedFeature_1_20_R3_Impl extends VersionedFeature_1_20_R2_Impl{
    public VersionedFeature_1_20_R3_Impl() {
        this.version= Version.v1_20_R3;
    }
    {
        remappingMaterialId.put("grass","short_grass");
    }

}
