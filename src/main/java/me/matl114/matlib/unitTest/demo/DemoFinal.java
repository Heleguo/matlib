package me.matl114.matlib.unitTest.demo;

import me.matl114.matlib.utils.reflect.descriptor.DescriptorImplBuilder;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.DescriptorException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.lang.reflect.InvocationTargetException;

public final class DemoFinal implements DemoInterface {
    static final VarHandle varhandle;
    static final MethodHandle methodHandle;

    @Override
    public String a(Object instance) throws InvocationTargetException {
        return (String) ((DemoClass)instance).c;
    }
    static{
        varhandle = DescriptorImplBuilder.initVarHandle(0,"111");
        methodHandle = DescriptorImplBuilder.initMethodHandle(0,"122");
    }
    public Class  acc1(Object a, boolean b){
        try{
            DemoInterface itf = this;
            return itf.getClass();
        }catch (Throwable e){
            throw DescriptorException.dump(e);
        }

    }
//    public int retint(){
//        return 0;
//    }
//    public long retLong(){
//        return 0L;
//    }
//    public double retDouble(){
//        return 0D;
//    }
//    public float retFloat(){
//        return 0F;
//    }
//    public void retVoid(){
//    }

    public int acc2(Object c){
        return (int)varhandle.get(c);
    }
}
