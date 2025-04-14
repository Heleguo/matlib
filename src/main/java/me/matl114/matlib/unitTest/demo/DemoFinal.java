package me.matl114.matlib.unitTest.demo;

import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.descriptor.DescriptorImplBuilder;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.DescriptorException;
import me.matl114.matlib.utils.reflect.mixImpl.buildTools.MixBase;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.lang.reflect.InvocationTargetException;

public final class DemoFinal implements DemoInterface {
    public static DemoFinal ins(){
        int[][] a = new int[2][2];
        return new DemoFinal();
    }
    static final VarHandle varhandle;
    static final MethodHandle methodHandle;
    {
        setA(true);
        a = true;
        invokeB(this, Debug.isDebugMod()?1:3);
        b(this,Debug.isDebugMod()?1:3);
        Object a = this;
        Object finalA = a;
        Runnable run = ()->{
            Object s = MixBase.castInsn(finalA, "Lnet/");
            Debug.logger(s);
        };

        if(Debug.isDebugMod()){
            a= MixBase.castInsn(getClass(), "Lnet/");
            a =MixBase.castInsn(a, "Lnet/");
            a = MixBase.castInsn(a, "Lnet/");
            a = MixBase.castInsn(a, "Lnet/");
            a = MixBase.castInsn(a, "Lnet/");
        }else {
            a= MixBase.castInsn(getClass(), "Lnet/");
            if( MixBase.instanceofInsn(a, "Lnet/")){
                Object debug = MixBase.castInsn(a, "Lnet/");
                Debug.logger(debug);
            }

            Debug.logger("Lnet/");
        }

    }
    public void setA(boolean a){

    }
    public void invokeB(Object a, Object b){

    }
    public void b(DemoFinal s, int t){

    }

    boolean a;
    @Override
    public Boolean a(Object instance) throws InvocationTargetException {
        return instance instanceof DemoFinal;
    }
    static{
        varhandle = DescriptorImplBuilder.initVarHandle(0,"111");
        methodHandle = DescriptorImplBuilder.initMethodHandle(0,"122");
    }
    public Class  acc1(String a, boolean b){
       return (Class)  varhandle.get(a);

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
