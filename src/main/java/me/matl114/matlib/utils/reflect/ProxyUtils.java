package me.matl114.matlib.utils.reflect;

import me.matl114.matlib.utils.reflect.adaptors.AdaptorInvocation;

import java.lang.reflect.Proxy;

public class ProxyUtils {
    public static <T> T buildAdaptorOf(Class<T> interfaceClass,Object invokeTarget) throws Throwable {
        AdaptorInvocation invocation = new AdaptorInvocation(interfaceClass,invokeTarget);
        T proxy =  (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),new Class[]{interfaceClass},invocation);
        return proxy;
    }
}
