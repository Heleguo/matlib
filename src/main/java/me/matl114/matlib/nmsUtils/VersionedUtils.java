package me.matl114.matlib.nmsUtils;

public class VersionedUtils {
    public static RuntimeException removal(){
        return new UnsupportedOperationException("removal");
    }
    public static RuntimeException versionLow(){
        return new UnsupportedOperationException("low version");
    }

}
