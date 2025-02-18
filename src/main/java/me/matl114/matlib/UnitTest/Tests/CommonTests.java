package me.matl114.matlib.UnitTest.Tests;

import com.esotericsoftware.reflectasm.MethodAccess;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import me.matl114.matlib.UnitTest.OnlineTest;
import me.matl114.matlib.UnitTest.TestCase;
import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.Reflect.*;
import me.matl114.matlibAdaptor.Algorithms.DataStructures.LockFactory;
import me.matl114.matlibAdaptor.Algorithms.Interfaces.Initialization;
import me.matl114.matlibAdaptor.Proxy.Utils.AnnotationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommonTests implements TestCase {
    @OnlineTest(name = "LoggerUtil Test")
    public void testLogger(){
        Debug.logger("This is logger output 1");
        Debug.logger("This is logger output 2");
        Debug.catchAllOutputs(()->{Debug.logger("this is logger output 3");Debug.logger("this is logger output 4.1");Debug.logger("this is logger output 4.2");
            System.out.println("This is stdoutput 4.3");},false);
        String value = Debug.catchAllOutputs(()->{Debug.logger("this is logger output 4");Debug.logger("this is logger output 4.1");Debug.logger("this is logger output 4.2");
            System.out.println("This is stdoutput 4.3");},true);
        Debug.logger(value);
        Debug.catchAllOutputs(()->{
            new RuntimeException("This is logger output 5").printStackTrace();},false);
        String value2 = Debug.catchAllOutputs(()->{
            new RuntimeException("This is logger output 6").printStackTrace();},true);
        Debug.logger(value2.isEmpty());
        Debug.logger(value2);
    }
    @OnlineTest(name = "TestRunner Test")
    public void testThrowError(){
        throw new NullPointerException("This is a null pointer");
    }
    @OnlineTest(name = "MatlibAdaptor Test")
    public void testAPI() throws Throwable {
        Class logiTech = Class.forName("me.matl114.logitech.MyAddon");
        Debug.logger(logiTech.getName());
        FieldAccess initAccess = FieldAccess.ofName(logiTech,"matlibInstance");
        Object instance= initAccess.initWithNull().getValue(null);
        Debug.logger(instance.getClass().getName());
        Debug.logger(instance instanceof Initialization);
        Initialization init = ProxyUtils.buildAdaptorOf(Initialization.class, instance);
        Debug.logger(init);
        Debug.logger(init.getClass());
        Debug.logger(init.getClass().getSimpleName());
        Debug.logger(init.getDisplayName());
        Debug.logger(init.getLogger());
        long start = System.nanoTime();
        String value = null;
        for (int i=0;i<1_000_000;++i){
             value = init.getDisplayName();
        }
        long end = System.nanoTime();
        Debug.logger("time cost for 1_000_000 invocation",end-start,value);


        //DO NOT CALL METHOD WITH OUR CLASS RETURN VALUE ,OTHERWISE CLASS CAST EXCEPTION WILL OCCURS
        Debug.logger(init.isTestMode());
        Object access = me.matl114.matlib.Utils.Reflect.MethodAccess.ofName(Slimefun.class,"getCargoLockFactory")
                .noSnapShot()
                .initWithNull()
                .invoke(null);
        Debug.logger(access);
        LockFactory<Location> locationLockFactory = ProxyUtils.buildAdaptorOf(LockFactory.class, access);
        Debug.logger(locationLockFactory);
        Debug.logger(locationLockFactory.getClass().getSimpleName());
        Debug.logger(locationLockFactory.checkThreadStatus(new Location(Bukkit.getWorlds().get(0),0,0,0)));
    }
    public static Map<String,Integer> ObjectInvocationIndex = new HashMap<>(){{
        put("getClass",-1);
        put("hashCode",-2);
        put("equals",-3);
        put("clone",-4);
        put("toString",-5);
        put("notify",-6);
        put("notifyAll",-7);
        put("wait",-8);
        put("wait0",-9);
        put("finalize",-10);
    }};
    public static Object invokeBase(Object target,int index,Object[] args){
        switch (index){
            case -1:return target.getClass();
            case -2:return target.hashCode();
            case -3:return target.equals(args[0]);
            case -4:throw new IllegalStateException("clone method not supported");
            case -5:return target.toString();
            case -6:target.notify();return null;
            case -7:target.notifyAll();return null;
            case -8:try{ switch (args.length){
                case 0:target.wait();return null;
                case 1:target.wait((Long) args[0]);return null;
                case 2:target.wait();return null;
                default:throw new IllegalArgumentException("Wrong argument count for wait!");
            }}catch (InterruptedException e){
            }
            return null;
            case -9:throw new IllegalArgumentException("wait0 method not supported");
            case -10:throw new IllegalArgumentException("finalize method not supported");
        }
        return null;
    }
    //测试耗时
    //
    public class InvocationTest implements InvocationHandler{
        private final Object target;
        private final MethodAccess methodAccess;
        private Map<MethodSignature,Integer> invocations =new HashMap<>();

        public InvocationTest(Object value,String interfaceName){
            this.target = value;

            Class<?> targetInterface = ReflectUtils.getAllInterfacesRecursively(value.getClass()).stream().filter(e->interfaceName.equals(e.getSimpleName())).findAny().orElse(null);
            Debug.logger(targetInterface);
            this.methodAccess = MethodAccess.get(targetInterface);
            Debug.logger((Object[])this.methodAccess.getMethodNames());
            Debug.logger(methodAccess);
            Debug.logger(AnnotationUtils.getAdaptorInstance(targetInterface).isPresent());
            Debug.logger(AnnotationUtils.getAdaptedMethods(targetInterface));
            for (Method method : value.getClass().getMethods()){
                MethodSignature methodSignature = MethodSignature.getSignature(method);
                Debug.logger(method,method.getDeclaringClass().getClassLoader());
                try{
                    Debug.logger(methodSignature.getSignatureIndex(methodAccess));
                    invocations.put(methodSignature,methodAccess.getIndex(method.getName(),method.getParameterTypes()));
                }catch (IllegalArgumentException baseMethod){

                    if(ObjectInvocationIndex.containsKey(methodSignature.methodName())){
                        Debug.logger("Base Method",methodSignature);
                        invocations.put(methodSignature,ObjectInvocationIndex.get(methodSignature.methodName()));
                    }else {
                        Debug.logger("NotFound Method",methodSignature);
                    }

                }
            }
        }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Debug.logger(method.getDeclaringClass(),method.getDeclaringClass().getClassLoader());
            MethodSignature sig = MethodSignature.getSignature(method);
            Debug.logger(sig);
            Debug.logger(invocations);
            Debug.logger(invocations.get(sig));
            int index = invocations.get(sig);
            if(index >= 0){
                Debug.logger("methodAccess",target.getClass().getClassLoader(),index, Arrays.toString(args));
                return methodAccess.invoke(target,index,args);
            }else {
                return method.invoke(target,args);
            }

        }
    }
    //public class MethodSignature2Int

}
