package me.matl114.matlib.Utils.Version;

import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.Version.VersionedFeatures.*;
import org.bukkit.Bukkit;

import java.util.function.Supplier;

public enum Version {
    v1_20_R1("v1_20_R1", VersionedFeature_1_20_R1_Impl::new),
    v1_20_R2("v1_20_R2", VersionedFeature_1_20_R2_Impl::new),
    v1_20_R3("v1_20_R3", VersionedFeature_1_20_R3_Impl::new),
    v1_20_R4("v1_20_R4", VersionedFeature_1_20_R4_Impl::new),
    v1_21_R1("v1_21_R1", VersionedFeature_1_21_R1_Impl::new),
    v1_21_R2("v1_21_R2",VersionedFeature_1_21_R2_Impl::new),
    unknown("unknown", VersionedFeature_1_20_R1_Impl::new);
    private Version(String name, Supplier<VersionedFeature> feature){
        this.name = name;
        this.feature = feature;
    }
    private String name;
    private Supplier<VersionedFeature> feature;
    public VersionedFeature getFeature(){
        return feature.get();
    }
    public static Version getVersionInstance(){
        String version=null;
        try{
            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].trim();
            for(Version v : Version.values()){
                if(v.name.equals(version)){
                    Debug.logger("Using version", version);
                    return v;
                }
            }
            throw new RuntimeException("Version not supported for " + version);
        }catch (Throwable e){
            Debug.logger("Fail to create version specific feature :",version);
            Debug.logger(e);
            Debug.logger("Using default versiond feature ");
            return unknown;
        }
    }
}
