package me.matl114.matlib.utils.reflect.adaptors;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import me.matl114.matlib.algorithms.dataStructures.frames.HashContainer;
import me.matl114.matlib.utils.reflect.MethodSignature;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import me.matl114.matlibAdaptor.proxy.Utils.AnnotationUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;

public class AdaptorInvocation implements InvocationHandler {
    private final Object target;
    private final MethodAccess fastAccess;
    private final HashContainer<MethodIndex> methods;
//    private final Map<MethodSignature,Integer> methods2 ;
    /**
     * create Adaptor of an object
     * @param targetInterface
     * @param invokeTarget
     */
    public AdaptorInvocation(@Nonnull Class<?> targetInterface,@Nonnull Object invokeTarget){
        this.target = invokeTarget;
        var annotation = AnnotationUtils.getAdaptorInstance(targetInterface);
        Preconditions.checkState(annotation.isPresent(),"Illegal Adaptor class!This is not an AdaptorInterface!");
        String simpleName = targetInterface.getSimpleName();
        var targetClass = AnnotationUtils.getTargetInterface(invokeTarget.getClass(),simpleName);
        if(targetClass == null){
            throw new IllegalArgumentException("Illegal Adaptor class!No matching interface present in "+ invokeTarget.getClass() +"!");
        }
        Preconditions.checkState(AnnotationUtils.getAdaptorInstance(targetClass).isPresent(),"Illegal Adaptor class! Interface {0} in invoke target class is not marked as AdaptorInterface!",targetClass);
        Preconditions.checkNotNull(targetClass,"Invoke target does not contains Interface named {0}!",simpleName);
        this.fastAccess = me.matl114.matlib.utils.reflect.MethodAccess.getOrCreateAccess(targetClass);
        //save the mapping from targetInterface to invokeTarget fastAccess index;
         Map<Method,Integer> methodSnapshot = new HashMap<>();

        int maxCount = 0;
        Set<Method> methodMustRequired = AnnotationUtils.getAdaptedMethods(targetInterface);

        for(Method method : invokeTarget.getClass().getMethods()){
            //add base method invoke access
            if(ReflectUtils.isBaseMethod(method)){
                //require base method like toString and sth
                int count = method.getParameterCount();
                methodSnapshot.put(method,ReflectUtils.getBaseMethodIndex(method));
                maxCount = Math.max(maxCount,count);
            }
        }

        Iterator<Method> iter = methodMustRequired.iterator();
        while(iter.hasNext()){
            Method m = iter.next();
            try{
                var params = m.getParameterTypes();
                int index = this.fastAccess.getIndex(m.getName(),params);
                //find matched target
                methodSnapshot.put(m,index);
                maxCount = Math.max(maxCount,params.length);
            }catch (Throwable e){
                throw new IllegalArgumentException("Method " + m + " not found in invoke target class! using"+targetInterface +" as Adaptor and "+targetClass+" as invoke target");
            }
        }
        this.methods = new HashContainer<>(2*methodSnapshot.size(),p->p.signature.hashCode());


        for(var count :methodSnapshot.entrySet()){
            this.methods.add(new MethodIndex(MethodSignature.getSignature(count.getKey()),count.getValue()));
        }
//        Map<MethodSignature,Integer> bey = new HashMap<>(maxCount);
//        for (var count:methodSnapshot.entrySet()){
//            bey.put(MethodSignature.getSignature(count.getKey()),count.getValue());
//        }
//        this.methods2 = ImmutableMap.copyOf(bey);

    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        var methodParam = method.getParameterTypes();
        MethodIndex info = this.methods.findFirst(MethodSignature.getHash(method),index->index.signature.ofSameSignature(method));
//        Integer index = this.methods2.get(MethodSignature.getSignature(method));
        if(info!=null){
            return invoke0(info.index,args);
        }
//        if(index!=null){
//            return invoke0(index,args);
//        }

        throw new IllegalArgumentException("Method " + method + " not Accessible in this Adaptor!");
    }

    public Object invoke0(int index,Object[] args){
        if(index<0){
            return ReflectUtils.invokeBaseMethod(target,index,args);
        }else {
            return fastAccess.invoke(target,index,args);
        }
    }
    @AllArgsConstructor
    private static class MethodIndex{
        MethodSignature signature;
        int index;
    }
}
