package me.matl114.matlib.utils.reflect.proxy;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import me.matl114.matlib.utils.reflect.proxy.methodMap.MethodSignature;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import me.matl114.matlib.utils.reflect.proxy.invocation.InvocationCreator;
import me.matl114.matlib.utils.reflect.proxy.methodMap.MethodIndex;
import me.matl114.matlibAdaptor.proxy.utils.AnnotationUtils;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;

/**
 * this method is use to build light-weighted code-bridge without asm
 */
@SuppressWarnings("all")
public class ProxyBuilder {
    public static Set<MethodIndex> createMappingProxy(Class<?> targetInterface, Class<?> proxyClass){
        return createMappingProxy(targetInterface, proxyClass, true);
    }
    public static void ensureAdaptor(Class<?> targetInterface, Class<?> proxyClass){
        String simpleName = targetInterface.getSimpleName();
        var targetClass = AnnotationUtils.getTargetInterface(proxyClass,simpleName);
        if(targetClass == null){
            throw new IllegalArgumentException("Illegal Adaptor class!No matching interface present in "+ proxyClass +"!");
        }
        Preconditions.checkState(AnnotationUtils.getAdaptorInstance(targetClass).isPresent(),"Illegal Adaptor class! Interface {0} in invoke target class is not marked as AdaptorInterface!",targetClass);
        Preconditions.checkNotNull(targetClass,"Invoke target does not contains Interface named {0}!",simpleName);
    }

    public static Set<MethodIndex> createMappingProxy(Class<?> targetInterface, Class<?> proxyClass, boolean ensureAdaptor){
        var annotation = AnnotationUtils.getAdaptorInstance(targetInterface);
        Preconditions.checkState(annotation.isPresent(),"Illegal Adaptor class!This is not an AdaptorInterface!");
        if(ensureAdaptor){
            ensureAdaptor(targetInterface, proxyClass);
        }
                //save the mapping from targetInterface to invokeTarget fastAccess index;
        Set<MethodIndex> methodSnapshot = new ReferenceArraySet<>();
        Set<Method> methodMustRequired = AnnotationUtils.getAdaptedMethods(targetInterface);
        for(Method method : proxyClass.getMethods()){
            //add base method invoke access
            if(ReflectUtils.isBaseMethod(method)){
                //require base method like toString and sth
                methodSnapshot.add(new MethodIndex(method, MethodSignature.getSignature(method), ReflectUtils.getBaseMethodIndex(method),false));

            }
        }
        Iterator<Method> iter = methodMustRequired.iterator();
        int indexCnt = 0;
        while(iter.hasNext()){
            Method m = iter.next();
            try{
                var params = m.getParameterTypes();
                int index = ++indexCnt;//fastAccess.getIndex(m.getName(),params);
                //find matched target
                methodSnapshot.add(new MethodIndex(proxyClass.getMethod(m.getName(), params), MethodSignature.getSignature(m), index, AnnotationUtils.getDefaultAnnotation(m).isPresent()));

            }catch (Throwable e){
                throw new IllegalArgumentException("Method " + m + " not found in invoke target class! using"+targetInterface +" as Adaptor and "+proxyClass+" as invoke target");
            }
        }
        return methodSnapshot;
    }
    public static <T> T buildMatlibAdaptorOf(Class<T> interfaceClass, Object invokeTarget, Function<Set<MethodIndex>, InvocationCreator> adaptorBuilder) throws Throwable {
        Set<MethodIndex> context = createMappingProxy(interfaceClass, invokeTarget.getClass());
        InvocationCreator invocation = adaptorBuilder.apply(context); //AdaptorInvocation.create(interfaceClass, invokeTarget.getClass());
        T proxy =  (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),new Class[]{interfaceClass},invocation.bindTo(invokeTarget));
        return proxy;
    }


}
