package me.matl114.matlib.utils.reflect.proxy.invocation;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import me.matl114.matlib.algorithms.dataStructures.frames.HashContainer;
import me.matl114.matlib.utils.reflect.proxy.methodMap.MethodSignature;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import me.matl114.matlib.utils.reflect.proxy.methodMap.MethodIndex;

import java.lang.reflect.Method;
import java.util.Set;

public abstract class FastRemappingInvocation implements InvocationCreator {
    //can we use something like , fastutil or real-hash to compare reference
    //shit, how can we know whether this is same method instance?
    private final HashContainer<MethodIndex> methods;
    public FastRemappingInvocation(Set<MethodIndex> rawData){
        this.methods = new HashContainer<>(2*rawData.size(),p->p.signature().hashCode());
        this.methods.addAll(rawData);
    }
    @Override
    public Object invoke(Object proxy, Object target, Method method, Object[] args) throws Throwable {
        MethodIndex info = this.methods.findFirst(MethodSignature.getHash(method),index->index.signature().ofSameSignature(method));
        if(info!=null){
            int index = info.index();
            if(index < 0){
                return ReflectUtils.invokeBaseMethod(target,index,args);
            }
            return invoke0(proxy, target, info, args);
        }
        throw new IllegalArgumentException("Method " + method + " not Accessible in this Adaptor!");
    }
    public abstract Object invoke0(Object proxy, Object target, MethodIndex methodIndex, Object[] args);

}
