package me.matl114.matlib.utils.version;

import lombok.Getter;
import me.matl114.matlib.utils.Debug;
import org.bukkit.Bukkit;
import javax.annotation.Nonnull;

public enum Version {
    unknown("unknown", -1),
    v1_20_R1("v1_20_R1", 15),
    v1_20_R2("v1_20_R2", 18),
    v1_20_R3("v1_20_R3", 26),
    v1_20_R4("v1_20_R4", 41),
    v1_21_R1("v1_21_R1", 48),
    v1_21_R2("v1_21_R2",57);
    private Version(String name, int datapackNumber){
        this.name = name;
        this.datapackNumber = datapackNumber;
    }
    private String name;
    @Getter
    private int datapackNumber;
    static Version INSTANCE;
    public static Version getVersionInstance(){
        if(INSTANCE == null){
            INSTANCE = getVersionInstance0();
        }
        return INSTANCE;
    }
    @Nonnull
    private static Version getVersionInstance0(){
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
            Debug.logger(e,"Fail to create version specific feature :",version);
            Debug.logger("Using default versiond feature ");
            return unknown;
        }
    }
    public boolean isAtLeast(Version v2){
        return versionAtLeast(this,v2);
    }
    public static boolean versionAtLeast(Version v1, Version v2){
        return v1.datapackNumber >= v2.datapackNumber;
    }
}
