package me.matl114.matlib.Utils.Version.VersionedFeatures;

import me.matl114.matlib.Utils.Version.Version;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;



public class VersionedFeature_1_20_R3_Impl extends VersionedFeature_1_20_R2_Impl{
    public VersionedFeature_1_20_R3_Impl() {
        this.version= Version.v1_20_R3;
    }
    {

        remappingMaterialId.put("grass","tall_grass");
    }
    public boolean copyBlockStateTo( BlockState state1, Block target){
        try{
            state1.copy(target.getLocation()).update(true,false);
            return true;
        }catch(Throwable e){
            return super.copyBlockStateTo( state1, target );
        }
    }
}
