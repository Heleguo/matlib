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
    unknown("unknown", DefaultVersionedFeatureImpl::new);
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
            String[] path=Bukkit.getServer().getClass().getPackage().getName().split("\\.");
            if(path.length>=4){
                version = path[3].trim();
            }else {
                version = Bukkit.getServer().getBukkitVersion().split("-")[0].trim();
            }
            for(Version v : Version.values()){
                if(v.name.equals(version)){
                    Debug.logger("Using version", version);
                    return v;
                }
            }
            switch (version) {
                case "1.20.5":
                case "1.20.6":
                    Debug.logger("Using version",v1_20_R4.name);
                    return v1_20_R4;
                case "1.21":
                case "1.21.1":
                case "1.21.2":
                    Debug.logger("Using version",v1_21_R1.name);
                    return v1_21_R1;
                case "1.21.3":
                    Debug.logger("Using version",v1_21_R2.name);
                    return v1_21_R2;
                default:
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
