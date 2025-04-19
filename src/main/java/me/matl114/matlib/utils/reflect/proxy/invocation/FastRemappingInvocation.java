package me.matl114.matlib.utils.reflect.proxy.invocation;

import it.unimi.dsi.fastutil.objects.Reference2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import me.matl114.matlib.algorithms.dataStructures.frames.HashContainer;
import me.matl114.matlib.utils.reflect.proxy.methodMap.MethodSignature;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import me.matl114.matlib.utils.reflect.proxy.methodMap.MethodIndex;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class FastRemappingInvocation implements InvocationCreator {
    public static final int DEFAULT_INVOCATION_INDEX = -336;
    //can we use something like , fastutil or real-hash to compare reference
    //shit, how can we know whether this is same method instance?
    private final HashContainer<MethodIndex> methods;
    private final Reference2ReferenceOpenHashMap<MethodIndex, MethodHandle> defaultInvocation = new Reference2ReferenceOpenHashMap<>();
    protected final Reference2BooleanOpenHashMap<MethodIndex> staticFlag = new Reference2BooleanOpenHashMap<>();
    public FastRemappingInvocation(Set<MethodIndex> rawData){
        this.methods = new HashContainer<>(2*rawData.size(),p->p.signature().hashCode());
        Set<MethodIndex> defaulta = new HashSet<>();
        for (MethodIndex rawDatum : rawData) {
            Method tar = rawDatum.target();
            if(tar != null){
                staticFlag.put(rawDatum, Modifier.isStatic(tar.getModifiers()));
            }
            if(rawDatum.hasDefault()){
                defaulta.add(rawDatum);
            }
        }
        this.methods.addAll(rawData);
        for (var raw : defaulta){
            try{
                MethodHandle handle = MethodHandles.privateLookupIn(raw.target().getDeclaringClass(),MethodHandles.lookup()).unreflectSpecial(raw.target(), raw.target().getDeclaringClass());
                defaultInvocation.put(raw, handle);
            }catch (Throwable e){
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Object invoke(Object proxy, Object target, Method method, Object[] args) throws Throwable {
        MethodIndex info = this.methods.findFirst(MethodSignature.getHash(method),index->index.signature().ofSameSignature(method));
        if(info!=null){
            if(info.hasDefault()){
                MethodHandle handle0 = Objects.requireNonNull(defaultInvocation.get(info));
                if(staticFlag.getBoolean(info)){
                    try{
                        return handle0.invokeWithArguments(args);
                    }catch (Throwable e){
                        throw new RuntimeException(e);
                    }
                }else {
                    Object[] argument = new Object[args.length+1];
                    System.arraycopy(args, 0, argument, 1,args.length);
                    argument[0] = proxy;
                    try{
                        return handle0.invokeWithArguments(argument);
                    }catch (Throwable e){
                        throw new RuntimeException(e);
                    }
                }
            }else {
                int index = info.index();
                if(index < 0){
                    return ReflectUtils.invokeBaseMethod(target,index,args);
                }
                return invoke0(proxy, target, info, args);
            }
        }
        throw new IllegalArgumentException("Method " + method + " not Accessible in this Adaptor!");
    }
    public abstract Object invoke0(Object proxy, Object target, MethodIndex methodIndex, Object[] args);

}
