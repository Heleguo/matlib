package me.matl114.matlib.unitTest.autoTests.reflectionTests;

import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.unitTest.demo.DemoLoad;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import me.matl114.matlib.utils.reflect.descriptor.DescriptorProxyBuilder;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.reflect.internel.ObfManager;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public class ProxyUtilTests implements TestCase {
    @OnlineTest(name = "Proxy descriptor test")
    public void test_proxy()throws Throwable{
        DemoProxy handle = DescriptorProxyBuilder.createHelperImpl(DemoProxy.class);
        Debug.logger(handle);
        Debug.logger(handle.getClass());
        Class cls = DemoLoad.initDemo();
        var constructor = cls.getConstructor();
        constructor.setAccessible(true);
        Object val = constructor.newInstance();
        handle.a(val);
        handle.notComplete(val, true);
        Debug.logger(ReflectUtils.getAllFieldsRecursively(handle.getClass()));
    }

    @OnlineTest(name = "Proxy module test")
    public void test_proxyLoader() throws Throwable {
        //DID NOT WORK！
        // proxy itf should be visible from loader
//        ClassLoader loader = ProxyUtilTests.class.getClassLoader().getParent();
//        Class cls = DemoLoad.initDemo();
//        var constructor = cls.getConstructor();
//        constructor.setAccessible(true);
//        Object val = constructor.newInstance();
//        DemoProxy test = DescriptorProxyBuilder.createSingleInternel(cls, DemoProxy.class, loader);
//        Debug.logger(test.getClass().getClassLoader());
//        Debug.logger();
    }

    @Descriptive(target = "me.matl114.matlib.unitTest.demo.DemoTargetClass")
    public static interface DemoProxy extends TargetDescriptor {
        @MethodTarget
        void a(Object target);

        @MethodTarget
        default void notComplete(Object target, boolean val){
            Debug.logger("Default notComplete called",val);
            try{
                MethodHandles.Lookup lookup1 = MethodHandles.privateLookupIn(ObfManager.getManager().reobfClass("net.minecraft.world.item.ItemStack"),MethodHandles.lookup());
                Debug.logger( lookup1.hasFullPrivilegeAccess());
            }catch (Throwable e){
            }

        }
    }
}
