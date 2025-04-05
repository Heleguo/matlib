package me.matl114.matlib.utils.reflect.descriptor.buildTools;

public class DescriptorBuildException extends RuntimeException{
    public static DescriptorBuildException warp(Throwable e){
        return e instanceof DescriptorBuildException b?b:new DescriptorBuildException(e);
    }
    public DescriptorBuildException(Throwable e){
        super("Error while creating DescriptorImpl :", DescriptorException.handled(e));
    }
    public DescriptorBuildException(String v){
        super("Error while creating DescriptorImpl :" + v);
    }
}
