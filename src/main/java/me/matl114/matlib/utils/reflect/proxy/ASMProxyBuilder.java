package me.matl114.matlib.utils.reflect.proxy;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import me.matl114.matlib.common.lang.exceptions.NotImplementedYet;
import me.matl114.matlibAdaptor.proxy.Utils.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ASMProxyBuilder {
    public static Map<Method, Method> createMapping(Class<?> targetInterface, Class<?> proxyClass){

        return null;

    }
    public static <T> T buildAdaptorOf(Class<T> targetInterface, Object invokeTarget) throws Throwable{
        return buildAdaptorOf(targetInterface, invokeTarget, true);
    }
    public static <T> T buildAdaptorOf(Class<T> targetInterface, Object invokeTarget, boolean ensureAdaptor) throws Throwable{
        var annotation = AnnotationUtils.getAdaptorInstance(targetInterface);
        Preconditions.checkState(annotation.isPresent(),"Illegal Adaptor class!This is not an AdaptorInterface!");
        Class proxyClass = invokeTarget.getClass();
        if(ensureAdaptor){
            ProxyBuilder.ensureAdaptor(targetInterface, proxyClass);
        }
                //save the mapping from targetInterface to invokeTarget fastAccess index;
        Map<Method,Method> methodMapper = new Reference2ReferenceOpenHashMap<>();
        Set<Method> methodMustRequired = AnnotationUtils.getAdaptedMethods(targetInterface);
        Iterator<Method> iter = methodMustRequired.iterator();
        int indexCnt = 0;
        while(iter.hasNext()){
            Method m = iter.next();
            try{
                var params = m.getParameterTypes();
                Method mapped = proxyClass.getMethod(m.getName(), params);
                methodMapper.put(m, mapped);
            }catch (Throwable e){
                throw new IllegalArgumentException("Method " + m + " not found in invoke target class! using"+targetInterface +" as Adaptor and "+proxyClass+" as invoke target");
            }
        }
        throw new NotImplementedYet();
    }
}
